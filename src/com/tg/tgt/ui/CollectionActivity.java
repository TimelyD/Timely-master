package com.tg.tgt.ui;


import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.classic.common.MultipleStatusView;
import com.hyphenate.easeui.utils.EaseSmileUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import com.hyphenate.easeui.GlideApp;
import com.tg.tgt.utils.OpenFileUtil;

/**
 * Created by DELL on 2018/7/2.
 */

public class CollectionActivity extends BaseActivity{

    private EaseTitleBar easeTitleBar;
    private RelativeLayout bottomRelative;

    private boolean isEditor;
    private int currentPage;
    private int maxPage;

    private RecyclerView mRecyclerView;
    //private MultipleStatusView mStatusView;
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
    private Map<Integer,Boolean> isSelect = new HashMap<>();
    private List<Integer> hasSelect = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        init();
        mSwipeRefreshLayout.measure(0,0);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
     //   mSwipeRefreshLayout.setRefreshing(true);
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
                checkBoxAll.setChecked(false);
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
                                Toast.makeText(CollectionActivity.this, R.string.delete_successful,Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFaild(HttpResult<EmptyData> result) {
                                super.onFaild(result);
                                Toast.makeText(CollectionActivity.this, R.string.delete_fail,Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(1,false);
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
                    checkBoxAll.setChecked(false);
                    deleteTitleBt.setText(getString(R.string.editor));
                    bottomRelative.setVisibility(View.GONE);
                }else {
                    deleteTitleBt.setText(getString(R.string.cancel_collection));
                    bottomRelative.setVisibility(View.VISIBLE);
                    isSelect.clear();
                    hasSelect.clear();
                    if (isSeleteAll){
                        isSeleteAll = false;
                        mAdapter.notifyDataSetChanged();
                    }
                }
                isEditor = !isEditor;
                mAdapter.notifyDataSetChanged();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.collection_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.collection_layout);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new BaseQuickAdapter<CollectionItemModel, BaseViewHolder>(R.layout
                .item_collection, mDatas) {
            @Override
            protected void convert(final BaseViewHolder helper, final CollectionItemModel item) {
                if (!TextUtils.isEmpty(item.getContent())) {
                    Spannable span = EaseSmileUtils.getSmiledText(CollectionActivity.this, item.getContent());
                    ((TextView)helper.getView(R.id.item_title)).setText(span, TextView.BufferType.SPANNABLE);
                }

                if (TextUtils.isEmpty(item.getFormUserName()))
                    helper.setText(R.id.item_message,item.getCrtTime());
                else
                     helper.setText(R.id.item_message,getString(R.string.from_user)+item.getFormUserName()+"     "+item.getCrtTime());
            //    if (!TextUtils.isEmpty(item.getPicturePath())) {
                    RoundedCorners roundedCorners= new RoundedCorners(6);
                    RequestOptions options= RequestOptions.bitmapTransform(roundedCorners).override(120, 120);
                    GlideApp.with(mActivity).load(item.getPicturePath()).placeholder(R.drawable.collection_default)
                            .into((ImageView) helper.getView(R.id.item_image));
        //        }
                if (isEditor){
                    helper.getView(R.id.delete_image).setVisibility(View.VISIBLE);
                }else {
                    helper.getView(R.id.delete_image).setVisibility(View.GONE);
                }
                CheckBox checkBox = (CheckBox) helper.getView(R.id.delete_image);
                Log.e("Tag","下标"+helper.getAdapterPosition());
                if (isSelect == null)
                    Log.e("Tag","null");
     //           checkBox.setChecked(isSelect.get(helper.getAdapterPosition()));
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        item.setSelect(isChecked);
                        if (isChecked)
                            hasSelect.add(helper.getAdapterPosition());
                        else
                            hasSelect.remove((Object)helper.getAdapterPosition());
                    }
                });
            }
        };
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.empty_moment);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(1,true);
            }
        });
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (currentPage < maxPage) {
                    currentPage++;
                    loadData(currentPage,false);
                } else {
                    mAdapter.loadMoreEnd();
                }
            }
        }, mRecyclerView);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!isEditor) {
                    if (mDatas.get(position).getType() == 4){
                        startActivity(OpenFileUtil.openFile(mDatas.get(position).getFilePath()));
                    }else {
                        Intent mIntent = new Intent();
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable("model", mDatas.get(position));
                        mIntent.putExtras(mBundle);
                        mIntent.setClass(CollectionActivity.this, CollectionShowActivity.class);
                        startActivity(mIntent);
                    }
                }
            }
        });

    }

    private void loadData(int currentPageInput, final Boolean freshB) {
        currentPage = currentPageInput;
        ApiManger2.getApiService()
                .collectionList(currentPage, pageSize)
                .compose(this.<HttpResult<CollectionModel>>bindToLifeCyclerAndApplySchedulers(null,false))
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (freshB)
                            mSwipeRefreshLayout.setRefreshing(true);
                    }
                })
                .subscribe(new BaseObserver2<CollectionModel>() {
                    @Override
                    protected void onSuccess(CollectionModel collectionModel) {
                        if (mDatas != null & mDatas.size() > 0)
                            mDatas.clear();
                        maxPage = collectionModel.getTotal();
                        if (collectionModel.getList() != null && collectionModel.getList().size() > 0) {
                            for (int i = 0; i < collectionModel.getList().size(); i++) {
                                mDatas.add(collectionModel.getList().get(i));
                            }
                            if (isSelect == null || isSelect.size()<=0){
                                for (int i = 0; i < mDatas.size(); i++)
                                    isSelect.put(i, false);
                            }else {
                                for (int i = 0; i < mDatas.size(); i++) {
                                    isSelect.put(i, false);
                                    for (int j = 0; j < hasSelect.size(); j++) {
                                        if (i == hasSelect.get(j)) {
                                            isSelect.put(i, true);
                                            mDatas.get(i).setSelect(true);
                                            j = hasSelect.size()-1;
                                        }
                                    }
                                }
                            }
                        }
                        if (freshB) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            mAdapter.notifyDataSetChanged();
                        }else {
                            mAdapter.loadMoreComplete();
                        }
                    }

                    @Override
                    public void onFaild(HttpResult<CollectionModel> result) {
                        super.onFaild(result);
                        if (freshB) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }else {
                            mAdapter.loadMoreComplete();
                        }
                    }
                });
    }

}
