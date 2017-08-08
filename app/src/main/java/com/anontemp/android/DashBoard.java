package com.anontemp.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.anontemp.android.misc.AnonDialog;
import com.anontemp.android.misc.DialogListener;
import com.anontemp.android.misc.Helper;
import com.anontemp.android.misc.OnSwipeTouchListener;
import com.anontemp.android.model.Region;
import com.anontemp.android.model.Tweet;
import com.anontemp.android.model.User;
import com.anontemp.android.view.AnonTView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class DashBoard extends FullscreenController implements View.OnClickListener, DialogListener {

    public static final int EDIT = 0;
    public static final int POST = 1;
    public static final int TWEET_LENGHT = 140;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 3;
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
    private TextView tvCount;
    private TextView tvLocation;
    private Switch commentSwitch;
    private SeekBar ttlSlider;
    private AnonTView ttlText;
    private AnonTView commentText;
    private RecyclerView moodView;

    private BroadcastReceiver geofenceChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case GeofenceTransitionsIntentService.ACTION_ENTERED:
                    String geofenceId = intent.getStringExtra(Constants.GEOFENCE_ID);
                    tvLocation.setText(geofenceId);
                    break;
//                case GeofenceTransitionsIntentService.ACTION_EXIT:
//                    tvLocation.setText(R.string.location_default);
//                    break;


            }


        }
    };
    private List<Region> regions;
    private boolean keyboardListenersAttached = false;
    private ViewGroup rootLayout;
    private Snackbar snackbar;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {

            Rect r = new Rect();
            rootLayout.getWindowVisibleDisplayFrame(r);

            int screenHeight = rootLayout.getRootView().getHeight();
            int heightDifference = screenHeight - (r.bottom - r.top);

            if (heightDifference < 300) {
                onHideKeyboard();
            } else {
                onShowKeyboard();
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    protected int init() {
        return R.layout.activity_board;
    }

    private void deleteUser() {
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    AnonApp.get().getSharedPreferences().edit().remove(Helper.PASSWORD).remove(Helper.EMAIL).remove(Helper.UUID).apply();


                    Snackbar snackbar = Helper.getSnackBar(getString(R.string.user_deleted), DashBoard.this);
                    snackbar.addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            Intent intent = new Intent(DashBoard.this, Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Helper.downToUpTransition(DashBoard.this);
                            super.onDismissed(transientBottomBar, event);

                        }
                    }).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachKeyboardListeners();

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("users");

        dbRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                if (currentUser == null) {
                    AuthCredential credential = Helper.getAuthCredential();

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d(Helper.TAG, "User re-authenticated.");
                                    deleteUser();
                                }
                            });

                    return;
                }
                setPostAuthUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        setViews();


        database.getReference("Regions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                regions = new ArrayList<>();
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    Region region = shot.getValue(Region.class);
                    regions.add(region);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        boardInput.setOnClickListener(this);
        ivPost.setOnClickListener(this);
        boardInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int count = TWEET_LENGHT - editable.length();

                if (count < 0) {
                    tvCount.setText("0");
                    tvCount.setTextColor(Color.RED);
                } else if (count < 141 && count > 50) {
                    tvCount.setText(String.valueOf(count));
                    tvCount.setTextColor(ContextCompat.getColor(DashBoard.this, android.R.color.holo_blue_light));
                } else if (count >= 0 && count < 51) {
                    tvCount.setTextColor(Color.YELLOW);
                    tvCount.setText(String.valueOf(count));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(GeofenceTransitionsIntentService.ACTION_ENTERED);
        intentFilter.addAction(GeofenceTransitionsIntentService.ACTION_EXIT);
        LocalBroadcastManager.getInstance(this).registerReceiver(geofenceChangeReceiver,
                intentFilter);
        if (ivPost != null) {
            ivPost.setImageDrawable(ContextCompat.getDrawable(DashBoard.this, R.mipmap.ic_pencil));
            ivPost.setEnabled(true);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(geofenceChangeReceiver);
    }

    protected void onShowKeyboard() {
        showSnackbar(R.string.empty, R.string.done,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        inputMethodManager.hideSoftInputFromWindow(boardInput.getWindowToken(), 0);
                    }
                });
    }

    protected void onHideKeyboard() {
        if (snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
    }

    protected void attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return;
        }

        rootLayout = findViewById(R.id.rootLayout);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        keyboardListenersAttached = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (keyboardListenersAttached) {
            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.messageBoard:
                Intent intent = new Intent(DashBoard.this, MessageBoard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

                    saveAndPost();
                }
                break;
            case R.id.create_account:
                intent = new Intent(DashBoard.this, CreateAccount.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                Helper.downToUpTransition(DashBoard.this);
                break;
            case R.id.genderSwitch:
                if (currentUser.getUsername().equals("?")) {
                    commonDialog = AnonDialog.newInstance(0, R.string.create_account_dialog, R.string.yes, R.string.no);
                    commonDialog.show(getSupportFragmentManager(), null);

                } else {
                    genderHint.setText(genderSwitch.isChecked() ? R.string.gender_hint_on : R.string.gender_hint_off);
                    setGenderImage();
                }
                break;
            case R.id.snapshot:
            case R.id.ivGlobe:
                intent = new Intent(DashBoard.this, Snapshot.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                Helper.downToUpTransition(DashBoard.this);
                break;

        }
    }

    private void saveAndPost() {

        if (!validate()) {
            return;
        }

        ivPost.setEnabled(false);
        showProgressSnowboard(R.string.loading);
        ivPost.setImageDrawable(ContextCompat.getDrawable(DashBoard.this, R.mipmap.ic_lock));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.US);
        String dateString = sdf.format(new Date());


        Tweet tweet = new Tweet();
        String regionText = tvLocation.getText().toString();
        tweet.setRegionName(regionText);
        tweet.setRegionId(getRegionId(regionText));
        tweet.setMoodText(Constants.MOODS_IMAGE.get((Integer) ivMood.getTag()));
        tweet.setTweetId(UUID.randomUUID().toString());
        tweet.setUserId(currentUser.getUid());
        tweet.setUsername(genderSwitch.isChecked() ? currentUser.getUsername() : "?");
        tweet.setFirstName(currentUser.getFirstName());
        tweet.setTweetText(boardInput.getText().toString());
        tweet.setTweetVotes(0);
        tweet.setDate(dateString);
        tweet.setCountDown(1800);
        tweet.setLocation("-26.1829922153333,28.1404067522872");
        tweet.setAllowComment(false);

        DatabaseReference tweetRef = database.getReference("Tweets").push();
        tweetRef.setValue(tweet.toMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                hideProgressDialog();
                if (databaseError == null) {
                    Intent intent = new Intent(DashBoard.this, MessageBoard.class);
                    startActivity(intent);
                    Helper.downToUpTransition(DashBoard.this);

                }
            }
        });


    }

    private String getRegionId(String regionName) {
        for (Region region : regions) {
            if (regionName.equals(region.getRegionText())) {
                return region.getRegionId();
            }
        }

        Region newRegion = new Region(UUID.randomUUID().toString(), regionName);
        DatabaseReference regionRef = database.getReference("Regions").push().getRef();
        regionRef.setValue(newRegion.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Log.e(Helper.TAG, task.getException().getLocalizedMessage());
                }
            }
        });

        return newRegion.getRegionId();


    }

    private boolean validate() {
        if (boardInput.getText().length() > 140) {
            Helper.showSnackbar(getString(R.string.message_long), DashBoard.this);
            return false;
        }
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
        tvCount = findViewById(R.id.count);
        tvLocation = findViewById(R.id.location);
        ivMood.setTag(R.mipmap.ic_face);
        if (getIntent() != null && getIntent().getStringExtra(MapsActivity.REGION_NAME) != null) {
            tvLocation.setText(getIntent().getStringExtra(MapsActivity.REGION_NAME));
        }
        findViewById(R.id.snapshot).setOnClickListener(this);
        findViewById(R.id.ivGlobe).setOnClickListener(this);
        findViewById(R.id.rootLayout).setOnTouchListener(new OnSwipeTouchListener(DashBoard.this) {
            public void onSwipeBottom() {
                inputMethodManager.hideSoftInputFromWindow(boardInput.getWindowToken(), 0);
            }
        });

        ttlSlider = findViewById(R.id.sliderTTL);
        ttlText = findViewById(R.id.ttlHint);
        commentText = findViewById(R.id.commentHint);
        commentSwitch = findViewById(R.id.commentSwitch);
        moodView = findViewById(R.id.moodView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        moodView.setLayoutManager(layoutManager);
        setProgressText(ttlSlider.getProgress());
        ttlSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                final Animation in = new AlphaAnimation(0.0f, 1.0f);
                in.setDuration(100);

                final Animation out = new AlphaAnimation(1.0f, 0.0f);
                out.setDuration(100);

                AnimationSet as = new AnimationSet(true);
                as.addAnimation(out);
                in.setStartOffset(100);
                as.addAnimation(in);
                int progress = seekBar.getProgress();
                ttlText.startAnimation(as);
                setProgressText(progress);


            }
        });


    }

    private void setProgressText(int progress) {
        if (progress == 0) {
            ttlText.setTextColor(Color.RED);
            ttlText.setText(getString(R.string.ttl_hint, "30 mins"));
        } else if (progress == 1) {
            ttlText.setTextColor(Color.BLACK);
            ttlText.setText(getString(R.string.ttl_hint, "1 hour"));

        } else {
            String d = String.valueOf(progress) + " hours";
            ttlText.setTextColor(Color.BLACK);
            ttlText.setText(getString(R.string.ttl_hint, d));
        }
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
        String tag = (String) iv.getTag();
        int identifier = getResources().getIdentifier(tag, "mipmap", getPackageName());
        ivMood.setTag(identifier);

    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            Log.i(Helper.TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(DashBoard.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(Helper.TAG, "Requesting permission");
            ActivityCompat.requestPermissions(DashBoard.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        View container = findViewById(android.R.id.content);
        if (container != null && snackbar == null) {

            snackbar = Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(mainTextStringId),
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(actionStringId), listener);
            snackbar.setActionTextColor(Color.BLUE);
            View snackbarView = snackbar.getView();
            int snackbarTextId = android.support.design.R.id.snackbar_text;
            TextView textView = snackbarView.findViewById(snackbarTextId);
            textView.setTextColor(Color.BLACK);
            snackbarView.setBackgroundColor(ContextCompat.getColor(DashBoard.this, android.R.color.white));
            snackbar.show();
        } else if (snackbar != null && !snackbar.isShown()) {
            snackbar.show();
        }
    }

    @Override
    public void onYes() {
        commonDialog.dismiss();
        Intent intent = new Intent(DashBoard.this, CreateAccount.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        Helper.downToUpTransition(DashBoard.this);
    }

    @Override
    public void onCancel() {
        commonDialog.dismiss();
        genderSwitch.setChecked(false);
        genderHint.setText(R.string.gender_hint_disabled);
    }
}
