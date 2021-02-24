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

  public List<PlaylistTrack> getDuplicatesTracksByName(String playListId)
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
   * Compare the name of two playlist items while ignoring the case sensitivity.
   *
   * @param firstName
   * @param secondName
   * @throws NullPointerException if one of the two inputs is null
   * @return <code>true</code> if the two titles are the same or start with the same words, <code>
   *     false</code> otherwise
   */
  private boolean compareSongTitle(String firstName, String secondName) {
    if (firstName.equalsIgnoreCase(secondName)) {
      return true;
    }

    String[] firstWords = firstName.split("\\s+");
    String[] secondWords = secondName.split("\\s+");

    for (int i = 0; i < Math.min(firstWords.length, secondWords.length); i++) {
      String firstWord = firstWords[i];
      String secondWord = secondWords[i];

      if (!firstWord.equalsIgnoreCase(secondWord)) {
        return false;
      }
    }

    return true;
  }
}
