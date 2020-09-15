package org.firstinspires.ftc.teamcode;

import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.media.Image;
import android.provider.ContactsContract;
import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;

import java.io.File;

import static org.firstinspires.ftc.teamcode.Robot.wheelSet1;
import static org.firstinspires.ftc.teamcode.Robot.wheelSet2;

class StateMachine{

    public String flag = "";
    //Robot rbt= Robot.getInstance();
    //From NEW Kung Local branch
    Robot rbt = Robot.getInstance();
    final String TAG = "StateMachine";
    final double wheelCircumference = 2.7222222222222222222222222222222 * Math.PI;
    final int countsPerRotation = 560;
    int current_number = 0;
    int state_in_progress = 1;
    int pos = 0;
    Timer t = new Timer();
    boolean timerOS = true;
    boolean initOS = true;
    boolean moveInit = true;

    long systemTime;

    @Override
    public String toString(){
        return "cn:"+current_number+"\tSIP:"+state_in_progress+"\tFlag:"+flag;
    }

    boolean next_state_to_execute(){
        return (state_in_progress == ++current_number);
    }
    void initializeMachine(){
        current_number = 0;
    }

    void incrementState(){
        state_in_progress++;
    }

    void reset(){
        state_in_progress = 1;
        current_number = 0;
    }

    boolean waitHasFinished(long milliseconds) {
        boolean returnVal = false;

        if (initOS) {
            systemTime = System.nanoTime() / 1000000;
            initOS = false;
        } else if ((System.nanoTime() / 1000000) - systemTime >= milliseconds) {
            initOS = true;
            returnVal = true;
        }

        return returnVal;
    }

    void SetFlag(StateMachine receiver, String key){
        if(next_state_to_execute()){
            receiver.flag = key;
            incrementState();
        }
    }

    void WaitForFlag(String key){
        if(next_state_to_execute()){
            if(flag.equals(key)){
                incrementState();
            }
        }
    }

    public void pause(long msec) {
        if(next_state_to_execute()) {
            if(timerOS){
                t.reset();
                timerOS = false;
            }
            if(t.hasTimeElapsed(msec)) {
                incrementState();
                timerOS = true;
            }
        }
    }

    void ClearFlag(){
        if(next_state_to_execute()){
            flag = "";
            incrementState();
        }
    }

    public void initRunToTarget(String motorName, int target, double power) {
        if (rbt.motors.get(motorName) != null) {
            rbt.setTarget(motorName, target);
            rbt.setPower(motorName, power);
        } else {
            Log.e(TAG, "initRunToTarget: failed to run motor: " + motorName + " to target: " + target + " at power: " + power);
        }
    }

    public void initRunToTarget(String motorName, int target, double power, boolean reset) {
        if (rbt.motors.get(motorName) != null) {
            if (reset) rbt.resetEncoder(motorName);
            initRunToTarget(motorName, target, power);
        }
    }

//    <<------------------------------ Mecanum Wheels --------------------------------->>


    void translate(double degrees, double power, double distance) { //Degrees0->Straight, Degrees 90 -> Left , Degrees -90 -> Right, Degrees 180 -> Backwards
            //boolean moveInit = true;
        if(next_state_to_execute()) {
            double wheelRotations = distance / wheelCircumference;
            int targetEncoderCounts = (int) (wheelRotations * countsPerRotation);
            double theta2 = (-45 - degrees) / 180.0 * Math.PI;
            int wheelSetEncoder1 = (int) (targetEncoderCounts * (Math.cos(theta2)));
            Log.d("ENCODERS", targetEncoderCounts + "");
            int wheelSetEncoder2 = (int) (-targetEncoderCounts * (Math.sin(theta2)));
            double wheelSetPower1 = power * (Math.cos(theta2));
            double wheelSetPower2 = power * -(Math.sin(theta2));

            Log.d("POWER1: ",wheelSetPower1+"");
            Log.d("POWER2: ",wheelSetPower2+"");
            //Log.d("ENC_TRANSLATE",String.format("MoveInit:%s",moveInit));
            //if(moveInit) {
            Log.d("ENCODERS", "(" + wheelSetEncoder1 + ":" + wheelSetPower1 + ") , (" + wheelSetEncoder2 + ":" + wheelSetPower2 + ")");

            if (moveInit) {
                rbt.initRunDriveToTarget(wheelSetEncoder1, wheelSetPower1, wheelSetEncoder2, wheelSetPower2, true);
                moveInit = false;
            }

            if ((rbt.hasMotorEncoderReached(wheelSet1[0], wheelSetEncoder1+10)
                    || rbt.hasMotorEncoderReached(wheelSet1[0], wheelSetEncoder1-10))
                    && (rbt.hasMotorEncoderReached(rbt.wheelSet2[0], wheelSetEncoder2+10)
                    || rbt.hasMotorEncoderReached(wheelSet2[0], wheelSetEncoder2-10)
                    )) {
                Log.d("ENC_TRANSLATE", String.format("(%d,%d) / (%d,%d)",
                        rbt.getEncoderCounts(wheelSet1[0]),
                        rbt.getEncoderCounts(rbt.wheelSet2[0]),
                        wheelSetEncoder1, wheelSetEncoder2));

                rbt.setDrivePower(-wheelSetPower1,-wheelSetPower2);
                rbt.setPower(wheelSet1[0],0);
                rbt.setPower(wheelSet1[1],0);
                rbt.setPower(wheelSet2[0],0);
                rbt.setPower(wheelSet2[1],0);

                rbt.resetDriveEncoders();
                rbt.setDriveRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
                moveInit = true;
                incrementState();
//            }
            }
        }
    }


    public void rotate(double degrees, double power) {
        if(next_state_to_execute()) {
            double turnCircumference = 16.5 * Math.PI; //changed but still have to test long-term rotate effect
            double wheelRotations = (turnCircumference / wheelCircumference) * (Math.abs(degrees) / 360);
            int targetEncoderCounts = (int) (wheelRotations * countsPerRotation);
            Log.d(TAG, "MOTOR turn: Target counts: " + targetEncoderCounts);

            initRunToTarget(rbt.wheelSetL[0], (int) (-Math.signum(degrees) * targetEncoderCounts), power);
            initRunToTarget(rbt.wheelSetL[1], (int) (-Math.signum(degrees) * targetEncoderCounts), power);
            initRunToTarget(rbt.wheelSetR[0], (int) (Math.signum(degrees) * targetEncoderCounts), power);
            initRunToTarget(rbt.wheelSetR[1], (int) (Math.signum(degrees) * targetEncoderCounts), power);

            Log.d(TAG, "BMOTOR Front Left "+rbt.getEncoderCounts("mtrFrontLeft"));
            Log.d(TAG, "BMOTOR Front Right "+rbt.getEncoderCounts("mtrFrontRight"));
            Log.d(TAG, "BMOTOR Back Left "+rbt.getEncoderCounts("mtrBackLeft"));
            Log.d(TAG, "BMOTOR Back Right "+rbt.getEncoderCounts("mtrBackRight"));

            if (rbt.hasMotorEncoderReached(rbt.wheelSetL[0], targetEncoderCounts)
                    && rbt.hasMotorEncoderReached(rbt.wheelSetR[0], targetEncoderCounts)) {
                Log.d(TAG,"turn: counts: "+rbt.getEncoderCounts(rbt.wheelSet1[0]));

                Log.d(TAG, "MOTOR Front Left "+rbt.getEncoderCounts("mtrFrontLeft"));
                Log.d(TAG, "MOTOR Front Right "+rbt.getEncoderCounts("mtrFrontRight"));
                Log.d(TAG, "MOTOR Back Left "+rbt.getEncoderCounts("mtrBackLeft"));
                Log.d(TAG, "MOTOR Back Right "+rbt.getEncoderCounts("mtrBackRight"));


                rbt.setPower(wheelSet1[0],0);
                rbt.setPower(wheelSet1[1],0);
                rbt.setPower(wheelSet2[0],0);
                rbt.setPower(wheelSet2[1],0);

                rbt.resetDriveEncoders();
                rbt.setDriveRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
                incrementState();
            }
        }

    }

    public void skyStone(){
        if(next_state_to_execute()) {
            try {
                /*rbt.takePicture();
                ImageAnalyzer imageAnalyzer = new ImageAnalyzer(0, 0);
                pos = imageAnalyzer.analyze(rbt.imageView);*/
                incrementState();
            } catch (Exception c){
                Log.e("Picture Execpetion",c+"");
            }
        }
    }
    //

//    public int[] getVisionData() {
//        if (next_state_to_execute()) {
//            int[] returnvalues = null;
//            try {
//                File filename = FtcRobotControllerActivity.ftcApp.takePicture();
//                returnvalues = new ImageAnalyzer(0, 0).analyze(filename);
//                incrementState();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//            return returnvalues;
//        }
//        return null;
//    }

}
