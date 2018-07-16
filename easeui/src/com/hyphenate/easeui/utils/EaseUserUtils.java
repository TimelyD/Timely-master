package com.hyphenate.easeui.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;

public class EaseUserUtils {
    
    static EaseUI.EaseUserProfileProvider userProvider;
    
    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }
    
    /**
     * get EaseUser according username
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username){
        if(userProvider != null)
            return  userProvider.getUser(username.toLowerCase());
        
        return null;
    }
    
    /**
     * set user avatar
     * @param username
     */
    public static void setUserAvatar(Context context, String username, final ImageView imageView){
    	EaseUser user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //use default avatar
                Glide.with(context).load(user.getAvatar()).apply(new RequestOptions().placeholder(R.drawable.default_avatar)
                        .dontAnimate()).into(imageView);
//                ImageUtils.show(context, user.getAvatar(), R.drawable.default_avatar, imageView);
            }
        }else{
            Glide.with(context).load(com.hyphenate.easeui.R.drawable.default_avatar).into(imageView);
        }
    }
    /**
     *  正方形头像
     * */
    public static void setUserAvatar2(Context context, String username,String avater ,final ImageView imageView){
        EaseUser user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //use default avatar
                Glide.with(context).load(user.getAvatar()).apply(new RequestOptions().placeholder(R.drawable.default_avatar2)
                        .dontAnimate()).into(imageView);
//                ImageUtils.show(context, user.getAvatar(), R.drawable.default_avatar, imageView);
            }
        }else{
            if(avater!=null){
                Glide.with(context).load(avater).into(imageView);
            }else {
                Glide.with(context).load(com.hyphenate.easeui.R.drawable.default_avatar2).into(imageView);
            }
        }
    }

    /**
     * set user's nickname
     */
    public static void setUserNick(String username,TextView textView){
        if(textView != null){
        	EaseUser user = getUserInfo(username);
        	if(user != null && user.getNick() != null){
                if(!TextUtils.isEmpty(user.safeGetRemark())){
                    textView.setText(user.safeGetRemark());
                }else {
                    textView.setText(user.getNick());
                }
        	}else{
        		textView.setText(username);
        	}
        }
    }
    /**
     * set user's islock
     */
    public static void setIsLock(String username,ImageView imageView){
        if(imageView != null){
        	EaseUser user = getUserInfo(username);
        	if(user != null && user.getIsLock() == 1){
                imageView.setVisibility(View.VISIBLE);
            }else{
                imageView.setVisibility(View.INVISIBLE);
            }
        }
    }

}
