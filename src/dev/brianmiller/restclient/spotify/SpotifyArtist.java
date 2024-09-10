package dev.brianmiller.restclient.spotify;

public record SpotifyArtist(
        // external_urls
        String href,
        String id,
        String name,
        String type,
        String uri
) {
}
