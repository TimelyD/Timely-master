package com.tg.tgt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.hyphenate.easeui.widget.EaseTitleBar;
import com.jakewharton.rxbinding2.view.RxView;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.ApiService2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.utils.CodeUtils;
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
public class ForgetPwdAct extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_RESET = 0x86;
    private com.hyphenate.easeui.widget.EaseTitleBar titlebar;
    private android.widget.EditText emailet;
    private android.widget.Spinner emailspinner;
    private android.widget.EditText codeet;
    private android.widget.Button getcodebtn;
    private Button nextbtn;

    private String mEmailLast = "+86";
    private Disposable d;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_forget_pwd);
        this.getcodebtn = (Button) findViewById(R.id.get_code_btn);
        this.codeet = (EditText) findViewById(R.id.code_et);
        this.emailspinner = (Spinner) findViewById(R.id.email_spinner);
        this.emailet = (EditText) findViewById(R.id.email_et);
        this.titlebar = (EaseTitleBar) findViewById(R.id.title_bar);
        this.nextbtn = (Button) findViewById(R.id.next_btn);

        //codeet.setFilters(new InputFilter[]{CodeUtils.filter});
        emailet.setFilters(new InputFilter[]{CodeUtils.fil});

        initSpinner();

        setFocus(emailet);
        setFocus(codeet);


        titlebar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getcodebtn.setOnClickListener(this);
        nextbtn.setOnClickListener(this);
    }

    private void setFocus(final View v) {
        RxView.focusChanges(v)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if(aBoolean){
                            ((View)v.getParent()).setSelected(true);
                        }else {
                            ((View)v.getParent()).setSelected(false);
                        }
                    }
                });
    }

    private void initSpinner() {
        final String[] strings = getResources().getStringArray(R.array.emails);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.emails, R
                .layout.spinner_item);

        emailspinner.setAdapter(adapter);
        emailspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mEmailLast = strings[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_code_btn:
                getCode();
                break;
            case R.id.next_btn:
                next();
                break;

        }

    }

    private void next() {
        final String email = emailet.getText().toString();
        if(TextUtils.isEmpty(email)){
            ToastUtils.showToast(App.applicationContext, R.string.input_email);
            return;
        }
        final String code = codeet.getText().toString();
        if (TextUtils.isEmpty(code)) {
            ToastUtils.showToast(App.applicationContext, R.string.input_code);
            return;
        }

        ApiManger2.getApiService()
                .verifyRestPwd(mEmailLast,code,email)
                .compose(this.<HttpResult<String>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<String>() {
                    @Override
                    protected void onSuccess(String emptyData) {
                        startActivityForResult(new Intent(ForgetPwdAct.this, ModifyPwdAct.class)
                                .putExtra(Constant.CODE, emptyData)
                                .putExtra(Constant.EMAIL, email)
                                .putExtra(Constant.EMAIL_LAST, mEmailLast), REQUEST_RESET);
                    }
                });
        /*//目前改为无需校验，直接跳转下一个界面
        startActivityForResult(new Intent(ForgetPwdAct.this, ModifyPwdAct.class)
                .putExtra(Constant.CODE, code)
                .putExtra(Constant.EMAIL, email)
                .putExtra(Constant.EMAIL_LAST, mEmailLast), REQUEST_RESET);*/
//        finish();
        /*ApiManger.getApiService()
                .isUser(email, code)
                .compose(RxUtils.<BaseHttpResult>applySchedulers())
                .subscribe(new BaseObserver<BaseHttpResult>(this) {
                    @Override
                    protected void onSuccess(BaseHttpResult result) {
                        //检验成功可以修改密码
                        startActivity(new Intent(ForgetPwdAct.this, ModifyPwdAct.class)
                                .putExtra(Constant.CODE, code)
                                .putExtra(Constant.EMAIL, email)
                                .putExtra(Constant.EMAIL_LAST, mEmailLast));
                        finish();
                    }
                });*/

//        startActivity(new Intent(this, ModifyPwdAct.class).putExtra(Constant.EMAIL, email+mEmailLast));

    }

    /**
     * 倒计时秒数
     */
    private int second ;

    private void getCode() {
//        count();
//        if(true)
//            return;
        String email = emailet.getText().toString();
        if(TextUtils.isEmpty(email)){
            ToastUtils.showToast(App.applicationContext, R.string.login_editusername);
            return;
        }
//        String code = codeet.getText().toString();
//        if (TextUtils.isEmpty(code)) {
//            ToastUtils.showToast(App.applicationContext, R.string.input_code);
//            return;
//        }

       /* ApiManger.getApiService()
                .getCode(email, mEmailLast)
                .compose(RxUtils.<BaseHttpResult>applySchedulers())
                .subscribe(new BaseObserver<BaseHttpResult>(this) {
                    @Override
                    protected void onSuccess(BaseHttpResult result) {
                        ToastUtils.showToast(App.applicationContext, result.getTocon());
                        count();
                    }
                });*/

        ApiManger2.getApiService()
                .sendRestPwdSms(mEmailLast,email)
                .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers(false))
                .subscribe(new BaseObserver2<EmptyData>() {
                    @Override
                    protected void onSuccess(EmptyData emptyData) {
//                        ToastUtils.showToast(App.applicationContext, result.getTocon());
                        count();
                        CodeUtils.showToEmailDialog(mActivity);
                    }
                });

    }

    private void count() {
        second = 60;
        Observable.interval(1, TimeUnit.SECONDS)
                .take(second + 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        getcodebtn.setEnabled(false);
                        ForgetPwdAct.this.d = d;
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        getcodebtn.setText(""+(second -aLong)+"s");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        getcodebtn.setEnabled(true);
                        getcodebtn.setText(R.string.get_code);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if(d != null && !d.isDisposed()){
            d.dispose();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            finish();
        }
    }
}
