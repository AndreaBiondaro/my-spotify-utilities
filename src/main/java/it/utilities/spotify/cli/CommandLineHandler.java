package it.utilities.spotify.cli;

import java.util.Scanner;
import java.util.function.Consumer;

public class CommandLineHandler {

  private static boolean running = true;

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    do {
      System.out.print("Command: ");
      handleInputCommand(scanner.nextLine());
    } while (running);
  }

  private static void handleInputCommand(String command) {
    Consumer consumer = null;

    if (command.equalsIgnoreCase("exit")) {
      running = false;
      System.out.println("Closing");
    }
  }
}
