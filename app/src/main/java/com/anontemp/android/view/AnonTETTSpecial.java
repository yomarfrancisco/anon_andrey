package com.anontemp.android.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;

import com.anontemp.android.misc.FontCache;

/**
 * Created by jaydee on 20.07.17.
 */

public class AnonTETTSpecial extends TextInputEditText {
    public AnonTETTSpecial(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public AnonTETTSpecial(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public AnonTETTSpecial(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("DK_Cinnabar_Brush.ttf", context);
        setTypeface(customFont);
    }
}
