package org.team5892.BatteryTracking;

import edu.wpi.first.networktables.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.DoubleSupplier;

public class NTApi {
  private final IntegerSubscriber epochLocalTimeMinSub;
  private final DoubleSupplier usageSupplierAH;
  private final BooleanSubscriber triggerWriteSub;
  private final BooleanPublisher triggerWritePub;
  private final BooleanPublisher failedPub;
  private final NetworkTable table;
  private final StringPublisher batteryNamePub;
  private final IntegerPublisher batteryIdPub;
  private final StringPublisher batteryStatusPub;
  private final StructArrayPublisher<BatteryTracking.Battery.LogEntry> logPub;
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
    this.table = instance.getTable("BatteryTracking");
    this.epochLocalTimeMinSub = table.getIntegerTopic("epochLocalTimeMin").subscribe(-1);
    this.usageSupplierAH = table.getDoubleTopic("usageAH").subscribe(0.0);
    this.triggerWriteSub = table.getBooleanTopic("triggerWrite").subscribe(false);
    this.triggerWritePub = table.getBooleanTopic("triggerWrite").publish();
    this.failedPub = table.getBooleanTopic("failed").publish();
    this.batteryNamePub = table.getStringTopic("batteryName").publish();
    this.batteryIdPub = table.getIntegerTopic("batteryId").publish();
    this.batteryStatusPub = table.getStringTopic("batteryYear").publish();
    this.logPub =
        table.getStructArrayTopic("log", BatteryTracking.Battery.LogEntry.struct).publish();
    failedPub.set(false);
  }

  public void loop() {
    //noinspection InfiniteLoopStatement
    while (true) {
      try {
        // wait for DS and robot code to connect
        if (this.epochLocalTimeMinSub.get() == -1
            || !NetworkTableInstance.getDefault().isConnected()) {
          this.hasRead = false;
          continue;
        }
        // Once connected, read the battery data
        if (!this.hasRead) {
          this.hasRead = true;
          // Check if coprocessor is still up but robot code restarted
          if (BatteryTracking.getInsertedBattery().isEmpty()) {
            BatteryTracking.initialRead();
          }
          if (BatteryTracking.getInsertedBattery().isPresent()) {
            BatteryTracking.Battery insertedBattery = BatteryTracking.getInsertedBattery().get();
            batteryNamePub.set(insertedBattery.getName());
            batteryIdPub.set(insertedBattery.getId());
            batteryStatusPub.set(insertedBattery.getStatus().name());
            logPub.set(insertedBattery.getLog().toArray(new BatteryTracking.Battery.LogEntry[0]));
            triggerWritePub.set(false);
          } else {
            this.failedPub.set(true);
          }
        }
        if (this.triggerWriteSub.get()) {
          this.triggerWritePub.set(false);
          BatteryTracking.updateSync(
              this.usageSupplierAH.getAsDouble(),
              LocalDateTime.ofEpochSecond(this.epochLocalTimeMinSub.get() * 60, 0, ZoneOffset.UTC));
        }
      } catch (Exception e) {
        failedPub.set(true);
      }
    }
  }
}
