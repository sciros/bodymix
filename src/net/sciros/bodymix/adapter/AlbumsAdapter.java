package net.sciros.bodymix.adapter;

import java.util.List;

import net.sciros.bodymix.R;
import net.sciros.bodymix.domain.Album;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

public class AlbumsAdapter extends BaseExpandableListAdapter {
    private final Context context;
    
    private List<Album> albums;
    public List<Album> getAlbums () { return this.albums; }
    public void setAlbums (List<Album> value) { this.albums = value; }
    
    public LayoutInflater inflater;
    public Activity activity;
    public TrackAdapter trackAdapter;

    public AlbumsAdapter (Activity activity, List<Album> albums, Context context) {
        this.activity = activity;
        this.albums = albums;
        this.context = context;
        inflater = activity.getLayoutInflater();
    }
    
    static class AlbumViewHolder {
        public ImageView iconView;
        public TextView trackNumberView;
        public TextView titleView;
        public TextView descriptionView;
    }
    
    @Override
    public Object getChild (int albumPosition, int trackPosition) {
        return albums.get(albumPosition).getTracks().get(trackPosition);
    }

    @Override
    public long getChildId (int albumPosition, int trackPosition) {
        return 0;
    }

    @Override
    public View getChildView (int albumPosition, final int trackPosition,
            boolean isLastTrack, View convertView, ViewGroup parent) {
        if (trackAdapter == null) {
            trackAdapter = new TrackAdapter(this.activity.getBaseContext(), albums.get(albumPosition));
        } else {
            trackAdapter.setAlbum(albums.get(albumPosition));
        }
        return trackAdapter.getView(trackPosition, convertView, parent);
    }

    @Override
    public int getChildrenCount (int albumPosition) {
        return albums.get(albumPosition).getTracks().size();
    }

    @Override
    public Object getGroup (int albumPosition) {
        return albums.get(albumPosition);
    }

    @Override
    public int getGroupCount () {
        return albums.size();
    }

    @Override
    public void onGroupCollapsed (int albumPosition) {
        super.onGroupCollapsed(albumPosition);
    }

    @Override
    public void onGroupExpanded (int albumPosition) {
        super.onGroupExpanded(albumPosition);
        if (trackAdapter == null) {
            trackAdapter = new TrackAdapter(this.activity.getBaseContext(), albums.get(albumPosition));
        }
    }

    @Override
    public long getGroupId (int albumPosition) {
        return 0;
    }

    @Override
    public View getGroupView (int albumPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View albumView = convertView;
        if (albumView == null) {
            albumView = inflater.inflate(R.layout.album, null);
        }
        
        Album album = (Album) getGroup(albumPosition);
        String albumName = album.getType().getType() + " " + album.getNumber().toString();
        ((CheckedTextView) albumView).setText(albumName);
        ((CheckedTextView) albumView).setChecked(isExpanded);
        ((CheckedTextView) albumView).setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(album.getType().getIconResourceId()), null);
        return albumView;
    }

    @Override
    public boolean hasStableIds () {
        return false;
    }

    @Override
    public boolean isChildSelectable (int albumPosition, int trackPosition) {
        return false;
    }
}
