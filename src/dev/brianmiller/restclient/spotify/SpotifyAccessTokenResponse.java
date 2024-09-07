package dev.brianmiller.restclient.spotify;

public record SpotifyAccessTokenResponse(String access_token, String token_type,
        int expires_in) {
}
