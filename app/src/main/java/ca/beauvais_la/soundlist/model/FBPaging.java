package ca.beauvais_la.soundlist.model;

import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;

/**
 * @author alacasse (10/2/16)
 */
public class FBPaging {

    @SerializedName("next")
    private String nextUrl;


    public String getNextUrl() {
        return nextUrl;
    }

    public boolean hasNextPage() {
        return !TextUtils.isEmpty(nextUrl);
    }

}
