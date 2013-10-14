package net.sciros.bodymix.io;

import java.io.File;
import java.util.ArrayList;

import net.sciros.bodymix.domain.InternalConstants;
import net.sciros.bodymix.domain.Playlist;
import net.sciros.bodymix.userstate.RunningSession;
import net.sciros.bodymix.userstate.UserStateConstants;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.google.gson.Gson;

public class PlaylistPersistTask extends AsyncTask<Playlist, Integer, Boolean> {
    private Gson jsonParser = new Gson();
    private PlaylistRefresher playlistRefresher;
    private Boolean performRefresh;
    private Context context;
    
    public PlaylistPersistTask(Context context, PlaylistRefresher playlistRefresher, Boolean performRefresh) {
        this.context = context;
        this.playlistRefresher = playlistRefresher;
        this.performRefresh = performRefresh;
    }
    
    @SuppressWarnings("unchecked")
    private Boolean persistPlaylist (final Playlist playlist) {
        Boolean success = false;
        String pathToPlaylists = InternalConstants.PATH_TO_PLAYLISTS;
        File playlistDirectory = new File(Environment.getExternalStorageDirectory(), pathToPlaylists);
        if (playlistDirectory.exists()) {
            File playlistM3uFile = new File(playlistDirectory, playlist.getName() + InternalConstants.M3U_EXTENSION);
            playlist.createPlaylistM3uFile(playlistM3uFile);
            File playlistJsonFile = new File(playlistDirectory, playlist.getName() + InternalConstants.JSON_EXTENSION);
            playlist.createPlaylistJsonFile(jsonParser, playlistJsonFile);
            ArrayList<Playlist> playlists = (ArrayList<Playlist>) RunningSession.getInstance().getAttributes().get(UserStateConstants.PLAYLISTS);
            if (playlists.contains(playlist)) {
                playlists.set(playlists.indexOf(playlist),playlist);//replace
            } else {
                playlists.add(playlist);
            }
            success = true;
        } else {
            success = false;
        }
        return success;
    }

    @Override
    protected Boolean doInBackground (Playlist... playlists) {
        return persistPlaylist(playlists[0]);
    }
    
    @Override
    protected void onPostExecute (Boolean result) {
        if (result) {
            if (performRefresh) {
                //refresh all playlists in RunningSession and in playlists fragment view
                playlistRefresher.refreshPlaylists();
            }
            //add to media store
            Uri playlistUri = Uri.parse("file://"+Environment.getExternalStorageDirectory()+InternalConstants.PATH_TO_PLAYLISTS);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,playlistUri));
        }
    }
}
