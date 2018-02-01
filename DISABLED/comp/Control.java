package org.firstinspires.ftc.teamcode.com.comp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@Disabled
@TeleOp(name="Controled mode", group="Linear Opmode")

public class Control extends LinearOpMode {

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
    
    private DcMotor relicArm = null;
    private DcMotor relicExtender = null;
    private Servo relicServo = null;
    
    private int LIFTERMAX           = 6400;
    private int LIFTERMIN           = 0;
    private int RELICEXTENDERMAX    = 999999999;
    private int RELICARMMAX         = 999999999;

    private void gliffSetlocation (double location) {
        leftGliffServo.setPosition(1-location);
        rightGliffServo.setPosition(location);
    }
    
    @Override
    public void runOpMode() {
        telemetry.addLine("USAGE:\n\nplayer 1:\nleft stick: directional motion,\n"+
        "right stick: rotation on left / right axis\n"+
        "left trigger: makes the robot go faster\n"+
        "\nPlayer 2:\nY: glyph grabber up\nA: glyph grabber Down\n"+
        "X: Glyph Grabber Close\nB: Glyph Grabber Open\n"+
        "Back button (left of the logitech logo): disable limits on glyph grabber up / down");
        telemetry.update();
        
        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftFrontDrive  = hardwareMap.get(DcMotor.class, "motor_front_left");
        leftBackDrive = hardwareMap.get(DcMotor.class, "motor_back_left");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "motor_front_right");
        rightBackDrive = hardwareMap.get(DcMotor.class, "motor_back_right");
        parallelDrive = hardwareMap.get(DcMotor.class, "motor_sideways");
        lifterMotor = hardwareMap.get(DcMotor.class, "motor_lifter");
        
        relicServo = hardwareMap.get(Servo.class, "relicServo");
        relicArm = hardwareMap.get(DcMotor.class, "relicArm");
        relicExtender = hardwareMap.get(DcMotor.class, "relicExtender");
        relicArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        relicExtender.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        
        //ArmServo = hardwareMap.get(Servo.class, "ArmServo");
        //ArmServo.scaleRange(0.11,1);
        //double armPos = 1;
        
        rightGliffServo = hardwareMap.get(Servo.class, "servo_lifter_bottom_right");
        leftGliffServo = hardwareMap.get(Servo.class, "servo_lifter_bottom_left");
        
        rightGliffServo.scaleRange(0.22, 0.46);
        leftGliffServo.scaleRange(0.43, 0.71);
        //leftGliffServo.setDirection(Servo.Direction.REVERSE);
        
        //rightGliffServo.setDirection(Servo.Direction.REVERSE);
        
        
        
        // i guess our motors are on backwards? idk. 
        // this seems to fix driveing backwards...

        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        
        parallelDrive.setDirection(DcMotor.Direction.REVERSE);
        
        
        
        // zero force means breaks
        
        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        parallelDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        
        lifterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //relicExtender.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //relicArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        
        //ArmServo.setPosition(armPos);
        gliffSetlocation(0);
        
        
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
                
            // Setup a variable for each drive wheel to save power level for telemetry
            double leftPower = gamepad1.left_stick_y + gamepad1.left_stick_x;
            double rightPower = gamepad1.left_stick_y - gamepad1.left_stick_x;
            double sidewaysPower = gamepad1.right_stick_x; 
                

            
            // asign gamepad.leftTrigger to turbo mode
            double powerPercent = (gamepad1.left_trigger);
            powerPercent = Range.clip(powerPercent, 0.5, 1.0);
            //powerPercent = 0.5;
            
            
            leftPower *= powerPercent;
            rightPower *= powerPercent;
            sidewaysPower *= powerPercent;
            
            // Send calculated power to wheels
            leftFrontDrive.setPower(leftPower);
            rightFrontDrive.setPower(rightPower);
            leftBackDrive.setPower(leftPower);
            rightBackDrive.setPower(rightPower);
            parallelDrive.setPower(sidewaysPower);
            telemetry.addData("Motors", "left: %.2f right: %.2f, sideways: %.2f", leftPower, rightPower, sidewaysPower);
            telemetry.addData("Power Level", "%.2f", powerPercent);

            
            // lifter motor setup
            // set liftPos to the position of the motor
            int liftPos = lifterMotor.getCurrentPosition();
            // if the motor is greater, dont move up. only move up if the gamepad's y is pressed.
            if (liftPos < LIFTERMAX && gamepad2.y)
            {
                lifterMotor.setPower(1);
            } 
            // a moves down, only when greater than 0 
            else if (gamepad2.a && liftPos > LIFTERMIN)
            {
                lifterMotor.setPower(-1);
            }
            else
            {
                lifterMotor.setPower(0);
            }
            
            
            if (gamepad2.back)
            {
                LIFTERMAX = 999999;
                LIFTERMIN = -999999;
            }
           
            // glyph grabbing sets        
            if (gamepad2.b)
            {
                gliffSetlocation(1);
            } 
            else if (gamepad2.x)
            {
                gliffSetlocation(0);
            }
            
            
            //BEG COPY PASTE ARM TEST 
            
            
            if(gamepad2.x) {
                relicExtender.setPower(-1);
                telemetry.addLine("relicExtender -1");
                
            } else if (gamepad2.y) {
                relicExtender.setPower(1);
                telemetry.addLine("relicExtender 1");
                
            } else {
                relicExtender.setPower(0);
                
                telemetry.addLine("relicExtender 0");
            }
            
            
            
            if(gamepad2.a) {
                relicArm.setPower(-1);
                telemetry.addLine("relicArm -1");
                
            } else if (gamepad2.b) {
                relicArm.setPower(1);
                
                telemetry.addLine("relicArm 1");
                
            } else {
                relicArm.setPower(0);
                telemetry.addLine("relicArm 0");
            }
            
            
            if (gamepad2.dpad_left){
                
                relicServo.setPosition(1);
                telemetry.addLine("servos 1");
            }
            else if (gamepad2.dpad_right)
            {
                relicServo.setPosition(0);
                telemetry.addLine("servos 0");
            }
            
            
            
            
             
            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());

            telemetry.addData("liftpos", liftPos);
            telemetry.update();
        }
    }
}
