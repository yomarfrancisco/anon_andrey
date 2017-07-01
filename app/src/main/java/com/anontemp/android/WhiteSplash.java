package com.anontemp.android;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;

import static com.anontemp.android.Splash.SPLASH_TIME_OUT;

public class WhiteSplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_splash);


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(WhiteSplash.this, MainActivity.class);
                    startActivity(i);

                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
