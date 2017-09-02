package com.anontemp.android;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anontemp.android.misc.ActionsDialog;
import com.anontemp.android.misc.ActionsListener;
import com.anontemp.android.misc.ChildEventAdapter;
import com.anontemp.android.misc.GeoRegion;
import com.anontemp.android.misc.Helper;
import com.anontemp.android.misc.LocationReceiver;
import com.anontemp.android.misc.OnLocationReceivedListener;
import com.anontemp.android.model.Capital;
import com.anontemp.android.model.Region;
import com.anontemp.android.model.Tweet;
import com.anontemp.android.model.UserLocation;
import com.anontemp.android.view.AnonSnackbar;
import com.anontemp.android.view.AnonTEdit;
import com.anontemp.android.view.AnonTView;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.anontemp.android.misc.Helper.isShowableTweet;

public class Snapshot extends FullscreenMapController implements OnMapReadyCallback, View.OnClickListener, ActionsListener, OnLocationReceivedListener {

    public static final LatLng CENTER = new LatLng(-26.190160, 28.028817);
    public static final String EMIT_COMMENT = "emit_comment";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 3;
    private final static int MAX_COUNT_SHOWABLE = 5;
    public static int intMarkerNumber = 1;
    public final int REPEAT_SECONDS = 91;
    Location mLocation;
    Marker lastOpenned = null;
    List<GroundOverlay> mOverlays = new ArrayList<>();
    FirebaseAuth mAuth;
    Map<Marker, Capital> mMarkerCapitalMap = new HashMap<>();
    Handler handler = new Handler();
    private GoogleMap mMap;
    private List<Tweet> mTweetList = new ArrayList<>();
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
    private ViewGroup rootLayout;
    private boolean keyboardListenersAttached, isUserCommented = false;
    private InputMethodManager inputMethodManager;
    private Marker mMarker;
    private BitmapDescriptor frame0;
    private BitmapDescriptor frame1;
    private BitmapDescriptor frame2;
    private BitmapDescriptor frame3;
    private BitmapDescriptor frame4;
    private BitmapDescriptor frame5;
    private BitmapDescriptor frame6;
    private BitmapDescriptor frame7;
    private LocationService locationService;
    private ServiceConnection locationConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            locationService = ((LocationService.LocationBinder) service).getService();
            mLocation = locationService.getLocation();

        }

        public void onServiceDisconnected(ComponentName className) {
            locationService = null;
        }
    };
    private LocationReceiver locationReceiver = new LocationReceiver(this);
    private BroadcastReceiver geofenceChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case GeofenceTransitionsIntentService.ACTION_ENTERED:
                    String geofenceId = intent.getStringExtra(Constants.GEOFENCE_ID);
                    break;
//                case GeofenceTransitionsIntentService.ACTION_EXIT:
//                    tvLocation.setText(R.string.location_default);
//                    break;


            }


        }
    };
    private boolean mIsBound;
    private AnonSnackbar snackbar;
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
    private String userComment;
    private ActionsDialog mActionsDialog;
    private LinearLayout commentLayout;
    private LinearLayout likeLayout;
    private ChildEventListener mTweetsListener = new ChildEventAdapter() {


        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            if (dataSnapshot.exists()) {
                Tweet tweet = dataSnapshot.getValue(Tweet.class);
                setTime(tweet);
                if (!isShowableTweet(tweet))
                    return;
                tweet.setReference(dataSnapshot.getRef());
                mTweetList.add(tweet);
                reloadTweets();
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
                return;
            }
            updatedTweet.setTimeToLive(originalTweet.getTimeToLive());
            updatedTweet.setRealDate(originalTweet.getRealDate());
            updatedTweet.setReference(originalTweet.getReference());
            filterNewCommentFromTweet(originalTweet, updatedTweet);
            mTweetList.set(index, updatedTweet);
            reloadTweets();


            if (updatedTweet.getLoves() != null && activeTweet != null
                    && updatedTweet.getTweetId().equals(activeTweet.getTweetId())
                    && (activeTweet.getLoves() == null || updatedTweet.getLoves().size() != activeTweet.getLoves().size())) {
                voteValue = updatedTweet.getLoves().size();
                emitLike();

            }

        }

    };

    private void setTime(Tweet tweet) {
        tweet.setRealDate(Helper.getRealDate(tweet.getDate()));
        tweet.setTimeToLive(Helper.formatTimeToLive(Helper.getTimeToLive(tweet.getRealDate(), tweet.getCountdown()), Snapshot.this));
    }

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
                emitComment(newComment, color);
                return;
            }

            String oldComment = oldComments.get(uid);

            if (TextUtils.isEmpty(oldComment) || !newComment.equals(oldComment)) {
                emitComment(newComment, color);
                return;
            }


        }
        if (isUserCommented && activeTweet != null) {
            color = Helper.getColorWithRegionName(activeTweet.getRegionName());
            emitComment(userComment, color);
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

    private void trackUsersWithWits() {
        if (user == null)
            return;

        DatabaseReference geoRef = FirebaseDatabase.getInstance().getReference().child("GeoFire");
        GeoFire geoFire = new GeoFire(geoRef);
        GeoRegion witsRegion = Constants.LOCAL_REGIONS.get(0);
        GeoLocation center = new GeoLocation(witsRegion.getLatLng().latitude, witsRegion.getLatLng().longitude);
        GeoQuery circleQuery = geoFire.queryAtLocation(center, witsRegion.getRadius());
        circleQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                UserLocation userLocation = new UserLocation(key, location.latitude, location.longitude);
                updateUserLocation(userLocation);
                loadTweets();
            }

            @Override
            public void onKeyExited(String key) {
                for (Iterator<UserLocation> iterator = trackUsersList.iterator(); iterator.hasNext(); ) {
                    UserLocation ul = iterator.next();
                    if (key.equals(ul.getUid())) {
                        iterator.remove();
                        loadTweets();
                        break;
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                UserLocation userLocation = new UserLocation(key, location.latitude, location.longitude);
                updateUserLocation(userLocation);
                loadTweets();
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

    private void clearMap() {
        for (GroundOverlay overlay : mOverlays)
            overlay.remove();
        mOverlays.clear();
        handler.removeCallbacksAndMessages(null);

        for (Marker marker : mMarkerCapitalMap.keySet())
            marker.remove();
        mMarkerCapitalMap.clear();
    }

    private void loadTweets() {

        clearMap();


        for (int i = 0; i < mTweetList.size(); i++) {
            Tweet tweet = mTweetList.get(i);
            if (Helper.isShowableTweet(tweet) && tweet.getAllowComment()) {
                GeoRegion localRegion = Helper.getLocalRegion(tweet);


                String tweetTime = Helper.getVisibleDate(tweet.getRealDate());
                Capital annotation = Capital.newInstance("@" + tweet.getRegionName(), tweetTime + "'" + tweet.getTweetText() + "'", localRegion.getLatLng(), tweet);
                Marker marker = mMap.addMarker(annotation.getOptions());
                mMarkerCapitalMap.put(marker, annotation);

                if (activeTweet != null && activeTweet.getTweetId().equals(tweet.getTweetId())) {
                    marker.showInfoWindow();
                }

            }

        }

        for (UserLocation userLocation : trackUsersList) {

            GroundOverlayOptions options = new GroundOverlayOptions().position(new LatLng(userLocation.getLat(), userLocation.getLng()), 70f, 70f).image(frame0);
            GroundOverlay overlay = mMap.addGroundOverlay(options);
            mOverlays.add(overlay);
            markerAnimation(overlay);


        }

    }

    private int dp(int px) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (px * scale + 0.5f);
    }

    private void emitComment(String comment, int color) {

        commentLayout.removeAllViews();

        AnonTView ecView = new AnonTView(Snapshot.this);
        ecView.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.emit_border));
        ecView.setText(comment);
        ecView.setTextSize(12f);
        ecView.setTextColor(ContextCompat.getColor(Snapshot.this, R.color.white_alfa));
        int five = dp(5);
        ecView.setPadding(five, five, five, five);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(200), dp(24));
        params.setMargins(0, dp(40), 0, 0);
        ecView.setGravity(Gravity.CENTER_VERTICAL);
        ecView.setLayoutParams(params);

        ecView.setSupportBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, Constants.REGION_COLORS.get(color))));

        commentLayout.addView(ecView);

        ecView.animate().alphaBy(1.0f).alpha(0.0f).translationY(-dp(200)).setDuration(20000).setInterpolator(new DecelerateInterpolator()).start();

    }

    private void emitLike() {

        ImageView likeView = new ImageView(Snapshot.this);
        likeView.setImageDrawable(ContextCompat.getDrawable(Snapshot.this, R.drawable.ic_smile));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dp(40), 0, 0);
        likeView.setLayoutParams(params);
        likeLayout.addView(likeView);
        likeView.animate().alphaBy(1.0f).alpha(0.0f).translationY(-dp(200)).setDuration(20000).setInterpolator(new DecelerateInterpolator()).start();


    }

    private void showActionWithTweet(Tweet tweet) {
        activeTweet = tweet;
        mActionsDialog = ActionsDialog.newInstance(tweet);
        mActionsDialog.show(getSupportFragmentManager(), null);


    }

    private Bitmap resizeMarker(int resId) {
        int side = 80;
        Bitmap b = BitmapFactory.decodeResource(getResources(), resId);

        return Bitmap.createScaledBitmap(b, side, side, false);
    }

    public void markerAnimation(final GroundOverlay marker) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    if (marker == null || !marker.isVisible())
                        return;

                    switch (intMarkerNumber) {

                        case 1:
                            if (frame0 == null)
                                break;
                            marker.setImage(frame0);
                            ++intMarkerNumber;
                            break;
                        case 2:
                            if (frame1 == null)
                                break;
                            marker.setImage(frame1);
                            ++intMarkerNumber;
                            break;
                        case 3:
                            if (frame2 == null)
                                break;
                            marker.setImage(frame2);
                            ++intMarkerNumber;
                            break;
                        case 4:
                            if (frame3 == null)
                                break;
                            marker.setImage(frame3);
                            ++intMarkerNumber;
                            break;
                        case 5:
                            if (frame4 == null)
                                break;
                            marker.setImage(frame4);
                            ++intMarkerNumber;
                            break;
                        case 6:
                            if (frame5 == null)
                                break;
                            marker.setImage(frame5);
                            ++intMarkerNumber;
                            break;
                        case 7:
                            if (frame6 == null)
                                break;
                            marker.setImage(frame6);
                            ++intMarkerNumber;
                            break;
                        default:
                            if (frame7 == null) {
                                intMarkerNumber = 1;
                                break;
                            }
                            marker.setImage(frame7);
                            intMarkerNumber = 1;
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, REPEAT_SECONDS);

            }
        }, REPEAT_SECONDS);
    }

    protected void onShowKeyboard() {

    }

    protected void onHideKeyboard() {
//        if (snackbar != null && snackbar.isShowing()) {
//            snackbar.dismiss();
//        }
    }

    protected void attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return;
        }

        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        keyboardListenersAttached = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        commentLayout = findViewById(R.id.comment_layout);
        likeLayout = findViewById(R.id.like_layout);
        frame0 = BitmapDescriptorFactory.fromBitmap(resizeMarker(R.drawable.frame_0));
        frame1 = BitmapDescriptorFactory.fromBitmap(resizeMarker(R.drawable.frame_1));
        frame2 = BitmapDescriptorFactory.fromBitmap(resizeMarker(R.drawable.frame_2));
        frame3 = BitmapDescriptorFactory.fromBitmap(resizeMarker(R.drawable.frame_3));
        frame4 = BitmapDescriptorFactory.fromBitmap(resizeMarker(R.drawable.frame_4));
        frame5 = BitmapDescriptorFactory.fromBitmap(resizeMarker(R.drawable.frame_5));
        frame6 = BitmapDescriptorFactory.fromBitmap(resizeMarker(R.drawable.frame_6));
        frame7 = BitmapDescriptorFactory.fromBitmap(resizeMarker(R.drawable.frame_7));
        mDatabase = FirebaseDatabase.getInstance();
        rootLayout = findViewById(R.id.rootLayout);
        rootLayout.setOnClickListener(this);
        attachKeyboardListeners();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!checkPermissions()) {
            requestPermissions();
        }
        doBindService();
        findViewById(R.id.pin_link).setOnClickListener(this);
        findViewById(R.id.board_link).setOnClickListener(this);
        findViewById(R.id.create_account).setOnClickListener(this);

        activeTweet = getIntent().getSerializableExtra(MessageBoard.TWEET_EXTRA) != null ?
                (Tweet) getIntent().getSerializableExtra(MessageBoard.TWEET_EXTRA) : null;
        trackUsersWithWits();


    }

    @Override
    @SuppressWarnings("MissingPermission")
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        IntentFilter intentFilter = new IntentFilter(GeofenceTransitionsIntentService.ACTION_ENTERED);
        intentFilter.addAction(GeofenceTransitionsIntentService.ACTION_EXIT);
        mDatabase.getReference("Tweets").addChildEventListener(mTweetsListener);
        mDatabase.getReference("Regions").addChildEventListener(mRegionsListener);
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver, new IntentFilter(LocationService.ARG_LOCATION_ACTION));
        LocalBroadcastManager.getInstance(this).registerReceiver(geofenceChangeReceiver,
                intentFilter);


    }

    @SuppressWarnings("MissingPermission")
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);


        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    void doBindService() {
        bindService(new Intent(Snapshot.this,
                LocationService.class), locationConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(locationConnection);
            mIsBound = false;
        }
    }

    @Override
    @SuppressWarnings("MissingPermission")
    protected void onPause() {
        super.onPause();
        mMap.setMyLocationEnabled(false);
        mDatabase.getReference("Regions").removeEventListener(mRegionsListener);
        mDatabase.getReference("Tweets").removeEventListener(mTweetsListener);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(geofenceChangeReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);

    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();

        if (keyboardListenersAttached) {
            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }
        clearMap();
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

                if (mMarkerCapitalMap.isEmpty() || !mMarkerCapitalMap.containsKey(marker)) {
                    return true;
                }

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

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (!mMarkerCapitalMap.isEmpty() && mMarkerCapitalMap.containsKey(marker)) {
                    showActionWithTweet(mMarkerCapitalMap.get(marker).getInfo());
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!checkPermissions()) {
            requestPermissions();
        } else {
        }


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
                startActivity(intent);
                break;
            case R.id.board_link:
                intent = new Intent(Snapshot.this, MessageBoard.class);
                startActivity(intent);
                break;
            case R.id.create_account:
                intent = new Intent(Snapshot.this, CreateAccount.class);
                startActivity(intent);
                break;
            default:
                if (snackbar != null && snackbar.isShowing()) {
                    snackbar.dismiss();
                }
                break;
        }


    }

    @Override
    public void onLikeClick(Tweet tweet) {
        mActionsDialog.dismiss();

        if (activeTweet != null && activeTweet.getLoves() != null) {
            voteValue = activeTweet.getLoves().size();
        }

        String uid = currentUser.getUid();
        String username = currentUser.getFirstName();
        DatabaseReference ref = mDatabase.getReference().child("Tweets").child(activeTweet.getTweetId()).child("loves");

        if (activeTweet.getLoves() == null) {
            Map<String, String> map = new HashMap<>();
            map.put(uid, username);
            ref.setValue(map);

        } else {
            activeTweet.getLoves().put(uid, username);
            ref.setValue(activeTweet.getLoves());
        }

    }

    @Override
    public void onCommentClick(Tweet tweet) {
        mActionsDialog.dismiss();
        showSnackbar();
    }

    @Override
    public void onReceive(Location location) {
        this.mLocation = location;
    }

    private void addComment(String comment) {
        if (activeTweet == null)
            return;

        String userId = currentUser.getUserId();

        if (userId != null) {
            comment = userId + ": " + comment;
        }

        DatabaseReference ref = mDatabase.getReference().child("Tweets").child(activeTweet.getTweetId()).child("comments");

        if (activeTweet.getComments() == null) {
            Map<String, String> map = new HashMap<>();
            map.put(currentUser.getUid(), comment);
            ref.setValue(map);
        } else {
            isUserCommented = true;
            userComment = comment;
            activeTweet.getComments().put(currentUser.getUid(), comment);
            ref.setValue(activeTweet.getComments());

        }

    }

    private void showSnackbar() {
        View container = findViewById(android.R.id.content);
        if (container != null && snackbar == null) {

            snackbar = AnonSnackbar.Builder(Snapshot.this)
                    .layout(R.layout.comment_bar)
                    .duration(AnonSnackbar.LENGTH.INDEFINITE)
                    .swipe(false)
                    .build(container);

            View snackbarView = snackbar.getContentView();
            final AnonTEdit tEdit = snackbarView.findViewById(R.id.comment_input);

            tEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_SEND || i == EditorInfo.IME_ACTION_UNSPECIFIED) {
                        addComment(textView.getText().toString());
                        tEdit.setText(R.string.empty);
                        snackbar.dismiss();

                    }
                    return false;
                }
            });

            snackbar.show();
        } else if (snackbar != null && !snackbar.isShowing()) {
            snackbar.show();
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
