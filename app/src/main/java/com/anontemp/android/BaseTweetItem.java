package com.anontemp.android;

import com.google.firebase.database.Exclude;

/**
 * Created by jaydee on 19.08.17.
 */

public abstract class BaseTweetItem {

    private Long _id;


    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    @Exclude
    public abstract int getType();

    public static class Type {
        public static final int TYPE_TWEET = 2;
        public static final int TYPE_HEADER = 1;
    }

}
