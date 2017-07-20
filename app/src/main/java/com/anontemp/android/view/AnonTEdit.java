package com.anontemp.android.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.anontemp.android.FontCache;

/**
 * Created by jaydee on 20.07.17.
 */

public class AnonTEdit extends android.support.v7.widget.AppCompatEditText {
    public AnonTEdit(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public AnonTEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public AnonTEdit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("helvetica-neue.ttf", context);
        setTypeface(customFont);
    }

}
