package com.anontemp.android;

import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;

import com.securepreferences.SecurePreferences;

/**
 * Created by jaydee on 01.07.17.
 */

public class AnonApp extends MultiDexApplication {
    protected static AnonApp instance;
    private SecurePreferences mSecurePrefs;

    public AnonApp() {
        super();
        instance = this;
    }

    public static AnonApp get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }


    public SharedPreferences getSharedPreferences() {
        if (mSecurePrefs == null) {
            mSecurePrefs = new SecurePreferences(this, "", "sec_prefs.xml");
            SecurePreferences.setLoggingEnabled(true);
        }
        return mSecurePrefs;
    }
}
