package com.tg.tgt.ui;


import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.classic.common.MultipleStatusView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.tg.tgt.R;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.RxUtils;
import com.tg.tgt.http.model2.CollectionItemModel;
import com.tg.tgt.http.model2.CollectionModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import com.hyphenate.easeui.GlideApp;

/**
 * Created by DELL on 2018/7/2.
 */

public class CollectionActivity extends BaseActivity{

    private EaseTitleBar easeTitleBar;
    private RelativeLayout bottomRelative;

    private boolean isEditor;

    private RecyclerView mRecyclerView;
    private MultipleStatusView mStatusView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static int pageSize = 10;
    private List<CollectionItemModel> mDatas = new ArrayList<>();
    private BaseQuickAdapter<CollectionItemModel, BaseViewHolder> mAdapter;


    private CheckBox checkBoxAll;
    private Button deleteBt;
    private Button deleteTitleBt;
    private boolean isSeleteAll;
    private String deleteIds = "";
    private List<Integer> deletePos = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        init();
        mSwipeRefreshLayout.measure(0,0);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        loadData(false);
        checkBoxAll = (CheckBox) findViewById(R.id.selected_all) ;
        deleteBt = (Button) findViewById(R.id.delete_bt);
        checkBoxAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    isSeleteAll = true;
                }else {
                    isSeleteAll = false;
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDatas!=null && mDatas.size()>0){
                    for (int i=0;i<mDatas.size();i++){
                        if (mDatas.get(i).isSelect()) {
                            deletePos.add(i);
                            if (TextUtils.isEmpty(deleteIds))
                                deleteIds = deleteIds + mDatas.get(i).getId();
                            else
                                deleteIds = deleteIds + "," + mDatas.get(i).getId();
                        }
                    }
                    if (!TextUtils.isEmpty(deleteIds)){
                        ApiManger2.getApiService()
                        .deleteCollection(deleteIds)
                        .compose(CollectionActivity.this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers(null,false))
                        .subscribe(new BaseObserver2<EmptyData>() {
                            @Override
                            protected void onSuccess(EmptyData emptyData) {
                                for (int i=0;i<deletePos.size();i++) {
                                    mDatas.remove(deletePos.get(i));
                                    mAdapter.remove(deletePos.get(i));
                                }
                                mAdapter.notifyDataSetChanged();
                                Toast.makeText(CollectionActivity.this,"删除成功",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFaild(HttpResult<EmptyData> result) {
                                super.onFaild(result);
                                Toast.makeText(CollectionActivity.this,"删除失败",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        });
    }

    private void init(){
        isEditor = false;
        easeTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        bottomRelative = (RelativeLayout) findViewById(R.id.layout_bottom);
        easeTitleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        deleteTitleBt = (Button)findViewById(R.id.delete_title);
        deleteTitleBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditor){
                    deleteTitleBt.setText(getString(R.string.editor));
                    bottomRelative.setVisibility(View.GONE);
                }else {
                    deleteTitleBt.setText(getString(R.string.cancel_collection));
                    bottomRelative.setVisibility(View.VISIBLE);
                    if (isSeleteAll){
                        isSeleteAll = false;
                        mAdapter.notifyDataSetChanged();
                    }
                }
                isEditor = !isEditor;
                mAdapter.notifyDataSetChanged();
            }
        });

        mStatusView = (MultipleStatusView) findViewById(R.id.status_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.collection_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.collection_layout);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new BaseQuickAdapter<CollectionItemModel, BaseViewHolder>(R.layout
                .item_collection, mDatas) {
            @Override
            protected void convert(BaseViewHolder helper, final CollectionItemModel item) {
                helper.setText(R.id.item_title,item.getContent());
                if (TextUtils.isEmpty(item.getIsFrom()))
                    helper.setText(R.id.item_message,item.getCrtTime());
                else
                     helper.setText(R.id.item_message,"来自:  "+item.getIsFrom()+"     "+item.getCrtTime());
                ImageView view = (ImageView) helper.getView(R.id.item_image);
                GlideApp.with(mActivity).load(item.getPicturePath()).placeholder(R.drawable.default_avatar).into(view);
                if (isEditor){
                    helper.getView(R.id.delete_image).setVisibility(View.VISIBLE);
                }else {
                    helper.getView(R.id.delete_image).setVisibility(View.GONE);
                }
                CheckBox checkBox = (CheckBox) helper.getView(R.id.delete_image);
                checkBox.setChecked(item.isSelect());
                if (isSeleteAll){
                    checkBox.setChecked(true);
                    item.setSelect(true);
                }else {
                    checkBox.setChecked(false);
                    item.setSelect(false);
                }
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        item.setSelect(isChecked);
                    }
                });
            }
        };
        mAdapter.bindToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
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
                Intent mIntent = new Intent();
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("model",mDatas.get(position));
                mIntent.putExtras(mBundle);
                mIntent.setClass(CollectionActivity.this,CollectionShowActivity.class);
                startActivity(mIntent);
            }
        });

    }

    private void loadData(final boolean loadMore) {
        ApiManger2.getApiService()
                .collectionList(!loadMore||mDatas.size()==0?1:mDatas.get(mDatas.size()-1).getId(), pageSize)
           //     .compose(this.<HttpResult<List<CollectionModel>>>bindToLifeCyclerAndApplySchedulers(null))
               // .compose(this.<HttpResult<List<CollectionItemModel>>>bindToLifeCyclerAndApplySchedulers(null))
//                .compose(RxUtils.applyRetry(this, new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean aBoolean) throws Exception {
//                        dismissProgress();
//                    }
//                }))
                .compose(this.<HttpResult<CollectionModel>>bindToLifeCyclerAndApplySchedulers(null,false))
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if(!loadMore)
                            mSwipeRefreshLayout.setRefreshing(true);
                    }
                })
                .subscribe(new BaseObserver2<CollectionModel>() {
                    @Override
                    protected void onSuccess(CollectionModel collectionModel) {
                        if(loadMore){
                            if(collectionModel.getList() == null){
                                mAdapter.loadMoreEnd();
                                return;
                            }else if(collectionModel.getList().size()<10){
                                mAdapter.loadMoreEnd();
                            }else {
                                mAdapter.loadMoreComplete();
                            }
                            mDatas.addAll(collectionModel.getList());
                            mAdapter.notifyDataSetChanged();

                        }else {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mDatas.clear();
                            if(collectionModel.getList() != null) {
                                mDatas.addAll(collectionModel.getList());

                                if(collectionModel.getList().size()>=pageSize){
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
                    public void onFaild(HttpResult<CollectionModel> result) {
                        super.onFaild(result);
                        if(loadMore)
                            mAdapter.loadMoreFail();
                        else {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
//                .subscribe(new BaseObserver2<List<CollectionItemModel>>() {
//                    @Override
//                    protected void onSuccess(List<CollectionItemModel> collectionItemModels) {
//                        if(loadMore){
//                            if(collectionItemModels == null){
//                                mAdapter.loadMoreEnd();
//                                return;
//                            }else if(collectionItemModels.size()<10){
//                                mAdapter.loadMoreEnd();
//                            }else {
//                                mAdapter.loadMoreComplete();
//                            }
//                            mDatas.addAll(collectionItemModels);
//                            mAdapter.notifyDataSetChanged();
//
//                        }else {
//                            mSwipeRefreshLayout.setRefreshing(false);
//                            mDatas.clear();
//                            if(collectionItemModels != null) {
//                                mDatas.addAll(collectionItemModels);
//
//                                if(collectionItemModels.size()>=pageSize){
//                                    mAdapter.loadMoreComplete();
//                                }else {
//                                    mAdapter.loadMoreEnd();
//                                }
//                            }else {
//                                mStatusView.showEmpty();
//                            }
//                            mAdapter.notifyDataSetChanged();
//                        }
//                    }
//
//                    @Override
//                    public void onFaild(HttpResult<List<CollectionItemModel>> result) {
//                        super.onFaild(result);
//                        if(loadMore)
//                            mAdapter.loadMoreFail();
//                        else {
//                            mSwipeRefreshLayout.setRefreshing(false);
//                        }
//                    }
//                });
    }

}
