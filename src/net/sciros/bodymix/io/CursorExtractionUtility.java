package net.sciros.bodymix.io;

import net.sciros.bodymix.domain.Track;
import android.database.Cursor;
import android.provider.MediaStore;

public class CursorExtractionUtility {
    public static Track extractTrackInfoFromCursor (Cursor cursor) {
        //String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
        String fullPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        String albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
        String artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
        String trackTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
        int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
        duration /= 1000; //initially in milliseconds
        int trackNumber = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK));
        //TODO "alt" tracks in BodyPump and what can be done... might need users to set track num
        
        Track track = new Track();
        track.setAlbum(albumName);
        track.setArtist(artistName);
        track.setPath(fullPath);
        track.setTitle(trackTitle);
        track.setTrackLengthInSeconds(duration);
        track.setTrackNumber(trackNumber);
        
        return track;
    }
}
