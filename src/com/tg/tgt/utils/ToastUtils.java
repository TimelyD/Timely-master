package com.tg.tgt.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 *
 * @author yiyang
 */
public class ToastUtils {
    private static Toast toast;
    private static Handler sHandler;
    private static Handler getHandler(){
        if(sHandler == null)
            sHandler = new Handler(Looper.getMainLooper());
        return sHandler;
    }
    public static Toast makeText(Context context,
                                 String content, int duration){
        if (toast == null) {
            toast = Toast.makeText(context,
                    content,
                    duration);
        } else {
            toast.setText(content);
        }
        return toast;
    }

    public static void showToast(Context context,
                                 String content, int duration) {
        if (toast == null) {
            toast = Toast.makeText(context,
                    content,
                    duration);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

    public static void showToast(Context context, String content) {
        showToast(context, content, Toast.LENGTH_SHORT);
    }
    public static void safeShowToast(final Context context, final String content) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                showToast(context, content, Toast.LENGTH_SHORT);
            }
        });
    }
    public static void showToast(Context context, @StringRes int content) {
        showToast(context, context.getString(content), Toast.LENGTH_SHORT);
    }
    public static void showGeneralToast(Context context, String content) {
        showGeneralToast(context, content, Toast.LENGTH_SHORT);
    }
    public static void safeShowGeneralToast(final Context context, final String content) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                showGeneralToast(context, content, Toast.LENGTH_SHORT);
            }
        });
    }
    public static void showGeneralToast(Context context, String content, int duration) {
        Toast.makeText(context, content, duration).show();
    }
}
