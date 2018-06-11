package com.hyphenate.easeui.utils;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.hyphenate.easeui.EaseApp;
import com.hyphenate.easeui.widget.chatrow.timeUtil;
import com.hyphenate.util.DateUtils;
import com.hyphenate.util.TimeInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.hyphenate.util.DateUtils.getTodayStartAndEndTime;
import static com.hyphenate.util.DateUtils.getYesterdayStartAndEndTime;

/**
 *
 * @author yiyang
 */
public class TimeUtils {
    public static String getTime(long time){
        time = time / 1000;
//        int week = (int) (time / 3600 / 24 / 7);
//        int day = (int) (time / 3600 / 24 % 7);
//        int hour = (int) (time / 3600 % 24);
        int minute = (int) (time % 3600 / 60);
        int second = (int) (time % 60);

//        StringBuilder sb = new StringBuilder();
//        if(hour == 0){
//            sb.append("0:");
//        }else {
//            sb.append(hour+":");
//        }

//        if(minute == 0){
//            sb.append("0:");
//        }else {
//            sb.append(minute+":");
//        }
        if(second/10 < 1){
            return minute +":0"+second;
        }
        return minute+":"+second;

    }

    public static SpannableString getMomentDateShortString(String createTime) throws ParseException {
        String var1 = null;
        boolean var3 = EaseApp.applicationContext.getResources().getConfiguration().locale.getLanguage().startsWith("zh");
        SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.DEFAULT, var3?Locale.CHINA:Locale.ENGLISH);
        if(var3) {
            df.applyPattern("yyyy-MM-dd hh:mm:ss");
        }else {
            df.applyPattern("yyyy-MM-dd hh:mm:ss");
        }
        Date var0 = df.parse(createTime);
        long var4 = var0.getTime();
        if(isSameDay(var4)) {
            if(var3) {
                var1 = "今天";
            } else {
                var1 = "Today";
            }
        } else if(isYesterday(var4)) {
            if(var3) {
                var1 = "昨天";
            }else {
                var1 = "Yesterday";
                SpannableString spannableString = new SpannableString(var1);
                spannableString.setSpan(new RelativeSizeSpan(1.5f), 0, var1.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, var1.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                return spannableString;
            }
        } else{
            if(var3) {
                var1 = "dM月";
            }else {
                var1 = "ddMMM";
            }
            df.applyPattern(var1);
            var1 = df.format(var0);
        }
//        String s = var3 ? (new SimpleDateFormat(var1, Locale.CHINESE)).format(var0) : (new SimpleDateFormat(var1,
//                Locale.ENGLISH)).format(var0);

        SpannableString spannableString = new SpannableString(var1);
        spannableString.setSpan(new RelativeSizeSpan(2.0f), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public static String getMomentDateString(String createTime) throws ParseException {
        SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateInstance();
        df.applyPattern("yyyy-MM-dd hh:mm:ss");
        Date var0 = df.parse(createTime);
        return timeUtil.getTimestampString(var0);
    }

    private static boolean isSameDay(long var0) {
        TimeInfo var2 = getTodayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

    private static boolean isYesterday(long var0) {
        TimeInfo var2 = getYesterdayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

}
