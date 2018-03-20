package com.tg.tgt.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.tg.tgt.R;

/**
 *
 * @author yiyang
 */
public class LoadMoreListView extends ListView implements AbsListView.OnScrollListener {

    private View footer;
    private int totalItemCount;
    private int lastVisibieItem;
    private boolean isLoading;
    private View mLoadMoreLayout;
    private View mNoMoreLayout;
    private View mRetryLayout;

    public enum State{
        NoMore,
        HasMore,
        Retry
    }

    private State mState = State.HasMore;

    public void setState(State state){
        mState = state;
        if(state == State.Retry){
            showRetry();
        }else if(state == State.NoMore){
            showNoMore();
        }else {
            showLoading();
        }
    }
    public State getState(){
        return mState;
    }

    public LoadMoreListView(Context context) {
        super(context);
        initView(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        footer = inflater.inflate(R.layout.layout_loading_item, null);
        mLoadMoreLayout = footer.findViewById(R.id.load_more_layout);
        mNoMoreLayout = footer.findViewById(R.id.no_more_layout);
        mRetryLayout = footer.findViewById(R.id.retry_layout);
        mRetryLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnLoadMoreListener != null) {
                    showLoading();
                    mOnLoadMoreListener.onLoadMore();
                }
            }
        });

        //避免触发onItemclick事件
        footer.setOnClickListener(null);
        // 设置隐藏底部布局
        dismissFooter();
        this.addFooterView(footer);
        this.setOnScrollListener(this);

    }

    private void dismissFooter() {
        footer.setVisibility(View.GONE);
    }

    private void showFooter() {
        footer.setVisibility(View.VISIBLE);
    }

    private void showLoading(){
        showFooter();
        mNoMoreLayout.setVisibility(GONE);
        mRetryLayout.setVisibility(GONE);
        mLoadMoreLayout.setVisibility(VISIBLE);
    }
    private void showNoMore(){
        showFooter();
        mLoadMoreLayout.setVisibility(GONE);
        mRetryLayout.setVisibility(GONE);
        mNoMoreLayout.setVisibility(VISIBLE);
    }
    private void showRetry(){
        showFooter();
        mLoadMoreLayout.setVisibility(GONE);
        mNoMoreLayout.setVisibility(GONE);
        mRetryLayout.setVisibility(VISIBLE);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(mState == State.HasMore) {
            if (totalItemCount == lastVisibieItem && scrollState == SCROLL_STATE_IDLE) {
                if (!isLoading) {
                    isLoading = true;
                    showLoading();
                    // 加载更多（获取接口）
                    if (mOnLoadMoreListener != null)
                        mOnLoadMoreListener.onLoadMore();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.lastVisibieItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
    }

    private OnLoadMoreListener mOnLoadMoreListener;
    public interface OnLoadMoreListener {
        void onLoadMore();
    }
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener){
        mOnLoadMoreListener = onLoadMoreListener;
    }
    public void loadComplete(){
        isLoading = false;
        dismissFooter();
    }

}
