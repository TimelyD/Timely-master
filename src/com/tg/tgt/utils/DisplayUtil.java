package com.tg.tgt.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;


public class DisplayUtil {

    /**
     * 屏幕宽度
     */
    private static int DisplayWidthPixels = 0;
    /**
     * 屏幕高度
     */
    private static int DisplayheightPixels = 0;

    private static int gridViewItemWidth = 0;

    private static int SmallSizeGridViewItemWidth = 0;

    private static int RecommendChannelPicWidth = 0;
    private static int RecommendUserPicWidth = 0;
    private static int PointD = 0;
    private static int PhotoWidth = 0;
    private static int smallPic;
    private static int middlePic;
    private static int largePic;
    private static int thumbnailWidth;
    public static final int commonWidth = 480;
    public static final int commonHeight = 800;
    public static  int stickWidth=0;
    /**
     * 获取屏幕参数
     *
     * @param context
     */
    private static void getDisplayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        DisplayWidthPixels = dm.widthPixels;// 宽度
        DisplayheightPixels = dm.heightPixels;// 高度
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getDisplayWidthPixels(Context context) {
        if (context == null) {
            return -1;
        }
        if (DisplayWidthPixels == 0) {
            getDisplayMetrics(context);
        }
        return DisplayWidthPixels;
    }









    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getDisplayheightPixels(Context context) {
        if (context == null) {
            return -1;
        }
        if (DisplayheightPixels == 0) {
            getDisplayMetrics(context);
        }
        return DisplayheightPixels;
    }

    /**
     * 将px值转换为dip或dp值
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getPointRadius(Context context) {
        if (PointD == 0) {
            PointD = getDisplayWidthPixels(context) / 45;
        }
        return PointD;
    }


    /**
     * 将dip或dp值转换为px值
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        if (context == null) {
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }



    /**
     * 将sp值转换为px值
     *
     * @param spValue
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    public static int getSmallSizeGridViewItemWidth(Context context) {
        if (context == null) {
            return -1;
        }
        if (SmallSizeGridViewItemWidth == 0) {
            SmallSizeGridViewItemWidth = (getDisplayWidthPixels(context) * 10 / 38 - 5 * dip2px(context, 1)) / 4;
        }
        return SmallSizeGridViewItemWidth;
    }

    public static void initHeight(ListView listView){
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount()-1))+50;
//        ((ViewGroup.MarginLayoutParams)params).setMargins(10, 10, 10, 10);
        listView.setLayoutParams(params);
    }

}
