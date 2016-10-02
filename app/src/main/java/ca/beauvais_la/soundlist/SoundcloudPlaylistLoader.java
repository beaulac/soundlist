package ca.beauvais_la.soundlist;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import ca.beauvais_la.soundlist.loader.AsyncTaskLoaderEx;
import ca.beauvais_la.soundlist.model.FBMusicLike;
import ca.beauvais_la.soundlist.model.FBMusicLikes;
import ca.beauvais_la.soundlist.model.FBPaging;
import ca.beauvais_la.soundlist.model.SoundcloudPlaylist;
import ca.beauvais_la.soundlist.model.SoundcloudPlaylistResponse;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author alacasse (10/2/16)
 */
public class SoundcloudPlaylistLoader extends AsyncTaskLoaderEx<List<SoundcloudPlaylist>> {

    //TODO PUT THIS SOMEWHERE BETTER
    private static final String CLIENT_ID = "97f0a3478366b7e82c6009e22e3317ce";
    private static final String CLIENT_SECRET = "";
    private final ApiWrapper soundcloudApi = new ApiWrapper(CLIENT_ID, CLIENT_SECRET, null, null);

    private static final int MAXIMUM_NUMBER_OF_LIKES_TO_PROCESS = 1000;

    private final Queue<String> playlistQueriesQueue = new LinkedList<>();

    private int numberOfLikesProcessed = 0;

    private FBMusicLikes musicLikes;

    private String nextSoundcloudUrl;


    public SoundcloudPlaylistLoader(Context context, FBMusicLikes musicLikes) {
        super(context);
        this.musicLikes = musicLikes;

        addToQueryQueue(this.musicLikes.getMusicLikes());
    }

    private void addToQueryQueue(List<FBMusicLike> musicLikes) {
        for (FBMusicLike like : musicLikes) {
            playlistQueriesQueue.add(like.name);
            ++numberOfLikesProcessed;
        }
    }

    private void fillQueriesQueueWithMoreLikes() {
        FBPaging paging = musicLikes.getPaging();

        if (paging.hasNextPage()) {
            GraphRequest.Callback callback = new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {
                    Log.e("response: ", graphResponse + "");
                    try {
                        FBMusicLikes newPageOfLikes = JsonUtil.deserializeAs(graphResponse.getRawResponse(), FBMusicLikes.class);

                        SoundcloudPlaylistLoader.this.musicLikes = newPageOfLikes;

                        addToQueryQueue(newPageOfLikes.getMusicLikes());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), paging.getNextUrl(), callback).executeAndWait();
        } else {
            Log.i(Soundlist.TAG, "No more likes to query.");
        }
    }


    @Override
    public List<SoundcloudPlaylist> loadInBackground() {

        final String url = determineNextUrl();
        final List<SoundcloudPlaylist> additionalPlaylists = new ArrayList<>();

        if (!TextUtils.isEmpty(url)) {

            Log.i("soundlist", String.format("About to fetch %s", url));
            final SoundcloudPlaylistResponse response = queryUrlAndGetResponse(url);

            if (response == null) {
                Log.e(Soundlist.TAG, "Could not get response, aborting query.");
                nextSoundcloudUrl = null;
                return additionalPlaylists;
            }

            final List<SoundcloudPlaylist> morePlaylists = response.getPlaylists();
            if (morePlaylists != null && !morePlaylists.isEmpty()) {
                for (SoundcloudPlaylist playlistWithoutArtwork : morePlaylists) {
                    playlistWithoutArtwork.loadArtworkFile();
                }

                Log.i(Soundlist.TAG, String.format("Added %d new playlists", morePlaylists.size()));

                additionalPlaylists.addAll(morePlaylists);

                nextSoundcloudUrl = response.getNextPageHref();
            } else {
                nextSoundcloudUrl = null;
                Log.i(Soundlist.TAG, "done loading - no more");
            }
        }

        Log.i(Soundlist.TAG, "done loading batch");

        return additionalPlaylists;
    }

    private String determineNextUrl() {
        if (nextSoundcloudUrl == null) {
            if (playlistQueriesQueue.isEmpty() && numberOfLikesProcessed < MAXIMUM_NUMBER_OF_LIKES_TO_PROCESS) {
                fillQueriesQueueWithMoreLikes();
            }

            String nextQuery = playlistQueriesQueue.poll();
            if (nextQuery != null) {
                nextSoundcloudUrl = buildInitialPlaylistQuery(nextQuery);
            } else {
                Log.i(Soundlist.TAG, "No more playlists to query.");
                stopLoading();
            }
        }
        return nextSoundcloudUrl;
    }


    private static String buildInitialPlaylistQuery(String query) {
        return query != null ? String.format("/playlists?q=%s&linked_partitioning=1", query) : "";
    }

    private SoundcloudPlaylistResponse queryUrlAndGetResponse(String url) {
        String responseBody;
        try {
            final HttpResponse httpResponse = soundcloudApi.get(new Request(url));
            final HttpEntity responseEntity = httpResponse.getEntity();

            responseBody = EntityUtils.toString(responseEntity);

        } catch (IOException e) {
            e.printStackTrace();
            responseBody = "{}";
        }

        return SoundcloudPlaylistResponse.extractPlaylistsFromJSON(responseBody);
    }


    @Override
    protected void onStartLoading() {
        Log.i("soundlist", "starting");
        if (nextSoundcloudUrl != null || !playlistQueriesQueue.isEmpty()) {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(List<SoundcloudPlaylist> apps) {
        Log.i("soundlist", "delivering");
        super.deliverResult(apps);
    }


}
