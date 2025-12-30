package org.team5892.BatteryTracking;

import java.util.ArrayList;
import java.util.Scanner;
import org.team5892.BatteryTracking.BatteryTracking.Battery;

public class Kiosk {

  /**
   * Starts the battery tracking kiosk
   *
   * @param startingId the starting ID for auto-incrementing batteries. Set to -1 to disable
   *     auto-increment
   */
  protected void start(int startingId) {

    if (startingId < -1) {
      throw new IllegalArgumentException("startingId must be -1 or greater");
    }

    // Yes its overengineered. It is maintainable though
    final String header = "# Welcome to the Battery Tracking Kiosk #";
    final String padding = "#".repeat(header.length());
    System.out.println(padding + "\n" + header + "\n" + padding);

    boolean autoIncrement = startingId != -1;
    if (autoIncrement) {
      System.out.println("Auto-increment enabled. Starting ID: " + startingId);
    } else {
      System.out.println("Auto-increment disabled. ");
    }
    printHep(autoIncrement);
    try (Scanner stdin = new Scanner(System.in)) {
      while (stdin.hasNextLine()) {
        final String line = stdin.nextLine();
        if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) {
          System.out.println("Exiting Kiosk. Goodbye!");
          break;
        } else {
          String[] parts = line.split(" ");
          if ((autoIncrement && parts.length != 2) || (!autoIncrement && parts.length != 3)) {
            System.out.println("Invalid command format. Please try again.");
            continue;
          }

          String name;
          int id;
          Battery.Status status;
          try {
            if (autoIncrement) {
              name = parts[0];
              status = Battery.Status.fromKey(parts[1].toUpperCase().charAt(0));
              id = startingId++;
            } else {
              id = Integer.parseInt(parts[0]);
              name = parts[1];
              status = Battery.Status.fromKey(parts[2].toUpperCase().charAt(0));
            }
          } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please try again.");
            printHep(autoIncrement);
            continue;
          } catch (IllegalArgumentException e) {
            System.out.println("Invalid status key. Please try again.");
            printHep(autoIncrement);
            continue;
          }
          Battery newBattery = new Battery(new ArrayList<>(), 0, name, id, status, 0);
          BatteryTracking.updateSyncForced(newBattery);
          System.out.println("Tag Written. Switch tag");
          printHep(autoIncrement);
        }
      }
    }
  }

  private static void printHep(boolean autoIncrement) {
    System.out.format(
        """
      Enter a command and press Enter.
      Commands:
        - '%s<Name> <Status>' to write a new tag.
          Statuses include C (competition), P (practice) and I (Inverter Only)
          Names must be one word (e.g. Jeffery or AmazingGuy)
          example: %sJeffery C
        - 'exit' or 'quit' to exit
      """,
        autoIncrement ? "" : "<ID> ", autoIncrement ? "" : "58 ");
  }
}
