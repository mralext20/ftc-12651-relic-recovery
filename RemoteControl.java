package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;


@TeleOp(name="Controlled Mode", group="Linear Opmode")

public class RemoteControl extends LinearOpMode {
    
    public static final int RAMPSIZE = 15;
    
    private DcMotor LeftWheel = null;
    private DcMotor RightWheel = null;
    private DcMotor CenterWheel = null;
    
    
    private DcMotor lifterMotor = null;
    
    private DcMotor LeftCompliant = null;
    private DcMotor RightCompliant = null;
    private Servo gemServo = null;

    private double[] recentLeftPowers = new double[RAMPSIZE];
    private double[] recentRightPowers = new double[RAMPSIZE];
    private double[] recentCenterPowers = new double[RAMPSIZE];
    
    private int curRecentIteration = 0;
    
    private int curGlyphPos = 0;
    
    private Gamepad prevGamepad1;
    private Gamepad prevGamepad2;
    
    public boolean pressed(boolean previous, boolean now) {
        if (now == true && previous == false) {
            return true;
        }
        else {
            return false;
        }
    }
    
    
    @Override
    public void runOpMode() {
        
        telemetry.addLine("USAGE:\n\nremote1:\n");
        telemetry.update();
        LeftWheel = hardwareMap.get(DcMotor.class, "leftwheel");
        RightWheel = hardwareMap.get(DcMotor.class, "rightwheel");
        CenterWheel = hardwareMap.get(DcMotor.class, "centerwheel");
        RightCompliant = hardwareMap.get(DcMotor.class, "rightcompliant");
        LeftCompliant = hardwareMap.get(DcMotor.class, "leftcompliant");
        
        RightWheel.setDirection(DcMotor.Direction.REVERSE);
        CenterWheel.setDirection(DcMotor.Direction.REVERSE);
        
        
        LeftWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RightWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        CenterWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        LeftCompliant.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RightCompliant.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        
        lifterMotor = hardwareMap.get(DcMotor.class, "liftermotor");
        lifterMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lifterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        
        waitForStart();
        //gemServo.setPosition(1);
        prevGamepad1 = gamepad1;
        prevGamepad2 = gamepad2;
        while( opModeIsActive())
        {
            //get motor powers
            double leftInputPower = gamepad1.left_stick_y - gamepad1.right_stick_x;
            double rightInputPower = gamepad1.left_stick_y + gamepad1.right_stick_x;
            double centerInputPower;
            if (gamepad1.left_bumper)
            {
                centerInputPower = 1;
            }
            else if (gamepad1.right_bumper)
            {
                centerInputPower = -1;
            }
            else 
            {
                centerInputPower = 0;
            }
            
            
            double powerPercent = (gamepad1.left_trigger);
            powerPercent = Range.clip(powerPercent, .5, 1);
            
            leftInputPower *= powerPercent;
            rightInputPower *= powerPercent;
            //centerPower *= powerPercent;
            if (curRecentIteration > RAMPSIZE-1)
            {
                curRecentIteration =0;
            }
            // array lists of recent powers
            recentLeftPowers[curRecentIteration] = leftInputPower;
            recentRightPowers[curRecentIteration] = rightInputPower;
            recentCenterPowers[curRecentIteration] = centerInputPower;
            curRecentIteration++;
            double runningLeft = 0;
            double runningCenter = 0;
            double runningRight = 0;
            for (int i = 0; i < RAMPSIZE ; i++)
            {
                runningLeft  += recentLeftPowers[i];
                runningRight += recentRightPowers[i];
                runningCenter+= recentCenterPowers[i];
            }
            double leftPower= runningLeft/RAMPSIZE;
            double rightPower=runningRight/RAMPSIZE;
            double centerPower=runningCenter/RAMPSIZE;
            
            if (Math.abs(leftPower) < Math.abs(leftInputPower))
            {
                leftPower = leftInputPower;
            }
            
            if (Math.abs(rightPower) < Math.abs(rightInputPower))
            {
                rightPower = rightInputPower;
            }
            
            if (Math.abs(centerPower) < Math.abs(centerInputPower))
            {
                centerPower = centerInputPower;
            }
            LeftWheel.setPower(leftPower);
            RightWheel.setPower(rightPower);
            CenterWheel.setPower(centerPower);
            
            telemetry.addData("curRecentiteration", curRecentIteration);
            telemetry.addData("leftPower", "%.2f", leftPower);
            telemetry.addData("RightPower", "%.2f", rightPower);
            telemetry.addData("CenterPower", "%.2f", centerPower);
            telemetry.addData("left Endocer", LeftWheel.getCurrentPosition());
            telemetry.addData("right Endocer", RightWheel.getCurrentPosition());
            telemetry.addData("center Endocer", CenterWheel.getCurrentPosition());
            
            
            
            //compliant spitters out 
            if (gamepad2.right_trigger > .5)
            {
                LeftCompliant.setPower(1);
                RightCompliant.setPower(1);
                telemetry.addLine("Compliant Wheels runing OUT");
            } 
            //compliant suckers in.
            else if (gamepad2.left_trigger > .5)
            {
                RightCompliant.setPower(-1);
                LeftCompliant.setPower(-1);
                telemetry.addLine("Compliant Wheels runing IN");
            }
            else
            //COMPLIANT DOES NOTHING
            {
                RightCompliant.setPower(0);
                LeftCompliant.setPower(0);
                telemetry.addLine("Compliant Wheels NOT RUNNING");
            }
            
            
            
            // beg glyph stuff
            
            // check if y is newly pressed
            if (pressed(gamepad2.y, prevGamepad2.y)) {
                curGlyphPos +=1;
            }
            // check if a is newly pressed
            if (pressed(gamepad2.a, prevGamepad2.a)) {
                curGlyphPos -=1;
            }
            
            curGlyphPos = Range.clip(curGlyphPos, 0,4);
            telemetry.addData("curGlyphPos", curGlyphPos);
            
            if (curGlyphPos == 0) {
                lifterMotor.setTargetPosition(0);
                
                
            }
            
            
            
            telemetry.update();
            prevGamepad1 = gamepad1;
            prevGamepad2 = gamepad2;
        } // while( opModeIsActive())
    } // public void runOpMode() {
} // public class RemoteControl extends LinearOpMode {