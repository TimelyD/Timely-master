package com.hyphenate.easeui;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.KeyBean;
import com.hyphenate.easeui.utils.SpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public static Boolean ontype=true;//图片放大页的点击事件
    public static String me_name;
    public static String map_me="map_me";
    public static String map_receiver="map_receiver";  //Keybean单聊
    public static String map_group="map_group";     //Keybean群聊
    public static String keyBean="keyBean";     //我的当前最新版本的keybean
    public static String keylist="keylist";     //我的keybean列表
    public static String pri_key;
    public static String pub_key;
    public static SharedPreferences sf;
    public static String groupId;   //当前对话页面的ID
    public static KeyBean receiver_pub; //接受者的bean
    public static List<KeyBean> group_pub;
    public static List<String> nick = new ArrayList<String>();
    public static List<EaseUser> mAlluserList=new ArrayList<>();
    public static String isFromId;

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
