package com.tg.tgt.http.interceptor;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.hyphenate.easeui.utils.DeviceUtils;
import com.hyphenate.easeui.utils.L;
import com.hyphenate.easeui.utils.SpUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * @author yiyang
 */
public class AddTokenInterceptor implements Interceptor {
    private Context context;

    public static final String TOKEN = "token";
    public static final String REFRESH_TOKEN = "refresh_token";

    public AddTokenInterceptor(Context context) {
        super();
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (chain == null)
            L.i("http", "Addchain == null");
        final Request.Builder builder = chain.request().newBuilder();
        String token = SpUtils.get(context, TOKEN, "");
        String uuid = DeviceUtils.getUniqueId(context);
        Log.i("http", "AddTokenInterceptor:token---------------->" + token);
        Log.i("http", "AddTokenInterceptor:uuid----------------->" + uuid);
        if (!TextUtils.isEmpty(token))
            builder.addHeader(TOKEN, token);
        builder.addHeader("uuid", uuid);


        Response originalResponse = chain.proceed(builder.build());
        L.d("http", "originalResponse" + originalResponse.toString());
/*
        //版本更新接口返回的不保存
        if (originalResponse.request().url().toString().contains("version")) {
            return originalResponse;
        }
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                final StringBuffer cookieBuffer = new StringBuffer();
                Observable.just(originalResponse.headers("Set-Cookie"))
                        .map(new Function<List<String>, String>() {
                            @Override
                            public String apply(@NonNull List<String> cookies) throws Exception {
//                            String[] cookieArray = s.split(";");
//                            return cookieArray[0];
                                for (int i = 0; i < cookies.size(); i++) {
                                    if (cookies.get(i).contains("SESSION=")) {
                                        return cookies.get(i);
                                    }
                                }
                                throw new ApiException("");
                            }
                        })
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(@NonNull String cookie) throws Exception {
//                            cookieBuffer.append(cookie).append(";");
                                cookieBuffer.append(cookie);
                                SpUtils.put(context, TOKEN, cookieBuffer.toString());
                                L.i("http", "ReceivedCookiesInterceptor:" + cookieBuffer.toString());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {

                            }
                        });

        }*/
        return originalResponse;
    }
}
