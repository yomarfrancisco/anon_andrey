package com.anontemp.android.misc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.anontemp.android.AnonApp;
import com.anontemp.android.R;
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
    public static final String WELCOME = "welcome";

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

    public static boolean isAlreadyWelcome() {
        return AnonApp.get().getDefaultPrefs().getBoolean(WELCOME, false);
    }

    public static void setWelcome() {
        AnonApp.get().getDefaultPrefs().edit().putBoolean(WELCOME, true).apply();
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


}
