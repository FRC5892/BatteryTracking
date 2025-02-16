package org.team5892.BatteryTracking;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.util.CombinedRuntimeLoader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

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
      System.out.println("Usage: java -jar BatteryTracking.jar <team/NT ip>");
      return;
    }
    NetworkTableInstance instance = NetworkTableInstance.getDefault();

    instance.startClient4("Battery Tracking");
    NetworkTable table = instance.getTable("BatteryTracking");
    table.getBooleanTopic("triggerWrite").subscribe(false);
    table.getIntegerTopic("epochLocalTimeSec").subscribe(0);
  }

  protected static class NTErrors {}
}
