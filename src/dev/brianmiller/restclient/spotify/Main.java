package dev.brianmiller.restclient.spotify;

import dev.brianmiller.restclient.spotify.data.SpotifyAlbum;
import dev.brianmiller.restclient.spotify.data.SpotifyTrack;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
public class Main {

    private final static String BRIANS_2024_ALBUMS_PLAYLIST =
            "https://api.spotify.com/v1/playlists/3rJ5F9yxAMkxUUlSbzNLv1/tracks";
            // "https://open.spotify.com/playlist/3rJ5F9yxAMkxUUlSbzNLv1?si=a68ef8737a10416b";

    private final static String BRIANS_AMERICANAFEST_2024_PREVIEW_PLAYLIST =
            // https://open.spotify.com/playlist/1PbpPitOs0VfLjLNTaCvKl?si=2d47670d60e34c46
            "https://api.spotify.com/v1/playlists/1PbpPitOs0VfLjLNTaCvKl/tracks";

    public static void main(String[] args) {

        SpotifyClient spotClient = new SpotifyClient();

        var spotifyAlbumsList = spotClient.getListAlbumsInPlaylist(
                BRIANS_AMERICANAFEST_2024_PREVIEW_PLAYLIST);
                //BRIANS_2024_ALBUMS_PLAYLIST);

        System.out.println("spotifyAlbumsList " + (spotifyAlbumsList == null ? "is null" : "is list containing " + spotifyAlbumsList.size() + " items"));

        if (spotifyAlbumsList != null) {

            // Collections.shuffle(spotifyAlbumsList);
            Collections.sort(spotifyAlbumsList, Comparator.comparing(SpotifyAlbum::toString));

            for (var album : spotifyAlbumsList) {

                System.out.println(album);
                System.out.println("Album ID: " + album.id());

                List<SpotifyTrack> tracks = spotClient.getAlbumTracks(album.id());
                if (tracks != null) {
                    for (var track : tracks) {
                        System.out.println("    " + track.track_number() + ". " + track.name());
                    }
                }
/*
            if (album.artists() != null) {
                System.out.println("Album Artist(s):");
                for (var artist : album.artists()) {
                    System.out.println("    " + artist.name()); // + " (" + artist.type() + ")");
                }
            }
            System.out.println("Album name: " + album.name());
            System.out.println("Album href: " + album.href());
            System.out.println("===================================");
 */

            }
        }
    }
}
