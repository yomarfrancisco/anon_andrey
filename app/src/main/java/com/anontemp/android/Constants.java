package com.anontemp.android;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by jaydee on 05.07.17.
 */

final class Constants {


    public final static String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static final String WITS_UNIVERSITY = "WITS University";
    public static final String MTV_BASE = "MTV Base";
    static final HashMap<String, Integer> MOODS = new HashMap<>();
    static final HashMap<String, GeoRegion> REGIONS = new HashMap<>();
    static final HashMap<String, Integer> GENDERS = new HashMap<>();
    static final String GEOFENCE_ID = "geofenceId";
    private static final String PACKAGE_NAME = "com.anontemp.android";
    static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    static {
        REGIONS.put(MTV_BASE, new GeoRegion(new LatLng(-26.115230, 28.032296), 300));

        REGIONS.put(WITS_UNIVERSITY, new GeoRegion(new LatLng(-26.189460, 28.028117), 1000));

        //MOODS

        MOODS.put("\uD83D\uDE0D", R.mipmap.ic_inlove);
        MOODS.put("\uD83D\uDE0E", R.mipmap.ic_sunglasses);
        MOODS.put("\uD83D\uDE2E", R.mipmap.ic_open_mouth);
        MOODS.put("\uD83D\uDE4F", R.mipmap.ic_hands);
        MOODS.put("\uD83D\uDE02", R.mipmap.ic_tears);
        MOODS.put("\uD83D\uDE21", R.mipmap.ic_pouting_face);
        MOODS.put("\uD83D\uDD1E", R.mipmap.ic_no_eighteen);
        MOODS.put("\uD83D\uDE10", R.mipmap.ic_neutral_face);
        MOODS.put("☹️", R.mipmap.ic_frowning_face);
        MOODS.put("\uD83D\uDD25", R.mipmap.ic_fire);
        MOODS.put("\uD83D\uDC4F", R.mipmap.ic_clapping_hands);
        MOODS.put("\uD83D\uDE4C", R.mipmap.ic_raising_hands);

        //GENDERS

        GENDERS.put("?", R.mipmap.ic_question);
        GENDERS.put("\uD83C\uDF46", R.mipmap.ic_aubergine);
        GENDERS.put("\uD83C\uDF51", R.mipmap.ic_peach);
        GENDERS.put("\uD83C\uDF3C", R.mipmap.ic_blossom);


    }

    private Constants() {
    }


}
