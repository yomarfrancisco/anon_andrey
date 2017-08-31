package com.anontemp.android;

import com.anontemp.android.misc.GeoRegion;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final Map<Integer, Integer> REGION_COLORS = new HashMap<>();
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
                    fillColor(0x40505050).strokeWidth(10).strokeColor(region.getColor()));
        }


        REGION_COLORS.put(WITS_COLOR, R.color.wits_color);
        REGION_COLORS.put(LAW_COLOR, R.color.law_color);
        REGION_COLORS.put(BARNATO_COLOR, R.color.barnato_color);
        REGION_COLORS.put(LIBRARY_COLOR, R.color.library_color);
        REGION_COLORS.put(MATRIX_COLOR, R.color.matrix_colo);
        REGION_COLORS.put(BLOCK_COLOR, R.color.block_color);
        REGION_COLORS.put(JUBES_COLOR, R.color.jubes_color);




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
