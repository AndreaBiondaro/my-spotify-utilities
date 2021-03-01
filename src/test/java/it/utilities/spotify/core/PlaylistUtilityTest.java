package it.utilities.spotify.core;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.hc.core5.http.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PlaylistUtilityTest {

  @InjectMocks private PlaylistUtility playlistUtility;

  @Mock private SpotifyApiWrapper spotifyApiWrapper;

  @BeforeAll
  static void setup() {
    MockitoAnnotations.openMocks(PlaylistUtilityTest.class);
  }

  @Test
  void testGetDuplicatesTracksByNameNullResponse()
      throws ParseException, SpotifyWebApiException, IOException {
    when(spotifyApiWrapper.getPlaylistsItems(anyString())).thenReturn(null);

    Assertions.assertThrows(
        NullPointerException.class, () -> playlistUtility.getDuplicatesTracksByName(""));
  }

  @Test
  void testGetDuplicatesTracksByNameNullItems()
      throws ParseException, SpotifyWebApiException, IOException {
    Paging<PlaylistTrack> tracks = new Paging.Builder<PlaylistTrack>().setItems(null).build();

    when(spotifyApiWrapper.getPlaylistsItems(anyString())).thenReturn(tracks);

    Assertions.assertThrows(
        NullPointerException.class, () -> playlistUtility.getDuplicatesTracksByName(""));
  }

  @Test
  void testGetDuplicatesTracksByNameNullTrack()
      throws ParseException, SpotifyWebApiException, IOException {
    PlaylistTrack[] songs = new PlaylistTrack[2];
    songs[0] = new PlaylistTrack.Builder().setTrack(null).build();
    songs[1] = new PlaylistTrack.Builder().setTrack(null).build();

    Paging<PlaylistTrack> tracks = new Paging.Builder<PlaylistTrack>().setItems(songs).build();

    when(spotifyApiWrapper.getPlaylistsItems(anyString())).thenReturn(tracks);

    Assertions.assertThrows(
        NullPointerException.class, () -> playlistUtility.getDuplicatesTracksByName(""));
  }

  @Test
  void testGetDuplicatesTracksByNameNullTitle()
      throws ParseException, SpotifyWebApiException, IOException {
    Track track1 = new Track.Builder().setName(null).build();
    Track track2 = new Track.Builder().setName(null).build();

    PlaylistTrack[] songs = new PlaylistTrack[2];
    songs[0] = new PlaylistTrack.Builder().setTrack(track1).build();
    songs[1] = new PlaylistTrack.Builder().setTrack(track2).build();

    Paging<PlaylistTrack> tracks = new Paging.Builder<PlaylistTrack>().setItems(songs).build();

    when(spotifyApiWrapper.getPlaylistsItems(anyString())).thenReturn(tracks);

    Assertions.assertThrows(
        NullPointerException.class, () -> playlistUtility.getDuplicatesTracksByName(""));
  }

  @Test
  void testGetDuplicatesTracksByNameNoDuplicates()
      throws ParseException, SpotifyWebApiException, IOException {
    Track track1 = new Track.Builder().setName("Give me").build();
    Track track2 = new Track.Builder().setName("Another way").build();
    Track track3 = new Track.Builder().setName("A Give me").build();

    PlaylistTrack[] songs = new PlaylistTrack[3];
    songs[0] = new PlaylistTrack.Builder().setTrack(track1).build();
    songs[1] = new PlaylistTrack.Builder().setTrack(track2).build();
    songs[2] = new PlaylistTrack.Builder().setTrack(track3).build();

    Paging<PlaylistTrack> tracks = new Paging.Builder<PlaylistTrack>().setItems(songs).build();

    when(spotifyApiWrapper.getPlaylistsItems(anyString())).thenReturn(tracks);

    Assertions.assertTrue(playlistUtility.getDuplicatesTracksByName("").isEmpty());
  }

  @Test
  void testGetDuplicatesTracksByNameWithDuplicates()
      throws ParseException, SpotifyWebApiException, IOException {
    final Track song1 = new Track.Builder().setName("Give Me").build();
    final Track song2 = new Track.Builder().setName("Another way").build();
    final Track song3 = new Track.Builder().setName("GIVE ME - Extended").build();

    final PlaylistTrack track1 = new PlaylistTrack.Builder().setTrack(song1).build();
    final PlaylistTrack track2 = new PlaylistTrack.Builder().setTrack(song2).build();
    final PlaylistTrack track3 = new PlaylistTrack.Builder().setTrack(song3).build();

    PlaylistTrack[] songs = new PlaylistTrack[3];
    songs[0] = track1;
    songs[1] = track2;
    songs[2] = track3;

    Paging<PlaylistTrack> tracks = new Paging.Builder<PlaylistTrack>().setItems(songs).build();

    when(spotifyApiWrapper.getPlaylistsItems(anyString())).thenReturn(tracks);

    List<PlaylistTrack> expected = Arrays.asList(track1, track3);

    Assertions.assertIterableEquals(expected, playlistUtility.getDuplicatesTracksByName(""));
  }

  @Test
  void testGetDuplicatesTracksByNameWithNoDuplicatesExtremeCase()
      throws ParseException, SpotifyWebApiException, IOException {
    final Track song1 = new Track.Builder().setName("give me - original version").build();
    final Track song2 = new Track.Builder().setName("Another way").build();
    final Track song3 = new Track.Builder().setName("GIVE ME - Extended").build();

    final PlaylistTrack track1 = new PlaylistTrack.Builder().setTrack(song1).build();
    final PlaylistTrack track2 = new PlaylistTrack.Builder().setTrack(song2).build();
    final PlaylistTrack track3 = new PlaylistTrack.Builder().setTrack(song3).build();

    PlaylistTrack[] songs = new PlaylistTrack[3];
    songs[0] = track1;
    songs[1] = track2;
    songs[2] = track3;

    Paging<PlaylistTrack> tracks = new Paging.Builder<PlaylistTrack>().setItems(songs).build();

    when(spotifyApiWrapper.getPlaylistsItems(anyString())).thenReturn(tracks);

    Assertions.assertTrue(playlistUtility.getDuplicatesTracksByName("").isEmpty());
  }
}
