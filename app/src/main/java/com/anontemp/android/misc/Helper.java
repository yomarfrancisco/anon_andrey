package com.anontemp.android.misc;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.anontemp.android.AnonApp;
import com.anontemp.android.Constants;
import com.anontemp.android.R;
import com.anontemp.android.model.Tweet;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jaydee on 01.07.17.
 */

public class Helper {
    public static final String TAG = "ANON";
    public static final String UUID = "uuid";
    public static final String DEF_VALUE = "";

    //    public static void upToDownTransition(Activity activity) {
//        activity.overridePendingTransition(R.anim.no_change, R.anim.slide_out_down);
//    }
    public static final String ARG_PAGE_TYPE = "pageType";
    public static final int PAGE_TYPE_TERMS = 1;
    public static final int PAGE_TYPE_PRIVACY = 2;
    public static final String ARG_TEMP_FLAG = "tempFalg";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String WELCOME_GIF = "welcome_gif";
    public static final String WELCOME_BOARD = "welcome_board";

    public static void downToUpTransition(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_up_dialog, R.anim.no_change);
    }

    public static void showSnackbar(final String text, Activity activity) {
        getSnackBar(text, activity).show();
    }

    public static Snackbar getSnackBar(final String text, Activity activity) {
        View container = activity.findViewById(android.R.id.content);
        if (container != null) {
            Snackbar snackbar = Snackbar.make(container, text, Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.BLACK);
            View snackbarView = snackbar.getView();
            int snackbarTextId = android.support.design.R.id.snackbar_text;
            TextView textView = snackbarView.findViewById(snackbarTextId);
            textView.setTextColor(Color.BLACK);
            snackbarView.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.white));
            return snackbar;
        }
        return null;
    }


    public static String getUuid() {
        return AnonApp.get().getSharedPreferences().getString(UUID, DEF_VALUE);
    }

    public static void setUuid(String uuid) {
        AnonApp.get().getSharedPreferences().edit().putString(UUID, uuid).apply();

    }

    public static boolean isGifAlreadyWelcome() {
        return AnonApp.get().getDefaultPrefs().getBoolean(WELCOME_GIF, false);
    }

    public static void seGiftWelcome() {
        AnonApp.get().getDefaultPrefs().edit().putBoolean(WELCOME_GIF, true).apply();
    }

    public static boolean isBoardWelcome() {
        return AnonApp.get().getDefaultPrefs().getBoolean(WELCOME_BOARD, false);
    }

    public static void setBoardWelcome() {
        AnonApp.get().getDefaultPrefs().edit().putBoolean(WELCOME_BOARD, true).apply();
    }

    public static Pair<String, String> getCredentials() {
        SharedPreferences s = AnonApp.get().getSharedPreferences();
        String mail = s.getString(EMAIL, "");
        String pass = s.getString(PASSWORD, "");
        if (mail.isEmpty() || pass.isEmpty())
            return null;

        return new Pair<>(mail, pass);
    }

    public static void setCredentials(Pair<String, String> pair) {
        AnonApp.get().getSharedPreferences().edit().putString(EMAIL, pair.first).putString(PASSWORD, pair.second).apply();

    }

    public static AuthCredential getAuthCredential() {
        SharedPreferences s = AnonApp.get().getSharedPreferences();
        return EmailAuthProvider
                .getCredential(s.getString(EMAIL, ""), s.getString(PASSWORD, ""));
    }

    public static Long getRealDate(String rawDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
        Date date;
        try {
            date = sdf.parse(rawDate);
        } catch (ParseException e) {
            return null;
        }
        return date.getTime();
    }

    public static String getVisibleDate(Long date) {

        if (date == null)
            return "";


        Calendar cS = Calendar.getInstance();
        Calendar cE = Calendar.getInstance();
        cE.setTimeInMillis(date);
        boolean sameDay = cS.get(Calendar.YEAR) == cE.get(Calendar.YEAR) &&
                cS.get(Calendar.MONTH) == cE.get(Calendar.MONTH) && cS.get(Calendar.DAY_OF_MONTH) == cE.get(Calendar.DAY_OF_MONTH);
        boolean sameYear = cS.get(Calendar.YEAR) == cE.get(Calendar.YEAR);

        SimpleDateFormat startFormat = new SimpleDateFormat("MMM d, yyyy, HH:mm", Locale.US);

        if (sameDay) {
            SimpleDateFormat onlyTime = new SimpleDateFormat("HH:mm", Locale.US);
            return onlyTime.format(date);
        } else if (sameYear) {
            SimpleDateFormat woYear = new SimpleDateFormat("MMM d, HH:mm", Locale.US);
            return woYear.format(date);
        } else {
            return startFormat.format(date);
        }
    }

    public static String getTweetFirstNameLabel(String regionName) {
        switch (regionName) {
            case Constants.JHB_EAST:
            case Constants.JHB_NORTH:
            case "WITS Braam Campus":
                return Constants.BRAAM_CAMP;
            case Constants.BARNATO_HALL:
            case Constants.COMMERCE_LAW_AND_MANAGEMENT:
                return Constants.WEST_CAMP;
            case Constants.CENTRAL_B_SENATE_H:
            case Constants.LIBRARY_LAWNS:
            case Constants.MATRIX_STUDENT_UNION:
            case Constants.JUBILEE_HALL:
                return Constants.EAST_CAMP;
            default:
                return Constants.WITS_CAMP;
        }
    }

    public static int getColorWithRegionName(String regionName) {

        for (GeoRegion region : Constants.LOCAL_REGIONS) {
            if (regionName.equals(region.getTitle())) {
                return region.getColor();
            }
        }
        return android.R.color.black;
    }

    public static boolean isShowableTweet(Tweet tweet) {
        if (tweet.getReporters() != null && tweet.getReporters().size() > 4) {
            return false;
        }

        return getTimeToLive(tweet.getRealDate(), tweet.getCountdown() == null ? 0 : tweet.getCountdown()) > 0;


    }

    public static long getTimeToLive(long date, int countdown) {


        return date + (countdown * 1000) - Calendar.getInstance().getTimeInMillis();

    }

    public static String formatTimeToLive(long date, Context context) {
        return context.getString(R.string.ttl_tweet, new SimpleDateFormat("H'h' mm'm'").format(new Date(date)));
    }

    public static GeoRegion getLocalRegion(Tweet tweet) {
        for (GeoRegion region : Constants.LOCAL_REGIONS)
            if (tweet.getRegionName().equals(region.getTitle())) {
                return region;
            }

        return Constants.LOCAL_REGIONS.get(0);
    }

    public static LatLng getLocation(String location) {

        if (TextUtils.isEmpty(location))
            return new LatLng(-26.1947, 28.0522);

        String[] words = location.split(",");
        if (words.length != 2) {
            return new LatLng(-26.1947, 28.0522);
        }

        return new LatLng(Double.valueOf(words[0]), Double.valueOf(words[1]));
    }

}
