package com.hyphenate.easeui.widget.chatrow;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.exceptions.HyphenateException;

import android.content.Context;
import android.text.Spannable;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class EaseChatRowText extends EaseChatRow{

	private TextView contentView;
    private View mFireView;
    private CheckBox select;

    public EaseChatRowText(Context context, EMMessage message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflateView() {
		inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
				com.hyphenate.easeui.R.layout.ease_row_received_message : com.hyphenate.easeui.R.layout.ease_row_sent_message, this);
	}

	@Override
	protected void onFindViewById() {
		contentView = (TextView) findViewById(com.hyphenate.easeui.R.id.tv_chatcontent);
        mFireView = findViewById(com.hyphenate.easeui.R.id.iv_fire);
        select= (CheckBox) findViewById(R.id.select);
    }

    @Override
    public void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        Spannable span = EaseSmileUtils.getSmiledText(context, txtBody.getMessage());
        // 设置内容
        contentView.setText(span, BufferType.SPANNABLE);

        //设置阅后即焚标识
        mFireView.setVisibility(message.getStringAttribute(EaseConstant.MESSAGE_ATTR_IS_FIRE, "").equals("1") ? VISIBLE : GONE);

        handleTextMessage();
        Log.i("dcz_MESAGE",EaseConstant.MESSAGE_ATTR_SELECT+"");
        select.setVisibility(EaseConstant.MESSAGE_ATTR_SELECT==true?VISIBLE:GONE);
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
            if(!message.isAcked() && message.getChatType() == ChatType.Chat){
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onUpdateView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onBubbleClick() {
        // TODO Auto-generated method stub
        
    }



}
