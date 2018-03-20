package com.tg.tgt.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 * @author yiyang
 */
public abstract class BaseFragment extends android.support.v4.app.Fragment {
    String TAG = BaseFragment.this.getClass().getSimpleName();

    /**
     * 贴附的activity
     */
    protected AppCompatActivity mContext;

    /**
     * 根view
     */
    protected View mRootView;

    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible;

    /**
     * 是否加载完成
     * 当执行完oncreatview,View的初始化方法后方法后即为true
     */
    protected boolean mIsPrepare;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (AppCompatActivity) getActivity();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化数据
        initData(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {

        Log.i(TAG, mRootView == null ? "onCreateView mRootView=null" : "onCreateView mRootView!=null");
        //在onCreateView方法内复用RootView
        if (mRootView == null) {


            mRootView = inflater.inflate(setLayoutResouceId(), container, false);

            initView(mRootView);
            initToolBar();
            mIsPrepare = true;
            if (isVisible)
                lazyLoad();
            initEvent();
        }


        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mRootView) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initToolBar() {
//        Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
//        mContext.setSupportActionBar(toolbar);
//        ActionBar actionBar = mContext.getSupportActionBar();
//        if (actionBar != null) {
////            actionBar.setTitle("");
//            actionBar.setDisplayShowTitleEnabled(false);
//
//            if (/*canBack()*/!(ActMgrs.getInstance().currentActivity() instanceof MainActivity)) {
//                actionBar.setDisplayHomeAsUpEnabled(true);
////                // 显示导航按钮
////                toolbar.setNavigationIcon(R.drawable.icon_back);
//                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mContext.onBackPressed();
//                    }
//                });
//            }
//        }
    }

    /**
     * 子类如果不需要返回需要复写该方法
     * @return
     */
    protected boolean canBack() {
        return true;
    }


    /**
     * 初始化数据
     * @param arguments 接收到的从其他地方传递过来的参数
     */
    protected void initData(Bundle arguments) {
    }

    /**
     * 初始化View
     * @param rootView
     */
    protected abstract void initView(View rootView);

    /**
     * 设置监听事件
     */
    protected void initEvent() {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getUserVisibleHint()) {
            isVisible = true;
            Log.i(TAG, "onVisible");
            onVisible();
        } else {
            isVisible = false;
            Log.i(TAG, "onInvisible");
            onInvisible();
        }
    }

    private boolean isInit = false;

    /**
     * 可见
     */
    protected void onVisible() {
        if (!isInit && mIsPrepare)
            lazyLoad();
        isInit = true;
    }

    /**
     * 不可见
     */
    protected void onInvisible() {

    }

    /**
     * 延迟加载
     * 子类必须重写此方法
     */
    protected abstract void lazyLoad();


    /**
     * @return 根布局资源id
     */
    @LayoutRes
    protected abstract int setLayoutResouceId();

    protected <T extends View> T findViewById(int id) {
        if (mRootView == null) {
            return null;
        }

        return (T) mRootView.findViewById(id);
    }
}
