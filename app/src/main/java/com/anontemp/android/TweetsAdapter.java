package com.anontemp.android;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anontemp.android.com.anontemp.android.model.Tweet;

import org.apache.commons.text.StringEscapeUtils;

import java.util.List;

/**
 * Created by jaydee on 16.07.17.
 */

public class TweetsAdapter extends RecyclerView.Adapter {

    private final List<Tweet> tweets;
    private final Context context;

    public TweetsAdapter(List<Tweet> tweets, Context context) {
        this.tweets = tweets;
        this.context = context;
        setHasStableIds(true);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tweet, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ViewHolder h = (ViewHolder) holder;
        Tweet tweet = tweets.get(position);
        h.tweet = tweet;
        h.firstName.setText(tweet.getFirstName());
        h.timestamp.setText(tweet.getVisibleDate());
        h.body.setText(tweet.getTweetText());
        h.regionText.setText(tweet.getRegionName());
        if (Constants.MOODS_UNICODE.get(StringEscapeUtils.escapeJava(tweet.getMoodText())) != null)
            h.mood.setImageDrawable(ContextCompat.getDrawable(context, Constants.MOODS_UNICODE.get(StringEscapeUtils.escapeJava(tweet.getMoodText()))));
        if (Constants.GENDERS_UNICODE.get(StringEscapeUtils.escapeJava(tweet.getUsername())) != null)
            h.gender.setImageDrawable(ContextCompat.getDrawable(context, Constants.GENDERS_UNICODE.get(StringEscapeUtils.escapeJava(tweet.getUsername()))));


    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    @Override
    public long getItemId(int position) {
        return tweets.get(position).get_id();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView firstName;
        public final TextView timestamp;
        public final TextView body;
        public final TextView regionText;
        public final ImageView mood;
        public final ImageView gender;
        public Tweet tweet;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            firstName = view.findViewById(R.id.firstName);
            timestamp = view.findViewById(R.id.timestamp);
            body = view.findViewById(R.id.body);
            regionText = view.findViewById(R.id.regionText);
            mood = view.findViewById(R.id.mood);
            gender = view.findViewById(R.id.gender);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + body.getText() + "'";
        }
    }


}
