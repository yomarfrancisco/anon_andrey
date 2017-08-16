package com.anontemp.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
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
import com.anontemp.android.view.AnonSnackbar;
import com.anontemp.android.view.AnonTView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
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
    public static final int GIF_REQUEST_CODE = 30;
    public static final String GIF_ADDED = "gifAdded";
    public static final String NO_GIF = "noGif";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static AnimationSet as;
    private static String mFileName = null;

    static {
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        final Animation out = new AlphaAnimation(1.0f, 0.0f);
        in.setDuration(100);
        out.setDuration(100);
        as = new AnimationSet(true);
        as.addAnimation(out);
        in.setStartOffset(100);
        as.addAnimation(in);
    }

    FirebaseStorage storage;
    long timeInMilliseconds = 0L;
    private String gifFileName;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    // Requesting permission to RECORD_AUDIO
    private String[] permissions = {android.Manifest.permission.RECORD_AUDIO};
    private InputMethodManager inputMethodManager;
    private ImageView ivPost;
    private TextInputEditText boardInput;
    private ConstraintLayout postLayout;
    private Animation popOut;
    private Animation popIn;
    private Animation popBubble;
    private AnonTView ivMood;
    private boolean startedOnce = false;
    private ImageView gender;
    private FirebaseAuth mAuth;
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
    private AnonTView moodHint;
    private RecyclerView moodView;
    private AnonTView tvGif;
    private ImageView gifLabel;
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
    private AnonSnackbar snackbar;
    private AnonTView timestamp;
    private boolean onceScrolled = false;
    private StorageReference mStorageRef;
    private AnonTView tvDone;
    private long startTime = 0L;
    private AnonTView timer;
    private AnonTView recordInfo;
    private Handler customHandler = new Handler();
    private AnonTView tvSound;
    private ImageView ivMic;
    private RecordState recordState = RecordState.NONE;
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            int secs = (int) (timeInMilliseconds / 1000);
            int mins = secs / 60;
            if (mins > 0) {
                stopRecording();
            }
            secs = secs % 60;
            int milliseconds = (int) (timeInMilliseconds % 100);
            timer.setText("" + String.format("%02d", secs) + ":"
                    + String.format("%02d", milliseconds));
            customHandler.postDelayed(this, 2);
        }
    };
    private View.OnTouchListener toucher = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (recordState == RecordState.PREPARED)
                        startRecording();
                    break;
                case MotionEvent.ACTION_UP:
                    if (recordState == RecordState.RECORDING)
                        stopRecording();
                    else {
                        view.setOnTouchListener(null);
                        view.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                switch (motionEvent.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        if (recordState == RecordState.RECORDING)
                                            stopRecording();
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        break;
                                }
                                return false;
                            }
                        });
                    }
                    break;
            }
            return false;
        }
    };
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

    private void updateSelectGifButton() {
        if (!TextUtils.isEmpty(gifFileName)) {
            gifLabel.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_gif_loaded));
            tvGif.setVisibility(View.VISIBLE);
        } else {
            gifLabel.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_gif));
            tvGif.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(Helper.TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(Helper.TAG, "Permission granted.");
                recordState = RecordState.PREPARED;

            } else {
                recordState = RecordState.ERROR;
            }
        }
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO
        );
        return permissionState == PackageManager.PERMISSION_GRANTED;

    }

    private void prepareRecording() {

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioChannels(1);
        mRecorder.setAudioSamplingRate(12000);
        mRecorder.setMaxDuration(60000);
        mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {
                if (i == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    stopRecording();
                    timer.setText("" + String.format("%02d", 60) + ":"
                            + String.format("%02d", 00));
                }
            }
        });

        try {
            mRecorder.prepare();

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
            recordState = RecordState.ERROR;
        }

    }

    private void startRecording() {
        prepareRecording();
        mRecorder.start();
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
        recordInfo.setVisibility(View.VISIBLE);
        recordInfo.setText(R.string.rec_on);
        timer.setVisibility(View.VISIBLE);
        recordState = RecordState.RECORDING;

    }

    private void stopRecording() {

        if (mRecorder == null)
            return;
        try {
            mRecorder.stop();

        } catch (Exception e) {
            Log.w(LOG_TAG, e.getMessage());
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            removeRecord();
            customHandler.removeCallbacks(updateTimerThread);
            return;
        }
        mRecorder.release();
        mRecorder = null;

        recordState = RecordState.COMPLETED;
        customHandler.removeCallbacks(updateTimerThread);

        tvSound.setVisibility(View.VISIBLE);
        ivMic.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_mic_disabled));
        ivMic.setOnTouchListener(null);

        recordInfo.startAnimation(as);
        recordInfo.setText(R.string.cancel);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    protected int init() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        return R.layout.activity_board;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachKeyboardListeners();


        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("users");
        mStorageRef = FirebaseStorage.getInstance().getReference();

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
                if (tvDone != null) {
                    tvDone.setEnabled(editable.length() > 0);
                }

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


        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/recording.m4a";
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            recordState = RecordState.PREPARED;
        }
    }

    private void requestPermissions() {
        Log.i(Helper.TAG, "Requesting permission");
        ActivityCompat.requestPermissions(DashBoard.this, permissions,
                REQUEST_RECORD_AUDIO_PERMISSION);

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
        showSnackbar();
    }

    protected void onHideKeyboard() {
        if (snackbar != null && snackbar.isShowing()) {
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
                if (isTempUser()) {
                    commonDialog = AnonDialog.newInstance(0, R.string.create_account_dialog, R.string.yes, R.string.no);
                    commonDialog.show(getSupportFragmentManager(), null);

                } else {
                    genderHint.setText(genderSwitch.isChecked() ? R.string.gender_hint_on : R.string.gender_hint_off);
                    setGenderImage();
                }
                break;
            case R.id.snapshot:
                intent = new Intent(DashBoard.this, Snapshot.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                Helper.downToUpTransition(DashBoard.this);
                break;
            case R.id.commentSwitch:
                commentText.setText(commentSwitch.isChecked() ? R.string.comments_on : R.string.comments_off);
                break;

        }
    }

    private boolean isTempUser() {
        return currentUser.getEmail().equals(getString(R.string.temp_mail));
    }

    private void saveAndPost() {

        if (!validate()) {
            return;
        }

        ivPost.setEnabled(false);
        showProgressSnowboard(R.string.loading, R.drawable.cat);
        ivPost.setImageDrawable(ContextCompat.getDrawable(DashBoard.this, R.mipmap.ic_lock));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.US);
        String dateString = sdf.format(new Date());


        Tweet tweet = new Tweet();
        String regionText = tvLocation.getText().toString();
        tweet.setRegionName(regionText);
        tweet.setRegionId(getRegionId(regionText));
//        tweet.setMoodText(Constants.MOODS_IMAGE.get((Integer) ivMood.getTag()));
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


        postLayout.setVisibility(View.VISIBLE);
        startedOnce = true;
        ivPost.setTag(POST);
        setTimestamp(ttlSlider.getProgress());

    }

    private void setViews() {

        ivPost = findViewById(R.id.ivPost);
        boardInput = findViewById(R.id.boardInput);
        boardInput.setFocusableInTouchMode(true);
        postLayout = findViewById(R.id.postLayout);
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
        if (getIntent() != null && getIntent().getStringExtra(MapsActivity.REGION_NAME) != null) {
            tvLocation.setText(getIntent().getStringExtra(MapsActivity.REGION_NAME));
        }
        findViewById(R.id.snapshot).setOnClickListener(this);
        findViewById(R.id.rootLayout).setOnTouchListener(new OnSwipeTouchListener(DashBoard.this) {
            public void onSwipeBottom() {
                inputMethodManager.hideSoftInputFromWindow(boardInput.getWindowToken(), 0);
            }
        });
        timestamp = findViewById(R.id.timestamp);


        ttlSlider = findViewById(R.id.sliderTTL);
        ttlText = findViewById(R.id.ttlHint);
        commentText = findViewById(R.id.commentHint);
        commentSwitch = findViewById(R.id.commentSwitch);
        commentSwitch.setOnClickListener(this);
        moodHint = findViewById(R.id.moodHint);
        moodView = findViewById(R.id.moodView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        moodView.setLayoutManager(layoutManager);
        moodView.setAdapter(new MoodsAdapter(DashBoard.this));
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(moodView);

        tvGif = findViewById(R.id.tvGif);


        moodView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int pos = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                    RecyclerView.ViewHolder v = recyclerView.findViewHolderForLayoutPosition(pos);
                    if (v instanceof MoodsAdapter.ViewHolder) {
                        String text = ((MoodsAdapter.ViewHolder) v).text.getText().toString();
                        moodHint.startAnimation(as);
                        moodHint.setText(text + " selected as a mood");
                    }

                    if (!onceScrolled) {
                        onceScrolled = true;
                        ivMood.setText(R.string.ellipsis);
                    }

                }
            }

        });
        setProgressText(ttlSlider.getProgress());
        ttlSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                double time = i / 2.0;
                if (i <= 1)
                    timestamp.setText("30m");
                else {
                    if (time == (long) time)
                        timestamp.setText(String.format("%d", (long) time) + "Hrs");
                    else timestamp.setText(String.format("%s", time) + "Hrs");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int progress = seekBar.getProgress();
                ttlText.startAnimation(as);
                setProgressText(progress);


            }
        });

        tvSound = findViewById(R.id.tvSound);


    }

    private void setTimestamp(int i) {
        double time = i / 2.0;
        if (i <= 1)
            timestamp.setText("30m");
        else {
            if (time == (long) time)
                timestamp.setText(String.format("%d", (long) time) + "Hrs");
            else timestamp.setText(String.format("%s", time) + "Hrs");
        }
    }

    private void setProgressText(int progress) {
        if (progress <= 1) {
            ttlText.setTextColor(Color.RED);
            ttlText.setText(getString(R.string.ttl_hint, "30 mins"));
        } else if (progress == 2) {
            ttlText.setTextColor(ContextCompat.getColor(DashBoard.this, R.color.gold));
            ttlText.setText(getString(R.string.ttl_hint, "1 hour"));
        } else if (progress == 3) {
            ttlText.setTextColor(ContextCompat.getColor(DashBoard.this, R.color.gold));
            ttlText.setText(getString(R.string.ttl_hint, "1 hour 30 mins"));
        } else {
            String d = String.valueOf(progress / 2) + " hours";
            ttlText.setTextColor(Color.BLACK);
            ttlText.setText(getString(R.string.ttl_hint, d));
        }
    }

    private void setPostAuthUI() {
        if (isTempUser()) {
            findViewById(R.id.create_account).setVisibility(View.VISIBLE);
            findViewById(R.id.separator2).setVisibility(View.VISIBLE);
            return;
        } else {
            genderSwitch.setChecked(true);
            genderHint.setTextColor(Color.BLACK);
            genderHint.setText(R.string.gender_hint_on);
            setGenderImage();
        }

        genderSwitch.setEnabled(true);

    }

    @Override
    public void onBackPressed() {
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

    private void removeRecord() {
        timer.setVisibility(View.INVISIBLE);
        recordInfo.setVisibility(View.INVISIBLE);
        tvSound.setVisibility(View.INVISIBLE);
        recordState = RecordState.PREPARED;
    }

    private void showSnackbar() {
        View container = findViewById(android.R.id.content);
        if (container != null && snackbar == null) {

            snackbar = AnonSnackbar.Builder(DashBoard.this)
                    .layout(R.layout.board_bar)
                    .duration(AnonSnackbar.LENGTH.INDEFINITE)
                    .swipe(false)
                    .build(container);

            View snackbarView = snackbar.getContentView();
            tvDone = snackbarView.findViewById(R.id.snackbar_text);
            timer = snackbarView.findViewById(R.id.timer);
            recordInfo = snackbarView.findViewById(R.id.recordInfo);
            recordInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recordState == RecordState.COMPLETED) {
                        removeRecord();
                        ivMic.setOnTouchListener(toucher);
                        ivMic.setImageDrawable(ContextCompat.getDrawable(DashBoard.this, R.drawable.ic_mic));
                    }

                }
            });
            tvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recordState == RecordState.RECORDING)
                        return;
                    inputMethodManager.hideSoftInputFromWindow(boardInput.getWindowToken(), 0);
                }
            });
            ivMic = snackbarView.findViewById(R.id.mic);
            ivMic.setOnTouchListener(toucher);

            gifLabel = snackbarView.findViewById(R.id.gif);
            gifLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (NO_GIF.equals(view.getTag())) {
                        Intent intent = new Intent(DashBoard.this, GifActivity.class);
                        startActivityForResult(intent, GIF_REQUEST_CODE);
                    } else {
                        gifLabel.setImageDrawable(ContextCompat.getDrawable(DashBoard.this, R.drawable.ic_gif));
                        gifLabel.setTag(NO_GIF);
                        gifFileName = null;
                        tvGif.setVisibility(View.INVISIBLE);
                    }

                }
            });
            gifLabel.setTag(NO_GIF);


            snackbar.show();
        } else if (snackbar != null && !snackbar.isShowing()) {
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

    @Override
    public void onAuthDone() {
        setPostAuthUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GIF_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        gifLabel.setImageDrawable(ContextCompat.getDrawable(DashBoard.this, R.drawable.ic_gif_loaded));
                        gifLabel.setTag(GIF_ADDED);
                        tvGif.setVisibility(View.VISIBLE);
                        gifFileName = data.getStringExtra(GifActivity.GIF_URI);
                        break;
                    case RESULT_CANCELED:
                        gifLabel.setTag(NO_GIF);
                        break;
                }


                break;
        }


    }


    private enum RecordState {
        NONE, ERROR, PREPARED, RECORDING, COMPLETED
    }
}
