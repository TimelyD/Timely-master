package com.tg.tgt.http.interceptor;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.easeui.utils.DeviceUtils;
import com.hyphenate.easeui.utils.L;
import com.hyphenate.easeui.utils.SpUtils;
import com.tg.tgt.ActMgrs;
import com.tg.tgt.App;
import com.tg.tgt.BuildConfig;
import com.tg.tgt.DemoHelper;
import com.tg.tgt.http.ApiService2;
import com.tg.tgt.http.HttpHelper;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.ResponseCode;
import com.tg.tgt.http.model2.Nonce2Bean;
import com.tg.tgt.http.model2.NonceBean;
import com.tg.tgt.http.model2.TokenModel;
import com.tg.tgt.ui.LoginActivity;
import com.tg.tgt.utils.RSAHandlePwdUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.tg.tgt.http.ApiService2.REFRESH_TOKEN_URL;

/**
 * 判断是否登录，做出相应操作
 * @author yiyang
 */
public class LoginInterceptor implements Interceptor {
    public static final String TAG = "LoginInterceptor";
    /**
     * 用来处理多次跳转到login请求
     */
    private final PublishSubject<HttpResult> mPublishSubject;
    private Gson mGson = new Gson();

    private Context mContext = App.applicationContext;
    public LoginInterceptor() {
        mPublishSubject = PublishSubject.create();
        mPublishSubject.debounce(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HttpResult>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull HttpResult result) throws Exception {
                        L.i("LoginInterceptor", "accept");

                    }
                });
    }
    private Request getNonce() {
        String uuid = DeviceUtils.getUniqueId(mContext);
        return  new Request.Builder()
                .url(ApiService2.BASE_URL+"api/servernonce")
                .header("uuid", uuid)
                .get()
                .build();
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        if (chain == null)
            L.i(TAG, "Addchain == null");
        Response response = chain.proceed(chain.request());

        MediaType mediaType = response.body().contentType();
        if (mediaType.toString().contains("image")) {
            return response;
        }
        String s = response.body().string();
        HttpResult result = mGson.fromJson(s, HttpResult.class);
        //这里替换成rxjava来写
        L.i(TAG, "intercept");


        synchronized (LoginInterceptor.class) {
            if (ResponseCode.TOKEN_IS_EXPIRED_OR_INVALID.getCode() == result.getCode()) {
                //如果是token过期则刷新
                Request loginRequest = getNonce();
                Response loginResponse = chain.proceed(loginRequest);
                String loginString = loginResponse.body().string();
                Nonce2Bean resultLogin = mGson.fromJson(loginString,Nonce2Bean.class);
                if(resultLogin.getCode().equals("0")){
                    SpUtils.put(mContext, AddTokenInterceptor.TOKEN, "");
                    String refresh_token = SpUtils.get(mContext, AddTokenInterceptor.REFRESH_TOKEN, "");
                    String mi = RSAHandlePwdUtil.jia(refresh_token + "#" + resultLogin.getData().getValue()).replace("\n","").trim();
                    Log.i("刷新token:",refresh_token+"哈哈哈"+mi);
                    Log.i("刷新nonce:",resultLogin.getData().getKey());
                    Request refresh_request = new Request.Builder()
                            .url(ApiService2.BASE_URL+REFRESH_TOKEN_URL)
                            .header("token",mi)
                            .post(new FormBody.Builder()
                                    .add("type","1")
                                    .add("nonce",resultLogin.getData().getKey())
                                    .build())
                            .build();
                    if(BuildConfig.IS_DEBUG){
                        toast("开始刷新token");
                    }
                    L.i(TAG, "开始刷新--------->"+refresh_request.toString());
                    Response refresh_response = chain.proceed(refresh_request);
                    HttpResult<TokenModel> tokenModelHttpResult = mGson.fromJson(refresh_response.body().string(), new TypeToken<HttpResult<TokenModel>>(){}.getType());
                    L.i(TAG, "刷新------------>"+tokenModelHttpResult.toString());
                    if (HttpHelper.isHttpSuccess(tokenModelHttpResult)) {
                        L.i(TAG, "刷新token成功");
                        //刷新token成功
                        TokenModel tokenModel = tokenModelHttpResult.getData();
                        //重新赋值
                        SpUtils.put(mContext, AddTokenInterceptor.TOKEN, tokenModel.getToken());
                        SpUtils.put(mContext, AddTokenInterceptor.REFRESH_TOKEN, tokenModel.getRefreshToken());
                        L.i(TAG, "处理之前请求------>"+chain.request().toString());
                        //继续之前的请求，将token替换成新得到的
                        response = chain.proceed(chain.request());
                        return response;
                    }else {
                        L.i(TAG, "刷新失败------>"+result.getMsg());
                        toast(result.getMsg());
                        toLogin();
                    }
                }
            }else if (ResponseCode.THE_SAME_ACCOUNT_WAS_LOGGEDIN_IN_OTHER_DEVICE.getCode() == result.getCode()
                    || ResponseCode.USER_NOT_LOGIN.getCode() == result.getCode()
                    || ResponseCode.TOKEN_IS_EMPTY.getCode() == result.getCode()) {
                L.i(TAG, "退出登录------>"+result.getMsg());
                toast(result.getMsg());
                toLogin();
            }
            response = response.newBuilder()
                    .body(ResponseBody.create(null, s))
                    .build();
        }
        return response;
    }

    private void toast(String msg) {
        Observable.just(msg)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void toLogin() {
        SpUtils.put(mContext, AddTokenInterceptor.TOKEN, "");
        SpUtils.put(mContext, AddTokenInterceptor.REFRESH_TOKEN, "");
        DemoHelper.getInstance().logout(false, null);
        ActMgrs.getActManager().popAllActivity();
        Intent intent = new Intent(App.applicationContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.applicationContext.startActivity(intent);
    }

}