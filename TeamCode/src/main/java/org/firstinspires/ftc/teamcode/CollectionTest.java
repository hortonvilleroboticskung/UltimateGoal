package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp (name = "Collection Test", group = "TeleOp")
public class CollectionTest extends OpMode {

    Servo clampLeft,clampRight,rotator;
    boolean clampLeftTog = false;
    boolean clampLeftOS = false;
    boolean clampRightTog = false;
    boolean clampRightOS = false;
    boolean rotatorTog = false;
    boolean rotatorOS = false;


    @Override
    public void init() {

        rotator = hardwareMap.servo.get("srvRotator");
        clampLeft = hardwareMap.servo.get("srvClampLeft");
        clampRight = hardwareMap.servo.get("srvClampRight");
    }

    @Override
    public void loop() {

        //Flap Control - OS Toggle
        if(gamepad1.a && !clampLeftOS){
            clampLeftOS = true;
            clampLeftTog = !clampLeftTog;
        } else if(!gamepad1.a) clampLeftOS = false;

        if(clampLeftTog){
            clampLeft.setPosition(.5);
            telemetry.addData("Left IDK",clampLeft.getPosition());
        } else {
            clampLeft.setPosition(.15);
            telemetry.addData("Left otherIDK",clampLeft.getPosition());
        }

        //Rotator Control - OS Toggle
        if(gamepad1.b && !clampRightOS){
            clampRightOS = true;
            clampRightTog = !clampRightTog;
        } else if(!gamepad1.b) clampRightOS = false;

        if(clampRightTog){
            clampRight.setPosition(.85);
            telemetry.addData("Right idk",clampRight.getPosition());
        } else {
            clampRight.setPosition(.5);
            telemetry.addData("Right otherIDK",clampRight.getPosition());
        }

        if(gamepad1.y && !rotatorOS){
            rotatorOS = true;
            rotatorTog = !rotatorTog;
        } else if (!gamepad1.y) rotatorOS = false;

        if(rotatorTog){
            rotator.setPosition(.9);
            telemetry.addData("Rotator IDK",rotator.getPosition());
        } else {
            rotator.setPosition(.1);
            telemetry.addData("Rotator otherIDK",rotator.getPosition());
        }


    }
}