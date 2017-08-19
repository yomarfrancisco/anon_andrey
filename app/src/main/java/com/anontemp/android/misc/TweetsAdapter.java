package com.anontemp.android.misc;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.anontemp.android.BaseTweetItem;
import com.anontemp.android.R;
import com.anontemp.android.model.Tweet;
import com.anontemp.android.view.AnonTVSpecial;
import com.anontemp.android.view.AnonTView;

import java.util.List;

/**
 * Created by jaydee on 16.07.17.
 */

public class TweetsAdapter extends RecyclerView.Adapter {

    private final List<BaseTweetItem> tweets;
    private final Context context;

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
                TweetHolder th = (TweetHolder) holder;
                Tweet tweet = (Tweet) tweets.get(position);
                th.tweet = tweet;
                th.firstName.setText(tweet.getFirstName());
                th.location.setText(tweet.getRegionName());
                th.body.setText(tweet.getTweetText());
                th.mood.setText(tweet.getMoodText());
                th.gender.setText(tweet.getUsername());
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
    public int getItemCount() {
        return tweets.size();
    }

    @Override
    public long getItemId(int position) {
        return tweets.get(position).get_id();
    }


    public class TweetHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final AnonTVSpecial firstName;
        public final AnonTVSpecial location;
        public final AnonTView body;
        public final LinearLayout gifContainer;
        public final AnonTView mood;
        public final AnonTVSpecial gender;
        public final AnonTVSpecial tvTimeToDelete;
        public Tweet tweet;


        public TweetHolder(View view) {
            super(view);
            mView = view;
            firstName = view.findViewById(R.id.firstName);
            location = view.findViewById(R.id.location);
            body = view.findViewById(R.id.body);
            gifContainer = view.findViewById(R.id.gifContainer);
            mood = view.findViewById(R.id.mood);
            gender = view.findViewById(R.id.gender);
            tvTimeToDelete = view.findViewById(R.id.ttl);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + body.getText() + "'";
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
