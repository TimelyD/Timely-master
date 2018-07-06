package com.tg.tgt.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.tg.tgt.R;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.model2.CollectionItemModel;
import com.tg.tgt.utils.Player;
import com.tg.tgt.utils.PlayerVideo;

import com.hyphenate.easeui.GlideApp;

/**
 * Created by DELL on 2018/7/2.
 */

public class CollectionShowActivity extends BaseActivity {

    private EaseTitleBar easeTitleBar;
    private Button deleteBt;
    private Player musicPlayer;
    private PlayerVideo videoPlayer;
    private CollectionItemModel collectionItemModel;

    private VideoView mVideoView;
    private SeekBar mSeekBar;
    private ImageButton playButton;
    private TextView timeText;

    private RelativeLayout playRelative;
    private ImageView userImage;
    private TextView userName;
    private TextView colletionTime;

    private RelativeLayout imageRelative;
    private ImageView imageView;

    private RelativeLayout textRelative;
    private TextView textView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_show);
        initView();
        initData();
    }

    private void initView(){
        easeTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        easeTitleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        deleteBt = (Button)findViewById(R.id.delete_title);
        deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ApiManger2.getApiService()
                            .deleteCollection(String.valueOf(collectionItemModel.getId()))
                            .compose(CollectionShowActivity.this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers(null,false))
                            .subscribe(new BaseObserver2<EmptyData>() {
                                @Override
                                protected void onSuccess(EmptyData emptyData) {
                                    Toast.makeText(CollectionShowActivity.this,"删除成功",Toast.LENGTH_LONG).show();
                                    finish();
                                }

                                @Override
                                public void onFaild(HttpResult<EmptyData> result) {
                                    super.onFaild(result);
                                    Toast.makeText(CollectionShowActivity.this,"删除失败",Toast.LENGTH_LONG).show();
                                }
                            });
            }
        });
        mVideoView = (VideoView)findViewById(R.id.play_video);
        mSeekBar = (SeekBar)findViewById(R.id.progress_seekbar);
        timeText = (TextView)findViewById(R.id.time_text);
        playButton = (ImageButton) findViewById(R.id.play_image);
        playRelative = (RelativeLayout) findViewById(R.id.play_relative);
        userImage = (ImageView) findViewById(R.id.image_user);
        userName = (TextView) findViewById(R.id.nick_name);
        colletionTime = (TextView) findViewById(R.id.collection_time);
        imageRelative = (RelativeLayout)findViewById(R.id.image_relative);
        imageView = (ImageView) findViewById(R.id.image_show);
        textRelative = (RelativeLayout)findViewById(R.id.text_relative);
        textView = (TextView) findViewById(R.id.text_content);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initData(){
        collectionItemModel = (CollectionItemModel) this.getIntent().getExtras().get("model");
        Log.e("Tag","type=="+collectionItemModel.getType());
        switch (collectionItemModel.getType()) {//1:图片 2:视频 3:音频 4:文件 5:文本
            case 1:
                imageRelative.setVisibility(View.VISIBLE);
                playRelative.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(collectionItemModel.getFilePath()))
                    GlideApp.with(mActivity).load(collectionItemModel.getFilePath()).placeholder(R.drawable.default_avatar).into(imageView);
                break;
            case 2:
                mVideoView.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(collectionItemModel.getFilePath())){
                    videoPlayer = new PlayerVideo(CollectionShowActivity.this, collectionItemModel.getFilePath(),mVideoView, mSeekBar,
                            timeText, playButton);
                }
                break;
            case 3:
                if (!TextUtils.isEmpty(collectionItemModel.getFilePath())){
                    musicPlayer = new Player(collectionItemModel.getFilePath(),mSeekBar,
                            timeText, playButton);
                }
                break;
            case 4:
                playRelative.setVisibility(View.GONE);
                break;
            case 5:
                playRelative.setVisibility(View.GONE);
                textRelative.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(collectionItemModel.getContent()))
                    textView.setText(collectionItemModel.getContent());
                break;
        }
        if (!TextUtils.isEmpty(collectionItemModel.getIsFrom()))
            userName.setText(collectionItemModel.getIsFrom());
        if (!TextUtils.isEmpty(collectionItemModel.getCrtTime()))
            colletionTime.setText(collectionItemModel.getCrtTime());
        if (!TextUtils.isEmpty(collectionItemModel.getUserPicture())) {
            RoundedCorners roundedCorners= new RoundedCorners(6);
            RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override(120, 120);
            GlideApp.with(mActivity).load(collectionItemModel.getUserPicture()).apply(options).placeholder(R.drawable.default_avatar).into(userImage);
        }
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (collectionItemModel.getType() == 2){
                    if (videoPlayer != null) {
                        if (videoPlayer.getPlayStatue()) {
                            videoPlayer.setPlayFlag(false);
                            videoPlayer.stopVideo();
                        } else {
                            videoPlayer.setPlayFlag(true);
                            videoPlayer.playVideo();
                        }
                    }
                }
                if (collectionItemModel.getType() == 3){
                    if (musicPlayer != null) {
                        if (musicPlayer.getPlaying()) {
                            // player.setPlaying(false);
                            musicPlayer.pause();
                        } else {
                            musicPlayer.setPlaying(true);
                            musicPlayer.play();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicPlayer != null)
            musicPlayer.stop();
        if (videoPlayer != null)
            videoPlayer.stopVideo();
    }
}
