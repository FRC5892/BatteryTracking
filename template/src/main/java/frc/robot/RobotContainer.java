// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import frc.robot.subsystems.BatteryTrackingNew;

public class RobotContainer {
  private final BatteryTrackingNew batteryTracking;

  public RobotContainer() {
    batteryTracking =
        new BatteryTrackingNew(new PowerDistribution(63, PowerDistribution.ModuleType.kRev));
    configureBindings();
  }

  private void configureBindings() {
    RobotModeTriggers.teleop().onFalse(batteryTracking.writeCommand());
    RobotModeTriggers.autonomous()
        .and(() -> !DriverStation.isFMSAttached())
        .onFalse(batteryTracking.writeCommand());
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
