package com.hyphenate.easeui;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.hyphenate.easeui.utils.SpUtils;

/**
 *
 * @author yiyang
 */
public class EaseApp extends Application {
    /**
     * 当前用户标识
     */
    protected static String myUid;
    public static Context applicationContext;

    public static String getMyUid() {
        if(TextUtils.isEmpty(myUid)){
            myUid = SpUtils.get(applicationContext, "login_myUid","");
        }
        return myUid;
    }

    public static void setMyUid(String myUid) {
        EaseApp.myUid = myUid;
    }
}
