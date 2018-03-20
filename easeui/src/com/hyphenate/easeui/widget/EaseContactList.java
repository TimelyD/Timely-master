package com.hyphenate.easeui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseContactAdapter;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.ArrayList;
import java.util.List;

public class EaseContactList extends RelativeLayout {
    protected static final String TAG = EaseContactList.class.getSimpleName();

    protected Context context;
    protected ListView listView;
    protected EaseContactAdapter adapter;
    protected List<EaseUser> contactList;
    protected EaseSidebar sidebar;

    protected int primaryColor;
    protected int primarySize;
    protected boolean showSiderBar;
    protected Drawable initialLetterBg;

    static final int MSG_UPDATE_LIST = 0;

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_LIST:
                    if (adapter != null) {
                        adapter.clear();
                        adapter.addAll(new ArrayList<EaseUser>(contactList));
                        adapter.notifyDataSetChanged();
                        mContactNumTv.setText(String.format(getContext().getString(R.string.num_of_contact),
                                contactList.size()));
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    protected int initialLetterColor;
    private TextView mContactNumTv;


    public EaseContactList(Context context) {
        super(context);
        init(context, null);
    }

    public EaseContactList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EaseContactList(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, com.hyphenate.easeui.R.styleable.EaseContactList);
        primaryColor = ta.getColor(com.hyphenate.easeui.R.styleable.EaseContactList_ctsListPrimaryTextColor, 0);
        primarySize = ta.getDimensionPixelSize(com.hyphenate.easeui.R.styleable
                .EaseContactList_ctsListPrimaryTextSize, 0);
        showSiderBar = ta.getBoolean(com.hyphenate.easeui.R.styleable.EaseContactList_ctsListShowSiderBar, true);
        initialLetterBg = ta.getDrawable(com.hyphenate.easeui.R.styleable.EaseContactList_ctsListInitialLetterBg);
        initialLetterColor = ta.getColor(com.hyphenate.easeui.R.styleable.EaseContactList_ctsListInitialLetterColor, 0);
        ta.recycle();


        LayoutInflater.from(context).inflate(com.hyphenate.easeui.R.layout.ease_widget_contact_list, this);
        listView = (ListView) findViewById(com.hyphenate.easeui.R.id.list);
        sidebar = (EaseSidebar) findViewById(com.hyphenate.easeui.R.id.sidebar);
        if (!showSiderBar)
            sidebar.setVisibility(View.GONE);
    }

    /*
     * init view
     */
    public void init(List<EaseUser> contactList) {
        this.contactList = contactList;
        adapter = new EaseContactAdapter(context, 0, new ArrayList<EaseUser>(contactList));
        adapter.setPrimaryColor(primaryColor).setPrimarySize(primarySize).setInitialLetterBg(initialLetterBg)
                .setInitialLetterColor(initialLetterColor);
        adapter.setEaseContactListHelper(mEaseContactListHelper);
        listView.setAdapter(adapter);
        /*mContactNumTv = new TextView(context);
        int padding = (int) (5 * context.getResources().getDisplayMetrics().density + 0.5f);
        //使用viewgroup模拟器api_19报错
        mContactNumTv.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.WRAP_CONTENT));
        mContactNumTv.setPadding(padding, padding, padding, padding);
        mContactNumTv.setGravity(Gravity.CENTER);
        mContactNumTv.setText(String.format(getContext().getString(R.string.num_of_contact), contactList.size()));
        listView.addFooterView(mContactNumTv);
        mContactNumTv.setOnClickListener(null)*/
        View footer = LayoutInflater.from(context).inflate(R.layout.footer_contact_list, null);
        mContactNumTv = (TextView)footer.findViewById(R.id.tv_num);
        mContactNumTv.setText(String.format(getContext().getString(R.string.num_of_contact), contactList.size()));
        listView.addFooterView(footer);
        //避免长按弹出contextmenu引起bug
        footer.setOnClickListener(null);
        if (showSiderBar) {
            sidebar.setListView(listView);
        }
    }


    public void refresh() {
        Message msg = handler.obtainMessage(MSG_UPDATE_LIST);
        handler.sendMessage(msg);
    }

    public void filter(CharSequence str) {
        adapter.getFilter().filter(str);
    }

    public ListView getListView() {
        return listView;
    }

    public void setShowSiderBar(boolean showSiderBar) {
        if (showSiderBar) {
            sidebar.setVisibility(View.VISIBLE);
        } else {
            sidebar.setVisibility(View.GONE);
        }
    }

    private EaseContactListHelper mEaseContactListHelper;

    public interface EaseContactListHelper {

        /**
         * 设置该用户是否有设置加密
         * @param username
         * @param msgClock
         * @param avatar
         * @param nameView
         */
        void onSetIsMsgClock(String username, ImageView msgClock, ImageView avatar, TextView nameView);
    }

    public void setEaseContactListHelper(EaseContactListHelper easeContactListHelper) {
        this.mEaseContactListHelper = easeContactListHelper;
    }
}
