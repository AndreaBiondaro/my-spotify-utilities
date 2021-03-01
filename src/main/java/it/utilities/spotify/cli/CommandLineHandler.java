package it.utilities.spotify.cli;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import it.utilities.spotify.core.PlaylistUtility;
import it.utilities.spotify.core.SpotifyApiWrapper;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class CommandLineHandler {

  private boolean running = true;
  private Scanner scanner;

  private SpotifyApi.Builder builder;
  private SpotifyApiWrapper spotifyApiWrapper;
  private PlaylistUtility playlistUtility;

  public CommandLineHandler() {
    this.scanner = new Scanner(System.in);
    this.builder = SpotifyApi.builder();
    this.spotifyApiWrapper = new SpotifyApiWrapper(this.builder);
    this.playlistUtility = new PlaylistUtility(this.spotifyApiWrapper);
  }

  public static void main(String[] args) {
    CommandLineHandler handler = new CommandLineHandler();
    handler.startReadingInput();
  }

  public void startReadingInput() {
    do {
      System.out.print("Command: ");
      handleInputCommand(scanner.nextLine());
    } while (running);
  }

  private void handleInputCommand(String command) {
    Consumer consumer = null;

    if (command == null || command.isBlank()) {
      return;
    }

    String[] args = command.split("\\s+");
    command = args[0];

    // TODO: need to understand how see the playlists that are private

    // authorization-request --> client id, client secret, redirect url --> open browser if possible
    // or print the url generated

    // generate-tokens* --> code --> request return access token and refresh token. Print refresh
    // token than user can save somewhere

    // refresh-token* --> client id, client-secret, refresh token --> this is used if user already
    // have the access token

    //    * --> before access token expire need to make a request to give another one

    // ScheduledExecutorService

    switch (command.toLowerCase()) {
      case "manual":
      case "help":
        break;
      case "generate-authorization-url":
        consumer = generateAuthorizationUrl();
        break;
      case "generate-tokens":
        consumer = generateTokens();
        break;
      case "use-refresh-token":
        consumer = useRefreshToken();
        break;
      case "playlist-duplicate-elements":
        consumer = getPlaylistDuplicateElements();
      case "exit":
      case "quit":
        consumer = terminateExecution();
        break;
      default:
        System.out.println(String.format("The command \"%s\" is not recognized", command));
        return;
    }

    if (consumer != null) {
      if (args.length > 1) {
        // Remove the command you typed from the input arguments, because it is not used
        System.arraycopy(args, 1, args, 0, args.length - 1);
      } else {
        args = new String[0];
      }

      consumer.accept(args);
    }
  }

  private Consumer<String[]> generateAuthorizationUrl() {
    return args -> {
      if (args == null || args.length < 3) {
        System.out.println(
            "Syntax error. To use this command you need to pass other arguments.\nSyntax: generate-authorization-url <client-id> <client-secret> <redirect-url>");
      } else {
        this.builder.setClientId(args[0]);
        this.builder.setClientSecret(args[1]);
        this.builder.setRedirectUri(SpotifyHttpManager.makeUri(args[2]));

        URI uri = this.spotifyApiWrapper.authorizationCodeUri();

        if (Desktop.isDesktopSupported()
            && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
          try {
            Desktop.getDesktop().browse(uri);
          } catch (IOException e) {
            System.out.println(uri);
          }
        } else {
          System.out.println(uri);
        }
      }
    };
  }

  private Consumer<String[]> generateTokens() {
    return args -> {
      if (args == null || args.length < 1) {
        System.out.println(
            "Syntax error. To use this command you need to pass other arguments.\nSyntax: generate-tokens <code>");
      } else {
        AuthorizationCodeCredentials authorizationCodeCredentials = null;

        try {
          authorizationCodeCredentials = this.spotifyApiWrapper.authorizationCode(args[0]);
        } catch (Exception e) {
          System.err.println(e.getMessage());
          System.out.println("Error while trying to retrieve access tokens. Please try again.");
        }

        if (authorizationCodeCredentials != null) {
          this.builder.setAccessToken(authorizationCodeCredentials.getAccessToken());
          this.builder.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
          System.out.println("Refresh Token: " + authorizationCodeCredentials.getRefreshToken());
        }
      }
    };
  }

  private Consumer<String[]> useRefreshToken() {
    return args -> {
      if (args == null || args.length < 1) {
        System.out.println(
            "Syntax error. To use this command you need to pass other arguments.\nSyntax: use-refresh-token <refresh-code>");
      } else {
        this.builder.setRefreshToken(args[0]);

        AuthorizationCodeCredentials authorizationCodeCredentials = null;
        try {
          authorizationCodeCredentials = this.spotifyApiWrapper.authorizationCode();
        } catch (Exception e) {
          System.err.println(e.getMessage());
          System.out.println("Error while trying to retrieve access token. Please try again.");
        }

        if (authorizationCodeCredentials != null) {
          this.builder.setAccessToken(authorizationCodeCredentials.getAccessToken());
        }
      }
    };
  }

  private Consumer<String[]> getPlaylistDuplicateElements() {
    return args -> {
      if (args == null || args.length < 1) {
        System.out.println(
            "Syntax error. To use this command you need to pass other arguments.\nSyntax: playlist-duplicate-elements <playlist-id>");
      } else {
        List<PlaylistTrack> tracks = null;

        try {
          tracks = this.playlistUtility.getDuplicatesTracksByName(args[0]);
        } catch (Exception e) {
          System.err.println(e.getMessage());
          System.out.println("Error while trying to retrieve playlist elements. Please try again.");
        }

        if (tracks != null && !tracks.isEmpty()) {
          tracks.stream().forEach(track -> System.out.println(track));
        } else {
          System.out.println("Nothing to show");
        }
      }
    };
  }

  private Consumer terminateExecution() {
    return s -> {
      System.out.print(
          "Are you sure you want to end the session? If you want to open another one you have to re-authenticate. (Y/N) ");
      String answer = scanner.nextLine();

      if (answer.equalsIgnoreCase("Y")) {
        running = false;
        System.out.println("Terminated");
      }
    };
  }
}
