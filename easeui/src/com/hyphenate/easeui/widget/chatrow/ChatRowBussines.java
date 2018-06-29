package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;

public class ChatRowBussines extends EaseChatRow {

    private TextView name;
    private TextView state;

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
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        state.setText(txtBody.getMessage());
        name.setText(message.getStringAttribute(EaseConstant.BUSSINES_NAME,null));
        state.setText(message.getStringAttribute(EaseConstant.BUSSINES_NUMBER,null));
    }
    
    @Override
    protected void onUpdateView() {
        
    }

    @Override
    protected void onBubbleClick() {
        
    }

  

}
