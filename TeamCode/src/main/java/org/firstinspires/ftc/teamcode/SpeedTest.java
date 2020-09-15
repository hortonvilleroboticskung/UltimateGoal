package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import static org.firstinspires.ftc.teamcode.Robot.wheelSet1;
import static org.firstinspires.ftc.teamcode.Robot.wheelSet2;

@Autonomous (name = "SpeedTest", group = "Testing")
public class SpeedTest extends OpMode {

    Robot r = null;
    StateMachine s = new StateMachine();

    @Override
    public void init() {
        r = Robot.getInstance();
        r.initialize(this);
    }

    @Override
    public void loop() {
        s.initializeMachine();
        if(s.next_state_to_execute()){
            r.setPower(wheelSet1[0],0.90071    *.3); //Top Left 0.8765382124
            r.setPower(wheelSet1[1],0.90451    *.3); //Bottom Right 0.8571775537
            r.setPower(wheelSet2[0],0.94713    *.3); //Top Right 0.9452373242
            r.setPower(wheelSet2[1],1               *.3); //Bottom Left
        }
    }
}
