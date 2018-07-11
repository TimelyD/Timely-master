/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tg.tgt.ui;

import android.content.Context;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.ImageUtils;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.tg.tgt.App;
import com.tg.tgt.DemoHelper;
import com.tg.tgt.R;
import com.tg.tgt.domain.VoiceBean;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.moment.bean.CollectBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

import static com.tg.tgt.R.id.tv_mute;

/**
 * 语音通话页面
 * 
 */
public class VoiceCallActivity extends CallActivity implements OnClickListener {
	private LinearLayout comingBtnContainer;
	private View hangupBtn;
	private View refuseBtn;
	private View answerBtn;
	private ImageView muteImage;
	private ImageView handsFreeImage;
    private TextView mTvMute;
    private TextView mTvHandsFree;
	private boolean isMuteState;
	private boolean isHandsfreeState;
	private TextView callStateTextView;
	private boolean endCallTriggerByMe = false;
	private Chronometer chronometer;
	String st1;
	private LinearLayout voiceContronlLayout;
    private TextView netwrokStatusVeiw;
    private boolean monitor = false;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
        	finish();
        	return;
        }
		setContentView(R.layout.em_activity_voice_call);
		DemoHelper.getInstance().isVoiceCalling = true;
		callType = 0;
        audoManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        comingBtnContainer = (LinearLayout) findViewById(R.id.ll_coming_call);
		refuseBtn =  findViewById(R.id.btn_refuse_call);
		answerBtn =  findViewById(R.id.btn_answer_call);
		hangupBtn =  findViewById(R.id.btn_hangup_call);
		muteImage = (ImageView) findViewById(R.id.iv_mute);
		handsFreeImage = (ImageView) findViewById(R.id.iv_handsfree);
        ImageView ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        TextView nickTextView = (TextView) findViewById(R.id.tv_nick);
        TextView durationTextView = (TextView) findViewById(R.id.tv_calling_duration);
		chronometer = (Chronometer) findViewById(R.id.chronometer);
		voiceContronlLayout = (LinearLayout) findViewById(R.id.ll_voice_control);
		netwrokStatusVeiw = (TextView) findViewById(R.id.tv_network_status);
        mTvMute = (TextView) findViewById(tv_mute);
        mTvHandsFree = (TextView) findViewById(R.id.tv_handsfree);
        handler();
		refuseBtn.setOnClickListener(this);
		answerBtn.setOnClickListener(this);
		hangupBtn.setOnClickListener(this);
		muteImage.setOnClickListener(this);
		handsFreeImage.setOnClickListener(this);

		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		addCallStateListener();
		msgid = UUID.randomUUID().toString();

		username = getIntent().getStringExtra("username");
		isInComingCall = getIntent().getBooleanExtra("isComingCall", false);

		if (!isInComingCall) {// outgoing call

            EaseUser user = EaseUserUtils.getUserInfo(username);
            if(user.safeGetRemark()!=null) {
                ImageUtils.show(this, user.getAvatar(), R.drawable.default_avatar, ivAvatar);
                nickTextView.setText(user.safeGetRemark());
            }else {
                nickTextView.setText(username);
            }

            chronometer.setText(R.string.waiting_to_listen);
			soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
			outgoing = soundPool.load(this, R.raw.em_outgoing, 1);

//			comingBtnContainer.setVisibility(View.INVISIBLE);
            answerBtn.setVisibility(View.GONE);
			hangupBtn.setVisibility(View.VISIBLE);
			st1 = getResources().getString(R.string.Are_connected_to_each_other);
			callStateTextView.setText(st1);
			handler.sendEmptyMessage(MSG_CALL_MAKE_VOICE);
            handler.postDelayed(new Runnable() {
                public void run() {
                    streamID = playMakeCallSounds();
                }
            }, 300);
        } else { // incoming call
            mVibrator.vibrate(new long[]{1000, 1000, 1000, 1000},0);
            EaseUser user = EaseUserUtils.getUserInfo(username);
            if (user.safeGetRemark() != null) {
                nickTextView.setText(user.safeGetRemark());
                ImageUtils.show(this, user.getAvatar(), R.drawable.default_avatar, ivAvatar);
            }else {
                try {
                    String ext = EMClient.getInstance().callManager().getCurrentCallSession().getExt();
//                ToastUtils.showToast(getApplicationContext(), "ext:  "+ext);
                    //格式:昵称-头像
                    nickTextView.setText(ext.substring(0, ext.lastIndexOf("-")));
                    ImageUtils.show(this, ext.substring(ext.lastIndexOf("-") + 1), R.drawable.default_avatar, ivAvatar);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//            chronometer.setText(getString(R.string.from)+nickTextView.getText().toString().trim()+getString(R.string.de_voice_call));
            chronometer.setText(R.string.invite_to_voice);
			voiceContronlLayout.setVisibility(View.INVISIBLE);
			Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			audioManager.setMode(AudioManager.MODE_RINGTONE);
			audioManager.setSpeakerphoneOn(true);
			ringtone = RingtoneManager.getRingtone(this, ringUri);
			ringtone.play();

        }
        final int MAKE_CALL_TIMEOUT = 50 * 1000;
        handler.removeCallbacks(timeoutHangup);
        handler.postDelayed(timeoutHangup, MAKE_CALL_TIMEOUT);
	}
    public static Handler Handler ;
    private void handler(){
        Handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 2://语音
                        hangupBtn.setEnabled(false);
                        chronometer.stop();
                        endCallTriggerByMe = true;
                        callStateTextView.setText(getResources().getString(R.string.hanging_up));
                        handler.sendEmptyMessage(MSG_CALL_END);
                        break;
                }
            }
        };
    }
	/**
	 * set call state listener
	 */

	void addCallStateListener() {
	    callStateListener = new EMCallStateChangeListener() {
            
            @Override
            public void onCallStateChanged(CallState callState, final CallError error) {
                // Message msg = handler.obtainMessage();
                EMLog.d("EMCallManager", "onCallStateChanged:" + callState);
                switch (callState) {
                case NETWORK_DISCONNECTED:
                    Handler.sendEmptyMessage(2);
                    break;
                case CONNECTING:
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            callStateTextView.setText(st1);
                        }
                    });
                    break;
                case CONNECTED:
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
//                            String st3 = getResources().getString(R.string.have_connected_with);
//                            callStateTextView.setText(st3);
                        }
                    });
                    break;

                case ACCEPTED:
                    handler.removeCallbacks(timeoutHangup);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (soundPool != null)
                                    soundPool.stop(streamID);
                            } catch (Exception e) {
                            }
                            if(!isHandsfreeState)
                                closeSpeakerOn();
                            //show relay or direct call, for testing purpose
                            ((TextView)findViewById(R.id.tv_is_p2p)).setText(EMClient.getInstance().callManager().isDirectCall()
                                    ? R.string.direct_call : R.string.relay_call);
                            chronometer.setVisibility(View.VISIBLE);
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            chronometer.start();
                            String str4 = getResources().getString(R.string.In_the_call);
                            callStateTextView.setText(str4);
                            callingState = CallingState.NORMAL;
//                            startMonitor();

                            voiceContronlLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    break;
                case NETWORK_UNSTABLE:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            netwrokStatusVeiw.setVisibility(View.VISIBLE);
                            if(error == CallError.ERROR_NO_DATA){
                                netwrokStatusVeiw.setText(R.string.no_call_data);
                            }else{
                                netwrokStatusVeiw.setText(R.string.network_unstable);
                            }
                        }
                    });
                    break;
                case NETWORK_NORMAL:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            netwrokStatusVeiw.setVisibility(View.INVISIBLE);
                        }
                    });
                    break;
                case VOICE_PAUSE:
                    /*runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "VOICE_PAUSE", Toast.LENGTH_SHORT).show();
                        }
                    });*/
                    break;
                case VOICE_RESUME:
                    /*runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "VOICE_RESUME", Toast.LENGTH_SHORT).show();
                        }
                    });*/
                    break;
                case DISCONNECTED:
                    handler.removeCallbacks(timeoutHangup);
                    @SuppressWarnings("UnnecessaryLocalVariable") final CallError fError = error;
                    if (fError == CallError.ERROR_UNAVAILABLE){
                        App.sf.edit().putBoolean("zq",false).commit();
                        EMMessage message = EMMessage.createTxtSendMessage("未接听，点击回拨",username);
                        JSONObject a=new JSONObject();
                        try {
                            a.put("em_push_title","您有一个新来电");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        message.setAttribute("em_apns_ext",a);
                        message.setAttribute("VoiceOrVideoText","未接听，点击回拨");
                        message.setAttribute("VoiceOrVideoImage","ease_chat_voice_call_receive");
                        EMClient.getInstance().chatManager().sendMessage(message);
                        sms();
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        private void postDelayedCloseMsg() {
                            handler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("AAA", "CALL DISCONNETED");
                                            removeCallStateListener();
                                            saveCallRecord();
                                            Animation animation = new AlphaAnimation(1.0f, 0.0f);
                                            animation.setDuration(800);
                                            findViewById(R.id.root_layout).startAnimation(animation);
                                            finish();
                                        }
                                    });
                                }
                            }, 200);
                        }

                        @Override
                        public void run() {
                            chronometer.stop();
                            callDruationText = chronometer.getText().toString();
                            String st1 = getResources().getString(R.string.Refused);
                            String st2 = getResources().getString(R.string.The_other_party_refused_to_accept);
                            String st3 = getResources().getString(R.string.Connection_failure);
                            String st4 = getResources().getString(R.string.The_other_party_is_not_online);
                            String st5 = getResources().getString(R.string.The_other_is_on_the_phone_please);
                            
                            String st6 = getResources().getString(R.string.The_other_party_did_not_answer_new);
                            String st7 = getResources().getString(R.string.hang_up);
                            String st8 = getResources().getString(R.string.The_other_is_hang_up);
                            
                            String st9 = getResources().getString(R.string.did_not_answer);
                            String st10 = getResources().getString(R.string.Has_been_cancelled);
                            String st11 = getResources().getString(R.string.hang_up);
                            
                            if (fError == CallError.REJECTED) {
                                callingState = CallingState.BEREFUSED;
                                callStateTextView.setText(st2);
                            } else if (fError == CallError.ERROR_TRANSPORT) {
                                callStateTextView.setText(st3);
                            } else if (fError == CallError.ERROR_UNAVAILABLE) {
                                callingState = CallingState.OFFLINE;
                                callStateTextView.setText(st4);
                            } else if (fError == CallError.ERROR_BUSY) {
                                callingState = CallingState.BUSY;
                                callStateTextView.setText(st5);
                            } else if (fError == CallError.ERROR_NORESPONSE) {
                                callingState = CallingState.NO_RESPONSE;
                                callStateTextView.setText(st6);
                            } else if (fError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED || fError == CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED){
                                callingState = CallingState.VERSION_NOT_SAME;
                                callStateTextView.setText(R.string.call_version_inconsistent);
                            } else {
                                if (isRefused) {
                                    callingState = CallingState.REFUSED;
                                    callStateTextView.setText(st1);
                                }
                                else if (isAnswered) {
                                    callingState = CallingState.NORMAL;
                                    if (endCallTriggerByMe) {
//                                        callStateTextView.setText(st7);
                                    } else {
                                        callStateTextView.setText(st8);
                                    }
                                } else {
                                    if (isInComingCall) {
                                        callingState = CallingState.UNANSWERED;
                                        callStateTextView.setText(st9);
                                    } else {
                                        if (callingState != CallingState.NORMAL) {
                                            callingState = CallingState.CANCELLED;
                                            callStateTextView.setText(st10);
                                        }else {
                                            callStateTextView.setText(st11);
                                        }
                                    }
                                }
                            }
                            postDelayedCloseMsg();
                        }

                    });

                    break;

                default:
                    break;
                }

            }
        };
		EMClient.getInstance().callManager().addCallStateChangeListener(callStateListener);
	}
	
    void removeCallStateListener() {
        EMClient.getInstance().callManager().removeCallStateChangeListener(callStateListener);
    }

    protected void sms(){
        //Log.i("dcx",username);
        ApiManger2.getApiService()
                .voice_sms(username,"2")
                .compose(mActivity.<HttpResult<CollectBean>>bindToLifeCyclerAndApplySchedulers(null))
                .subscribe(new BaseObserver2<CollectBean>() {
                    @Override
                    protected void onSuccess(CollectBean emptyData) {

                    }
                    @Override
                    public void onFaild(int code, String message) {
                        super.onFaild(code, message);
                    }
                });
        }

        private AudioManager audoManager;
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_refuse_call:
		    if(isInComingCall) {
                isRefused = true;
                refuseBtn.setEnabled(false);
                handler.sendEmptyMessage(MSG_CALL_REJECT);
			    break;
            }

		case R.id.btn_hangup_call:
		    hangupBtn.setEnabled(false);
			chronometer.stop();
			endCallTriggerByMe = true;
			callStateTextView.setText(getResources().getString(R.string.hanging_up));
            handler.sendEmptyMessage(MSG_CALL_END);
			break;
		case R.id.btn_answer_call:
            mVibrator.cancel();
		    answerBtn.setEnabled(false);
		    closeSpeakerOn();
//            callStateTextView.setText("正在接听...");
			comingBtnContainer.setVisibility(View.INVISIBLE);
            hangupBtn.setVisibility(View.VISIBLE);
            voiceContronlLayout.setVisibility(View.VISIBLE);
            handler.sendEmptyMessage(MSG_CALL_ANSWER);
			break;


		case R.id.iv_mute:
			if (isMuteState) {
				muteImage.setImageResource(R.drawable.t_mute);
                mTvMute.setTextColor(ContextCompat.getColor(mContext, R.color.half_white));
                try {
                    EMClient.getInstance().callManager().resumeVoiceTransfer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
				isMuteState = false;
			} else {
				muteImage.setImageResource(R.drawable.t_mute_white);
                mTvMute.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                try {
                    EMClient.getInstance().callManager().pauseVoiceTransfer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
				isMuteState = true;
			}
			break;
		case R.id.iv_handsfree:
            boolean state = audoManager.isWiredHeadsetOn();
            Log.i("ddd",state+"");
           /* if(state==true){
            }else {*/
                if (isHandsfreeState) {
                    handsFreeImage.setImageResource(R.drawable.t_speaker);
                    mTvHandsFree.setTextColor(ContextCompat.getColor(mContext, R.color.half_white));
                    closeSpeakerOn();
                    isHandsfreeState = false;
                } else {
                    handsFreeImage.setImageResource(R.drawable.t_speaker_white);
                    mTvHandsFree.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    openSpeakerOn();
                    isHandsfreeState = true;
                }
            //}
			break;
		default:
			break;
		}
	}
	
    @Override
    protected void onDestroy() {
        DemoHelper.getInstance().isVoiceCalling = false;
        stopMonitor();
        super.onDestroy();
    }

	@Override
	public void onBackPressed() {
		callDruationText = chronometer.getText().toString();
	}

    /**
     * for debug & testing, you can remove this when release
     */
    void startMonitor(){
        monitor = true;
        new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        ((TextView)findViewById(R.id.tv_is_p2p)).setText(EMClient.getInstance().callManager().isDirectCall()
                                ? R.string.direct_call : R.string.relay_call);
                    }
                });
                while(monitor){
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }, "CallMonitor").start();
    }

    void stopMonitor() {
        monitor = false;
    }

}
