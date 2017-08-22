package com.anontemp.android.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by jaydee on 13.07.17.
 */

@IgnoreExtraProperties
public class User {

    private String username;
    private String email;
    private String firstName;
    private String uid;
    private String userId;

    public User(String username, String uid) {
        this.username = username;
        this.uid = uid;
    }

    public User(String username, String email, String uid, String firstName, String userId) {
        this.username = username;
        this.email = email;
        this.uid = uid;
        this.firstName = firstName;
        this.userId = userId;
    }

    public User(DataSnapshot snapshot) {
        this.firstName = snapshot.child("firstName").getValue(String.class);
        this.username = snapshot.child("username").getValue(String.class);
        this.email = snapshot.child("email").getValue(String.class);
        this.uid = snapshot.child("uid").getValue(String.class);

    }

    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }



}
