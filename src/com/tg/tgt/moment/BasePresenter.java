package com.tg.tgt.moment;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.tg.tgt.http.IView;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Presenter基类
 * @author yiyang
 */
public abstract class BasePresenter<M extends IModel, V extends IView> implements IPresenter {

    protected M mModel;

    protected V mView;

    public BasePresenter(M model, V view) {
        mView = view;
        mModel = model;
    }

    public BasePresenter(V view) {
        this(null, view);
    }

    @Override
    public void onStart() {

    }

    /**
     * 清除对外部对象的引用，反正内存泄露。
     */
    @Override
    @CallSuper
    public void recycle() {
        this.mView = null;
        this.mModel = null;
    }

    public <T> ObservableTransformer<T, T> bindToLifeCyclerAndApplySchedulers(){
        return bindToLifeCyclerAndApplySchedulers(null);
    }
    //处理rx生命周期以及dialog的显示
    public <T> ObservableTransformer<T, T> bindToLifeCyclerAndApplySchedulers(final String progress) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(@io.reactivex.annotations.NonNull Disposable disposable) throws
                                    Exception {
                                if (mView != null) {
                                    mView.addRxDestroy(disposable);
                                    if (!TextUtils.isEmpty(progress))
                                        mView.showProgress(progress);
                                }
                            }
                        })
                        .doOnTerminate(new Action() {
                            @Override
                            public void run() throws Exception {
                                if (mView != null) {
                                    if (!TextUtils.isEmpty(progress))
                                        mView.dismissProgress();
                                }
                            }
                        })
                        ;
            }
        };
    }
}
