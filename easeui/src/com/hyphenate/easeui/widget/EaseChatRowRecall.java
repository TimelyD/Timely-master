package com.hyphenate.easeui.widget;

import android.content.Context;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;

/**
 * Created by easemob on 2017/7/31.
 */

public class EaseChatRowRecall extends EaseChatRow {

    private TextView contentView;

    public EaseChatRowRecall(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(R.layout.row_invite_message, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = (TextView) findViewById(R.id.tv_invite_msg);
    }

    @Override
    protected void onUpdateView() {

    }

    @Override
    protected void onSetUpView() {
        // 设置显示内容
        String messageStr = null;
        if (message.direct() == EMMessage.Direct.SEND) {
            messageStr = String.format(context.getString(R.string.msg_recall_by_self));
        } else {
            messageStr = String.format(context.getString(R.string.msg_recall_by_user), message.getFrom());
        }
        if(message.getStringAttribute("name", null)!=null){
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.conversationId());
            if(conversation.getType()== EMConversation.EMConversationType.GroupChat){
                messageStr = String.format(context.getString(R.string.msg_recall_by_user),message.getStringAttribute("name", null));
            }else {
                messageStr = String.format(context.getString(R.string.msg_recall_by_user),"对方");
            }
        }
        Log.i("收到2：",messageStr);
        contentView.setText(messageStr);
    }

    @Override
    protected void onBubbleClick() {

    }

}
