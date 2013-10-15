package net.sciros.bodymix.domain;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Album implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private AlbumType type;
    public AlbumType getType () { return this.type; }
    public void setType (AlbumType value) { this.type = value; }
    
    private Integer number;
    public Integer getNumber () { return this.number; }
    public void setNumber (Integer value) { this.number = value; }
    
    private String name;
    public String getName () { return this.name; }
    public void setName (String value) { this.name = value; }
    
    private List<Track> tracks;
    public List<Track> getTracks () { return this.tracks; }
    public void setTracks (List<Track> value) { this.tracks = value; }
    
    public void assignTypeAndNumberBasedOnName () {
        if (type == null) {
            type = guessAlbumTypeFromAlbumName(name);
        }
        if (number == null) {
            number = extractNumberFromAlbumName(name);
        }
    }
    
    public AlbumType guessAlbumTypeFromAlbumName (String albumName) {
        AlbumType albumType = null;
        String capitalizedAlbumName = albumName.toUpperCase(); //TODO fix locale stuff
        for (AlbumType type : AlbumType.values()) {
            if (capitalizedAlbumName.contains(type.getShortHand())) {
                albumType = type;
                break;
            }
        }
        return albumType;
    }
    
    public Integer extractNumberFromAlbumName (String albumName) {
        Integer number = 0;
        String pattern = "\\.*(\\d+)\\.*";
        Matcher matcher = Pattern.compile(pattern).matcher(albumName);
        if (matcher.find()) {
            number = Integer.parseInt(matcher.group(1));
        }
        return number;
    }
    
    public class AlbumComparator implements Comparator<Album> {
        @Override
        public int compare (Album lhs, Album rhs) {
            if (lhs.getType().equals(rhs.getType())) {
                return (lhs.getNumber().compareTo(rhs.getNumber()));
            } else {
                return (lhs.getType().getType().compareTo(rhs.getType().getType()));
            }
        }
    }
}
