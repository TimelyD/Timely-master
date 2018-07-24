package com.hyphenate.easeui;

/**
 * Created by DELL on 2018/7/24.
 */

public class GroupInterfaceUtil {

    private static GroupInterface mInstance;

    public static void setInstance(GroupInterface groupInterface){
        mInstance = groupInterface;
    }

    public static void sendHttpData(String id){
        mInstance.GetGroupDate(id);
    }
}
