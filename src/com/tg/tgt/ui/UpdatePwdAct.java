package com.tg.tgt.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.easeui.utils.PhoneUtil;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.jakewharton.rxbinding2.view.RxView;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.model2.NonceBean;
import com.tg.tgt.logger.Logger;
import com.tg.tgt.utils.CodeUtils;
import com.tg.tgt.utils.RSAHandlePwdUtil;
import com.tg.tgt.utils.ToastUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 *
 * @author yiyang
 */
public class UpdatePwdAct extends BaseActivity{
    private Button confirm;
    private android.widget.EditText etoldpwd;
    private android.widget.EditText etnewpwd;
    private android.widget.EditText etrepwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_update_pwd);
        this.confirm = (Button) findViewById(R.id.confirm);
        this.etrepwd = (EditText) findViewById(R.id.et_re_pwd);
        this.etnewpwd = (EditText) findViewById(R.id.et_new_pwd);
        this.etoldpwd = (EditText) findViewById(R.id.et_old_pwd);

        etrepwd.setFilters(new InputFilter[]{CodeUtils.filter});
        etnewpwd.setFilters(new InputFilter[]{CodeUtils.filter});
        etoldpwd.setFilters(new InputFilter[]{CodeUtils.filter});



        EaseTitleBar titleb = (EaseTitleBar) findViewById(R.id.title_bar);
        titleb.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        confirm.setOnClickListener(this);

        RxView.clicks(confirm)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        confirm();
                    }
                });


    }

    public void confirm() {
//        rePwdSuccess();
//        if(true)
//            return;
        final String oldPwd = etoldpwd.getText().toString();
//        String stringValue = SharedPreStorageMgr.getIntance().getStringValue(this, Constant.PWD);
//        Logger.d(oldPwd+":"+stringValue);
        String newPwd = etnewpwd.getText().toString();
        final String rePwd = etrepwd.getText().toString();
        if(TextUtils.isEmpty(oldPwd)){
            ToastUtils.showToast(getApplicationContext(), R.string.old_cannot_be_empty);
            return;
        }if(TextUtils.isEmpty(newPwd)){
            ToastUtils.showToast(getApplicationContext(), R.string.new_pwd_cannot_be_empty);
            return;
        }else if(TextUtils.isEmpty(rePwd)){
            ToastUtils.showToast(getApplicationContext(), R.string.Confirm_password_cannot_be_empty);
            return;
        }else if( newPwd.length()<6){
            ToastUtils.showToast(App.applicationContext, R.string.register_editpassword);
            return;
        }else if(!rePwd.equals(newPwd)){
            ToastUtils.showToast(App.applicationContext, R.string.re_pwd_wrong);
            return;
        }
        if(oldPwd.equals(newPwd)){
            ToastUtils.showToast(getApplicationContext(), R.string.ti);
            return;
        }
        ApiManger2.getApiService()
                .servernonce(Constant.MYUID)
                .compose(this.<HttpResult<NonceBean>>bindToLifeCyclerAndApplySchedulers(null))
                .subscribe(new BaseObserver2<NonceBean>() {
                    @Override
                    protected void onSuccess(NonceBean emptyData) {
                        Log.i("dcz",emptyData.getValue());
                        setdata(RSAHandlePwdUtil.jia(oldPwd+"#"+emptyData.getValue()),RSAHandlePwdUtil.jia(rePwd+"#"+emptyData.getValue()),emptyData.getKey());
                    }
                });

        /*ApiManger.getApiService()
                .rePwd(App.getMyUid(), rePwd)
                .compose(RxUtils.<BaseHttpResult>applySchedulers())
                .subscribe(new BaseObserver<BaseHttpResult>(this) {
                    @Override
                    protected void onSuccess(BaseHttpResult result) {
                        SharedPreStorageMgr.getIntance().saveStringValue(App.applicationContext,Constant.PWD, rePwd);
                        rePwdSuccess();
                    }
                });*/
    }

    private void setdata(String oldPwd,String rePwd,String nonce){
        ApiManger2.getApiService()
                .modifyPassword(oldPwd,rePwd,nonce)
                .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<EmptyData>() {
                    @Override
                    protected void onSuccess(EmptyData emptyData) {
                        rePwdSuccess();
                    }
                });
    }

    private void rePwdSuccess() {
        View successView = LayoutInflater.from(this).inflate(R.layout.dialog_update_pwd_success, null);
        final Dialog dialog = new Dialog(this, R.style.DialogTranslucentStyle);
        dialog.setContentView(successView, new ViewGroup.LayoutParams(/*(int) (PhoneUtil.getScreenWidth(this) * 0.82)*/ViewGroup.LayoutParams.WRAP_CONTENT, (int) (PhoneUtil.getScreenHeight(this) * 0.58)));
        dialog.setCancelable(false);

        //设置dialog位置
        Window window = dialog.getWindow();
        window.setGravity(Gravity.TOP);
//        WindowManager.LayoutParams attributes = window.getAttributes();
////        attributes.x = attributes.x + PhoneUtil.dp2px(this, 200);
//        attributes.x = 300;
//        window.setAttributes(attributes);

        dialog.show();

        final TextView backTimeTv = (TextView) successView.findViewById(R.id.back_time);

        successView.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        final int s = 3;
        Observable.interval(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .take(s)
                .subscribe(new Observer<Long>() {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
//                        clearDisposabl();
//                        addRxDestroy(d);
                        UpdatePwdAct.this.d = d;
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        Logger.d(aLong);
                        backTimeTv.setText(s-1-aLong.intValue()+"s");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onComplete() {
                        dialog.dismiss();
                        finish();
                    }
                });
//        addRxDestroy(Observable.interval(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
////                .delay(1, TimeUnit.SECONDS)
//                .take(s)
//                .doOnTerminate(new Action() {
//                    @Override
//                    public void run() throws Exception {
//                        dialog.dismiss();
//                        finish();
//                    }
//                })
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(@NonNull Long aLong) throws Exception {
//                        backTimeTv.setText(s-1-aLong.intValue()+"s");
//                    }
//                }));


    }

    public Disposable d;
    @Override
    protected void onDestroy() {
        if(d != null){
            d.dispose();
        }
        super.onDestroy();
    }
}
