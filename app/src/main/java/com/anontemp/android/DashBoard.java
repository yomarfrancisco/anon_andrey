package com.anontemp.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.Switch;
import android.widget.TextView;

import com.anontemp.android.com.anontemp.android.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DashBoard extends FullscreenController implements View.OnClickListener {

    public static final int EDIT = 0;
    public static final int POST = 1;
    private InputMethodManager inputMethodManager;
    private ImageView ivPost;
    private TextInputEditText boardInput;
    private ConstraintLayout postLayout;
    private ImageView ivGlobe;
    private Animation popOut;
    private Animation popIn;
    private Animation popBubble;
    private ImageView ivMood;
    private boolean startedOnce = false;
    private ImageView gender;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private User currentUser;
    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    private Switch genderSwitch;
    private TextView genderHint;

    @Override
    protected int init() {
        return R.layout.activity_board;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("users");

        dbRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                setPostAuthUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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

                int tag = (int) ivPost.getTag();
                if (tag == EDIT) {
                    animatePost();
                    boardInput.requestFocus();
                    inputMethodManager.showSoftInput(boardInput, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    saveAndPost();
                }
                break;
            case R.id.create_account:
                intent = new Intent(DashBoard.this, CreateAccount.class);
                startActivity(intent);
                Helper.downToUpTransition(DashBoard.this);
                break;
            case R.id.genderSwitch:
                genderHint.setText(genderSwitch.isChecked() ? R.string.gender_hint_on : R.string.gender_hint_off);
                setGenderImage();
                break;

        }
    }


    private void saveAndPost() {


    }

    private boolean validate() {

        return true;
    }


    private void animatePost() {

        if (startedOnce)
            return;


        ivPost.startAnimation(popIn);

        ivPost.setImageDrawable(ContextCompat.getDrawable(DashBoard.this, R.mipmap.ic_pushpin));
        popOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                final Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        ivPost.startAnimation(popBubble);
                        handler.postDelayed(this, 5000);
                    }
                };
                handler.post(runnable);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
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
        ivPost.setTag(POST);

    }

    private void setViews() {

        ivPost = findViewById(R.id.ivPost);
        boardInput = findViewById(R.id.boardInput);
        boardInput.setFocusableInTouchMode(true);
        postLayout = findViewById(R.id.postLayout);
        ivGlobe = findViewById(R.id.ivGlobe);
        popIn = AnimationUtils.loadAnimation(DashBoard.this, R.anim.pop_in);
        popOut = AnimationUtils.loadAnimation(DashBoard.this, R.anim.pop_out);
        popBubble = AnimationUtils.loadAnimation(DashBoard.this, R.anim.pop_bubble);
        ivMood = findViewById(R.id.ivMood);
        gender = findViewById(R.id.gender);
        genderSwitch = findViewById(R.id.genderSwitch);
        genderHint = findViewById(R.id.genderHint);
        genderSwitch.setOnClickListener(this);
        findViewById(R.id.messageBoard).setOnClickListener(this);
        findViewById(R.id.create_account).setOnClickListener(this);
        ivPost.setTag(EDIT);


    }


    private void setPostAuthUI() {
        if (currentUser.getUsername().equals("?")) {
            findViewById(R.id.create_account).setVisibility(View.VISIBLE);
            findViewById(R.id.separator2).setVisibility(View.VISIBLE);
            return;
        }


        genderSwitch.setEnabled(true);
        genderSwitch.setChecked(true);
        genderHint.setTextColor(Color.BLACK);
        genderHint.setText(R.string.gender_hint_on);
        setGenderImage();


    }


    private void setGenderImage() {
        Drawable g;
        int res = R.mipmap.ic_question;
        if (genderSwitch.isChecked())
            switch (currentUser.getUsername()) {
                case "\uD83C\uDF51":
                    res = R.mipmap.ic_peach;
                    break;
                case "\uD83C\uDF46":
                    res = R.mipmap.ic_aubergine;
                    break;
                case "\uD83C\uDF3C":
                    res = R.mipmap.ic_blossom;
                    break;
            }
        g = ContextCompat.getDrawable(DashBoard.this, res);
        gender.setImageDrawable(g);
    }

    public void iconClick(View v) {

        if (!(v instanceof ImageView))
            return;

        ImageView iv = (ImageView) v;
        ivMood.setImageDrawable(iv.getDrawable());

    }

}
