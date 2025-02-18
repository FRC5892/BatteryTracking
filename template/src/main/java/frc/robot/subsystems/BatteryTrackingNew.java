// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.networktables.BooleanPublisher;
import edu.wpi.first.networktables.BooleanSubscriber;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.IntegerPublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import java.time.ZonedDateTime;

public class BatteryTrackingNew extends SubsystemBase {
  private final IntegerPublisher epochLocalTimeMinPub;
  private final BooleanPublisher writePub;
  private final DoublePublisher usageSupplier;
  private final BooleanSubscriber failedSub;
  private final Timer writeTimer = new Timer();
  private final Alert readAlert = new Alert("Battery has not been read yet", AlertType.kWarning);
  private final Alert failedAlert = new Alert("Battery Tracking failed!", AlertType.kError);

  private double batteryUsageAH = 0;
  private final PowerDistribution powerDistribution;

  public Command writeCommand() {
    return runOnce(() -> writePub.set(true));
  }

  /** Creates a new BatteryTrackingNew. */
  public BatteryTrackingNew(PowerDistribution powerDistribution) {
    this.powerDistribution = powerDistribution;
    NetworkTable table = NetworkTableInstance.getDefault().getTable("BatteryTracking");
    epochLocalTimeMinPub = table.getIntegerTopic("epochLocalTimeMin").publish();
    writePub = table.getBooleanTopic("triggerWrite").publish();
    usageSupplier = table.getDoubleTopic("usageAH").publish();
    failedSub = table.getBooleanTopic("failed").subscribe(false);
    readAlert.set(true);
    writeTimer.start();
  }

  @Override
  public void periodic() {
    batteryUsageAH += (powerDistribution.getTotalCurrent() * (Robot.kDefaultPeriod / (60 * 60)));
    usageSupplier.set(batteryUsageAH);
    if (RobotController.isSystemTimeValid()) {
      epochLocalTimeMinPub.set(ZonedDateTime.now().toEpochSecond() / (60));
    }
    if (writeTimer.hasElapsed(180)) {
      writeTimer.reset();
      writePub.set(true);
    }
    ;
    // Only set the failed alert once, allowing it to move down the list
    if (failedSub.get() && !failedAlert.get()) {
      failedAlert.set(true);
    } else {
      failedAlert.set(false);
    }
  }
}
