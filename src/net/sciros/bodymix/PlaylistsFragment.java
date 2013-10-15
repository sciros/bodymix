package net.sciros.bodymix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.sciros.bodymix.adapter.PlaylistsAdapter;
import net.sciros.bodymix.domain.AlbumType;
import net.sciros.bodymix.domain.InternalConstants;
import net.sciros.bodymix.domain.Playlist;
import net.sciros.bodymix.listener.PlaylistsInteractionListener;
import net.sciros.bodymix.listener.SwapFragmentsListener;
import net.sciros.bodymix.userstate.RunningSession;
import net.sciros.bodymix.userstate.UserStateConstants;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.gson.Gson;

public class PlaylistsFragment extends ListFragment implements PlaylistsInteractionListener {
    private PlaylistsAdapter adapter;
    public PlaylistsAdapter getAdapter () { return this.adapter; }
    
    private ArrayList<Playlist> playlists;
    private Gson jsonParser;
    
    private SwapFragmentsListener swapFragmentsListener;
    public SwapFragmentsListener getSwapFragmentsListener () { return this.swapFragmentsListener; }
    
    public static PlaylistsFragment newInstance (SwapFragmentsListener swapFragmentsListener) {
        PlaylistsFragment playlistsFragment = new PlaylistsFragment();
        playlistsFragment.swapFragmentsListener = swapFragmentsListener;
        playlistsFragment.jsonParser = new Gson();
        return playlistsFragment;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        playlists = (ArrayList<Playlist>) RunningSession.getInstance().getAttributes().get(UserStateConstants.PLAYLISTS);
        if (playlists == null) {
            playlists = retrievePlaylists();
            RunningSession.getInstance().getAttributes().put(UserStateConstants.PLAYLISTS, playlists);
        }
        
        View view = inflater.inflate(R.layout.playlists, container, false);
//        View header = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.playlists_heading, null);
        ImageButton newPlaylistButton = (ImageButton) view.findViewById(R.id.new_button);
        newPlaylistButton.setOnClickListener(new OnClickListener () {
            @Override
            public void onClick (View arg0) {
                handleNewPlaylistButtonClick();
            }
        });
        
        return view;
    }
    
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addViewAdapter();
    }
    
    @SuppressWarnings("unchecked")
    public void addViewAdapter () {
        playlists = (ArrayList<Playlist>) RunningSession.getInstance().getAttributes().get(UserStateConstants.PLAYLISTS);
        if (playlists == null) {
            playlists = retrievePlaylists();
            RunningSession.getInstance().getAttributes().put(UserStateConstants.PLAYLISTS, playlists);
        }
        
        adapter = new PlaylistsAdapter(this.getActivity(), playlists, this);
        this.getListView().setAdapter(adapter);
    }
    
    //implementing OnClickListener for PlaylistsAdapter to invoke
    public void handlePlaylistSelection () {
        ((MainActivity) getActivity()).handlePlaylistSelection();
    }
    
    //implementing another OnClickListener for PlaylistsAdapter to invoke
    public void handleNewPlaylistButtonClick() {
        ((MainActivity) getActivity()).handleNewPlaylist();
    }
    
    private ArrayList<Playlist> retrievePlaylists () {
        ArrayList<Playlist> playlists = null;
        String pathToPlaylists = InternalConstants.PATH_TO_PLAYLISTS;
        File playlistDirectory = new File(Environment.getExternalStorageDirectory(), pathToPlaylists);
        if (!playlistDirectory.exists()) { //create playlist directory if it doesn't exist
            playlistDirectory.mkdirs(); //TODO if returns false, error. log it.
        } else {
            playlists = extractInfoFromPlaylistFiles(playlistDirectory);
        }
        return playlists;
    }
    
    private ArrayList<Playlist> extractInfoFromPlaylistFiles (File playlistDirectory) {
        List<Playlist> playlists = new ArrayList<Playlist>();
        for (File playlistFile : playlistDirectory.listFiles()) {
            if (playlistFile.isFile() && playlistFile.getAbsolutePath().contains(InternalConstants.M3U_EXTENSION)) {
                Playlist playlist = new Playlist();
                playlist.setFilePath(playlistFile.getAbsolutePath());
                playlist.setName(playlistFile.getName().substring(0, playlistFile.getName().indexOf(InternalConstants.M3U_EXTENSION)));
                
                //check if JSON metadata is present for this playlist; if not then create it
                File playlistJsonFile = new File(playlist.getFilePath().replace(InternalConstants.M3U_EXTENSION, InternalConstants.JSON_EXTENSION));
                if (!playlistJsonFile.exists()) {
                    createPlaylistJsonFile(playlist, playlistJsonFile);
                } else {
                    playlist = readPlaylistFromJsonFile(playlistJsonFile);
                }
                
                playlists.add(playlist);
            }
        }
        
        Playlist[] playlistsAsArray = playlists.toArray(new Playlist[]{});
        Arrays.sort(playlistsAsArray, new Comparator<Playlist> () {
            @Override
            public int compare (Playlist lhs, Playlist rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        return new ArrayList<Playlist>(Arrays.asList(playlistsAsArray));
    }
    
    private void createPlaylistJsonFile (Playlist playlist, File playlistJsonFile) {
        try {
            //need to fill out playlist's type! this is critical
            AlbumType type = extrapolateAlbumType(playlist);
            if (type != null) {
                PrintWriter writer = new PrintWriter(playlistJsonFile);
                playlist.setType(type);
                if (playlist.getLength() == null) { //if length not set, add a default
                    playlist.setLength(playlist.guessLengthByPlaylistType());
                }
                
                String playlistAsJson = jsonParser.toJson(playlist);
                writer.println(playlistAsJson);
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AlbumType extrapolateAlbumType (Playlist playlist) {
        AlbumType type = AlbumType.BODYPUMP;
        
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(playlist.getFilePath()));
            
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains(InternalConstants.MP3_EXTENSION)) {
                    Uri mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    String[] projection = { 
                            MediaStore.Audio.Media.DATA,
                            MediaStore.Audio.Media.ALBUM,
                    };
                    String selection = MediaStore.Audio.Media.DATA + "=?";
                    String sortOrder = null;
                    String[] selectionArgs = { line };
                    Cursor cursor = getActivity().getContentResolver().query(mediaStoreUri, projection, selection, selectionArgs, sortOrder);
                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        String albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                        type = playlist.guessAlbumTypeFromAlbumName(albumName);
                    }
                    break;
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return type;
    }
    
    private Playlist readPlaylistFromJsonFile (File file) {
        Playlist playlist = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            String fileJson = stringBuilder.toString();
            playlist = jsonParser.fromJson(fileJson, Playlist.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playlist;
    }
}
