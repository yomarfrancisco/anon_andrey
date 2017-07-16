package com.anontemp.android;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.anontemp.android.com.anontemp.android.model.Tweet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MessageBoard extends FullscreenController {

    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;

    private RecyclerView rv;
    private TweetsAdapter adapter;
    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (rv == null)
                return;

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            if (rv == null)
                return;

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected int init() {
        return R.layout.activity_message_board;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();

        rv = findViewById(R.id.list);
        final List<Tweet> tweetList = new ArrayList<>();
        final Query tweetsQuery = dbRef.child("Tweets");
        tweetsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot tweetShot : dataSnapshot.getChildren()) {
                    Tweet t = tweetShot.getValue(Tweet.class);
                    t.set_id(new Random().nextLong());
                    t.setRealDate(Helper.getRealDate(t.getDate()));
                    t.setVisibleDate(Helper.getVisibleDate(t.getRealDate()));
                    tweetList.add(t);
                }
                tweetsQuery.removeEventListener(this);
                rv.setLayoutManager(new LinearLayoutManager(MessageBoard.this));

                Collections.sort(tweetList);
                adapter = new TweetsAdapter(tweetList, MessageBoard.this);
                rv.setAdapter(adapter);
                rv.addItemDecoration(new MessageDivider(MessageBoard.this));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Helper.showSnackbar(databaseError.getMessage(), MessageBoard.this);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
