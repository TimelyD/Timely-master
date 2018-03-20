package com.tg.tgt.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 *
 * @author yiyang
 */
public class ListViewAdaptWidth extends ListView {
    public ListViewAdaptWidth(Context context) {
        super(context);
    }

    public ListViewAdaptWidth(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ListViewAdaptWidth(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMaxWidthOfChildren() + getPaddingLeft() + getPaddingRight();//计算listview的宽度
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), heightMeasureSpec);//设置listview的宽高

    }

    /**
     * 计算item的最大宽度
     *
     * @return
     */
    private int getMaxWidthOfChildren() {
        int maxWidth = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            maxWidth = getMinimumWidth();
        }
        View view = null;
        int count = getAdapter().getCount();
        for (int i = 0; i < count; i++) {
            try {
                view = getAdapter().getView(i, null, this);
                view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                if (view.getMeasuredWidth() > maxWidth)
                    maxWidth = view.getMeasuredWidth();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("maxWidth",""+maxWidth);
        }
        return maxWidth;
    }
}
