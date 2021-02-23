package it.utilities.spotify.core;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import java.io.IOException;
import org.apache.hc.core5.http.ParseException;
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

  @Mock private RequestHandler requestHandler;

  @BeforeAll
  static void setup() {
    MockitoAnnotations.openMocks(PlaylistUtilityTest.class);
  }

  @Test
  void testCompareSongTitle() throws ParseException, SpotifyWebApiException, IOException {
    Paging<PlaylistTrack> tracks = new Paging.Builder<PlaylistTrack>().setItems(null).build();

    when(requestHandler.getPlaylistsItems(anyString())).thenReturn(tracks);
    System.out.println(playlistUtility.getDuplicateTrackByName(""));
  }
}
