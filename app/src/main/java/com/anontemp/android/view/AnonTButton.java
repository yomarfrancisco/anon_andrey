package com.anontemp.android.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.anontemp.android.misc.FontCache;

/**
 * Created by jaydee on 20.07.17.
 */

public class AnonTButton extends AppCompatButton {
    public AnonTButton(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public AnonTButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public AnonTButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("helvetica-neue.ttf", context);
        setTypeface(customFont);
    }
}
