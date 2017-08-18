package com.anontemp.android.misc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.anontemp.android.LocationService;

/**
 * Created by jaydee on 17.08.17.
 */

public class LocationReceiver extends BroadcastReceiver {

    private OnLocationReceivedListener listener;

    public LocationReceiver(OnLocationReceivedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (listener != null)
            listener.onReceive((Location) intent.getParcelableExtra(LocationService.ARG_LOCATION));

    }
}
