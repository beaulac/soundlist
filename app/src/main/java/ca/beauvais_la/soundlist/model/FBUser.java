package ca.beauvais_la.soundlist.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author alacasse (10/1/16)
 */
public class FBUser implements Serializable {

    @SerializedName("id")
    public String facebookID;

    @SerializedName("name")
    public String name;

    @SerializedName("music")
    public FBMusicLikes musicLikes;

}
