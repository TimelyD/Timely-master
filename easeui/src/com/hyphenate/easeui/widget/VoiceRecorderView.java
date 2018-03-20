package com.hyphenate.easeui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMError;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.EaseVoiceRecorder;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.TimeUtils;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowVoicePlayClickListener;

/**
 * Voice recorder view
 *
 */
public class VoiceRecorderView extends RelativeLayout {
    protected Context context;
    protected LayoutInflater inflater;
    protected Drawable[] micImages;
    protected EaseVoiceRecorder voiceRecorder;

    protected PowerManager.WakeLock wakeLock;
//    protected ImageView micImage;
    protected TextView recordingHint;

    protected Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // change image
            //TODO 这里是用来控制显示声音大小的
//            micImage.setImageDrawable(micImages[msg.what]);
        }
    };
    private TextView mTimerTv;
    private ImageView mSendVoiceIv;

    private EaseVoiceRecorderCallback callBack;
    private ImageView mRightWaveIv;
    private ImageView mLeftWaveIv;
    //    private AnimationDrawable mRightAnim;

    public void setCallback(EaseVoiceRecorderCallback callBack){
        this.callBack = callBack;
    }

    public VoiceRecorderView(Context context) {
        super(context);
        init(context);
    }

    public VoiceRecorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VoiceRecorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_send_voice_menu, this);

//        micImage = (ImageView) findViewById(R.id.mic_image);
        recordingHint = (TextView) findViewById(R.id.recording_hint);

        mTimerTv = (TextView)findViewById(R.id.timer_tv);
        mSendVoiceIv = (ImageView) findViewById(R.id.send_voice_iv);
        mRightWaveIv = (ImageView) findViewById(R.id.right_wave_iv);
        mLeftWaveIv = (ImageView) findViewById(R.id.left_wave_iv);

        mSendVoiceIv.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onPressToSpeakBtnTouch(v, event, callBack);
            }
        });

        voiceRecorder = new EaseVoiceRecorder(micImageHandler);

        // animation resources, used for recording
//        micImages = new Drawable[] { getResources().getDrawable(R.drawable.ease_record_animate_01),
//                getResources().getDrawable(R.drawable.ease_record_animate_02),
//                getResources().getDrawable(R.drawable.ease_record_animate_03),
//                getResources().getDrawable(R.drawable.ease_record_animate_04),
//                getResources().getDrawable(R.drawable.ease_record_animate_05),
//                getResources().getDrawable(R.drawable.ease_record_animate_06),
//                getResources().getDrawable(R.drawable.ease_record_animate_07),
//                getResources().getDrawable(R.drawable.ease_record_animate_08),
//                getResources().getDrawable(R.drawable.ease_record_animate_09),
//                getResources().getDrawable(R.drawable.ease_record_animate_10),
//                getResources().getDrawable(R.drawable.ease_record_animate_11),
//                getResources().getDrawable(R.drawable.ease_record_animate_12),
//                getResources().getDrawable(R.drawable.ease_record_animate_13),
//                getResources().getDrawable(R.drawable.ease_record_animate_14), };

        wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
    }

    /**
     * on speak button touched
     * 
     * @param v
     * @param event
     */
    public boolean onPressToSpeakBtnTouch(View v, MotionEvent event, EaseVoiceRecorderCallback recorderCallback) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            try {
                if (EaseChatRowVoicePlayClickListener.isPlaying)
                    EaseChatRowVoicePlayClickListener.currentPlayListener.stopPlayVoice();
                v.setPressed(true);
                startRecording();

                startCount();
            } catch (Exception e) {
                v.setPressed(false);
                stopCount();
            }
            return true;
        case MotionEvent.ACTION_MOVE:
            if (event.getY() < 0) {
                showReleaseToCancelHint();
            } else {
                showMoveUpToCancelHint();
            }
            return true;
        case MotionEvent.ACTION_UP:
            v.setPressed(false);
            stopCount();
            if (event.getY() < 0) {
                // discard the recorded audio.
                discardRecording();
            } else {
                // stop recording and send voice file
                try {
                    int length = stopRecoding();
                    if (length > 0) {
                        if (recorderCallback != null) {
                            recorderCallback.onVoiceRecordComplete(getVoiceFilePath(), length);
                        }
                    } else if (length == EMError.FILE_INVALID) {
                        Toast.makeText(context, R.string.Recording_without_permission, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, R.string.The_recording_time_is_too_short, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, R.string.send_failure_please, Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        default:
            discardRecording();
            return false;
        }
    }

    long startTime;
    private Handler countHandler = new Handler();
    //每隔一秒计时
    private int countTime  = 1000;
    /**
     * 开始计时
     */
    private void startCount() {
        startTime = System.currentTimeMillis();
        mTimerTv.setText("0:00");
        startWaveAnim();
        showMoveUpToCancelHint();
        countHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String time = TimeUtils.getTime(System.currentTimeMillis() - startTime);
                mTimerTv.setText(time);
                countHandler.postDelayed(this, countTime);
            }
        }, countTime);
    }

    private void stopCount(){
        countHandler.removeCallbacksAndMessages(null);
        mTimerTv.setText("0:00");
        stopWaveAnim();
        recordingHint.setText(R.string.button_pushtotalk);
        recordingHint.setBackgroundColor(Color.TRANSPARENT);

    }

    /**
     * 停止波动动画
     */
    private void stopWaveAnim() {
        ((AnimationDrawable) mLeftWaveIv.getDrawable()).stop();
        mLeftWaveIv.setImageResource(R.anim.left_wave);

        ((AnimationDrawable) mRightWaveIv.getDrawable()).stop();
        mRightWaveIv.setImageResource(R.anim.right_wave);


    }

    /**
     * 开始波动动画
     */
    private void startWaveAnim() {
        ((AnimationDrawable) mRightWaveIv.getDrawable()).start();
        ((AnimationDrawable) mLeftWaveIv.getDrawable()).start();
    }


    public interface EaseVoiceRecorderCallback {
        /**
         * on voice record complete
         * 
         * @param voiceFilePath
         *            录音完毕后的文件路径
         * @param voiceTimeLength
         *            录音时长
         */
        void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength);
    }

    public void startRecording() {
        if (!EaseCommonUtils.isSdcardExist()) {
            Toast.makeText(context, R.string.Send_voice_need_sdcard_support, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            wakeLock.acquire();
            recordingHint.setText(context.getString(R.string.move_up_to_cancel));
            recordingHint.setBackgroundColor(Color.TRANSPARENT);
            voiceRecorder.startRecording(context);
        } catch (Exception e) {
            e.printStackTrace();
            if (wakeLock.isHeld())
                wakeLock.release();
            if (voiceRecorder != null)
                voiceRecorder.discardRecording();
            Toast.makeText(context, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void showReleaseToCancelHint() {
        recordingHint.setText(context.getString(R.string.release_to_cancel));
        recordingHint.setBackgroundResource(R.drawable.ease_recording_text_hint_bg);
    }

    public void showMoveUpToCancelHint() {
        recordingHint.setText(context.getString(R.string.move_up_to_cancel));
        recordingHint.setBackgroundColor(Color.TRANSPARENT);
    }

    public void discardRecording() {
        if (wakeLock.isHeld())
            wakeLock.release();
        try {
            // stop recording
            if (voiceRecorder.isRecording()) {
                voiceRecorder.discardRecording();
            }
        } catch (Exception e) {
        }
    }

    public int stopRecoding() {
        if (wakeLock.isHeld())
            wakeLock.release();
        return voiceRecorder.stopRecoding();
    }

    public String getVoiceFilePath() {
        return voiceRecorder.getVoiceFilePath();
    }

    public String getVoiceFileName() {
        return voiceRecorder.getVoiceFileName();
    }

    public boolean isRecording() {
        return voiceRecorder.isRecording();
    }

}
