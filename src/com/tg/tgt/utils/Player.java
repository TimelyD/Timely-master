package com.tg.tgt.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tg.tgt.R;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by DELL on 2018/7/5.
 */

public class Player implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {

    public MediaPlayer mediaPlayer;
    private SeekBar skbProgress;
    private TextView timeText;
    private Timer mTimer = new Timer();
    private String videoUrl;
    private boolean pause;
    private int playPosition;
    private ImageButton playBt;

    private boolean isPlaying;
    private boolean isOut;

    public Player(String videoUrl, SeekBar skbProgress, TextView textView, ImageButton playBt) {
        this.skbProgress = skbProgress;
        this.timeText = textView;
        this.videoUrl = videoUrl;
        this.playBt = playBt;
        isPlaying = false;
        isOut = false;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.e("mediaPlayer", "onCompletion播放完了");
                    isPlaying = false;
                    isOut = true;
                    handleProgress.sendEmptyMessage(1);
                }
            });
        } catch (Exception e) {
            Log.e("mediaPlayer", "error", e);
        }

        mTimer.schedule(mTimerTask, 0, 1000);
    }

    /*******************************************************
     * 通过定时器和Handler来更新进度条
     ******************************************************/
    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (mediaPlayer == null)
                return;
            if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false) {
                handleProgress.sendEmptyMessage(0);
            }
        }
    };

    Handler handleProgress = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                int position = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                if (duration > 0) {
                    long pos = skbProgress.getMax() * position / duration;
                    skbProgress.setProgress((int) pos);
                    timeText.setText(longToString((long) position));
                }
                break;
                case 1:
                    int position1 = mediaPlayer.getCurrentPosition();
                    int duration1 = mediaPlayer.getDuration();
                    if (duration1 > 0) {
                        long pos = skbProgress.getMax() * position1 / duration1;
                        skbProgress.setProgress((int) pos);
                        timeText.setText(longToString((long) position1));
                    }
                    playBt.setBackgroundResource(R.drawable.pauseing);
                    break;
            }
        };
    };

    public boolean isOut() {
        return isOut;
    }

    /**
     * 来电话了
     */
    public void callIsComing() {
        if (mediaPlayer.isPlaying()) {
            playPosition = mediaPlayer.getCurrentPosition();// 获得当前播放位置
            mediaPlayer.stop();
        }
    }


    /**
     * 通话结束
     */
    public void callIsDown() {
        if (playPosition > 0) {
            playNet(playPosition);
            playPosition = 0;
        }
    }

    /**
     * 播放
     */
    public void play() {
        isPlaying = true;
        playNet(0);
        playBt.setBackgroundResource(R.drawable.playing);
    }

    /**
     * 重播
     */
    public void replay() {
        isPlaying = true;
        isOut = false;
        playBt.setBackgroundResource(R.drawable.playing);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);// 从开始位置开始播放音乐
        } else {
            playNet(0);
        }
    }


    public void setPlayerPos(int pos){
        mediaPlayer.seekTo(pos);
        playBt.setBackgroundResource(R.drawable.playing);
    }

    /**
     * 暂停
     */
    public boolean pause() {
        if (mediaPlayer.isPlaying()) {// 如果正在播放
            mediaPlayer.pause();// 暂停
            pause = true;
            playBt.setBackgroundResource(R.drawable.pauseing);
        } else {
            if (pause) {// 如果处于暂停状态
                mediaPlayer.start();// 继续播放
                pause = false;
                playBt.setBackgroundResource(R.drawable.playing);
            }
        }
        return pause;
    }

    /**
     * 停止
     */
    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void setPlaying(boolean flag){
        isPlaying = flag;
    }

    public boolean getPlaying(){
        return isPlaying;
    }

    @Override
    /**
     * 通过onPrepared播放
     */
    public void onPrepared(MediaPlayer arg0) {
        arg0.start();
        Log.e("mediaPlayer", "onPrepared");
        playBt.setBackgroundResource(R.drawable.playing);
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
        skbProgress.setSecondaryProgress(bufferingProgress);
        int currentProgress = skbProgress.getMax()
                * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
        Log.e(currentProgress + "% play", bufferingProgress + "% buffer");
    }

    /**
     * 播放音乐
     *
     * @param playPosition
     */
    private void playNet(int playPosition) {
        try {
            mediaPlayer.reset();// 把各项参数恢复到初始状态
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.prepare();// 进行缓冲
            mediaPlayer.setOnPreparedListener(new MyPreparedListener(
                    playPosition));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private final class MyPreparedListener implements
            android.media.MediaPlayer.OnPreparedListener {
        private int playPosition;

        public MyPreparedListener(int playPosition) {
            this.playPosition = playPosition;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaPlayer.start();// 开始播放
            if (playPosition > 0) {
                mediaPlayer.seekTo(playPosition);
            }
        }

    }

    public static String longToString(Long time){
        SimpleDateFormat format;
        if (time >= 3600*1000)
            format = new SimpleDateFormat("hh:mm:ss");
        else
            format = new SimpleDateFormat("mm:ss");
        String songtime = format.format(time);
        return songtime;
    }

}
