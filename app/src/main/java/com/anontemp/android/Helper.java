package com.anontemp.android;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by jaydee on 01.07.17.
 */

public class Helper {
    public static final String TAG = "ANON";

    public static void upToDownTransition(Activity activity) {
        activity.overridePendingTransition(R.anim.no_change, R.anim.slide_out_down);
    }

    public static void downToUpTransition(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_up_dialog, R.anim.no_change);
    }

    public static void showSnackbar(final String text, Activity activity) {
        View container = activity.findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

}
