package net.sciros.bodymix.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sciros.bodymix.domain.Album;
import net.sciros.bodymix.domain.AlbumType;
import net.sciros.bodymix.domain.Playlist;
import net.sciros.bodymix.domain.Track;

public class TestDataConjurer {
    public static Playlist conjurePlaylist () {
        return conjurePlaylist("My BC Mix");
    }
    
    public static Playlist conjurePlaylist (String playlistName) {
        Playlist playlist = new Playlist();
        playlist.setName(playlistName);
        playlist.setFavorite(true);
        playlist.setDateModified(new Date());
        playlist.setTracks(conjureTracks());
        playlist.setType(AlbumType.BODYCOMBAT);
        return playlist;

    }
    
    public static Album conjureAlbum (String albumName) {
        Album album = new Album();
        album.setName(albumName);
        album.setTracks(conjureTracks(albumName));
        album.setType(AlbumType.BODYCOMBAT);
        return album;
    }
    
    public static List<Track> conjureTracks (String albumTitle) {
        List<Track> tracks = new ArrayList<Track>();
        for (int i = 1; i < 11; i++) {
            tracks.add(conjureTrack("Title " + String.valueOf(i), albumTitle, i));
        }
        return tracks;
    }
    
    public static List<Track> conjureTracks () {
        return conjureTracks("BodyCombat 90");
    }
    
    public static Track conjureTrack(String title, String albumTitle, Integer number) {
        Track track = new Track();
        track.setAlbum(albumTitle);
        track.setArtist("Scooter vs Hixxy");
        track.setPath("file:///user/music/bodycombat/BodyCombat 90/track1.mp3");
        track.setTitle(title);
        track.setTrackLengthInSeconds(340);
        track.setTrackNumber(number);
        
        return track;
    }
    
    public static Track conjureTrack () {
        return conjureTrack("Woot Hard", "BodyCombat 90", 1);
    }
}
