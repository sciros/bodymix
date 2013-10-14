package net.sciros.bodymix;

import java.util.ArrayList;
import java.util.List;

import net.sciros.bodymix.adapter.TrackAdapter;
import net.sciros.bodymix.domain.Album;
import net.sciros.bodymix.domain.AlbumType;
import net.sciros.bodymix.domain.Playlist;
import net.sciros.bodymix.domain.Track;
import net.sciros.bodymix.listener.SwapFragmentsListener;
import net.sciros.bodymix.userstate.RunningSession;
import net.sciros.bodymix.userstate.UserStateConstants;
import android.content.Context;
import android.os.Bundle;
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

public class TracksFragment extends ListFragment {
    private List<Track> tracks;
    public List<Track> getTracks () { return this.tracks; }
    public void setTracks (List<Track> value) {
        this.tracks = value;
    }
    
    private Integer trackNumber;
    private AlbumType albumType;
    
    private SwapFragmentsListener swapFragmentsListener;
    public SwapFragmentsListener getSwapFragmentsListnener () { return this.swapFragmentsListener; }
    
    public static TracksFragment newInstance (SwapFragmentsListener swapFragmentsListener) {
        TracksFragment tracksFragment = new TracksFragment();
        tracksFragment.swapFragmentsListener = swapFragmentsListener;
        return tracksFragment;
    }
    
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.basic_list, container, false);
        return view;
    }
    
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findTracksAndBuildViewAdapterAndListener();
    }
    
    protected void findTracksAndBuildViewAdapterAndListener () {
        Integer currentTrackNumber = (Integer) RunningSession.getInstance().getAttributes().get(UserStateConstants.CURRENT_TRACK_NUMBER);
        Playlist playlist = (Playlist) RunningSession.getInstance().getAttributes().get(UserStateConstants.CURRENT_PLAYLIST);
        AlbumType currentAlbumType = playlist.getType();
        
        if (currentTrackNumber != null && currentAlbumType != null &&
            (trackNumber != currentTrackNumber || !currentAlbumType.equals(albumType))) {
            tracks = findTracksByNumberAndType(currentTrackNumber, currentAlbumType);
            trackNumber = currentTrackNumber;
            albumType = currentAlbumType;
        }
        RunningSession.getInstance().getAttributes().put(UserStateConstants.TRACKS, tracks);
        
        if (tracks != null) {
            Album album = new Album();
            album.setType(albumType);
            album.setTracks(tracks);
            
            final TrackAdapter adapter = new TrackAdapter(this.getActivity().getApplicationContext(), album);
            this.getListView().setAdapter(adapter);
            
            LayoutInflater inflater = (LayoutInflater) this.getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
            addOnClickListenerToListView(this.getListView(), inflater);
        }
    }
    
    private List<Track> findTracksByNumberAndType (Integer trackNumber, AlbumType albumType) {
        @SuppressWarnings("unchecked")
        List<Album> albums = (List<Album>) RunningSession.getInstance().getAttributes().get(UserStateConstants.ALBUMS);
        
        List<Track> tracks = new ArrayList<Track>();
        
        if (albums != null) {
            for (int i = 0; i < albums.size(); i++) {
                Album album = albums.get(i);
                if (albumType.equals(album.getType())) {
                    for (int j = 0; j < album.getTracks().size(); j++) {
                        Track track = album.getTracks().get(j);
                        if (trackNumber.equals(track.getTrackNumber())) {
                            tracks.add(track);
                        }
                    }
                }
            }
        }
        
        return tracks;
    }
    
    private void addOnClickListenerToListView (ListView listView, final LayoutInflater inflater) {
        final Context context = getActivity();
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, final int position, long id) {
                if (!RunningSession.getInstance().getAttributes().containsKey(UserStateConstants.PROMPT_UP)) {
                    final View dialogLayout = inflater.inflate(R.layout.dialog_select_cancel, getListView(), false);
                    ImageButton cancelButton = (ImageButton) dialogLayout.findViewById(R.id.cancel_button);
                    ImageButton selectButton = (ImageButton) dialogLayout.findViewById(R.id.select_button);
                    
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
                    
                    selectButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick (View v) {
                            Track track = getSelectedTrack(position);
                            Playlist playlist = (Playlist) RunningSession.getInstance().getAttributes().get(UserStateConstants.CURRENT_PLAYLIST);
                            //trackNumber is 1-indexed
                            playlist.getTracks().set(trackNumber - 1, track);
                            trackView.removeView(dialogLayout);
                            RunningSession.getInstance().getAttributes().put(UserStateConstants.CURRENT_PLAYLIST, playlist);
                            RunningSession.getInstance().getAttributes().remove(UserStateConstants.CURRENT_TRACK_NUMBER);
                            RunningSession.getInstance().getAttributes().remove(UserStateConstants.PROMPT_UP);
                            playlist.save(context, null, false);
                            ((MainActivity) getActivity()).handleTrackSelection();
                        }
                    });
                    
                    RunningSession.getInstance().getAttributes().put(UserStateConstants.PROMPT_UP, true);
                }
            }
        });
    }
    
    private Track getSelectedTrack (int position) {
        Track selectedTrack = null;
        if (tracks != null) {
          selectedTrack = tracks.get(position);
        }
        return selectedTrack;
    }
}
