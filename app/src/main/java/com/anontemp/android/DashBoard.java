package com.anontemp.android;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.anontemp.android.Constants.PendingGeofenceTask;
import com.anontemp.android.com.anontemp.android.model.Region;
import com.anontemp.android.com.anontemp.android.model.Tweet;
import com.anontemp.android.com.anontemp.android.model.User;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
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
import java.util.Map;
import java.util.UUID;


public class DashBoard extends FullscreenController implements View.OnClickListener, OnCompleteListener<Void> {

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
    private GeofencingClient mGeofencingClient;
    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;
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

    private void populateGeofenceList() {
        for (Map.Entry<String, GeoRegion> entry : Constants.SUB_REGIONS.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().getLatLng().latitude,
                            entry.getValue().getLatLng().longitude,
                            entry.getValue().getRadius()
                    )
                    .setExpirationDuration(86400000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    .build());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPendingGeofenceTask = Constants.PendingGeofenceTask.ADD;
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            performPendingGeofenceTask();
        }
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

        mGeofenceList = new ArrayList<>();
        mGeofencePendingIntent = null;
        populateGeofenceList();
        mGeofencingClient = LocationServices.getGeofencingClient(this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.messageBoard:
                Intent intent = new Intent(DashBoard.this, MessageBoard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                Helper.downToUpTransition(DashBoard.this);
                break;
            case R.id.genderSwitch:
                genderHint.setText(genderSwitch.isChecked() ? R.string.gender_hint_on : R.string.gender_hint_off);
                setGenderImage();
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
        showProgressDialog(R.string.loading);
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
            if (regionName.equals(region.getRegiontext())) {
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
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == Constants.PendingGeofenceTask.ADD) {
            addGeofences();
        }
    }

    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(Constants.TWEET_GEOFENCES_ADDED_KEY, added)
                .apply();
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = Constants.PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());

        } else {
            String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            Log.w(Helper.TAG, errorMessage);
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                Constants.TWEET_GEOFENCES_ADDED_KEY, false);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        builder.addGeofences(mGeofenceList);

        return builder.build();
    }


    @SuppressWarnings("MissingPermission")
    private void addGeofences() {
        if (!checkPermissions()) {
            Helper.showSnackbar(getString(R.string.insufficient_permissions), this);
            return;
        }

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(Helper.TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(Helper.TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(Helper.TAG, "Permission granted.");
                performPendingGeofenceTask();

            } else {
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                mPendingGeofenceTask = Constants.PendingGeofenceTask.NONE;
            }
        }
    }

}
