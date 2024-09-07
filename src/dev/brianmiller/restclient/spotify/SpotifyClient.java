package dev.brianmiller.restclient.spotify;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import dev.brianmiller.properties.Properties;

import dev.brianmiller.restclient.RestClient;
import dev.brianmiller.restclient.RestResponse;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 *
 */
public class SpotifyClient {

    private final static String REQUEST_ACCESS_TOKEN_ENDPOINT_URL =
            "https://accounts.spotify.com/api/token";

    private final static String PLAYLISTS_URL_PREFIX = "https://api.spotify.com/v1/playlists/";
    private final static String PLAYLIST_TRACKS_URL_SUFFIX = "/tracks";

    private final static String HEADER_ACCEPT_KEY   = "Accept";
    private final static String HEADER_ACCEPT_VALUE = "application/json";

    private final static String HEADER_CONTENT_TYPE_KEY = "Content-Type";
    private final static String HEADER_CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";

    private final static String HEADER_AUTHORIZATION_KEY = "Authorization";
    private final static String HEADER_AUTHORIZATION_VALUE_PREFIX = "Bearer ";

    private final static String DATA_GRANT_TYPE_KEY = "grant_type";
    private final static String DATA_GRANT_TYPE_VALUE_CLIENT_CREDENTIALS = "client_credentials";

    private final static String DATA_CLIENT_ID_KEY = "client_id";
    private final static String DATA_CLIENT_SECRET_KEY = "client_secret";

    private final static String PROPERTY_KEY_CLIENT_ID = "spotify_client_id";
    private final static String PROPERTY_KEY_CLIENT_SECRET = "spotify_client_secret";

    private static String clientID;
    private static String clientSecret;
    private static String accessToken;
    private static LocalDateTime accessTokenExpiration;

    static {
        clientID = Properties.getValue(PROPERTY_KEY_CLIENT_ID);
        clientSecret = Properties.getValue(PROPERTY_KEY_CLIENT_SECRET);
        accessToken = null;
        accessTokenExpiration = null;
    }

    private RestClient restClient;

    public SpotifyClient() {
        this.restClient = new RestClient();
    }

    private String getAccessToken() {
        // TODO: reverse if/else
        if (accessToken == null || accessTokenExpiration == null ||
            LocalDateTime.now().isAfter(accessTokenExpiration)) {

            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(HEADER_CONTENT_TYPE_KEY, HEADER_CONTENT_TYPE_VALUE);

            Map<String, String> dataMap = new HashMap<>();
            dataMap.put(DATA_GRANT_TYPE_KEY, DATA_GRANT_TYPE_VALUE_CLIENT_CREDENTIALS);
            dataMap.put(DATA_CLIENT_ID_KEY, clientID);
            dataMap.put(DATA_CLIENT_SECRET_KEY, clientSecret);

            RestResponse restResponseRequestAccessToken =
                    restClient.post(REQUEST_ACCESS_TOKEN_ENDPOINT_URL,
                            headerMap, dataMap);

            if ((restResponseRequestAccessToken != null) &&
                    (restResponseRequestAccessToken.getCode() == HTTP_OK) &&
                    (restResponseRequestAccessToken.getBody() != null)) {

                Gson gson = new Gson();
                try {
                    SpotifyAccessTokenResponse spotifyAccessTokenResponse =
                            gson.fromJson(restResponseRequestAccessToken.getBody(),
                                    SpotifyAccessTokenResponse.class);
                    if ((spotifyAccessTokenResponse != null) &&
                            (spotifyAccessTokenResponse.access_token() != null)) {
                        accessToken = spotifyAccessTokenResponse.access_token();
                        accessTokenExpiration = LocalDateTime.now().plusSeconds(
                                spotifyAccessTokenResponse.expires_in()
                        );
                        return accessToken;
                    }
                } catch (JsonSyntaxException ex) {
                    System.err.println("Failed to parse access token response from Spotify");
                    ex.printStackTrace(System.err);
                }
            }

        } else {
            return accessToken;
        }
        return null;
    }

    /**
     *
     * @param playlistURL fully-qualified URL
     * @return
     */
    public List<SpotifyAlbum> getListAlbumsInPlaylist(String playlistURL) {
        List<SpotifyAlbum> albumsList = new ArrayList<>();

        String accessToken = getAccessToken();
        System.out.println("accessToken: " + accessToken);

        String url = playlistURL.trim();
        if (!url.endsWith("tracks") && !url.endsWith("tracks/")) {
            if (url.endsWith("/")) {
                url = url + "tracks";
            } else {
                url = url + "/tracks";
            }
        }

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(HEADER_ACCEPT_KEY, HEADER_ACCEPT_VALUE);
        headerMap.put(HEADER_AUTHORIZATION_KEY, HEADER_AUTHORIZATION_VALUE_PREFIX + accessToken);

        Gson gson = new Gson();
        RestResponse restResponse = restClient.get(url, headerMap);
        if (restResponse == null) {
            System.err.println("null response to " + url);
        } else {
            // System.out.println(url);
            // System.out.println("Response Code: " + restResponse.getCode());
            // System.out.println("Response Body: " + restResponse.getBody());
        }
        boolean readNextPage = true;
        while (readNextPage && (restResponse != null) && (restResponse.getCode() == HTTP_OK) &&
                (restResponse.getBody() != null)) {

            try {
                SpotifyPlaylistTracksResponse spotResponse =
                        gson.fromJson(restResponse.getBody(), SpotifyPlaylistTracksResponse.class);

                if (spotResponse == null) {
                    readNextPage = false;
                } else {

                    var items = spotResponse.items();
                    if (items != null) {
                        for (var item : items) {
                            var track = item.track();
                            if (track != null) {
                                var album = track.album();
                                if (album != null) {
                                    if (!albumsList.contains(album)) {
                                        albumsList.add(album);
                                    }
                                }
                            }
                        }
                    }

                    String nextURL = spotResponse.next();
                    if ((nextURL == null) || nextURL.isBlank()) {
                        readNextPage = false;
                    } else {
                        restResponse = restClient.get(nextURL, headerMap);
                        if (restResponse == null) {
                            //System.err.println("null response to " + url);
                        } else {
                            //System.out.println(url);
                            //System.out.println("Response Code: " + restResponse.getCode());
                        }

                    }
                }

            } catch (Exception ex) {
                //
                readNextPage = false;
            }
        }


        return albumsList;
    }

}
