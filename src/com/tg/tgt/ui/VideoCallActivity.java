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
import android.graphics.Color;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMCallManager.EMCameraDataProcessor;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVideoCallHelper;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.ImageUtils;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.media.EMCallSurfaceView;
import com.hyphenate.util.EMLog;
import com.superrtc.sdk.VideoView;
import com.tg.tgt.App;
import com.tg.tgt.DemoHelper;
import com.tg.tgt.R;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.moment.bean.CollectBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

import static com.tg.tgt.R.id.tv_mute;

public class VideoCallActivity extends CallActivity implements OnClickListener {
    private boolean isMuteState;
    private boolean isHandsfreeState;
    private boolean isAnswered;
    private boolean endCallTriggerByMe = false;
    private boolean monitor = true;

    private TextView callStateTextView;

    private LinearLayout comingBtnContainer;
    private View refuseBtn;
    private View answerBtn;
    private View hangupBtn;
    private ImageView muteImage;
    private ImageView handsFreeImage;
    private TextView nickTextView;
    private Chronometer chronometer;
    private LinearLayout voiceContronlLayout;
    private RelativeLayout rootContainer;
    private LinearLayout topContainer;
    private LinearLayout bottomContainer;
    private TextView monitorTextView;
    private TextView netwrokStatusVeiw;

    private Handler uiHandler;

    private boolean isInCalling;
    boolean isRecording = false;
    //    private Button recordBtn;
    private EMVideoCallHelper callHelper;
    private Button toggleVideoBtn;

    //    private BrightnessDataProcess dataProcessor = new BrightnessDataProcess();
    private ImageView mIvAvatar;
    private AnimationSet mAvatarAnim;
    private TranslateAnimation mTextAnim;
    private ImageView mIvCamera;
    private TextView mTvMute;
    private TextView mTvHandsFree;
    private TextView mTvPackUp;
    private TextView mTvInviteMember;
    private TextView mTvCamera;
    private ImageView mIvPackUp;
    private ImageView mIvInvite;
    private EMCallSurfaceView localSurface;
    private EMCallSurfaceView oppositeSurface;
    private AudioManager audoManager;
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
                        if (isRecording) {
                            callHelper.stopVideoRecord();
                        }
                        EMLog.d(TAG, "btn_hangup_call");
                        handler.sendEmptyMessage(MSG_CALL_END);
                        break;
                }
            }
        };
    }
    // dynamic adjust brightness
    class BrightnessDataProcess implements EMCameraDataProcessor {
        byte yDelta = 0;

        synchronized void setYDelta(byte yDelta) {
            Log.d("VideoCallActivity", "brigntness uDelta:" + yDelta);
            this.yDelta = yDelta;
        }

        // data size is width*height*2
        // the first width*height is Y, second part is UV
        // the storage layout detailed please refer 2.x demo CameraHelper.onPreviewFrame
        @Override
        public synchronized void onProcessData(byte[] data, Camera camera, final int width, final int height, final
        int rotateAngel) {
            int wh = width * height;
            for (int i = 0; i < wh; i++) {
                int d = (data[i] & 0xFF) + yDelta;
                d = d < 16 ? 16 : d;
                d = d > 235 ? 235 : d;
                data[i] = (byte) d;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            finish();
            return;
        }
        setContentView(R.layout.em_activity_video_call);
        audoManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        DemoHelper.getInstance().isVideoCalling = true;
        callType = 1;
        handler();
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        uiHandler = new Handler();

        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        comingBtnContainer = (LinearLayout) findViewById(R.id.ll_coming_call);
        rootContainer = (RelativeLayout) findViewById(R.id.root_layout);
//        refuseBtn = (Button) findViewById(R.id.btn_refuse_call);
//        answerBtn = (Button) findViewById(R.id.btn_answer_call);
        hangupBtn = findViewById(R.id.hangup_call_layout);
        refuseBtn = findViewById(R.id.refuse_call_layout);
        answerBtn = findViewById(R.id.answer_call_layout);
        muteImage = (ImageView) findViewById(R.id.iv_mute);
        handsFreeImage = (ImageView) findViewById(R.id.iv_handsfree);
        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        mIvAvatar = (ImageView) findViewById(R.id.iv_avatar);
        nickTextView = (TextView) findViewById(R.id.tv_nick);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        voiceContronlLayout = (LinearLayout) findViewById(R.id.ll_voice_control);
        RelativeLayout btnsContainer = (RelativeLayout) findViewById(R.id.ll_btns);
        topContainer = (LinearLayout) findViewById(R.id.ll_top_container);
        bottomContainer = (LinearLayout) findViewById(R.id.ll_bottom_container);
        monitorTextView = (TextView) findViewById(R.id.tv_call_monitor);
        netwrokStatusVeiw = (TextView) findViewById(R.id.tv_network_status);
//        recordBtn = (Button) findViewById(R.id.btn_record_video);
        findViewById(R.id.btn_switch_camera).setOnClickListener(this);
        findViewById(R.id.btn_capture_image).setOnClickListener(this);
//        SeekBar YDeltaSeekBar = (SeekBar) findViewById(R.id.seekbar_y_detal);


        mTvMute = (TextView) findViewById(tv_mute);
        mTvHandsFree = (TextView) findViewById(R.id.tv_handsfree);
        mTvCamera = (TextView) findViewById(R.id.tv_camera);

        //自己添加的
        mIvCamera = (ImageView) findViewById(R.id.iv_camera);
        mIvCamera.setOnClickListener(this);

        findViewById(R.id.btn_mute).setOnClickListener(this);
        findViewById(R.id.btn_handsfree).setOnClickListener(this);


        refuseBtn.setOnClickListener(this);
        answerBtn.setOnClickListener(this);
        hangupBtn.setOnClickListener(this);
//        muteImage.setOnClickListener(this);
//        handsFreeImage.setOnClickListener(this);
        rootContainer.setOnClickListener(this);
//        recordBtn.setOnClickListener(this);

//        YDeltaSeekBar.setOnSeekBarChangeListener(new YDeltaSeekBarListener());

        msgid = UUID.randomUUID().toString();
        isInComingCall = getIntent().getBooleanExtra("isComingCall", false);
        username = getIntent().getStringExtra("username");


        // local surfaceview
        localSurface = (EMCallSurfaceView) findViewById(R.id.local_surface);
        localSurface.setZOrderMediaOverlay(true);
        localSurface.setZOrderOnTop(true);
        localSurface.setOnClickListener(this);

        // remote surfaceview
        oppositeSurface = (EMCallSurfaceView) findViewById(R.id.opposite_surface);

        // set call state listener
        addCallStateListener();
        if (!isInComingCall) {// outgoing call
            EaseUser user = EaseUserUtils.getUserInfo(username);
            if (user.safeGetRemark() != null) {
                nickTextView.setText(user.safeGetRemark());
                ImageUtils.show(this, user.getAvatar(), R.drawable.default_avatar, mIvAvatar);
            } else {
                nickTextView.setText(username);
            }

            chronometer.setText(R.string.waiting_to_listen);

            soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
            outgoing = soundPool.load(this, R.raw.em_outgoing, 1);

//            comingBtnContainer.setVisibility(View.INVISIBLE);
//            hangupBtn.setVisibility(View.VISIBLE);
            hangupBtn.setVisibility(View.GONE);
            answerBtn.setVisibility(View.GONE);
            String st = getResources().getString(R.string.Are_connected_to_each_other);
            callStateTextView.setText(st);
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
            handler.sendMessage(handler.obtainMessage(MSG_CALL_MAKE_VIDEO));
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
                ImageUtils.show(this, user.getAvatar(), R.drawable.default_avatar, mIvAvatar);
            }else {
                try {
                    String ext = EMClient.getInstance().callManager().getCurrentCallSession().getExt();
//                ToastUtils.showToast(getApplicationContext(), "ext:  "+ext);
                    //格式:昵称-头像
                    nickTextView.setText(ext.substring(0, ext.lastIndexOf("-")));
                    ImageUtils.show(this, ext.substring(ext.lastIndexOf("-") + 1), R.drawable.default_avatar, mIvAvatar);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//            callStateTextView.setText("Ringing");
            if (EMClient.getInstance().callManager().getCallState() == EMCallStateChangeListener.CallState.IDLE
                    || EMClient.getInstance().callManager().getCallState() == EMCallStateChangeListener.CallState
                    .DISCONNECTED) {
                // the call has ended
                finish();
                return;
            }
//            chronometer.setText(getString(R.string.from)+nickTextView.getText().toString().trim()+getString(R.string.de_video_call));
            chronometer.setText(R.string.invite_to_video);
            voiceContronlLayout.setVisibility(View.INVISIBLE);
            localSurface.setVisibility(View.INVISIBLE);
            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(true);
            ringtone = RingtoneManager.getRingtone(this, ringUri);
            ringtone.play();
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
        }

        final int MAKE_CALL_TIMEOUT = 50 * 1000;
        handler.removeCallbacks(timeoutHangup);
        handler.postDelayed(timeoutHangup, MAKE_CALL_TIMEOUT);

        // get instance of call helper, should be called after setSurfaceView was called
        callHelper = EMClient.getInstance().callManager().getVideoCallHelper();

//        EMClient.getInstance().callManager().setCameraDataProcessor(dataProcessor);

        //判断是视频通话还是语音，并切换视图
//        if (!isHasVideo) {
//            tvHandUp.setTextColor(noVideoColor);
//            tvAnswer.setTextColor(noVideoColor);
//            tvReply.setTextColor(noVideoColor);
//            refreshView(false);
//        }
    }

    public void refreshView(boolean hasVideo){
        if(!hasVideo) {
            oppositeSurface.setVisibility(View.GONE);

            if(!isMuteState) {
                mTvMute.setTextColor(noVideoColor);
                muteImage.setImageResource(R.drawable.mute_gray);
            }

            if(!isHandsfreeState) {
                mTvHandsFree.setTextColor(noVideoColor);
                handsFreeImage.setImageResource(R.drawable.speaker_gray);
            }

            mTvPackUp.setTextColor(noVideoColor);
            mIvPackUp.setImageResource(R.drawable.pack_up_gray);

            mTvInviteMember.setTextColor(noVideoColor);
            mIvInvite.setImageResource(R.drawable.invite_member_gray);

            mTvCamera.setTextColor(noVideoColor);
            mTvCamera.setText(R.string.open_camera);
            mIvCamera.setImageResource(R.drawable.camera_gray);

            topContainer.setBackgroundResource(R.drawable.video_bg);
        }else {
            oppositeSurface.setVisibility(View.VISIBLE);
            int white = getResources().getColor(R.color.white);
            if(!isMuteState) {
                mTvMute.setTextColor(white);
                muteImage.setImageResource(R.drawable.mute_white);
            }

            if(!isHandsfreeState) {
                mTvHandsFree.setTextColor(white);
                handsFreeImage.setImageResource(R.drawable.speaker_white);
            }

            mTvPackUp.setTextColor(white);
            mIvPackUp.setImageResource(R.drawable.pack_up_white);

            mTvInviteMember.setTextColor(white);
            mIvInvite.setImageResource(R.drawable.invite_member_white);

            mTvCamera.setTextColor(white);
            mTvCamera.setText(R.string.close_camera);
            mIvCamera.setImageResource(R.drawable.camera_white);

            topContainer.setBackgroundDrawable(null);
        }
    }

    private int noVideoColor = Color.parseColor("#7F7F7F");
//    class YDeltaSeekBarListener implements SeekBar.OnSeekBarChangeListener {
//
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            dataProcessor.setYDelta((byte) (20.0f * (progress - 50) / 50.0f));
//        }
//
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//        }
//
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//        }
//
//    }

    /**
     * set call state listener
     */
    void addCallStateListener() {
        callStateListener = new EMCallStateChangeListener() {

            @Override
            public void onCallStateChanged(final CallState callState, final CallError error) {
                switch (callState) {

                    case CONNECTING: // is connecting
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                callStateTextView.setText(R.string.Are_connected_to_each_other);
                            }

                        });
                        break;
                    case CONNECTED: // connected
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
//                                if (!isHasVideo) {
//                                    try {
//                                        EMClient.getInstance().callManager().pauseVideoTransfer();
//                                    } catch (HyphenateException e) {
//                                        e.printStackTrace();
//                                    }
//                                } else {
//                                    animateTop(true);
                                    oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
//                                }

//                            callStateTextView.setText(R.string.have_connected_with);
                            }

                        });
                        break;

                    case ACCEPTED: // call is accepted
                        handler.removeCallbacks(timeoutHangup);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (soundPool != null)
                                        soundPool.stop(streamID);
                                    EMLog.d("EMCallManager", "soundPool stop ACCEPTED");
                                } catch (Exception e) {
                                }
                                openSpeakerOn();
                                ((TextView) findViewById(R.id.tv_is_p2p)).setText(EMClient.getInstance().callManager
                                        ().isDirectCall()
                                        ? R.string.direct_call : R.string.relay_call);
                                handsFreeImage.setImageResource(R.drawable.t_speaker_white);
                                mTvHandsFree.setTextColor(getResources().getColor(R.color.white));
                                isHandsfreeState = true;
                                isInCalling = true;
                                chronometer.setVisibility(View.VISIBLE);
                                chronometer.setBase(SystemClock.elapsedRealtime());
                                chronometer.start();
//                            nickTextView.setVisibility(View.INVISIBLE);
                                callStateTextView.setText(R.string.In_the_call);
//                            recordBtn.setVisibility(View.VISIBLE);
                                callingState = CallingState.NORMAL;
//                                startMonitor();
                                findViewById(R.id.call_control_layout).setVisibility(View.VISIBLE);
                                hangupBtn.setVisibility(View.VISIBLE);
                                refuseBtn.setVisibility(View.GONE);
                                //topContainer.setVisibility(View.GONE);
                                mIvAvatar.setVisibility(View.INVISIBLE);
                                localSurface.setVisibility(View.VISIBLE);
                            }

                        });
                        break;
                    case NETWORK_DISCONNECTED:
                        Handler.sendEmptyMessage(2);
                        /*runOnUiThread(new Runnable() {
                            public void run() {
                                netwrokStatusVeiw.setVisibility(View.VISIBLE);
                                netwrokStatusVeiw.setText(R.string.network_unavailable);
                            }
                        });*/
                        break;
                    case NETWORK_UNSTABLE:
                        runOnUiThread(new Runnable() {
                            public void run() {
                                netwrokStatusVeiw.setVisibility(View.VISIBLE);
                                if (error == CallError.ERROR_NO_DATA) {
                                    netwrokStatusVeiw.setText(R.string.no_call_data);
                                } else {
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
                    case VIDEO_PAUSE:
                        runOnUiThread(new Runnable() {
                            public void run() {
//                                canSetHasVideo = true;
//                                ToastUtils.showToast(getApplicationContext(), R.string.opposite_close_camera);
//                                setHasVideo(false);
//                                Toast.makeText(getApplicationContext(), "VIDEO_PAUSE", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case VIDEO_RESUME:
                        runOnUiThread(new Runnable() {
                            public void run() {
//                                canSetHasVideo = true;
//                                ToastUtils.showToast(getApplicationContext(), R.string.opposite_open_camera);
//                                setHasVideo(true);
//                                Toast.makeText(getApplicationContext(), "VIDEO_RESUME", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case VOICE_PAUSE:
                        runOnUiThread(new Runnable() {
                            public void run() {
//                                Toast.makeText(getApplicationContext(), "VOICE_PAUSE", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case VOICE_RESUME:
                        runOnUiThread(new Runnable() {
                            public void run() {
//                                canSetHasVideo = true;

//                                ToastUtils.showToast(getApplicationContext(), R.string.opposite_open_camera);
//                                setHasVideo(true);
//                                Toast.makeText(getApplicationContext(), "VOICE_RESUME", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case DISCONNECTED: // call is disconnected
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
                            message.setAttribute("VoiceOrVideoImage","ease_chat_video_call_receive");
                            EMClient.getInstance().chatManager().sendMessage(message);
                            sms();
                            //return;
                        }
                        runOnUiThread(new Runnable() {
                            private void postDelayedCloseMsg() {
                                uiHandler.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        removeCallStateListener();
                                        saveCallRecord();
                                        Animation animation = new AlphaAnimation(1.0f, 0.0f);
                                        animation.setDuration(1200);
                                        rootContainer.startAnimation(animation);
                                        finish();
                                    }

                                }, 1000);
                            }

                            @Override
                            public void run() {
                                chronometer.stop();
                                callDruationText = chronometer.getText().toString();
                                String s1 = getResources().getString(R.string.The_other_party_refused_to_accept);
                                String s2 = getResources().getString(R.string.Connection_failure);
                                String s3 = getResources().getString(R.string.The_other_party_is_not_online);
                                String s4 = getResources().getString(R.string.The_other_is_on_the_phone_please);
                                String s5 = getResources().getString(R.string.The_other_party_did_not_answer);

                                String s6 = getResources().getString(R.string.hang_up);
                                String s7 = getResources().getString(R.string.The_other_is_hang_up);
                                String s8 = getResources().getString(R.string.did_not_answer);
                                String s9 = getResources().getString(R.string.Has_been_cancelled);
                                String s10 = getResources().getString(R.string.Refused);

                                if (fError == CallError.REJECTED) {
                                    callingState = CallingState.BEREFUSED;
                                    callStateTextView.setText(s1);
                                } else if (fError == CallError.ERROR_TRANSPORT) {
                                    callStateTextView.setText(s2);
                                } else if (fError == CallError.ERROR_UNAVAILABLE) {
                                    callingState = CallingState.OFFLINE;
                                    callStateTextView.setText(s3);
                                } else if (fError == CallError.ERROR_BUSY) {
                                    callingState = CallingState.BUSY;
                                    callStateTextView.setText(s4);
                                } else if (fError == CallError.ERROR_NORESPONSE) {
                                    callingState = CallingState.NO_RESPONSE;
                                    callStateTextView.setText(s5);
                                } else if (fError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED || fError ==
                                        CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED) {
                                    callingState = CallingState.VERSION_NOT_SAME;
                                    callStateTextView.setText(R.string.call_version_inconsistent);
                                } else {
                                    if (isRefused) {
                                        callingState = CallingState.REFUSED;
                                        callStateTextView.setText(s10);
                                    } else if (isAnswered) {
                                        callingState = CallingState.NORMAL;
                                        if (endCallTriggerByMe) {
//                                        callStateTextView.setText(s6);
                                        } else {
                                            callStateTextView.setText(s7);
                                        }
                                    } else {
                                        if (isInComingCall) {
                                            callingState = CallingState.UNANSWERED;
                                            callStateTextView.setText(s8);
                                        } else {
                                            if (callingState != CallingState.NORMAL) {
                                                callingState = CallingState.CANCELLED;
                                                callStateTextView.setText(s9);
                                            } else {
                                                callStateTextView.setText(s6);
                                            }
                                        }
                                    }
                                }
                                Toast.makeText(VideoCallActivity.this, callStateTextView.getText(), Toast
                                        .LENGTH_SHORT).show();
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
        ApiManger2.getApiService()
                .voice_sms(username,"1")
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


//    private long duration = 1000l;
//
//    private void animateTop(boolean toTop) {
//        canSetHasVideo = false;
//        if (toTop) {
//            TranslateAnimation avatarTransAnim = new TranslateAnimation(0f, -mIvAvatar.getX() * 2 + PhoneUtil.dp2px(App.applicationContext, 10), 0f, -mIvAvatar
//                    .getY());
//
//            ScaleAnimation avatarScaleAnim = new ScaleAnimation(1.0f, 0.5f, 1.0f, 0.5f);
//            mAvatarAnim = new AnimationSet(true);
//            mAvatarAnim.addAnimation(avatarTransAnim);
//            mAvatarAnim.addAnimation(avatarScaleAnim);
//
//            mTextAnim = new TranslateAnimation(0f, -nickTextView.getX() + mIvAvatar.getWidth
//                    () / 2 + PhoneUtil.dp2px(App.applicationContext, 10), 0f, -nickTextView.getY() + mIvAvatar.getWidth() / 18);
//
//        } else {
//            TranslateAnimation avatarTransAnim = new TranslateAnimation(-mIvAvatar.getX() * 2 + PhoneUtil.dp2px(App.applicationContext, 10), 0f, -mIvAvatar
//                    .getY(), 0f);
//
//            ScaleAnimation avatarScaleAnim = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f);
//            mAvatarAnim = new AnimationSet(true);
//            mAvatarAnim.addAnimation(avatarTransAnim);
//            mAvatarAnim.addAnimation(avatarScaleAnim);
//
//            mTextAnim = new TranslateAnimation(-nickTextView.getX() + mIvAvatar.getWidth
//                    () / 2 + PhoneUtil.dp2px(App.applicationContext, 10), 0f, -nickTextView.getY() + mIvAvatar.getWidth() / 18, 0f);
//        }
//        nickTextView.setAnimation(mTextAnim);
//        chronometer.setAnimation(mTextAnim);
//        mIvAvatar.setAnimation(mAvatarAnim);
//        mAvatarAnim.setFillAfter(true);
//        mAvatarAnim.setDuration(duration);
//        mTextAnim.setDuration(duration);
//        mTextAnim.setFillAfter(true);
//        mAvatarAnim.start();
//        mTextAnim.start();
//
//        mAvatarAnim.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                refreshView(isHasVideo);
//                canSetHasVideo = true;
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//
////        PropertyValuesHolder mPropertyValuesHolderScaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f,0.5f);
////        PropertyValuesHolder mPropertyValuesHolderScaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f,0.5f);
////        PropertyValuesHolder translateX = PropertyValuesHolder.ofFloat("translationX", 0f,-mIvAvatar.getX());
////        PropertyValuesHolder translateY = PropertyValuesHolder.ofFloat("translationY", 0f,-mIvAvatar.getY());
////        PropertyValuesHolder nickTranslateX = PropertyValuesHolder.ofFloat("nickTranslationX", 0f,-nickTextView
//// .getX()+mIvAvatar.getWidth());
////        PropertyValuesHolder nickTranslateY = PropertyValuesHolder.ofFloat("nickTranslationY", 0f,-nickTextView
//// .getY()+mIvAvatar.getWidth()/4);
////        ValueAnimator valueAnimator = ValueAnimator.ofPropertyValuesHolder(mPropertyValuesHolderScaleX,
//// mPropertyValuesHolderScaleY, translateX, translateY,nickTranslateX,nickTranslateY);
////        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
////            @Override
////            public void onAnimationUpdate(ValueAnimator animation) {
////                float animatorValueScaleX =  (float) animation.getAnimatedValue("scaleX");
////                float animatorValueScaleY = (float) animation.getAnimatedValue("scaleY");
////                mIvAvatar.setScaleX(animatorValueScaleX);
////                mIvAvatar.setScaleY(animatorValueScaleY);
////                mIvAvatar.setTranslationX((float) animation.getAnimatedValue("translationX"));
////                mIvAvatar.setTranslationY((float) animation.getAnimatedValue("translationY"));
////                nickTextView.setTranslationX((float) animation.getAnimatedValue("nickTranslationX"));
////                nickTextView.setTranslationY((float) animation.getAnimatedValue("nickTranslationY"));
////                chronometer.setTranslationX((float) animation.getAnimatedValue("nickTranslationX"));
////                chronometer.setTranslationY((float) animation.getAnimatedValue("nickTranslationY"));
////            }
////        });
////        valueAnimator.setTarget(mIvAvatar);
////        valueAnimator.setDuration(2000);
////        valueAnimator.start();
//    }
    private boolean state;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_camera:
                break;
            case R.id.answer_call_layout: // answer the call
                EMLog.d(TAG, "btn_answer_call clicked");
                mVibrator.cancel();
                answerBtn.setEnabled(false);
                //openSpeakerOn();
                if (ringtone != null)
                    ringtone.stop();

                callStateTextView.setText("answering...");
                handler.sendEmptyMessage(MSG_CALL_ANSWER);
//                handsFreeImage.setImageResource(R.drawable.em_icon_speaker_on);
                isAnswered = true;
                isHandsfreeState = true;
                comingBtnContainer.setVisibility(View.INVISIBLE);
                hangupBtn.setVisibility(View.VISIBLE);
                voiceContronlLayout.setVisibility(View.VISIBLE);
                localSurface.setVisibility(View.VISIBLE);
                findViewById(R.id.call_control_layout).setVisibility(View.VISIBLE);
                state = audoManager.isWiredHeadsetOn();
               /* if(state==true){
                    handsFreeImage.setImageResource(R.drawable.t_speaker);
                    mTvHandsFree.setTextColor(ContextCompat.getColor(App.applicationContext, R.color.half_white));
                    closeSpeakerOn();
                    isHandsfreeState = false;
                }else {
                    openSpeakerOn();
                }*/
                break;
            case R.id.refuse_call_layout: // decline the call
                mVibrator.cancel();
                if(isInComingCall) { //outgoing
                    isRefused = true;
                    refuseBtn.setEnabled(false);
                    handler.sendEmptyMessage(MSG_CALL_REJECT);
                    break;
                }


            case R.id.hangup_call_layout: // hangup
                hangupBtn.setEnabled(false);
                chronometer.stop();
                endCallTriggerByMe = true;
                callStateTextView.setText(getResources().getString(R.string.hanging_up));
                if (isRecording) {
                    callHelper.stopVideoRecord();
                }
                EMLog.d(TAG, "btn_hangup_call");
                handler.sendEmptyMessage(MSG_CALL_END);
                break;

            case R.id.btn_mute: // mute
                if (isMuteState) {
                    // resume voice transfer
//                    if (isHasVideo) {
                        muteImage.setImageResource(R.drawable.t_mute);
                        mTvMute.setTextColor(ContextCompat.getColor(App.applicationContext, R.color.half_white));
//                    } else {
//                        muteImage.setImageResource(R.drawable.mute_gray);
//                        mTvMute.setTextColor(noVideoColor);
//                    }
                    try {
                        EMClient.getInstance().callManager().resumeVoiceTransfer();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    isMuteState = false;
                } else {
                    // pause voice transfer
                    muteImage.setImageResource(R.drawable.t_mute_white);
                    mTvMute.setTextColor(ContextCompat.getColor(App.applicationContext, R.color.white));
                    try {
                        EMClient.getInstance().callManager().pauseVoiceTransfer();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    isMuteState = true;
                }
                break;
            case R.id.btn_handsfree: // handsfree
                state = audoManager.isWiredHeadsetOn();
                Log.i("ddd",state+"");
                /*if(state==true){
                }else{*/
                    if (isHandsfreeState) {
//                    if (isHasVideo) {
                        handsFreeImage.setImageResource(R.drawable.t_speaker);
                        mTvHandsFree.setTextColor(ContextCompat.getColor(App.applicationContext, R.color.half_white));
//                    } else {
//                        handsFreeImage.setImageResource(R.drawable.speaker_gray);
//                        mTvHandsFree.setTextColor(noVideoColor);
//                    }
                        closeSpeakerOn();
                        isHandsfreeState = false;
                    } else {
                        handsFreeImage.setImageResource(R.drawable.t_speaker_white);
                        mTvHandsFree.setTextColor(ContextCompat.getColor(App.applicationContext, R.color.white));
                        openSpeakerOn();
                        isHandsfreeState = true;
                    }
               // }
                break;
        /*
        case R.id.btn_record_video: //record the video
            if(!isRecording){
//                callHelper.startVideoRecord(PathUtil.getInstance().getVideoPath().getAbsolutePath());
                callHelper.startVideoRecord("/sdcard/");
                EMLog.d(TAG, "startVideoRecord:" + PathUtil.getInstance().getVideoPath().getAbsolutePath());
                isRecording = true;
                recordBtn.setText(R.string.stop_record);
            }else{
                String filepath = callHelper.stopVideoRecord();
                isRecording = false;
                recordBtn.setText(R.string.recording_video);
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.record_finish_toast),
                filepath), Toast.LENGTH_LONG).show();
            }
            break;
        */
            case R.id.root_layout:
//                if (callingState == CallingState.NORMAL) {
//                    if (bottomContainer.getVisibility() == View.VISIBLE) {
//                        bottomContainer.setVisibility(View.GONE);
//                        topContainer.setVisibility(View.GONE);
//                        oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);
//
//                    } else {
//                        bottomContainer.setVisibility(View.VISIBLE);
//                        topContainer.setVisibility(View.VISIBLE);
//                        oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFit);
//                    }
//                }
                break;
            case R.id.btn_switch_camera: //switch camera
                handler.sendEmptyMessage(MSG_CALL_SWITCH_CAMERA);
                break;
            case R.id.btn_capture_image:
                DateFormat df = DateFormat.getDateTimeInstance();
                Date d = new Date();
                final String filename = Environment.getExternalStorageDirectory() + File.separator+ df.format(d) + ".jpg";
                EMClient.getInstance().callManager().getVideoCallHelper().takePicture(filename);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("filename", filename);
                        Toast.makeText(VideoCallActivity.this, "saved image to:" + filename, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
//            case R.id.reply_msg_layout:
//                onClick(refuseBtn);
//                EMMessage message = EMMessage.createTxtSendMessage("有事先挂断", username);
//                EMClient.getInstance().chatManager().sendMessage(message);
//                break;
            case R.id.local_surface:
                changeCallView();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        DemoHelper.getInstance().isVideoCalling = false;
        stopMonitor();
        if (isRecording) {
            callHelper.stopVideoRecord();
            isRecording = false;
        }
        localSurface.getRenderer().dispose();
        localSurface = null;
        oppositeSurface.getRenderer().dispose();
        oppositeSurface = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        callDruationText = chronometer.getText().toString();
        super.onBackPressed();
    }

    /**
     * for debug & testing, you can remove this when release
     */
    void startMonitor() {
        monitor = true;
        new Thread(new Runnable() {
            public void run() {
                while (monitor) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            monitorTextView.setText("WidthxHeight：" + callHelper.getVideoWidth() + "x" + callHelper
                                    .getVideoHeight()
                                    + "\nDelay：" + callHelper.getVideoLatency()
                                    + "\nFramerate：" + callHelper.getVideoFrameRate()
                                    + "\nLost：" + callHelper.getVideoLostRate()
                                    + "\nLocalBitrate：" + callHelper.getLocalBitrate()
                                    + "\nRemoteBitrate：" + callHelper.getRemoteBitrate());

                            ((TextView) findViewById(R.id.tv_is_p2p)).setText(EMClient.getInstance().callManager()
                                    .isDirectCall()
                                    ? R.string.direct_call : R.string.relay_call);
                        }
                    });
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

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (isInCalling) {
            try {
                EMClient.getInstance().callManager().pauseVideoTransfer();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isInCalling) {
            try {
                EMClient.getInstance().callManager().resumeVideoTransfer();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }


    private int surfaceState = 0;
    /**
     * 切换通话界面，这里就是交换本地和远端画面控件设置，以达到通话大小画面的切换
     */
    private void changeCallView() {
        if (surfaceState == 0) {
            surfaceState = 1;
            EMClient.getInstance().callManager().setSurfaceView(oppositeSurface, localSurface);
        } else {
            surfaceState = 0;
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
        }
    }


}
