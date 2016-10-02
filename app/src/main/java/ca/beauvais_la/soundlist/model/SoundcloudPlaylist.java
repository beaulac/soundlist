package ca.beauvais_la.soundlist.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import ca.beauvais_la.soundlist.Soundlist;
import com.google.gson.annotations.SerializedName;

import java.io.InputStream;
import java.net.URL;

/**
 * @author alacasse (10/1/16)
 */
public class SoundcloudPlaylist {

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("artwork_url")
    private String artworkUrl;

    private transient Bitmap artwork;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public void setArtworkUrl(String artworkUrl) {
        this.artworkUrl = artworkUrl;
    }

    public Bitmap getArtwork() {
        return artwork;
    }

    public void setArtwork(Bitmap artwork) {
        this.artwork = artwork;
    }

    public void loadArtworkFile() {
        if (TextUtils.isEmpty(artworkUrl)) {
            return;
        }

        final String croppedArtworkUrl = artworkUrl.replaceAll("large.jpg", "crop.jpg");

        Bitmap playlistArtwork = null;
        try {
            InputStream in = new URL(croppedArtworkUrl).openStream();
            playlistArtwork = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e(Soundlist.TAG, "Error fetching artwork.", e);
        }

        this.artwork = playlistArtwork;
    }

}
