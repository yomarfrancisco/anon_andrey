package com.anontemp.android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.anontemp.android.misc.Helper;

public class LocationService extends Service {
    public static final String ARG_LOCATION = "location";
    public static final String ARG_LOCATION_ACTION = "com.anontemp.android.service.location.received";
    public static final float DISTANCE = 10f;
    private static final int INTERVAL = 30000;
    private static final String TAG = "Location Service";
    private final IBinder mBinder = new LocationBinder();
    LocListener[] locationListeners = new LocListener[]{
            new LocListener(LocationManager.GPS_PROVIDER),
            new LocListener(LocationManager.NETWORK_PROVIDER)
    };
    private LocationManager locationManager = null;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        initializeLocationManager();
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, INTERVAL, DISTANCE,
                    locationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }

        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, INTERVAL, DISTANCE,
                    locationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }


    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (locationManager != null) {
            for (int i = 0; i < locationListeners.length; i++) {
                try {
                    locationManager.removeUpdates(locationListeners[i]);
                } catch (Exception ex) {
                    Log.i(Helper.TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(Helper.TAG, "Received start id " + startId + ": " + intent);
        return START_NOT_STICKY;
    }

    public Location getLocation() {
        return locationListeners[0].lastLocation != null ? locationListeners[0].lastLocation : locationListeners[1].lastLocation;
    }

    private class LocListener implements LocationListener {

        Location lastLocation;

        public LocListener(String provider) {
            this.lastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            lastLocation.set(location);
            Intent intent = new Intent(ARG_LOCATION_ACTION);
            intent.putExtra(ARG_LOCATION, location);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
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
    }

    public class LocationBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }


}
