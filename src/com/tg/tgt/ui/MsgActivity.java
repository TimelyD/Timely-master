package com.tg.tgt.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easeui.EaseApp;
import com.hyphenate.easeui.model.KeyBean;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.adapter.MsgAdapter;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.moment.bean.MsgBean;
import com.tg.tgt.utils.CodeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 段   消息列表
 **/

public class MsgActivity extends BaseActivity {
    private ListView ListView;
    private RelativeLayout kong;
    private String url;
    private List<MsgBean.ListBean> list=new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private MsgAdapter adapter;
    private int pageNumber=1;
    private int pageSize=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        setTitleBarLeftBack();
        ListView = (ListView) findViewById(R.id.list);
        kong=(RelativeLayout)findViewById(R.id.kong);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(){
                    @Override
                    public void run(){
                        getData();
                    }
                }.start();
            }
        });
        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        ListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getCurrentFocus() != null)
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
        getData();
    }

    private void setViews(){
        if(adapter==null){
            adapter=new MsgAdapter(MsgActivity.this,list,url);
            ListView.setAdapter(adapter);
        }else {
            adapter.notify(list);
        }
        kong.setVisibility(list.size()>0?View.GONE:View.VISIBLE);
    }

    private void getData(){
        ApiManger2.getApiService()
                .getMomentNotice(pageNumber,pageSize)
                .compose(this.<HttpResult<MsgBean>>bindToLifeCyclerAndApplySchedulers(null))
                .subscribe(new BaseObserver2<MsgBean>() {
                    @Override
                    protected void onSuccess(MsgBean list1) {
                        swipeRefreshLayout.setRefreshing(false);
                        list = list1.getList();
                    }

                    @Override
                    public void onNext(HttpResult<MsgBean> result) {
                        super.onNext(result);
                        swipeRefreshLayout.setRefreshing(false);
                        url=result.getDfsfileaccessprefix();
                        setViews();
                    }
                });
    }

    private void clean(){
        ApiManger2.getApiService()
                .cleanNotice(Constant.MYUID)
                .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers(null))
                .subscribe(new BaseObserver2<EmptyData>() {
                    @Override
                    protected void onSuccess(EmptyData list1) {
                        list.clear();
                        setViews();
                    }
                });
    }

    public void clear(View view) {
        showDialog();
    }

    private void showDialog() {
        View view = getLayoutInflater().inflate(R.layout.pup2, null);
        final Dialog dialog = new Dialog(this, R.style.TransparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        TextView bt1 = (TextView) view.findViewById(R.id.ok);
        TextView ti = (TextView) view.findViewById(R.id.ti);
        TextView cancle = (TextView) view.findViewById(R.id.cancle);
        ti.setText(R.string.ti13);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                clean();
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }
}
