package com.tg.tgt.http.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * @author yiyang
 */
public class PostJsonInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        Request requst = builder.addHeader("Content-type", "application/json").build();
        return chain.proceed(requst);
    }
}
