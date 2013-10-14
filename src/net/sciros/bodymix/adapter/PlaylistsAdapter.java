package net.sciros.bodymix.adapter;

import java.util.List;

import net.sciros.bodymix.R;
import net.sciros.bodymix.domain.AlbumType;
import net.sciros.bodymix.domain.Playlist;
import net.sciros.bodymix.listener.PlaylistsInteractionListener;
import net.sciros.bodymix.userstate.RunningSession;
import net.sciros.bodymix.userstate.UserStateConstants;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaylistsAdapter extends ArrayAdapter<Playlist> {
    private final Context context;
    private List<Playlist> playlists;
    private PlaylistsInteractionListener playlistsInteractionListener;
    
    public PlaylistsAdapter (Context context, List<Playlist> playlists, PlaylistsInteractionListener listener) {
        super(context, R.layout.playlists, playlists.toArray(new Playlist[]{}));
        this.context = context;
        this.playlists = playlists;
        this.playlistsInteractionListener = listener;
    }
    
    static class PlaylistInfoViewHolder {
        public ImageView iconView;
        public TextView titleView;
        public ImageButton selectButton;
    }
    
    @Override
    public int getCount() {
       return playlists.size();
    }
    
    @Override
    public Playlist getItem (int position) {
        return playlists.get(position);
    }
    
    @Override
    public View getView (final int position, View convertView, ViewGroup parent) {
        View trackView = convertView;
        if (trackView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            trackView = inflater.inflate(R.layout.playlist_info, parent, false);
            PlaylistInfoViewHolder viewHolder = new PlaylistInfoViewHolder();
            viewHolder.iconView = (ImageView) trackView.findViewById(R.id.type_icon);
            viewHolder.titleView = (TextView) trackView.findViewById(R.id.title);
            viewHolder.selectButton = (ImageButton) trackView.findViewById(R.id.select_button);
            trackView.setTag(viewHolder);
        }
        
        PlaylistInfoViewHolder viewHolder = (PlaylistInfoViewHolder) trackView.getTag();
        
        final Playlist playlist = playlists.get(position);
        viewHolder.titleView.setText(playlist.getName());
        viewHolder.iconView.setImageResource(playlist.getType().getIconResourceId());
        
        viewHolder.selectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                RunningSession.getInstance().getAttributes().put(UserStateConstants.CURRENT_PLAYLIST, playlist);
                playlistsInteractionListener.handlePlaylistSelection();
            }
        });
        
        setBackgroundBasedOnPlaylistType(trackView, playlist);
        
        return trackView;
    }
    
    //replace deprecated call with setBackground() when min API becomes 16 (not 11)
    private void setBackgroundBasedOnPlaylistType (final View trackView, Playlist playlist) {
        if (AlbumType.BODYCOMBAT.equals(playlist.getType())) {
            trackView.setBackgroundResource(R.drawable.gradient_green_1);
        } else if (AlbumType.BODYPUMP.equals(playlist.getType())) {
            trackView.setBackgroundResource(R.drawable.gradient_red_1);
        }
    }
}
