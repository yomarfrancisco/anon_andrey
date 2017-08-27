package com.anontemp.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anontemp.android.misc.AnonDialog;
import com.anontemp.android.misc.FontCache;
import com.anontemp.android.misc.Helper;
import com.anontemp.android.model.User;
import com.anontemp.android.view.AnonTVSpecial;
import com.anontemp.android.view.AnonTView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.nekocode.emojix.Emojix;

/**
 * Created by jaydee on 04.07.17.
 */

public abstract class FullscreenMapController extends FragmentActivity {

    public static final String LOG_TAG = "ANON";
    protected static User currentUser;
    protected static FirebaseUser user;
    @VisibleForTesting
    public ProgressDialog mProgressDialog;
    protected AnonDialog commonDialog;
    private View decorView;

    public void onAuthDone() {

    }

    private void getUser() {
        if (currentUser == null) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                if (!Helper.getUuid().isEmpty() && (user == null || user != null && !user.getUid().equals(Helper.getUuid()))) {
                    Pair<String, String> pair = Helper.getCredentials();
                    if (pair != null) {

                        FirebaseAuth.getInstance().signInWithEmailAndPassword(pair.first, pair.second)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d(Helper.TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                        if (!task.isSuccessful()) {
                                            Log.w(Helper.TAG, "signInWithEmail:failed", task.getException());


                                        }

                                    }
                                });
                    }


                }
            }

            if (user == null)
                return;
            DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
            users.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentUser = dataSnapshot.getValue(User.class);
                    if (currentUser == null) {
                        AuthCredential credential = Helper.getAuthCredential();

                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d(Helper.TAG, "User re-authenticated.");
                                        deleteUser();
                                    }
                                });

                        return;
                    }
                    onAuthDone();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onAuthDone();
                }
            }, 3000);
        }
    }

    private void deleteUser() {
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    AnonApp.get().getSharedPreferences().edit().remove(Helper.PASSWORD).remove(Helper.EMAIL).remove(Helper.UUID).apply();
                    user = null;


                    Snackbar snackbar = Helper.getSnackBar(getString(R.string.user_deleted), FullscreenMapController.this);
                    snackbar.addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            Intent intent = new Intent(FullscreenMapController.this, Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Helper.downToUpTransition(FullscreenMapController.this);
                            super.onDismissed(transientBottomBar, event);

                        }
                    }).show();
                }
            }
        });
    }

    protected abstract int init();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Emojix.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(init());
        getUser();
        decorView = getWindow().getDecorView();
        hideSystemUI();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    hideSystemUI();
                                }
                            }, 5000);
                        }
                    }
                });

    }

    private void hideSystemUI() {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void showProgressSnowboard(int messageRes, int gif) {
        showProgressSnowboard(getString(messageRes), gif);
    }

    public void showProgressSnowboard(int messageRes) {
        showProgressSnowboard(getString(messageRes), R.drawable.dope);
    }

    public void showProgressSnowboard(CharSequence sequence) {
        showProgressSnowboard(sequence.toString());
    }


    public void showProgressSnowboard(String message) {
        showProgressSnowboard(message, R.drawable.dope);
    }

    public void showProgressSnowboard(String message, int gif) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, R.style.MyTheme);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);


        }

        mProgressDialog.show();
        mProgressDialog.setContentView(R.layout.progress_bar);

        final ImageView img_loading_frame = mProgressDialog.findViewById(R.id.iv_frame_loading);

        img_loading_frame.setBackground(ContextCompat.getDrawable(this, gif));
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });

        TextView tv_progress_message = mProgressDialog.findViewById(R.id.tv_progress_message);
        tv_progress_message.setTypeface(FontCache.getTypeface("CFSnowboardProjectPERSONAL.ttf", this));
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        Helper.downToUpTransition(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    public AlertDialog.Builder getAlertBuilder(Object message, Object title, Object button) {
        View d = LayoutInflater.from(this).inflate(R.layout.c_alert, null);
        final AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setView(d);
        if (title != null) {
            AnonTVSpecial tvTitle = d.findViewById(R.id.title);
            if (title instanceof Integer)
                tvTitle.setText((Integer) title);
            if (title instanceof String)
                tvTitle.setText((String) title);
        } else {
            d.findViewById(R.id.title).setVisibility(View.GONE);
        }
        AnonTView tv = d.findViewById(R.id.text);
        Typeface thin = FontCache.getTypeface("HelveticaNeue-Thin.otf", this);
        tv.setTypeface(thin);
        if (message instanceof Integer)
            tv.setText((Integer) message);
        if (message instanceof String)
            tv.setText((String) message);
        AnonTView ivOk = d.findViewById(R.id.ivOk);
        ivOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAlertClick();
            }
        });
        if (button != null) {

            if (button instanceof Integer)
                ivOk.setText((Integer) button);
            if (button instanceof String)
                ivOk.setText((String) button);
        }

        return build;

    }

    public void onAlertClick() {

    }


    public AlertDialog showAlert(Object message, Object title) {
        AlertDialog dialog = showAlert(message, title, null);
        dialog.show();
        return dialog;
    }


    public AlertDialog showAlert(Object message) {
        AlertDialog dialog = showAlert(message, null);
        dialog.show();
        return dialog;
    }

    public AlertDialog showAlert(Object message, Object title, Object button) {
        AlertDialog dialog = getAlertBuilder(message, title, button).setCancelable(true).create();
        dialog.show();
        return dialog;
    }


}
