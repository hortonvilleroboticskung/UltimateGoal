package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp (name = "GrabbyArmyTele-Op", group = "TeleOp")
public class DemoTeleOp extends OpMode {

    DcMotor left, right, armLift;
    Servo armLeft, armRight;
//    DFRoboticsGravityI2CTemperatureandDistanceSensor thingo;
    boolean closed = false;
    boolean closedOS = true;

    @Override
    public void init() {
        left = hardwareMap.dcMotor.get("mtrLeftDrive");
        right = hardwareMap.dcMotor.get("mtrRightDrive");
        armLift = hardwareMap.dcMotor.get("mtrLift");

        right.setDirection(DcMotorSimple.Direction.REVERSE);

        left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        armLeft = hardwareMap.servo.get("srvLeft");
        armRight = hardwareMap.servo.get("srvRight");
        armRight.setDirection(Servo.Direction.REVERSE);

    }

    @Override
    public void loop() {
        //Speed Control
        left.setPower(Math.abs(gamepad1.left_stick_y) < 0.05 ? 0 : gamepad1.right_trigger > .5 ? gamepad1.left_stick_y/2 : gamepad1.left_stick_y);
        right.setPower(Math.abs(gamepad1.right_stick_y) < 0.05 ? 0 : gamepad1.right_trigger > .5 ? gamepad1.right_stick_y/2 : gamepad1.right_stick_y);

        //Arm Controls
        telemetry.addData("ARMY", armLift.getCurrentPosition());
        //              1)..............................................   1t & 2)..   2t).   2f & 3)...   3t)   3f)..   1f)..
        armLift.setPower(armLift.getCurrentPosition() < 0 || gamepad1.y || gamepad1.left_bumper ? gamepad1.y ? -0.6 : gamepad1.a && !gamepad1.start ? 0.5 : -0.0 : -0.0);

        //Arm Soft Stop Override
        if(gamepad1.back){
            armLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            armLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }


        //Servo Control - OS Toggle
        if(gamepad1.right_bumper && closedOS){
            closedOS = false;
            closed = !closed;
            telemetry.addData("CLOSED", closed);
        }else if(!gamepad1.right_bumper) closedOS = true;

        if(closed){
            armLeft.setPosition(0.8);
            armRight.setPosition(0.8);
        } else {
            armLeft.setPosition(0);
            armRight.setPosition(0);
        }

    }
}