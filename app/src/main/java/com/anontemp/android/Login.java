package com.anontemp.android;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anontemp.android.misc.AnonDialog;
import com.anontemp.android.misc.DialogListener;
import com.anontemp.android.misc.FontCache;
import com.anontemp.android.misc.Helper;
import com.anontemp.android.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.anontemp.android.misc.Helper.ARG_PAGE_TYPE;
import static com.anontemp.android.misc.Helper.PAGE_TYPE_PRIVACY;

public class Login extends FullscreenController implements View.OnClickListener, DialogListener {

    public static final String QUESTION = "?";
    private FirebaseAuth mAuth;
    private TextInputEditText mMail, mPass;
    private AppCompatButton mTempLoginButton, mAuthButton;
    private FirebaseDatabase database;
    private Typeface mFontSnowboard;

    @Override
    protected int init() {
        mAuth = FirebaseAuth.getInstance();
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mFontSnowboard = FontCache.getTypeface("CFSnowboardProjectPERSONAL.ttf", this);

        database = FirebaseDatabase.getInstance();

        findViewById(R.id.returnToMap).setVisibility(View.VISIBLE);
        findViewById(R.id.returnToMap).setOnClickListener(this);
        mMail = findViewById(R.id.emailInput);
        mPass = findViewById(R.id.passInput);
        mTempLoginButton = findViewById(R.id.temp_login_button);
        mTempLoginButton.setTypeface(mFontSnowboard);
        mTempLoginButton.setOnClickListener(this);
        mAuthButton = findViewById(R.id.authenticate_button);
        mAuthButton.setTypeface(mFontSnowboard);
        mAuthButton.setOnClickListener(this);
        findViewById(R.id.ivLogo).setOnClickListener(this);

        TextView links = findViewById(R.id.links);
        links.setMovementMethod(LinkMovementMethod.getInstance());
        Spannable spans = (Spannable) links.getText();
        ClickableSpan termsSpan = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Login.this, TermsAndPrivacy.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        };

        ClickableSpan policySpan = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Login.this, TermsAndPrivacy.class);
                intent.putExtra(ARG_PAGE_TYPE, PAGE_TYPE_PRIVACY);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        };
        spans.setSpan(termsSpan, 288, 293, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spans.setSpan(policySpan, 298, 312, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    }


    private void anonymousAuthentication() {
        mAuthButton.setEnabled(false);
        mTempLoginButton.setEnabled(false);
        mAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                user = authResult.getUser();

                Log.i(LOG_TAG, "anon user has signed in successfully");

                database.getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String userId = "anon0";

                        if (dataSnapshot.exists()) {
                            userId = "anon" + dataSnapshot.getChildrenCount();
                        }
                        currentUser = new User(QUESTION, QUESTION, user.getUid(), QUESTION, userId);
                        database.getReference("users").child(user.getUid()).setValue(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                completeLogin();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mAuthButton.setEnabled(true);
                        mTempLoginButton.setEnabled(true);
                        showAlert(databaseError.getMessage());

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mAuthButton.setEnabled(true);
                mTempLoginButton.setEnabled(true);
                showAlert(e.getLocalizedMessage());
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.returnToMap:

                commonDialog = AnonDialog.newInstance(R.string.sure, R.string.logout_desc, R.string.logout_ok, android.R.string.cancel);
                commonDialog.show(getSupportFragmentManager(), null);


                break;
            case R.id.temp_login_button:
                showProgressSnowboard(R.string.loading, R.drawable.balaclava);
                anonymousAuthentication();
                break;

            case R.id.authenticate_button:
                showProgressSnowboard(R.string.loading, R.drawable.balaclava);
                if (!validate()) {
                    Helper.showSnackbar(getString(R.string.validate_err), this);
                    hideProgressDialog();
                    break;
                }
                signIn(mMail.getText().toString(), mPass.getText().toString());

                break;
            case R.id.ivLogo:
                View d = LayoutInflater.from(Login.this).inflate(R.layout.c_alert, null);
                AlertDialog.Builder build = new AlertDialog.Builder(Login.this);
                build.setView(d);
                final AlertDialog dialog = build.create();

                LinearLayout iv = d.findViewById(R.id.dialLayout);
                TextView tv = iv.findViewById(R.id.text);
                tv.setText(R.string.just_log_in);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.isShown()) {
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
                break;
        }
    }


    private boolean validate() {

        if (!validateForm())
            return false;

        String text = mMail.getText().toString();

        return text.matches(Constants.EMAIL_PATTERN);

    }

    private boolean validateForm() {
        boolean valid = true;

        if (TextUtils.isEmpty(mMail.getText().toString())) {
            mMail.setError("Required.");
            valid = false;
        } else {
            mMail.setError(null);
        }

        if (TextUtils.isEmpty(mPass.getText().toString())) {
            mPass.setError("Required.");
            valid = false;
        } else {
            mPass.setError(null);
        }

        return valid;
    }

    private void signIn(final String email, final String password) {
        mAuthButton.setEnabled(false);
        mAuthButton.setText(R.string.load_auth);
        mTempLoginButton.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(Helper.TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a tweet to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(Helper.TAG, "signInWithEmail:failed", task.getException());
                            showAlert(task.getException().getLocalizedMessage());
                            mAuthButton.setEnabled(true);
                            mTempLoginButton.setEnabled(true);
                            return;
                        }

                        user = task.getResult().getUser();

                        Log.i(LOG_TAG, user.getEmail() + " has signed in successfuly");
                        database.getReference("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                currentUser = dataSnapshot.getValue(User.class);
                                if (currentUser == null) {

                                } else {
                                    completeLogin();
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        // ...
                    }
                });

    }

    private void completeLogin() {
        hideProgressDialog();
        Intent intent = new Intent(Login.this, DashBoard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (getIntent() != null && getIntent().getStringExtra(MapsActivity.REGION_NAME) != null) {
            intent.putExtra(MapsActivity.REGION_NAME, getIntent().getStringExtra(MapsActivity.REGION_NAME));
        }
        startActivity(intent);
    }


    @Override
    public void onYes() {
        commonDialog.dismiss();
        Intent intent = new Intent(Login.this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    public void onCancel() {
        commonDialog.dismiss();
    }
}
