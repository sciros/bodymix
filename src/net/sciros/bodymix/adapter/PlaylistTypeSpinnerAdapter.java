package net.sciros.bodymix.adapter;

import net.sciros.bodymix.R;
import net.sciros.bodymix.domain.AlbumType;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaylistTypeSpinnerAdapter extends ArrayAdapter<AlbumType> {
//new ArrayAdapter<AlbumType>(this, android.R.layout.simple_list_item_1, AlbumType.values())
    private Activity context;
    AlbumType[] albumTypes;

    public PlaylistTypeSpinnerAdapter(Activity context, AlbumType[] albumTypes) {
        super(context, R.layout.spinner_selected_item, albumTypes);
        this.context = context;
        this.albumTypes = albumTypes;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.spinner_row, parent, false);
        }
        
        AlbumType currentType = albumTypes[position];
        
        ImageView programIcon = (ImageView) row.findViewById(R.id.programIcon);
        programIcon.setBackgroundResource(currentType.getIconResourceId());
        
        TextView name = (TextView) row.findViewById(R.id.spinnerText);
        name.setText(currentType.getType());
        
        return row;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View albumTypeView = inflater.inflate(R.layout.spinner_selected_item, parent, false);
        TextView label = (TextView) albumTypeView.findViewById(R.id.spinner_selected_item);
        label.setText(albumTypes[position].getType());
        return label;
    }
}
