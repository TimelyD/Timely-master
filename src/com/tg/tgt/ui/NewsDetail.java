package com.tg.tgt.ui;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.easeui.widget.EaseTitleBar;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.helper.ActionItem;
import com.tg.tgt.helper.MenuPopup;
import com.tg.tgt.http.ApiException;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpHelper;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.model2.NewsModel;
import com.tg.tgt.utils.ToastUtils;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * @author yiyang
 */
public class NewsDetail extends BaseActivity {
    private EaseTitleBar mTitleBar;
    private TextView mNewsDetailTv;
    private MenuPopup popup;
    private String mId;
    private NewsModel mModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_news_detail);
        initView();


        mId = getIntent().getStringExtra(Constant.NEWS);

        ApiManger2.getApiService().showNewsDetail(mId)
                .compose(this.<HttpResult<NewsModel>>bindToLifeCyclerAndApplySchedulers(null))
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        showProgress(R.string.loading);
                    }
                })
                .map(new Function<HttpResult<NewsModel>, String>() {
                    @Override
                    public String apply(@NonNull HttpResult<NewsModel> newsModelHttpResult) throws Exception {
                        if(HttpHelper.isHttpSuccess(newsModelHttpResult)){
                            NewsModel newsModel = newsModelHttpResult.getData();
                            mModel = newsModel;
                            String content = newsModel.getContent();
                            mTitleBar.setTitle(mModel.getTitle());
                            if(!TextUtils.isEmpty(content))
                            return content;
                        }else {
                            throw new ApiException(newsModelHttpResult.getMsg());
                        }
                        return "";
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<String, Spanned>() {
                    @Override
                    public Spanned apply(@NonNull String content) throws Exception {
                        Spanned spanned;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            spanned = Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT, new Html.ImageGetter() {
                                @Override
                                public Drawable getDrawable(String source) {
                                    Drawable drawable = null;
                                    try {
                                        drawable = Drawable.createFromStream(new URL(source)
                                                .openStream(), UUID
                                                .randomUUID().toString());  // Or fetch it from the URL

                                        // Important
                                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                                                drawable
                                                        .getIntrinsicHeight());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        return ContextCompat.getDrawable(mContext, R.drawable
                                                .default_img);
                                    }
                                    return drawable;
                                }
                            }, null);
                        }else {
                            spanned = Html.fromHtml(content, new Html.ImageGetter() {
                                @Override
                                public Drawable getDrawable(String source) {
                                    Drawable drawable = null;
                                    try {
                                        drawable = Drawable.createFromStream(new URL(source)
                                                .openStream(), UUID
                                                .randomUUID().toString());  // Or fetch it from the URL

                                        // Important
                                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                                                drawable
                                                        .getIntrinsicHeight());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        return ContextCompat.getDrawable(mContext, R.drawable
                                                .default_img);
                                    }
                                    return drawable;
                                }
                            }, null);
                        }
                        return spanned;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Spanned>() {
                    @Override
                    public void accept(@NonNull Spanned spanned) throws Exception {
                        dismissProgress();
                        mNewsDetailTv.setText(spanned);
                        mNewsDetailTv.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        dismissProgress();
                    }
                });

    }

    private void initView() {
        mTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        mNewsDetailTv = (TextView) findViewById(R.id.news_detail_tv);

        setTitleBarLeftBack();
        mTitleBar.setRightLayoutVisibility(View.VISIBLE);
        mTitleBar.setRightImageResource(R.drawable.share);
        mTitleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });
    }

    /**显示下拉菜单*/
    public void showMenu(View view) {
        if(popup == null) {
            popup = new MenuPopup(this, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                    .LayoutParams.WRAP_CONTENT);
            popup.addAction(new ActionItem(getResources().getDrawable(R.drawable.share), getString(R.string.share_news)));
            popup.setItemOnClickListener(new MenuPopup.OnItemOnClickListener() {
                @Override
                public void onItemClick(ActionItem item, int position) {
                    switch (position) {
                        case 0:
                            if(mId == null || TextUtils.isEmpty(mModel.getId())){
                                return;
                            }
                            ApiManger2.getApiService().shareNews(mModel.getId(), null, null)
                                    .compose(mActivity.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers())
                                    .subscribe(new BaseObserver2<EmptyData>() {
                                        @Override
                                        protected void onSuccess(EmptyData emptyData) {
                                            ToastUtils.showToast(getApplicationContext(), R.string.share_success);
                                        }
                                    });

                            break;

                        default:
                            break;
                    }
                }
            });
        }
        popup.show(mTitleBar);
    }
}
