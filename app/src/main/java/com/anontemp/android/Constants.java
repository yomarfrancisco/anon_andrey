package com.anontemp.android;

import android.graphics.Color;
import android.util.SparseArray;

import com.anontemp.android.misc.GeoRegion;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.apache.commons.text.StringEscapeUtils.escapeJava;
import static org.apache.commons.text.StringEscapeUtils.unescapeJava;

/**
 * Created by jaydee on 05.07.17.
 */

public final class Constants {


    public final static String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static final String WITS_UNIVERSITY = "WITS University";
    public static final String WITS_UNIVERSITY_LOWCASE = "Wits University";
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
    public static final String LIBRARY_LAWNS = "Library & Lawns";
    public static final String MATRIX_STUDENT_UNION = "Matrix & Student Union";
    public static final String JUBILEE_HALL = "Jubilee Hall";
    public static final String MENS_RES = "Mens' Res";
    public static final String BRAAM_CAMPUS = "Braam Campus";
    public static final String NEIGHBOURGOODS = "Neighbourgoods";
    public static final String MTV_BASE_HQ = "MTV Base HQ";
    public static final String MELVILLE = "Melville";
    public static final String FNB_BUILDING = "FNB Building";
    public static final String COMMERCE_LAW_AND_MANAGEMENT = "Comm. Law & Management";
    public static final String OLIVER_SHREINER_LAW_SCHOOL = "Oliver Shreiner Law School";
    public static final String DAVID_WEBSTER_RES = "David Webster Res";
    public static final String BARNATO_HALL = "Barnato Hall";
    public static final String CENTRAL_BLOCK_SH = "Central Block/SH";
    public static final String COLLEGE_HOUSE = "College House";
    public static final String[] WARN_WORDS = new String[]{"bomb", "bomb "};
    public static final String[] DANGEROUS_WORDS = new String[]{"kill", "explode", "latest1", "kill ", "explode ", "latest1 "};
    public static final HashMap<String, Integer> MOODS_UNICODE = new HashMap<>();
    public static final SparseArray<String> MOODS_IMAGE = new SparseArray<>();
    public static final HashMap<String, GeoRegion> LOCAL_REGIONS = new HashMap<>();
    public static final HashMap<String, GeoRegion> SNAPSHOT_REGIONS = new HashMap<>();
    public static final HashMap<String, Integer> GENDERS_UNICODE = new HashMap<>();
    public static final SparseArray<String> GENDERS_IMAGE = new SparseArray<>();
    public static final List<MarkerOptions> SNAPSHOT_MARKERS = new ArrayList<>();
    public static final List<CircleOptions> SNAPSHOT_CIRCLES = new ArrayList<>();
    public static final List<CircleOptions> LOCAL_CIRCLES = new ArrayList<>();
    public static final String GEOFENCE_ID = "geofenceId";
    private static final String PACKAGE_NAME = "com.anontemp.android";
    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    static {
        LOCAL_REGIONS.put(WITS_UNIVERSITY, new GeoRegion(new LatLng(-26.189460, 28.028117), 1000));
        LOCAL_REGIONS.put(COMMERCE_LAW_AND_MANAGEMENT, new GeoRegion(new LatLng(-26.189360, 28.026503), 200));
        LOCAL_REGIONS.put(BARNATO_HALL, new GeoRegion(new LatLng(-26.186972, 28.024893), 100));
        LOCAL_REGIONS.put(CENTRAL_BLOCK_SH, new GeoRegion(new LatLng(-26.193107, 28.030311), 100));
        LOCAL_REGIONS.put(LIBRARY_LAWNS, new GeoRegion(new LatLng(-26.191247, 28.030161), 100));
        LOCAL_REGIONS.put(MATRIX_STUDENT_UNION, new GeoRegion(new LatLng(-26.189505, 28.030849), 100));
        LOCAL_REGIONS.put(JUBILEE_HALL, new GeoRegion(new LatLng(-26.188344, 28.032598), 100));

        LOCAL_CIRCLES.add(getCircleOptions().center(LOCAL_REGIONS.get(COMMERCE_LAW_AND_MANAGEMENT).getLatLng()).radius(LOCAL_REGIONS.get(COMMERCE_LAW_AND_MANAGEMENT).getRadius()));
        LOCAL_CIRCLES.add(getCircleOptions().center(LOCAL_REGIONS.get(BARNATO_HALL).getLatLng()).radius(LOCAL_REGIONS.get(BARNATO_HALL).getRadius()));
        LOCAL_CIRCLES.add(getCircleOptions().center(LOCAL_REGIONS.get(CENTRAL_BLOCK_SH).getLatLng()).radius(LOCAL_REGIONS.get(CENTRAL_BLOCK_SH).getRadius()));
        LOCAL_CIRCLES.add(getCircleOptions().center(LOCAL_REGIONS.get(LIBRARY_LAWNS).getLatLng()).radius(LOCAL_REGIONS.get(LIBRARY_LAWNS).getRadius()));
        LOCAL_CIRCLES.add(getCircleOptions().center(LOCAL_REGIONS.get(MATRIX_STUDENT_UNION).getLatLng()).radius(LOCAL_REGIONS.get(MATRIX_STUDENT_UNION).getRadius()));
        LOCAL_CIRCLES.add(getCircleOptions().center(LOCAL_REGIONS.get(JUBILEE_HALL).getLatLng()).radius(LOCAL_REGIONS.get(JUBILEE_HALL).getRadius()));


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



        //SNAPSHOTS
        SNAPSHOT_REGIONS.put(BRAAM_CAMPUS, new GeoRegion(new LatLng(-26.189460, 28.028117), 750));
        SNAPSHOT_REGIONS.put(NEIGHBOURGOODS, new GeoRegion(new LatLng(-26.193872, 28.035106), 150));
        SNAPSHOT_REGIONS.put(MTV_BASE_HQ, new GeoRegion(new LatLng(-26.115230, 28.032296), 300));
        SNAPSHOT_REGIONS.put(MELVILLE, new GeoRegion(new LatLng(-26.175025, 28.007747), 500));
        SNAPSHOT_REGIONS.put(ROSEBANK, new GeoRegion(new LatLng(-26.144726, 28.039409), 500));
        SNAPSHOT_REGIONS.put(FNB_BUILDING, new GeoRegion(new LatLng(-26.188618, 28.026288), 100));
        SNAPSHOT_REGIONS.put(COMMERCE_LAW_AND_MANAGEMENT, new GeoRegion(new LatLng(-26.189360, 28.026503), 100));
        SNAPSHOT_REGIONS.put(OLIVER_SHREINER_LAW_SCHOOL, new GeoRegion(new LatLng(-26.188618, 28.025387), 70));
        SNAPSHOT_REGIONS.put(DAVID_WEBSTER_RES, new GeoRegion(new LatLng(-26.186799, 28.026202), 75));
        SNAPSHOT_REGIONS.put(BARNATO_HALL, new GeoRegion(new LatLng(-26.186972, 28.024893), 75));
        SNAPSHOT_REGIONS.put(CHAMBER_OF_MINES, new GeoRegion(new LatLng(-26.191584, 28.027050), 100));
        SNAPSHOT_REGIONS.put(CENTRAL_BLOCK_SH, new GeoRegion(new LatLng(-26.192507, 28.030311), 100));
        SNAPSHOT_REGIONS.put(WARTENWEILLER_LIBRARY, new GeoRegion(new LatLng(-26.191083, 28.030816), 50));
        SNAPSHOT_REGIONS.put(LIBRARY_LAWNS, new GeoRegion(new LatLng(-26.190717, 28.030161), 100));
        SNAPSHOT_REGIONS.put(MATRIX_STUDENT_UNION, new GeoRegion(new LatLng(-26.189841, 28.030633), 75));
        SNAPSHOT_REGIONS.put(JUBILEE_HALL, new GeoRegion(new LatLng(-26.188344, 28.032398), 75));
        SNAPSHOT_REGIONS.put(COLLEGE_HOUSE, new GeoRegion(new LatLng(-26.189028, 28.030472), 75));


        SNAPSHOT_MARKERS.add(new MarkerOptions().position(SNAPSHOT_REGIONS.get(BRAAM_CAMPUS).getLatLng()).title(WITS_UNIVERSITY).snippet(BRAAM_CAMPUS));
        SNAPSHOT_MARKERS.add(new MarkerOptions().position(SNAPSHOT_REGIONS.get(NEIGHBOURGOODS).getLatLng()).title(NEIGHBOURGOODS));
        SNAPSHOT_MARKERS.add(new MarkerOptions().position(SNAPSHOT_REGIONS.get(MTV_BASE_HQ).getLatLng()).title(MTV_BASE_HQ));
        SNAPSHOT_MARKERS.add(new MarkerOptions().position(SNAPSHOT_REGIONS.get(MELVILLE).getLatLng()).title(MELVILLE));
        SNAPSHOT_MARKERS.add(new MarkerOptions().position(SNAPSHOT_REGIONS.get(ROSEBANK).getLatLng()).title(ROSEBANK));
        SNAPSHOT_MARKERS.add(new MarkerOptions().position(SNAPSHOT_REGIONS.get(FNB_BUILDING).getLatLng()).title(FNB_BUILDING));
        SNAPSHOT_MARKERS.add(new MarkerOptions().position(SNAPSHOT_REGIONS.get(COMMERCE_LAW_AND_MANAGEMENT).getLatLng()).title(COMMERCE_LAW_AND_MANAGEMENT));
        SNAPSHOT_MARKERS.add(new MarkerOptions().position(SNAPSHOT_REGIONS.get(OLIVER_SHREINER_LAW_SCHOOL).getLatLng()).title(OLIVER_SHREINER_LAW_SCHOOL));
        SNAPSHOT_MARKERS.add(new MarkerOptions().position(SNAPSHOT_REGIONS.get(DAVID_WEBSTER_RES).getLatLng()).title(DAVID_WEBSTER_RES));
        SNAPSHOT_MARKERS.add(new MarkerOptions().position(SNAPSHOT_REGIONS.get(BARNATO_HALL).getLatLng()).title(BARNATO_HALL));
        SNAPSHOT_MARKERS.add(new MarkerOptions().position(SNAPSHOT_REGIONS.get(CHAMBER_OF_MINES).getLatLng()).title(CHAMBER_OF_MINES));
        SNAPSHOT_MARKERS.add(new MarkerOptions().position(SNAPSHOT_REGIONS.get(CENTRAL_BLOCK_SH).getLatLng()).title(CENTRAL_BLOCK_SH));
        SNAPSHOT_MARKERS.add(new MarkerOptions().position(SNAPSHOT_REGIONS.get(WARTENWEILLER_LIBRARY).getLatLng()).title(WARTENWEILLER_LIBRARY));
        SNAPSHOT_MARKERS.add(new MarkerOptions().position(SNAPSHOT_REGIONS.get(LIBRARY_LAWNS).getLatLng()).title(LIBRARY_LAWNS));
        SNAPSHOT_MARKERS.add(new MarkerOptions().position(SNAPSHOT_REGIONS.get(MATRIX_STUDENT_UNION).getLatLng()).title(MATRIX_STUDENT_UNION));
        SNAPSHOT_MARKERS.add(new MarkerOptions().position(SNAPSHOT_REGIONS.get(JUBILEE_HALL).getLatLng()).title(JUBILEE_HALL));
        SNAPSHOT_MARKERS.add(new MarkerOptions().position(SNAPSHOT_REGIONS.get(COLLEGE_HOUSE).getLatLng()).title(COLLEGE_HOUSE));


        SNAPSHOT_CIRCLES.add(getCircleOptions().center(SNAPSHOT_REGIONS.get(NEIGHBOURGOODS).getLatLng()).radius(SNAPSHOT_REGIONS.get(NEIGHBOURGOODS).getRadius()));
        SNAPSHOT_CIRCLES.add(getCircleOptions().center(SNAPSHOT_REGIONS.get(MELVILLE).getLatLng()).radius(SNAPSHOT_REGIONS.get(MELVILLE).getRadius()));
        SNAPSHOT_CIRCLES.add(getCircleOptions().center(SNAPSHOT_REGIONS.get(ROSEBANK).getLatLng()).radius(SNAPSHOT_REGIONS.get(ROSEBANK).getRadius()));
        SNAPSHOT_CIRCLES.add(getCircleOptions().center(SNAPSHOT_REGIONS.get(FNB_BUILDING).getLatLng()).radius(SNAPSHOT_REGIONS.get(FNB_BUILDING).getRadius()));
        SNAPSHOT_CIRCLES.add(getCircleOptions().center(SNAPSHOT_REGIONS.get(COMMERCE_LAW_AND_MANAGEMENT).getLatLng()).radius(SNAPSHOT_REGIONS.get(COMMERCE_LAW_AND_MANAGEMENT).getRadius()));
        SNAPSHOT_CIRCLES.add(getCircleOptions().center(SNAPSHOT_REGIONS.get(OLIVER_SHREINER_LAW_SCHOOL).getLatLng()).radius(SNAPSHOT_REGIONS.get(OLIVER_SHREINER_LAW_SCHOOL).getRadius()));
        SNAPSHOT_CIRCLES.add(getCircleOptions().center(SNAPSHOT_REGIONS.get(DAVID_WEBSTER_RES).getLatLng()).radius(SNAPSHOT_REGIONS.get(DAVID_WEBSTER_RES).getRadius()));
        SNAPSHOT_CIRCLES.add(getCircleOptions().center(SNAPSHOT_REGIONS.get(BARNATO_HALL).getLatLng()).radius(SNAPSHOT_REGIONS.get(BARNATO_HALL).getRadius()));
        SNAPSHOT_CIRCLES.add(getCircleOptions().center(SNAPSHOT_REGIONS.get(CHAMBER_OF_MINES).getLatLng()).radius(SNAPSHOT_REGIONS.get(CHAMBER_OF_MINES).getRadius()));
        SNAPSHOT_CIRCLES.add(getCircleOptions().center(SNAPSHOT_REGIONS.get(CENTRAL_BLOCK_SH).getLatLng()).radius(SNAPSHOT_REGIONS.get(CENTRAL_BLOCK_SH).getRadius()));
        SNAPSHOT_CIRCLES.add(getCircleOptions().center(SNAPSHOT_REGIONS.get(WARTENWEILLER_LIBRARY).getLatLng()).radius(SNAPSHOT_REGIONS.get(WARTENWEILLER_LIBRARY).getRadius()));
        SNAPSHOT_CIRCLES.add(getCircleOptions().center(SNAPSHOT_REGIONS.get(LIBRARY_LAWNS).getLatLng()).radius(SNAPSHOT_REGIONS.get(LIBRARY_LAWNS).getRadius()));
        SNAPSHOT_CIRCLES.add(getCircleOptions().center(SNAPSHOT_REGIONS.get(MATRIX_STUDENT_UNION).getLatLng()).radius(SNAPSHOT_REGIONS.get(MATRIX_STUDENT_UNION).getRadius()));
        SNAPSHOT_CIRCLES.add(getCircleOptions().center(SNAPSHOT_REGIONS.get(JUBILEE_HALL).getLatLng()).radius(SNAPSHOT_REGIONS.get(JUBILEE_HALL).getRadius()));
        SNAPSHOT_CIRCLES.add(getCircleOptions().center(SNAPSHOT_REGIONS.get(COLLEGE_HOUSE).getLatLng()).radius(SNAPSHOT_REGIONS.get(COLLEGE_HOUSE).getRadius()));


    }

    private Constants() {
    }

    public static CircleOptions getCircleOptions() {
        return new CircleOptions().fillColor(Color.argb(100, 128, 128, 128)).strokeColor(Color.argb(140, 20, 0, 255)).strokeWidth(5);
    }


    public enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }
}
