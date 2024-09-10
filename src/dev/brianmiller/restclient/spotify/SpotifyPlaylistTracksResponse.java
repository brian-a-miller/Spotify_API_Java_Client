package dev.brianmiller.restclient.spotify;

public record SpotifyPlaylistTracksResponse(
        String href,
        int limit,
        String next,
        int offset,
        String previous,
        int total,
        SpotifyPlaylistContentItem[] items) {
}
