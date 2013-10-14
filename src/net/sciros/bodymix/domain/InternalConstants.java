package net.sciros.bodymix.domain;

public interface InternalConstants {
    public static final Integer PLAYLIST_TAB_POSITION = 0;
    public static final Integer TRACKS_TAB_POSITION = 1;
    
    public static final int ALBUMS_LIST_LOADER = 10;
    
    public static final String DB_NAME = "bodymix_db";
    public static final String M3U_HEADER = "#EXTM3U";
    public static final String M3U_INFO_PREFIX = "#EXTINF:";
    public static final String PATH_TO_PLAYLISTS = "/Music/BodyMixPlaylists";
    public static final String MISSING_TRACK = "_MISSING_TRACK_"; //placeholder value to fill out playlists
    
    public static final String M3U_EXTENSION = ".m3u";
    public static final String MP3_EXTENSION = ".mp3";
    public static final String JSON_EXTENSION = ".json";
}
