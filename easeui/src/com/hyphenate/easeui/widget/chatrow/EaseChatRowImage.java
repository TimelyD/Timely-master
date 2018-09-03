package com.hyphenate.easeui.widget.chatrow;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.GlideApp;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.EaseImageCache;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.ui.EaseShowBigImageActivity;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.hyphenate.easeui.utils.GlideRoundTransform;
import com.hyphenate.easeui.utils.photo.MediaBean;
import com.hyphenate.easeui.utils.photo.PhotoBean;
import com.hyphenate.easeui.widget.ZQImageViewRoundOval;
import com.hyphenate.easeui.widget.photoselect.MsgImageActivity;
import com.hyphenate.easeui.widget.photoselect.PreviewImageActivity;
import com.hyphenate.easeui.widget.photoselect.SelectObserable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

public class EaseChatRowImage extends EaseChatRowFile{
    public static final int REQUEST_SELECT = 123;
    protected ZQImageViewRoundOval imageView;
    private EMImageMessageBody imgBody;
    private CheckBox select;
    private View bt;

    public EaseChatRowImage(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_picture : R.layout.ease_row_sent_picture, this);
    }

    @Override
    protected void onFindViewById() {
        percentageView = (TextView) findViewById(R.id.percentage);
        imageView = (ZQImageViewRoundOval) findViewById(R.id.image);
        imageView.setType(ZQImageViewRoundOval.TYPE_ROUND);imageView.setRoundRadius(10);
        select= (CheckBox) findViewById(R.id.select);
        bt= findViewById(R.id.bt);
    }

    
    @Override
    protected void onSetUpView() {
        imgBody = (EMImageMessageBody) message.getBody();
        // received messages
        Log.i("dcz_MESAGE2", EaseConstant.MESSAGE_ATTR_SELECT+"q");

        select.setVisibility(EaseConstant.MESSAGE_ATTR_SELECT==true?VISIBLE:GONE);
        bt.setVisibility(EaseConstant.MESSAGE_ATTR_SELECT==true?VISIBLE:GONE);
        select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("dcz_id",message.getMsgId()+"qqq");
                if(isChecked==true){
                    if(!EaseConstant.list_ms.contains(message.getMsgId())){
                        EaseConstant.list_ms.add(message.getMsgId());
                    }
                }else {
                    if(EaseConstant.list_ms.contains(message.getMsgId())){
                        EaseConstant.list_ms.remove(message.getMsgId());
                    }
                }
                Log.i("dcz_check",EaseConstant.list_ms+"");
            }
        });
        if(select.getVisibility()==VISIBLE){
            select.setChecked(EaseConstant.list_ms.contains(message.getMsgId())?true:false);
        }
        bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("dcz","点击了");
                if(select.getVisibility()==VISIBLE){
                    select.setChecked(select.isChecked()?false:true);
                }
            }
        });

        if (message.direct() == EMMessage.Direct.RECEIVE) {
            if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                imageView.setImageResource(R.drawable.ease_default_image);
                setMessageReceiveCallback();
            } else {
                progressBar.setVisibility(GONE);
                percentageView.setVisibility(GONE);
                imageView.setImageResource(R.drawable.ease_default_image);
                String thumbPath = imgBody.thumbnailLocalPath();
                if (!new File(thumbPath).exists()) {
                	// to make it compatible with thumbnail received in previous version
                    thumbPath = EaseImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
                }
                Log.i("xxx2",thumbPath);
                showImageView(thumbPath, imageView, imgBody.getLocalUrl(), message);
            }
            return;
        }
        
        String filePath = imgBody.getLocalUrl();
        String thumbPath = EaseImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
        Log.i("xxx2",filePath+"+"+thumbPath);
        showImageView(thumbPath, imageView, filePath, message);
        handleSendMessage();
    }
    
    @Override
    protected void onUpdateView() {
        super.onUpdateView();
    }
    
    @Override
    protected void onBubbleClick() {
        int postion=0;
        List<MediaBean> beans = new ArrayList<MediaBean>();
        List<EMMessage> msg = EaseChatFragment.conversation.getAllMessages();
        for (EMMessage message:msg) {
            if(message.getType()== EMMessage.Type.IMAGE){
                EMImageMessageBody body = (EMImageMessageBody)message.getBody();
                PhotoBean e = new PhotoBean(body.getLocalUrl());
                e.setMsg_id(message.getMsgId());
                e.setThumbnail(EaseImageUtils.getThumbnailImagePath(body.getLocalUrl()));
                beans.add(e);
                if(body.getLocalUrl().equals(imgBody.getLocalUrl())){
                    postion=beans.size()-1;
                }
            }
        }
        SelectObserable.getInstance().setFolderAllImages(beans);
        MsgImageActivity.startPreviewPhotoActivity(this.getContext(),postion,imageView,false);
/*        Intent intent = new Intent(context, EaseShowBigImageActivity.class);
        File file = new File(imgBody.getLocalUrl());
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            intent.putExtra("uri", uri);
        } else {
            // The local full size pic does not exist yet.
            // ShowBigImage needs to download it from the server
            // first
            String msgId = message.getMsgId();
            intent.putExtra("messageId", msgId);
            intent.putExtra("localUrl", imgBody.getLocalUrl());
        }
        if (message != null && message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked()
                && message.getChatType() == ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        context.startActivity(intent);*/
    }

    public Bitmap setImgSize(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高.
        int width = bm.getWidth();Log.i("宽：",width+"");
        int height = bm.getHeight();Log.i("高：",height+"");
        // 计算缩放比例.
       /* float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;*/
        float scaleWidth = 0;
        float scaleHeight =0;
        float bi = (float)width / (float)height;Log.i("比例1：",bi+"");
        float li = 50 / bi;Log.i("比例2：",li+"");
       if(width==height){
           scaleWidth = ((float)width+50) / width;
           scaleHeight = ((float)height+50) / height;
       }else {
           scaleWidth = ((float)width+50) / width;
           scaleHeight = ((float)height+li) / height;
       }
        // 取得想要缩放的matrix参数.
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片.
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }
    
    /**
     * load image into image view
     * 
     * @param thumbernailPath
     * @param iv
     * @param position
     * @return the image exists or not
     */
    private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath,final EMMessage message) {
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = EaseImageCache.getInstance().get(thumbernailPath);
        Log.i("dcz","显示");
        if (bitmap != null) {
            //bitmap=setImgSize(bitmap,500,500);
            iv.setImageBitmap(bitmap);
            /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bytes=baos.toByteArray();
            GlideApp.with(this).load(bytes).transform(new GlideRoundTransform(context,10)).into(iv);*/
            return true;
        } else {
            new AsyncTask<Object, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(Object... args) {
                    File file = new File(thumbernailPath);
                    if (file.exists()) {
                        return EaseImageUtils.decodeScaleImage(thumbernailPath, 160, 160);
                    } else if (new File(imgBody.thumbnailLocalPath()).exists()) {
                        return EaseImageUtils.decodeScaleImage(imgBody.thumbnailLocalPath(), 160, 160);
                    }
                    else {
                        if (message.direct() == EMMessage.Direct.SEND) {
                            if (localFullSizePath != null && new File(localFullSizePath).exists()) {
                                return EaseImageUtils.decodeScaleImage(localFullSizePath, 160, 160);
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }
                }

                protected void onPostExecute(Bitmap image) {
                    if (image != null) {
                        //image=setImgSize(image,500,500);
                        iv.setImageBitmap(image);
                        /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] bytes=baos.toByteArray();
                        GlideApp.with(activity).load(bytes).transform(new GlideRoundTransform(context,10)).into(iv);*/

                        EaseImageCache.getInstance().put(thumbernailPath, image);
                    } else {
                        if (message.status() == EMMessage.Status.FAIL) {
                            if (EaseCommonUtils.isNetWorkConnected(activity)) {
                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        EMClient.getInstance().chatManager().downloadThumbnail(message);
                                    }
                                }).start();
                            }
                        }

                    }
                }
            }.execute();

            return true;
        }
    }

}
