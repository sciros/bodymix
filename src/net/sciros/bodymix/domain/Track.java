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
    
    private Integer trackLengthInSeconds;
    public Integer getTrackLengthInSeconds () { return this.trackLengthInSeconds; }
    public void setTrackLengthInSeconds (Integer value) { this.trackLengthInSeconds = value; }
    
    private String filePath;
    public String getPath () { return this.filePath; }
    public void setPath (String value) { this.filePath = value; }
}
