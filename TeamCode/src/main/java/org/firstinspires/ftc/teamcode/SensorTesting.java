package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "SensorTestingTeleOp", group = "TeleOp")
public class SensorTesting extends OpMode {
    DFRoboticsGravityI2CTemperatureandDistanceSensor thingo;
    @Override
    public void init() {
        thingo = (DFRoboticsGravityI2CTemperatureandDistanceSensor)hardwareMap.get("thingo");
//        byte[] bytes = ;
        thingo.write(DFRoboticsGravityI2CTemperatureandDistanceSensor.Register.CONFIGURE_REGISTERS.bVal, new byte[]{-0b11000});
    }

    @Override
    public void loop() {
        thingo.readShort(DFRoboticsGravityI2CTemperatureandDistanceSensor.Register.DISTANCE_HIGH);
        telemetry.addData("THINGO", thingo.getDistanceRaw());

    }
}
