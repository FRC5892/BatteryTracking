package org.team5892.BatteryTracking;

import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.util.CombinedRuntimeLoader;
import java.io.IOException;

public class Main {

  public static void main(String[] args) throws IOException {
    NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
    CombinedRuntimeLoader.loadLibraries(Main.class, "wpiutiljni", "ntcorejni");

    if (args.length == 0) {
      System.out.println("Usage: java -jar BatteryTracking.jar <team/NT ip>");
      return;
    }
    new NTApi(args[0]).loop();
  }
}
