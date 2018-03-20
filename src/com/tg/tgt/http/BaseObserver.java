package com.tg.tgt.http;

import android.net.ParseException;
import android.support.annotation.CallSuper;
import android.support.annotation.StringRes;

import com.google.gson.JsonParseException;
import com.tg.tgt.R;

import org.json.JSONException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 *
 * @author yiyang
 */
public abstract class BaseObserver<T extends BaseHttpResult> implements Observer<T> {

    private static final int SHOW_MSG_CODE = 0;
    private static final int SUCCESS_CODE = 1;//自定义的业务逻辑，成功返回数据
    private static final int RESPONSE_CODE_FAILED = -1;  //返回数据失败,严重的错误

    private final IView mReact;
    private final boolean isShowLoading;

    //  Activity 是否在执行onStop()时取消订阅
    private boolean isAddInStop = false;
    private Disposable mDisposable;


    public BaseObserver(IView react) {
        this(react, true);
    }

    public BaseObserver(IView mReact, boolean isShowLoading) {
        this.mReact = mReact;
        this.isShowLoading = isShowLoading;
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (null != mReact) {
            mDisposable = d;
            if (isAddInStop) {    //  在onStop中取消订阅
                mReact.addRxStop(mDisposable);
            } else { //  在onDestroy中取消订阅
                mReact.addRxDestroy(d);
            }
            if (isShowLoading) {
                mReact.showProgress(R.string.loading);
            }
        }
    }

    @Override
    public void onNext(T result) {
        if (null != mReact && isShowLoading) {
            mReact.dismissProgress();
        }
//        if(result.getCode() == SUCCESS_CODE){
//            T t = result.getContent();
//            onSuccess(t);
//        }else {
//            onFaild(result.getCode(), result.getMessage());
//        }
        if (SUCCESS_CODE == result.getMsg_code()) {
            onSuccess(result);
        } else {
//            onFaild(result.getMsg_code(), result.getTocon());
            onFaild(result);
        }

    }

    @CallSuper
    public void onFaild(T result) {
        onFaild(result.getMsg_code(), result.getTocon());
    }

    /**
     * 复写该方法来处理业务请求出错
     */
    @CallSuper//if overwrite,you should let it run.
    public void onFaild(int code, String message) {
        //
        if (null == mReact)
            return;

        if(!isShowLoading)
            return;

        if (code == RESPONSE_CODE_FAILED) {
                mReact.showTips(message);
        } else /*if(code == SHOW_MSG_CODE)*/{
            //TODO 对一些通用的错误code进行处理
            mReact.showTips(message);
        }
    }

    /**
     * 复写该方法来处理网络请求出错
     */
    @CallSuper//if overwrite,you should let it run.
    public void onFaild(int code, @StringRes int message) {
        if (null == mReact)
            return;

        if(!isShowLoading)
            return;

        if (code == RESPONSE_CODE_FAILED) {
                mReact.showTips(message);
        } else {
            //TODO 对一些通用的错误code进行处理
        }
    }

    protected abstract void onSuccess(T t);


    @Override
    public void onError(Throwable t) {
        if (null != mReact && isShowLoading) {
            mReact.dismissProgress();
        }
        int errorCode = RESPONSE_CODE_FAILED;
        int errorMsg = R.string.default_net_failed;
        if (t instanceof HttpException) {
            HttpException httpException = (HttpException) t;
//            errorCode = httpException.code();
//            errorMsg = httpException.getMessage();
//            getErrorMsg(httpException);

            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = R.string.http_net_failed;
        } else if (t instanceof SocketTimeoutException) {  //VPN open
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = R.string.socket_timeout_net_failed;
        } else if (t instanceof ConnectException) {
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = R.string.connect_net_failed;
//            errorMsg = R.string.the_current_network;
        } else if (t instanceof UnknownHostException) {
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = R.string.unknownhost__net_failed;
        } else if (t instanceof UnknownServiceException) {
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = R.string.unknownservice_net_failed;
        } else if (t instanceof IOException) {  //飞行模式等
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = R.string.io_net_failed;
        } else if (t instanceof RuntimeException) {
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = R.string.runtime_net_failed;
        } else if (t instanceof JsonParseException
                || t instanceof JSONException
                || t instanceof ParseException) {   //  解析错误
            errorCode = RESPONSE_CODE_FAILED;
            errorMsg = R.string.parse_net_failed;
        }
        onFaild(errorCode, errorMsg);

    }

    @Override
    public void onComplete() {

    }


    /**
     * 请求异常
     *
     * @param reason
     */
    public void onException(ExceptionReason reason) {
        switch (reason) {
            case CONNECT_ERROR:
                break;

            case CONNECT_TIMEOUT:
                break;

            case BAD_NETWORK:
                break;

            case PARSE_ERROR:
                break;

            case UNKNOWN_ERROR:
            default:
                break;
        }
    }

    /**
     * 请求网络失败原因
     */
    public enum ExceptionReason {
        /**
         * 解析数据失败
         */
        PARSE_ERROR,
        /**
         * 网络问题
         */
        BAD_NETWORK,
        /**
         * 连接错误
         */
        CONNECT_ERROR,
        /**
         * 连接超时
         */
        CONNECT_TIMEOUT,
        /**
         * 未知错误
         */
        UNKNOWN_ERROR,
    }
}
