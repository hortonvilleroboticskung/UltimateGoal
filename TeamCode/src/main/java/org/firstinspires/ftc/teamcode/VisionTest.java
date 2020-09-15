package org.firstinspires.ftc.teamcode;

import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.media.Image;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Autonomous(name="VisionTest")
public class VisionTest extends OpMode {

    boolean picOS = true;
    StateMachine sm = new StateMachine();
    ImageAnalyzer imageAnalyzer = new ImageAnalyzer(1, 1);
    int pos = -1;
    int count = 0;
    int[] returnValues = new int[2];
    File filename;
    Bitmap b = null;
    int[] out = {-1,-1};
    Timer t = new Timer();
    File f = new File(Environment.getExternalStorageDirectory() + "/imagetest.txt");

    @Override
    public void init() {
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void loop() {

        sm.initializeMachine();

        int[] temp = sm.getVisionData();
        out = temp == null ? out : temp;

        sm.pause(3000);

        if(sm.next_state_to_execute()){
            count++;
            sm.reset();
        }

        telemetry.addData("pos", out[0]);
        telemetry.addData("count", out[1]);
        telemetry.addData("Total count", count);
        if (gamepad1.a && !gamepad1.start && picOS) {
            try {
                filename = FtcRobotControllerActivity.ftcApp.takePicture();
                imageAnalyzer.analyze(filename);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

            picOS = false;
        }
        if (b != null) {
            Log.e("testing", "B not null");
        }
        if (!gamepad1.a) picOS = true;

    }
}

