package com.tg.tgt.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowVoicePlayClickListener;
import com.hyphenate.util.EMLog;
import com.tg.tgt.Constant;
import com.tg.tgt.conference.ConferenceActivity;
import com.tg.tgt.ui.VideoCallActivity;
import com.tg.tgt.ui.VoiceCallActivity;

/**
 * Created by lzan13 on 2018/4/2.
 * 耳机插入拔出监听广播接收类
 */
public class HeadsetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 耳机插入状态 0 拔出，1 插入
        //AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        boolean state = intent.getIntExtra("state", 0) == 0 ? false : true;
        Log.e("Tag","耳机拔插"+state);
        if (EaseChatRowVoicePlayClickListener.currentPlayListener != null){
            if (state)
                EaseChatRowVoicePlayClickListener.currentPlayListener.setModelVoice(2);
            else
                EaseChatRowVoicePlayClickListener.currentPlayListener.setModelVoice(0);
        }
        EaseConstant.isInputHeadset = state;
        if (VoiceCallActivity.Handler != null && state)
            VoiceCallActivity.Handler.sendEmptyMessage(100);
        if (VideoCallActivity.Handler != null && state)
            VideoCallActivity.Handler.sendEmptyMessage(101);
        if (ConferenceActivity.Handler != null && state)
            ConferenceActivity.Handler.sendEmptyMessage(102);
        // 耳机类型
        String name = intent.getStringExtra("name");
        // 耳机是否带有麦克风 0 没有，1 有
        boolean mic = intent.getIntExtra("microphone", 0) == 0 ? false : true;
        String headsetChange = String.format("耳机插入: %b, 有麦克风: %b", state, mic);
        //audioManager.setSpeakerphoneOn(false);
        EMLog.d("HeadsetReceiver", headsetChange);
       // Toast.makeText(context, headsetChange, Toast.LENGTH_SHORT).show();
    }
}
