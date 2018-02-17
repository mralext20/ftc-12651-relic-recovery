/* 
 *  turn(625) gives you a turn to the leftby 90 degrees. 
 *     this method can be given negative numbers in order to turn right.
 *
 *   drive()
 *
 *
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ColorSensor;

@Autonomous(name = "auto", group = "competition")

public class Auto extends LinearOpMode {

 // Declare OpMode members.
 private DcMotor leftWheel = null;
 private DcMotor rightWheel = null;
 private DcMotor centerWheel = null;

 
 public static final double GLYPHTOPLEFTOPEN = 0.65;
 public static final double GLYPHTOPLEFTCLOSED = 0.46;
 public static final double GLYPHTOPRIGHTOPEN = 0.14;
 public static final double GLYPHTOPRIGHTCLOSED = 0.37;
 
 private static final double speed = .3;
 private static final int gemDist = 400;
 private static final int closeToCrypt = 3100;
 private static final int cornerToCrypt = -1000;
 private static final int cornerForeward = 2000;
 private static final int nintydeg = 1100;
 private static final int intoCrypt = -800;
 

 private DcMotor relicArm = null;
 private Servo relicServo = null;

 private DcMotor lifterMotor = null;
 private int LIFTERMIN = 0;
 private Servo leftGliffServo = null;
 private Servo rightGliffServo = null;

 private DcMotor leftCompliant = null;
 private DcMotor rightCompliant = null;

 private Servo gemServo = null;
 
 private Servo GlyphTopLeft = null;
 private Servo GlyphTopRight = null;
 private CRServo GlyphLifter = null;
 
 public enum Locations {
  Close, Far
 };
 private Locations location;
 
 public enum Teams {
  Red, Blue, Testing
 };
 private Teams team;
 
 
 private ColorSensor colorsens = null;
 
 public void print(String Line) {
  telemetry.addLine(Line);
  telemetry.update();
 }

 public void drive(int delta) {
  int curLeft = leftWheel.getCurrentPosition();
  int curRight = rightWheel.getCurrentPosition();
   leftWheel.setTargetPosition(curLeft + delta);
   rightWheel.setTargetPosition(curRight + delta);
   leftWheel.setPower(speed);
  rightWheel.setPower(speed);
  while ((leftWheel.isBusy() || rightWheel.isBusy()) && !isStopRequested())
  {
   sleep(10);
  }
  leftWheel.setPower(0);
  rightWheel.setPower(0);
 }

 public void turn(int delta) {
  int curLeft = leftWheel.getCurrentPosition();
  int curRight = rightWheel.getCurrentPosition();
  leftWheel.setTargetPosition(curLeft + delta);
  rightWheel.setTargetPosition(curRight - delta);
  leftWheel.setPower(speed);
  rightWheel.setPower(speed);
    while ((leftWheel.isBusy() || rightWheel.isBusy()) && !isStopRequested())
  {
   sleep(10);
  }
  leftWheel.setPower(0);
  rightWheel.setPower(0);
 }

 @Override
 public void runOpMode() {
        leftWheel = hardwareMap.get(DcMotor.class, "leftwheel");
        rightWheel = hardwareMap.get(DcMotor.class, "rightwheel");
        rightCompliant = hardwareMap.get(DcMotor.class, "rightcompliant");
        leftCompliant = hardwareMap.get(DcMotor.class, "leftcompliant");
        
        GlyphTopLeft = hardwareMap.get(Servo.class, "glyphTopLeft");
        GlyphTopRight = hardwareMap.get(Servo.class, "glyphTopRight");
        GlyphLifter = hardwareMap.get(CRServo.class, "belt drive");
        
        GlyphLifter.setDirection(DcMotor.Direction.REVERSE);
        
        leftWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        rightWheel.setDirection(DcMotor.Direction.REVERSE);
        
        leftWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        leftCompliant.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightCompliant.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);  
        gemServo = hardwareMap.get(Servo.class, "gemservo");
        colorsens = hardwareMap.get(ColorSensor.class, "revsens");

  telemetry.addLine("press B for red, X for blue on controller 1");
  telemetry.update();
  while (team == null || opModeIsActive()) {
   if (gamepad1.b) {
    team = Teams.Red;
   } else if (gamepad1.x) {
    team = Teams.Blue;
   }
   else if (gamepad1.y) {
    team = Teams.Testing;
   }
  }
  sleep(300);
  telemetry.addData("press Y for CLOSE TO THE RELIC SCORING MAT," +
   "press A for FAR FROM THE RELIC SCORING MAT\n\n team was ", team);
  telemetry.update();
  while (location == null || opModeIsActive()) {
   if (gamepad1.y) {
    location = Locations.Close;
   } else if (gamepad1.a) {
    location = Locations.Far;
   }
  }
  telemetry.addData("Team", team);
  telemetry.addData("location relative to the relic mat", location);
  telemetry.update();
  // Wait for the game to start
  waitForStart();
  if (team == Teams.Testing)
  {
   print("red > blue ");
   return;
  }
  
  GlyphTopLeft.setPosition(GLYPHTOPLEFTCLOSED);
  GlyphTopRight.setPosition(GLYPHTOPRIGHTCLOSED);
  
  GlyphLifter.setPower(-1);
  
  gemServo.setPosition(0.04);
  sleep(1500);
  GlyphLifter.setPower(0);
  if (team == Teams.Red)
  {
   if (colorsens.blue() > colorsens.red())
   {
    print("red > blue ");
    drive(-gemDist);
    gemServo.setPosition(.9);
    sleep(500);
    drive(gemDist);
    print("end of gems");
   } else 
   {
    print("blue > red");
    drive(gemDist);
    gemServo.setPosition(.9);
    sleep(500);
    drive(-gemDist);
   }
  }
   else if (team == Teams.Blue)
   {
    if (colorsens.blue() > colorsens.red()){
    drive(gemDist);
    gemServo.setPosition(.9);
    sleep(500);
    drive(-gemDist);
     
    }
    else {
    drive(-gemDist);
    gemServo.setPosition(.9);
    sleep(500);
    drive(gemDist);
    }
   } // team == blue
   
   
   // END OF GEMS
   
   if (team == Teams.Red)
   {
    if (location == Locations.Close)
    {
     drive(closeToCrypt);
     turn(nintydeg);
    }
    else 
    {
     drive(cornerForeward);
     turn(-nintydeg);
     drive(cornerToCrypt);
     turn (-nintydeg);
    }// location == far 
   } // team == blue
   else
   {
    if (location == Locations.Close)
    {
     drive(-closeToCrypt);
     turn(nintydeg);
    }
    else {
     drive(-cornerForeward);
     turn(-nintydeg);
     drive(cornerToCrypt);
     turn(nintydeg);
    }
   } // if team == red for board to crypt
   
   drive(intoCrypt);
   turn(nintydeg/6);
  } // end of program
 }// end of class