package codism.flashlight;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import butterknife.BindView;
import butterknife.ButterKnife;



public class MainActivity extends AppCompatActivity {



    TextView textView;
    @BindView(R.id.imageToggleButton)
    ToggleButton button;

    private static final String Value_ZERO = "0";
    private boolean isFlashOn = false;
    private Thread t;
    private StroboRunner stroboRunner;
    private boolean stopFlicker = false;
    public Context mContext;

    private CameraManager mCameraManager;
    private String cameraId;
    ImageView img;
    Animation animFadeIn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        ButterKnife.bind(this);

        checkForResources();


        img = (ImageView) findViewById(R.id.img);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    flashFlicker();
                    animation();


                } else {
                    Toast.makeText(MainActivity.this, "Press the above button", Toast.LENGTH_SHORT).show();

                }
            }

        });
    }


    private void animation(){

        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        img.startAnimation(animFadeIn);
        animFadeIn.setRepeatMode(Animation.INFINITE);

    }

    private void cancelanimation(){
        img.clearAnimation();
    }



    private void checkForResources() {
        PackageManager pm = mContext.getPackageManager();

        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.NO_CAMERA_ERROR),
                    Toast.LENGTH_SHORT).show();
            return;
        } else {

            initialiseResourcesCamera2();
            setToggleButtonBehaviour();

        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initialiseResourcesCamera2() {

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            cameraId =  mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


    }


    private void setFlashOn(Boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {

                mCameraManager.setTorchMode(cameraId,enable);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }



    private void setToggleButtonBehaviour() {

        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isFlashOn = isChecked;
                if (!isChecked) {
                    //Enabled//

                    setFlashOn(false);
                    isFlashOn = false;
                    stopFlicker = true;
                    cancelanimation();



                } else

                {

                    setFlashOn(true);
                    isFlashOn = true;
                    stopFlicker = false;
                }
            }
        });
    }



    int s1= 150;
    private void flashFlicker() {
        if (isFlashOn) {

            stroboRunner = new StroboRunner();
            t = new Thread(stroboRunner);
            t.start();
            return;
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.SWITCH_FLASH_ON), Toast.LENGTH_SHORT).show();
            ;
        }
    }

    private class StroboRunner implements Runnable {

        public void run() {
            try {
                while (!stopFlicker) {

                    if (s1 != 0) {
                        setFlashOn(true);
                        Thread.sleep(s1);
                        setFlashOn(false);
                        Thread.sleep(s1);
                    } else {
                        setFlashOn(true);
                    }
                }

            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    private void releaseCamera(){
        setFlashOn(false);
        isFlashOn=false;
        textView.setText(Value_ZERO);
        button.setChecked(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();


    }


}


