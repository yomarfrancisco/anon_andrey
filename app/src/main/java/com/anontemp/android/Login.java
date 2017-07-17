package com.anontemp.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anontemp.android.com.anontemp.android.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.anontemp.android.Helper.ARG_PAGE_TYPE;
import static com.anontemp.android.Helper.PAGE_TYPE_PRIVACY;

public class Login extends FullscreenController implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextInputEditText mMail;
    private TextInputEditText mPass;
    private AppCompatButton bAuth;
    private FirebaseDatabase database;

    @Override
    protected int init() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(Helper.TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(Helper.TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        database = FirebaseDatabase.getInstance();

        findViewById(R.id.returnToMap).setVisibility(View.VISIBLE);
        findViewById(R.id.returnToMap).setOnClickListener(this);
        mMail = findViewById(R.id.emailInput);
        mPass = findViewById(R.id.passInput);
        bAuth = findViewById(R.id.authenticate);
        bAuth.setOnClickListener(this);

        TextView links = findViewById(R.id.links);
        links.setMovementMethod(LinkMovementMethod.getInstance());
        Spannable spans = (Spannable) links.getText();
        ClickableSpan termsSpan = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Login.this, TermsAndPrivacy.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                Helper.downToUpTransition(Login.this);
            }
        };

        ClickableSpan policySpan = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Login.this, TermsAndPrivacy.class);
                intent.putExtra(ARG_PAGE_TYPE, PAGE_TYPE_PRIVACY);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                Helper.downToUpTransition(Login.this);
            }
        };
        spans.setSpan(termsSpan, 288, 293, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spans.setSpan(policySpan, 298, 312, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.returnToMap:
                Intent intent = new Intent(Login.this, MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                Helper.downToUpTransition(Login.this);
                break;
            case R.id.authenticate:
                showProgressDialog(R.string.loading);
                if (!validate()) {
                    Helper.showSnackbar(getString(R.string.validate_err), this);
                    hideProgressDialog();
                    break;
                }
                signIn(mMail.getText().toString(), mPass.getText().toString());

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
        bAuth.setEnabled(false);

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
                            Toast.makeText(Login.this, R.string.validate_err,
                                    Toast.LENGTH_SHORT).show();
                            bAuth.setEnabled(true);
                            return;
                        }

                        final FirebaseUser user = task.getResult().getUser();
                        database.getReference("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User currentUser = dataSnapshot.getValue(User.class);
                                if (currentUser == null) {
                                    currentUser = new User("?", email, user.getUid(), "Wits");
                                    database.getReference("users").child(user.getUid()).setValue(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            completeLogin();
                                        }
                                    });

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
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        if (getIntent() != null && getIntent().getStringExtra(MapsActivity.REGION_NAME) != null) {
            intent.putExtra(MapsActivity.REGION_NAME, getIntent().getStringExtra(MapsActivity.REGION_NAME));
        }
        startActivity(intent);
    }


}
