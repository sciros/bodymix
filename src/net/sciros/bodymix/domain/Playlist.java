package net.sciros.bodymix.domain;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import net.sciros.bodymix.io.PlaylistDeleteTask;
import net.sciros.bodymix.io.PlaylistPersistTask;
import net.sciros.bodymix.io.PlaylistRefresher;
import net.sciros.bodymix.userstate.RunningSession;
import net.sciros.bodymix.userstate.UserStateConstants;
import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;

public class Playlist extends Album {
    private static final long serialVersionUID = 1L;
    
    private String filePath;
    public String getFilePath () { return this.filePath; }
    public void setFilePath (String value) { this.filePath = value; }
    
    private Date dateModified;
    public Date getDateModified () { return this.dateModified; }
    public void setDateModified (Date value) { this.dateModified = value; }
    
    private Boolean favorite;
    public Boolean getFavorite () { return this.favorite; }
    public void setFavorite (Boolean value) { this.favorite = value; }
    
    private Integer length; //10 for most album types, but less for others
    public Integer getLength () { return this.length; }
    public void setLength (Integer value) { this.length = value; }
    
    private Integer runningTimeInSeconds;
    public Integer getRunningTimeInSeconds () { return this.runningTimeInSeconds; }
    public void setRunningTimeInSeconds (Integer value) { this.runningTimeInSeconds = value; }
    
    public void fillInFilePathUsingName () {
        if (this.getName() != null && !this.getName().equals("")) {
            filePath = Environment.getExternalStorageDirectory() + 
                    InternalConstants.PATH_TO_PLAYLISTS + 
                    "/" + this.getName() + InternalConstants.M3U_EXTENSION;
        }
    }
    
    public void fillInRunningTimeInSecondsFromTracks () {
        runningTimeInSeconds = 0;
        for (Track track : this.getTracks()) {
            if (track != null && track.getTrackDurationInSeconds() != null) {
                runningTimeInSeconds += track.getTrackDurationInSeconds();
            }
        }
    }
    
    public void save (Context context, PlaylistRefresher playlistRefresher, Boolean performRefresh) {
        PlaylistPersistTask persistTask = new PlaylistPersistTask(context, playlistRefresher, performRefresh);
        persistTask.execute(this);
    }
    
    public void delete (Context context, PlaylistRefresher playlistRefresher, Boolean performRefresh) {
        PlaylistDeleteTask deleteTask = new PlaylistDeleteTask(context, playlistRefresher, performRefresh);
        deleteTask.execute(this);
        
        @SuppressWarnings("unchecked")
        ArrayList<Playlist> currentPlaylists = (ArrayList<Playlist>) RunningSession.getInstance().getAttributes().get(UserStateConstants.PLAYLISTS);
        currentPlaylists.remove(this);
    }
    
    public void createPlaylistM3uFile (File playlistM3uFile) {
        try {
            PrintWriter writer = new PrintWriter(playlistM3uFile);
            writer.println(InternalConstants.M3U_HEADER);
            for (Track track : this.getTracks()) {
                String trackInfo = track.getTrackDurationInSeconds() == null ? track.getTitle() : 
                    track.getTrackDurationInSeconds() + "," + track.getTitle();
                writer.println(InternalConstants.M3U_INFO_PREFIX + trackInfo);
                writer.println(track.getPath());
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void createPlaylistJsonFile (Gson jsonParser, File playlistJsonFile) {
        if (filePath == null) { fillInFilePathUsingName(); }
        try {
            PrintWriter writer = new PrintWriter(playlistJsonFile);
            String playlistAsJson = jsonParser.toJson(this);
            writer.println(playlistAsJson);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void fillInPlaylistBlanksWithBlankTracks () {
        if (this.getTracks() == null) {
            this.setTracks(new ArrayList<Track>());
        }
        for (int i = 0; i < this.getLength(); i++) {
            if (i == this.getTracks().size()) {
                this.getTracks().add(conjureBlankTrack(i+1));
            } else {
                Track track = this.getTracks().get(i);
                if (track.getTrackNumber() != i + 1) {
                    this.getTracks().add(i, conjureBlankTrack(i+1));
                }
            }
        }
    }
    
    private Track conjureBlankTrack (int trackNumber) {
        Track blankTrack = new Track();
        blankTrack.setTitle(InternalConstants.MISSING_TRACK);
        blankTrack.setTrackNumber(trackNumber);
        return blankTrack;
    }
    
    public boolean validateFields (String name, AlbumType type, Integer length) {
        boolean valid = true;
        if (name == null || name.length() == 0) { valid = false; }
        return valid;
    }
    
    public Integer guessLengthByPlaylistType () {
        Integer length = 10;
        switch(this.getType()) {
            case BODYJAM: length = 16; //most are shorter but I've seen a 16
            case CXWORX: length = 6;
            case SHBAM: length = 13; //most are 12 but one so far is 13
            case GRIT_CARDIO: length = 7; //usually 6
            case GRIT_PLYO: length = 7; //usually 6
            case GRIT_STRENGTH: length = 7; //usually 6
            default: break;
        }
        return length;
    }
}
