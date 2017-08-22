package com.anontemp.android.misc;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.anontemp.android.R;
import com.anontemp.android.view.AnonTView;

/**
 * Created by jaydee on 08.08.17.
 */

public class AnonAlert extends DialogFragment {

    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    public static final String BUTTON = "button";
    View d;
    private AlertListener listener;
    private int title;
    private int message;
    private int button;

    public static AnonAlert newInstance(int title, int message, int button) {
        AnonAlert f = new AnonAlert();
        f.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        Bundle args = new Bundle();
        args.putInt(TITLE, title);
        args.putInt(MESSAGE, message);
        args.putInt(BUTTON, button);
        f.setArguments(args);
        return f;
    }

    public void setOnButtonListener(AlertListener alertListener) {
        listener = alertListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getInt(TITLE, 0);
        message = getArguments().getInt(MESSAGE, 0);
        button = getArguments().getInt(BUTTON, 0);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        d = LayoutInflater.from(getActivity()).inflate(R.layout.c_alert_ext, null);
        final AlertDialog.Builder build = new AlertDialog.Builder(getContext(), R.style.MyTheme);
        build.setView(d);
        TextView tvTitle = d.findViewById(R.id.title);
        if (title != 0) {

            Typeface bold = FontCache.getTypeface("helvetica-neue-bold.ttf", getContext());
            tvTitle.setTypeface(bold);
            tvTitle.setText(title);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
        TextView tv = d.findViewById(R.id.text);
        Typeface thin = FontCache.getTypeface("HelveticaNeue-Thin.otf", getContext());
        Typeface normal = FontCache.getTypeface("helvetica-neue.otf", getContext());
        tv.setTypeface(thin);
        tv.setText(message);


        AnonTView btn = d.findViewById(R.id.tvButton);
        btn.setTypeface(normal);
        btn.setText(button);


        final AlertDialog dialog = build.setCancelable(false).create();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onButton();
                }
            }
        });

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        forceWrapContent(d);
    }

    protected void forceWrapContent(View v) {
        // Start with the provided view
        View current = v;

        // Travel up the tree until fail, modifying the LayoutParams
        do {
            // Get the parent
            ViewParent parent = current.getParent();

            // Check if the parent exists
            if (parent != null) {
                // Get the view
                try {
                    current = (View) parent;
                } catch (ClassCastException e) {
                    // This will happen when at the top view, it cannot be cast to a View
                    break;
                }

                // Modify the layout
                current.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        } while (current.getParent() != null);

        // Request a layout to be re-done
        current.requestLayout();
    }

    public interface AlertListener {
        void onButton();
    }
}

