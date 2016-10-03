package ca.beauvais_la.soundlist.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author alacasse (10/1/16)
 */
public class FBUser {

    @SerializedName("name")
    public String name;

    @SerializedName("music")
    public FBMusicLikes musicLikes;

}
