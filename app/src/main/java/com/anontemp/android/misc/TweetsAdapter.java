package com.anontemp.android.misc;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.anontemp.android.BaseTweetItem;
import com.anontemp.android.R;
import com.anontemp.android.model.Tweet;
import com.anontemp.android.view.AnonTVSpecial;
import com.anontemp.android.view.AnonTView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.anontemp.android.FullscreenController.LOG_TAG;

/**
 * Created by jaydee on 16.07.17.
 */

public class TweetsAdapter extends RecyclerView.Adapter {

    public static final String WITS_UNIVERSITY = "Wits University";
    public static final String M4A = ".m4a";
    private final List<BaseTweetItem> tweets;
    private final Context context;

    private Typeface thin;
    private Animation popOut, popIn, popBubble;
    private int lastPosition = -1;
    private SparseBooleanArray mPlays = new SparseBooleanArray();

    public TweetsAdapter(List<BaseTweetItem> tweets, Context context) {
        this.tweets = tweets;
        this.context = context;

        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case BaseTweetItem.Type.TYPE_TWEET:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.tweet, parent, false);
                return new TweetHolder(view);
            case BaseTweetItem.Type.TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.tweet_header, parent, false);
                return new HeaderHolder(view);
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return tweets.get(position).getType();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        switch (getItemViewType(position)) {
            case BaseTweetItem.Type.TYPE_TWEET:
                final TweetHolder th = (TweetHolder) holder;
                Tweet tweet = (Tweet) tweets.get(position);
                th.tweet = tweet;
                th.mFirstName.setText(tweet.getFirstName());
                th.mLocation.setText(tweet.getRegionName());
                th.mBody.setText(tweet.getTweetText());
                th.mMood.setText(tweet.getMoodText());
                th.mGender.setText(context.getString(R.string.gender_tweet, tweet.getUsername()));

                if (WITS_UNIVERSITY.equals(tweet.getRegionName())) {
                    th.mBlurRegion.setVisibility(View.VISIBLE);
                } else {
                    th.mBlurRegion.setVisibility(View.INVISIBLE);
                }


                if (TextUtils.isEmpty(tweet.getTweetGif())) {
                    th.mGifContainer.setVisibility(View.GONE);
                    Glide.clear(th.mGifImage);
                } else {
                    th.mGifContainer.setVisibility(View.VISIBLE);
                    Glide.with(context).using(new FirebaseImageLoader()).
                            load(FirebaseStorage.getInstance().getReference().child("gifs/" + tweet.getTweetGif())).
                            asGif().crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(th.mGifImage);
                }


                if (tweet.getTweetRecord() != null && tweet.getTweetRecord() > 0) {
                    th.mMood.setText(R.string.speaker);
                    th.loadAudio();

                } else {

                    if (position % 2 == 0) {
                        Drawable moodImage = getMoodBlurImageWithMoodText(tweet.getMoodText());
                        if (moodImage != null) {
                            th.mMoodImage.setVisibility(View.VISIBLE);
                            th.mMoodImage.setImageDrawable(moodImage);
                            th.mMood.setVisibility(View.INVISIBLE);
                        } else {
                            th.mMoodImage.setImageDrawable(null);
                            th.mMoodImage.setVisibility(View.INVISIBLE);
                            th.mMood.setVisibility(View.VISIBLE);
                        }
                    }

                }


                if (position % 5 == 0) {
                    th.mPinkSplash.setVisibility(View.VISIBLE);
                    th.mBlackSplash.setVisibility(View.VISIBLE);
                } else {
                    th.mBlackSplash.setVisibility(View.INVISIBLE);
                    th.mPinkSplash.setVisibility(View.INVISIBLE);
                }

                if (position % 3 == 2) {
                    th.mPurpleSplash.setVisibility(View.VISIBLE);

                } else {
                    th.mPurpleSplash.setVisibility(View.INVISIBLE);
                }


                setAnimation(tweet.getTweetRecord() != null && tweet.getTweetRecord() > 0 ? th.mMood : null, th.mComment, tweet.getMoodText(), position);

                break;

            case BaseTweetItem.Type.TYPE_HEADER:
                HeaderHolder hh = (HeaderHolder) holder;
                HeaderItem headerItem = (HeaderItem) tweets.get(position);
                hh.mHeaderItem = headerItem;
                hh.tvTile.setText(headerItem.getTitle());
                break;
        }


    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof TweetHolder) {
            ((TweetHolder) holder).clearAnimation();
        }

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    @Override
    public long getItemId(int position) {
        return tweets.get(position).get_id();
    }

    private Drawable getMoodBlurImageWithMoodText(String moodText) {
        if (TextUtils.isEmpty(moodText)) {
            return null;
        }
        Drawable image = null;
        int res = 0;

        switch (moodText) {
            case "😶":
                res = R.drawable.ic_blur_blank_trans;
                break;
            case "😚":
                res = R.drawable.ic_blur_blush_trans;
                break;
            case "💔":
                res = R.drawable.ic_blur_brkn_heart_trans;
                break;
            case "😎":
                res = R.drawable.ic_blur_cool_trans;
                break;
            case "😍":
                res = R.drawable.ic_blur_heart_eyes_trans;
                break;
            case "😡":
                res = R.drawable.ic_blur_mad_trans;
                break;
            case "🙏":
                res = R.drawable.ic_blur_prayer;
                break;
            case "😏":
                res = R.drawable.ic_blur_smirk_trans;
                break;
            case "☹️":
                res = R.drawable.ic_blur_sad_trans;
                break;
            case "😂":
                res = R.drawable.ic_lol_trans;
                break;
            case "😄":
                res = R.drawable.ic_lol_no_tears;
                break;
            case "😔":
                res = R.drawable.ic_pensive_trans;
                break;
            case "🔞":
                res = R.drawable.ic_blur_pg;
                break;
            case "😒":
                res = R.drawable.ic_blur_annoyed;
                break;
        }

        if (res != 0)
            image = ContextCompat.getDrawable(context, res);

        return image;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        popIn = AnimationUtils.loadAnimation(context, R.anim.pop_in);
        popOut = AnimationUtils.loadAnimation(context, R.anim.pop_out);
        popBubble = AnimationUtils.loadAnimation(context, R.anim.pop_bubble);
        thin = FontCache.getTypeface("HelveticaNeue-Thin.otf", context);
    }

    private void startAudioAnimation(final AnonTView moodView, final String moodText, final int position) {
        if (moodView != null) {
            moodView.startAnimation(popIn);

            moodView.setText(moodText);
            popOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    final Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (mPlays.get(position, false)) {
                                moodView.setText("🔇");
                                handler.postDelayed(this, 5000);
                                return;
                            }

                            moodView.startAnimation(popIn);

                            if (moodText.equals(moodView.getText().toString()))
                                moodView.setText(R.string.speaker);
                            else moodView.setText(moodText);

                            handler.postDelayed(this, 5000);
                        }
                    };
                    handler.post(runnable);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            moodView.startAnimation(popOut);
        }
    }

    private void setAnimation(final AnonTView moodView, final View commentView, final String moodText, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            popBubble.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    final Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            commentView.startAnimation(popBubble);
                            handler.postDelayed(this, 5000);
                        }
                    };
                    handler.post(runnable);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            commentView.startAnimation(popBubble);
            startAudioAnimation(moodView, moodText, position);
            lastPosition = position;
        }
    }

    public class TweetHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final AnonTVSpecial mFirstName;
        public final AnonTVSpecial mLocation;
        public final AnonTVSpecial mBody;
        public final LinearLayout mGifContainer;
        public final AnonTView mMood;
        public final AnonTView mGender;
        public final AnonTView mTimeToDelete;
        public final ImageView mBlurRegion;
        public final ImageView mGifImage;
        public final ImageView mMoodImage;
        public final ImageView mPin;
        public final ImageView mBlackSplash;
        public final ImageView mPinkSplash;
        public final ImageView mPurpleSplash;
        public final AnonTView mComment;
        public Tweet tweet;
        boolean mStartPlaying = true;
        private MediaPlayer mPlayer = null;
        View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (!view.isShown()) {
                    return false;
                }
                Log.d(Helper.TAG, view.getId() + "is clicked long");


                if (tweet.getTweetRecord() == null || tweet.getTweetRecord() == 0) {
                    return false;
                }

                if (mStartPlaying) {
                    stopPlaying();
                    mStartPlaying = !mStartPlaying;
                    mPlays.put(getAdapterPosition(), mStartPlaying);
                }
                if (mMood.isShown())
                    if (tweet.getMoodText().equals(mMood.getText().toString())) {
                        mMood.setText(R.string.speaker);
                    } else {
                        mMood.setText(tweet.getMoodText());
                    }


                return true;
            }
        };
        private boolean loadedAudioFile = false;
        private String filePath;
        View.OnClickListener mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!view.isShown()) {
                    return;
                }
                Log.d(Helper.TAG, view.getId() + "is clicked");

                if (tweet.getTweetRecord() == null || tweet.getTweetRecord() == 0) {
                    return;
                }

                if (mMood.getText().toString().equals(tweet.getMoodText())) {
                    return;
                }

                onPlay();


            }
        };
        public TweetHolder(View view) {
            super(view);
            mView = view;
            mFirstName = view.findViewById(R.id.firstName);
            mLocation = view.findViewById(R.id.location);
            mBody = view.findViewById(R.id.body);
            mGifContainer = view.findViewById(R.id.gifContainer);
            mMood = view.findViewById(R.id.mood);
            mGender = view.findViewById(R.id.gender);
            mTimeToDelete = view.findViewById(R.id.ttl);
            mBlurRegion = view.findViewById(R.id.blur_region_image);
            mGifImage = view.findViewById(R.id.gifView);
            mMoodImage = view.findViewById(R.id.blur_mood_image);
            mPin = view.findViewById(R.id.pin_image);
            mBlackSplash = view.findViewById(R.id.black_splash_image);
            mPinkSplash = view.findViewById(R.id.pink_splash_image);
            mPurpleSplash = view.findViewById(R.id.purple_splash_image);
            mComment = view.findViewById(R.id.comment_link);
            mMood.setOnClickListener(mClickListener);
            mMoodImage.setOnClickListener(mClickListener);
            mMood.setOnLongClickListener(mLongClickListener);
            mMoodImage.setOnLongClickListener(mLongClickListener);
            mComment.setTypeface(thin);

        }

        public void clearAnimation() {
            mMood.clearAnimation();
            mComment.clearAnimation();
        }

        public void loadAudio() {
            String recordName = tweet.getTweetId() + M4A;
            filePath = context.getExternalCacheDir().getAbsolutePath();
            filePath += "/" + recordName;
            File file = new File(filePath);
            if (!file.exists()) {
                FirebaseStorage.getInstance().getReference().child("records/" + recordName).
                        getFile(Uri.fromFile(file)).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        loadedAudioFile = true;
                    }
                });
            } else {
                loadedAudioFile = true;
            }
        }


        private void onPlay() {
            if (!loadedAudioFile)
                return;
            if (mStartPlaying) {
                startPlaying(filePath);
            } else {
                stopPlaying();
            }
            mStartPlaying = !mStartPlaying;
            mPlays.put(getAdapterPosition(), mStartPlaying);
        }

        private void startPlaying(String mFileName) {
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(mFileName);
                mPlayer.prepare();
                mMood.clearAnimation();
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        onPlay();
                        startAudioAnimation(mMood, tweet.getMoodText(), getAdapterPosition());
                    }
                });
                mPlayer.start();

                if (mMood.isShown())
                    mMood.setText("🔇");
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }
        }


        private void stopPlaying() {
            if (mPlayer != null) {
                mPlayer.release();
                mPlayer = null;
            }
            if (mMood.isShown())
                mMood.setText(R.string.speaker);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBody.getText() + "'";
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final AnonTVSpecial tvTile;
        public HeaderItem mHeaderItem;


        public HeaderHolder(View view) {
            super(view);
            mView = view;
            tvTile = view.findViewById(R.id.tvTitle);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvTile.getText() + "'";
        }
    }

}
