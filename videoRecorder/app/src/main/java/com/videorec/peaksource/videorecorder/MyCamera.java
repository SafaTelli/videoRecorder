package com.videorec.peaksource.videorecorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyCamera extends AppCompatActivity  implements View.OnClickListener,SurfaceHolder.Callback, Camera.ShutterCallback, Camera.PictureCallback{

    Camera mCamera;
    SurfaceView mPreview;
    SurfaceHolder holder ;
    String filePath ;
    int currentCameraId = 1;
    MediaRecorder recorder;
    boolean recording = false;
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);







        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        recorder = new MediaRecorder();
        setContentView(R.layout.activity_my_camera);

        initRecorder();

        mPreview = (SurfaceView)findViewById(R.id.preview);
        holder = mPreview.getHolder() ;
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCamera = Camera.open(currentCameraId);
        mPreview.setClickable(true);
        mPreview.setOnClickListener(this);


    }


    private void prepareRecorder() {
        recorder.setPreviewDisplay(holder.getSurface());

        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }



    @SuppressWarnings("deprecation")
    private void initRecorder() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            /*if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                Log.d("Home", "Already granted access");
                //initializeView(v);
            }
            if (this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                   )
            {
               requestPermissions( new String[] {Manifest.permission.CAMERA},MY_CAMERA_REQUEST_CODE);
            }*/


            if (!checkIfAlreadyhavePermission())
            {
                requestForSpecificPermission();
            }
            else
            {

                recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

                CamcorderProfile cpHigh = CamcorderProfile
                        .get(CamcorderProfile.QUALITY_HIGH);
                recorder.setProfile(cpHigh);
                // recorder.setCamera(Camera.open(findFrontFacingCameraID()));
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES),"video" + System.currentTimeMillis()+ ".mp4");
                if (file.exists()) {
                    file.delete();
                }
                Log.d("hhhh",file.getAbsolutePath());


                recorder.setOutputFile(file.getAbsolutePath());
                //recorder.setOutputFile("/sdcard/video.mp4");
                recorder.setMaxDuration(10000); // 50 seconds
                recorder.setMaxFileSize(5000000); // Approximately 5 megabytes
            }





        }


        else {


            recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            CamcorderProfile cpHigh = CamcorderProfile
                    .get(CamcorderProfile.QUALITY_HIGH);
            recorder.setProfile(cpHigh);
            // recorder.setCamera(Camera.open(findFrontFacingCameraID()));
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES),"video" + System.currentTimeMillis()+ ".mp4");
            if (file.exists()) {
                file.delete();
            }
            Log.d("hhhh",file.getAbsolutePath());

           // int id = findFrontFacingCameraID();
           // Log.d("hh",id+"");
            recorder.setOutputFile(file.getAbsolutePath());
            //recorder.setOutputFile("/sdcard/video.mp4");
            recorder.setMaxDuration(10000); // 50 seconds
            recorder.setMaxFileSize(5000000); // Approximately 5 megabytes
        }

    }


    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.RECORD_AUDIO
                ,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }



    @Override
    public void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCamera.release();
        Log.d("CAMERA","Destroy");
    }

    /*
    public void onCancelClick(View v) {

        mCamera.stopPreview();
        mCamera.release();
        if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        mCamera = Camera.open(currentCameraId);
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, info);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break; //Natural orientation
            case Surface.ROTATION_90: degrees = 90; break; //Landscape left
            case Surface.ROTATION_180: degrees = 180; break;//Upside down
            case Surface.ROTATION_270: degrees = 270; break;//Landscape right
        }
        int rotate = (info.orientation - degrees + 360) % 360;

        //STEP #2: Set the 'rotation' parameter
        Camera.Parameters params = mCamera.getParameters();
        params.setRotation(rotate);
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mCamera.setParameters(params);
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }
*/
   /* public void onSnapClick(View v) {
        mCamera.takePicture(this, null, null, this);
    }
*/
    @Override
    public void onShutter() {
        Toast.makeText(this, "Click!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        //Here, we chose internal storage
        FileOutputStream fos = null;
        try {
            filePath = "/sdcard/test.jpg";
            fos = new FileOutputStream(
                    filePath);
            fos.write(data);
            fos.close();
            //Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
        } finally {
            Intent i = getIntent();
            i.putExtra("Path",filePath);
            setResult(RESULT_OK, i);
            finish();
        }
        camera.startPreview();
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break; //Natural orientation
            case Surface.ROTATION_90: degrees = 90; break; //Landscape left
            case Surface.ROTATION_180: degrees = 180; break;//Upside down
            case Surface.ROTATION_270: degrees = 270; break;//Landscape right
        }
        int rotate = (info.orientation - degrees + 360) % 360;

        //STEP #2: Set the 'rotation' parameter
        Camera.Parameters params = mCamera.getParameters();
        params.setRotation(rotate);
        mCamera.setParameters(params);
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        prepareRecorder();
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("PREVIEW","surfaceDestroyed");
        if (recording) {
            recorder.stop();
            recording = false;
        }
        recorder.release();
        finish();
    }

    public void onClick(View v) {
        if (recording) {
            recorder.stop();
            recording = false;

            // Let's initRecorder so we can record again
            initRecorder();
            prepareRecorder();
        } else {
            recording = true;
            recorder.start();
        }
    }





}
