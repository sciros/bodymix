package net.sciros.bodymix.io;

import java.io.File;

import net.sciros.bodymix.domain.InternalConstants;
import net.sciros.bodymix.domain.Playlist;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class PlaylistDeleteTask extends AsyncTask<Playlist, Integer, Boolean> {
    private PlaylistRefresher playlistRefresher;
    private Boolean performRefresh;
    private Context context;
    private static final String TAG = PlaylistDeleteTask.class.getSimpleName();
    
    public PlaylistDeleteTask(Context context, PlaylistRefresher playlistRefresher, Boolean performRefresh) {
        this.context = context;
        this.playlistRefresher = playlistRefresher;
        this.performRefresh = performRefresh;
    }
    
    private Boolean deletePlaylist (final Playlist playlist) {
        Boolean success = false;
        String pathToPlaylists = InternalConstants.PATH_TO_PLAYLISTS;
        File playlistDirectory = new File(Environment.getExternalStorageDirectory(), pathToPlaylists);
        if (playlistDirectory.exists()) {
            if (playlist.getFilePath() != null) {
                (new File(playlist.getFilePath())).delete();
                String jsonFilePath = playlist.getFilePath().replace(InternalConstants.M3U_EXTENSION, InternalConstants.JSON_EXTENSION);
                (new File(jsonFilePath)).delete();
            }
            success = true;
        } else {
            success = false;
        }
        return success;
    }

    @Override
    protected Boolean doInBackground (Playlist... playlists) {
        return deletePlaylist(playlists[0]);
    }
    
    @Override
    protected void onPostExecute (Boolean result) {
        if (result) {
            if (performRefresh) {
                //refresh all playlists in RunningSession and in playlists fragment view
                playlistRefresher.refreshPlaylists();
            }
            //remove from media store
            Uri playlist_uri= MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
            String[] wildcard = {"*"};
            Cursor cursor= context.getContentResolver().query(playlist_uri, wildcard, null,null,null);
            cursor.moveToFirst();
            for(int r= 0; r < cursor.getCount(); r++, cursor.moveToNext()) {
                int i = cursor.getInt(0);
                String filepath = cursor.getString(1); //_data is the filepath
                File file = new File(filepath);
                if(file.exists()) {
                    // keep these, and let me know
                    Log.d(TAG, "Keeping : " + cursor.getString(2) + " : id(" + i + ")");
                } else {
                    // delete any play-lists with a data length of '0'
                    Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, i);
                    context.getContentResolver().delete(uri, null, null);
                    Log.d(TAG, "Deleted : " + cursor.getString(2) + " : id(" + i + ")");
                }
            }
            cursor.close();
        }
    }
}
