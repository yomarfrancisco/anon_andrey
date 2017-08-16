package com.anontemp.android;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class Splash extends FullscreenController {

    public static final int SPLASH_TIME_OUT = 3000;

    @Override
    protected int init() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(Splash.this, WhiteSplash.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(Splash.this, findViewById(R.id.tvLogo), "title");
                    startActivity(i, options.toBundle());
                } else {
                    startActivity(i);
                }


                finish();
            }
        }, SPLASH_TIME_OUT);

    }


}
