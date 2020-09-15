package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name = "TeleOpComp", group ="Comp")
public class TeleOpComp extends OpMode {

    Robot r;
    Servo srvClampRight, srvClampLeft, srvFound, srvRotator;
    public boolean auto = false;
    StateMachine m = new StateMachine();
    double theta1 = 0;
    boolean OSClamp, OSClampRight, openClampRight, openClamp, OSRotator, openRotator, OSFound, downFound = false;

    @Override
    public void init() {

        try {
            srvClampLeft = hardwareMap.servo.get("srvClampLeft");
            srvClampRight = hardwareMap.servo.get("srvClampRight");
            srvRotator = hardwareMap.servo.get("srvRotator");
            srvFound = hardwareMap.servo.get("srvFound");
        }catch (Exception e){}
        m.state_in_progress = 99;
        r = Robot.getInstance();
        r.initialize(this);
    }

    @Override
    public void loop() {

        m.initializeMachine();

        if (!auto) {

//          ****************************************************************************************
            //            Gamepad One Controls
            //right trigger     25% speed
            //right button      72% speed
            //regular           50% speed

//          ****************************Driving Controls********************************************

            //If not rotating
            if (Math.abs(gamepad1.right_stick_x) < 0.075) {
                double x = gamepad1.left_stick_x;
                double y = -gamepad1.left_stick_y;

                theta1 = ((Math.atan(y / x)));
                //This series of if statements prevents us from dividing by 0
                //Because we divide by X, X != 0
                if (x == 0 && y > 0) {
                    theta1 = Math.PI / 2;
                } else if (x == 0 && y < 0) {
                    theta1 = 3 * Math.PI / 2;
                } else if (x < 0) {
                    theta1 = Math.atan(y / x) + Math.PI;
                }
                double theta2 = Math.PI / 4 - theta1;
                double hyp = Math.sqrt(x * x + y * y);
                boolean motorBand = Math.abs(x) > .05 || Math.abs(y)> .05;
                double speedControl = gamepad1.right_bumper ? 1 : gamepad1.right_trigger > .5 ? .25 : .5;
                r.setPower(r.wheelSet1[0], motorBand ?  hyp * Math.cos(theta2) * speedControl : 0);
                r.setPower(r.wheelSet2[0], motorBand ? -hyp * Math.sin(theta2) * speedControl : 0);
                r.setPower(r.wheelSet1[1], motorBand ?  hyp * Math.cos(theta2) * speedControl : 0);
                r.setPower(r.wheelSet2[1], motorBand ? -hyp * Math.sin(theta2) * speedControl : 0);

            }else {
                r.setPower(r.wheelSetL[0], gamepad1.right_stick_x/2);
                r.setPower(r.wheelSetL[1], gamepad1.right_stick_x/2);
                r.setPower(r.wheelSetR[0], -gamepad1.right_stick_x/2);
                r.setPower(r.wheelSetR[1], -gamepad1.right_stick_x/2);
            }

//          ***************************Collector Controls*******************************************

            if(gamepad1.left_bumper) {
                r.setPower("mtrCollectionLeft", .35);
                r.setPower("mtrCollectionRight", .35);
            }else if(gamepad1.left_trigger > 0.5){
                r.setPower("mtrCollectionLeft",-.6);
                r.setPower("mtrCollectionRight",-.6);
            } else {
                r.setPower("mtrCollectionLeft",0);
                r.setPower("mtrCollectionRight",0);
            }
//          ****************************************************************************************
//            Gamepad Two Controls
            //dpad up           level up
            //dpad down         level down
            //left stick        adjust clamp
            //right stick       adjust lift
            //a                 toggle clamp
            //y                 toggle conveyor
            //x                 toggle angle in
            //b                 toggle angle out

//          ********************************Lift Controls*******************************************

            if(gamepad2.left_bumper){
                r.resetEncoder("mtrLift");
            }

            //up/down controls
            telemetry.addData("G2 RIGHT Y",gamepad2.right_stick_y);
            if(Math.abs(gamepad2.right_stick_y) > 0.05){
                r.setPower("mtrLift",gamepad2.right_stick_y);
            } else {
                r.setPower("mtrLift",0);
            }

//          *************************************Toggles********************************************

            if(gamepad2.a && !gamepad2.start && !gamepad1.start && !OSClamp){
                OSClamp = true;
                openClamp = !openClamp;
            } else if(!gamepad2.a && !gamepad1.start && !gamepad2.start) OSClamp = false;

            if(openClamp){
                r.setServoPosition("srvClampLeft",.18);
                r.setServoPosition("srvClampRight", .4);
            } else {
                r.setServoPosition("srvClampLeft",.3);
                r.setServoPosition("srvClampRight", .19);
            }

            //Foundation Clip.......................................................................
            if(gamepad2.y && !OSFound){
                OSFound = true;
                downFound = !downFound;
            } else if (!gamepad2.y) OSFound = false;

            if(downFound){
                r.setServoPosition("srvFound",.7);
            } else {
                r.setServoPosition("srvFound",.2);
            }

            //Rotator...............................................................................

            if(gamepad2.x && !OSRotator){
                OSRotator = true;
                openRotator = !openRotator;
            } else if(!gamepad2.x) OSRotator = false;

            if(openRotator){
                r.setServoPosition("srvRotator", .85);
            } else {
                r.setServoPosition("srvRotator", .1);
            }

//          **********************************Stick Adjustments*************************************

//            //clamp adjustment......................................................................
//            if(Math.abs(gamepad2.left_stick_y) > 0.05){
//                r.setServoPosition("srvClamp", Math.abs(gamepad2.left_stick_y));
//            } else if(Math.abs(gamepad1)) {
//                r.setServoPosition("srvClamp",0);
//            }



        }
// Automous In TeleOp
//        if(gamepad1.a && !gamepad1.start){
//            auto = true;
//            m.reset();
//            r.resetDriveEncoders();
//        }
//
//
//        //place autonomous code for teleop here
//
//        if(m.next_state_to_execute() && auto){
//            auto = false;
//            m.incrementState();
//        }

        telemetry.addData("mtrFrontLeft", r.getEncoderCounts("mtrFrontLeft"));
        telemetry.addData("mtrFrontRight", r.getEncoderCounts("mtrFrontRight"));
        telemetry.addData("mtrBackLeft", r.getEncoderCounts("mtrBackLeft"));
        telemetry.addData("mtrBackRight", r.getEncoderCounts("mtrBackRight"));

        telemetry.addData("Left Clamp Position:",srvClampLeft.getPosition());
        telemetry.addData("Right Clamp Position:",srvClampRight.getPosition());
        telemetry.addData("Rotator Position:",srvRotator.getPosition());
        telemetry.addData("Foundation Position:",srvFound.getPosition());


        telemetry.addData("theta1", theta1 * 180 / Math.PI);
        telemetry.addData("SIP", m.state_in_progress);
        telemetry.addData("Auto", auto);
    }
}
