package net.sciros.bodymix.domain;

public class Track {
    private String title;
    public String getTitle () { return this.title; }
    public void setTitle (String value) { this.title = value; }
    
    private String artist;
    public String getArtist () { return this.artist; }
    public void setArtist (String value) { this.artist = value; }
    
    private String album;
    public String getAlbum () { return this.album; }
    public void setAlbum (String value) { this.album = value; }
    
    private Integer trackNumber;
    public Integer getTrackNumber () { return this.trackNumber; }
    public void setTrackNumber (Integer value) { this.trackNumber = value; }
    
    private Integer trackDurationInSeconds;
    public Integer getTrackDurationInSeconds () { return this.trackDurationInSeconds; }
    public void setTrackDurationInSeconds (Integer value) { this.trackDurationInSeconds = value; }
    
    private String filePath;
    public String getPath () { return this.filePath; }
    public void setPath (String value) { this.filePath = value; }
    
    private Boolean pauseOnFinish;
    public Boolean getPauseOnFinish () { return this.pauseOnFinish; }
    public void setPauseOnFinish (Boolean value) { this.pauseOnFinish = value; }
    
    private Integer silenceOnFinishInSeconds;
    public Integer getSilenceOnFinishInSeconds () { return this.silenceOnFinishInSeconds; }
    public void setSilenceOnFinishInSeconds (Integer value) { this.silenceOnFinishInSeconds = value; }
}
