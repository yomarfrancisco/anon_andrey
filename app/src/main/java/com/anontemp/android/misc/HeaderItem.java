package com.anontemp.android.misc;

import com.anontemp.android.BaseTweetItem;

/**
 * Created by jaydee on 19.08.17.
 */

public class HeaderItem extends BaseTweetItem {

    private String title;

    public HeaderItem(String title) {
        this.title = title;
        this.set_id(-1L);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int getType() {
        return Type.TYPE_HEADER;
    }
}
