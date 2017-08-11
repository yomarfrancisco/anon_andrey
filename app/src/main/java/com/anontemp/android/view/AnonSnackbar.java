package com.anontemp.android.view;

/**
 * Created by jaydee on 10.08.17.
 */

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;


@SuppressWarnings("SpellCheckingInspection")
public class AnonSnackbar {

    private LayoutInflater layoutInflater;
    private int layout;
    private int background;
    private View contentView;
    private LENGTH duration;
    private boolean swipe;

    private Snackbar snackbar;
    private Snackbar.SnackbarLayout snackbarView;

    private AnonSnackbar(Context context) {
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.duration = LENGTH.LONG;
        this.background = -1;
        this.layout = -1;
        this.swipe = true;
    }

    public static AnonSnackbar Builder(Context context) {
        return new AnonSnackbar(context);
    }

    public AnonSnackbar layout(int layout) {
        this.layout = layout;
        return this;
    }

    public AnonSnackbar background(int background) {
        this.background = background;
        return this;
    }

    public AnonSnackbar duration(LENGTH duration) {
        this.duration = duration;
        return this;
    }

    public AnonSnackbar swipe(boolean swipe) {
        this.swipe = swipe;
        return this;
    }

    public AnonSnackbar build(View view) {
        if (view == null) throw new CustomSnackbarException("view can not be null");
        if (layout == -1) throw new CustomSnackbarException("layout must be setted");
        switch (duration) {
            case INDEFINITE:
                snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);
                break;
            case SHORT:
                snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT);
                break;
            case LONG:
                snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG);
                break;
        }
        snackbarView = (Snackbar.SnackbarLayout) snackbar.getView();

        if (!swipe) {
            snackbarView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    snackbarView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }

        snackbarView.setPadding(0, 0, 0, 0);
        if (background != -1) snackbarView.setBackgroundResource(background);
        TextView text = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        text.setVisibility(View.INVISIBLE);
        TextView action = snackbarView.findViewById(android.support.design.R.id.snackbar_action);
        action.setVisibility(View.INVISIBLE);
        contentView = layoutInflater.inflate(layout, null);
        snackbarView.addView(contentView, 0);
        return this;
    }

    public void show() {
        snackbar.show();
    }

    public boolean isShowing() {
        return snackbar != null && snackbar.isShown();
    }

    public void dismiss() {
        if (snackbar != null) snackbar.dismiss();
    }

    public View getContentView() {
        return contentView;
    }

    public enum LENGTH {
        INDEFINITE, SHORT, LONG
    }

    public class CustomSnackbarException extends RuntimeException {

        public CustomSnackbarException(String detailMessage) {
            super(detailMessage);
        }

    }

}
