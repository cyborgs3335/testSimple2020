/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.lib.util.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
  private final WPI_VictorSPX frontLeftController = new WPI_VictorSPX(11);
  private final WPI_VictorSPX frontRightController = new WPI_VictorSPX(10);
  private final WPI_VictorSPX backLeftController = new WPI_VictorSPX(13);
  private final WPI_VictorSPX backRightController = new WPI_VictorSPX(12);

  private final ChoosableSolenoid pneumatic1 = new ChoosableSolenoid(1, 0);
  private final ChoosableSolenoid pneumatic2 = new ChoosableSolenoid(1, 2);

  private final TalonFX rollers = new TalonFX(30);

  private final SpeedControllerGroup leftGroup = new SpeedControllerGroup(frontLeftController
      /*, backLeftController*/);
  private final SpeedControllerGroup rightGroup = new SpeedControllerGroup(frontRightController
      /*, backRightController*/);
  private final DifferentialDrive m_robotDrive = new DifferentialDrive(leftGroup, rightGroup);
  private final Joystick m_stick = new Joystick(0);
  private final Timer m_timer = new Timer();

  private final LatchedBoolean pneumatic1LB = new LatchedBoolean();
  private final LatchedBoolean pneumatic2LB = new LatchedBoolean();
  
  private final LatchedBoolean rollersOn = new LatchedBoolean();
  private boolean rollersPowered = false;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    rollers.setNeutralMode(NeutralMode.Brake);
  }

  /**
   * This function is run once each time the robot enters autonomous mode.
   */
  @Override
  public void autonomousInit() {
    m_timer.reset();
    m_timer.start();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    // Drive for 2 seconds
    if (m_timer.get() < 2.0) {
      m_robotDrive.arcadeDrive(0.5, 0.0); // drive forwards half speed
      //m_robotDrive.stopMotor(); // stop robot
    } else {
      m_robotDrive.stopMotor(); // stop robot
    }
  }

  /**
   * This function is called once each time the robot enters teleoperated mode.
   */
  @Override
  public void teleopInit() {
  }

  /**
   * This function is called periodically during teleoperated mode.
   */
  @Override
  public void teleopPeriodic() {
    m_robotDrive.arcadeDrive(m_stick.getY(), -m_stick.getX());
    if(pneumatic1LB.update(m_stick.getRawButton(4))){pneumatic1.toggleState();}
    if(pneumatic2LB.update(m_stick.getRawButton(5))){pneumatic2.toggleState();}
    if(rollersOn.update(m_stick.getRawButton(8))){rollersPowered=!rollersPowered;}
    if(rollersPowered){
      //DriverStation.reportWarning(""+m_stick.getRawAxis(2), false);
      rollers.set(ControlMode.PercentOutput, -m_stick.getRawAxis(2));
    }else {
      rollers.set(ControlMode.PercentOutput, 0);
    }
    if(hi){
      hi=false;
      DriverStation.reportWarning("I ran at least one", false);
    }
  }
  private boolean hi = true;
  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
