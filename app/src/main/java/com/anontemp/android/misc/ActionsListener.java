package com.anontemp.android.misc;

import com.anontemp.android.model.Tweet;

public interface ActionsListener {

    void onLikeClick(Tweet tweet);

    void onCommentClick(Tweet tweet);

}
