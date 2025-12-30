package org.team5892.BatteryTracking;

import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.util.CombinedRuntimeLoader;
import edu.wpi.first.util.WPIUtilJNI;
import java.io.IOException;

public class Main {

  public static void main(String[] args) throws IOException {
    NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
    WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
    CombinedRuntimeLoader.loadLibraries(Main.class, "wpiutiljni", "ntcorejni");

    if (args[0].equalsIgnoreCase("kiosk")) {
      if (args.length == 2) {
        int startingId = Integer.parseInt(args[1]);
        new Kiosk().start(startingId);
        return;
      } else {
        new Kiosk().start(-1);
      }
      return;
    }
    if (args.length != 1) {
      System.out.println("Usage: java -jar BatteryTracking.jar kiosk [starting ID] |<team/NT ip>");
      return;
    }
    new NTApi(args[0]).loop();
  }
}
