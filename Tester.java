package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;



@TeleOp(name="Tester", group="Tester")

public class Tester extends LinearOpMode {


  // set ye motors / servos
  private String hwServo = "gemservo";
  private String hwMotor = "liftermotor";
  private String hwCrServo="belt drive";
  private String hwColorSensor = "revsens";
  
  // Declare OpMode members 
  private Servo servo = null;
  private DcMotor motor = null;
  private CRServo crservo = null;
  private ColorSensor colorsens = null;
  private String mode = ""; 
  double servoPos = .5;
  double motorPower = 0;

    @Override
    public void runOpMode() {
        telemetry.addData("servo", hwServo);
        telemetry.addData("motor", hwMotor);
        telemetry.addData("CrServo", hwCrServo);
        
        telemetry.addLine("what mode?\npress X for servo\npress B for motor\npress y for CrServo");
        telemetry.update();
        
        do {
            if (gamepad1.x)
            {
                mode = "servo";
            } 
            else if (gamepad1.b)
            {
                mode = "motor";
            }
            else if (gamepad1.y)
            {
                mode = "crservo";
            }
        } while (mode == "");
        

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        //Wait for the game to start (driver presses PLAY)
       if (mode == "servo")
       {
           servo = hardwareMap.get(Servo.class, hwServo);
           
           telemetry.addLine(" hit left bumper for regular, right bumper for reverse.");
           telemetry.update();
           boolean adv = false;
           do {
               if (gamepad1.left_bumper) {
                    adv = true;
                } else if (gamepad1.right_bumper) {
                    servo.setDirection(Servo.Direction.REVERSE);
                    adv = true;
                }
            } while (adv == false);
       }
       else if (mode == "motor")
       {
           motor = hardwareMap.get(DcMotor.class, hwMotor);

            telemetry.addLine("brakeing or floating?\nleft bumber for breaking\nright bumber for float");
            telemetry.update();
            boolean adv = false;
            do {
                if (gamepad1.left_bumper) {
                    motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    adv = true;
                } else if (gamepad1.right_bumper) {
                    motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                    adv = true;
                }
            } while (adv == false);
        }
        else if (mode == "crservo")
        {
            crservo = hardwareMap.get(CRServo.class, hwCrServo);
        }
    
  
        telemetry.addData("ready to go! mode == ", mode);
        telemetry.update();
        waitForStart();
       
        if (mode == "servo")
        {
        while (opModeIsActive()) {
                if (gamepad1.a)
                {
                    servoPos += .001;
                }
                else if (gamepad1.b)
                {
                    servoPos -= .001;
                }
                if (gamepad1.y)
                {
                    servoPos += .01;
                }
                else if (gamepad1.x)
                {
                    servoPos -= .01;
                }
                servoPos = Range.clip(servoPos, 0,1);
                servo.setPosition(servoPos);
                
                telemetry.addData("servoPos", servoPos);
                telemetry.update();
            }
        }
        else if (mode == "gemservo")
        {
        while (opModeIsActive()) {
                if (gamepad1.a)
                {
                    servoPos += .001;
                }
                else if (gamepad1.b)
                {
                    servoPos -= .001;
                }
                if (gamepad1.y)
                {
                    servoPos += .01;
                }
                else if (gamepad1.x)
                {
                    servoPos -= .01;
                }
                servoPos = Range.clip(servoPos, 0,1);
                servo.setPosition(servoPos);
                telemetry.addData("red", colorsens.red());
                telemetry.addData("blue", colorsens.blue());
                telemetry.addData("green", colorsens.green());
                telemetry.addData("alpha", colorsens.alpha());
                telemetry.addData("servoPos", servoPos);
                telemetry.update();
            }
        }
        else if (mode == "motor")
        {
            while (opModeIsActive()) {
                motorPower = gamepad1.left_trigger + (-1 * gamepad1.right_trigger);
                motor.setPower(motorPower);
                
                telemetry.addData("motorPower",motorPower);
                telemetry.addData("motor.getcurrentposition", motor.getCurrentPosition());
                telemetry.update();
            }
        }
        else if (mode == "crservo")
        {
            while (opModeIsActive()) {
                motorPower = gamepad1.left_trigger + (-1 * gamepad1.right_trigger);
                crservo.setPower(motorPower);
                
                telemetry.addData("motorPower",motorPower);
                telemetry.update();
            }
        }
    }
}
