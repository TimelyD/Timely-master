package com.tg.tgt.widget;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.tg.tgt.Constant;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;

public class ChatRowVoiceCall extends EaseChatRow {

    private TextView contentvView;
    private CheckBox select;
    private View bt;
    public ChatRowVoiceCall(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)){
            inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                    com.hyphenate.easeui.R.layout.ease_row_received_voice_call : com.hyphenate.easeui.R.layout.ease_row_sent_voice_call, this);
        // video call
        }else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)){
            inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                    com.hyphenate.easeui.R.layout.ease_row_received_video_call : com.hyphenate.easeui.R.layout.ease_row_sent_video_call, this);
        }
    }

    @Override
    protected void onFindViewById() {
        contentvView = (TextView) findViewById(com.hyphenate.easeui.R.id.tv_chatcontent);
        select= (CheckBox) findViewById(com.hyphenate.easeui.R.id.select);
        bt= findViewById(com.hyphenate.easeui.R.id.bt);
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        contentvView.setText(txtBody.getMessage());
        select.setVisibility(EaseConstant.MESSAGE_ATTR_SELECT==true?VISIBLE:GONE);
        bt.setVisibility(EaseConstant.MESSAGE_ATTR_SELECT==true?VISIBLE:GONE);
        select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("dcz_id",message.getMsgId()+"q");
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
            //select.setChecked(EaseConstant.list_ms.contains(message.getMsgId())?true:false);
        }
        bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("dcz","点击了");
                if(select.getVisibility()==VISIBLE){
                   // select.setChecked(select.isChecked()?false:true);
                }
            }
        });
    }
    
    @Override
    protected void onUpdateView() {
        
    }

    @Override
    protected void onBubbleClick() {
        
    }

  

}
