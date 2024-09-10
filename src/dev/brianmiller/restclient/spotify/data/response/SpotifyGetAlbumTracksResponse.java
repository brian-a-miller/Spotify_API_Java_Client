package dev.brianmiller.restclient.spotify.data.response;

import dev.brianmiller.restclient.spotify.data.SpotifyTrack;

public record SpotifyGetAlbumTracksResponse(
        String href,
        SpotifyTrack[] items,
        int limit,
        String next,
        int offset,
        String previous,
        int total
) {
}
