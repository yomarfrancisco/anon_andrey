package com.anontemp.android.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import com.anontemp.android.FontCache;

/**
 * Created by jaydee on 20.07.17.
 */

public class AnonProgress extends AlertDialog {


    public AnonProgress(Context context) {
        super(context);
    }

    public AnonProgress(Context context, int theme) {
        super(context, theme);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView view = this.findViewById(android.R.id.message);
        if (view != null) {
            // Shouldn't be null. Just to be paranoid enough.
            view.setTypeface(FontCache.getTypeface("helvetica-neue-bold.ttf", getContext()));
        }


    }


}
