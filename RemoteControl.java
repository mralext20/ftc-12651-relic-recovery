package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;
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
    
    public static final int RAMPSIZE = 1;
    
    public static final double GLYPHBOTTOMLEFTOPEN = 0.154;
    public static final double GLYPHBOTTOMLEFTCLOSED = 0.385;
    public static final double GLYPHBOTTOMRIGHTOPEN = 0.84; 
    public static final double GLYPHBOTTOMRIGHTCLOSED = 0.42;
    public static final double GLYPHTOPLEFTOPEN = 0.65;
    public static final double GLYPHTOPLEFTCLOSED = 0.46;
    public static final double GLYPHTOPRIGHTOPEN = 0.14;
    public static final double GLYPHTOPRIGHTCLOSED = 0.37;
    public static final int LIFTERMOTORBOTTOM = 0;
    public static final int LIFTERMOTORMIDDLE = 600;
    public static final int LIFTERMOTORTOP = 6100;
    public static final double RELICHOLDEROPEN = 0.324;
    public static final double RELICHOLDERCLOSED = 0.061;
    
    
    private DcMotor LeftWheel = null;
    private DcMotor RightWheel = null;
    
    private Servo glyphTopLeft = null;
    private Servo glyphTopRight = null;
    private Servo glyphBottomLeft = null;
    private Servo glyphBottomRight = null;
    private CRServo glyphLifterServo = null;
    private DigitalChannel revMagnetSensor = null;
    
    private CRServo relicSpinner = null;
    private Servo relicHolder = null;
    private DcMotor relicExtender = null;
    
    private DcMotor lifterMotor = null;
    
    private DcMotor LeftCompliant = null;
    private DcMotor RightCompliant = null;
    private Servo gemServo = null;

    private double[] recentLeftPowers = new double[RAMPSIZE];
    private double[] recentRightPowers = new double[RAMPSIZE];
    private double[] recentCenterPowers = new double[RAMPSIZE];
    
    private ElapsedTime glyphLifterServoMovingTime = new ElapsedTime();
    private int curRecentIteration = 0;
    
    private int curGlyphPos = 0;
    public enum glyphSystemStates {
        Manual, Smart
    };
    
    public enum gp2States {
        Relic, Glyph
    }
    private gp2States gp2State = gp2States.Glyph;
    
    private glyphSystemStates glyphSystemState = glyphSystemStates.Smart;
    
    public enum glyphCRServoStates {
        Bottom, Top, TransitTop, TransitBottom
    };
    
    private glyphCRServoStates glyphCRServoState = glyphCRServoStates.Bottom;
    // private ElapsedTime glyphLifterServoMovingTime;
    
    private boolean prevgp2back = false;
    private boolean prevgp2y = false;
    private boolean prevgp2a = false;
    private boolean prevgp2stick = false;
    
    
    @Override
    public void runOpMode() {
        
        telemetry.addLine("USAGE:\n\nremote1:\n");
        telemetry.update();
        LeftWheel = hardwareMap.get(DcMotor.class, "leftwheel");
        RightWheel = hardwareMap.get(DcMotor.class, "rightwheel");
        RightCompliant = hardwareMap.get(DcMotor.class, "rightcompliant");
        LeftCompliant = hardwareMap.get(DcMotor.class, "leftcompliant");
        
        RightWheel.setDirection(DcMotor.Direction.REVERSE);
        
        gemServo = hardwareMap.get(Servo.class, "gemservo");
        
        glyphBottomLeft = hardwareMap.get(Servo.class, "glyphBottomLeft");
        glyphBottomRight = hardwareMap.get(Servo.class, "glyphBottomRight");
        
        glyphTopLeft = hardwareMap.get(Servo.class, "glyphTopLeft");
        glyphTopRight = hardwareMap.get(Servo.class, "glyphTopRight");
        
        glyphLifterServo = hardwareMap.get(CRServo.class, "belt drive");
        
        revMagnetSensor = hardwareMap.get(DigitalChannel.class, "revMagnetSensor");
        revMagnetSensor.setMode(DigitalChannel.Mode.INPUT);
        
        LeftWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RightWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        LeftCompliant.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        RightCompliant.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        relicSpinner =hardwareMap.get(CRServo.class, "relic spinner");
        relicHolder = hardwareMap.get(Servo.class, "relic holder");
        relicExtender = hardwareMap.get(DcMotor.class, "relic extender");
        
        relicSpinner.setDirection(DcMotor.Direction.REVERSE);
        
        
        relicExtender.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        
        lifterMotor = hardwareMap.get(DcMotor.class, "liftermotor");
        lifterMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lifterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lifterMotor.setDirection(DcMotor.Direction.REVERSE);
        glyphLifterServo.setDirection(DcMotor.Direction.REVERSE);
        
        waitForStart();
        gemServo.setPosition(.8);
        lifterMotor.setPower(1);
        //gemServo.setPosition(1);
        while( opModeIsActive())
        {
            //get motor powers
            double leftInputPower = gamepad1.left_stick_y - gamepad1.right_stick_x;
            double rightInputPower = gamepad1.left_stick_y + gamepad1.right_stick_x;
            
            
            
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
            curRecentIteration++;
            double runningLeft = 0;
            double runningRight = 0;
            for (int i = 0; i < RAMPSIZE ; i++)
            {
                runningLeft  += recentLeftPowers[i];
                runningRight += recentRightPowers[i];
            }
            double leftPower= runningLeft/RAMPSIZE;
            double rightPower=runningRight/RAMPSIZE;
           
            
            if (Math.abs(leftPower) < Math.abs(leftInputPower))
            {
                leftPower = leftInputPower;
            }
            
            if (Math.abs(rightPower) < Math.abs(rightInputPower))
            {
                rightPower = rightInputPower;
            }
            
            LeftWheel.setPower(leftPower);
            RightWheel.setPower(rightPower);
            
            telemetry.addData("curRecentiteration", curRecentIteration);
            telemetry.addData("leftPower", "%.2f", leftPower);
            telemetry.addData("RightPower", "%.2f", rightPower);
            telemetry.addData("left Endocer", LeftWheel.getCurrentPosition());
            telemetry.addData("right Endocer", RightWheel.getCurrentPosition());
            
            
            

            
            if (gamepad2.back) {
                lifterMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                glyphSystemState = glyphSystemStates.Manual;
            }
            if (gamepad2.back && !prevgp2back && glyphSystemState == glyphSystemStates.Smart) {
                glyphSystemState = glyphSystemStates.Manual;
            } else if (gamepad2.back && !prevgp2back && glyphSystemState == glyphSystemStates.Manual)
            {
                glyphSystemState = glyphSystemStates.Smart;
            }
            if (gamepad2.right_stick_button && !prevgp2stick && gp2State == gp2States.Glyph) {
                gp2State = gp2States.Relic;
            } else if (gamepad2.right_stick_button && !prevgp2stick && gp2State == gp2States.Relic)
            {
                gp2State = gp2States.Glyph;
            }
            prevgp2back= gamepad2.back;
            prevgp2stick = gamepad2.right_stick_button;
            
    if (gp2State == gp2States.Glyph) {
        if (glyphSystemState == glyphSystemStates.Smart) {
            // beg smart glyph stuff
            
            // check if y is newly pressed
            if (gamepad2.y && !prevgp2y) {
                curGlyphPos +=1;
            }
            else if (gamepad2.a && !prevgp2a) {
                curGlyphPos -=1;
            }
            else if (gamepad2.b) {
                curGlyphPos = 0;
            }
            
            prevgp2a = gamepad2.a;
            prevgp2y = gamepad2.y;
            curGlyphPos = Range.clip(curGlyphPos, 0,4);
            
            
            telemetry.addData("curGlyphPos", curGlyphPos);
            telemetry.addLine("automatic control of glyph system");
            
            if (gamepad2.x) {
                glyphBottomLeft.setPosition(GLYPHBOTTOMLEFTOPEN);
                glyphBottomRight.setPosition(GLYPHBOTTOMRIGHTOPEN);
                glyphTopLeft.setPosition(GLYPHTOPLEFTOPEN);
                glyphTopRight.setPosition(GLYPHTOPRIGHTOPEN);
            }
            else if (curGlyphPos == 0) {
                // open, at the bottom
                lifterMotor.setTargetPosition(LIFTERMOTORBOTTOM);
                glyphBottomLeft.setPosition(GLYPHBOTTOMLEFTOPEN);
                glyphBottomRight.setPosition(GLYPHBOTTOMRIGHTOPEN);
                glyphTopLeft.setPosition(GLYPHTOPLEFTOPEN);
                glyphTopRight.setPosition(GLYPHTOPRIGHTOPEN);
                lifterMotor.setTargetPosition(LIFTERMOTORBOTTOM);
                glyphCRServoState = glyphCRServoStates.TransitBottom;
                
            } else if (curGlyphPos == 1) {
                // top closed, and lifted to the top
                lifterMotor.setTargetPosition(LIFTERMOTORBOTTOM);
                glyphBottomLeft.setPosition(GLYPHBOTTOMLEFTOPEN);
                glyphBottomRight.setPosition(GLYPHBOTTOMRIGHTOPEN);
                glyphTopLeft.setPosition(GLYPHTOPLEFTCLOSED);
                glyphTopRight.setPosition(GLYPHTOPRIGHTCLOSED);
                glyphCRServoState = glyphCRServoStates.TransitTop;
                
            } else if (curGlyphPos == 2) {
                lifterMotor.setTargetPosition(LIFTERMOTORMIDDLE);
                // bottom closed, lifted off the floor
                glyphBottomLeft.setPosition(GLYPHBOTTOMLEFTCLOSED);
                glyphBottomRight.setPosition(GLYPHBOTTOMRIGHTCLOSED);
                glyphTopLeft.setPosition(GLYPHTOPLEFTCLOSED);
                glyphTopRight.setPosition(GLYPHTOPRIGHTCLOSED);
                glyphCRServoState = glyphCRServoStates.TransitTop;
            } else if (curGlyphPos == 3) {
                // all closed, lifdted up high
                lifterMotor.setTargetPosition(LIFTERMOTORTOP);
                glyphBottomLeft.setPosition(GLYPHBOTTOMLEFTCLOSED);
                glyphBottomRight.setPosition(GLYPHBOTTOMRIGHTCLOSED);
                glyphTopLeft.setPosition(GLYPHTOPLEFTCLOSED);
                glyphTopRight.setPosition(GLYPHTOPRIGHTCLOSED);
                glyphCRServoState = glyphCRServoStates.TransitTop;
            } else if (curGlyphPos == 4) {
                // open, lifted up high.
                lifterMotor.setTargetPosition(LIFTERMOTORTOP);
                glyphBottomLeft.setPosition(GLYPHBOTTOMLEFTOPEN);
                glyphBottomRight.setPosition(GLYPHBOTTOMRIGHTOPEN);
                glyphTopLeft.setPosition(GLYPHTOPLEFTOPEN);
                glyphTopRight.setPosition(GLYPHTOPRIGHTOPEN);
                glyphCRServoState = glyphCRServoStates.TransitTop;
                
            }
            //compliant spitters out 
            if (gamepad2.dpad_down)
            {
                LeftCompliant.setPower(1);
                RightCompliant.setPower(1);
                telemetry.addLine("Compliant Wheels runing OUT");
            } 
            //compliant suckers in.
            else if (gamepad2.dpad_up)
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
            
            // smart crservo system:
            telemetry.addData("glyph cr servo state: ", glyphCRServoState);
            telemetry.addData("glyph cr servo time: ", glyphLifterServoMovingTime);
            
            if (glyphCRServoState == glyphCRServoStates.TransitTop) {
                glyphLifterServo.setPower(-1);
            }
            else if (glyphCRServoState == glyphCRServoStates.TransitBottom) {
                        glyphLifterServo.setPower(1);
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
                
                if (gamepad2.left_trigger > .8) {
                    glyphBottomLeft.setPosition(GLYPHBOTTOMLEFTCLOSED);
                    glyphBottomRight.setPosition(GLYPHBOTTOMRIGHTCLOSED);
                } else if (gamepad2.right_trigger > .8) {
                    glyphBottomLeft.setPosition(GLYPHBOTTOMLEFTOPEN);
                    glyphBottomRight.setPosition(GLYPHBOTTOMRIGHTOPEN);
                }
                lifterMotor.setPower(gamepad2.right_stick_y);
                glyphLifterServo.setPower(gamepad2.left_stick_y);
                
            //compliant spitters out 
            if (gamepad2.dpad_down)
            {
                LeftCompliant.setPower(1);
                RightCompliant.setPower(1);
                telemetry.addLine("Compliant Wheels runing OUT");
            } 
            //compliant suckers in.
            else if (gamepad2.dpad_up)
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
            
                
        } // end manual mode for glyph system
        } else if (gp2State == gp2States.Relic) {// end if mode == glyph
            if (gamepad2.y) {
                
                telemetry.addLine("relic extender: OUT");
                relicExtender.setPower(1);
            } else if (gamepad2.a) {
                
                telemetry.addLine("relic extender: IN");
                relicExtender.setPower(-1);
            } else {
                
                telemetry.addLine("relic extender: NONE");
                relicExtender.setPower(0);
            }
            
            if (gamepad2.b) {
                telemetry.addLine("relic holder: closed");
                relicHolder.setPosition(RELICHOLDERCLOSED);
            } else if (gamepad2.x) {
                telemetry.addLine("relic holder: open");
                relicHolder.setPosition(RELICHOLDEROPEN);
            }
            if (gamepad2.dpad_up) {
                telemetry.addLine("relic spinner: open");
                relicSpinner.setPower(-1);
            } else if (gamepad2.dpad_down) {
                telemetry.addLine("relic spinner: close");
                relicSpinner.setPower(1);
            }
            else {
                telemetry.addLine("relic spinner: none");
                relicSpinner.setPower(0);
            }
        }
            
            
        
            
            telemetry.addData("gp2 state", gp2State);
            telemetry.update();
        } // while( opModeIsActive())
    } // public void runOpMode() {
} // public class RemoteControl extends LinearOpMode {
