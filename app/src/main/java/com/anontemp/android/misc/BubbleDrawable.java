package com.anontemp.android.misc;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by jaydee on 29.08.17.
 */

public class BubbleDrawable extends Drawable {
    private static final int OFFSET = 25;
    Paint whitePaint = new android.graphics.Paint();

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect r = getBounds();
        RectF rect = new RectF(r);
        rect.inset(OFFSET, OFFSET);

        //Build a path
        Path path = new Path();

        //up arrow
        path.moveTo(OFFSET, OFFSET);
        path.lineTo(OFFSET * 2, 0);
        path.lineTo(OFFSET * 3, OFFSET);

        //top horizontal line.
        path.lineTo(r.width() - OFFSET, OFFSET);

        //top right arc
        path.arcTo(new RectF(r.right - OFFSET * 2, OFFSET, r.right, OFFSET * 3), 270, 90);

        //right vertical line.
        path.lineTo(r.width(), r.height() - OFFSET);

        //bottom right arc.
        path.arcTo(new RectF(r.right - OFFSET * 2, r.bottom - OFFSET * 2, r.right, r.bottom), 0, 90);

        //bottom horizontal line.
        path.lineTo(OFFSET, r.height());

        //bottom left arc.
        path.arcTo(new RectF(0, r.bottom - OFFSET * 2, OFFSET * 2, r.bottom), 90, 90);

        //left horizontal line.
        path.lineTo(0, OFFSET);

        //top right arc.
        path.arcTo(new RectF(0, OFFSET, OFFSET * 2, OFFSET * 3), 180, 90);


        path.close();

        whitePaint.setColor(android.graphics.Color.WHITE);
        whitePaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, whitePaint);
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int i) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return android.graphics.PixelFormat.OPAQUE;
    }
}
