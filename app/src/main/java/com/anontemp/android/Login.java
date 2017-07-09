package com.anontemp.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends FullscreenController implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextInputEditText mMail;
    private TextInputEditText mPass;
    private AppCompatButton bAuth;


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

        findViewById(R.id.returnToMap).setVisibility(View.VISIBLE);
        findViewById(R.id.returnToMap).setOnClickListener(this);
        mMail = (TextInputEditText) findViewById(R.id.emailInput);
        mPass = (TextInputEditText) findViewById(R.id.passInput);
        bAuth = (AppCompatButton) findViewById(R.id.authenticate);
        bAuth.setOnClickListener(this);

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
                onBackPressed();
                Helper.upToDownTransition(this);
                break;
            case R.id.authenticate:
                showProgressDialog();
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

    private void signIn(String email, String password) {
        bAuth.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(Helper.TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(Helper.TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(Login.this, R.string.validate_err,
                                    Toast.LENGTH_SHORT).show();
                            bAuth.setEnabled(true);
                        }


                        hideProgressDialog();
                        Intent intent = new Intent(Login.this, DashBoard.class);
                        startActivity(intent);

                        // ...
                    }
                });
    }


}
