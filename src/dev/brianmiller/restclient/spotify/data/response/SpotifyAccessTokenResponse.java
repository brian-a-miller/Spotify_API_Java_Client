package dev.brianmiller.restclient.spotify.data.response;

public record SpotifyAccessTokenResponse(String access_token, String token_type,
        String scope, int expires_in, String refresh_token) {
}
