package com.anontemp.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.anontemp.android.misc.Helper;
import com.anontemp.android.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccount extends FullscreenController implements View.OnClickListener {

    public static final String MALE = "male";
    public static final String FEMALE = "female";
    public static final String BLOSSOM = "blossom";
    public static final int INT_UNDEFINED = 0;
    public static final int INT_MALE = 1;
    public static final int INT_FEMALE = 2;
    public static final int INT_TRANSGENDER = 3;
    ImageView chosenGender;
    TextInputEditText mMail;
    TextInputEditText mPass;
    Button signUp;
    private FirebaseDatabase database;
    private DatabaseReference dbRef;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected int init() {
        return R.layout.activity_create_account;
    }

    @Override
    protected void onStart() {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();

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
        dbRef = database.getReference("users");

    }

    public void genderClick(View v) {

        if (v.getTag() == null)
            return;

        String sTag = (String) v.getTag();
        switch (sTag) {
            case MALE:
                chosenGender.setImageDrawable(ContextCompat.getDrawable(CreateAccount.this, R.mipmap.ic_aubergine));
                chosenGender.setTag(INT_MALE);
                break;
            case FEMALE:
                chosenGender.setImageDrawable(ContextCompat.getDrawable(CreateAccount.this, R.mipmap.ic_peach));
                chosenGender.setTag(INT_FEMALE);
                break;
            case BLOSSOM:
                chosenGender.setImageDrawable(ContextCompat.getDrawable(CreateAccount.this, R.mipmap.ic_blossom));
                chosenGender.setTag(INT_TRANSGENDER);
                break;

        }

    }

    private void initViews() {
        chosenGender = findViewById(R.id.chosenGender);
        mMail = findViewById(R.id.emailInput);
        mPass = findViewById(R.id.passInput);
        signUp = findViewById(R.id.authenticate);
        signUp.setOnClickListener(this);
        chosenGender.setTag(INT_UNDEFINED);
        findViewById(R.id.tempLoginLink).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tempLoginLink:
                Intent in
                        = new Intent(CreateAccount.this, Login.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(in);
                Helper.downToUpTransition(CreateAccount.this);
                break;
            case R.id.authenticate:
                if (!validate()) {
                    break;
                }
                showProgressSnowboard(R.string.acc_created);
                signUp(mMail.getText().toString(), mPass.getText().toString());
                break;
        }
    }


    private void signUp(final String email, final String password) {
        signUp.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(Helper.TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            hideProgressDialog();
                            Helper.showSnackbar(getString(R.string.signup_wrong_mail), CreateAccount.this);
                            signUp.setEnabled(true);
                            return;
                        }

                        String username = "";
                        int tag = (int) chosenGender.getTag();
                        switch (tag) {
                            case INT_MALE:
                                username = "\uD83C\uDF46";
                                break;
                            case INT_FEMALE:
                                username = "\uD83C\uDF51";
                                break;
                            case INT_TRANSGENDER:
                                username = "\uD83C\uDF3C";
                                break;
                        }

                        user = task.getResult().getUser();


                        //TODO region identifier
                        currentUser = new User(username, email, user.getUid(), "Wits");
                        DatabaseReference userRef = dbRef.child(user.getUid());
                        userRef.setValue(currentUser);


                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(CreateAccount.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d(Helper.TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                        if (!task.isSuccessful()) {
                                            Log.w(Helper.TAG, "signInWithEmail:failed", task.getException());
                                            hideProgressDialog();
                                            Helper.showSnackbar(getString(R.string.validate_err), CreateAccount.this);
                                            signUp.setEnabled(true);
                                            return;
                                        }

                                        Pair<String, String> pair = new Pair<>(email, password);
                                        Helper.setUuid(user.getUid());
                                        Helper.setCredentials(pair);


                                        hideProgressDialog();
                                        Intent intent = new Intent(CreateAccount.this, DashBoard.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        startActivity(intent);

                                    }
                                });


                    }
                });
    }


    private boolean validate() {

        if (TextUtils.isEmpty(mMail.getText().toString())) {
            Helper.showSnackbar(getString(R.string.signup_mail), CreateAccount.this);
            return false;
        }

        if (TextUtils.isEmpty(mPass.getText().toString()) || mPass.getText().length() < 8) {
            Helper.showSnackbar(getString(R.string.signup_pass_long), CreateAccount.this);
            return false;
        }

        if ((int) chosenGender.getTag() == INT_UNDEFINED) {
            Helper.showSnackbar(getString(R.string.signup_gender), CreateAccount.this);
            return false;
        }

        String text = mMail.getText().toString();

        if (!text.matches(Constants.EMAIL_PATTERN)) {
            Helper.showSnackbar(getString(R.string.signup_mail), CreateAccount.this);
            return false;
        }

        return true;

    }

}
