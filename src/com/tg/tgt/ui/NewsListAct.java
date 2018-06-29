package com.tg.tgt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gyf.barlibrary.ImmersionBar;
import com.hyphenate.easeui.GlideApp;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.model2.NewsModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 *
 * @author yiyang
 */
public class NewsListAct extends BaseActivity {


    private EaseTitleBar mTitleBar;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<NewsModel> mDatas = new ArrayList<>();
    private BaseQuickAdapter<NewsModel, BaseViewHolder> mAdapter;
    public static final int pageSize = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_news_list);
        initView();
        initEvent();
        mSwipeRefreshLayout.measure(0,0);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mSwipeRefreshLayout.setRefreshing(true);
        loadData(false);
    }

    private void initEvent() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(false);
            }
        });
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData(true);
            }
        }, mRecyclerView);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                startActivity(new Intent(NewsListAct.this, NewsDetail.class).putExtra(Constant.NEWS, mDatas.get(position).getId()));
            }
        });
    }

    private void loadData(final boolean loadMore) {
        ApiManger2.getApiService()
                .showNews(!loadMore||mDatas.size()==0?null:mDatas.get(mDatas.size()-1).getId(), pageSize)
                .compose(this.<HttpResult<List<NewsModel>>>bindToLifeCyclerAndApplySchedulers(null))
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        if(!loadMore)
                            mSwipeRefreshLayout.setRefreshing(true);
                    }
                })
                .subscribe(new BaseObserver2<List<NewsModel>>() {
                    @Override
                    protected void onSuccess(List<NewsModel> newsModels) {
                        if(loadMore){
                            if(newsModels == null){
                                mAdapter.loadMoreEnd();
                                return;
                            }else if(newsModels.size()<10){
                                mAdapter.loadMoreEnd();
                            }else {
                                mAdapter.loadMoreComplete();
                            }
                            mDatas.addAll(newsModels);
                            mAdapter.notifyDataSetChanged();

                        }else {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mDatas.clear();
                            if(newsModels != null) {
                                mDatas.addAll(newsModels);

                                if(newsModels.size()>=pageSize){
                                    mAdapter.loadMoreComplete();
                                }else {
                                    mAdapter.loadMoreEnd();
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onFaild(int code, String message) {
                        super.onFaild(code, message);
                        if(loadMore)
                            mAdapter.loadMoreFail();
                        else {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

    private void initView() {
        mImmersionBar
                .fitsSystemWindows(true)
                .statusBarColor(com.hyphenate.easeui.R.color.white)
                .statusBarDarkFont(true, 0.5f)//设置状态栏字体颜色
                .init();
        mTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
        mTitleBar.setLeftImageResource(R.drawable.back_black);
        mTitleBar.setTitleColor(getResources().getColor(R.color.title_black));

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        
        setTitleBarLeftBack();
        
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new BaseQuickAdapter<NewsModel, BaseViewHolder>(R.layout.item_news, mDatas) {
            @Override
            protected void convert(BaseViewHolder helper, NewsModel item) {
                helper.setText(R.id.news_time_top, item.getCreateTime());
                helper.setText(R.id.news_title_tv, item.getTitle());
                ImageView view = (ImageView) helper.getView(R.id.news_iv);
                GlideApp.with(mActivity).load(item.getPicture()).centerCrop().placeholder(R.drawable.default_img).into(view);
                if(!TextUtils.isEmpty(item.getBrief()))
                helper.setText(R.id.news_des, item.getBrief());
                helper.setText(R.id.news_time_tv, item.getCreateTime());
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }
}
