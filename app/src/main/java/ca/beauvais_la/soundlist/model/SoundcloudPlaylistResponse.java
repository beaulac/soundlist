package ca.beauvais_la.soundlist.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author alacasse (10/1/16)
 */
public class SoundcloudPlaylistResponse {

    @SerializedName("collection")
    private List<SoundcloudPlaylist> playlists;
    @SerializedName("next_href")
    private String nextUrl;

    public List<SoundcloudPlaylist> getPlaylists() {
        return playlists;
    }

    public String getNextPageUrl() {
        return nextUrl;
    }

}
