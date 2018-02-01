

package org.firstinspires.ftc.teamcode.DISABLED;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and eecxuted.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Disabled
@TeleOp(name="armTest", group="Linear Opmode")

public class ArmTest extends LinearOpMode {

    // Declare OpMode members 
  private DcMotor relicArm = null;
  private DcMotor relicExtender = null;
  private Servo relicServo = null;
    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        relicArm = hardwareMap.get(DcMotor.class, "relicArm");
        relicExtender = hardwareMap.get(DcMotor.class, "relicExtender");
        
        relicArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        relicExtender.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        relicServo = hardwareMap.get(Servo.class, "relicServo");
        
        //relicServo.
        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        //Wait for the game to start (driver presses PLAY)
        
        
        waitForStart();
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            if(gamepad1.x) {
                relicExtender.setPower(-1);
                telemetry.addLine("relicExtender -1");
                
            } else if (gamepad1.y) {
                relicExtender.setPower(1);
                telemetry.addLine("relicExtender 1");
                
            } else {
                relicExtender.setPower(0);
                
                telemetry.addLine("relicExtender 0");
            }
            
            
            
            if(gamepad1.a) {
                relicArm.setPower(-1);
                telemetry.addLine("relicArm -1");
                
            } else if (gamepad1.b) {
                relicArm.setPower(1);
                
                telemetry.addLine("relicArm 1");
                
            } else {
                relicArm.setPower(0);
                telemetry.addLine("relicArm 0");
            }
            
            
            if (gamepad1.dpad_left){
                
                relicServo.setPosition(1);
                telemetry.addLine("servos 1");
            }
            else if (gamepad1.dpad_right)
            {
                relicServo.setPosition(0);
                telemetry.addLine("servos 0");
            }
            
            
            telemetry.update();
        }
    }
}
