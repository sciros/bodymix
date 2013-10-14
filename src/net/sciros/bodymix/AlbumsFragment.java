package net.sciros.bodymix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sciros.bodymix.adapter.AlbumsAdapter;
import net.sciros.bodymix.domain.Album;
import net.sciros.bodymix.domain.InternalConstants;
import net.sciros.bodymix.domain.Track;
import net.sciros.bodymix.io.CursorExtractionUtility;
import net.sciros.bodymix.listener.SwapFragmentsListener;
import net.sciros.bodymix.userstate.RunningSession;
import net.sciros.bodymix.userstate.UserStateConstants;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ExpandableListView;

public class AlbumsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ArrayList<Album> albums;
    public ArrayList<Album> getAlbums () { return this.albums; }
    public void setAlbums (ArrayList<Album> value) {
        this.albums = value;
    }
    private AlbumsAdapter adapter;
    
    private SwapFragmentsListener swapFragmentsListener;
    public SwapFragmentsListener getSwapFragmentsListener () { return this.swapFragmentsListener; }
    
    public static AlbumsFragment newInstance (SwapFragmentsListener swapFragmentsListener) {
        AlbumsFragment albumsFragment = new AlbumsFragment();
        albumsFragment.swapFragmentsListener = swapFragmentsListener;
        return albumsFragment;
    }
    
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.albums, container, false);
        return view;
    }
    
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        ArrayList<Album> initialAlbums = new ArrayList<Album>();
        //initialAlbums = albums; 
        adapter = new AlbumsAdapter(this.getActivity(), initialAlbums, this.getActivity().getApplicationContext());
        ExpandableListView listView = (ExpandableListView) getView().findViewById(R.id.albums);
        listView.setAdapter(adapter);
    }
    
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.getLoaderManager().initLoader(InternalConstants.ALBUMS_LIST_LOADER, null, this);
    }
    
    @Override
    public Loader<Cursor> onCreateLoader (int id, Bundle args) {
        Uri externalMusicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; //content://media/external/audio/media on simulator
        
        String[] projection = { 
            MediaStore.MediaColumns.TITLE,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Files.FileColumns.MIME_TYPE
        };
        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selectionParameterValues = { MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3") };
        String sortOrder = MediaStore.Audio.Media.TRACK;
        
        CursorLoader cursorLoader = new CursorLoader(this.getActivity(), externalMusicUri, projection,
                selectionMimeType, selectionParameterValues, sortOrder);
        return cursorLoader;
    }
    
    @Override
    public void onLoadFinished (Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                Map<String,List<Track>> albumMap = new HashMap<String,List<Track>>();
                
                do {
                    Track track = CursorExtractionUtility.extractTrackInfoFromCursor(cursor);
                    String albumName = track.getAlbum();
                    if (!albumMap.containsKey(albumName)) {
                        albumMap.put(albumName, new ArrayList<Track>());
                    }
                    albumMap.get(albumName).add(track);
                } while (cursor.moveToNext());
                
                List<Album> albumList = new ArrayList<Album>();
                for (String albumName : albumMap.keySet()) {
                    Album album = new Album();
                    album.setName(albumName);
                    album.setTracks(albumMap.get(albumName));
                    album.assignTypeAndNumberBasedOnName();
                    albumList.add(album);
                }
                
                if (albumList.size() > 1) {
                    Album[] albumsAsArray = albumList.toArray(new Album[]{});
                    Arrays.sort(albumsAsArray, albumList.get(0).new AlbumComparator());
                    albumList = Arrays.asList(albumsAsArray);
                }
                RunningSession.getInstance().getAttributes().put(UserStateConstants.ALBUMS, albumList);
                adapter.setAlbums(albumList);
            }
        }
        adapter.notifyDataSetChanged();
    }
    
    @Override
    public void onLoaderReset (Loader<Cursor> loader) {
        //release any references to cursor (set to null)... currently none tracked
    }
}
