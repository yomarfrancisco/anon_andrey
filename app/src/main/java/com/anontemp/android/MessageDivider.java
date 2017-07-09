package com.anontemp.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class MessageDivider extends RecyclerView.ItemDecoration {
    private final Context context;

    public MessageDivider(Context context) {
        this.context = context;


    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

        int left;
        int right;

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            Drawable mDivider;

            Drawable solid = ContextCompat.getDrawable(context, R.drawable.line_divider);
            left = parent.getPaddingLeft() + 10;
            right = parent.getWidth() - parent.getPaddingRight();

            mDivider = solid;

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }


}
