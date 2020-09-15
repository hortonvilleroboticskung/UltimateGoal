//package org.firstinspires.ftc.teamcode;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.ColorMatrix;
//import android.graphics.ColorMatrixColorFilter;
//import android.graphics.Matrix;
//import android.graphics.Rect;
//import android.media.Image;
//import android.os.Environment;
//import android.util.Log;
//import android.view.TextureView;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
////import boofcv.alg.enhance.EnhanceImageOps;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//
//import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
//import org.jcodec.movtool.streaming.tracks.ToAACTrack;
//
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.OutputStream;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.concurrent.TimeUnit;
//
//import boofcv.alg.InputSanityCheck;
//import boofcv.alg.distort.DistortImageOps;
//import boofcv.alg.filter.binary.BinaryImageOps;
////import boofcv.alg.filter.binary.GThresholdImageOps;
//import boofcv.alg.filter.binary.ThresholdImageOps;
////import boofcv.alg.filter.blur.GBlurImageOps;
////import boofcv.alg.misc.ImageMiscOps;
////import boofcv.alg.sfm.DepthSparse3D;
//import boofcv.alg.filter.binary.impl.ImplBinaryImageOps;
//import boofcv.alg.filter.binary.impl.ImplBinaryImageOps_MT;
//import boofcv.alg.interpolate.InterpolationType;
//import boofcv.alg.misc.ImageMiscOps;
//import boofcv.android.ConvertBitmap;
////import boofcv.io.image.ConvertBufferedImage;
////import boofcv.struct.image.GrayF32;
//import boofcv.concurrency.BoofConcurrency;
//import boofcv.io.image.UtilImageIO;
//import boofcv.struct.border.BorderType;
//import boofcv.struct.image.GrayU8;
////import boofcv.struct.image.ImageType;
//import boofcv.struct.image.Planar;
//
//
//public class ImageAnalyzer extends OpMode {
//    public static  int height = 200;
//    public static  int width = 200;
//    public static  int threshold = 0;
//
//    public Bitmap bitmap;
//
//    public ImageAnalyzer(int a, int b){}
//    @Override
//    public void init() {
//    }
//    @Override
//    public void loop() {
//
//    }
//
//    public int[] analyze(File filename) {
//        telemetry.addData("ImageAnalyzer: ", "true");
//        while(!filename.exists() || !filename.canWrite());
//        String filenameString = filename.getPath();
//        Bitmap image = null;
//        try {
//            while(image == null) {
//                image = BitmapFactory.decodeFile(filenameString);
//                TimeUnit.MILLISECONDS.sleep(10);
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        image = BitmapFactory.decodeFile(filenameString);//filename.getAbsolutePath()
//        Log.v("IMAGE", ""+(image==null)+":"+filename);
//        float degrees = 90;//rotation degree
//        Matrix matrix = new Matrix();
//        matrix.setRotate(degrees,image.getWidth()/2,image.getHeight()/2);
//        Bitmap nimage = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
//
//        width = nimage.getWidth();//432
//        height = nimage.getHeight();//332
//
//        Bitmap croppedImg = cropImg(nimage);
//
//        //saveImg(croppedImg, new File("C:\\Users\\Sam\\Pictures\\testImgCrop.jpg"));
////        saveImg(blurImg(croppedImg), new File("C:\\Users\\Sam\\Pictures\\testImgCrop.jpg"));
//
//
//        //return -1;
//        return blurImg(croppedImg);
//    }
//    /*public static void saveImg(BufferedImage image, File filePath){
//        try {
//            if(!filePath.exists()) filePath.createNewFile();
//            ImageIO.write(image, "jpg", filePath);//code playground
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//    }*/
//    public static Bitmap cropImg(Bitmap image){
//        Bitmap croppedImg = image;
//        //Bitmap bmp=BitmapFactory.decodeResource(getResources(), R.drawable.xyz);
//
//        //Bitmap croppedImg =Bitmap.createBitmap(image, 20,100,width, height);
//        save(croppedImg);
//        //saveImg(croppedImg, new File("C:\\Users\\Sam\\Pictures\\testImgCrop.jpg"));
//        //save(croppedImg, context);
//        return croppedImg;
//    }
//
//    public static int[] blurImg(Bitmap bitmap){
//
//        int x = 0;
//        int y = 0;
//
//
//        //Planar<GrayU8> layers = ConvertBitmap.bitmapToPlanar(bitmap, null, GrayU8.class, null);
////        DistortImageOps.rotate(layers, layers, BorderType.ZERO, InterpolationType.POLYNOMIAL4, 270);
//        //for (int i = 0; i<layers.getNumBands();i++) ImageMiscOps.rotateCW(layers.getBand(i), layers.getBand(i));
//        //ConvertBitmap.planarToBitmap(layers, bitmap, null);
//        bitmap = Bitmap.createBitmap(bitmap,50, 300,190,50);
//        //save(bitmap);
//        width = bitmap.getWidth();
//        height = bitmap.getHeight();
//        //Planar<GrayU8> layers = ConvertBufferedImage.convertFromPlanar(bufferedImage, null, true, GrayU8.class);
//          //GrayU8 blueLayer = layers.getBand(2);
////        GrayU8 redLayer = layers.getBand(0);
//
//        //System.out.println("Width: " + blueLayer.getWidth() + " Height: " + blueLayer.getHeight());
//
//        //GrayU8 blueSubImage = blueLayer.subimage(x, y, x+width, y+height);
//
//
//        /*Planar<GrayU8> blueImage = new Planar<>(GrayU8.class, 3);
//        bufferedImageFromLayers(blueImage, blueLayer, "blueImgLayer");*/
//
//        /*Planar<GrayU8> redImage = new Planar<>(GrayU8.class, 3);
//        bufferedImageFromLayers(redImage, redLayer, "redImgLayer");*/
//        /*blueImage.setWidth(blueSubImage.getWidth());
//        blueImage.setHeight(blueSubImage.getHeight());
//
//        blueImage.setBand(0,blueSubImage);
//        blueImage.setBand(1,blueSubImage);
//        blueImage.setBand(2,blueSubImage);
//
//        BufferedImage bufferedImgBlue = ConvertBufferedImage.convertTo(blueSubImage, null, true);
//        saveImg(bufferedImgBlue, new File("C:\\Users\\Sam\\Pictures\\blueImgLayer.jpg"));*/
//
//        //System.out.println("Width: " + blueLayer.getWidth() + " Height: " + blueLayer.getHeight());
//
//
//       //GrayU8  blueThresh = new GrayU8(width, height);
////                redThresh = new GrayU8(width, height),
////                dilated = new GrayU8(width , height),
////                eroded = new GrayU8(width, height);
//        //System.out.println(blueSubImage.getWidth() + " "+ blueSubImage.getHeight());
//
//        /*
//         *
//         * crashes the app when ever i run the bluethresh, redthresh and overall
//         *
//         * */
//        //int threshold = 140;
////        blueThresh = ThresholdImageOps.threshold(blueLayer, blueThresh, threshold, true);
////        redThresh = ThresholdImageOps.threshold(redLayer, redThresh, threshold, false);
//
//        //blueThresh = threshold(blueLayer);
//        //redThresh = threshold(redLayer);
//
//        //GrayU8 overall = new GrayU8(width, height);
//        save(bitmap);
//        int xAverages = 0;
//        int count = 0;
//        for (int p = 0; p < width; p+=1) {
//            for (int e = 0; e < height; e+=1) {
//                //textView.setText("asdlfjalsdjfk;asdfjasdlkfj");
//
//
//                // get pixel color
//                int pixel = bitmap.getPixel(p, e);
//                //int A = Color.alpha(pixel);
//                int R = Color.red(pixel);
//                int G = Color.green(pixel);
//                int B = Color.blue(pixel);
//                int gray = (int) (4 * R + 0 * G + -8 * B);//(R,G,B)->(0.2989,0.5870,0.1140)
//                int average = G+R+B;
//                // use 128 as threshold, above -> white, below -> black
//                if (R > 140 && B < 140) {
//                    count++;
//                    xAverages += p;
//                    gray = 255;
//                }else
//                    gray = 0;
//                // set new pixel color to output bitmap
//                bitmap.setPixel(p, e, Color.argb(255, gray, gray, gray));
//            }
//        }
//
//
//        save(bitmap);
//        if(count == 0) return new int[]{-1, count};
//        xAverages = xAverages / count;
//
//
//        //textView.setText(xAverages+"");
//
//        int pos = 666;
//
//        if(xAverages >= 0 && xAverages < 4*width/11) {
//            //Toast.makeText(context, "2", Toast.LENGTH_LONG).show();
//            pos =  2;
//        }
//        if(xAverages >= 4*width/11 && xAverages < 5*width/11) {
//            //Toast.makeText(context, "3", Toast.LENGTH_LONG).show();
//            pos =  3;
//        }
//        if(xAverages >= 5*width/11 && xAverages <= width){
//            //Toast.makeText(context, "1", Toast.LENGTH_LONG).show();
//            pos =  1;
//        }
//        int[] returned = {pos, xAverages};
//        return returned;
//    }
//    public static Bitmap save(Bitmap bitmap){
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(getOutputMediaFile());
//            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
//            fos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        /*try (OutputStream out = new FileOutputStream(getOutputMediaFile())){
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//
//        } catch (Exception e){
//            //Toast.makeText(FtcRobotControllerActivity.this, e+"", Toast.LENGTH_LONG).show();
//        }*/
//        return bitmap;
//    }
//    private static File getOutputMediaFile(){
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "CameraCaptureAppIATests");
//        if (!mediaStorageDir.exists()){
//            if (!mediaStorageDir.mkdirs()){
//                return null;
//            }
//        }
//
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        return new File(mediaStorageDir.getPath() + File.separator +
//                "IMG_"+ timeStamp + "__"+threshold+ ".jpg");
//    }
//}
//
//
