package com.hyphenate.easeui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 *
 * @author yiyang
 */
@SuppressLint("AppCompatCustomView")
public class AutoWidthImageView extends ImageView {
    public AutoWidthImageView(Context context) {
        super(context);
    }

    public AutoWidthImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoWidthImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        Drawable d = getDrawable();
        if(d!=null){
            int height = MeasureSpec.getSize(heightMeasureSpec);
            //高度根据使得图片的宽度充满屏幕计算而得
            int width = (int) Math.ceil((float) height * (float) d.getIntrinsicWidth() / (float) d.getIntrinsicHeight());
            setMeasuredDimension(width, height);
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
