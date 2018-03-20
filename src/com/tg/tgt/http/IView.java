package com.tg.tgt.http;

import android.support.annotation.StringRes;

import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.Disposable;

/**
 *  Rx的生命周期管理，
 *  以及网络请求时的一些交互
 * @author yiyang
 */
public interface IView {
    boolean addRxStop(Disposable disposable);

    boolean addRxDestroy(Disposable disposable);
    void clearDisposabl();
    void remove(Disposable disposable);

    /**
     * 显示ProgressDialog
     */
    void showProgress(String msg);
    void showProgress(@StringRes int msg);

    /**
     * 取消ProgressDialog
     */
    void dismissProgress();

    /**
     * 展示失败时候的提示
     */
    void showTips(String errorMsg);
    /**
     * 展示失败时候的提示
     */
    void showTips(@StringRes int errorMsg);

    //处理rx生命周期以及dialog的显示
    public <T> ObservableTransformer<T, T> bindToLifeCyclerAndApplySchedulers(final String progress, boolean canCancel);
}
