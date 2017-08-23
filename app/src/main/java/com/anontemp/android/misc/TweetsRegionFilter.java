package com.anontemp.android.misc;

import android.widget.Filter;

import com.anontemp.android.BaseTweetItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaydee on 24.08.17.
 */

public class TweetsRegionFilter extends Filter {

    private final TweetsAdapter mTweetsAdapter;
    private final List<BaseTweetItem> mOriginalTweetItems;
    private final List<BaseTweetItem> mFilteredTweetItems;


    public TweetsRegionFilter(TweetsAdapter tweetsAdapter, List<BaseTweetItem> originalTweetItems, List<BaseTweetItem> filteredTweetItems) {
        mTweetsAdapter = tweetsAdapter;
        mOriginalTweetItems = originalTweetItems;
        mFilteredTweetItems = new ArrayList<>();
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {

        mFilteredTweetItems.clear();
        final FilterResults results = new FilterResults();


        return null;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

    }
}
