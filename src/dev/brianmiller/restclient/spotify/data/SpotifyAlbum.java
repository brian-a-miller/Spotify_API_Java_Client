package dev.brianmiller.restclient.spotify.data;

import dev.brianmiller.restclient.spotify.SpotifyArtist;
import dev.brianmiller.restclient.spotify.SpotifyExternalUrls;
import dev.brianmiller.restclient.spotify.SpotifyImage;

import java.util.Objects;

public record SpotifyAlbum(
        String album_type,
        int total_tracks,
        String[] available_markets,
        SpotifyExternalUrls external_urls,
        String href,
        String id,
        SpotifyImage[] images,
        String name,
        String release_date,
        String release_date_precision,
        // restrictions
        String type,
        String uri,
        SpotifyArtist[] artists
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpotifyAlbum that)) return false;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                Objects.equals(album_type, that.album_type) &&
                Objects.equals(release_date, that.release_date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(album_type, id, name, release_date, type);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (artists != null) {
            for (var artist : artists) {
                String artistName = artist.name();
                if ((artistName != null) && !artistName.isBlank()) {
                    if (!sb.isEmpty()) {
                        sb.append(", ");
                    }
                    sb.append(artistName);
                }
            }
            sb.append(": ");
        }
        sb.append(name);

        if ((release_date != null) && (release_date.length() >= 4)) {
            sb.append(" (");
            sb.append(release_date, 0, 4); // sb.append(releaseDate.substring(0, 4));
            sb.append("),");
        }
        sb.append(" - ");
        sb.append(href());

        return sb.toString();
    }
}
