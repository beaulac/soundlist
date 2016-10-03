package ca.beauvais_la.soundlist.loader;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import ca.beauvais_la.soundlist.R;
import ca.beauvais_la.soundlist.model.FBMusicLike;
import ca.beauvais_la.soundlist.model.FBMusicLikes;
import ca.beauvais_la.soundlist.model.FBPaging;
import ca.beauvais_la.soundlist.model.JsonUtil;
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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static ca.beauvais_la.soundlist.Soundlist.TAG;

/**
 * @author alacasse (10/2/16)
 */
public class SoundcloudPlaylistLoader extends AsyncTaskLoaderEx<List<SoundcloudPlaylist>> {

    private static final int MAX_NUMBER_OF_LIKES_TO_PROCESS = 1000;

    private FBMusicLikes musicLikes;
    private int numberOfLikesProcessed = 0;
    private final Queue<String> playlistQueriesQueue = new LinkedList<>();

    private String nextSoundcloudUrl;
    private final ApiWrapper soundCloudAPI;


    public SoundcloudPlaylistLoader(Context context, FBMusicLikes musicLikes) {
        super(context);

        this.soundCloudAPI = new ApiWrapper(context.getString(R.string.soundcloud_client_id), "", null, null);

        this.musicLikes = musicLikes;
        addToQueryQueue(musicLikes);
    }

    private void addToQueryQueue(FBMusicLikes musicLikes) {
        for (FBMusicLike like : musicLikes) {
            playlistQueriesQueue.add(like.name);
            ++numberOfLikesProcessed;
        }
    }


    @Override
    public List<SoundcloudPlaylist> loadInBackground() {

        final String queryUrl = determineNextQueryURL();

        List<SoundcloudPlaylist> additionalPlaylists = Collections.emptyList();
        String nextUrl = null;

        if (!TextUtils.isEmpty(queryUrl)) {
            Log.d(TAG, String.format("About to fetch %s", queryUrl));

            final SoundcloudPlaylistResponse response = queryAndGetResponse(queryUrl);

            if (response != null) {
                final List<SoundcloudPlaylist> responsePlaylists = response.getPlaylists();

                if (responsePlaylists != null && !responsePlaylists.isEmpty()) {
                    additionalPlaylists = responsePlaylists;
                    nextUrl = response.getNextPageUrl();

                    Log.d(TAG, String.format("Added %d new playlists", responsePlaylists.size()));
                } else {
                    Log.d(TAG, "Done loading - No more playlists for query");
                }
            } else {
                Log.e(TAG, String.format("Could not get response, aborting query on %s.", queryUrl));
            }
        }

        this.nextSoundcloudUrl = nextUrl;
        return additionalPlaylists;
    }


    private String determineNextQueryURL() {
        if (nextSoundcloudUrl == null) {
            if (playlistQueriesQueue.isEmpty() && numberOfLikesProcessed < MAX_NUMBER_OF_LIKES_TO_PROCESS) {
                fillQueriesQueueWithMoreLikes();
            }

            String nextQuery = playlistQueriesQueue.poll();
            if (nextQuery != null) {
                nextSoundcloudUrl = buildInitialPlaylistQuery(nextQuery);
            } else {
                Log.i(TAG, "No more playlists to query.");
                stopLoading();
            }
        }

        return nextSoundcloudUrl;
    }

    private SoundcloudPlaylistResponse queryAndGetResponse(String url) {
        String responseBody;
        try {
            final HttpResponse httpResponse = soundCloudAPI.get(new Request(url));
            final HttpEntity responseEntity = httpResponse.getEntity();

            responseBody = EntityUtils.toString(responseEntity);

        } catch (IOException e) {
            Log.e(TAG, "Could not get playlist ", e);
            return null;
        }

        return JsonUtil.deserializeAs(responseBody, SoundcloudPlaylistResponse.class);
    }


    private void fillQueriesQueueWithMoreLikes() {
        FBPaging paging = musicLikes.paging;

        if (paging.hasNextPage()) {
            GraphRequest.Callback callback = new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {
                    String rawResponse = graphResponse.getRawResponse();
                    Log.d(TAG, rawResponse);
                    try {
                        FBMusicLikes newPageOfLikes = JsonUtil.deserializeAs(rawResponse, FBMusicLikes.class);

                        SoundcloudPlaylistLoader.this.musicLikes = newPageOfLikes;
                        addToQueryQueue(newPageOfLikes);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), paging.getNextUrl(), callback).executeAndWait();
        } else {
            Log.i(TAG, "No more likes to query.");
        }
    }

    private static String buildInitialPlaylistQuery(String query) {
        return query != null ? String.format("/playlists?q=%s&linked_partitioning=1", query) : "";
    }

}
