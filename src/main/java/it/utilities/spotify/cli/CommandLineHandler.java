package it.utilities.spotify.cli;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import it.utilities.spotify.core.PlaylistUtility;
import it.utilities.spotify.core.SpotifyApiWrapper;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/** Instances of the CommandLineHandler class provide access to methods for handling user input. */
public class CommandLineHandler {

  private boolean running = true;
  private Scanner scanner;

  private SpotifyApi.Builder builder;
  private SpotifyApiWrapper spotifyApiWrapper;
  private PlaylistUtility playlistUtility;
  private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

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

  /**
   * This method is the starting point from which the program listens for user input. Execution will
   * continue as long as the <code>running</code> attribute is <code>true</code>.
   */
  public void startReadingInput() {
    do {
      System.out.print("Command: ");
      handleInputCommand(scanner.nextLine());
    } while (running);
  }

  /**
   * Manage user input.
   *
   * @param command indicates what the user writes to the console.
   */
  private void handleInputCommand(String command) {
    // Operation that must be performed
    Consumer<String[]> consumer = null;

    if (command == null || command.isBlank()) {
      return;
    }

    String[] args = command.split("\\s+");
    command = args[0];

    switch (command.toLowerCase()) {
      case "manual":
      case "help":
        consumer = printHelp();
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
      case "get-user-playlists":
        consumer = getUserPlaylists();
        break;
      case "playlist-duplicate-elements":
        consumer = getPlaylistDuplicateElements();
        break;
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
        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        args = newArgs;
      } else {
        args = new String[0];
      }

      consumer.accept(args);
    }
  }

  /**
   * Generate a help message with the description of all the commands managed by the program.
   *
   * @return
   */
  private Consumer<String[]> printHelp() {
    return args -> {
      final String commandFormat = "\t\t%-50s %s\n";
      final String helpMessage =
          "Manual for using Spotify utilities.\n"
              + "\tTo use one of the commands, you need to type the command you want and the arguments (if needed).\n"
              + "\tTo see if the command requires arguments, use the command and if arguments are needed, print an error message.\n"
              + "\tList of current commands:\n"
              + String.format(commandFormat, "help, manual", "Print all commands.")
              + String.format(
                  commandFormat,
                  "generate-authorization-url",
                  "Generate URL, which you need to use to authorize this application to access Spotify data.")
              + String.format(
                  commandFormat,
                  "generate-tokens",
                  "Given the authorization code, it retrieves the access token and the refresh token.")
              + String.format(
                  commandFormat,
                  "use-refresh-token",
                  "If you already have the refresh token, you can use it for configuration. However, you must first use the command \"generate-authorization-url\"")
              + String.format(
                  commandFormat, "get-user-playlists", "Returns all playlists of the current user.")
              + String.format(
                  commandFormat,
                  "playlist-duplicate-elements",
                  "Returns all tracks that are duplicated in a playlist.")
              + String.format(commandFormat, "exit, quit", "Finish the execution.");

      System.out.println(helpMessage);
    };
  }

  /**
   * Perform the Access Token Refresh operation before it expires.
   *
   * @param time the time from now to delay execution
   */
  private void scheduleRefreshTokenOperation(Integer time) {
    this.scheduledExecutorService.schedule(refreshAccessToken(), time, TimeUnit.SECONDS);
  }

  /**
   * Make the call to retrieve the new access token.
   *
   * @return
   */
  private Runnable refreshAccessToken() {
    return () -> {
      AuthorizationCodeCredentials authorizationCodeCredentials = null;
      try {
        authorizationCodeCredentials = this.spotifyApiWrapper.authorizationCode();
      } catch (Exception e) {
        System.err.println(e.getMessage());
        System.out.println("Error while trying to retrieve access token. Please try again.");
      }

      if (authorizationCodeCredentials != null) {
        this.builder.setAccessToken(authorizationCodeCredentials.getAccessToken());
        scheduleRefreshTokenOperation(authorizationCodeCredentials.getExpiresIn());
      }
    };
  }

  /**
   * Generate the URL that the user must use to authorize the program to use Spotify API.<br>
   * If the platform supports the browser, it automatically opens to the generated URL.
   *
   * @return
   */
  private Consumer<String[]> generateAuthorizationUrl() {
    return args -> {
      if (args == null || args.length < 3) {
        System.out.println(
            "Syntax error. To use this command you need to pass other arguments.\nSyntax: generate-authorization-url <client-id> <client-secret> <redirect-url> [open-browser]");
      } else {
        this.builder.setClientId(args[0]);
        this.builder.setClientSecret(args[1]);
        this.builder.setRedirectUri(SpotifyHttpManager.makeUri(args[2]));

        URI uri = this.spotifyApiWrapper.authorizationCodeUri();

        // The user can indicate whether he wants to open the browser to authenticate himself or
        // print the url only on the console.
        boolean openBrowser = args.length <= 3 || Boolean.TRUE.toString().equalsIgnoreCase(args[3]);

        if (Desktop.isDesktopSupported()
            && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)
            && openBrowser) {
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

  /**
   * Performs the request to retrieve the access token.
   *
   * @return
   */
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

          scheduleRefreshTokenOperation(authorizationCodeCredentials.getExpiresIn());
        }
      }
    };
  }

  /**
   * Set the refresh token to be used.
   *
   * @return
   */
  private Consumer<String[]> useRefreshToken() {
    return args -> {
      if (args == null || args.length < 1) {
        System.out.println(
            "Syntax error. To use this command you need to pass other arguments.\nSyntax: use-refresh-token <refresh-code>");
      } else {
        this.builder.setRefreshToken(args[0]);
        refreshAccessToken().run();
      }
    };
  }

  /**
   * Retrieve the playlist list of the current user.
   *
   * @return
   */
  private Consumer<String[]> getUserPlaylists() {
    return args -> {
      Paging<PlaylistSimplified> playlists = null;
      try {
        playlists = this.spotifyApiWrapper.getListOfCurrentUsersPlaylists();
      } catch (Exception e) {
        System.err.println(
            String.format(
                "Error while trying to retrieve user playlists. Error message: %s",
                e.getMessage()));
      }

      if (playlists != null && playlists.getItems() != null && playlists.getItems().length > 0) {
        Arrays.stream(playlists.getItems()).forEach(System.out::println);
      } else {
        System.out.println("Nothing to show");
      }
    };
  }

  /**
   * Recover duplicate tracks for a specific playlist.
   *
   * @return
   */
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
          tracks.forEach(System.out::println);
        } else {
          System.out.println("Nothing to show");
        }
      }
    };
  }

  /**
   * Stop program execution.
   *
   * @return
   */
  private Consumer<String[]> terminateExecution() {
    return args -> {
      System.out.print(
          "Are you sure you want to end the session? If you want to open another one you have to re-authenticate. (Y/N) ");
      String answer = scanner.nextLine();

      if (answer.equalsIgnoreCase("Y")) {
        this.scheduledExecutorService.shutdownNow();
        running = false;
        System.out.println("Terminated");
      }
    };
  }
}
