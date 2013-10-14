package net.sciros.bodymix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import net.sciros.bodymix.adapter.TrackAdapter;
import net.sciros.bodymix.domain.InternalConstants;
import net.sciros.bodymix.domain.Playlist;
import net.sciros.bodymix.domain.Track;
import net.sciros.bodymix.io.CursorExtractionUtility;
import net.sciros.bodymix.io.PlaylistRefresher;
import net.sciros.bodymix.listener.SwapFragmentsListener;
import net.sciros.bodymix.userstate.RunningSession;
import net.sciros.bodymix.userstate.UserStateConstants;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class PlaylistFragment extends ListFragment {
    private SwapFragmentsListener swapFragmentsListener;
    public SwapFragmentsListener getSwapFragmentsListnener () { return this.swapFragmentsListener; }
    
    public static PlaylistFragment newInstance (SwapFragmentsListener swapFragmentsListener) {
        PlaylistFragment playlistFragment = new PlaylistFragment();
        playlistFragment.swapFragmentsListener = swapFragmentsListener;
        return playlistFragment;
    }
    
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playlist, container, false);
        
        //TODO perhaps extract from savedInstanceState or something
        Playlist playlist = (Playlist) RunningSession.getInstance().getAttributes().get(UserStateConstants.CURRENT_PLAYLIST);
        if (playlist != null && (playlist.getTracks() == null || playlist.getTracks().size() == 0)) {
            //read file line by line and put together stuff by looking for songs from mediastore
            File playlistFile = new File(playlist.getFilePath());
            extractPlaylistFileIntoPlaylist(playlist, playlistFile);
            RunningSession.getInstance().getAttributes().put(UserStateConstants.CURRENT_PLAYLIST, playlist);
        }
        
        //ImageView icon = (ImageView) view.findViewById(R.id.)
        
        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(playlist.getName());
        
        return view;
    }
    
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addViewAdapterAndListener();
    }
    
    public void addViewAdapterAndListener () {
        Playlist playlist = (Playlist) RunningSession.getInstance().getAttributes().get(UserStateConstants.CURRENT_PLAYLIST);
        final TrackAdapter adapter = new TrackAdapter(this.getActivity().getApplicationContext(), playlist);
        this.getListView().setAdapter(adapter);
        
        LayoutInflater inflater = (LayoutInflater) this.getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        addOnClickListenerToListView(this.getListView(), inflater);
        addOnClickListenerToDeleteButton(inflater, playlist);
        addOnClickListenerToPlayButton(inflater, playlist);
    }
    
    private void addOnClickListenerToDeleteButton (LayoutInflater inflater, final Playlist playlist) {
        View heading = this.getView();
        ImageButton deleteButton = (ImageButton) heading.findViewById(R.id.delete_button);
        final Context context = this.getActivity();
        final PlaylistRefresher playlistRefresher = (PlaylistRefresher) this.getActivity();
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle(R.string.confirm_delete);
                alertDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick (DialogInterface dialog, int which) {
                        playlist.delete(context, playlistRefresher, true);
                        RunningSession.getInstance().getAttributes().remove(UserStateConstants.CURRENT_PLAYLIST);
                        swapFragmentsListener.onSwapFragments();
                    }
                });
                alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                final AlertDialog confirmationDialog = alertDialogBuilder.create();
                confirmationDialog.show();
            }
        });
    }
    
    private void addOnClickListenerToPlayButton (LayoutInflater inflater, final Playlist playlist) {
        View heading = inflater.inflate(R.layout.playlist_heading, null);
        ImageButton playButton = (ImageButton) heading.findViewById(R.id.play_button);
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                String playlistPath = playlist.getFilePath();
                if (playlistPath != null) {
                    //see about playing the music... possible or not?
                }
            }
        });
    }
    
    private void addOnClickListenerToListView (ListView listView, final LayoutInflater inflater) {
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, final int position, long id) {
                if (!RunningSession.getInstance().getAttributes().containsKey(UserStateConstants.PROMPT_UP)) {
                    final View dialogLayout = inflater.inflate(R.layout.dialog_edit_cancel, getListView(), false);
                    ImageButton cancelButton = (ImageButton) dialogLayout.findViewById(R.id.cancel_button);
                    ImageButton editButton = (ImageButton) dialogLayout.findViewById(R.id.edit_button);
                    
                    final RelativeLayout trackView = (RelativeLayout) view;
                    LayoutParams layoutParams = new LayoutParams(trackView.getWidth(), trackView.getHeight());
                    layoutParams.alignWithParent = true;
                    trackView.addView(dialogLayout, layoutParams);
                    
                    cancelButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick (View v) {
                            trackView.removeView(dialogLayout);
                            RunningSession.getInstance().getAttributes().remove(UserStateConstants.PROMPT_UP);
                        }
                    });
                    
                    editButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick (View v) {
                            trackView.removeView(dialogLayout);
                            //position + 1 is the actual track number which is 1-indexed
                            RunningSession.getInstance().getAttributes().put(UserStateConstants.CURRENT_TRACK_NUMBER, position + 1);
                            RunningSession.getInstance().getAttributes().remove(UserStateConstants.PROMPT_UP);
                            ((MainActivity) getActivity()).handleTrackEditing();
                        }
                    });
                    
                    RunningSession.getInstance().getAttributes().put(UserStateConstants.PROMPT_UP, true);
                }
            }
        });
    }
    
    private void extractPlaylistFileIntoPlaylist (Playlist playlist, File file) {
        playlist.setTracks(new ArrayList<Track>());
        Uri mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
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
        String selection = MediaStore.Audio.Media.DATA + "=?";
        String sortOrder = MediaStore.Audio.Media.TRACK;
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains(InternalConstants.MP3_EXTENSION)) { //the rest are either blank or meta info
                    String[] selectionArgs = { line };
                    Cursor cursor = getActivity().getContentResolver().query(mediaStoreUri, projection, selection, selectionArgs, sortOrder);
                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        Track track = CursorExtractionUtility.extractTrackInfoFromCursor(cursor);
                        playlist.getTracks().add(track);
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //sorting tracks should not be necessary -- but keep an eye out in case that's wrong
        
        //fill in blanks if there are any (if playlist is not full intended length)
        playlist.fillInPlaylistBlanksWithBlankTracks();
    }
}
