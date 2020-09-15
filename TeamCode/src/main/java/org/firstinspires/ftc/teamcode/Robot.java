package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareDevice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.firstinspires.ftc.teamcode.Timer;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;


class   Robot{

    private Robot(){

    }

    byte[] bytes;

    File filepath;

    TextureView imageView = FtcRobotControllerActivity.ftcApp.imageView;

    TextView averages, heightTV, widthTV;

    CameraDevice cameraDevice;
    String cameraId;
    Size imageDimensions;
    CaptureRequest.Builder captureRequestBuilder;
    CameraCaptureSession cameraSession;

    //CameraHelper cameraHelper;

    Bitmap finalImage;


    ImageAnalyzer imageAnalyzer ;
    Handler backgroundHandler;
    HandlerThread handlerThread;



    public Map<String, DcMotor> motors;
    public Map<String, Object> servos;
    public Map<String, Object> sensors;
    List<String> flags = new ArrayList<>();
    public OpMode opMode = null;
    public static String TAG = "ROBOTCLASS";

    String[][] mtrList = {
            {"mtrFrontLeft","F"},   //Wheel Set 1
            {"mtrBackRight","R"},   //Wheel Set 1
            {"mtrBackLeft","F"},    //Wheel Set 2
            {"mtrFrontRight","R"},  //Wheel Set 2
            {"mtrLift","F"}
    };

    String[][] senList = {
            {"colorFront","3c"},
            {"colorBack","1a"}/*,
            {"distF","4c"},
            {"distS","5c"}*/
    };
    String[][] srvList = {
            {"srvClampLeft","p"},
            {"srvClampRight","p"},
            {"srvRotator","p"},
            {"srvFound","p"},
    };
    static String[] wheelSet1 = {"mtrFrontLeft", "mtrBackRight"};
    static String[] wheelSet2 = {"mtrFrontRight", "mtrBackLeft"};
    static String[] wheelSetL = {"mtrFrontLeft", "mtrBackLeft"};
    static String[] wheelSetR = {"mtrFrontRight", "mtrBackRight"};

    private static Robot currInstance = null;

    public void initialize(OpMode op){
        motors = new HashMap<>();
        servos = new HashMap<>();
        sensors = new HashMap<>();
        flags.clear();


        try {
            for (String[] m : mtrList) {
                DcMotor holder = op.hardwareMap.dcMotor.get(m[0]);
                if (m[1].equals("R")) holder.setDirection(DcMotorSimple.Direction.REVERSE);
                else if (m[1].equals("F")) holder.setDirection(DcMotorSimple.Direction.FORWARD);
                else holder = null;
                if(holder != null) holder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                motors.put(m[0], holder);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            for (String[] s : srvList) {
                Object holder = null;
                if (s[1].equals("p")) holder = op.hardwareMap.servo.get(s[0]);
                else if (s[1].equals("c")) holder = op.hardwareMap.crservo.get(s[0]);
                servos.put(s[0], holder);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            for(String[] sen : senList){
                    HardwareDevice sensor = op.hardwareMap.get(sen[0]);
                    Log.d("ROBOT", "SENSOR TYPE:\t"+sensor.getClass());
                    sensor.resetDeviceConfigurationForOpMode();
                    if (sensor instanceof ColorSensor) {
                        Log.d("ROBOT", "SENSOR IS RIGHT TYPE");
                        sensor = op.hardwareMap.colorSensor.get(sen[0]);
                        ((ColorSensor) sensor).setI2cAddress(I2cAddr.create8bit(Integer.parseInt(sen[1],16)));
                        ((ColorSensor) sensor).enableLed(true);
                        sensors.put(sen[0], (ColorSensor)sensor);
                    } else sensors.put(sen[0], sensor);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Robot getInstance() {
        currInstance = currInstance == null ? new Robot() : currInstance;
        return currInstance;
    }



    //----ROBOT UTILITY FUNCTIONS----//

    public void setPower(String motorName, double power) {
        if (motors.get(motorName) != null) motors.get(motorName).setPower(power);
    }

    public Integer getEncoderCounts(String motorName) {
        return (motors.get(motorName) != null) ? motors.get(motorName).getCurrentPosition() : null;
    }

    public Double getPower(String motorName) {
        return (motors.get(motorName) != null) ? motors.get(motorName).getPower() : null;
    }

    public void resetEncoder(String...motorNames) {
        for(String name : motorNames) {
            if (motors.get(name) != null) {
                DcMotor.RunMode rm = motors.get(name).getMode();
                setRunMode(name, DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                setRunMode(name, rm);
            } else {
                Log.e(TAG, "resetEncoder: motor is null: " + name);
            }
        }
    }

    public void setRunMode(String motorName, DcMotor.RunMode runMode) {
        if (motors.get(motorName) != null) motors.get(motorName).setMode(runMode);
    }

    public void setTarget(String motorName, int target) {
        if (motors.get(motorName) != null) {
            motors.get(motorName).setTargetPosition(target);
            setRunMode(motorName, DcMotor.RunMode.RUN_TO_POSITION);

        }
    }

    public void initRunToTarget(String motorName, int target, double power) {
        if (motors.get(motorName) != null) {
            setTarget(motorName, target);
            setPower(motorName, power);
        } else {
            Log.e(TAG, "initRunToTarget: failed to run motor: " + motorName + " to target: " + target + " at power: " + power);
        }
    }

    public void initRunToTarget(String motorName, int target, double power, boolean reset) {
        if (motors.get(motorName) != null) {
            if (reset) resetEncoder(motorName);
            initRunToTarget(motorName, target, power);
        }
    }

    public Integer getColorValue(String sensor, String channel) {
        if (sensors.get(sensor) != null && sensors.get(sensor) instanceof ColorSensor) {
            switch (channel) {
                case "red":
                    return ((ColorSensor) sensors.get(sensor)).red();
                case "blue":
                    return ((ColorSensor) sensors.get(sensor)).blue();
                case "green":
                    return ((ColorSensor) sensors.get(sensor)).green();
                case "alpha":
                    return ((ColorSensor) sensors.get(sensor)).alpha();
            }
        }
        return null;
    }

    public Double getDistance(String sensor) {
        if (sensors.get(sensor) != null && sensors.get(sensor) instanceof DistanceSensor) {
            return ((DistanceSensor) sensors.get(sensor)).getDistance(DistanceUnit.CM);
        }
        return null;
    }


    public void resetDriveEncoders() {
        resetEncoder(wheelSet1[0]);
        resetEncoder(wheelSet1[1]);
        resetEncoder(wheelSet2[0]);
        resetEncoder(wheelSet2[1]);
    }

    public void setDrivePower(double ws1Pow, double ws2Pow) {
        setPower(wheelSet1[0], ws1Pow);
        setPower(wheelSet1[1], ws1Pow);
        setPower(wheelSet2[0], ws2Pow);
        setPower(wheelSet2[1], ws2Pow);
    }

    public void setDriveEncoderTarget(int ws1Target, int ws2Target) {
        setTarget(wheelSet1[0], ws1Target);
        setTarget(wheelSet1[1], ws1Target);
        setTarget(wheelSet2[0], ws2Target);
        setTarget(wheelSet2[1], ws2Target);
    }

    public void setDriveRunMode(DcMotor.RunMode rm) {
        setRunMode(wheelSet1[0], rm);
        setRunMode(wheelSet1[1],rm);
        setRunMode(wheelSet2[0], rm);
        setRunMode(wheelSet2[1],rm);
    }

    public void initRunDriveToTarget(int ws1Target, double ws1Pow, int ws2Target, double ws2Pow) {
        setDriveEncoderTarget(ws1Target, ws2Target);
        setDrivePower(ws1Pow, ws2Pow);
        setDriveRunMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void initRunDriveToTarget(int ws1Target, double ws1Pow, int ws2Target, double ws2Pow, boolean reset) {
        if (reset) resetDriveEncoders();
        setDriveEncoderTarget(ws1Target, ws2Target);
        setDrivePower(ws1Pow, ws2Pow);
        setDriveRunMode(DcMotor.RunMode.RUN_TO_POSITION);

    }

    public boolean opModeIsActive() {
        return opMode instanceof LinearOpMode && ((LinearOpMode) opMode).opModeIsActive();
    }

//    public double calculateVelocity(VelocityTask t, long sampleTimeMS) {
//        Timer timer = new Timer();
//        double startVal = t.getSample();
//        while (!timer.hasTimeElapsed(sampleTimeMS)) ;
//        double currentVelocity = (t.getSample() - startVal) / sampleTimeMS * 1000.;
//        Log.d(TAG, "calculateVelocity: current velocity: " + currentVelocity);
//        return currentVelocity;
//    }

    public void setServoPosition(String servoName, double position) {
        Object servo = servos.get(servoName);
        if (servo != null) {
            if (servo instanceof Servo) ((Servo) servo).setPosition(position);
            else if (servo instanceof CRServo) setServoPower(servoName, position);
        } else Log.e(TAG, "setServoPosition: servo is null: " + servoName);
    }

    public void setServoPower( String servoName, double power) {
        Object servo = servos.get(servoName);
        if (servo != null) {
            if (servo instanceof CRServo) ((CRServo) servo).setPower(power);
            else if (servo instanceof Servo) setServoPosition(servoName, power);

        } else Log.e(TAG, "setServoPower: CR servo is null: " + servoName);
    }

    public Boolean hasMotorEncoderReached( String motorName, int encoderCount) {
        return (motors.get(motorName) != null) ? Math.abs(getEncoderCounts(motorName)) >= Math.abs(encoderCount) : null;
    }

    //METHOD TO BE CALLED UPON COMPLETION OF AN AUTONOMOUS PROGRAM IF ENCODERS ARE DESIRED TO BE RESET
    public void finish() {
        Log.i(TAG, "finish: entering finish phase");
        for (String m : motors.keySet()) resetEncoder(m);
    }

    TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            try {
                openCamera(getCameraManager());
            } catch (Exception e ){
                //Toast.makeText(MainActivity.this, e+"", Toast.LENGTH_LONG).show();
            }
        }



        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    CameraDevice.StateCallback stateCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera;

            try {
                startCameraPreview();
            } catch (Exception e){
                Toast.makeText(getActivity(), e+"", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    public void takePicture() throws CameraAccessException {
        finalImage = null;
        if (cameraDevice == null){
            Log.e(TAG, "cameraDevice is null");
            //return null;
        }

        //CameraPreview cameraPreview = new CameraPreview()

        CameraManager manager = getCameraManager();

        CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
        Size[] jpegSize = null;

        jpegSize = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);

        int width = 100;
        int height =100;

        if (jpegSize!=null && jpegSize.length>0){
            width = jpegSize[0].getWidth();
            height = jpegSize[0].getHeight();
        }

        ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
        List<Surface> outputSurface = new ArrayList<>(2);
        outputSurface.add(reader.getSurface());

        outputSurface.add(new Surface(imageView.getSurfaceTexture()));

        final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        captureBuilder.addTarget(reader.getSurface());
        captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        //int rotation = getWindowManager().getDefaultDisplay().getRotation();
        captureBuilder.set(CaptureRequest.CONTROL_MODE, 50);

        // File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
        //Environment.DIRECTORY_PICTURES), "CameraDemo");
       /* if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Toast.makeText(MainActivity.this, "This directory does not exist", Toast.LENGTH_LONG).show();
                return;
            }
        }

*/


        //filepath = new File(mediaStorageDir.getPath() + File.separator +
        //        "IMG_"+ timeStamp + ".jpg");
        filepath = getOutputMediaFile();

        ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = null;

                image = reader.acquireLatestImage();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                bytes = new byte[buffer.capacity()];

                try {

                    //b.compress(Bitmap.CompressFormat.JPEG, 100, null);
                    //finalImage = b;
                    // pos = imageAnalyzer.analyze(imageView);


                } catch (Exception e){
                    Toast.makeText(getActivity(), e+"6465468465", Toast.LENGTH_LONG).show();
                } finally {
                    if (image != null){
                        image.close();
                    }
                }

            }
        };

        reader.setOnImageAvailableListener(readerListener, backgroundHandler);

        final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureCompleted( CameraCaptureSession session,  CaptureRequest request,  TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);
                //Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_LONG).show();
                //Toast.makeText(MainActivity.this, filepath+"", Toast.LENGTH_LONG).show();
                try {
                    startCameraPreview();
                } catch (Exception e){
                    Toast.makeText(getActivity(), e+"", Toast.LENGTH_LONG).show();
                }



            }
        };


        cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured( CameraCaptureSession session) {
                try {
                    session.capture(captureBuilder.build(), captureListener, backgroundHandler);
                } catch (Exception e){
                    Toast.makeText(getActivity(), e+"", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onConfigureFailed(CameraCaptureSession session) {

            }
        },  backgroundHandler);
        //return finalImage;
    }

    public void openCamera(CameraManager cameraManager) throws CameraAccessException, SecurityException{
        //CameraManager cameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);

        cameraId = cameraManager.getCameraIdList()[0];

        CameraCharacteristics cc = cameraManager.getCameraCharacteristics(cameraId);
        StreamConfigurationMap map = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        imageDimensions = map.getOutputSizes(SurfaceTexture.class)[0];

        try {
            //int permissionCheck = this.checkSelfPermission("android.permission.CAMERA");
            //checkPermission("android.permission.CAMERA", null, null);
            cameraManager.openCamera(cameraId, stateCallBack, null);
        } catch (Exception e){
            Toast.makeText(getActivity(), e+"", Toast.LENGTH_LONG).show();
            System.out.println(e);
        }
    }


    private void startCameraPreview() throws  CameraAccessException{
        SurfaceTexture texture = imageView.getSurfaceTexture();
        texture.setDefaultBufferSize(imageDimensions.getWidth(), imageDimensions.getHeight());

        Surface surface = new Surface(texture);

        captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

        captureRequestBuilder.addTarget(surface);

        cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(CameraCaptureSession session) {
                if(cameraDevice == null){
                    return;
                }

                cameraSession = session;
                try {
                    updatePreview();
                } catch (Exception e){
                    Toast.makeText(getActivity(), e+"", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onConfigureFailed(CameraCaptureSession session) {

            }
        }, null);

    }

    private void updatePreview() throws CameraAccessException{
        if(cameraDevice == null){
            return;
        }

        captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_ANTIBANDING_MODE_AUTO);
        cameraSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);
    }

    //@Override
    protected void onResume() {
        //super.onResume();
        startBackgroundThread();

        if(imageView.isAvailable()){
            try {
                openCamera(getCameraManager());
            } catch (CameraAccessException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), e+"", Toast.LENGTH_LONG).show();
            }
        } else {
            imageView.setSurfaceTextureListener(surfaceTextureListener);
        }

    }

    private void startBackgroundThread(){
        handlerThread = new HandlerThread("Camera background");

        handlerThread.start();

        backgroundHandler = new Handler((handlerThread.getLooper()));
    }

    private void stopBackgroundThread() throws InterruptedException{
        handlerThread.quitSafely();
        handlerThread.join();

        backgroundHandler = null;
        handlerThread = null;

    }



    protected void onPause(){
        //super.onPause();
        try {
            stopBackgroundThread();
        } catch (Exception e) {
            Toast.makeText(getActivity(), e+"", Toast.LENGTH_LONG).show();
            System.out.println(e);
        }

    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraCaptureApp");
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    public Activity getActivity(){
        FtcRobotControllerActivity ftcRobotControllerActivity = FtcRobotControllerActivity.ftcApp;

        final Activity activity = ftcRobotControllerActivity.getActivity();
        return activity;
    }

    public CameraManager getCameraManager(){
        FtcRobotControllerActivity ftcRobotControllerActivity = FtcRobotControllerActivity.ftcApp;
        CameraManager manager = ftcRobotControllerActivity.getCameraManager();
        return manager;
    }

}
