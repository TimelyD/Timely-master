package com.tg.tgt.conference;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMConferenceListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConference;
import com.hyphenate.chat.EMConferenceStream;
import com.hyphenate.chat.EMStreamParam;
import com.hyphenate.easeui.EaseApp;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.GlideApp;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.L;
import com.hyphenate.easeui.utils.SpUtils;
import com.hyphenate.easeui.widget.CircleImageView;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.util.EMLog;
import com.tg.tgt.BuildConfig;
import com.tg.tgt.Constant;
import com.tg.tgt.DemoHelper;
import com.tg.tgt.R;
import com.tg.tgt.helper.CmdHelper;
import com.tg.tgt.helper.GroupManger;
import com.tg.tgt.http.model2.GroupUserModel;
import com.tg.tgt.ui.BaseActivity;
import com.tg.tgt.utils.CodeUtils;
import com.tg.tgt.utils.ToastUtils;
import com.tg.tgt.widget.MyChronometer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ConferenceActivity extends BaseActivity implements EMConferenceListener, View.OnClickListener {

    private static final int REQUEST_CODE_SELECT_CONFERENCE = 123;
    /** Chronometer */
    private MyChronometer mChronometer;
    private ImageView mIvMute;
    private TextView mTvMute;
    private LinearLayout mBtnMute;
    private ImageView mIvHandsfree;
    private TextView mTvHandsfree;
    private LinearLayout mBtnHandsfree;
    private ImageView mIvCamera;
    private TextView mTvCamera;
    private LinearLayout mBtnVideo;
    private LinearLayout mHangupCallLayout;
    private ImageView mIvSwitchCamera;
    private LinearLayout mBtnSwitchCamera;
    private RelativeLayout mCallControlLayout;
    private CircleImageView mIvAvatar;
    /** 张三 */
    private TextView mTvNick;
    private TextView mTvHint;
    private LinearLayout mAnswerCallLayout;
    private LinearLayout mRefuseCallLayout;
    private RelativeLayout mComingCallLayout;

    protected Ringtone ringtone;

    private int screenWidth;
    public static final String TAG = "ConferenceActivity";
    private List<EMConferenceStream> streamList = new ArrayList<>();
    private AudioManager audioManager;
    private String groupId;
    /**用来控制最多9人*/
    public static final Set<String> allUsers = new HashSet<>();

    public static Handler Handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference);
        DemoHelper.getInstance().pushActivity(this);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        allUsers.clear();
        initView();
        init();
        Handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 102:
                        closeSpeaker();
                        break;
                }
            }
        };
    }

    @Override
    protected void initBar() {
        setTran();
    }

    private void init() {
        isCreator = getIntent().getBooleanExtra(Constant.EXTRA_CONFERENCE_IS_CREATOR, false);
        confId = getIntent().getStringExtra(Constant.EXTRA_CONFERENCE_ID);
        password = getIntent().getStringExtra(Constant.EXTRA_CONFERENCE_PASS);

        param = new EMStreamParam();
        param.setVideoOff(true);
        param.setMute(false);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        initConferenceViewGroup();

            groupId = getIntent().getStringExtra(Constant.GROUP_ID);
        if (isCreator) {
            createAndJoinConference();
        } else {
            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            if (!EaseConstant.isInputHeadset)
                audioManager.setSpeakerphoneOn(true);
            ringtone = RingtoneManager.getRingtone(this, ringUri);
            ringtone.play();
            mTvNick.setText(getIntent().getStringExtra("nickname"));
            GlideApp.with(mActivity).load(getIntent().getStringExtra("pic")).into(mIvAvatar);
            String pics = getIntent().getStringExtra("pics");
            if (!TextUtils.isEmpty(pics)) {
                String[] picsArray = pics.split(",");
                findViewById(R.id.incoming_member_layout).setVisibility(View.VISIBLE);
                LinearLayout first = (LinearLayout) findViewById(R.id.incoming_first_container);
                LinearLayout second = (LinearLayout) findViewById(R.id.incoming_second_container);
                int ivWidth = getResources().getDimensionPixelSize(R.dimen.common_50dp);
                int ivMargin = getResources().getDimensionPixelSize(R.dimen.common_7dp);
                for (int i = 0; i < picsArray.length; i++) {
                    if (i > 7) {
                        //超过8个不处理
                        return;
                    }
                    EaseImageView imageView = new EaseImageView(mActivity);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ivWidth, ivWidth);
                    if (i == 0 || i == 4) {
                        params.leftMargin = 0;
                    } else
                        params.leftMargin = ivMargin;
                    imageView.setLayoutParams(params);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setShapeType(2);
                    GlideApp.with(mActivity).load(picsArray[i]).placeholder(R.color.white).into(imageView);
                    if (i > 3) {
                        second.addView(imageView);
                    } else {
                        first.addView(imageView);
                    }
                }
            }
            Observable.timer(timeout, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(@NonNull Disposable disposable) throws Exception {
                            addRxDestroy(disposable);
                        }
                    })
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(@NonNull Long aLong) throws Exception {
                            if(mComingCallLayout.getVisibility()==View.VISIBLE)
                            onBackPressed();
                        }
                    });
        }
    }

    private ConferenceMemberView localView;
    private EaseViewGroup callConferenceViewGroup;

    private void initView() {
        callConferenceViewGroup = (EaseViewGroup) findViewById(R.id.surface_view_group);
        mChronometer = (MyChronometer) findViewById(R.id.chronometer);
        mIvMute = (ImageView) findViewById(R.id.iv_mute);
        mTvMute = (TextView) findViewById(R.id.tv_mute);
        mBtnMute = (LinearLayout) findViewById(R.id.btn_mute);
        mBtnMute.setOnClickListener(this);
        mIvHandsfree = (ImageView) findViewById(R.id.iv_handsfree);
        mTvHandsfree = (TextView) findViewById(R.id.tv_handsfree);
        mBtnHandsfree = (LinearLayout) findViewById(R.id.btn_handsfree);
        mBtnHandsfree.setOnClickListener(this);
        mIvCamera = (ImageView) findViewById(R.id.iv_camera);
        mTvCamera = (TextView) findViewById(R.id.tv_camera);
        mBtnVideo = (LinearLayout) findViewById(R.id.btn_video);
        mBtnVideo.setOnClickListener(this);
        mHangupCallLayout = (LinearLayout) findViewById(R.id.hangup_call_layout);
        mHangupCallLayout.setOnClickListener(this);
        mIvSwitchCamera = (ImageView) findViewById(R.id.iv_switch_camera);
        mBtnSwitchCamera = (LinearLayout) findViewById(R.id.btn_switch_camera);
        mBtnSwitchCamera.setOnClickListener(this);
        mCallControlLayout = (RelativeLayout) findViewById(R.id.call_control_layout);
        mIvAvatar = (CircleImageView) findViewById(R.id.iv_avatar);
        mTvNick = (TextView) findViewById(R.id.tv_nick);
        mTvHint = (TextView) findViewById(R.id.tv_hint);
        mAnswerCallLayout = (LinearLayout) findViewById(R.id.answer_call_layout);
        mAnswerCallLayout.setOnClickListener(this);
        mRefuseCallLayout = (LinearLayout) findViewById(R.id.refuse_call_layout);
        mRefuseCallLayout.setOnClickListener(this);
        mComingCallLayout = (RelativeLayout) findViewById(R.id.coming_call_layout);
        findViewById(R.id.btn_invite).setOnClickListener(this);
    }

    /**
     * 初始化多人音视频画面管理控件
     */
    private void initConferenceViewGroup() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(0, 0);
        lp.width = screenWidth;
        lp.height = screenWidth;
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        callConferenceViewGroup.setLayoutParams(lp);

        localView = new ConferenceMemberView(mActivity);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(0, 0);
        params.width = screenWidth;
        params.height = screenWidth;
        localView.setLayoutParams(params);
        localView.updateVideoState(param.isVideoOff());
        localView.updateMuteState(param.isMute());
        localView.setPubOrSub(false);
        callConferenceViewGroup.addView(localView);

        mBtnMute.setAlpha(getAlpha(param.isMute()));
        mBtnVideo.setAlpha(getAlpha(!param.isVideoOff()));
        mBtnHandsfree.setAlpha(getAlpha(true));
        if (!EaseConstant.isInputHeadset)
            openSpeaker();
        else
            closeSpeaker();
        localView.setUser(EMClient.getInstance().getCurrentUser());
        EMClient.getInstance().conferenceManager().setLocalSurfaceView(localView.getSurfaceView());
//        localView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (localView.isPubOrSub()) {
//                    unpublish();
//                } else {
//                    publish();
//                }
//            }
//        });
    }

    private void startChronometer() {
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
    }

    /**
     * 开始推自己的数据
     */
    private void publish() {
        EMClient.getInstance().conferenceManager().publish(param, new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                conference.setPubStreamId(value);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        localView.setPubOrSub(true);
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                L.e(TAG, "publish failed: error=" + error + ", msg=" + errorMsg);
            }
        });
    }

    /**
     * 停止推自己的数据
     */
    private void unpublish() {
        EMClient.getInstance().conferenceManager().unpublish(conference.getPubStreamId(), new EMValueCallBack<String>
                () {
            @Override
            public void onSuccess(String value) {
                conference.setPubStreamId(value);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        localView.setPubOrSub(false);
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                L.e(TAG, "unpublish failed: error=" + error + ", msg=" + errorMsg);
            }
        });
    }

    /**
     * 作为成员直接根据 confId 和 password 加入会议
     */
    private void joinConference() {
        mComingCallLayout.setVisibility(View.GONE);
        EMClient.getInstance().conferenceManager().joinConference(confId, password, param, new
                EMValueCallBack<EMConference>() {
            @Override
            public void onSuccess(EMConference value) {
                conference = value;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCallControlLayout.setVisibility(View.VISIBLE);
                        localView.setPubOrSub(true);
                        startChronometer();
                    }
                });
            }

            @Override
            public void onError(int error, final String errorMsg) {
                EMLog.e(TAG, "join conference failed error " + error + ", msg " + errorMsg);
                //TODO 这里需要处理，当会议没有人时
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
            }
        });
    }

    private int timeout = 30000;
    private void inviteUser(final String[] members) {
        try {
            JSONObject object = new JSONObject();
            int type = 0;
//            if (!cameraSwitch.isActivated()) {
//                type = 1;
//            }
            EaseUser myInfo = EaseUserUtils.getUserInfo(EMClient.getInstance().getCurrentUser());
            object.put("type", type);
            object.put("creater", EMClient.getInstance().getCurrentUser());
            object.put("groupId", groupId);
            object.put("nickname", myInfo.getNick());
            object.put("pic", myInfo.getAvatar());
            List<String> memberList = EMClient.getInstance().conferenceManager().getConferenceMemberList();
//            memberList.add(GroupManger.getGroup(groupId).getGroupUserModels().get(0).getUsername());
//            memberList.add(GroupManger.getGroup(groupId).getGroupUserModels().get(0).getUsername());
            Map<String, GroupUserModel> groupUsers = GroupManger.getGroupUsers(groupId);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < memberList.size(); i++) {
                String picture = groupUsers.get(memberList.get(i)).getPicture();
                if (i > 0)
                    sb.append(",");
                if(picture == null) {
                    sb.append("nopic");
                }else {
                    sb.append(picture);
                }
            }
            for (String m : members) {
                String picture = groupUsers.get(m).getPicture();
                if(!TextUtils.isEmpty(sb.toString()))
                    sb.append(",");
                if(picture == null){
                    sb.append("nopic");
                }else {
                    sb.append(picture);
                }
            }
            object.put("pics", sb.toString());
            Log.e("ZWW","members.length"+members.length);
            for (int i = 0; i < members.length; i++) {
                final int finalI = i;
                allUsers.add(members[i]);
                EMClient.getInstance()
                        .conferenceManager()
                        .inviteUserToJoinConference(conference.getConferenceId(), conference.getPassword(), members[i],
                                object.toString(), new EMValueCallBack() {
                                    @Override
                                    public void onSuccess(Object value) {
                                        EMLog.e(TAG, "invite join conference success");
                                        ToastUtils.safeShowToast(mContext, String.format("邀请%s成功",EaseApp.nick.get(finalI)));
                                        Log.e("ZWW","members邀请成功"+members[finalI]);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Observable.timer(timeout, TimeUnit.MILLISECONDS)
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .doOnSubscribe(new Consumer<Disposable>() {
                                                            @Override
                                                            public void accept(@NonNull Disposable disposable) throws Exception {
                                                                addRxDestroy(disposable);
                                                            }
                                                        })
                                                        .subscribe(new Consumer<Long>() {
                                                            @Override
                                                            public void accept(@NonNull Long aLong) throws Exception {
                                                                for (EMConferenceStream s : streamList) {
                                                                    //超时后如果已经加入了绘画，则不做判断
                                                                    Log.e("ZWW","members加入了绘画");
                                                                	if(s.getUsername().equals(members[finalI]))
                                                                	    return;
                                                                }
                                                                allUsers.remove(members[finalI]);
                                                                ToastUtils.safeShowToast(mContext, String.format
                                                                        ("邀请%s超时",EaseApp.nick.get(finalI)));

                                                                exitIfNobody();
                                                            }
                                                        });
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(int error, String errorMsg) {
                                        allUsers.remove(members[finalI]);
                                        EMLog.e(TAG, "invite join conference failed " + error + ", " + errorMsg);
                                        ToastUtils.safeShowGeneralToast(mContext, String.format("邀请%s失败:%s",
                                                EaseApp.nick.get(finalI), errorMsg));
                                    }
                                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void exitIfNobody() {
        if (allUsers.size() < 1 && streamList.size()<1) {
            //如果在邀请人，也不退出
            try {
                if(!EaseUI.getInstance().getTopActivity().getClass().getSimpleName().equals("ConferenceInviteJoinActivity"))
                onBackPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加一个展示远端画面的 view
     */
    private void addConferenceView(EMConferenceStream stream) {
        final ConferenceMemberView memberView = new ConferenceMemberView(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(0, 0);
        params.width = screenWidth / 3;
        params.height = screenWidth / 3;
        memberView.setLayoutParams(params);
        callConferenceViewGroup.addView(memberView);
//        memberView.setUser(stream.getUsername());
        if (GroupManger.getGroupUsers(groupId) != null)
            memberView.setGroupUser(GroupManger.getGroupUsers(groupId).get(stream.getUsername()));
        else
            Log.e("ZWW","添加窗口成功失败");
        memberView.setPubOrSub(false);
        Log.e("ZWW","添加窗口成功");
        //设置 view 点击监听
        /*memberView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = callConferenceViewGroup.indexOfChild(view);
                final EMConferenceStream stream = streamList.get(index - 1);
                if (memberView.isPubOrSub()) {
                    unsubscribe(stream, memberView);
                } else {
                    subscribe(stream, memberView);
                }
            }
        });*/
    }

    /**
     * 移除指定位置的 View，移除时如果已经订阅需要取消订阅
     */
    private void removeConferenceView(EMConferenceStream stream) {
        int index = streamList.indexOf(stream);
        final ConferenceMemberView memberView = (ConferenceMemberView) callConferenceViewGroup.getChildAt(index + 1);
        streamList.remove(stream);
        callConferenceViewGroup.removeView(memberView);
    }

    /**
     * 更新指定 View
     */
    private void updateConferenceMemberView(EMConferenceStream stream) {
        int position = streamList.indexOf(stream);
        ConferenceMemberView conferenceMemberView = (ConferenceMemberView) callConferenceViewGroup.getChildAt
                (position + 1);
        conferenceMemberView.updateMuteState(stream.isAudioOff());
        conferenceMemberView.updateVideoState(stream.isVideoOff());
    }

    /**
     * 更新指定 View
     */
    private void updateConferenceMemberView(EMConferenceStream stream, boolean autoSub) {
        int position = streamList.indexOf(stream);
        ConferenceMemberView conferenceMemberView = (ConferenceMemberView) callConferenceViewGroup.getChildAt
                (position + 1);
        conferenceMemberView.updateMuteState(stream.isAudioOff());
        conferenceMemberView.updateVideoState(stream.isVideoOff());
        if (autoSub)
            subscribe(stream, conferenceMemberView);
    }

    /**
     * 更新所有 Member view
     */
    private void updateConferenceViewGroup() {
        int memberViewSize;
        if (streamList.size() > 8) {
            memberViewSize = screenWidth / 4;
        } else if (streamList.size() > 3) {
            memberViewSize = screenWidth / 3;
        } else if (streamList.size() >= 1) {
            memberViewSize = screenWidth / 2;
        } else {
            memberViewSize = screenWidth;
        }
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(0, 0);
        lp.width = memberViewSize;
        lp.height = memberViewSize;
        for (int i = 0; i < callConferenceViewGroup.getChildCount(); i++) {
            ConferenceMemberView view = (ConferenceMemberView) callConferenceViewGroup.getChildAt(i);
            view.setLayoutParams(lp);
        }
    }

    /**
     * 订阅指定成员 stream
     */
    private void subscribe(EMConferenceStream stream, final ConferenceMemberView memberView) {
        EMClient.getInstance().conferenceManager().subscribe(stream, memberView.getSurfaceView(), new
                EMValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        memberView.setPubOrSub(true);
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    /**
     * 取消订阅指定成员 stream
     */
    private void unsubscribe(EMConferenceStream stream, final ConferenceMemberView memberView) {
        EMClient.getInstance().conferenceManager().unsubscribe(stream, new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        memberView.setPubOrSub(false);
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    private boolean isCreator = false;
    private String confId = "";
    private String password = "";
    private EMStreamParam param;

    private EMConference conference;

    /**
     * 作为创建者创建并加入会议
     */
    private void createAndJoinConference() {
        showProgress(R.string.loading_msg);
        mProgressDialog.setCancelable(false);
        mComingCallLayout.setVisibility(View.GONE);
        mCallControlLayout.setVisibility(View.VISIBLE);
        EMClient.getInstance().conferenceManager().createAndJoinConference(password, param, new
                EMValueCallBack<EMConference>() {
                    @Override
                    public void onSuccess(EMConference value) {
                        confId = value.getConferenceId();
                        conference = value;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissProgress();
                                localView.setPubOrSub(true);
                                inviteUser(getIntent().getStringArrayExtra(Constant.MEMBERS));
                                startChronometer();
                                stopRingTone();
                                if (getIntent().getBooleanExtra(Constant.EXTRA_CONFERENCE_IS_VIDEO, false)) {
                                    videoSwitch();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        dismissProgress();
                        Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        L.e(TAG, "create and join conference failed error " + error + ", msg " + errorMsg);
                    }
                });
    }

    /**
     * 打开扬声器
     * 主要是通过扬声器的开关以及设置音频播放模式来实现
     * 1、MODE_NORMAL：是正常模式，一般用于外放音频
     * 2、MODE_IN_CALL：
     * 3、MODE_IN_COMMUNICATION：这个和 CALL 都表示通讯模式，不过 CALL 在华为上不好使，故使用 COMMUNICATION
     * 4、MODE_RINGTONE：铃声模式
     */
    public void openSpeaker() {
        // 检查是否已经开启扬声器
        if (!audioManager.isSpeakerphoneOn()) {
            // 打开扬声器
            audioManager.setSpeakerphoneOn(true);
        }
        // 开启了扬声器之后，因为是进行通话，声音的模式也要设置成通讯模式
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        mBtnHandsfree.setAlpha(getAlpha(true));
    }

    /**
     * 关闭扬声器，即开启听筒播放模式
     * 更多内容看{@link #openSpeaker()}
     */
    public void closeSpeaker() {
        // 检查是否已经开启扬声器
        if (audioManager.isSpeakerphoneOn()) {
            // 关闭扬声器
            audioManager.setSpeakerphoneOn(false);
        }
        // 设置声音模式为通讯模式
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        mBtnHandsfree.setAlpha(getAlpha(false));
    }

    /**
     * 语音开关
     */
    private void voiceSwitch() {
        if (param.isMute()) {
            param.setMute(false);
            EMClient.getInstance().conferenceManager().openVoiceTransfer();
        } else {
            param.setMute(true);
            EMClient.getInstance().conferenceManager().closeVoiceTransfer();
        }
        mBtnMute.setAlpha(getAlpha(param.isMute()));
        localView.updateMuteState(param.isMute());
    }

    /**
     * 视频开关
     */
    private void videoSwitch() {
        if (param.isVideoOff()) {
            param.setVideoOff(false);
            EMClient.getInstance().conferenceManager().openVideoTransfer();
        } else {
            param.setVideoOff(true);
            EMClient.getInstance().conferenceManager().closeVideoTransfer();
        }
        mBtnVideo.setAlpha(getAlpha(!param.isVideoOff()));
        localView.updateVideoState(param.isVideoOff());
    }

    public float getAlpha(boolean flag) {
        return flag ? 1 : .5f;
    }

    /**
     * 切换摄像头
     */
    private void changeCamera() {
//        if (EMClient.getInstance().conferenceManager().getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//            changeCameraSwitch.setImageResource(R.drawable.ic_camera_rear_white_24dp);
//        } else {
//            changeCameraSwitch.setImageResource(R.drawable.ic_camera_front_white_24dp);
//        }
        EMClient.getInstance().conferenceManager().switchCamera();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EMClient.getInstance().conferenceManager().addConferenceListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EMClient.getInstance().conferenceManager().removeConferenceListener(this);
    }

    @Override
    protected void onDestroy() {
        if (!TextUtils.isEmpty(confId)) {
            for (String username : allUsers) {
                CmdHelper.notifyCancel(username, confId);
            }
        }
        allUsers.clear();
        stopRingTone();
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setMicrophoneMute(false);
        mChronometer.stop();
        DemoHelper.getInstance().popActivity(this);
        super.onDestroy();
        exitConference();
    }

    private void stopRingTone() {
        if (ringtone != null && ringtone.isPlaying())
            ringtone.stop();
    }

    /**
     * 退出会议
     */
    private void exitConference() {
        EMClient.getInstance().conferenceManager().exitConference(new EMValueCallBack() {
            @Override
            public void onSuccess(Object value) {
                finish();
            }

            @Override
            public void onError(int error, String errorMsg) {
                L.e("exit conference failed " + error + ", " + errorMsg);
                finish();
            }
        });
    }

    /**
     * --------------------------------------------------------------------
     * 多人音视频会议回调方法
     */
    private String nickname;
    @Override
    public void onMemberJoined(final String username) {
        allUsers.add(username);
        nickname = username;
        for(EaseUser nick:EaseApp.mAlluserList){
            if(nick.getUsername().equals(username)){
                nickname=nick.getNick();
            }
        }
        if (BuildConfig.DEBUG)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext,nickname + " 加入通话!", Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public void onMemberExited(final String username) {
        allUsers.remove(username);
        nickname = username;
        for(EaseUser nick:EaseApp.mAlluserList){
            if(nick.getUsername().equals(username)){
                nickname=nick.getNick();
            }
        }
        if (BuildConfig.DEBUG)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, nickname + " 取消通话!", Toast.LENGTH_SHORT).show();
                    exitIfNobody();
                }
            });
    }

    @Override
    public void onStreamAdded(final EMConferenceStream stream) {
        if (streamList.size() > Constant.MEMBERS_SIZE) {
            //最多加入9个steam
            return;
        }
        nickname =  stream.getUsername();
        for(EaseUser nick:EaseApp.mAlluserList){
            if(nick.getUsername().equals( stream.getUsername())){
                nickname=nick.getNick();
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(BuildConfig.DEBUG)
                Toast.makeText(mContext,nickname + "加入!", Toast.LENGTH_SHORT).show();
                streamList.add(stream);
                addConferenceView(stream);
                updateConferenceMemberView(stream, true);
                updateConferenceViewGroup();
            }
        });
    }

    @Override
    public void onStreamRemoved(final EMConferenceStream stream) {
        nickname =  stream.getUsername();
        for(EaseUser nick:EaseApp.mAlluserList){
            if(nick.getUsername().equals( stream.getUsername())){
                nickname=nick.getNick();
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(BuildConfig.DEBUG)
                Toast.makeText(mContext,nickname + " 退出!", Toast.LENGTH_SHORT).show();
                if (streamList.contains(stream)) {
                    removeConferenceView(stream);
                    updateConferenceViewGroup();
                }
            }
        });
    }

    @Override
    public void onStreamUpdate(final EMConferenceStream stream) {
        nickname =  stream.getUsername();
        for(EaseUser nick:EaseApp.mAlluserList){
            if(nick.getUsername().equals( stream.getUsername())){
                nickname=nick.getNick();
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(BuildConfig.DEBUG)
                Toast.makeText(mContext,nickname + " stream update!", Toast.LENGTH_SHORT).show();
                updateConferenceMemberView(stream);
            }
        });
    }

    @Override
    public void onPassiveLeave(final int error, final String message) {
        if (BuildConfig.DEBUG)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Passive exit " + error + ", message" + message, Toast.LENGTH_SHORT)
                            .show();
                }
            });
    }

    @Override
    public void onConferenceState(final ConferenceState state) {
        if (BuildConfig.DEBUG)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "State=" + state, Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public void onStreamSetup(final String streamId) {
        if (BuildConfig.DEBUG)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (streamId.indexOf(conference.getPubStreamId()) != -1) {
                        conference.setPubStreamId(streamId);
                        //Toast.makeText(mContext, "Publish setup streamId=" + streamId, Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(mContext, "Subscribe setup streamId=" + streamId, Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    /**
     * 收到其他人的会议邀请
     *
     * @param confId 会议 id
     * @param password 会议密码
     * @param extension 邀请扩展内容
     */
    @Override
    public void onReceiveInvite(final String confId, String password, String extension) {
        if (BuildConfig.DEBUG)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Receive invite " + confId, Toast.LENGTH_LONG).show();
                }
            });
    }

    /**对方发出邀请并取消时接收到回调*/
    public void userCancelInvite(final String from, String confId) {
        if(!confId.equals(this.confId))
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                allUsers.remove(from);
                if (mComingCallLayout.getVisibility() == View.VISIBLE) {
                    if (GroupManger.getGroupUsers(groupId) == null) {
                        //对方取消来电，来电状态下挂断
                        Toast.makeText(mContext, "已取消通话", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(mContext, String.format("%s已取消通话", GroupManger.getGroupUsers(groupId).get(from)
                                        .getNickname()),
                                Toast.LENGTH_SHORT).show();
                    }
                    if (allUsers.size() < 1)
                        onBackPressed();
                }
            }
        });
    }

    /**我发出邀请对方拒绝*/
    public void userRejectInvite(final String from, String confId) {
        if(!confId.equals(this.confId))
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, String.format("%s拒绝加入", GroupManger.getGroupUsers(groupId).get(from)
                        .getNickname()), Toast.LENGTH_SHORT).show();
                allUsers.remove(from);
                exitIfNobody();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_invite:
                startActivityForResult(new Intent(mActivity, ConferenceInviteJoinActivity.class)
                        .putExtra(Constant.GROUP_ID, groupId), REQUEST_CODE_SELECT_CONFERENCE);
                break;
            case R.id.btn_mute:
                voiceSwitch();
                break;
            case R.id.btn_handsfree:
                if (audioManager.isSpeakerphoneOn() || EaseConstant.isInputHeadset) {
                    closeSpeaker();
                } else {
                    openSpeaker();
                }
                break;
            case R.id.btn_video:
                videoSwitch();
                break;
            case R.id.hangup_call_layout:
                exitConference();
                break;
            case R.id.btn_switch_camera:
                changeCamera();
                break;
            case R.id.answer_call_layout:
                stopRingTone();
                joinConference();
                break;
            case R.id.refuse_call_layout:
                CmdHelper.notifyReject(getIntent().getStringExtra("creater"), confId);
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK != resultCode)
            return;
        switch (requestCode) {
            case REQUEST_CODE_SELECT_CONFERENCE:
                String[] newmemberses = data.getStringArrayExtra(Constant.MEMBERS);
                inviteUser(newmemberses);
                break;

            default:
                break;
        }
    }

}
