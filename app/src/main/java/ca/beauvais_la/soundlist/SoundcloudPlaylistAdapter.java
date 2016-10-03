package ca.beauvais_la.soundlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ca.beauvais_la.soundlist.model.SoundcloudPlaylist;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

/**
 * @author alacasse (10/1/16)
 */
public class SoundcloudPlaylistAdapter extends ArrayAdapter<SoundcloudPlaylist> {

    private final Context context;
    private ImageLoader imageLoader;

    public SoundcloudPlaylistAdapter(Context context) {
        super(context, R.layout.list_playlists, new ArrayList<SoundcloudPlaylist>());
        this.context = context;
        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this.context)
                .diskCacheSize(100 * 1024 * 1024)
                .memoryCacheSize(10 * 1024 * 1024)
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_playlists, parent, false);

            viewHolder = new ViewHolder((ImageView) convertView.findViewById(R.id.playlist_artwork),
                                        (TextView) convertView.findViewById(R.id.playlist_title));

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final SoundcloudPlaylist soundcloudPlaylist = getItem(position);

        viewHolder.playlistTitle.setText(soundcloudPlaylist.title);
        imageLoader.displayImage(soundcloudPlaylist.artworkUrl, viewHolder.playlistArtwork);

        return convertView;
    }

    private static class ViewHolder {
        public ViewHolder(ImageView playlistArtwork, TextView playlistTitle) {
            this.playlistArtwork = playlistArtwork;
            this.playlistTitle = playlistTitle;
        }

        ImageView playlistArtwork;
        TextView playlistTitle;
    }

}
