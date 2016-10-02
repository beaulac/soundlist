package ca.beauvais_la.soundlist.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author alacasse (10/2/16)
 */
public class FBMusicLikes {

    @SerializedName("data")
    private List<FBMusicLike> musicLikes;

    @SerializedName("paging")
    private FBPaging paging;

    public List<FBMusicLike> getMusicLikes() {
        return musicLikes;
    }

    public FBPaging getPaging() {
        return paging;
    }
}

