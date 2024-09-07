package dev.brianmiller.restclient.spotify;

public record SpotifyPlaylistContentItem(
        String added_at,
        boolean is_local,
        SpotifyTrack track) {
}
