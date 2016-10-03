package ca.beauvais_la.soundlist.model;

import com.google.gson.annotations.SerializedName;

import java.util.Iterator;
import java.util.List;

/**
 * @author alacasse (10/2/16)
 */
public class FBMusicLikes implements Iterable<FBMusicLike> {

    public static final String DATA_KEY = FBMusicLikes.class.getSimpleName();

    @SerializedName("data")
    public List<FBMusicLike> musicLikes;

    @SerializedName("paging")
    public FBPaging paging;


    @Override
    public Iterator<FBMusicLike> iterator() {
        return musicLikes.iterator();
    }
}

