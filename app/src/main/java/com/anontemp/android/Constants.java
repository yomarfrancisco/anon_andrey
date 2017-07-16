package com.anontemp.android;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import static org.apache.commons.text.StringEscapeUtils.escapeJava;
import static org.apache.commons.text.StringEscapeUtils.unescapeJava;

/**
 * Created by jaydee on 05.07.17.
 */

final class Constants {


    public final static String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static final String WITS_UNIVERSITY = "WITS University";
    public static final String MTV_BASE = "MTV Base";
    public static final String TOTAL_GAS_STATION = "Total gas station";
    public static final String VIRGIN_ALICE_LANE = "Virgin Alice Lane";
    public static final String FAMILY = "Family";
    public static final String TASHAS_BEDFORDVIEW = "Tashas Bedfordview";
    public static final String JHB_NORTH = "Jhb North";
    public static final String JHB_EAST = "Jhb East";
    public static final String THE_ZONE = "The Zone";
    public static final String ROSEBANK = "Rosebank";
    public static final String CHAMBER_OF_MINES = "Chamber of Mines";
    public static final String CENTRAL_B_SENATE_H = "Central B. /Senate H.";
    public static final String WARTENWEILLER_LIBRARY = "Wartenweiller Library";
    public static final String LIBRARY_LAWNS = "Library Lawns";
    public static final String MATRIX_STUDENT_UNION = "Matrix & Student Union";
    public static final String JUBILEE_HALL = "Jubilee Hall";
    public static final String MENS_RES = "Mens' Res";
    static final HashMap<String, Integer> MOODS_UNICODE = new HashMap<>();
    static final HashMap<Integer, String> MOODS_IMAGE = new HashMap<>();
    static final HashMap<String, GeoRegion> REGIONS = new HashMap<>();
    static final HashMap<String, GeoRegion> SUB_REGIONS = new HashMap<>();
    static final HashMap<String, Integer> GENDERS_UNICODE = new HashMap<>();
    static final HashMap<Integer, String> GENDERS_IMAGE = new HashMap<>();
    static final String GEOFENCE_ID = "geofenceId";
    private static final String PACKAGE_NAME = "com.anontemp.android";
    static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";
    static final String TWEET_GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".TWEET_GEOFENCES_ADDED_KEY";

    static {
        REGIONS.put(MTV_BASE, new GeoRegion(new LatLng(-26.115230, 28.032296), 300));

        REGIONS.put(WITS_UNIVERSITY, new GeoRegion(new LatLng(-26.189460, 28.028117), 1000));

        //MOODS_UNICODE

        MOODS_UNICODE.put(escapeJava("\uD83D\uDE0D"), R.mipmap.ic_inlove);
        MOODS_UNICODE.put(escapeJava("\uD83D\uDE0E"), R.mipmap.ic_sunglasses);
        MOODS_UNICODE.put(escapeJava("\uD83D\uDE2E"), R.mipmap.ic_open_mouth);
        MOODS_UNICODE.put(escapeJava("\uD83D\uDE4F"), R.mipmap.ic_hands);
        MOODS_UNICODE.put(escapeJava("\uD83D\uDE02"), R.mipmap.ic_tears);
        MOODS_UNICODE.put(escapeJava("\uD83D\uDE21"), R.mipmap.ic_pouting_face);
        MOODS_UNICODE.put(escapeJava("\uD83D\uDD1E"), R.mipmap.ic_no_eighteen);
        MOODS_UNICODE.put(escapeJava("\uD83D\uDE10"), R.mipmap.ic_neutral_face);
        MOODS_UNICODE.put(escapeJava("☹️"), R.mipmap.ic_frowning_face);
        MOODS_UNICODE.put(escapeJava("\uD83D\uDD25"), R.mipmap.ic_fire);
        MOODS_UNICODE.put(escapeJava("\uD83D\uDC4F"), R.mipmap.ic_clapping_hands);
        MOODS_UNICODE.put(escapeJava("\uD83D\uDE4C"), R.mipmap.ic_raising_hands);
        MOODS_UNICODE.put(escapeJava("\uD83D\uDE36"), R.mipmap.ic_face);

        MOODS_IMAGE.put(R.mipmap.ic_inlove, unescapeJava("\uD83D\uDE0D"));
        MOODS_IMAGE.put(R.mipmap.ic_sunglasses, unescapeJava("\uD83D\uDE0E"));
        MOODS_IMAGE.put(R.mipmap.ic_open_mouth, unescapeJava("\uD83D\uDE2E"));
        MOODS_IMAGE.put(R.mipmap.ic_hands, unescapeJava("\uD83D\uDE4F"));
        MOODS_IMAGE.put(R.mipmap.ic_tears, unescapeJava("\uD83D\uDE02"));
        MOODS_IMAGE.put(R.mipmap.ic_pouting_face, unescapeJava("\uD83D\uDE21"));
        MOODS_IMAGE.put(R.mipmap.ic_no_eighteen, unescapeJava("\uD83D\uDD1E"));
        MOODS_IMAGE.put(R.mipmap.ic_neutral_face, unescapeJava("\uD83D\uDE10"));
        MOODS_IMAGE.put(R.mipmap.ic_frowning_face, unescapeJava("☹️"));
        MOODS_IMAGE.put(R.mipmap.ic_fire, unescapeJava("\uD83D\uDD25"));
        MOODS_IMAGE.put(R.mipmap.ic_clapping_hands, unescapeJava("\uD83D\uDC4F"));
        MOODS_IMAGE.put(R.mipmap.ic_raising_hands, unescapeJava("\uD83D\uDE4C"));
        MOODS_IMAGE.put(R.mipmap.ic_face, unescapeJava("\uD83D\uDE36"));

        //GENDERS_UNICODE

        GENDERS_UNICODE.put(escapeJava("?"), R.mipmap.ic_question);
        GENDERS_UNICODE.put(escapeJava("\uD83C\uDF46"), R.mipmap.ic_aubergine);
        GENDERS_UNICODE.put(escapeJava("\uD83C\uDF51"), R.mipmap.ic_peach);
        GENDERS_UNICODE.put(escapeJava("\uD83C\uDF3C"), R.mipmap.ic_blossom);


        GENDERS_IMAGE.put(R.mipmap.ic_question, unescapeJava("?"));
        GENDERS_IMAGE.put(R.mipmap.ic_aubergine, unescapeJava("\uD83C\uDF46"));
        GENDERS_IMAGE.put(R.mipmap.ic_peach, unescapeJava("\uD83C\uDF51"));
        GENDERS_IMAGE.put(R.mipmap.ic_blossom, unescapeJava("\uD83C\uDF3C"));


        //SUBREGIONS


        SUB_REGIONS.put(TOTAL_GAS_STATION, new GeoRegion(new LatLng(-26.150344, 28.124033), 500));
        SUB_REGIONS.put(VIRGIN_ALICE_LANE, new GeoRegion(new LatLng(-26.1063, 28.0499), 300));
        SUB_REGIONS.put(FAMILY, new GeoRegion(new LatLng(-26.1832, 28.1401), 100));
        SUB_REGIONS.put(TASHAS_BEDFORDVIEW, new GeoRegion(new LatLng(-26.183181, 28.135359), 200));
        SUB_REGIONS.put(JHB_NORTH, new GeoRegion(new LatLng(-26.058322, 28.021814), 5000));
        SUB_REGIONS.put(JHB_EAST, new GeoRegion(new LatLng(-26.158820, 28.169276), 6000));
        SUB_REGIONS.put(THE_ZONE, new GeoRegion(new LatLng(-26.145805, 28.042311), 250));
        SUB_REGIONS.put(ROSEBANK, new GeoRegion(new LatLng(-26.144726, 28.039409), 500));
        SUB_REGIONS.put(CHAMBER_OF_MINES, new GeoRegion(new LatLng(-26.191584, 28.027050), 100));
        SUB_REGIONS.put(CENTRAL_B_SENATE_H, new GeoRegion(new LatLng(-26.192507, 28.030311), 100));
        SUB_REGIONS.put(WARTENWEILLER_LIBRARY, new GeoRegion(new LatLng(-26.191083, 28.030816), 50));
        SUB_REGIONS.put(LIBRARY_LAWNS, new GeoRegion(new LatLng(-26.190717, 28.030161), 100));
        SUB_REGIONS.put(MATRIX_STUDENT_UNION, new GeoRegion(new LatLng(-26.189841, 28.030633), 75));
        SUB_REGIONS.put(JUBILEE_HALL, new GeoRegion(new LatLng(-26.188344, 28.032398), 75));
        SUB_REGIONS.put(MENS_RES, new GeoRegion(new LatLng(-26.189028, 28.030472), 75));

    }

    private Constants() {
    }


    public enum PendingGeofenceTask {
        ADD, NONE
    }
}
