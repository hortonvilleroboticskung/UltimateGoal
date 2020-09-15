package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name = "MecanumTest", group ="test")
public class MechanumWheelTestTeleOp extends OpMode {

    Robot r;
    public boolean auto = false;
    StateMachine m = new StateMachine();
    double theta1 = 0;

    @Override
    public void init() {
        m.state_in_progress = 99;
        r = Robot.getInstance();
        r.initialize(this);
    }

    @Override
    public void loop() {

        m.initializeMachine();

        if (!auto) {
            //No turning while translating
            if (Math.abs(gamepad1.right_stick_x) < 0.05) {

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

                //I JUST NEED THIS COMMENT TO FORCE PUSH
                r.setPower(r.wheelSet1[0], Math.abs(x) > .05 || Math.abs(y) > .05 ? Math.sqrt(x * x + y * y) * Math.cos(theta2) : 0);
                r.setPower(r.wheelSet2[0], Math.abs(x) > .05 || Math.abs(y) > .05 ? -Math.sqrt(x * x + y * y) * Math.sin(theta2) : 0);
                r.setPower(r.wheelSet1[1], Math.abs(x) > .05 || Math.abs(y) > .05 ? Math.sqrt(x * x + y * y) * Math.cos(theta2) : 0);
                r.setPower(r.wheelSet2[1], Math.abs(x) > .05 || Math.abs(y) > .05 ? -Math.sqrt(x * x + y * y) * Math.sin(theta2) : 0);


            }else {
                r.setPower(r.wheelSetL[0], gamepad1.right_stick_x * .5);
                r.setPower(r.wheelSetL[1], gamepad1.right_stick_x * .5);
                r.setPower(r.wheelSetR[0], -gamepad1.right_stick_x * .5);
                r.setPower(r.wheelSetR[1], -gamepad1.right_stick_x * .5);
            }

            if(Math.abs(gamepad2.left_stick_y) > .5) {
                r.setServoPosition("srvClampLeft", gamepad2.left_stick_y);
            }
        }

        if(gamepad1.x && !gamepad1.start){
            auto = true;
            m.reset();
            r.resetDriveEncoders();

        }

        if(m.next_state_to_execute()){
            int frontBlue = r.getColorValue("colorFront", "blue");
            int backBlue = r.getColorValue("colorBack", "blue");
            if(frontBlue<11){
                //WheelSetL is static, might cause problem
                r.setPower(Robot.wheelSetL[0],.3);
                r.setPower(Robot.wheelSetL[1],.3);
            }
            if(backBlue<11){
                r.setPower(Robot.wheelSetL[0],-.3);
                r.setPower(Robot.wheelSetL[1],-.3);
            }
            if(frontBlue >= 11 && backBlue >= 11) m.incrementState();
        }
        if(m.next_state_to_execute() && auto){
            auto = false;
            m.incrementState();
        }

        telemetry.addData("mtrFrontLeft", r.getEncoderCounts("mtrFrontLeft"));
        telemetry.addData("mtrFrontRight", r.getEncoderCounts("mtrFrontRight"));
        telemetry.addData("mtrBackLeft", r.getEncoderCounts("mtrBackLeft"));
        telemetry.addData("mtrBackRight", r.getEncoderCounts("mtrBackRight"));

        telemetry.addData("theta1", theta1 * 180 / Math.PI);
        telemetry.addData("SIP", m.state_in_progress);
        telemetry.addData("Auto", auto);
    }
}
