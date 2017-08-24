package com.anontemp.android.misc;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anontemp.android.Constants;
import com.anontemp.android.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.anontemp.android.Constants.MENU_WITS;

/**
 * Created by jaydee on 24.08.17.
 */

public class MenuListAdapter extends BaseAdapter {


    public static final String PIN = "PIN SOMETHING";
    public static final String LIVE = "ANON. MOB LIVE";
    private final Context mContext;
    private final List<ListItem> mItems = new ArrayList<>();
    private Typeface mSpecial;
    private Typeface mHelvetica;

    public MenuListAdapter(Context context) {
        super();
        this.mContext = context;
        mItems.add(new HeadingItem(MENU_WITS));
        for (String title : Arrays.asList(Constants.MENU_LAW, Constants.MENU_BARNATO, Constants.MENU_LIBRARY, Constants.MENU_MATRIX, Constants.MENU_BLOCK, Constants.MENU_JUBES)) {
            mItems.add(new SimpleItem(title));
        }
        mItems.add(new HeadingItem(PIN));
        mItems.add(new HeadingItem(LIVE));
        mSpecial = FontCache.getTypeface("CFSnowboardProjectPERSONAL.ttf", mContext);
        mHelvetica = FontCache.getTypeface("helvetica-neue.otf", mContext);

    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        int type = getItemViewType(i);

        switch (type) {
            case ListItem.TYPE_HEAD:

                ViewHolder holder;
                View v = view;
                if (v == null) {
                    v = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.drawer_heading, viewGroup, false);
                    holder = new ViewHolder();
                    holder.mTextView = v.findViewById(R.id.text);
                    holder.mTextView.setTypeface(mSpecial);
                    v.setTag(holder);

                } else {
                    holder = (ViewHolder) v.getTag();
                }

                ListItem item = mItems.get(i);
                holder.mTextView.setText(item.title);
                return v;

            case ListItem.TYPE_SIMPLE:
                ViewHolder holder2;
                View v2 = view;
                if (v2 == null) {
                    v2 = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.drawer_simple, viewGroup, false);
                    holder2 = new ViewHolder();
                    holder2.mTextView = v2.findViewById(R.id.text);
                    holder2.mTextView.setTypeface(mHelvetica);
                    v2.setTag(holder2);

                } else {
                    holder2 = (ViewHolder) v2.getTag();
                }

                ListItem item2 = mItems.get(i);
                holder2.mTextView.setText(item2.title);
                return v2;


        }


        return null;
    }

    private abstract class ListItem {
        public static final int TYPE_HEAD = 0;
        public static final int TYPE_SIMPLE = 1;
        protected String title;

        public ListItem(String title) {
            this.title = title;
        }

        public abstract int getType();
    }

    private class HeadingItem extends ListItem {

        public HeadingItem(String title) {
            super(title);
        }

        @Override
        public int getType() {
            return TYPE_HEAD;
        }
    }

    private class SimpleItem extends ListItem {

        public SimpleItem(String title) {
            super(title);
        }

        @Override
        public int getType() {
            return TYPE_SIMPLE;
        }
    }

    public class ViewHolder {
        public TextView mTextView;

    }
}
