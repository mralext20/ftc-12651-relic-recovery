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
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ColorSensor;

@Autonomous(name = "GEM ONLY auto", group = "competition")

public class Auto extends LinearOpMode {

 // Declare OpMode members.
 private DcMotor leftWheel = null;
 private DcMotor rightWheel = null;
 private DcMotor centerWheel = null;

 
 private static final double speed = .3;
 private static final int gemDist = 400;
 private static final int closeToCrypt = 4000;
 private static final int cornerToCrypt = 500;
 private static final int nintydeg = 1100;
 private static final int intoCrypt = 400;
 

 private DcMotor relicArm = null;
 private Servo relicServo = null;

 private DcMotor lifterMotor = null;
 private int LIFTERMIN = 0;
 private Servo leftGliffServo = null;
 private Servo rightGliffServo = null;

 private DcMotor leftCompliant = null;
 private DcMotor rightCompliant = null;

 private Servo gemServo = null;
 
 private int lifterBottom = 0;
 private int lifterMiddle = 0;
 private int lifterTop = 0;
 
 private String team = "";
 private String location = "";
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

 private void gliffSetlocation(double location) {
  leftGliffServo.setPosition(location);
  rightGliffServo.setPosition(location);
 }

 @Override
 public void runOpMode() {
        leftWheel = hardwareMap.get(DcMotor.class, "leftwheel");
        rightWheel = hardwareMap.get(DcMotor.class, "rightwheel");
        centerWheel = hardwareMap.get(DcMotor.class, "centerwheel");
        rightCompliant = hardwareMap.get(DcMotor.class, "rightcompliant");
        leftCompliant = hardwareMap.get(DcMotor.class, "leftcompliant");
        
        leftWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        rightWheel.setDirection(DcMotor.Direction.REVERSE);
        centerWheel.setDirection(DcMotor.Direction.REVERSE);
        
        leftWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        centerWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        leftCompliant.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightCompliant.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);  
        gemServo = hardwareMap.get(Servo.class, "gemservo");
        colorsens = hardwareMap.get(ColorSensor.class, "revsens");

  telemetry.addLine("press B for red, X for blue on controller 1");
  telemetry.update();
  while (team == "") {
   if (gamepad1.b) {
    team = "red";
   } else if (gamepad1.x) {
    team = "blue";
   }
   else if (gamepad1.y) {
    team = "yellow";
   }
  }
  sleep(300);
  telemetry.addData("press Y for CLOSE TO THE RELIC SCORING MAT," +
   "press A for FAR FROM THE RELIC SCORING MAT\n\n team was ", team);
  telemetry.update();
  while (location == "") {
   if (gamepad1.y) {
    location = "close";
   } else if (gamepad1.a) {
    location = "far";
   }
  }
  telemetry.addData("Team", team);
  telemetry.addData("location relative to the relic mat", location);
  telemetry.update();
  // Wait for the game to start
  waitForStart();
  if (team == "yellow")
  {
   print("red > blue ");
    drive(-gemDist);
    gemServo.setPosition(.9);
    sleep(500);
    drive(gemDist);
    print("end of gems");
    return;
  }
  gemServo.setPosition(0.14);
  sleep(900);
  
  if (team == "red")
  {
   if (colorsens.red() > colorsens.blue())
   {
    print("red > blue ");
    drive(-gemDist);
    gemServo.setPosition(.9);
    sleep(500);
    drive(gemDist);
    print("end of gems");
   }
   else if (team == "blue")
   {
    drive(gemDist);
    gemServo.setPosition(.9);
    sleep(500);
    drive(-gemDist);
   }
  } // if team == red
  else
   if (colorsens.blue() > colorsens.red())
   {
    drive(gemDist);
    gemServo.setPosition(.9);
    sleep(500);
    drive(-gemDist);
   }
   else
   {
    drive(-gemDist);
    gemServo.setPosition(.9);
    sleep(500);
    drive(gemDist);
   } // else (team == blue)
   if (team == "red")
   {
    if (location == "close")
    {
     drive(closeToCrypt);
     turn(nintydeg);
    }
    else 
    {
     drive(closeToCrypt);
     turn(nintydeg);
     drive(cornerToCrypt);
     turn (-nintydeg);
    }
   }
   else
   {
    if (location == "close")
    {
     drive(-closeToCrypt);
     turn(-nintydeg);
    }
    else {
     drive(-closeToCrypt);
     turn(-nintydeg);
     drive(cornerToCrypt);
     turn(nintydeg);
    }
   } // if team == red for board to crypt
   
   drive(-intoCrypt);
   
  } // end of program
  
  
 }// end of class