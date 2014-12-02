package com.androchill.call411.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

public class ScalingImageView extends android.widget.ImageView {

    private static final String TAG = "ScalingImageView";

    public ScalingImageView(Context context) {
        super(context);
    }

    public ScalingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScalingImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure w:" + widthMeasureSpec + " h:" + heightMeasureSpec);
        Log.d(TAG, "onMeasure result w:"+getMeasuredWidth()+" h:"+getMeasuredHeight());
        Drawable d = getDrawable();
        if (d != null) {
            Log.d(TAG, "Drawable w:"+d.getMinimumWidth()+" h:"+d.getMinimumHeight());
            //let's try to fix things up.
            final double ratio = ((double)d.getMinimumWidth())/((double)d.getMinimumHeight());
            //getting the minimum dim of this view
            int idealWidth = (int)(getMeasuredHeight() * ratio);
            int idealHeight = (int)(getMeasuredWidth() / ratio);
            //these are ideal, but with no respect to the other dim
            Log.d(TAG, "Ideal dims w:"+idealWidth+" h:"+idealHeight);
            //now.. Who is breaking the borders of the view?
            final int width;
            final int height;
            if (idealWidth > getMeasuredWidth()) {
                //I should reduce the height size.
                width = getMeasuredWidth();
                height = idealHeight;
            } else {
                width = idealWidth;
                height = getMeasuredHeight();
            }
            Log.d(TAG, "onMeasure new values w:"+width+" h:"+height);
            setMeasuredDimension(width, height);
        } else {
            Log.d(TAG, "Drawable is null");
        }
    }
}