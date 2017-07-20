package com.anontemp.android.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
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
    private DatabaseReference ref;
    private String key;

    public User(String username, String uid) {
        this.username = username;
        this.uid = uid;
    }

    public User(String username, String email, String uid, String firstName) {
        this.username = username;
        this.email = email;
        this.uid = uid;
        this.firstName = firstName;
    }

    public User(DataSnapshot snapshot) {
        this.key = snapshot.getKey();
        this.ref = snapshot.getRef();
        this.firstName = snapshot.child("firstName").getValue(String.class);
        this.username = snapshot.child("username").getValue(String.class);
        this.email = snapshot.child("email").getValue(String.class);
        this.uid = snapshot.child("uid").getValue(String.class);

    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getUid() {
        return uid;
    }

    public DatabaseReference getRef() {
        return ref;
    }

    public void setRef(DatabaseReference ref) {
        this.ref = ref;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


}
