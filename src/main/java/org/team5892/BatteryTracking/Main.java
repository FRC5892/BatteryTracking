package org.team5892.BatteryTracking;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.util.CombinedRuntimeLoader;
import java.io.IOException;

public class Main {
  private static Main instance;

  public static Main getInstance() {
    if (instance == null) {
      instance = new Main();
    }
    return instance;
  }

  public static void main(String[] args) throws IOException {
    NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
    CombinedRuntimeLoader.loadLibraries(Main.class, "wpiutiljni", "ntcorejni");

    Main.getInstance().start(args);
  }

  public void start(String... args) throws IOException {

    if (args.length == 0) {
      System.out.println("Usage: java -jar BatteryTracking.jar <team/ip/localhost>");
      return;
    }
    NetworkTableInstance instance = NetworkTableInstance.getDefault();
    try {
      int team = Integer.parseInt(args[0]);
      instance.setServerTeam(team);
    } catch (NumberFormatException e) {
      instance.setServer(args[0]);
    }
    instance.startClient4("Battery Tracking");
    NetworkTable table = instance.getTable("BatteryTracking");
    table.getIntegerTopic("write");
  }

  protected static class NTErrors {}
}
