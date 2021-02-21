package it.utilities.spotify.cli;

import java.util.Scanner;

public class CommandLineHandler {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String command = null;

        do {
            System.out.print("Command: ");
            command = scanner.nextLine();

            System.out.println(command);
        } while (!command.equalsIgnoreCase("exit"));

        System.out.println("Close");
    }
}
