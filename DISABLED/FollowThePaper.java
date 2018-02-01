

package org.firstinspires.ftc.teamcode.comp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

@Disabled
@Autonomous(name="Follow the paper vumark", group="Linear Opmode")

public class FollowThePaper extends LinearOpMode {

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
        
      waitForStart();
      
      while (opModeIsActive())
      {
          
      }
    }
}
