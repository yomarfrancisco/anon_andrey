package com.anontemp.android.misc;

import android.widget.Filter;

import com.anontemp.android.BaseTweetItem;
import com.anontemp.android.Constants;
import com.anontemp.android.model.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaydee on 24.08.17.
 */

public class TweetsRegionFilter extends Filter {

    private final TweetsAdapter mTweetsAdapter;
    private final List<BaseTweetItem> mOriginalTweetItems;
    private final List<BaseTweetItem> mFilteredTweetItems;


    public TweetsRegionFilter(TweetsAdapter tweetsAdapter, List<BaseTweetItem> originalTweetItems) {
        mTweetsAdapter = tweetsAdapter;
        mOriginalTweetItems = originalTweetItems;
        mFilteredTweetItems = new ArrayList<>();
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {

        mFilteredTweetItems.clear();
        final FilterResults results = new FilterResults();

        int i = charSequence.length() == 0 ? 0 : Integer.valueOf(String.valueOf(charSequence));

        if (i <= 0) {
            HeaderItem item = (HeaderItem) mOriginalTweetItems.get(0);
            item.setTitle(Constants.MENU_WITS);
            mFilteredTweetItems.addAll(mOriginalTweetItems);
        } else {
            GeoRegion chosenRegion = Constants.LOCAL_REGIONS.get(i);
            HeaderItem headerItem = (HeaderItem) mOriginalTweetItems.get(0);
            headerItem.setTitle(chosenRegion.getTitle());
            mFilteredTweetItems.add(mOriginalTweetItems.get(0));
            for (BaseTweetItem item : mOriginalTweetItems) {
                if (item instanceof Tweet) {
                    Tweet tweet = (Tweet) item;
                    if (chosenRegion.getTitle().contains(tweet.getRegionName())) {
                        mFilteredTweetItems.add(tweet);
                    }
                }
            }

        }

        results.values = mFilteredTweetItems;
        results.count = mFilteredTweetItems.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        mTweetsAdapter.setList(mFilteredTweetItems);
        mTweetsAdapter.notifyDataSetChanged();

    }
}
