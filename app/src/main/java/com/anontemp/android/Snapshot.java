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
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.anontemp.android.misc.Helper;
import com.anontemp.android.model.Region;
import com.anontemp.android.model.Tweet;
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

import java.util.List;

public class Snapshot extends FullscreenMapController implements OnMapReadyCallback, LocationListener, View.OnClickListener {

    public static final LatLng CENTER = new LatLng(-26.190160, 28.028117);
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 3;
    private final static int MAX_COUNT_SHOWABLE = 5;
    LocationManager mLocationManager;
    Location mLocation;
    Marker lastOpenned = null;
    private GoogleMap mMap;
    private List<Tweet> mTweetList;
    private List<Region> mRegionList;

    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected int init() {
        return R.layout.activity_snapshot;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getMyLocation();
        }
        findViewById(R.id.pin_link).setOnClickListener(this);

    }

    @Override
    @SuppressWarnings("MissingPermission")
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

    }

    @SuppressWarnings("MissingPermission")
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);


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
                            this, R.raw.map_style));

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

                marker.showInfoWindow();
                lastOpenned = marker;

                return true;
            }
        });


    }

    private void getMyLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 10, this);
        } else if (mLocationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {

            mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 30000, 10, this);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!checkPermissions()) {
            requestPermissions();
        } else {
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

            } else {
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
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
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.pin_link:
                Intent intent = new Intent(Snapshot.this, DashBoard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                Helper.downToUpTransition(Snapshot.this);
                break;
            case R.id.board_link:
                intent = new Intent(Snapshot.this, MessageBoard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                Helper.downToUpTransition(Snapshot.this);
                break;
        }


    }

}
