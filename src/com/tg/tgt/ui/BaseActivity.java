/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tg.tgt.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowVoicePlayClickListener;
import com.tg.tgt.ActMgrs;
import com.tg.tgt.App;
import com.tg.tgt.R;
import com.tg.tgt.http.IView;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("Registered")
public class BaseActivity extends EaseBaseActivity implements IView {


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("订2",ActMgrs.getActManager().currentActivity()+"");
        if (EaseChatRowVoicePlayClickListener.currentPlayListener != null && EaseChatRowVoicePlayClickListener.isPlaying) {
            if(ActMgrs.getActManager().currentActivity()instanceof ChatActivity){
            }else {
                EaseChatRowVoicePlayClickListener.currentPlayListener.stopPlayVoice();
            }
        }
        // umeng
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // umeng
//        MobclickAgent.onPause(this);
    }

    protected ProgressDialog mProgressDialog;

    @Override
    public boolean addRxStop(Disposable disposable) {
//        if (disposables2Stop == null) {
//            throw new IllegalStateException(
//                    "addUtilStop should be called between onStart and onStop");
//        }
//        disposables2Stop.add(disposable);
        return false;
    }

    @Override
    public boolean addRxDestroy(Disposable disposable) {
        if (disposables2Destroy == null) {
            throw new IllegalStateException(
                    "addUtilDestroy should be called between onCreate and onDestroy");
        }
        disposables2Destroy.add(disposable);
        return false;
    }

    @Override
    public void remove(Disposable disposable) {
        if (disposables2Stop == null && disposables2Destroy == null) {
            throw new IllegalStateException("remove should not be called after onDestroy");
        }
        if (disposables2Stop != null) {
            disposables2Stop.remove(disposable);
        }
        if (disposables2Destroy != null) {
            disposables2Destroy.remove(disposable);
        }
    }

    @Override
    public void showProgress(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, null, msg);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    clearDisposabl();
                }

            });
        } else {
            mProgressDialog.setMessage(msg);
            if (!mProgressDialog.isShowing())
                mProgressDialog.show();
        }
    }

    @Override
    public void showProgress(@StringRes int msg) {
        showProgress(getString(msg));
    }

    @Override
    public void dismissProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public void showTips(String errorMsg) {
        if (!TextUtils.isEmpty(errorMsg))
            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showTips(@StringRes int errorMsg) {
        Toast.makeText(getApplicationContext(), getResources().getString(errorMsg), Toast.LENGTH_SHORT).show();
    }

    private CompositeDisposable disposables2Stop;// 管理Stop取消订阅者者
    private CompositeDisposable disposables2Destroy;// 管理Destroy取消订阅者者

    @Override
    public void clearDisposabl() {
        if (disposables2Stop != null) {
            disposables2Stop.clear();
        }
        if (disposables2Destroy != null) {
            disposables2Destroy.clear();
        }
    }

    protected BaseActivity mActivity;
    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        ActMgrs.getActManager().pushActivity(this);

        if (disposables2Destroy != null) {
            throw new IllegalStateException("onCreate called multiple times");
        }
        disposables2Destroy = new CompositeDisposable();

        mActivity = this;
        mContext = App.applicationContext;
        super.onCreate(savedInstanceState);

        //设置titlebarLeft点击后退
//        setTitleBarLeftBack();
    }

    @Override
    protected void onStart() {
        if (disposables2Stop != null) {
            throw new IllegalStateException("onStart called multiple times");
        }
        disposables2Stop = new CompositeDisposable();
        super.onStart();

    }

    protected void onStop() {
        super.onStop();
        if (disposables2Stop == null) {
            throw new IllegalStateException("onStop called multiple times or onStart not called");
        }
        disposables2Stop.dispose();
        disposables2Stop = null;
    }

    protected void onDestroy() {
        super.onDestroy();
        if (disposables2Destroy == null) {
            throw new IllegalStateException(
                    "onDestroy called multiple times or onCreate not called");
        }
        disposables2Destroy.dispose();
        disposables2Destroy = null;
    }

    @Override
    public void finish() {
        super.finish();
        ActMgrs.getActManager().popActivityWithoutFinish(this);
    }

    /**
     * 设置titlebarLeft点击后退
     */
    public void setTitleBarLeftBack(){
        try {
            ((EaseTitleBar)findViewById(R.id.title_bar)).setLeftLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    hideKeyBoard();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public <T> ObservableTransformer<T, T> bindToLifeCyclerAndApplySchedulers(){
        return bindToLifeCyclerAndApplySchedulers(getString(R.string.loading), true);
    }
    public <T> ObservableTransformer<T, T> bindToLifeCyclerAndApplySchedulers(boolean cancel){
        return bindToLifeCyclerAndApplySchedulers(getString(R.string.loading), cancel);
    }
    public <T> ObservableTransformer<T, T> bindToLifeCyclerAndApplySchedulers(String loading){
        return bindToLifeCyclerAndApplySchedulers(loading, true);
    }

    /**
     * 处理rx生命周期以及dialog的显示
     * @param progress 如果不需要加载框则传null，或者传进需要显示的文字
     */
    @Override
    public <T> ObservableTransformer<T, T> bindToLifeCyclerAndApplySchedulers(final String progress, final boolean canCancel) {
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
                                addRxDestroy(disposable);
                                    if (!TextUtils.isEmpty(progress)) {
                                        showProgress(progress);
                                        mProgressDialog.setCancelable(canCancel);
                                    }
                            }
                        })
                        .doOnTerminate(new Action() {
                            @Override
                            public void run() throws Exception {
                                    if (!TextUtils.isEmpty(progress))
                                        dismissProgress();
                            }
                        })
                        ;
            }
        };
    }
}
