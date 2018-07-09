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

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.ImageUtils;
import com.hyphenate.easeui.utils.L;
import com.hyphenate.easeui.utils.SpUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.DemoHelper;
import com.tg.tgt.R;
import com.tg.tgt.db.DemoDBManager;
import com.tg.tgt.helper.DBManager;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.ApiService2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.RxUtils;
import com.tg.tgt.http.interceptor.AddTokenInterceptor;
import com.tg.tgt.http.model2.LoginModel;
import com.tg.tgt.http.model2.NonceBean;
import com.tg.tgt.parse.ParseManager;
import com.tg.tgt.utils.CodeUtils;
import com.tg.tgt.utils.RSAHandlePwdUtil;
import com.tg.tgt.utils.SharedPreStorageMgr;
import com.tg.tgt.utils.ToastUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import com.hyphenate.easeui.GlideApp;
/**
 * Login screen
 *
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    public static final int REQUEST_CODE_SETNICK = 1;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText codeEditText;

    private boolean progressShow;
    private boolean autoLogin = false;
    private Button mGetCodeBtn;
    private ImageView mPwdTypeIv;
    private ImageView mHeadIv;
    private TextView mNickNameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // enter the main activity if already logged in
        if (DemoHelper.getInstance().isLoggedIn()) {
            autoLogin = true;
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            return;
        }
        setContentView(R.layout.em_activity_login);

        initSpinner();
        mImmersionBar
                .statusBarColor(com.hyphenate.easeui.R.color.white)
                .statusBarDarkFont(true, 0.5f)
                .init();
        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        codeEditText = (EditText) findViewById(R.id.code_et);
        //usernameEditText.setFilters(new InputFilter[]{CodeUtils.filter});
        passwordEditText.setFilters(new InputFilter[]{CodeUtils.fil});
        //codeEditText.setFilters(new InputFilter[]{CodeUtils.filter});
        mGetCodeBtn = (Button) findViewById(R.id.get_code_btn);
        mPwdTypeIv = (ImageView) findViewById(R.id.password_type_iv);
        mHeadIv = (ImageView) findViewById(R.id.head_iv);
        mNickNameTv = (TextView) findViewById(R.id.nickname_tv);
        mGetCodeBtn.setOnClickListener(this);
        mPwdTypeIv.setOnClickListener(this);
        setFocus(usernameEditText);
        setFocus(passwordEditText);
        setFocus(codeEditText);
        // if user changed, clear the password
        RxTextView.textChanges(usernameEditText)
                .debounce(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(@NonNull CharSequence charSequence) throws Exception {
                        passwordEditText.setText(null);
                        String str = SharedPreStorageMgr.getIntance().getStringValue(LoginActivity.this,
                                charSequence.toString());
                        Log.i("dcz_",str+"");
                        if(!TextUtils.isEmpty(str)){
                            try {
                                String url = str.substring(0, str.lastIndexOf("-"));
                                String nickname = str.substring(str.lastIndexOf("-")+1);
//                            Glide.with(LoginActivity.this).load(picture).placeholder(R.drawable.youhead).into(mHeadIv);
                                Log.e("Tag",url);
                                ImageUtils.show(LoginActivity.this, SharedPreStorageMgr.getIntance().getStringValue(App.applicationContext,
                                        Constant.HEADIMAGE), R.drawable.photo1, mHeadIv);
                              //  ImageUtils.show(LoginActivity.this, url, R.drawable.youhead, mHeadIv);
                                mNickNameTv.setText(nickname);
                            } catch (Exception e) {
                                e.printStackTrace();
                         //       ImageUtils.show(LoginActivity.this, url, R.drawable.youhead, mHeadIv);
                                Glide.with(LoginActivity.this).load(R.drawable.youhead).into(mHeadIv);
                                mNickNameTv.setText(R.string.login);
                            }
                        }else {
//                            boolean drawableEqauls = ImageUtils.isDrawableEqauls(LoginActivity.this, mHeadIv, R
//                                    .drawable.youhead);
//                            if(!drawableEqauls) {
                                Glide.with(LoginActivity.this).load(R.drawable.youhead).into(mHeadIv);
                                mNickNameTv.setText(R.string.login);
//                            }
                        }
                    }
                });

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) &&
                        (event.getAction() == KeyEvent.ACTION_DOWN))) {
                    login(null);
                    return true;
                } else {
                    return false;
                }
            }
        });

        /*if (DemoHelper.getInstance().getCurrentUsernName() != null) {
            usernameEditText.setText(DemoHelper.getInstance().getCurrentUsernName());
        }*/
        usernameEditText.setText(SpUtils.get(mContext, Constant.NOT_CLEAR_SP, Constant.USERNAME, ""));
        usernameEditText.setSelection(usernameEditText.getText().length());
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

    private String mEmailLast = "+86";
    private android.widget.Spinner emailspinner;
    private void initSpinner() {
        this.emailspinner = (Spinner) findViewById(R.id.email_spinner);
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

    /**
     * login
     *
     * @param view
     */
    public void login(View view) {

        if (!EaseCommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }
        final String currentUsername = usernameEditText.getText().toString().trim();
        final String currentPassword = passwordEditText.getText().toString().trim();
        final String code = codeEditText.getText().toString().trim();

        if (TextUtils.isEmpty(currentUsername)) {
            usernameEditText.requestFocus();
            Toast.makeText(this, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(currentPassword)) {
            passwordEditText.requestFocus();
            Toast.makeText(this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(code)){
            codeEditText.requestFocus();
            Toast.makeText(this, R.string.code_cannot_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(getString(R.string.Is_landing));
        mProgressDialog.setCancelable(false);

        progressShow = true;

        ApiManger2.getApiService()
                .servernonce(Constant.MYUID)
                .compose(this.<HttpResult<NonceBean>>bindToLifeCyclerAndApplySchedulers(null))
                .subscribe(new BaseObserver2<NonceBean>() {
                    @Override
                    protected void onSuccess(NonceBean emptyData) {
                        Log.i("dcz",emptyData.getValue());
                        Login(currentUsername,RSAHandlePwdUtil.jia(currentPassword+"#"+emptyData.getValue()),code,emptyData.getKey());
                    }
                });

    }

    private void Login(final String currentUsername, String currentPassword, String code,String nonce){
        ApiManger2.getApiService().login(currentUsername, currentPassword, code,nonce)
                .compose(this.<HttpResult<LoginModel>>bindToLifeCyclerAndApplySchedulers(null,false))
                .subscribe(new BaseObserver2<LoginModel>() {
                    @Override
                    protected void onSuccess(final LoginModel loginResult) {
                        SpUtils.put(mContext, Constant.NOT_CLEAR_SP, Constant.USERNAME, currentUsername);

                        final String password = loginResult.getEasemob();

                        SpUtils.put(mContext, AddTokenInterceptor.TOKEN, loginResult.getToken());
                        SpUtils.put(mContext, AddTokenInterceptor.REFRESH_TOKEN, loginResult.getRefreshToken());

                        Map<String, String> map = new HashMap<String, String>();

                        map.put(Constant.MYUID, String.valueOf(loginResult.getUserId()));
                        map.put(Constant.SEX, loginResult.getSex());
                        map.put(Constant.NICKNAME, loginResult.getNickname());
                        map.put(Constant.STATE, loginResult.getSignature());
                        map.put(Constant.HEADIMAGE, loginResult.getPicture());
                        map.put(Constant.USERNAME, loginResult.getUserId());
//                        map.put(Constant.PWD, currentPassword);
                        map.put(Constant.EMAIL_LAST, mEmailLast);
                        map.put(Constant.INFOCODE, loginResult.getSafePassword());
                        map.put(Constant.QR, loginResult.getQrCode());
                        map.put(Constant.ADDRESS, loginResult.getAddress());
                        map.put(Constant.SN, loginResult.getSn());

                        SharedPreStorageMgr.getIntance()
                                .saveStringValueMap(LoginActivity.this, map);

                        //保存登录时要显示的图片
                        Log.e("Tag","userName="+currentUsername+"nick=="+loginResult.getNickname());
                        Constant.User_Nick = loginResult.getNickname();
                        Constant.User_Phone = currentUsername;
                        SharedPreStorageMgr.getIntance().saveStringValue(LoginActivity.this,"user_phone_zww",Constant.User_Phone);
                        SharedPreStorageMgr.getIntance().saveStringValue(LoginActivity.this,"user_nick_zww",Constant.User_Nick);
                        SharedPreStorageMgr.getIntance().saveStringValue(LoginActivity.this, currentUsername, loginResult.getPicture()+"-"+loginResult.getNickname());

                        ParseManager.getInstance().getContactInfos(null, new EMValueCallBack<List<EaseUser>>() {
                            @Override
                            public void onSuccess(List<EaseUser> easeUsers) {
                                DemoHelper.getInstance().setCurrentUserName(currentUsername);
                                DemoHelper.getInstance().updateContactList(easeUsers);
                                loginHX(loginResult.getUserId(), password);
                            }

                            @Override
                            public void onError(int i, String s) {
                                dismissProgress();
                                ToastUtils.showToast(getApplicationContext(), s);
                            }
                        });

                    }

                    @Override
                    public void onFaild(int code, String message) {
                        super.onFaild(code, message);
                        SpUtils.put(mContext, AddTokenInterceptor.TOKEN, "");
                        dismissProgress();
                    }
                });
    }

    private void loginHX(String currentUsername, String currentPassword) {
        // After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
        // close it before login to make sure DemoDB not overlap
        DemoDBManager.getInstance().closeDB();
        DBManager.getInstance().closeDB();

        // reset current user name before login
        DemoHelper.getInstance().setCurrentUserName(currentUsername);

        // call login method
        Log.d(TAG, "EMClient.getInstance().login");
        EMClient.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "login: onSuccess");

                Observable.fromCallable(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        // ** manually load all local groups and conversation
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();

                        // update current user's display name for APNs
                        boolean updatenick = EMClient.getInstance().pushManager().updatePushNickname(
                                App.currentUserNick.trim());
                        if (!updatenick) {
                            Log.e("LoginActivity", "update current user nick fail");
                        }
                        // get user's info (this should be get from App's server or 3rd party service)
                        DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();
                        return this;
                    }
                }).compose(RxUtils.applySchedulers())
                        .doOnTerminate(new Action() {
                            @Override
                            public void run() throws Exception {
                                dismissProgress();
                            }
                        })
                        .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);

                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        DemoHelper.getInstance().logout(false, null);
                    }
                });

            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "login: onProgress");
            }

            @Override
            public void onError(final int code, final String message) {
                Log.d(TAG, "login: onError: " + code);
                if (mProgressDialog != null && !mProgressDialog.isShowing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
//						pd.dismiss();
                        dismissProgress();
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    /**
     * register
     *
     * @param view
     */
    public void register(View view) {
        startActivityForResult(new Intent(this, RegisterActivity.class), 0);
    }

    public void forgetPwd(View view){
        startActivity(new Intent(LoginActivity.this, ForgetPwdAct.class));
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (autoLogin) {
            return;
        }
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_code_btn:
                getNonce();
               /* try {
                    RSAHandlePwdUtil.ma();
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                break;
            case R.id.password_type_iv:
                changePwdType();
                break;
        }
    }

    public void getNonce() {
        ApiManger2.getApiService()
                .servernonce(Constant.MYUID)
                .compose(this.<HttpResult<NonceBean>>bindToLifeCyclerAndApplySchedulers(null))
                .subscribe(new BaseObserver2<NonceBean>() {
                    @Override
                    protected void onSuccess(NonceBean emptyData) {
                        Log.i("dcz",emptyData.getValue());
                        getcode(emptyData);
                    }
                });
    }

    public void getcode(NonceBean emptyData) {
        String email = usernameEditText.getText().toString();
        String mima = passwordEditText.getText().toString();
        if(TextUtils.isEmpty(email)){
            ToastUtils.showToast(App.applicationContext, R.string.input_email);
            usernameEditText.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(mima)){
            ToastUtils.showToast(App.applicationContext, R.string.login_editpassword);
            usernameEditText.requestFocus();
            return;
        }
        mima=RSAHandlePwdUtil.jia(mima+"#"+emptyData.getValue());
//        String code = codeet.getText().toString();
//        if (TextUtils.isEmpty(code)) {
//            ToastUtils.showToast(App.applicationContext, R.string.input_code);
//            return;
//        }

        /*ApiManger.getApiService()
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
                .sendLoginSms(usernameEditText.getText().toString(),mima,emptyData.getKey())
                .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<EmptyData>() {
                    @Override
                    protected void onSuccess(EmptyData emptyData) {
                        count();
                        CodeUtils.showToEmailDialog(mActivity);
                    }
                });
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
                        LoginActivity.this.d = d;
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
    protected void onDestroy() {
        if(d != null && !d.isDisposed()){
            d.dispose();
        }
        super.onDestroy();

    }


    //以下控制密码显示 true表示隐藏
    private boolean pwTypeFlag = true;
    public void changePwdType(){

        if (pwTypeFlag) {
            mPwdTypeIv.setImageDrawable(getResources().getDrawable(R.drawable.eye_open));
            passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            pwTypeFlag = false;
        }else{
            mPwdTypeIv.setImageDrawable(getResources().getDrawable(R.drawable.eye_close));
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            pwTypeFlag = true;
        }
        //切换后将EditText光标置于尾部
        CharSequence charSequence = passwordEditText.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
    }
}
