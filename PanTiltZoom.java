/*
the format of the location points file (locations.txt) is as follows:

each set of two rows represents a pan and tilt.

they are ordered as such:
presetAPan 
presetATilt
presetBPan 
presetBTilt
presetXPan 
presetXTilt
presetYPan 
presetYTilt
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import java.lang.annotation.Target;
import com.qualcomm.robotcore.util.Range;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a PushBot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Remove a @Disabled the on the next line or two (if present) to add this opmode to the Driver Station OpMode list,
 * or add a @Disabled annotation to prevent this OpMode from being added to the Driver Station
 */
@TeleOp

public class PanTiltZoom extends LinearOpMode {

Servo panServo;
Servo tiltServo;
File file = new File("/storage/emulated/0/locations.txt");

double presetAPan;
double presetATilt;

double presetBPan;
double presetBTilt;

double presetYPan;
double presetYTilt;

double presetXPan;
double presetXTilt;

double panTarget;
double tiltTarget;

public double extractDouble(BufferedReader br) throws FileNotFoundException {
    try{
    Scanner sc = new Scanner(br.readLine());
    return sc.nextDouble();
    } catch (IOException e) {
        throw new FileNotFoundException("lol not enough lines");
    }
}
public void telemetryPresets() {
    telemetry.addData("presetAPan", presetAPan);
    telemetry.addData("presetATilt", presetATilt);
    telemetry.addData("presetBPan", presetBPan);
    telemetry.addData("presetBTilt", presetBTilt);
    telemetry.addData("presetXPan", presetXPan);
    telemetry.addData("presetXTilt", presetXTilt);
    telemetry.addData("presetYPan", presetYPan);
    telemetry.addData("presetYTilt", presetYTilt);
    telemetry.addData("panTarget", panTarget);
    telemetry.addData("tiltTarget", tiltTarget);
}

    @Override
    public void runOpMode() {
        boolean useFile=true;
        if (!file.exists()){
            telemetry.addLine("created new location file");
            try {
                file.createNewFile();
            } catch (IOException e) {
                useFile=false;
            }
            PrintWriter pw;
            try {
                pw = new PrintWriter(file);
                if (useFile){
                    for (int i=0;i!=5;i++){ // fill new file with default values
                        pw.println("0.5");
                        pw.println("0.5");
                    }
                    pw.close();
                }
            } catch (FileNotFoundException e) {
                // shrug
                useFile=false;
            }
            presetAPan = .5;
            presetATilt= .5;
            presetBPan = .5;
            presetBTilt= .5;
            presetXPan = .5;
            presetXTilt= .5;
            presetYPan = .5;
            presetYTilt= .5;
            panTarget=.5;
            tiltTarget =.5;
        } //  if file not exists
        else
        {
            BufferedReader br;
            
            try {
            br = new BufferedReader(new FileReader(file));
            presetAPan  = extractDouble(br);
            presetATilt =extractDouble(br);
            presetBPan  =extractDouble(br);
            presetBTilt =extractDouble(br);
            presetXPan  =extractDouble(br);
            presetXTilt =extractDouble(br);
            presetYPan  =extractDouble(br);
            presetYTilt = extractDouble(br);
            panTarget = extractDouble(br);
            tiltTarget = extractDouble(br);
            try {
            br.close();
            } catch (IOException e) {
                throw new FileNotFoundException("lol wut");
            }
            }catch (FileNotFoundException e) {
                useFile=false;
            presetAPan = .5;
            presetATilt= .5;
            presetBPan = .5;
            presetBTilt= .5;
            presetXPan = .5;
            presetXTilt= .5;
            presetYPan = .5;
            presetYTilt= .5;
            panTarget=.5;
            tiltTarget =.5;
            }
        }
        
        
        
        panServo = hardwareMap.get(Servo.class, "panservo");
        tiltServo = hardwareMap.get(Servo.class, "tiltservo");
        telemetry.addData("Status", "Initialized");
        telemetryPresets();
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        // run until the end of the match (driver presses STOP)
        panServo.setPosition(panTarget);
        tiltServo.setPosition(tiltTarget);
        
        
        while (opModeIsActive()) {
            panTarget = panServo.getPosition();
            tiltTarget = tiltServo.getPosition();
            
            panTarget = panTarget + (gamepad1.left_stick_x * .0015);
            tiltTarget = tiltTarget + (gamepad1.left_stick_y *.0015);
            if (gamepad1.left_bumper || gamepad1.right_bumper){
                if (gamepad1.a) {
                    presetATilt = tiltTarget;
                    presetAPan = panTarget;
                }
                if (gamepad1.b) {
                    presetBTilt = tiltTarget;
                    presetBPan = panTarget;
                }
                if (gamepad1.x) {
                    presetXTilt = tiltTarget;
                    presetXPan = panTarget;
                }
                if (gamepad1.y) {
                    presetYTilt = tiltTarget;
                    presetYPan = panTarget;
                }
            } else 
            if (gamepad1.a) {
                    tiltTarget = presetATilt;
                    panTarget = presetAPan;
                }
                if (gamepad1.b) {
                    tiltTarget = presetBTilt;
                    panTarget = presetBPan;
                }
                if (gamepad1.x) {
                    tiltTarget = presetXTilt;
                    panTarget = presetXPan;
                }
                if (gamepad1.y) {
                    tiltTarget = presetYTilt;
                    panTarget = presetYPan;
                }
                
            panTarget = Range.clip(panTarget, 0, 1);
            tiltTarget = Range.clip(tiltTarget, 0, 1);
            panServo.setPosition(panTarget);
            tiltServo.setPosition(tiltTarget);
            
            telemetryPresets();
            telemetry.update();
        } // while opmode is active
        
        if (useFile){
            try{
                PrintWriter pwNew;
                pwNew = new PrintWriter(file);
                
                pwNew.println(presetAPan);
                pwNew.println(presetATilt);
                pwNew.println(presetBPan);
                pwNew.println(presetBTilt);
                pwNew.println(presetXPan );
                pwNew.println(presetXTilt);
                pwNew.println(presetYPan );
                pwNew.println(presetYTilt);
                pwNew.println(panTarget);
                pwNew.println(tiltTarget);
                pwNew.close();
            } catch (FileNotFoundException e) {
                telemetry.addLine("ERROR WRITING PRESETS? HOW DID THIS HAPPEN?");
                telemetry.update();
                sleep(2000);
            }
        }
    }
}
