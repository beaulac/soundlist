package ca.beauvais_la.soundlist.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author alacasse (10/1/16)
 */
public class SoundcloudPlaylist {

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    public String title;

    @SerializedName("artwork_url")
    public String artworkUrl;


}
