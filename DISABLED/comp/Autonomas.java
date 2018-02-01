package org.firstinspires.ftc.teamcode.DISABLED.comp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

@Disabled
@Autonomous(name="Auto", group="competition")

public class Autonomas extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFrontDrive  = null;
    private DcMotor leftBackDrive   = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive  = null;
    private Servo ArmServo          = null;
    private Servo lifterServo       = null;
    private Servo leftGliffServo    = null;
    private Servo rightGliffServo   = null;
    private DcMotor lifterMotor     = null;
    private DcMotor parallelDrive   = null;
    private DcMotor relicArm        = null;
    private DcMotor relicExtender   = null;
    private ColorSensor sensColor   = null;
    private int LIFTERMAX           = 5781;
    private int RELICEXTENDERMAX    = 999999999;
    private int RELICARMMAX         = 999999999;
    private String team = ""; 
    private String location = "";


    private void gliffSetlocation (double location) {
        leftGliffServo.setPosition(location);
        rightGliffServo.setPosition(1-location);
    }
    private void motorsStop() {
        leftFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
        rightFrontDrive.setPower(0);
        parallelDrive.setPower(0);
        sleep(200);
    }
    
    private void turnLeft(int sleepTime) {
        leftFrontDrive.setPower(.5);
        leftBackDrive.setPower(.5);
        rightBackDrive.setPower(-.5);
        rightFrontDrive.setPower(-.5);
        sleep(sleepTime);
        motorsStop();
        
    }
        private void turnRight(int sleepTime) {
        leftFrontDrive.setPower(-.5);
        leftBackDrive.setPower(-.5);
        rightBackDrive.setPower(.5);
        rightFrontDrive.setPower(.5);
        sleep(sleepTime);
        motorsStop();
        }
    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        double gemPos = 1;
        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftFrontDrive  = hardwareMap.get(DcMotor.class, "motor_front_left");
        leftBackDrive = hardwareMap.get(DcMotor.class, "motor_back_left");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "motor_front_right");
        rightBackDrive = hardwareMap.get(DcMotor.class, "motor_back_right");
        parallelDrive = hardwareMap.get(DcMotor.class, "motor_sideways");
        relicArm = hardwareMap.get(DcMotor.class, "motor_relic_arm");
        relicExtender = hardwareMap.get(DcMotor.class, "motor_relic_extender");
        sensColor = hardwareMap.get(ColorSensor.class, "sensor_gem");
        
        lifterMotor = hardwareMap.get(DcMotor.class, "motor_lifter");
        
        
        ArmServo = hardwareMap.get(Servo.class, "ArmServo");
        
        rightGliffServo = hardwareMap.get(Servo.class, "servo_lifter_bottom_right");
        leftGliffServo = hardwareMap.get(Servo.class, "servo_lifter_bottom_left");
        
        // i guess our motors are on backwards? idk. 
        // this seems to fix driveing backwards...

        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        
        relicArm.setDirection(DcMotor.Direction.REVERSE);
        
        
        
        
        // zero force means breaks
        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lifterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        parallelDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        relicExtender.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        relicArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        rightGliffServo.scaleRange(0.22, 0.46);
        leftGliffServo.scaleRange(0.43, 0.71);
        

        telemetry.addLine("press B for red, X for blue on controller 1");
        telemetry.update();
        while (team == "")
        {
          if (gamepad1.b)
          {
            team = "red";
          }
          else if (gamepad1.x)
          {
            team = "blue";
          }
        }
        telemetry.addData("press Y for CLOSE TO THE RELIC SCORING MAT, press A for FAR FROM THE RELIC SCORING MAT\n\n team was ", team);
        telemetry.update();
        while (location == "")
        {
          if (gamepad1.y)
          {
            location = "close";
          }
          else if (gamepad1.a)
          {
            location = "far";
          }
        }
        telemetry.addData("Team", team);
        telemetry.addData("location relative to the relic mat", location);
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        
        
        gliffSetlocation(1);
        ArmServo.setPosition(0.9);
        
        if (lifterMotor.getCurrentPosition() != 0)
        {
          lifterMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
          lifterMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
      lifterMotor.setPower(1);
      sleep(500);
      lifterMotor.setPower(0);
        sleep(1500);
        telemetry.addData("red:", "%d",sensColor.red() );
        telemetry.addData("blue:", "%d",sensColor.blue() );
        telemetry.update();
       if (team == "red")
        {
          if (sensColor.red() > sensColor.blue())
          {
          turnRight(200);
          ArmServo.setPosition(0);
          sleep(500);
          turnLeft(200);
          }// red is infront of us, we are on team 
          else 
          {
            turnLeft(200);
            ArmServo.setPosition(0);
                      sleep(500);
            turnRight(200);
          }
      }
      else
      {
        if (sensColor.red() > sensColor.blue())
        {
          turnLeft(200);
          ArmServo.setPosition(0);
                    sleep(500);
          turnRight(200);
        }
        else
        {
          turnRight(200);
          ArmServo.setPosition(0);
                    sleep(500);
          turnLeft(200);
        }
      }
      ArmServo.setPosition(0);
      sleep(2500);
      
      if (team == "blue")
      {
       
      leftFrontDrive.setPower(.5);
      leftBackDrive.setPower(.5);
      rightBackDrive.setPower(.5);
      rightFrontDrive.setPower(.5);
      sleep(850);
      
      motorsStop();
      turnRight(1600);
      } 
      else
      {
      leftFrontDrive.setPower(-.5);
      leftBackDrive.setPower(-.5);
      rightBackDrive.setPower(-.5);
      rightFrontDrive.setPower(-.5);
      sleep(1250);
      motorsStop();
      }

      if (location == "far")
      {
        if (team == "blue")
        {
          parallelDrive.setPower(-1);
        } 
        else
        {
          parallelDrive.setPower(1);
        }
        sleep(1000);
        motorsStop();
                leftFrontDrive.setPower(-.5);
        leftBackDrive.setPower(-.5);
        rightBackDrive.setPower(-.5);
        rightFrontDrive.setPower(-.5);
        sleep(200);
        motorsStop();
      } 
      else
      {
        if (team == "blue")
        {
          turnRight(800);
        }
        else
        {
          turnLeft(800);
        }
        leftFrontDrive.setPower(-.5);
        leftBackDrive.setPower(-.5);
        rightBackDrive.setPower(-.5);
        rightFrontDrive.setPower(-.5);
        sleep(700);
        motorsStop();
      }  
    }
}
