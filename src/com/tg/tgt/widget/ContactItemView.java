package com.tg.tgt.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easeui.widget.ZQImageViewRoundOval;
import com.tg.tgt.R;
import com.tg.tgt.utils.CodeUtils;

public class ContactItemView extends LinearLayout{

    private TextView unreadMsgView;

    public ContactItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ContactItemView(Context context) {
        super(context);
        init(context, null);
    }
    
    private void init(Context context, AttributeSet attrs){
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ContactItemView);
        String name = ta.getString(R.styleable.ContactItemView_contactItemName);
        Drawable image = ta.getDrawable(R.styleable.ContactItemView_contactItemImage);
        ta.recycle();
        
        LayoutInflater.from(context).inflate(R.layout.em_widget_contact_item, this);
        ZQImageViewRoundOval avatar = (ZQImageViewRoundOval) findViewById(R.id.avatar);
        avatar.setType(ZQImageViewRoundOval.TYPE_ROUND);avatar.setRoundRadius(10);
        unreadMsgView = (TextView) findViewById(R.id.unread_msg_number);
        TextView nameView = (TextView) findViewById(R.id.name);
        if(image != null){
            avatar.setImageDrawable(image);
        }
        nameView.setText(name);
    }
    
    public void setUnreadCount(int unreadCount){
        unreadMsgView.setText(CodeUtils.getUnreadCount(unreadCount));
    }
    
    public void showUnreadMsgView(){
        unreadMsgView.setVisibility(View.VISIBLE);
    }
    public void hideUnreadMsgView(){
        unreadMsgView.setVisibility(View.INVISIBLE);
    }
    
}
