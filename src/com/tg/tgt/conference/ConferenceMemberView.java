package com.tg.tgt.conference;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hyphenate.easeui.GlideApp;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.media.EMCallSurfaceView;
import com.superrtc.sdk.VideoView;
import com.tg.tgt.R;
import com.tg.tgt.http.model2.GroupUserModel;

/**
 * Created by lzan13 on 2017/8/21.
 */
public class ConferenceMemberView extends RelativeLayout {

    private Context context;

    private EMCallSurfaceView surfaceView;
    private ImageView avatarView;
    private ImageView muteIcon;
    private ImageView videoIcon;
    private View activateView;
    private boolean isPubOrSub = false;

    public ConferenceMemberView(Context context) {
        this(context, null);
    }

    public ConferenceMemberView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConferenceMemberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.em_widget_conference_view, this);
        init();
    }

    private void init() {
        surfaceView = (EMCallSurfaceView) findViewById(R.id.item_surface_view);
        avatarView = (ImageView) findViewById(R.id.img_call_avatar);
        muteIcon = (ImageView) findViewById(R.id.icon_mute);
        videoIcon = (ImageView) findViewById(R.id.icon_video);
        activateView = findViewById(R.id.icon_activate);

        surfaceView.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
    }

    public EMCallSurfaceView getSurfaceView() {
        return surfaceView;
    }

    public void updateMuteState(boolean state) {
        if (state) {
            muteIcon.setVisibility(View.VISIBLE);
        } else {
            muteIcon.setVisibility(View.GONE);
        }
    }

    public void updateVideoState(boolean state) {
        if (state) {
            videoIcon.setVisibility(View.GONE);
            surfaceView.setVisibility(GONE);
            avatarView.setVisibility(View.VISIBLE);
        } else {
            surfaceView.setVisibility(VISIBLE);
            videoIcon.setVisibility(View.VISIBLE);
            avatarView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置当前 view 对应的 stream 的用户，主要用来语音通话时显示对方头像
     */
    public void setUser(String username) {
//        EaseUserUtils.setUserAvatar(context, username, avatarView);
        EaseUser userInfo = EaseUserUtils.getUserInfo(username);
//        if(!TextUtils.isEmpty(userInfo.getAvatar())){
            GlideApp.with(getContext()).load(userInfo.getAvatar()).placeholder(R.color.white).into(avatarView);
//        }
    }
    public void setGroupUser(GroupUserModel user) {
        if (user == null)
            return;
      //  if (user!=null && TextUtils.isEmpty(user.getPicture()))
        GlideApp.with(getContext()).load(user.getPicture()).into(avatarView);
    }

    /**
     * 判断是否已推流或订阅
     */
    public boolean isPubOrSub() {
        return isPubOrSub;
    }

    /**
     * 设置当前画面状态
     */
    public void setPubOrSub(boolean activate) {
        isPubOrSub = activate;
        if (isPubOrSub) {
            activateView.setBackgroundResource(R.color.btn_green_noraml);
        } else {
            activateView.setBackgroundResource(R.color.holo_red_light);
        }
    }

}
