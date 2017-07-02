package com.anontemp.android;

import android.app.Activity;

/**
 * Created by jaydee on 01.07.17.
 */

public class Helper {
    public static final String TAG = "ANON";

    public static void upToDownTransition(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_out_down, R.anim.slide_up_dialog);
    }

    public static void downToUpTransition(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_up_dialog, R.anim.no_change);
    }

}
