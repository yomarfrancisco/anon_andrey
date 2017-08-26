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
import android.widget.Button;
import android.widget.TextView;

import com.anontemp.android.R;

/**
 * Created by jaydee on 08.08.17.
 */

public class AnonDialog extends DialogFragment {

    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    public static final String OK = "ok";
    public static final String CANCEL = "cancel";
    View d;
    private DialogListener listener;
    private int title;
    private int message;
    private int ok;
    private int cancel;


    public static AnonDialog newInstance(int title, int message, int ok, int cancel) {
        AnonDialog f = new AnonDialog();
        f.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        Bundle args = new Bundle();
        args.putInt(TITLE, title);
        args.putInt(MESSAGE, message);
        args.putInt(OK, ok);
        args.putInt(CANCEL, cancel);
        f.setArguments(args);
        return f;
    }


    public AnonDialog addListener(DialogListener dialogListener) {
        this.listener = dialogListener;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getInt(TITLE, 0);
        message = getArguments().getInt(MESSAGE, 0);
        ok = getArguments().getInt(OK, 0);
        cancel = getArguments().getInt(CANCEL, 0);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (listener == null) {
            try {
                this.listener = ((DialogListener) getActivity());
            } catch (ClassCastException e) {
                throw new ClassCastException("Activity must implement DialogListener.");
            }
        }

        d = LayoutInflater.from(getActivity()).inflate(R.layout.c_dial, null);
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


        Button btnOk = d.findViewById(R.id.btnOk);
        btnOk.setTypeface(normal);
        btnOk.setText(ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onYes();
            }
        });

        Button btnCancel = d.findViewById(R.id.btnCancel);
        btnCancel.setTypeface(normal);
        btnCancel.setText(cancel);


        AlertDialog dialog = build.setCancelable(false).create();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCancel();
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
}

