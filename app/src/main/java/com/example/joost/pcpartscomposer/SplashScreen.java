package com.example.joost.pcpartscomposer;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class SplashScreen extends AppCompatActivity {


    RingProgressBar ringProgressBar;

    int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ringProgressBar = (RingProgressBar) findViewById(R.id.progress_bar);


        int secondsDelayed = 2;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent mainIntent = new Intent(SplashScreen.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, secondsDelayed * 1000);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                //do something
            }
        }, 2000);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0 ; i<100;i++){
                    try {
                        Thread.sleep(20);
                        ringProgressBar.setProgress(progress);
                        progress++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }
}
