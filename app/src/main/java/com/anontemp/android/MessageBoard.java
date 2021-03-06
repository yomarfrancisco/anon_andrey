package com.anontemp.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.anontemp.android.misc.AnonAlert;
import com.anontemp.android.misc.AnonDialog;
import com.anontemp.android.misc.ChildEventAdapter;
import com.anontemp.android.misc.DialogListener;
import com.anontemp.android.misc.HeaderItem;
import com.anontemp.android.misc.Helper;
import com.anontemp.android.misc.MenuListAdapter;
import com.anontemp.android.misc.MessageDivider;
import com.anontemp.android.misc.TweetsAdapter;
import com.anontemp.android.model.Region;
import com.anontemp.android.model.Tweet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.anontemp.android.misc.Helper.formatTimeToLive;
import static com.anontemp.android.misc.Helper.getTimeToLive;
import static com.anontemp.android.misc.Helper.isShowableTweet;

public class MessageBoard extends FullscreenController implements View.OnClickListener, TweetsAdapter.TweetInterface {

    public static final int FIRST = 1;
    public static final String TWEET_EXTRA = "tweet_extra";
    List<BaseTweetItem> mTweetItems;
    DrawerLayout drawer;
    ListView mMenuList;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private ImageButton mMenuButton;
    private RecyclerView mRecycler;
    private TweetsAdapter mAdapter;
    private List<Region> mRegions = new ArrayList<>();
    private ChildEventListener mRegionsListener = new ChildEventAdapter() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            Region region = dataSnapshot.getValue(Region.class);
            mRegions.add(0, region);
        }

    };
    private AlertDialog mAlert;
    private ChildEventListener mTweetsListener = new ChildEventAdapter() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (mRecycler == null || mAdapter == null)
                return;

            Tweet tweet = dataSnapshot.getValue(Tweet.class);
            tweet.setReference(dataSnapshot.getRef());
            tweet.setRealDate(Helper.getRealDate(tweet.getDate()));
            if (!isShowableTweet(tweet)) {
                Log.d(LOG_TAG, tweet.getDate());
                return;
            }
            tweet.setTimeToLive(formatTimeToLive(getTimeToLive(tweet.getRealDate(), tweet.getCountdown()), MessageBoard.this));
            tweet.set_id(new Random().nextLong());
            mAdapter.addTweet(tweet);
            mRecycler.scrollToPosition(0);


        }


        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            if (mRecycler == null || mAdapter == null)
                return;

            Tweet mUpdatedTweet = dataSnapshot.getValue(Tweet.class);
            for (BaseTweetItem item : mAdapter.getTweets()) {

                if (!(item instanceof Tweet))
                    continue;

                Tweet mOriginalTweet = (Tweet) item;

                if (mOriginalTweet.getTweetId().equals(mUpdatedTweet.getTweetId())) {
                    int index = mTweetItems.indexOf(mOriginalTweet);
                    mUpdatedTweet.setRealDate(mOriginalTweet.getRealDate());
                    if (!isShowableTweet(mUpdatedTweet)) {
                        mAdapter.removeTweet(mOriginalTweet);
                        return;
                    }


                    mUpdatedTweet.setTimeToLive(formatTimeToLive(getTimeToLive(mUpdatedTweet.getRealDate(), mUpdatedTweet.getCountdown()), MessageBoard.this));
                    mUpdatedTweet.set_id(mOriginalTweet.get_id());
                    mUpdatedTweet.setReference(mOriginalTweet.getReference());

                    String uid = currentUser.getUid();
                    boolean shouldToggleComment = false;
                    if (uid.equals(mUpdatedTweet.getUserId())) {
                        if (mUpdatedTweet.getLoves() != null && mOriginalTweet.getLoves() == null
                                || mUpdatedTweet.getLoves() != null && mOriginalTweet.getLoves() != null &&
                                mUpdatedTweet.getLoves().size() != mOriginalTweet.getLoves().size()) {
                            if (!isNotifyShown())
                                mAlert = showAlert(R.string.new_love);
                        }

                        if (mUpdatedTweet.getComments() != null && mOriginalTweet.getComments() == null
                                || mUpdatedTweet.getComments() != null && mOriginalTweet.getComments() != null &&
                                mUpdatedTweet.getComments().size() != mOriginalTweet.getComments().size()) {
                            if (!isNotifyShown())
                                mAlert = showAlert(R.string.new_comment);
                            shouldToggleComment = true;
                        }
                    }

                    mAdapter.setTweet(mUpdatedTweet, index, shouldToggleComment);
                    break;
                }
            }

        }

    };
    private int lastPosition = 0;
    private AnonAlert alert;

    private boolean isNotifyShown() {
        return mAlert != null && mAlert.isShowing();
    }



    @Override
    protected int init() {
        return R.layout.message_board;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    private void selectItem(int position) {

//        for(Region region : mRegions) {
//            boolean existAllowedComment = false;
//            for (int j = 0; j < mTweets.size(); j++) {
//                Tweet t = mTweets.get(j);
//                if (region.getRegionId().equals(t.getRegionId())) {
//                    if(existAllowedComment) {
//                        t.setAllowComment(false);
//                        mTweets.remove(j);
//                        mTweets.add(j, t);
//                    } else if (isShowableTweet(t) && t.getAllowComment()) {
//                        existAllowedComment = true;
//
//                    }
//                }
//
//            }
//        }


        // Highlight the selected item, update the title, and close the drawer
        mMenuList.setItemChecked(position, true);
        if (position < Constants.LOCAL_REGIONS.size()) {
            mAdapter.filterList(String.valueOf(position));
            if (lastPosition != position && position != 0) {
                ConstraintLayout layout = (ConstraintLayout) getViewByPosition(position, mMenuList);
                TextView textView = layout.findViewById(R.id.text);
                textView.setTextColor(ContextCompat.getColor(this, R.color.pink));
                if (lastPosition != 0) {
                    layout = (ConstraintLayout) getViewByPosition(lastPosition, mMenuList);
                    textView = layout.findViewById(R.id.text);
                    textView.setTextColor(ContextCompat.getColor(this, R.color.white_alfa));
                }
            }
            lastPosition = position;
        } else {
            if (position == mMenuList.getCount() - 2) {
                goToBoard();
            } else {
                goToSnapshot();
            }
        }
        drawer.closeDrawer(GravityCompat.END);

    }


//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        switch (id) {
//            case R.id.nav_wits:
//                break;
//        }
//
//        drawer.closeDrawer(GravityCompat.END);
//        return true;
//    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawer = findViewById(R.id.drawer_layout);

        mMenuList = findViewById(R.id.menu_list);
        mMenuList.setAdapter(new MenuListAdapter(this));
        mMenuList.setOnItemClickListener(new DrawerItemClickListener());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mMenuButton = findViewById(R.id.menu_button);
        mMenuButton.setOnClickListener(this);
        mTweetItems = new ArrayList<>();
        mTweetItems.add(new HeaderItem(getString(R.string.wits_notice_board)));

        mRecycler = findViewById(R.id.list);

        mRecycler.setLayoutManager(new LinearLayoutManager(MessageBoard.this));
        mAdapter = new TweetsAdapter(mTweetItems, MessageBoard.this);
        mRecycler.setAdapter(mAdapter);
        mRecycler.addItemDecoration(new MessageDivider(MessageBoard.this));


//        Calendar now = Calendar.getInstance();
//        Calendar deadline = Calendar.getInstance();
//        deadline.set(Calendar.HOUR_OF_DAY, 23);
//        deadline.set(Calendar.MINUTE, 59);
//        deadline.set(Calendar.SECOND, 59);
//        long diff = deadline.getTimeInMillis() - now.getTimeInMillis();
//        new CountDownTimer(diff, 60000) {
//            @Override
//            public void onTick(long l) {
//                trashTime.setText(new SimpleDateFormat("H'hrs' mm'min'").format(new Date(l)));
//                alertTrashTime = new SimpleDateFormat("H' hours and 'm' minutes'").format(new Date(l));
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        }.start();


        findViewById(R.id.board_link).setOnClickListener(this);
        findViewById(R.id.live_link).setOnClickListener(this);
        showWelcomeScreen();


    }

    private void showWelcomeScreen() {
        if (Helper.isBoardWelcome())
            return;
        mAlert = showAlert(R.string.board_welcome_body, R.string.board_welcome_title);
        Helper.setBoardWelcome();

    }

    @Override
    public void onAlertClick() {
        if (mAlert != null && mAlert.isShowing())
            mAlert.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDatabase.getReference("Tweets").addChildEventListener(mTweetsListener);
        mDatabase.getReference("Regions").addChildEventListener(mRegionsListener);

    }

    private void goToBoard() {
        Intent intent = new Intent(MessageBoard.this, DashBoard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void goToSnapshot() {
        goToSnapshot(null);
    }

    private void goToSnapshot(Tweet tweet) {
        Intent intent = new Intent(MessageBoard.this, Snapshot.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (tweet != null)
            intent.putExtra(TWEET_EXTRA, tweet);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.board_link:
                goToBoard();
                break;
            case R.id.live_link:
                goToSnapshot();
                break;
            case R.id.menu_button:
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                } else {
                    drawer.openDrawer(GravityCompat.END);
                }

                break;
//            case R.id.report:
//                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
//                emailIntent.setType("plain/text");
//                emailIntent.putExtra(Intent.EXTRA_EMAIL,
//                        new String[]{getString(R.string.anon_mail)});
//                emailIntent.putExtra(Intent.EXTRA_SUBJECT,
//                        getString(R.string.mail_subject));
//                emailIntent.putExtra(Intent.EXTRA_TEXT,
//                        getString(R.string.mail_body));
//                try {
//                    startActivity(emailIntent);
//                } catch (ActivityNotFoundException e) {
//                    showAlert(R.string.mail_error_body);
//                }

//                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mDatabase.getReference("Tweets").removeEventListener(mTweetsListener);
        mDatabase.getReference("Regions").removeEventListener(mRegionsListener);
    }

    @Override
    public void onTweetLong(final Tweet tweet) {
        if (alreadyPostedWithTweet(tweet)) {
            alert = AnonAlert.newInstance(0, R.string.already_posted, R.string.ok_caps);
            alert.setOnButtonListener(new AnonAlert.AlertListener() {
                @Override
                public void onButton() {
                    alert.dismiss();
                }
            });
            alert.show(getSupportFragmentManager(), null);
        } else {
            commonDialog = AnonDialog.newInstance(0, R.string.post_question, R.string.yes, R.string.no);
            commonDialog.addListener(new DialogListener() {
                @Override
                public void onYes() {
                    if (currentUser == null)
                        return;

                    String uid = currentUser.getUid();
                    String username = currentUser.getFirstName();
                    if (tweet.getReporters() == null) {
                        Map<String, String> map = new HashMap<>();
                        map.put(uid, username);
                        tweet.getReference().child("reporters").setValue(map);
                    } else {
                        tweet.getReporters().put(uid, username);
                        tweet.getReference().child("reporters").setValue(tweet.getReporters());
                    }
                    if (commonDialog.getDialog().isShowing())
                        commonDialog.dismiss();

                }

                @Override
                public void onCancel() {
                    if (commonDialog.getDialog().isShowing())
                        commonDialog.dismiss();
                }
            });
            commonDialog.show(getSupportFragmentManager(), null);
        }
    }

    @Override
    public void onCommentClick(Tweet tweet) {
        goToSnapshot(tweet);
    }

    private boolean alreadyPostedWithTweet(Tweet tweet) {
        if (tweet.getReporters() == null)
            return false;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return tweet.getReporters().keySet().contains(uid);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

}
