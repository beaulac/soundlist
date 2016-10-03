package ca.beauvais_la.soundlist;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import ca.beauvais_la.soundlist.loader.EndlessScrollListener;
import ca.beauvais_la.soundlist.loader.SoundcloudPlaylistLoader;
import ca.beauvais_la.soundlist.model.FBMusicLikes;
import ca.beauvais_la.soundlist.model.JsonUtil;
import ca.beauvais_la.soundlist.model.SoundcloudPlaylist;

import java.util.List;

import static ca.beauvais_la.soundlist.Soundlist.TAG;
import static ca.beauvais_la.soundlist.loader.AsyncTaskLoaderEx.getNewUniqueLoaderId;


/**
 * @author alacasse (10/1/16)
 */
public class SoundcloudPlaylistActivity extends ListActivity implements LoaderCallbacks<List<SoundcloudPlaylist>> {

    private SoundcloudPlaylistAdapter mSoundcloudPlaylistAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle extraParams = getIntent().getExtras();
        final Loader<List<SoundcloudPlaylist>> listLoader = getLoaderManager().initLoader(getNewUniqueLoaderId(), extraParams, this);

        getListView().setOnScrollListener(buildListenerForLoader(listLoader));

        mSoundcloudPlaylistAdapter = new SoundcloudPlaylistAdapter(this);
        setListAdapter(mSoundcloudPlaylistAdapter);

    }

    private EndlessScrollListener buildListenerForLoader(final Loader<List<SoundcloudPlaylist>> listLoader) {
        return new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                final boolean started = listLoader.isStarted();
                if (started) {
                    listLoader.onContentChanged();
                }
                return started;
            }
        };
    }


    @Override
    public Loader<List<SoundcloudPlaylist>> onCreateLoader(int id, Bundle args) {
        String musicLikesJson = args.getString(FBMusicLikes.DATA_KEY);
        FBMusicLikes musicLikes = JsonUtil.deserializeAs(musicLikesJson, FBMusicLikes.class);

        return new SoundcloudPlaylistLoader(this, musicLikes);
    }

    @Override
    public void onLoadFinished(Loader<List<SoundcloudPlaylist>> loader,
                               List<SoundcloudPlaylist> soundcloudPlaylists) {
        Log.d(TAG, String.format("Added %d more playlists to adapter", soundcloudPlaylists.size()));
        mSoundcloudPlaylistAdapter.addAll(soundcloudPlaylists);
    }

    @Override
    public void onLoaderReset(Loader<List<SoundcloudPlaylist>> loader) {
        mSoundcloudPlaylistAdapter.clear();
    }

}
