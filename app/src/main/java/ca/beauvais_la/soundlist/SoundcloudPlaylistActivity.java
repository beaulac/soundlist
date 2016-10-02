package ca.beauvais_la.soundlist;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import ca.beauvais_la.soundlist.model.FBMusicLikes;
import ca.beauvais_la.soundlist.model.SoundcloudPlaylist;
import ca.beauvais_la.soundlist.model.FBUser;

import java.util.List;


/**
 * @author alacasse (10/1/16)
 */
public class SoundcloudPlaylistActivity extends ListActivity implements LoaderManager.LoaderCallbacks<List<SoundcloudPlaylist>> {

    private SoundcloudPlaylistAdapter mSoundcloudPlaylistAdapter;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupProgressBar();

        mSoundcloudPlaylistAdapter = new SoundcloudPlaylistAdapter(this);
        setListAdapter(mSoundcloudPlaylistAdapter);

        final ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        getLoaderManager().initLoader(0, getIntent().getExtras(), this);
    }


    private void setupProgressBar() {
        // Create a progress bar to display while the list loads
        progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                                                     LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        progressBar.setIndeterminate(false);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) this.findViewById(android.R.id.content);
        root.addView(progressBar);
    }

    @Override
    public Loader<List<SoundcloudPlaylist>> onCreateLoader(int id, Bundle args) {

        String musicLikesJson = args.getString("musicLikes");

        FBMusicLikes musicLikes = JsonUtil.deserializeAs(musicLikesJson, FBMusicLikes.class);

        return new SoundcloudPlaylistLoader(this, musicLikes);
    }

    @Override
    public void onLoadFinished(Loader<List<SoundcloudPlaylist>> loader,
                               List<SoundcloudPlaylist> soundcloudPlaylists) {
        Log.i("soundlist", String.format("updated adapter with %d playlists", soundcloudPlaylists.size()));
        mSoundcloudPlaylistAdapter.addAll(soundcloudPlaylists);

        progressBar.setVisibility(View.GONE);

        if (loader.isStarted()) {
            loader.onContentChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<SoundcloudPlaylist>> loader) {
        mSoundcloudPlaylistAdapter.setData(null);
    }


}
