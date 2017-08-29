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

import com.anontemp.android.MessageBoard;
import com.anontemp.android.R;
import com.anontemp.android.model.Tweet;
import com.anontemp.android.view.AnonTButton;
import com.anontemp.android.view.AnonTVSpecial;

/**
 * Created by jaydee on 08.08.17.
 */

public class ActionsDialog extends DialogFragment {

    View d;
    private ActionsListener listener;


    public static ActionsDialog newInstance(Tweet tweet) {
        ActionsDialog f = new ActionsDialog();
        f.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        Bundle args = new Bundle();
        args.putSerializable(MessageBoard.TWEET_EXTRA, tweet);
        f.setArguments(args);
        return f;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (listener == null) {
            try {
                this.listener = ((ActionsListener) getActivity());
            } catch (ClassCastException e) {
                throw new ClassCastException("Activity must implement ActionsListener.");
            }
        }

        final Tweet tweet = (Tweet) getArguments().getSerializable(MessageBoard.TWEET_EXTRA);

        d = LayoutInflater.from(getActivity()).inflate(R.layout.actions_dial, null);
        final AlertDialog.Builder build = new AlertDialog.Builder(getContext(), R.style.MyTheme);
        build.setView(d);
        AnonTVSpecial tvTitle = d.findViewById(R.id.title);
        tvTitle.setText(getString(R.string.region_name, tweet.getRegionName()));
        TextView tv = d.findViewById(R.id.text);
        Typeface thin = FontCache.getTypeface("HelveticaNeue-Thin.otf", getContext());
        tv.setTypeface(thin);
        tv.setText(getString(R.string.post_in_quotes, tweet.getTweetText()));

        AnonTButton btnLike = d.findViewById(R.id.like_button);
        AnonTButton btnComment = d.findViewById(R.id.comment_button);


        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onLikeClick(tweet);
            }
        });

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCommentClick(tweet);
            }
        });

        AlertDialog dialog = build.setCancelable(true).create();

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

