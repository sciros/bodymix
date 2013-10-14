package net.sciros.bodymix.adapter;

import net.sciros.bodymix.R;
import net.sciros.bodymix.domain.Album;
import net.sciros.bodymix.domain.AlbumType;
import net.sciros.bodymix.domain.InternalConstants;
import net.sciros.bodymix.domain.Track;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TrackAdapter extends ArrayAdapter<Track> {
    private final Context context;
    
    private Album album;
    public Album getAlbum () { return this.album; }
    public void setAlbum (Album value) { this.album = value; }
    
    public TrackAdapter (Context context, Album album) {
        super(context, R.layout.track, album.getTracks().toArray(new Track[]{}));
        this.context = context;
        this.album = album;
    }
    
    static class TrackViewHolder {
        public ImageView iconView;
        public TextView trackNumberView;
        public TextView titleView;
        public TextView descriptionView;
    }
    
    @Override
    public int getCount() {
       return album.getTracks().size();
    }
    
    @Override
    public Track getItem (int position) {
        return album.getTracks().get(position);
    }
    
    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        View trackView = convertView;
        if (trackView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            trackView = inflater.inflate(R.layout.track, parent, false);
            TrackViewHolder viewHolder = new TrackViewHolder();
            viewHolder.iconView = (ImageView) trackView.findViewById(R.id.track_icon);
            viewHolder.trackNumberView = (TextView) trackView.findViewById(R.id.track_number);
            viewHolder.titleView = (TextView) trackView.findViewById(R.id.track_title);
            viewHolder.descriptionView = (TextView) trackView.findViewById(R.id.track_info);
            trackView.setTag(viewHolder);
        }
        
        TrackViewHolder viewHolder = (TrackViewHolder) trackView.getTag();
        
        Track track = album.getTracks().get(position);
        
        String trackName = track.getTitle();
        if (InternalConstants.MISSING_TRACK.equals(trackName)) {
            viewHolder.titleView.setText(R.string.select_a_track);
            viewHolder.descriptionView.setText("");
            setTrackViewBackgroundToGray(trackView);
        } else {
            viewHolder.titleView.setText(track.getTitle());
            String trackInfo = track.getAlbum() + "\n" + track.getArtist();
            viewHolder.descriptionView.setText(trackInfo);
            setTrackViewBackgroundBasedOnPlaylistType(trackView);
        }
        
        viewHolder.trackNumberView.setText(track.getTrackNumber().toString());
        //set the image based on the track number
        float alpha = track.getTrackNumber().floatValue() / Float.parseFloat("10");
        viewHolder.iconView.setImageResource(album.getType().getIconResourceId());
        viewHolder.iconView.setAlpha(alpha);
        
        return trackView;
    }
    
    private void setTrackViewBackgroundBasedOnPlaylistType (final View trackView) {
        if (AlbumType.BODYCOMBAT.equals(album.getType())) {
            trackView.setBackgroundResource(R.drawable.gradient_green_1);
        } else if (AlbumType.BODYPUMP.equals(album.getType())) {
            trackView.setBackgroundResource(R.drawable.gradient_red_1);
        }
        
//        ViewTreeObserver vto = trackView.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                LayerDrawable ld = (LayerDrawable) trackView.getBackground();
//                ld.setLayerInset(1, 0, trackView.getHeight() / 2, 0, 0);
//            }
//        });
    }
    
    private void setTrackViewBackgroundToGray (final View trackView) {
        trackView.setBackgroundResource(R.drawable.gradient_gray_1);
    }
}
