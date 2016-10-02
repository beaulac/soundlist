package ca.beauvais_la.soundlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ca.beauvais_la.soundlist.model.SoundcloudPlaylist;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alacasse (10/1/16)
 */
public class SoundcloudPlaylistAdapter extends ArrayAdapter<SoundcloudPlaylist> {

    private final Context context;

    static class ViewHolder {
        ImageView playlistArtwork;
        TextView playlistTitle;
    }

    public SoundcloudPlaylistAdapter(Context context) {
        super(context, R.layout.list_playlists, new ArrayList<SoundcloudPlaylist>());
        this.context = context;
    }

    public void setData(List<SoundcloudPlaylist> data) {
        clear();
        if (data != null) {
            addAll(data);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_playlists, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.playlistArtwork = (ImageView) convertView.findViewById(R.id.playlist_artwork);
            viewHolder.playlistTitle = (TextView) convertView.findViewById(R.id.playlist_title);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final SoundcloudPlaylist soundcloudPlaylist = getItem(position);
        viewHolder.playlistTitle.setText(soundcloudPlaylist.getTitle());

        Bitmap artwork = soundcloudPlaylist.getArtwork();
        viewHolder.playlistArtwork.setImageBitmap(artwork);

//        artwork.recycle();

        return convertView;
    }

}
