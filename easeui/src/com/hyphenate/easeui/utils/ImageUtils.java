package com.hyphenate.easeui.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hyphenate.easeui.GlideApp;

/**
 *
 * @author yiyang
 */
public class ImageUtils {

    /**
     * glide加载第一次为空的处理办法。。。
     * @param context
     * @param url
     * @param holder
     * @param imageView
     */
    public static void show(Context context, String url, @DrawableRes int holder, final ImageView imageView) {
        GlideApp.with(context).load(url).placeholder(holder).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                imageView.setImageDrawable(resource);
            }

        });
    }

    public static void show2(Context context, String url, @DrawableRes int holder, final ImageView imageView) {
        GlideApp.with(context).load(url).placeholder(holder).diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                imageView.setImageDrawable(resource);
            }

        });
    }

    /**
     * 判断imageview背景与res是否相同
     * @param context
     * @param iv
     * @param res
     * @return
     */
    public static boolean isBackgroundEqauls(Context context, ImageView iv, @DrawableRes int res){
        return iv.getBackground().getConstantState().equals(ContextCompat.getDrawable(context, res).getConstantState());
    }
    /**
     * 判断imageview的src与res是否相同
     * @param context
     * @param iv
     * @param res
     * @return
     */
    public static boolean isDrawableEqauls(Context context, ImageView iv, @DrawableRes int res){
        return iv.getDrawable().getConstantState().equals(ContextCompat.getDrawable(context, res).getConstantState());
    }
}
