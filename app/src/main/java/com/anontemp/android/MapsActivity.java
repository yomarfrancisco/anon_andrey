package com.anontemp.android;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anontemp.android.view.AnonTEditText;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, OnCompleteListener<Void>, View.OnClickListener {

    public static final LatLng CENTER = new LatLng(-26.184760, 28.028717);
    public static final String REGION_NAME = "regionName";
    public static final String PASS = "fuckmtv";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 3;
    LocationManager mLocationManager;
    Location mLocation;
    Marker lastOpenned = null;
    GodModeDialog dialog;
    private GoogleMap mMap;
    private View decorView;
    private GeofencingClient mGeofencingClient;
    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private Constants.PendingGeofenceTask mPendingGeofenceTask = Constants.PendingGeofenceTask.NONE;
    private ImageView ivLock;
    private TextView tvLock;
    private ImageView ivKey;
    private ImageView ivSplash;
    private String regionName;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private BroadcastReceiver geofenceChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case GeofenceTransitionsIntentService.ACTION_ENTERED:
                    regionName = intent.getStringExtra(Constants.GEOFENCE_ID);
                    ivLock.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_unlock));
                    ivLock.setTag(getString(R.string.unlocked));
                    ivKey.setVisibility(View.VISIBLE);


                    tvLock.setText(getString(R.string.network_enter_text, regionName));


                    break;


            }


        }
    };
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private void showAlertDialog() {


        dialog = new GodModeDialog();
        dialog.show(getSupportFragmentManager(), null);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getMyLocation();
        }
        mGeofenceList = new ArrayList<>();
        mGeofencePendingIntent = null;
        populateGeofenceList();
        mGeofencingClient = LocationServices.getGeofencingClient(this);
        ivLock = findViewById(R.id.ivLock);
        ivLock.setOnClickListener(this);
        tvLock = findViewById(R.id.tvLock);
        ivKey = findViewById(R.id.ivKey);
        ivKey.setOnClickListener(this);
        ivSplash = findViewById(R.id.ivSplash);
        ivSplash.setOnTouchListener(new View.OnTouchListener() {

            int numberOfTaps = 0;
            long lastTapTimeMs = 0;
            long touchDownMs = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchDownMs = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_UP:

                        if ((System.currentTimeMillis() - touchDownMs) > ViewConfiguration.getTapTimeout()) {
                            //it was not a tap

                            numberOfTaps = 0;
                            lastTapTimeMs = 0;
                            break;
                        }

                        if (numberOfTaps > 0
                                && (System.currentTimeMillis() - lastTapTimeMs) < ViewConfiguration.getDoubleTapTimeout()) {
                            numberOfTaps += 1;
                        } else {
                            numberOfTaps = 1;
                        }

                        lastTapTimeMs = System.currentTimeMillis();

                        if (numberOfTaps == 3) {
                            showAlertDialog();
                            //handle triple tap
                        }
                }

                return true;
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(Helper.TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(Helper.TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


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

    @Override
    @SuppressWarnings("MissingPermission")
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(geofenceChangeReceiver,
                new IntentFilter(GeofenceTransitionsIntentService.ACTION_ENTERED));
        tryAuth();
        setUpMapIfNeeded();

    }

    @SuppressWarnings("MissingPermission")
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
            // Check if we were successful in obtaining the map.


        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    @SuppressWarnings("MissingPermission")
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(geofenceChangeReceiver);
        mMap.setMyLocationEnabled(false);

    }

    @Override
    protected void onDestroy() {
        if (mLocationManager != null)
            mLocationManager.removeUpdates(this);
        super.onDestroy();
    }

    private void populateGeofenceList() {
        for (Map.Entry<String, GeoRegion> entry : Constants.LOCAL_REGIONS.entrySet()) {

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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.getUiSettings().setZoomControlsEnabled(false);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER, 12));
        mMap.getUiSettings().setAllGesturesEnabled(false);

        for (CircleOptions circleOptions : Constants.LOCAL_CIRCLES) {
            mMap.addCircle(circleOptions);
        }

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.ios_style));

            if (!success) {
                Log.e(Helper.TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(Helper.TAG, "Can't find style. Error: ", e);
        }

        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                // Check if there is an open info window
                if (lastOpenned != null) {
                    // Close the info window
                    lastOpenned.hideInfoWindow();

                    // Is the marker the same marker that was already open
                    if (lastOpenned.equals(marker)) {
                        // Nullify the lastOpenned object
                        lastOpenned = null;
                        // Return so that the info window isn't openned again
                        return true;
                    }
                }

                // Open the info window for the marker
                marker.showInfoWindow();
                // Re-assign the last openned such that we can close it later
                lastOpenned = marker;

                // Event was handled by our code do not launch default behaviour.
                return true;
            }
        });


    }

    private void getMyLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        if (mLocation == null) {
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 10, this);
        } else if (mLocationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {

            mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 30000, 10, this);
        }
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        if (!checkPermissions()) {
            mPendingGeofenceTask = Constants.PendingGeofenceTask.REMOVE;
            requestPermissions();
            return;
        }
        removeGeofences();

    }

    @SuppressWarnings("MissingPermission")
    private void removeGeofences() {
        if (!checkPermissions()) {
            Helper.showSnackbar(getString(R.string.insufficient_permissions), MapsActivity.this);
            return;
        }

        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
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

        mAuth.addAuthStateListener(mAuthListener);


    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.v("Location Changed", location.getLatitude() + " and " + location.getLongitude());
            mLocation = location;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

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

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(Helper.TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(Helper.TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MapsActivity.this,
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
        } else if (mPendingGeofenceTask == Constants.PendingGeofenceTask.REMOVE) {
            removeGeofences();
        }
    }

    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(Constants.GEOFENCES_ADDED_KEY, added)
                .apply();
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = Constants.PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());

        } else {
            // Get the status code for the error and log it using a user-friendly tweet.
            String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            Log.w(Helper.TAG, errorMessage);
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                Constants.GEOFENCES_ADDED_KEY, false);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
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
                getMyLocation();
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ivLock:
                if (v.getTag().equals(getString(R.string.locked))) {
                    View d = LayoutInflater.from(MapsActivity.this).inflate(R.layout.c_alert, null);
                    AlertDialog.Builder build = new AlertDialog.Builder(MapsActivity.this);
                    build.setView(d);
                    final AlertDialog dialog = build.create();

                    LinearLayout iv = d.findViewById(R.id.dialLayout);
                    TextView tv = iv.findViewById(R.id.text);
                    tv.setText(R.string.lock_info);
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v.isShown()) {
                                dialog.dismiss();
                            }
                        }
                    });
                    dialog.show();
                } else {
                    ivKey.performClick();
                }
                break;
            case R.id.ivKey:
                transitToLogin();
                Animation gone = AnimationUtils.loadAnimation(MapsActivity.this, R.anim.rotate_and_gone);
                ivKey.startAnimation(gone);
                break;
        }

    }

    public void transitToLogin() {
        Intent intent;
        if (user != null && user.getUid().equals(Helper.getUuid())) {
            intent = new Intent(MapsActivity.this, DashBoard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        } else {
            intent = new Intent(MapsActivity.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        }
        intent.putExtra(REGION_NAME, Constants.WITS_UNIVERSITY_LOWCASE);
        startActivity(intent);
        Helper.downToUpTransition(MapsActivity.this);
    }

    private void tryAuth() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                user = mAuth.getCurrentUser();
                if (!Helper.getUuid().isEmpty() && (user == null || user != null && !user.getUid().equals(Helper.getUuid()))) {
                    Looper.prepare();
                    Pair<String, String> pair = Helper.getCredentials();
                    mAuth.signInWithEmailAndPassword(pair.first, pair.second)
                            .addOnCompleteListener(MapsActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(Helper.TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

//                                    user = mAuth.getCurrentUser();
                                    if (!task.isSuccessful()) {
                                        Log.w(Helper.TAG, "signInWithEmail:failed", task.getException());


                                    }

                                    // ...
                                }
                            });
                    Looper.loop();

                }
            }
        }).start();

    }

    public static class GodModeDialog extends DialogFragment {

        View d;

        public GodModeDialog() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create the dialog
            d = LayoutInflater.from(getContext()).inflate(R.layout.godmode_dial, null);
            final AlertDialog.Builder build = new AlertDialog.Builder(getContext(), R.style.MyTheme);
            build.setView(d);
            TextView tvTitle = d.findViewById(R.id.title);
            Typeface bold = FontCache.getTypeface("CFSnowboardProjectPERSONAL.ttf", getContext());
            tvTitle.setTypeface(bold);
            tvTitle.setText(R.string.god_title);
            TextView tv = d.findViewById(R.id.text);
            Typeface normal = FontCache.getTypeface("helvetica-neue.otf", getContext());
            tv.setTypeface(normal);
            tv.setText(R.string.login_pass_hint);

            final AnonTEditText pass = d.findViewById(R.id.pass);
            Button btnOk = d.findViewById(R.id.btnOk);
            btnOk.setTypeface(normal);
            btnOk.setText(R.string.submit);

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (pass.getText().toString().equals(PASS)) {
                        MapsActivity mapsActivity = (MapsActivity) getActivity();
                        mapsActivity.transitToLogin();
                        dismiss();

                    }
                }
            });

            Button btnCancel = d.findViewById(R.id.btnCancel);
            btnCancel.setTypeface(normal);
            btnCancel.setText(android.R.string.cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

            final ImageView img_loading_frame = d.findViewById(R.id.ivDope);
            final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
            img_loading_frame.post(new Runnable() {
                @Override
                public void run() {
                    frameAnimation.start();
                }
            });


            AlertDialog dialog = build.setCancelable(false).create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            return dialog;
        }

        @Override
        public void onStart() {
            super.onStart();

            forceWrapContent(d);
        }

        protected void forceWrapContent(View v) {
            // Start with the provided view
            View current = v;

            // Travel up the tree until fail, modifying the LayoutParams
            do {
                // Get the parent
                ViewParent parent = current.getParent();

                // Check if the parent exists
                if (parent != null) {
                    // Get the view
                    try {
                        current = (View) parent;
                    } catch (ClassCastException e) {
                        // This will happen when at the top view, it cannot be cast to a View
                        break;
                    }

                    // Modify the layout
                    current.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
            } while (current.getParent() != null);

            // Request a layout to be re-done
            current.requestLayout();
        }
    }


}
