package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp (name = "Lift Test", group = "Testing")
public class LiftTest extends OpMode {

    DcMotor lift, otherMotor;


    @Override
    public void init() {

        lift = hardwareMap.dcMotor.get("mtrLift");
        otherMotor = hardwareMap.dcMotor.get("mtrOther");
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        otherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    @Override
    public void loop() {

        /*
                        Gamepad1.y  --->    DOWN
                        Gamepad1.a  --->    UP
                        Else                No Motor Power
         */
        lift.setPower(gamepad1.y ? -0.6 : gamepad1.a && !gamepad1.start ? 0.5 : 0.0);
        otherMotor.setPower(gamepad1.y ? 0.5 : gamepad1.a && !gamepad1.start ? -0.6 : 0.0);


    }
}