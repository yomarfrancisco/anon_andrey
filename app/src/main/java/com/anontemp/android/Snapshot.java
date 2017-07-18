package com.anontemp.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

public class Snapshot extends FragmentActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener {

    public static final LatLng CENTER = new LatLng(-26.189460, 28.028117);
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 3;
    LocationManager mLocationManager;
    Location mLocation;
    Marker lastOpenned = null;
    private GoogleMap mMap;
    private View decorView;
    //    private GeofencingClient mGeofencingClient;
//    private List<Geofence> mGeofenceList;
//    private PendingIntent mGeofencePendingIntent;
//    private Constants.PendingGeofenceTask mPendingGeofenceTask = Constants.PendingGeofenceTask.NONE;
    private ImageView ivLock;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snapshot);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getMyLocation();
        }
//        mGeofenceList = new ArrayList<>();
//        mGeofencePendingIntent = null;
//        populateGeofenceList();
//        mGeofencingClient = LocationServices.getGeofencingClient(this);
        findViewById(R.id.ivPencil).setOnClickListener(this);

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
        mMap.setMyLocationEnabled(false);

    }

    @Override
    protected void onDestroy() {
        if (mLocationManager != null)
            mLocationManager.removeUpdates(this);
        super.onDestroy();
    }

//    private void populateGeofenceList() {
//        for (Map.Entry<String, GeoRegion> entry : Constants.SNAPSHOT_REGIONS.entrySet()) {
//
//            mGeofenceList.add(new Geofence.Builder()
//                    // Set the request ID of the geofence. This is a string to identify this
//                    // geofence.
//                    .setRequestId(entry.getKey())
//
//                    // Set the circular region of this geofence.
//                    .setCircularRegion(
//                            entry.getValue().getLatLng().latitude,
//                            entry.getValue().getLatLng().longitude,
//                            entry.getValue().getRadius()
//                    )
//                    .setExpirationDuration(86400000)
//                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
//                            Geofence.GEOFENCE_TRANSITION_EXIT)
//
//                    .build());
//        }
//    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.getUiSettings().setZoomControlsEnabled(false);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER, 16));
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(true);

        for (MarkerOptions markerOptions : Constants.SNAPSHOT_MARKERS) {
            if (markerOptions.getTitle().equals(Constants.BRAAM_CAMPUS)) {
                Marker marker = mMap.addMarker(markerOptions);
                marker.showInfoWindow();
            } else {
                mMap.addMarker(markerOptions);
            }
        }

        for (CircleOptions circleOptions : Constants.SNAPSHOT_CIRCLES) {
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
    protected void onStart() {
        super.onStart();
//        mPendingGeofenceTask = Constants.PendingGeofenceTask.ADD;
        if (!checkPermissions()) {
            requestPermissions();
        } else {
//            performPendingGeofenceTask();
        }


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
                            ActivityCompat.requestPermissions(Snapshot.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(Helper.TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(Snapshot.this,
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

//    private void performPendingGeofenceTask() {
//        if (mPendingGeofenceTask == Constants.PendingGeofenceTask.ADD) {
//            addGeofences();
//        }
//    }
//
//    private void updateGeofencesAdded(boolean added) {
//        PreferenceManager.getDefaultSharedPreferences(this)
//                .edit()
//                .putBoolean(Constants.GEOFENCES_ADDED_KEY, added)
//                .apply();
//    }
//
//    @Override
//    public void onComplete(@NonNull Task<Void> task) {
//        mPendingGeofenceTask = Constants.PendingGeofenceTask.NONE;
//        if (task.isSuccessful()) {
//            updateGeofencesAdded(!getGeofencesAdded());
//
//        } else {
//            // Get the status code for the error and log it using a user-friendly tweet.
//            String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
//            Log.w(Helper.TAG, errorMessage);
//        }
//    }
//
//    private PendingIntent getGeofencePendingIntent() {
//        // Reuse the PendingIntent if we already have it.
//        if (mGeofencePendingIntent != null) {
//            return mGeofencePendingIntent;
//        }
//        Intent intent = new Intent(this, SnapshotIntentService.class);
//        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
//        // addGeofences() and removeGeofences().
//        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//    }
//
//    private boolean getGeofencesAdded() {
//        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
//                Constants.GEOFENCES_ADDED_KEY, false);
//    }
//
//    private GeofencingRequest getGeofencingRequest() {
//        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
//
//        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
//        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
//        // is already inside that geofence.
//        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
//
//        // Add the geofences to be monitored by geofencing service.
//        builder.addGeofences(mGeofenceList);
//
//        // Return a GeofencingRequest.
//        return builder.build();
//    }
//
//
//    @SuppressWarnings("MissingPermission")
//    private void addGeofences() {
//        if (!checkPermissions()) {
//            Helper.showSnackbar(getString(R.string.insufficient_permissions), this);
//            return;
//        }
//
//        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
//                .addOnCompleteListener(this);
//    }

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
//                performPendingGeofenceTask();

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
//                mPendingGeofenceTask = Constants.PendingGeofenceTask.NONE;
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ivPencil:
                Intent intent = new Intent(Snapshot.this, DashBoard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                Helper.downToUpTransition(Snapshot.this);
                break;
        }


    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
