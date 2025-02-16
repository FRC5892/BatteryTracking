package org.team5892.BatteryTracking;

import edu.wpi.first.networktables.*;
import java.util.function.DoubleSupplier;

public class NTApi {
  private final IntegerSubscriber epochLocalTimeSecSub;
  private final DoubleSupplier usageSupplierAH;
  private final BooleanSubscriber triggerWriteSub;
  private final BooleanPublisher triggerWritePub;
  private final StringPublisher batteryNamePub;
  private final IntegerPublisher batteryIdPub;
  private final IntegerPublisher batteryYearPub;
  private final StructArrayPublisher<BatteryTracking.Battery.LogEntry> logPub;
  private final BooleanPublisher failedPub;
  private boolean hasRead = false;

  public NTApi(String teamOrIP) {
    NetworkTableInstance instance = NetworkTableInstance.getDefault();
    try {
      int team = Integer.parseInt(teamOrIP);
      instance.setServerTeam(team);
    } catch (NumberFormatException e) {
      instance.setServer(teamOrIP);
    }
    instance.startClient4("Battery Tracking");
    NetworkTable table = instance.getTable("BatteryTracking");
    this.epochLocalTimeSecSub = table.getIntegerTopic("epochLocalTimeSec").subscribe(-1);
    this.usageSupplierAH = table.getDoubleTopic("usageAH").subscribe(0.0);
    this.triggerWriteSub = table.getBooleanTopic("triggerWrite").subscribe(false);
    this.triggerWritePub = table.getBooleanTopic("triggerWrite").publish();
    this.batteryNamePub = table.getStringTopic("batteryName").publish();
    this.batteryIdPub = table.getIntegerTopic("batteryId").publish();
    this.batteryYearPub = table.getIntegerTopic("batteryYear").publish();
    this.logPub =
        table.getStructArrayTopic("log", BatteryTracking.Battery.LogEntry.struct).publish();
    this.failedPub = table.getBooleanTopic("failed").publish();
  }

  public void loop() {
    //noinspection InfiniteLoopStatement
    while (true) {
      // wait for DS and robot code to connect
      if (this.epochLocalTimeSecSub.get() == -1) {
        continue;
      }
      if (!this.hasRead) {
        this.hasRead = true;
        BatteryTracking.initialRead();
        BatteryTracking.Battery insertedBattery = BatteryTracking.getInsertedBattery();
        if (insertedBattery != null) {
          this.batteryNamePub.set(insertedBattery.getName());
          this.batteryIdPub.set(insertedBattery.getId());
          this.batteryYearPub.set(insertedBattery.getYear());
          this.logPub.set(
              insertedBattery.getLog().toArray(new BatteryTracking.Battery.LogEntry[0]));
        } else {
          this.failedPub.set(true);
        }
      }
      if (this.triggerWriteSub.get()) {
        BatteryTracking.updateSync(
            this.usageSupplierAH.getAsDouble()); // TODO: take in current time.*******
        this.triggerWritePub.set(false);
      }
    }
  }
}
