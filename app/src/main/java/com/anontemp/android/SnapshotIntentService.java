package com.anontemp.android;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.anontemp.android.misc.GeofenceErrorMessages;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;


public class SnapshotIntentService extends IntentService {

    public static final String ACTION_SNAPSHOT_ENTERED = "com.anontemp.android.service.snapshot.entered";
    public static final String ACTION_SNAPHOT_EXIT = "com.anontemp.android.service.snapshot.exit";
    private static final String TAG = "SnapshotIS";

    public SnapshotIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {


            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            if (triggeringGeofences != null && triggeringGeofences.size() > 0) {


                Intent intent1 = new Intent(ACTION_SNAPSHOT_ENTERED);
                intent1.putExtra(Constants.GEOFENCE_ID, triggeringGeofences.get(0).getRequestId());
                manager.sendBroadcast(intent1);
            }


            Log.i(TAG, getGeofenceTransitionDetails(geofenceTransition, triggeringGeofences));
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            Intent intent1 = new Intent(ACTION_SNAPHOT_EXIT);
            manager.sendBroadcast(intent1);
        } else {

            Log.d(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }

    }

    private String getGeofenceTransitionDetails(
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }


    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType A transition type constant defined in Geofence
     * @return A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }

}
