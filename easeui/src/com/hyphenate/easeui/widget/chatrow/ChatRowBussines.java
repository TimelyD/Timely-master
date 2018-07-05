package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.ImageUtils;
import com.hyphenate.easeui.widget.ZQImageViewRoundOval;
import com.hyphenate.exceptions.HyphenateException;

public class ChatRowBussines extends EaseChatRow {

    private TextView name;
    private TextView state;
    private ImageViewRoundOval avatar;
    private CheckBox select;
    private View bt;

    public ChatRowBussines(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                com.hyphenate.easeui.R.layout.ease_row_received_bussines : com.hyphenate.easeui.R.layout.ease_row_sent_bussines, this);
    }

    @Override
    protected void onFindViewById() {
        name = (TextView) findViewById(com.hyphenate.easeui.R.id.tv_file_name);
        state = (TextView) findViewById(com.hyphenate.easeui.R.id.tv_file_state);
        avatar = (ImageViewRoundOval) findViewById(com.hyphenate.easeui.R.id.pic);
        avatar.setType(ImageViewRoundOval.TYPE_ROUND);avatar.setRoundRadius(20);
        select= (CheckBox) findViewById(R.id.select);
        bt= findViewById(R.id.bt);
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        ImageUtils.show(getContext(),message.getStringAttribute(EaseConstant.BUSSINES_PIC,null), R.drawable.default_avatar2,avatar);
        state.setText(txtBody.getMessage());
        name.setText(message.getStringAttribute(EaseConstant.BUSSINES_NAME,null));
        state.setText(message.getStringAttribute(EaseConstant.BUSSINES_NUMBER,null));
        handleTextMessage();
        //ImageUtils.show(getContext(),message.getStringAttribute(EaseConstant.BUSSINES_PIC,null), R.drawable.ease_chat_item_file,avatar);
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
                    //select.setChecked(select.isChecked()?false:true);
                }
            }
        });
    }
    
    @Override
    protected void onUpdateView() {
        adapter.notifyDataSetChanged();
    }

    protected void handleTextMessage() {
        if (message.direct() == EMMessage.Direct.SEND) {
            setMessageSendCallback();
            switch (message.status()) {
                case CREATE:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.GONE);
                    break;
                case FAIL:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                    progressBar.setVisibility(View.VISIBLE);
                    statusView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }else{
            if(!message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat){
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onBubbleClick() {
        
    }

  

}
