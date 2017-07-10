package com.anontemp.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

public class DashBoard extends FullscreenController implements View.OnClickListener {

    private ImageView ivPost;
    private TextInputEditText boardInput;
    private ConstraintLayout postLayout;
    private ImageView ivGlobe;
    private Animation popOut;
    private Animation popIn;
    private Animation popBubble;
    private boolean startedOnce = false;

    @Override
    protected int init() {
        return R.layout.activity_board;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.messageBoard).setOnClickListener(this);

        setViews();

        boardInput.setOnClickListener(this);
        ivPost.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.messageBoard:
                Intent intent = new Intent(DashBoard.this, MessageBoard.class);
                startActivity(intent);
                Helper.downToUpTransition(DashBoard.this);
                break;
            case R.id.boardInput:
                animatePost();
                break;
            case R.id.ivPost:
                animatePost();
                boardInput.requestFocus();
                final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(boardInput, InputMethodManager.SHOW_IMPLICIT);
                break;

        }
    }

    private void animatePost() {

        if (startedOnce)
            return;


        ivPost.startAnimation(popIn);

        ivPost.setImageDrawable(ContextCompat.getDrawable(DashBoard.this, R.mipmap.ic_pushpin));
        ivPost.startAnimation(popOut);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ivGlobe.startAnimation(popBubble);
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(runnable);

        postLayout.setVisibility(View.VISIBLE);
        startedOnce = true;

    }

    private void setViews() {

        ivPost = (ImageView) findViewById(R.id.ivPost);
        boardInput = (TextInputEditText) findViewById(R.id.boardInput);
        boardInput.setFocusableInTouchMode(true);
        postLayout = (ConstraintLayout) findViewById(R.id.postLayout);
        ivGlobe = (ImageView) findViewById(R.id.ivGlobe);
        popIn = AnimationUtils.loadAnimation(DashBoard.this, R.anim.pop_in);
        popOut = AnimationUtils.loadAnimation(DashBoard.this, R.anim.pop_out);
        popBubble = AnimationUtils.loadAnimation(DashBoard.this, R.anim.pop_bubble);

    }

    public void iconClick() {

    }

}
