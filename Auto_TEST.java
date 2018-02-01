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

@Autonomous(name = "USE ME Auto_TEST", group = "competition")

public class Auto_TEST extends LinearOpMode {

 // Declare OpMode members.
 private DcMotor leftWheel = null;
 private DcMotor rightWheel = null;
 private DcMotor centerWheel = null;


 private DcMotor relicExtender = null;
 private DcMotor relicArm = null;
 private Servo relicServo = null;

 private DcMotor lifterMotor = null;
 private int LIFTERMIN = 0;
 private Servo leftGliffServo = null;
 private Servo rightGliffServo = null;

 private DcMotor leftCompliant = null;
 private DcMotor rightCompliant = null;

 private Servo gemServo = null;

 private int LIFTERMAX = 4000;
 private int RELICEXTENDERMAX = 999999999;
 private int RELICARMMAX = 999999999;
 private String team = "";
 private String location = "";
 private ColorSensor colorsens = null;
 
 public void print(String Line) {
  telemetry.addLine(Line);
  telemetry.update();
 }

 public void drive(int time) {
  if (time > 0) {
   leftWheel.setPower(.5);
   rightWheel.setPower(.5);
  } else if (time < 0) {
   leftWheel.setPower(-.5);
   rightWheel.setPower(-.5);
   time = time * -1;
  }
  sleep(time);
  leftWheel.setPower(0);
  rightWheel.setPower(0);
  sleep(200);
 }

 public void turn(int time) {
  if (time > 0) {
   leftWheel.setPower(.5);
   rightWheel.setPower(-.5);
  } else if (time < 0) {
   leftWheel.setPower(-.5);
   rightWheel.setPower(.5);
   time = time * -1;
  }
  sleep(time);
  leftWheel.setPower(0);
  rightWheel.setPower(0);
  sleep(200);
 }

 private void gliffSetlocation(double location) {
  leftGliffServo.setPosition(location);
  rightGliffServo.setPosition(location);
 }

 @Override
 public void runOpMode() {


  leftWheel = hardwareMap.get(DcMotor.class, "leftwheel");
  rightWheel = hardwareMap.get(DcMotor.class, "rightwheel");
  centerWheel = hardwareMap.get(DcMotor.class, "CenterWheel");
  rightCompliant = hardwareMap.get(DcMotor.class, "rightcompliant");
  leftCompliant = hardwareMap.get(DcMotor.class, "leftcompliant");

  leftWheel.setDirection(DcMotor.Direction.REVERSE);


  leftWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
  rightWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
  centerWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

  leftCompliant.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
  rightCompliant.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

  lifterMotor = hardwareMap.get(DcMotor.class, "liftermotor");
  lifterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
  
  lifterMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
  lifterMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
  
  leftGliffServo = hardwareMap.get(Servo.class, "leftServo");
  rightGliffServo = hardwareMap.get(Servo.class, "rightServo");

  rightGliffServo.scaleRange(0.43, 0.63);
  leftGliffServo.scaleRange(0.33, 0.45);

  leftGliffServo.setDirection(Servo.Direction.REVERSE);

  gemServo = hardwareMap.get(Servo.class, "gemServo");
  gemServo.scaleRange(0.141, .8);


  relicArm = hardwareMap.get(DcMotor.class, "relicArm");
  relicExtender = hardwareMap.get(DcMotor.class, "relicExtender");
  relicServo = hardwareMap.get(Servo.class, "relicServo");

  relicArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);


  relicArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
  relicExtender.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

  colorsens = hardwareMap.get(ColorSensor.class, "revsens");


  telemetry.addLine("press B for red, X for blue on controller 1");
  telemetry.update();
  while (team == "") {
   if (gamepad1.b) {
    team = "red";
   } else if (gamepad1.x) {
    team = "blue";
   }
  }
  telemetry.addData("Press Y for CLOSE TO THE RELIC SCORING MAT," +
   "Press A for FAR FROM THE RELIC SCORING MAT\n\n team was ", team);
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

  print("STARTING!");
  gemServo.setPosition(0);
  gliffSetlocation(1);
  sleep(700);
  lifterMotor.setPower(1);
  sleep(700);
  lifterMotor.setPower(0);



  sleep(1000);
  telemetry.addData("colorsens: red: %d", colorsens.red());
  telemetry.addData("blue:",colorsens.blue());
  telemetry.update();
  if (team == "red") {
   if (colorsens.red() > colorsens.blue()) {
    turn(300);
    gemServo.setPosition(1);
    sleep(20);
    turn(-300);
   } else {
    turn(-300);
    gemServo.setPosition(1);
    sleep(20);
    turn(300);
   }
  } else {
   // we are on blue team
   if (colorsens.blue() > colorsens.red()) {
    turn(300);
    gemServo.setPosition(1);
    sleep(20);
    turn(-300);
   } else {
    turn(-300);
    gemServo.setPosition(1);
    sleep(20);
    turn(300);
   }
  }

  //get ready to get off the balance board & face box
  if (team == "red") {
   drive(-1100);
   if (location == "far") {
    centerWheel.setPower(.25);
    sleep(500);
  } else 
    turn(-675);
  } else {
   // we are on blue
   drive(1100);
   if (location == "far"){
    centerWheel.setPower(-.25);
    sleep(500);
    } else {
     turn(-625);
    }
   }
  
  // AFTER THIS; we are now in the safe zone
  drive(600);
  // attempt to shove the block into the crypt
  if (team == "red")
  {
   centerWheel.setPower(-1); 
  }
  else{
   centerWheel.setPower(1);
  }
  sleep(200);
  drive(150);
  
  gliffSetlocation(0);
  sleep(600);
  centerWheel.setPower(0);
  drive(1000);
  drive(-1000);

  //turn around to the feild
  turn(635 * 2);
  drive(-1500);
  
  lifterMotor.setPower(-.5);
  sleep(500);
  lifterMotor.setPower(0);
  
 }
}