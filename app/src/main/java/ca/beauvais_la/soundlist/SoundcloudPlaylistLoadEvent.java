package ca.beauvais_la.soundlist;

import ca.beauvais_la.soundlist.model.SoundcloudPlaylist;

import java.util.List;

/**
 * @author alacasse (10/2/16)
 */
public class SoundcloudPlaylistLoadEvent {

    private List<SoundcloudPlaylist> loadedPlaylistsWithoutArtwork;

    public SoundcloudPlaylistLoadEvent(List<SoundcloudPlaylist> loadedPlaylistsWithoutArtwork) {
        this.loadedPlaylistsWithoutArtwork = loadedPlaylistsWithoutArtwork;
    }

    public List<SoundcloudPlaylist> getLoadedPlaylists() {
        return loadedPlaylistsWithoutArtwork;
    }
}
