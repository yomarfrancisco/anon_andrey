package com.anontemp.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.anontemp.android.misc.Helper;

public class WhiteSplash extends FullscreenController {
    @Override
    protected int init() {
        return R.layout.activity_white_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ImageView image = findViewById(R.id.ivHand);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.hand);


        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(WhiteSplash.this, MapsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Helper.downToUpTransition(WhiteSplash.this);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        image.startAnimation(animation);


    }


}
