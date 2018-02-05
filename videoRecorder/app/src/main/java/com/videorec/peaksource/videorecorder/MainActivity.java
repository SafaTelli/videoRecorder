package com.videorec.peaksource.videorecorder;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Socket;
import com.github.nkzawa.socketio.client.IO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,SurfaceHolder.Callback {

    MediaRecorder recorder;
    SurfaceHolder holder;
    boolean recording = false;
    Camera mCamera;
    int currentCameraId = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 29;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    com.github.nkzawa.socketio.client.Socket mSocket;

    APiInterface apiInterface ;


    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        recorder = new MediaRecorder();


        setContentView(R.layout.activity_main);
        initRecorder();

        SurfaceView cameraView = (SurfaceView) findViewById(R.id.holder);
        holder = cameraView.getHolder();
        holder.addCallback(this);


        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

       //mCamera = Camera.open(currentCameraId);

        cameraView.setClickable(true);
        cameraView.setOnClickListener(this);

        try {
            mSocket = IO.socket("http://chat.socket.io");
        } catch (URISyntaxException e) {}


        mSocket.on("start", onStartMessage);
        mSocket.connect();



    }



    private  Emitter.Listener onStartMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String id;
                    String duree;
                    try {
                        id = data.getString("id");
                        duree = data.getString("duree");
                    } catch (JSONException e) {
                        return;
                    }

                    // add the message to view
                  //  addMessage(username, message);
                    Log.d("kk",id+" "+ duree) ;
                }
            });
        }


    };


/*
    @Override
    public void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }
*/
    @Override
    public void onDestroy() {
        super.onDestroy();
       // mCamera.release();
      //  Log.d("CAMERA","Destroy");

        mSocket.disconnect();
        mSocket.off("new message", onStartMessage);
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
                        .get(findFrontFacingCameraID(),CamcorderProfile.QUALITY_HIGH);
                recorder.setProfile(cpHigh);
              // recorder.setCamera(Camera.open(findFrontFacingCameraID()));
              /*  File file = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES),"video" + System.currentTimeMillis()+ ".mp4");
                if (file.exists()) {
                    file.delete();
                }*/
               // Log.d("hhhh",file.getAbsolutePath());

                int id = findFrontFacingCameraID();
                Log.d("hh",id+"");
                String path = getExternalFilesDir(Environment.DIRECTORY_MOVIES)+"/video" + System.currentTimeMillis()+ ".mp4";
                Log.d("hh",path);
                recorder.setOutputFile(path);

                //recorder.setOutputFile("/sdcard/video.mp4");
                recorder.setMaxDuration(10000); // 50 seconds
                recorder.setMaxFileSize(5000000); // Approximately 5 megabytes
            }





        }


        else {


            recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            CamcorderProfile cpHigh = CamcorderProfile
                    .get(findFrontFacingCameraID(),CamcorderProfile.QUALITY_HIGH);
            recorder.setProfile(cpHigh);
            // recorder.setCamera(Camera.open(findFrontFacingCameraID()));
          /*  File file = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES),"video" + System.currentTimeMillis()+ ".mp4");
            if (file.exists()) {
                file.delete();
            }
            Log.d("hhhh",file.getAbsolutePath());*/

            int id = findFrontFacingCameraID();
            Log.d("hh",id+"");
            String path = getExternalFilesDir(Environment.DIRECTORY_MOVIES)+"/video" + System.currentTimeMillis()+ ".mp4";
            Log.d("hh",path);
            recorder.setOutputFile(path);

            //recorder.setOutputFile("/sdcard/video.mp4");
            recorder.setMaxDuration(10000); // 50 seconds
            recorder.setMaxFileSize(5000000); // Approximately 5 megabytes
        }

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

    public void surfaceCreated(SurfaceHolder holder) {
        prepareRecorder();

       /* try {
            mCamera.setPreviewDisplay(holder);
        } catch (Exception e) {
            e.printStackTrace();
        }*/







    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

      /*  Camera.CameraInfo info = new Camera.CameraInfo();
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


*/
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (recording) {
            recorder.stop();
            recording = false;
        }
        recorder.release();

        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

                if (grantResults.length> 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                    Log.d("Home", "Permission Granted");
                   // initializeView(v);
                } else {
                    Log.d("Home", "Permission Failed");
                    Toast.makeText(MainActivity.this.getBaseContext(), "You must allow permission record audio to your mobile device.", Toast.LENGTH_SHORT).show();
                    MainActivity.this.finish();





            // Add additional cases for other permissions you may have asked for
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




    @SuppressWarnings("deprecation")
    private int findFrontFacingCameraID() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++)
        {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d("hh", "Camera front found");
                cameraId = i;
                break;
            }
           /* else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
            {
                Log.d("hh", "Camera back found");
                cameraId = i;
                break;
            }*/
        }
        return cameraId;
    }



    @SuppressWarnings("deprecation")
    void setFrontCamera(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.set("camera-id", 2);
// (800, 480) is also supported front camera preview size at Samsung Galaxy S.
        parameters.setPreviewSize(640, 480);
        camera.setParameters(parameters);

    }



    @SuppressWarnings("deprecation")
    Camera getFrontFacingCamera() throws NoSuchElementException {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int cameraIndex = 0; cameraIndex < Camera.getNumberOfCameras(); cameraIndex++)
        {
            Camera.getCameraInfo(cameraIndex, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    return Camera.open(cameraIndex);
                }
                catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new NoSuchElementException("Can't find front camera.");}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return  true ;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_chnge) {

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


        return super.onOptionsItemSelected(item);
    }



    public void uplodadVideo ()
    {

        apiInterface = ApiConfig.getClient().create(APiInterface.class);
        Call<String> call = apiInterface.uploadVideo("");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });



    }
}
