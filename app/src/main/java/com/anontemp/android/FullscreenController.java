package com.anontemp.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.nekocode.emojix.Emojix;

/**
 * Created by jaydee on 04.07.17.
 */

public abstract class FullscreenController extends AppCompatActivity {
    @VisibleForTesting
    public ProgressDialog mProgressDialog;
    private View decorView;

    protected abstract int init();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Emojix.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(init());
        decorView = getWindow().getDecorView();
        hideSystemUI();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    hideSystemUI();
                                }
                            }, 5000);
                        }
                    }
                });

    }

    private void hideSystemUI() {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void showProgressDialog(int messageRes) {
        showProgressDialog(getString(messageRes));
    }

    public void showProgressDialog(CharSequence sequence) {
        showProgressDialog(sequence.toString());
    }

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, R.style.MyTheme);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);


        }

        mProgressDialog.show();
        mProgressDialog.setContentView(R.layout.progress_bar);

        final ImageView img_loading_frame = mProgressDialog.findViewById(R.id.iv_frame_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });

        TextView tv_progress_message = mProgressDialog.findViewById(R.id.tv_progress_message);
        tv_progress_message.setTypeface(FontCache.getTypeface("CFSnowboardProjectPERSONAL.ttf", this));
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    public void showAlert(Object message, Object title) {
        View d = LayoutInflater.from(this).inflate(R.layout.c_dial, null);
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setView(d);
        final AlertDialog dialog = build.create();

        LinearLayout iv = d.findViewById(R.id.dialLayout);
        TextView tv = iv.findViewById(R.id.text);
        if (message instanceof Integer)
            tv.setText((Integer) message);
        if (message instanceof String)
            tv.setText((String) message);

        if (title != null) {
            if (title instanceof Integer)
                dialog.setTitle((Integer) title);
            if (title instanceof String)
                dialog.setTitle((String) title);
        }

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isShown()) {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    public void showAlert(Object message) {
        showAlert(message, null);
    }


}
