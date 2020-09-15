package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;

import org.firstinspires.ftc.robotcore.internal.usb.exception.RobotUsbTimeoutException;


@TeleOp(name = "Color & Distance Sensor Test", group ="test")
public class ColorAndDistanceSensorTest extends OpMode {

    Robot r;

    @Override
    public void init() {
        r = Robot.getInstance();
        r.initialize(this);
    }

    @Override
    public void loop() {

        telemetry.addData("RED Front", r.getColorValue("colorFront","red"));
        telemetry.addData("BLUE", r.getColorValue("colorFront","blue"));
        telemetry.addData("GREEN", r.getColorValue("colorFront","green"));

        telemetry.addData("RED Back", r.getColorValue("colorBack","red"));
        telemetry.addData("BLUE", r.getColorValue("colorBack","blue"));
        telemetry.addData("GREEN", r.getColorValue("colorBack","green"));
    }
}
