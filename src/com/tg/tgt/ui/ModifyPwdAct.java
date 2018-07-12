package com.tg.tgt.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.easeui.widget.EaseTitleBar;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.ResponseCode;
import com.tg.tgt.http.model2.NonceBean;
import com.tg.tgt.utils.CodeUtils;
import com.tg.tgt.utils.RSAHandlePwdUtil;
import com.tg.tgt.utils.ToastUtils;

/**
 *
 * @author yiyang
 */
public class ModifyPwdAct extends BaseActivity implements View.OnClickListener {
    private com.hyphenate.easeui.widget.EaseTitleBar titlebar;
    private android.widget.TextView emailtv;
    private android.widget.EditText newpwdet;
    private android.widget.EditText repwdet;
    private android.widget.Button confirmbtn;
    private String mEmailLast="+86";
    private String mEmail;
    private String mCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_modify_pwd);
        this.confirmbtn = (Button) findViewById(R.id.confirm_btn);
        this.repwdet = (EditText) findViewById(R.id.re_pwd_et);
        this.newpwdet = (EditText) findViewById(R.id.new_pwd_et);
        this.emailtv = (TextView) findViewById(R.id.email_tv);
        this.titlebar = (EaseTitleBar) findViewById(R.id.title_bar);
        newpwdet.setFilters(new InputFilter[]{CodeUtils.fil});
        repwdet.setFilters(new InputFilter[]{CodeUtils.fil});
        titlebar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mEmail = getIntent().getStringExtra(Constant.EMAIL);
        mEmailLast = getIntent().getStringExtra(Constant.EMAIL_LAST);
        emailtv.setText(mEmail);

        mCode = getIntent().getStringExtra(Constant.CODE);

        confirmbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String newPwd = newpwdet.getText().toString();
        final String rePwd = repwdet.getText().toString();
        if(TextUtils.isEmpty(newPwd)){
            ToastUtils.showToast(getApplicationContext(), R.string.Password_cannot_be_empty);
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
        ApiManger2.getApiService()
                .servernonce(Constant.MYUID)
                .compose(this.<HttpResult<NonceBean>>bindToLifeCyclerAndApplySchedulers(null))
                .subscribe(new BaseObserver2<NonceBean>() {
                    @Override
                    protected void onSuccess(NonceBean emptyData) {
                        Log.i("dcz",emptyData.getValue());
                        getdata(emptyData,rePwd);
                    }
                });

        /*ApiManger.getApiService()
                .rePwdWithChatId(mEmail, rePwd)
                .compose(RxUtils.<BaseHttpResult>applySchedulers())
                .subscribe(new BaseObserver<BaseHttpResult>(this) {
                    @Override
                    protected void onSuccess(BaseHttpResult result) {
                        ToastUtils.showToast(App.applicationContext, R.string.modify_success);
                        finish();
                    }
                });*/

    }
    private void getdata(NonceBean emptyData,String rePwd){
        ApiManger2.getApiService()
                .resetPassword(mEmailLast,mEmail, mCode,emptyData.getKey(),RSAHandlePwdUtil.jia(rePwd+"#"+emptyData.getValue()))
                .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<EmptyData>() {
                    @Override
                    protected void onSuccess(EmptyData emptyData) {
                        ToastUtils.showToast(App.applicationContext, R.string.modify_success);
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFaild(int code, String message) {
                        super.onFaild(code, message);
                        if(ResponseCode.CODE_IS_ERROR.getCode() == code){
                            finish();
                        }
                    }
                });
    }
}
