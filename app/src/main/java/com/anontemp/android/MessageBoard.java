package com.anontemp.android;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anontemp.android.model.Tweet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MessageBoard extends FullscreenController implements View.OnClickListener {

    final List<Tweet> tweetList = new ArrayList<>();
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private ImageView ivTweet;
    private RecyclerView rv;
    private TweetsAdapter adapter;
    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (rv == null || adapter == null)
                return;

            Tweet tweet = dataSnapshot.getValue(Tweet.class);
            tweet.setRealDate(Helper.getRealDate(tweet.getDate()));
            tweet.setVisibleDate(Helper.getVisibleDate(tweet.getRealDate()));
            tweet.set_id(new Random().nextLong());
            tweetList.add(0, tweet);
            adapter.notifyItemInserted(0);
            rv.scrollToPosition(0);


        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            if (rv == null || adapter == null)
                return;
            Tweet tweet = dataSnapshot.getValue(Tweet.class);
            for (Tweet t : tweetList) {
                if (t.getTweetId().equals(tweet.getTweetId())) {
                    tweet.setVisibleDate(t.getVisibleDate());
                    tweet.setRealDate(t.getRealDate());
                    int index = tweetList.indexOf(t);
                    tweetList.set(index, tweet);
                    adapter.notifyItemChanged(index);
                    break;
                }
            }

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
    private TextView trashTime;
    private String alertTrashTime;

    @Override
    protected int init() {
        return R.layout.activity_message_board;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ivTweet = findViewById(R.id.ivTweet);
        ivTweet.setOnClickListener(this);

        rv = findViewById(R.id.list);

        database.getReference("Tweets").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot tweetShot : dataSnapshot.getChildren()) {
                    Tweet t = tweetShot.getValue(Tweet.class);
                    t.set_id(new Random().nextLong());
                    t.setRealDate(Helper.getRealDate(t.getDate()));
                    t.setVisibleDate(Helper.getVisibleDate(t.getRealDate()));
                    tweetList.add(t);
                }
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

        trashTime = findViewById(R.id.trashTime);
        Calendar now = Calendar.getInstance();
        Calendar deadline = Calendar.getInstance();
        deadline.set(Calendar.HOUR_OF_DAY, 23);
        deadline.set(Calendar.MINUTE, 59);
        deadline.set(Calendar.SECOND, 59);
        long diff = deadline.getTimeInMillis() - now.getTimeInMillis();
        new CountDownTimer(diff, 60000) {
            @Override
            public void onTick(long l) {
                trashTime.setText(new SimpleDateFormat("H'hrs' mm'min'").format(new Date(l)));
                alertTrashTime = new SimpleDateFormat("H' hours and 'm' minutes'").format(new Date(l));
            }

            @Override
            public void onFinish() {

            }
        }.start();


        findViewById(R.id.report).setOnClickListener(this);
        findViewById(R.id.trashLayout).setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        database.getReference("Tweets").addChildEventListener(childEventListener);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivTweet:
                Intent intent = new Intent(MessageBoard.this, DashBoard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                Helper.downToUpTransition(MessageBoard.this);
                break;
            case R.id.report:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL,
                        new String[]{getString(R.string.anon_mail)});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.mail_subject));
                emailIntent.putExtra(Intent.EXTRA_TEXT,
                        getString(R.string.mail_body));
                try {
                    startActivity(emailIntent);
                } catch (ActivityNotFoundException e) {
                    showAlert(R.string.mail_error_body);
                }

                break;
            case R.id.trashLayout:
                showAlert(getString(R.string.trash_time_alert, alertTrashTime));
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
        database.getReference("Tweets").removeEventListener(childEventListener);
    }
}
