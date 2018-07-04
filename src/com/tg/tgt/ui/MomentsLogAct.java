package com.tg.tgt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.classic.common.MultipleStatusView;
import com.hyphenate.easeui.GlideApp;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.model2.MomentsLogModel;
import com.tg.tgt.moment.ui.activity.MomentAct;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MomentsLogAct extends BaseActivity {

    private RecyclerView mRecyclerView;
    private MultipleStatusView mStatusView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<MomentsLogModel> mDatas = new ArrayList<>();
    private static int pageSize = 10;
    private BaseQuickAdapter<MomentsLogModel, BaseViewHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moments_log);
        initView();
        initEvent();
        mSwipeRefreshLayout.measure(0,0);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
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
                /*Intent intent = new Intent(mContext,.class);
                intent.putExtra("userId",mDatas.get(position).getId());intent.putExtra("username",mDatas.get(position).getNickname());
                startActivity(intent);*/
                try {
                    mActivity.startActivity(new Intent(mActivity, MomentAct.class)
                            .putExtra(Constant.USERNAME,mDatas.get(position).getFromName())
                            .putExtra(Constant.USER_ID,mDatas.get(position).getFromId())
                            .putExtra(Constant.IS_MINE_HOME_PAGE, true));
                }catch (Exception e){
                    Log.i("异常",e.getMessage());

                }
              /*  mContext.startActivity(new Intent(mContext, MomentAct.class).putExtra(Constant.USERNAME,mDatas.get(position).getNickname()).putExtra
                        (Constant.USER_ID,mDatas.get(position).getFromId()).putExtra(Constant.IS_MINE_HOME_PAGE, true));*/
            }
        });
    }

    private void loadData(final boolean loadMore) {
        ApiManger2.getApiService()
                .showMomentsLog(!loadMore||mDatas.size()==0?null:mDatas.get(mDatas.size()-1).getId(), pageSize)
                .compose(this.<HttpResult<List<MomentsLogModel>>>bindToLifeCyclerAndApplySchedulers(null))
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        if(!loadMore)
                            mSwipeRefreshLayout.setRefreshing(true);
                    }
                })
                .subscribe(new BaseObserver2<List<MomentsLogModel>>() {
                    @Override
                    protected void onSuccess(List<MomentsLogModel> momentsLogModels) {
                        if(loadMore){
                            if(momentsLogModels == null){
                                mAdapter.loadMoreEnd();
                                return;
                            }else if(momentsLogModels.size()<10){
                                mAdapter.loadMoreEnd();
                            }else {
                                mAdapter.loadMoreComplete();
                            }
                                mDatas.addAll(momentsLogModels);
                                mAdapter.notifyDataSetChanged();

                        }else {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mDatas.clear();
                            if(momentsLogModels != null) {
                                mDatas.addAll(momentsLogModels);

                                if(momentsLogModels.size()>=pageSize){
                                    mAdapter.loadMoreComplete();
                                }else {
                                    mAdapter.loadMoreEnd();
                                }
                            }else {
                               mStatusView.showEmpty();
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

    private int color1;
    private int color;

    private void initView() {
        color1 = getResources().getColor(R.color.tx_black_3);
        color = getResources().getColor(R.color.tx_black_1);
        setTitleBarLeftBack();
        mStatusView = (MultipleStatusView) findViewById(R.id.status_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
//        mRecyclerView.addItemDecoration(new SimpleDividerDecoration(mContext));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new BaseQuickAdapter<MomentsLogModel, BaseViewHolder>(R.layout
                .item_moments_log, mDatas) {
            @Override
            protected void convert(BaseViewHolder helper, MomentsLogModel item) {
                helper.setText(R.id.tv_time, item.getCreateTime());
                helper.setText(R.id.tv_name, item.safeGetRemark());
                ImageView view = (ImageView) helper.getView(R.id.iv_avatar);
                GlideApp.with(mActivity).load(item.getPicture()).placeholder(R.drawable.default_avatar).into(view);

                String relationStatus = item.getRelationStatus();
                Button btn = (Button) helper.getView(R.id.add_btn);
                if("0".equals(relationStatus)){
                    btn.setBackgroundResource(R.drawable.btn_agree_selector);
                    btn.setTextColor(color);
                    btn.setText(R.string.button_add);
                }else if("1".equals(relationStatus)){
                    btn.setBackgroundDrawable(null);
                    btn.setTextColor(color1);
                    btn.setText(R.string.has_added);
                }else {
                    btn.setBackgroundDrawable(null);
                    btn.setTextColor(color1);
                    btn.setText("unknown");
                }
                if(helper.getAdapterPosition()==mDatas.size()-1){
                    helper.setVisible(R.id.divider_long, true);
                    helper.setVisible(R.id.divider_short, false);
                }else {
                    helper.setVisible(R.id.divider_long, false);
                    helper.setVisible(R.id.divider_short, true);
                }
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setEmptyView(R.layout.empty_moments_log);
    }
}
