package com.tg.tgt.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.easeui.utils.SpUtils;
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

public class VerifySafeCodeAct extends BaseActivity implements View.OnClickListener {

    private EditText mEditText;
    private Button mGetCodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_safe_code);
        setTitleBarLeftBack();
        mEditText = (EditText) findViewById(R.id.code_et);
        mGetCodeBtn = (Button) findViewById(R.id.get_code_btn);
        findViewById(R.id.next_btn).setOnClickListener(this);
        findViewById(R.id.get_code_btn).setOnClickListener(this);
    }
    private static final int REQUEST_RESET = 0x86;
    @Override
    public void onClick(View v) {
        String username = SpUtils.get(mContext, Constant.NOT_CLEAR_SP, Constant.USERNAME, "");
                String emailSuffix = SpUtils.get(mContext, Constant.EMAIL_LAST, "");
        switch (v.getId()) {
            case R.id.next_btn:
                String code = mEditText.getText().toString().trim();
                if(TextUtils.isEmpty(code)){
                    ToastUtils.showToast(getApplicationContext(), R.string.code_cannot_empty);
                    return;
                }
                ApiManger2.getApiService().verifyRestSafePwd(code)
                        .compose(this.<HttpResult<String>>bindToLifeCyclerAndApplySchedulers())
                        .subscribe(new BaseObserver2<String>() {
                            @Override
                            protected void onSuccess(String s) {
                                startActivityForResult(new Intent(mActivity, SecurityPasswordAct.class)
                                        .putExtra(Constant.CODE, s)
                                        , REQUEST_RESET);
                            }
                        });
                break;
            case R.id.get_code_btn:
                ApiManger2.getApiService().sendRestSafePwdSms(Constant.MYUID)
                        .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers(false))
                        .subscribe(new BaseObserver2<EmptyData>() {
                            @Override
                            protected void onSuccess(EmptyData emptyData) {
                                count();
                                CodeUtils.showToEmailDialog2(mActivity);
                            }
                        });
                break;

            default:
                break;
        }
    }
    /**
     * 倒计时秒数
     */
    private int second ;
    private Disposable d;
    private void count() {
        second = 60;
        Observable.interval(1, TimeUnit.SECONDS)
                .take(second + 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mGetCodeBtn.setEnabled(false);
                        VerifySafeCodeAct.this.d = d;
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        mGetCodeBtn.setText(""+(second -aLong)+"s");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mGetCodeBtn.setEnabled(true);
                        mGetCodeBtn.setText(R.string.get_code);
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            finish();
        }
    }
    @Override
    protected void onDestroy() {
        if(d != null && !d.isDisposed()){
            d.dispose();
        }
        super.onDestroy();
    }
}
