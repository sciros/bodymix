package net.sciros.bodymix.adapter;

import java.util.concurrent.TimeUnit;

import net.sciros.bodymix.R;
import net.sciros.bodymix.domain.Album;
import net.sciros.bodymix.domain.InternalConstants;
import net.sciros.bodymix.domain.Track;
import android.annotation.SuppressLint;
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
        public TextView durationView;
    }
    
    @Override
    public int getCount() {
       return album.getTracks().size();
    }
    
    @Override
    public Track getItem (int position) {
        return album.getTracks().get(position);
    }
    
    @SuppressLint("DefaultLocale")
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
            viewHolder.durationView = (TextView) trackView.findViewById(R.id.track_duration);
            trackView.setTag(viewHolder);
        }
        
        TrackViewHolder viewHolder = (TrackViewHolder) trackView.getTag();
        
        Track track = album.getTracks().get(position);
        
        String trackName = track.getTitle();
        if (InternalConstants.MISSING_TRACK.equals(trackName)) {
            viewHolder.titleView.setText(R.string.select_a_track);
            viewHolder.descriptionView.setText("");
            setTrackViewBackgroundToGray(trackView);
            viewHolder.durationView.setText("");
        } else {
            viewHolder.titleView.setText(track.getTitle());
            String trackInfo = track.getAlbum() + "\n" + track.getArtist();
            viewHolder.descriptionView.setText(trackInfo);
            trackView.setBackgroundResource(album.getType().getBackgroundResourceId());
            Integer duration = track.getTrackDurationInSeconds();
            if (duration != null) {
                String trackDuration = String.format("%d:%02d",
                        TimeUnit.SECONDS.toMinutes(duration),
                        duration - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(duration)));
                viewHolder.durationView.setText(context.getString(R.string.duration) + " " + trackDuration);
            } else {
                viewHolder.durationView.setText("");
            }
        }
        
        viewHolder.trackNumberView.setText(track.getTrackNumber().toString());
        viewHolder.iconView.setImageResource(album.getType().getIconResourceId());
        //TODO get rid of this nonsense and make the number font legible
        float alpha = track.getTrackNumber().floatValue() / Float.parseFloat("10");
        viewHolder.iconView.setAlpha(alpha);
        
        return trackView;
    }
    
    private void setTrackViewBackgroundToGray (final View trackView) {
        trackView.setBackgroundResource(R.drawable.gradient_gray_1);
    }
}
