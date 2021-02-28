package it.utilities.spotify.cli;

import com.wrapper.spotify.SpotifyApi;
import it.utilities.spotify.core.PlaylistUtility;
import it.utilities.spotify.core.RequestHandler;
import java.util.Scanner;
import java.util.function.Consumer;

public class CommandLineHandler {

  private boolean running = true;
  private Scanner scanner;

  private SpotifyApi spotifyApi;
  private RequestHandler requestHandler;
  private PlaylistUtility playlistUtility;

  public CommandLineHandler() {
    this.scanner = new Scanner(System.in);
    this.spotifyApi = new SpotifyApi.Builder().build();
    this.requestHandler = new RequestHandler(this.spotifyApi);
    this.playlistUtility = new PlaylistUtility(this.requestHandler);
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

    switch (command.toLowerCase()) {
      case "manual":
      case "help":
        break;
      case "generate-authorization-url":
        break;
      case "generate-tokens":
        break;
      case "use-refresh-token":
        break;
      case "exit":
        consumer = stopReadingInput();
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

  private Consumer stopReadingInput() {
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
