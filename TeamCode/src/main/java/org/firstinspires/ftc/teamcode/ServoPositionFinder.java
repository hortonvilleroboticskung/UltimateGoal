package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class ServoPositionFinder extends OpMode {

    Servo servo;
    HardwareMap.DeviceMapping<Servo> sList;
    ArrayList<String> nList = new ArrayList<>();
    int sIndex = 0;

    @Override
    public void init() {
        Set<Map.Entry<String, Servo>> sList = hardwareMap.servo.entrySet();
        for(Map.Entry<String, Servo> entry: sList){
            nList.add(entry.getKey());
        }
    }

    @Override
    public void init_loop(){

    }

    @Override
    public void loop() {

    }
}
