package com.tg.tgt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.SpUtils;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.ResponseCode;
import com.tg.tgt.http.model2.NonceBean;
import com.tg.tgt.utils.RSAHandlePwdUtil;
import com.tg.tgt.utils.SharedPreStorageMgr;
import com.tg.tgt.utils.ToastUtils;
import com.tg.tgt.widget.lock.KurtEditText;

/**
 *
 * @author yiyang
 */
public class SecurityPasswordAct extends BaseActivity implements View.OnClickListener {
    private KurtEditText kurtedit;
    private TextView confirm;
    private String passwold;
    private String toChatUsername;
    private String mCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security_password);

        setTitleBarLeftBack();
        kurtedit = (KurtEditText) findViewById(R.id.kurtet);
        confirm = (TextView) findViewById(R.id.security_confirm);

        confirm.setOnClickListener(this);

        Intent intent = getIntent();
        //如果该Code不为空，则为重置密码
        mCode = intent.getStringExtra(Constant.CODE);
        toChatUsername = intent.getStringExtra("toChatUsername");
        kurtedit.setKurtListener(new KurtEditText.KurtListener() {

            @Override
            public void keyword(String str) {
                Log.e("NIRVANA", str);
                passwold = str;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.security_confirm:
                if (TextUtils.isEmpty(passwold) || passwold.length()<6) {
                    Toast.makeText(SecurityPasswordAct.this, R.string.input_right_six_pwd, Toast.LENGTH_SHORT).show();
                } else {
                    if(!TextUtils.isEmpty(mCode)){
                        ApiManger2.getApiService()
                                .servernonce(Constant.MYUID)
                                .compose(this.<HttpResult<NonceBean>>bindToLifeCyclerAndApplySchedulers(null))
                                .subscribe(new BaseObserver2<NonceBean>() {
                                    @Override
                                    protected void onSuccess(NonceBean emptyData) {
                                        Log.i("dcz",emptyData.getValue());
                                        resetPwd(RSAHandlePwdUtil.jia(passwold+"#"+emptyData.getValue()),emptyData.getKey());
                                    }
                                });
                        return;
                    }
                    //保存聊天密码,要在成功之后,进入该界面需要判断
                    updatePwd();
                }
                break;
        }

    }

    private void resetPwd(String passwold,String nonce) {
        String username = SpUtils.get(mContext, Constant.NOT_CLEAR_SP, Constant.USERNAME, "");
        //String emailSuffix = SpUtils.get(mContext, Constant.EMAIL_LAST, "");
        ApiManger2.getApiService().resetSafePassword(username, mCode,nonce, passwold)
                .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<EmptyData>() {
                    @Override
                    protected void onSuccess(EmptyData emptyData) {
                        ToastUtils.showToast(App.applicationContext, R.string.modify_success);
                        setResult(RESULT_OK);
                        exitThis();
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

    private void updatePwd() {
        if (!TextUtils.isEmpty(toChatUsername)) {
            //从聊天界面进来的
            /*ApiManger.getApiService()
                    .setInfocode(passwold, App.getMyUid(), toChatUsername)
                    .compose(RxUtils.<BaseHttpResult>applySchedulers())
                    .subscribe(new BaseObserver<BaseHttpResult>(this) {
                        @Override
                        protected void onSuccess(BaseHttpResult result) {
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            exitThis();
                        }
                    });*/
            showProgress(R.string.loading);
            mProgressDialog.setCancelable(false);
            ApiManger2.getApiService()
                    .servernonce(Constant.MYUID)
                    .compose(this.<HttpResult<NonceBean>>bindToLifeCyclerAndApplySchedulers(null))
                    .subscribe(new BaseObserver2<NonceBean>() {
                        @Override
                        protected void onSuccess(NonceBean emptyData) {
                            Log.i("dcz",emptyData.getValue());
                            setdata(RSAHandlePwdUtil.jia(passwold+"#"+emptyData.getValue()),emptyData.getKey());
                        }
                    });
        } else {
            ApiManger2.getApiService()
                    .servernonce(Constant.MYUID)
                    .compose(this.<HttpResult<NonceBean>>bindToLifeCyclerAndApplySchedulers(null))
                    .subscribe(new BaseObserver2<NonceBean>() {
                        @Override
                        protected void onSuccess(NonceBean emptyData) {
                            Log.i("dcz",emptyData.getValue());
                            setData(RSAHandlePwdUtil.jia(passwold+"#"+emptyData.getValue()),emptyData.getKey());
                        }
                    });
            //直接更新
            /*ApiManger.getApiService()
                    .updatecode(passwold, App.getMyUid())
                    .compose(RxUtils.<BaseHttpResult>applySchedulers())
                    .subscribe(new BaseObserver<BaseHttpResult>(this) {
                        @Override
                        protected void onSuccess(BaseHttpResult result) {
                            exitThis();
                        }
                    });*/
        }
    }

    private void setData(String passwold,String nonce){
        ApiManger2.getApiService()
                .safePassword(passwold,nonce)
                .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<EmptyData>() {
                    @Override
                    protected void onSuccess(EmptyData emptyData) {
                        ToastUtils.showToast(getApplicationContext(), R.string.setup_success);
                        exitThis();
                    }
                });
    }

    private void setdata(String passwold,String nonce){
        ApiManger2.getApiService()
                .safePassword(passwold,nonce)
                .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers(null))
                .subscribe(new BaseObserver2<EmptyData>() {
                    @Override
                    protected void onSuccess(EmptyData emptyData) {
                        ApiManger2.getApiService()
                                .modifySafe(true, EaseUserUtils.getUserInfo(toChatUsername).getChatid())
                                .retry(3)
                                .compose(mActivity.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers(null))
                                .subscribe(new BaseObserver2<EmptyData>() {
                                    @Override
                                    protected void onSuccess(EmptyData emptyData) {
                                        Intent intent = new Intent();
                                        setResult(RESULT_OK, intent);
                                        exitThis();
                                    }

                                    @Override
                                    public void onFaild(int code, String message) {
                                        super.onFaild(code, message);
                                        exitThis();
                                    }
                                });

                    }

                    @Override
                    public void onFaild(int code, String message) {
                        super.onFaild(code, message);
                        dismissProgress();
                    }
                });
    }

    private void exitThis() {
        dismissProgress();
        SharedPreStorageMgr.getIntance().saveStringValue(App.applicationContext, Constant.INFOCODE, passwold);
        //设置密码后，返回进来时界面
        onBackPressed();
    }
}
