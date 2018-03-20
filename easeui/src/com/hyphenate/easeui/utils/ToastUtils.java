package com.hyphenate.easeui.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 *
 * @author yiyang
 */
public class ToastUtils {
    private static Toast toast;

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
    public static void showToast(Context context, @StringRes int content) {
        showToast(context, context.getString(content), Toast.LENGTH_SHORT);
    }
    public static void showGeneralToast(Context context, String content) {
        showGeneralToast(context, content, Toast.LENGTH_SHORT);
    }
    public static void showGeneralToast(Context context, String content, int duration) {
        Toast.makeText(context, content, duration).show();
    }
}
