package ca.beauvais_la.soundlist.model;

import ca.beauvais_la.soundlist.JsonUtil;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * @author alacasse (10/1/16)
 */
public class SoundcloudPlaylistResponse {

    private static final TypeToken<SoundcloudPlaylistResponse> TOKEN = new TypeToken<SoundcloudPlaylistResponse>(){};

    @SerializedName("collection")
    private List<SoundcloudPlaylist> playlists;
    @SerializedName("next_href")
    private String nextHref;

    public static SoundcloudPlaylistResponse extractPlaylistsFromJSON(String responseJSON) {
        return JsonUtil.deserializeAs(responseJSON, TOKEN);
    }

    public List<SoundcloudPlaylist> getPlaylists() {
        return playlists;
    }

    public String getNextPageHref() {
        return nextHref;
    }

}
