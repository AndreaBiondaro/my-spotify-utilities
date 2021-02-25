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

  /**
   * Check if there is any track with the same title (or partially the same) in the Spotify
   * playlist.<br>
   * The result of this execution is not 100% correct. You have to manually check if the tracks are
   * really duplicated.
   *
   * @param playListId The ID of the playlist to be checked
   * @return List of duplicated tracks.
   * @throws IOException
   * @throws SpotifyWebApiException
   * @throws ParseException
   * @throws NullPointerException if the element returned by the request is null
   */
  public List<PlaylistTrack> getDuplicatesTracksByName(String playListId)
      throws IOException, SpotifyWebApiException, ParseException {
    final PlaylistTrack[] tracks = this.requestHandler.getPlaylistsItems(playListId).getItems();

    List<PlaylistTrack> duplicates = new ArrayList<>();

    // FIXME: find a way to improve performance, because this logic has O (n ^ 2) time complexity
    // and with huge amount of tracks I think time is a problem

    for (int i = 0; i < tracks.length; i++) {
      final PlaylistTrack currentTrack = tracks[i];

      boolean duplicate = false;
      List<PlaylistTrack> others = new ArrayList<>();

      for (int j = i + 1; j < tracks.length; j++) {
        final PlaylistTrack track = tracks[j];

        if (compareSongTitle(currentTrack.getTrack().getName(), track.getTrack().getName())) {
          duplicate = true;
          others.add(track);
        }
      }

      if (duplicate) {
        duplicates.add(currentTrack);
        duplicates.addAll(others);
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
