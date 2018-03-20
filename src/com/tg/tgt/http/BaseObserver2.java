package com.tg.tgt.http;

import android.widget.Toast;

import com.tg.tgt.App;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 *
 * @author yiyang
 */
public abstract class BaseObserver2<T> implements Observer<HttpResult<T>> {

    private static final int RESPONSE_CODE_FAILED = -1;  //返回数据失败,严重的错误

    private final boolean isShowTips;

    //  Activity 是否在执行onStop()时取消订阅
    private boolean isAddInStop = false;
    private Disposable mDisposable;


    public BaseObserver2() {
        this(true);
    }

    public BaseObserver2(boolean isShowTips) {
        this.isShowTips = isShowTips;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(HttpResult<T> result) {
        if (HttpHelper.isHttpSuccess(result)) {
            onSuccess(result.getData());
        } else {
//            onFaild(result.getMsg_code(), result.getTocon());
            onFaild(result);
        }

    }

    public void onFaild(HttpResult<T> result) {
//        onFaild(result.getMsg_code(), result.getTocon());
        onFaild(result.getCode(),result.getMsg());
    }

    /**
     * 复写该方法来处理业务请求出错
     */
    public void onFaild(int code, String message) {
        if(isShowTips)
        Toast.makeText(App.applicationContext, message, Toast.LENGTH_SHORT).show();
    }

    protected abstract void onSuccess(T t);

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
        onFaild(RESPONSE_CODE_FAILED, HttpHelper.handleException(t));

    }

    @Override
    public void onComplete() {

    }
}
