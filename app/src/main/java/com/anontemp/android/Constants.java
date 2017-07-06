package com.anontemp.android;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by jaydee on 05.07.17.
 */

final class Constants {


    public final static String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    static final HashMap<String, Region> REGIONS = new HashMap<>();
    static final String GEOFENCE_ID = "geofenceId";
    private static final String PACKAGE_NAME = "com.anontemp.android";
    static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    static {
        REGIONS.put("MTV Base", new Region(new LatLng(-26.115230, 28.032296), 300));

        REGIONS.put("WITS University", new Region(new LatLng(-26.189460, 28.028117), 1000));
    }

    private Constants() {
    }


}
