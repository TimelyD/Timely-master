package com.tg.tgt.utils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.tg.tgt.R;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by DELL on 2018/7/5.
 */

public class PlayerVideo implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    public VideoView playVideoView;
    private SeekBar skbProgress;
    private TextView timeText;
    private Timer mTimer = new Timer();
    private String videoUrl;
    private ImageButton playBt;

    private Context mContext;

    private int totalTime;

    private boolean playFlag;

    private MediaMetadataRetriever playModel;
    private MediaController mediaController;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlayerVideo(Context context, String videoUrl, VideoView videoView, SeekBar skbProgress, TextView textView, ImageButton playButton){
        mContext = context;
        this.videoUrl = videoUrl;
        playVideoView = videoView;
        this.skbProgress = skbProgress;
        playBt = playButton;
        timeText = textView;
        initVideo();
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initVideo(){
        playFlag = false;
        playVideoView.setVisibility(View.VISIBLE);
        skbProgress.setMax(100);
        playModel  = new MediaMetadataRetriever();
        mediaController = new MediaController(mContext);
        if (Build.VERSION.SDK_INT >= 14)
            playModel.setDataSource(videoUrl,new HashMap<String, String>());
        else
            playModel.setDataSource(videoUrl);
        String duration = playModel.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 播放时长单位为毫秒
        totalTime = Integer.valueOf(duration);
  //      Log.e("Tag","时长:"+Integer.valueOf(duration)/1000);
        playVideoView.setVideoURI(Uri.parse(videoUrl),new HashMap<String, String>());
        mediaController.setVisibility(View.INVISIBLE);
        playVideoView.setBackground(new BitmapDrawable(playModel.getFrameAtTime()));
        playVideoView.setMediaController(mediaController);
        //  videoView.start();
        playVideoView.setOnCompletionListener(this);
        playVideoView.setOnPreparedListener(this);
        playVideoView.setOnErrorListener(this);

        playVideoView.requestFocus();
    }

    /*******************************************************
     * 通过定时器和Handler来更新进度条
     ******************************************************/
    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (playVideoView == null)
                return;
            if (playVideoView.isPlaying() && skbProgress.isPressed() == false) {
                handleProgress.sendEmptyMessage(0);
            }
        }
    };

    Handler handleProgress = new Handler() {
        public void handleMessage(Message msg) {
            int position = playVideoView.getCurrentPosition();
            if (totalTime > 0) {
                long pos = skbProgress.getMax() * position / totalTime;
                skbProgress.setProgress((int) pos);
                timeText.setText(longToString((long) position));
            }
        };
    };

    public void playVideo(){
        playVideoView.start();
        playBt.setBackgroundResource(R.drawable.playing);
        playVideoView.setBackground(null);
        playFlag = true;
    }

    public void stopVideo(){
        playBt.setBackgroundResource(R.drawable.pauseing);
        playVideoView.pause();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        playBt.setBackgroundResource(R.drawable.pauseing);
        playFlag = false;
    }

    public boolean getPlayStatue(){
        return playFlag;
    }

    public void setPlayFlag(boolean flag){
        playFlag = false;
    }

    public void setPlayingPos(int pos){
        playVideoView.seekTo(pos);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Toast.makeText(mContext, "Error!!!", Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

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
