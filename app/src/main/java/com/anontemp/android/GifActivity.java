package com.anontemp.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.anontemp.android.misc.Helper;
import com.anontemp.android.view.AnonTView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class GifActivity extends FullscreenController implements GifsAdapter.GifAdapterInterface, View.OnClickListener {

    public static final String GIFS = "Gifs";
    public static final String GIF_URI = "gifUri";
    public static final String GIFS1 = "gifs/";
    private FirebaseDatabase database;
    private GifsAdapter gifsAdapter;
    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {


        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
    private StorageReference storageReference;

    private RecyclerView rv;
    private List<StorageReference> gifsList;
    private AnonTView cancelView;

    @Override
    protected int init() {
        return R.layout.activity_gif;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showWelcomeScreen();
        database = FirebaseDatabase.getInstance();
        rv = findViewById(R.id.list);
        gifsList = new ArrayList<>();
        cancelView = findViewById(R.id.cancelView);
        cancelView.setOnClickListener(this);
        storageReference = FirebaseStorage.getInstance().getReference();

        database.getReference(GIFS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot tweetShot : dataSnapshot.getChildren()) {
                    String gifUrl = tweetShot.getValue(String.class);
                    gifsList.add(storageReference.child(GIFS1 + gifUrl));
                }
                rv.setLayoutManager(new LinearLayoutManager(GifActivity.this));

                gifsAdapter = new GifsAdapter(gifsList, Glide.with(GifActivity.this), GifActivity.this);
                rv.setHasFixedSize(true);
                rv.setAdapter(gifsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Helper.showSnackbar(databaseError.getMessage(), GifActivity.this);
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        database.getReference(GIFS).addChildEventListener(childEventListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        database.getReference(GIFS).removeEventListener(childEventListener);
    }


    private void showWelcomeScreen() {
        if (Helper.isAlreadyWelcome())
            return;
        showAlert(R.string.gif_welcome_body, R.string.gif_welcome_title);
        Helper.setWelcome();

    }

    @Override
    public void onGifClick(StorageReference gifUri) {


        Intent intent = new Intent();
        intent.putExtra(GIF_URI, gifUri.getPath().split(GIFS1)[1]);
        setResult(Activity.RESULT_OK, intent);
        finish();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancelView:
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
        }
    }
}
