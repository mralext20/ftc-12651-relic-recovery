package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

/* programmers notes: 
 cur glyph mode is a fake enum between 0 and 4. 
  mode == 0 means that the arms are both open, and at the lowest position.
  mode == 1 means that the top set of arms are closed, and that the top set has moved up to the high position.
  mode == 2 means that the bottom set of arms have shut, and the entire arm set is moved up a small amount.
  mode == 3 means that the arm set moves as high as it can.
  mode == 4 means that both arms are opened, and as high as posible. 
  */


@TeleOp(name="Controlled Mode", group="Linear Opmode")

public class RemoteControl extends LinearOpMode {
    
    public static final int RAMPSIZE = 15;
    
    public static final double GLYPHBOTTOMLEFTOPEN = 0.254;
    public static final double GLYPHBOTTOMLEFTCLOSED = 0.385;
    public static final double GLYPHBOTTOMRIGHTOPEN = 0.64; 
    public static final double GLYPHBOTTOMRIGHTCLOSED = 0.42;
    public static final double GLYPHTOPLEFTOPEN = 0.65;
    public static final double GLYPHTOPLEFTCLOSED = 0.46;
    public static final double GLYPHTOPRIGHTOPEN = 0.14;
    public static final double GLYPHTOPRIGHTCLOSED = 0.37;
    
    
    private DcMotor LeftWheel = null;
    private DcMotor RightWheel = null;
    private DcMotor CenterWheel = null;
    
    private Servo glyphTopLeft = null;
    private Servo glyphTopRight = null;
    private Servo glyphBottomLeft = null;
    private Servo glyphBottomRight = null;
    private CRServo glyphLifterServo = null;
    
    private DcMotor lifterMotor = null;
    
    private DcMotor LeftCompliant = null;
    private DcMotor RightCompliant = null;
    private Servo gemServo = null;

    private double[] recentLeftPowers = new double[RAMPSIZE];
    private double[] recentRightPowers = new double[RAMPSIZE];
    private double[] recentCenterPowers = new double[RAMPSIZE];
    
    private int curRecentIteration = 0;
    
    private int curGlyphPos = 0;
    public enum glyphSystemStates {
        Manual, Smart
    };
    
    private glyphSystemStates glyphSystemState = glyphSystemStates.Manual;
    
    private boolean prevgp2back = false;
    private boolean prevgp2y = false;
    private boolean prevgp2a = false;
    
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
        
        
        glyphBottomLeft = hardwareMap.get(Servo.class, "glyphBottomLeft");
        glyphBottomRight = hardwareMap.get(Servo.class, "glyphBottomRight");
        
        glyphTopLeft = hardwareMap.get(Servo.class, "glyphTopLeft");
        glyphTopRight = hardwareMap.get(Servo.class, "glyphTopRight");
        
        glyphLifterServo = hardwareMap.get(CRServo.class, "belt drive");
        
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
            
            
            if (gamepad2.back) {
                lifterMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                glyphSystemState = glyphSystemStates.Manual;
            }
            
            
        if (glyphSystemState == glyphSystemStates.Smart) {
            // beg smart glyph stuff
            
            // check if y is newly pressed
            if (gamepad2.y && !prevgp2y) {
                curGlyphPos +=1;
            }
            // check if a is newly pressed
            if (gamepad2.a && !prevgp2a) {
                curGlyphPos -=1;
            }
            
            curGlyphPos = Range.clip(curGlyphPos, 0,4);
            telemetry.addData("curGlyphPos", curGlyphPos);
            telemetry.addLine("automatic control of glyph system");
            
            if (curGlyphPos == 0) {
                // open, at the bottom
                lifterMotor.setTargetPosition(0);
                glyphBottomLeft.setPosition(GLYPHBOTTOMLEFTOPEN);
                glyphBottomRight.setPosition(GLYPHBOTTOMRIGHTOPEN);
                glyphTopLeft.setPosition(GLYPHTOPLEFTOPEN);
                glyphTopRight.setPosition(GLYPHTOPRIGHTOPEN);
            } else if (curGlyphPos == 1) {
                // top closed, and lifted to the top
                glyphBottomLeft.setPosition(GLYPHBOTTOMLEFTOPEN);
                glyphBottomRight.setPosition(GLYPHBOTTOMRIGHTOPEN);
                glyphTopLeft.setPosition(GLYPHTOPLEFTCLOSED);
                
                
                
                
                glyphTopRight.setPosition(GLYPHTOPRIGHTCLOSED);
            } else if (curGlyphPos == 2) {
                // bottom closed, lifted off the floor
                glyphBottomLeft.setPosition(GLYPHBOTTOMLEFTCLOSED);
                glyphBottomRight.setPosition(GLYPHBOTTOMRIGHTCLOSED);
                glyphTopLeft.setPosition(GLYPHTOPLEFTCLOSED);
                glyphTopRight.setPosition(GLYPHTOPRIGHTCLOSED);
            } else if (curGlyphPos == 3) {
                // all closed, lifdted up high
                glyphBottomLeft.setPosition(GLYPHBOTTOMLEFTCLOSED);
                glyphBottomRight.setPosition(GLYPHBOTTOMRIGHTCLOSED);
                glyphTopLeft.setPosition(GLYPHTOPLEFTCLOSED);
                glyphTopRight.setPosition(GLYPHTOPRIGHTCLOSED);
            } else if (curGlyphPos == 4) {
                // open, lifted up high.
                glyphBottomLeft.setPosition(GLYPHBOTTOMLEFTOPEN);
                glyphBottomRight.setPosition(GLYPHBOTTOMRIGHTOPEN);
                glyphTopLeft.setPosition(GLYPHTOPLEFTOPEN);
                glyphTopRight.setPosition(GLYPHTOPRIGHTOPEN);
            }
        } // smart mode end, beg manual mode
            else if (glyphSystemState == glyphSystemStates.Manual) {
                telemetry.addLine("MANUAL CONTROL OF GLYPH SYSTEM");
                if (gamepad2.left_bumper) {
                    glyphTopLeft.setPosition(GLYPHTOPLEFTCLOSED);
                    glyphTopRight.setPosition(GLYPHTOPRIGHTCLOSED);
                } else if (gamepad2.right_bumper) {
                    glyphTopLeft.setPosition(GLYPHTOPLEFTOPEN);
                    glyphTopRight.setPosition(GLYPHTOPRIGHTOPEN);
                }
                
                if (gamepad2.left_trigger < .8) {
                    glyphBottomLeft.setPosition(GLYPHBOTTOMLEFTCLOSED);
                    glyphBottomRight.setPosition(GLYPHBOTTOMRIGHTCLOSED);
                } else if (gamepad2.right_trigger < .8) {
                    glyphBottomLeft.setPosition(GLYPHBOTTOMLEFTOPEN);
                    glyphBottomRight.setPosition(GLYPHBOTTOMRIGHTOPEN);
                }
                lifterMotor.setPower(gamepad2.right_stick_x);
                glyphLifterServo.setPower(gamepad2.left_stick_x);
        } // end manual mode for glyph system
            
            telemetry.update();
            prevgp2a = gamepad2.a;
            prevgp2back= gamepad2.back;
            prevgp2y = gamepad2.back;
        } // while( opModeIsActive())
    } // public void runOpMode() {
} // public class RemoteControl extends LinearOpMode {
