package com.anontemp.android;

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
    public static final String ROSEBANK = "Rosebank";
    public static final String CHAMBER_OF_MINES = "Chamber of Mines";
    public static final String WARTENWEILLER_LIBRARY = "Wartenweiller Library";
    public static final String LIBRARY_LAWNS = "Library & Lawns";
    public static final String MATRIX_STUDENT_UNION = "Matrix & Student Union";
    public static final String JUBILEE_HALL = "Jubilee Hall";
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
    public static final ArrayList<GeoRegion> LOCAL_REGIONS = new ArrayList<>();
    public static final HashMap<String, GeoRegion> SNAPSHOT_REGIONS = new HashMap<>();
    public static final HashMap<String, Integer> GENDERS_UNICODE = new HashMap<>();
    public static final SparseArray<String> GENDERS_IMAGE = new SparseArray<>();
    public static final List<MarkerOptions> SNAPSHOT_MARKERS = new ArrayList<>();
    public static final List<CircleOptions> SNAPSHOT_CIRCLES = new ArrayList<>();
    public static final List<CircleOptions> LOCAL_CIRCLES = new ArrayList<>();
    public static final String GEOFENCE_ID = "geofenceId";
    public static final String MENU_WITS = "WITS NOTICE BOARD";
    public static final String MENU_LAW = "Comm, Law & Man";
    public static final String MENU_BARNATO = "Barnato";
    public static final String MENU_LIBRARY = "Library & Lawns";
    public static final String MENU_MATRIX = "Matrix";
    public static final String MENU_BLOCK = "SH & C.Block";
    public static final String MENU_JUBES = "Jubes";
    public static final String JHB_NORTH = "Jhb North";
    public static final String JHB_EAST = "Jhb East";
    public static final String CENTRAL_B_SENATE_H = "Central B. /Senate H.";
    public static final String BRAAM_CAMP = "Braam. Camp";
    public static final String WEST_CAMP = "West Camp.";
    public static final String EAST_CAMP = "East Camp.";
    public static final String WITS_CAMP = "Wits Braam Camp.";
    public static final int WITS_COLOR = 0xFF550000;
    public static final int LAW_COLOR = 0xFF005500;
    public static final int BARNATO_COLOR = 0xFF000055;
    public static final int LIBRARY_COLOR = 0xFF550055;
    public static final int MATRIX_COLOR = 0xFF005555;
    public static final int BLOCK_COLOR = 0xFF555500;
    public static final int JUBES_COLOR = 0xFF000000;
    private static final String PACKAGE_NAME = "com.anontemp.android";
    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    static {
        LOCAL_REGIONS.add(new GeoRegion(WITS_UNIVERSITY, new LatLng(-26.189460, 28.028117), 1000, MENU_WITS, WITS_COLOR));
        LOCAL_REGIONS.add(new GeoRegion(COMMERCE_LAW_AND_MANAGEMENT, new LatLng(-26.189360, 28.026503), 200, MENU_LAW, LAW_COLOR));
        LOCAL_REGIONS.add(new GeoRegion(BARNATO_HALL, new LatLng(-26.186972, 28.024893), 100, MENU_BARNATO, BARNATO_COLOR));
        LOCAL_REGIONS.add(new GeoRegion(LIBRARY_LAWNS, new LatLng(-26.191247, 28.030161), 100, MENU_LIBRARY, LIBRARY_COLOR));
        LOCAL_REGIONS.add(new GeoRegion(MATRIX_STUDENT_UNION, new LatLng(-26.189505, 28.030849), 100, MENU_MATRIX, MATRIX_COLOR));
        LOCAL_REGIONS.add(new GeoRegion(CENTRAL_BLOCK_SH, new LatLng(-26.193107, 28.030311), 100, MENU_BLOCK, BLOCK_COLOR));
        LOCAL_REGIONS.add(new GeoRegion(JUBILEE_HALL, new LatLng(-26.188344, 28.032598), 100, MENU_JUBES, JUBES_COLOR));

        for (GeoRegion region : LOCAL_REGIONS) {
            if (region.getTitle().equals(WITS_UNIVERSITY))
                continue;
            LOCAL_CIRCLES.add(getCircleOptions().center(region.getLatLng()).radius(region.getRadius()).
                    fillColor(0x40505050).strokeWidth(15).strokeColor(region.getColor()));
        }


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
        return new CircleOptions();
    }


    public enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }
}
