package dev.brianmiller.restclient.spotify;

import dev.brianmiller.restclient.spotify.data.SpotifyTrack;

public record SpotifyPlaylistContentItem(
        String added_at,
        boolean is_local,
        SpotifyTrack track) {
}
