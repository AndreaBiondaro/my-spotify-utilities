package it.utilities.spotify.core;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hc.core5.http.ParseException;

/**
 * Instances of the PlaylistUtility class provide access to methods for performing operations on
 * Spotify playlists.
 */
public class PlaylistUtility {

  private RequestHandler requestHandler;

  public PlaylistUtility(RequestHandler requestHandler) {
    this.requestHandler = requestHandler;
  }

  public List<PlaylistTrack> getDuplicateTrackByName(String playListId)
      throws IOException, SpotifyWebApiException, ParseException {
    final PlaylistTrack[] tracks = this.requestHandler.getPlaylistsItems(playListId).getItems();

    List<PlaylistTrack> duplicates = new ArrayList<>();

    for (int i = 0; i < tracks.length; i++) {
      final PlaylistTrack currentTrack = tracks[i];

      boolean duplicate = false;

      for (int j = i + 1; j < tracks.length; j++) {
        final PlaylistTrack track = tracks[j];

        if (compareSongTitle(currentTrack.getTrack().getName(), track.getTrack().getName())) {
          duplicate = true;
          duplicates.add(track);
        }
      }

      if (duplicate) {
        duplicates.add(currentTrack);
      }
    }

    return duplicates;
  }

  /**
   * Compare the title of two songs while ignoring the case sensitivity.
   *
   * @param songOne
   * @param songTwo
   * @throws NullPointerException if one of the two inputs is null
   * @return <code>true</code> if the two titles are the same or start with the same words, <code>
   *     false</code> otherwise
   */
  private boolean compareSongTitle(String songOne, String songTwo) {
    if (songOne.equalsIgnoreCase(songTwo)) {
      return true;
    }

    String[] songOneWords = songOne.split("\\s+");
    String[] songTwoWords = songTwo.split("\\s+");

    for (int i = 0; i < Math.min(songOneWords.length, songTwoWords.length); i++) {
      String wordOne = songOneWords[i];
      String wordTwo = songTwoWords[i];

      if (!wordOne.equalsIgnoreCase(wordTwo)) {
        return false;
      }
    }

    return true;
  }
}
