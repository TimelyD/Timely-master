package com.tg.tgt.helper;

import android.content.Context;

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.SpUtils;
import com.tg.tgt.Constant;
import com.tg.tgt.R;

/**
 *
 * @author yiyang
 */
public class UserHelper {
    private static final String MEN = "1";
    private static final String WOMEN = "2";
    private static final String UNKNOWN = "3";
    /**
     * 获得保存的性别
     * @param context
     * @return
     */
    public static String getGender(Context context){
        //性别（M：男，W：女，O：其他）
        String gender = SpUtils.get(context, Constant.SEX, "UNKNOWN");
        if(MEN.equals(gender)){
            gender = context.getString(R.string.men);
        }else if(WOMEN.equals(gender)){
            gender = context.getString(R.string.women);
        }else {
//            gender = context.getString(R.string.unknown_gender);
            gender = context.getString(R.string.popSex_secret);
        }
        return gender;
    }
    public static int getGenderDrawableRes(Context context){
        //性别（M：男，W：女，O：其他）
        String gender = SpUtils.get(context, Constant.SEX, "UNKNOWN");
        int res;
        if(MEN.equals(gender)){
            res = R.drawable.man;
        }else if(WOMEN.equals(gender)){
            res = R.drawable.woman;
        }else {
//            gender = context.getString(R.string.unknown_gender);
            res = -1;
        }
        return res;
    }
    public static int getGenderDrawableRes(EaseUser user){
        //性别（M：男，W：女，O：其他）
        String gender = user.getChatidsex();
        int res;
        if(MEN.equals(gender)){
            res = R.drawable.man;
        }else if(WOMEN.equals(gender)){
            res = R.drawable.woman;
        }else {
//            gender = context.getString(R.string.unknown_gender);
            res = -1;
        }
        return res;
    }
    public static int getGenderString(EaseUser user){
        //性别（M：男，W：女，O：其他）
        String gender = user.getChatidsex();
        int res;
        if(MEN.equals(gender)){
            res = R.string.men;
        }else if(WOMEN.equals(gender)){
            res = R.string.women;
        }else {
//            gender = context.getString(R.string.unknown_gender);
            res = R.string.popSex_secret;
        }
        return res;
    }

    /**
     * 获取改性别在服务器上所代表的字符串
     * @param context
     * @param sex
     * @return
     */
    public static String getGenderChar(Context context, String sex){
        //性别（M：男，W：女，O：其他）
        String gender;
        if(context.getString(R.string.men).equals(sex)){
            gender = MEN;
        }else if(context.getString(R.string.women).equals(sex)){
            gender = WOMEN;
        }else {
            gender = UNKNOWN;
//            gender = "M";
        }
        return gender;
    }

    public static boolean isFriend(String relationStatus){
        if("1".equals(relationStatus)){
            return true;
        }else {
            return false;
        }
    }
}
