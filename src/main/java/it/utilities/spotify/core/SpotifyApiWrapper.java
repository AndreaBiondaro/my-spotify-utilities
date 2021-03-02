package it.utilities.spotify.core;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import java.io.IOException;
import java.net.URI;
import org.apache.hc.core5.http.ParseException;

public class SpotifyApiWrapper {

  private SpotifyApi.Builder builder;

  public SpotifyApiWrapper(SpotifyApi.Builder builder) {
    this.builder = builder;
  }

  public Paging<PlaylistSimplified> getListOfCurrentUsersPlaylists()
      throws ParseException, SpotifyWebApiException, IOException {
    return this.builder.build().getListOfCurrentUsersPlaylists().build().execute();
  }

  public Paging<PlaylistTrack> getPlaylistsItems(String playListId)
      throws ParseException, SpotifyWebApiException, IOException {
    final GetPlaylistsItemsRequest getPlaylistsItemsRequest =
        this.builder.build().getPlaylistsItems(playListId).build();

    return getPlaylistsItemsRequest.execute();
  }

  public URI authorizationCodeUri() {
    return this.builder.build().authorizationCodeUri().build().execute();
  }

  public AuthorizationCodeCredentials authorizationCode(String code)
      throws ParseException, SpotifyWebApiException, IOException {
    return this.builder.build().authorizationCode(code).build().execute();
  }

  public AuthorizationCodeCredentials authorizationCode()
      throws ParseException, SpotifyWebApiException, IOException {
    return this.builder.build().authorizationCodeRefresh().build().execute();
  }
}
