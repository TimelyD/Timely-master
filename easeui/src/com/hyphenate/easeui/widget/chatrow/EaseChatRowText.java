package com.hyphenate.easeui.widget.chatrow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseApp;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.KeyBean;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.utils.AESCodeer;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.utils.RSAUtil;
import com.hyphenate.exceptions.HyphenateException;

import android.content.Context;
import android.os.Message;
import android.text.Spannable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import java.lang.reflect.Type;
import java.util.List;

public class EaseChatRowText extends EaseChatRow{

	private TextView contentView;
    private View mFireView;
    private CheckBox select;
    private View bt;

    public EaseChatRowText(Context context, EMMessage message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflateView() {
		inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
				com.hyphenate.easeui.R.layout.ease_row_received_message : com.hyphenate.easeui.R.layout.ease_row_sent_message, this);
	}

    protected List<KeyBean> toArray(String string){
        Type type = new TypeToken<List<KeyBean>>(){}.getType();
        List<KeyBean> b = new Gson().fromJson(string,type);
        return b;
    }

	@Override
	protected void onFindViewById() {
		contentView = (TextView) findViewById(com.hyphenate.easeui.R.id.tv_chatcontent);
        mFireView = findViewById(com.hyphenate.easeui.R.id.iv_fire);
        select= (CheckBox) findViewById(R.id.select);
        bt= findViewById(R.id.bt);
    }
    private boolean mIsLongClick = false;
    @Override
    public void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        String text = txtBody.getMessage();

        String version = message.getStringAttribute(EaseConstant.VERSION, null);
        String mi = message.getStringAttribute(EaseConstant.MI, null);       //获得用对方的公钥RSA加密后的random
        String send_msg= message.getStringAttribute(EaseConstant.SEND, null);//获得用我的公钥RSA加密后的random
        String pri = EaseApp.sf.getString("pri_key", "");//获得私钥（可解密aesKey）
        String s = EaseApp.sf.getString("keyBean", ""); //得到登录时获取的我的最新版本聊天私钥（解密消息用）
        KeyBean bean = new Gson().fromJson(s, KeyBean.class);//我的聊天私钥的实体类
        String a = EaseApp.sf.getString("key_list", "");//得到登录时获取的我的所有版本聊天私钥
        if(mi!=null){
            try {
                if(message.getChatType()==ChatType.Chat){
                    Log.i("对话","单聊");
                    if(message.direct() == EMMessage.Direct.RECEIVE){
                        List<KeyBean> list = toArray(a);
                        for(KeyBean be:list){
                            if(version.equals(be.getVersion())){//获得对方发送消息的对应版本
                                bean=be;
                                break;
                            }
                        }
                    }
                }
                if(message.getChatType()==ChatType.GroupChat){
                    Log.i("对话","群聊");
                    if(message.direct() == EMMessage.Direct.RECEIVE){
                        for(KeyBean be:EaseApp.group_pub){
                            if(version.equals(be.getVersion()+"")){//获得对方发送消息的对应版本
                                bean=be;
                                break;
                            }
                        }
                    }
                }
                String Key=message.direct() == EMMessage.Direct.RECEIVE?mi:send_msg;
                String aeskey = RSAUtil.decryptBase64ByPrivateKey(bean.getAesKey(), pri);
                String prikey = AESCodeer.AESDncode(aeskey,bean.getChatSKey());       //对我的私钥进行解密
                String random = RSAUtil.decryptBase64ByPrivateKey(Key,prikey);
                text = AESCodeer.AESDncode(random,text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(text==null){
            text="";
        }

        Spannable span = EaseSmileUtils.getSmiledText(context,text);
        // 设置内容
        contentView.setText(span, BufferType.SPANNABLE);
        //写这个方法为了防止长按和点击的冲突
        contentView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    return mIsLongClick;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mIsLongClick = false;
                    return false;
                }
                return false;
            }
        });
        contentView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i("dcz","item长按中");
                mIsLongClick = true;
                EaseChatFragment.enu=true;
                Message msg = EaseChatFragment.mHandler.obtainMessage();
                msg.obj=message;msg.what=1;
                EaseChatFragment.mHandler.sendMessage(msg);
                return true;
            }
        });
        //设置阅后即焚标识
        mFireView.setVisibility(message.getStringAttribute(EaseConstant.MESSAGE_ATTR_IS_FIRE, "").equals("1") ? VISIBLE : GONE);

        handleTextMessage();
        //Log.i("dcz_MESAGE1",EaseConstant.MESSAGE_ATTR_SELECT+"");

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
