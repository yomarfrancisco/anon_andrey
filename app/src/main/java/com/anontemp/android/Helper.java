package com.anontemp.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

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

    public static void downToUpTransition(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_up_dialog, R.anim.no_change);
    }

    public static void showSnackbar(final String text, Activity activity) {
        View container = activity.findViewById(android.R.id.content);
        if (container != null) {
            Snackbar snackbar = Snackbar.make(container, text, Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.BLACK);
            View snackbarView = snackbar.getView();
            int snackbarTextId = android.support.design.R.id.snackbar_text;
            TextView textView = snackbarView.findViewById(snackbarTextId);
            textView.setTextColor(Color.BLACK);
            snackbarView.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.white));
            snackbar.show();
        }
    }


    public static String getUuid() {
        return AnonApp.get().getSharedPreferences().getString(UUID, DEF_VALUE);
    }

    public static void setUuid(String uuid) {
        AnonApp.get().getSharedPreferences().edit().putString(UUID, uuid).apply();

    }

    public static Pair<String, String> getCredentials() {
        SharedPreferences s = AnonApp.get().getSharedPreferences();
        return new Pair<>(s.getString(EMAIL, ""), s.getString(PASSWORD, ""));
    }

    public static void setCredentials(Pair<String, String> pair) {
        AnonApp.get().getSharedPreferences().edit().putString(EMAIL, pair.first).putString(PASSWORD, pair.second).apply();

    }


}
