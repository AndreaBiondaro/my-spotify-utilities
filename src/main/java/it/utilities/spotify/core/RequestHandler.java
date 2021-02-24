package it.utilities.spotify.core;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import java.io.IOException;
import org.apache.hc.core5.http.ParseException;

public class RequestHandler {

  private SpotifyApi spotifyApi;

  public RequestHandler(SpotifyApi spotifyApi) {
    this.spotifyApi = spotifyApi;
  }

  public Paging<PlaylistTrack> getPlaylistsItems(String playListId)
      throws ParseException, SpotifyWebApiException, IOException {
    final GetPlaylistsItemsRequest getPlaylistsItemsRequest =
        this.spotifyApi.getPlaylistsItems(playListId).build();

    return getPlaylistsItemsRequest.execute();
  }
}
