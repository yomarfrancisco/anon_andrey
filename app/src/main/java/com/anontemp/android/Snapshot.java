package com.anontemp.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.anontemp.android.misc.BubbleDrawable;
import com.anontemp.android.misc.ChildEventAdapter;
import com.anontemp.android.misc.GeoRegion;
import com.anontemp.android.misc.Helper;
import com.anontemp.android.model.Capital;
import com.anontemp.android.model.Region;
import com.anontemp.android.model.Tweet;
import com.anontemp.android.model.UserLocation;
import com.anontemp.android.view.AnonTView;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.anontemp.android.misc.Helper.isShowableTweet;

public class Snapshot extends FullscreenMapController implements OnMapReadyCallback, LocationListener, View.OnClickListener {

    public static final LatLng CENTER = new LatLng(-26.190160, 28.028817);
    public static final String SPACE = " ";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 3;
    private final static int MAX_COUNT_SHOWABLE = 5;
    LocationManager mLocationManager;
    Location mLocation;
    Marker lastOpenned = null;
    List<GroundOverlay> mOverlays = new ArrayList<>();
    List<Marker> mMarkers = new ArrayList<>();
    FirebaseAuth mAuth;
    Map<Marker, Capital> mMarkerCapitalMap = new HashMap<>();
    private GoogleMap mMap;
    private List<Tweet> mTweetList;
    private List<Region> mRegionList;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private List<UserLocation> trackUsersList = new ArrayList<>();
    private int voteValue = 0;
    private FirebaseDatabase mDatabase;
    private List<Region> mRegions = new ArrayList<>();
    private ChildEventListener mRegionsListener = new ChildEventAdapter() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            Region region = dataSnapshot.getValue(Region.class);
            mRegions.add(0, region);
        }

    };
    private Tweet activeTweet;
    private ChildEventListener mTweetsListener = new ChildEventAdapter() {


        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            if (dataSnapshot.exists()) {
                Tweet tweet = dataSnapshot.getValue(Tweet.class);
                mTweetList.add(tweet);
                //TODO reloadTweets
            }


        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            if (!dataSnapshot.exists())
                return;

            Tweet updatedTweet = dataSnapshot.getValue(Tweet.class);
            Tweet originalTweet = null;
            int index = -1;

            for (Tweet tweet : mTweetList)
                if (tweet.getTweetId().equals(updatedTweet.getTweetId())) {
                    originalTweet = tweet;
                    index = mTweetList.indexOf(tweet);
                    break;
                }

            if (originalTweet == null) {
                mTweetList.add(updatedTweet);
                //TODO reloadTweets
                return;
            }

            mTweetList.set(index, updatedTweet);
            //TODO reloadTweets


            if (updatedTweet.getLoves() != null && activeTweet != null
                    && updatedTweet.getTweetId().equals(activeTweet.getTweetId())
                    && (activeTweet.getLoves() == null || updatedTweet.getLoves().size() != activeTweet.getLoves().size())) {
                voteValue = updatedTweet.getLoves().size();
                //TODO emitloveeffect

            }

        }

    };
    private boolean isUserCommented = false;
    private ViewGroup rootLayout;
    private boolean keyboardListenersAttached, onceScrolled = false;
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
    private InputMethodManager inputMethodManager;
    private Marker mMarker;

    private void filterNewCommentFromTweet(Tweet oldTweet, Tweet newTweet) {
        Map<String, String> oldComments = oldTweet.getComments();
        Map<String, String> newComments = newTweet.getComments();


        if (newComments == null)
            return;


        int color = Helper.getColorWithRegionName(newTweet.getRegionName());

        for (String uid : newComments.keySet()) {

            String newComment = newComments.get(uid);

            if (TextUtils.isEmpty(newComment))
                continue;

            if (oldComments == null || oldComments.isEmpty()) {
                //Todo ammit comment
                return;
            }

            String oldComment = oldComments.get(uid);

            if (TextUtils.isEmpty(oldComment) || !newComment.equals(oldComment)) {
                //Todo ammit comment
                return;
            }


        }
        if (isUserCommented && activeTweet != null) {
            color = Helper.getColorWithRegionName(activeTweet.getRegionName());
            //Todo ammit comment
            isUserCommented = false;

        }


    }

    private void reloadTweets() {
        for (Region region : mRegions) {
            boolean existAllowedComment = false;
            for (int j = 0; j < mTweetList.size(); j++) {
                Tweet t = mTweetList.get(j);
                if (region.getRegionId().equals(t.getRegionId())) {
                    if (existAllowedComment) {
                        t.setAllowComment(false);
                        mTweetList.remove(j);
                        mTweetList.add(j, t);
                    } else if (isShowableTweet(t) && t.getAllowComment()) {
                        existAllowedComment = true;

                    }
                }

            }
        }
        loadTweets();

    }

    @Override
    protected int init() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        return R.layout.activity_snapshot;
    }

    private void trackUserWithWits() {
        if (user == null)
            return;

        DatabaseReference geoRef = FirebaseDatabase.getInstance().getReference().child("GeoFire");
        GeoFire geoFire = new GeoFire(geoRef);
        GeoRegion witsRegion = Constants.LOCAL_REGIONS.get(0);
        GeoLocation center = new GeoLocation(witsRegion.getLatLng().latitude, witsRegion.getLatLng().longitude);
        GeoQuery centerQuery = geoFire.queryAtLocation(center, witsRegion.getRadius());
        centerQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                UserLocation userLocation = new UserLocation(key, location.latitude, location.longitude);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }

    private void updateUserLocation(UserLocation location) {
        int index = -1;
        for (UserLocation user : trackUsersList) {
            if (location.getUid().equals(user.getUid())) {
                index = trackUsersList.indexOf(user);
                break;
            }
        }
        if (index == -1) {
            trackUsersList.add(location);
        } else {
            trackUsersList.set(index, location);
        }

    }

    private void loadTweets() {

        for (GroundOverlay overlay : mOverlays)
            overlay.remove();
        for (Marker marker : mMarkers)
            marker.remove();

        mMarkerCapitalMap.clear();

        for (int i = 0; i < mTweetList.size(); i++) {
            Tweet tweet = mTweetList.get(i);
            if (Helper.isShowableTweet(tweet) && tweet.getAllowComment()) {
                GeoRegion localRegion = Helper.getLocalRegion(tweet);
                String[] words = tweet.getDate().split(SPACE);
                String tweetTime = words != null && words.length > 2 ? words[0] + SPACE + words[1] + ": " : "";

                Capital annotation = Capital.newInstance("@" + tweet.getRegionName(), tweetTime + "'" + tweet.getTweetText() + "'", localRegion.getLatLng(), tweet);
                Marker marker = mMap.addMarker(annotation.getOptions());
                mMarkerCapitalMap.put(marker, annotation);

                if (activeTweet != null && activeTweet.getTweetId().equals(tweet.getTweetId())) {
                    marker.showInfoWindow();
                }

            }

        }

        for (UserLocation userLocation : trackUsersList) {
            MarkerOptions markerOptions = new MarkerOptions().draggable(false).position(new LatLng(userLocation.getLat(), userLocation.getLng()));
            final Marker marker = mMap.addMarker(markerOptions);

        }


    }

    protected void onShowKeyboard() {

    }

    protected void onHideKeyboard() {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mDatabase = FirebaseDatabase.getInstance();
        attachKeyboardListeners();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getMyLocation();
        }
        findViewById(R.id.pin_link).setOnClickListener(this);
        activeTweet = getIntent().getSerializableExtra(MessageBoard.TWEET_EXTRA) != null ?
                (Tweet) getIntent().getSerializableExtra(MessageBoard.TWEET_EXTRA) : null;

    }

    @Override
    @SuppressWarnings("MissingPermission")
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mDatabase.getReference("Regions").addChildEventListener(mRegionsListener);

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
        mDatabase.getReference("Regions").removeEventListener(mRegionsListener);

    }

    @Override
    protected void onDestroy() {
        if (mLocationManager != null)
            mLocationManager.removeUpdates(this);
        super.onDestroy();
        if (keyboardListenersAttached) {
            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
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

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER, 15.65f));
        mMap.getUiSettings().setAllGesturesEnabled(false);

        for (CircleOptions circleOptions : Constants.LOCAL_CIRCLES) {
            mMap.addCircle(circleOptions);
        }

        mMap.setInfoWindowAdapter(new AnonInfoWindowAdapter());


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

    class AnonInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {


        private final View mView;

        AnonInfoWindowAdapter() {
            mView = getLayoutInflater().inflate(R.layout.anon_info_window, null);
        }


        @Override
        public View getInfoWindow(Marker marker) {
            Snapshot.this.mMarker = marker;

            final String title = marker.getTitle();
            final LinearLayout layout = mView.findViewById(R.id.window_layout);
            layout.setBackground(new BubbleDrawable());
            final AnonTView titleUi = mView.findViewById(R.id.title);
            if (title != null) {
                titleUi.setText(title);
            } else {
                titleUi.setText("");
            }

            final String snippet = marker.getSnippet();
            final AnonTView snippetUi = mView
                    .findViewById(R.id.snippet);
            if (snippet != null) {
                snippetUi.setText(snippet);
            } else {
                snippetUi.setText("");
            }

            return mView;


        }

        @Override
        public View getInfoContents(Marker marker) {
            if (Snapshot.this.mMarker != null
                    && Snapshot.this.mMarker.isInfoWindowShown()) {
                Snapshot.this.mMarker.hideInfoWindow();
                Snapshot.this.mMarker.showInfoWindow();
            }
            return null;
        }
    }

}
