package com.tg.tgt.http;


import android.util.Log;

import com.tg.tgt.App;
import com.tg.tgt.http.interceptor.AddTokenInterceptor;
import com.tg.tgt.http.interceptor.HttpLoggingInterceptor;
import com.tg.tgt.http.interceptor.LanguageInterceptor;
import com.tg.tgt.http.interceptor.LoginInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * @author yiyang
 */
public class ApiManger2 {
    private final Retrofit retrofit;
    private final ApiService2 service;

    private ApiManger2(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i("Okhttp", message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        File cacheFile = new File(Utils.getContext().getCacheDir(), "cache");
//        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(ApiService2.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(ApiService2.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoginInterceptor())
                .addInterceptor(new AddTokenInterceptor(App.applicationContext))
                .addInterceptor(new LanguageInterceptor(App.applicationContext))
                .addInterceptor(interceptor)
//                .addNetworkInterceptor(new HttpCacheInterceptor())
//                .cache(cache)
                .build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(ApiService2.BASE_URL)
                .build();

        service = retrofit.create(ApiService2.class);
    }

    //  创建单例
    private static class SingletonHolder {
        private static final ApiManger2 INSTANCE = new ApiManger2();
    }
    public static ApiService2 getApiService() {

        return SingletonHolder.INSTANCE.service;
    }

//    class HttpCacheInterceptor implements Interceptor {
//
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Request request = chain.request();
//            if (!NetworkUtils.isConnected()) {  //没网强制从缓存读取
//                request = request.newBuilder()
//                        .cacheControl(CacheControl.FORCE_CACHE)
//                        .build();
//                LogUtils.d("Okhttp", "no network");
//            }
//
//
//            Response originalResponse = chain.proceed(request);
//            if (NetworkUtils.isConnected()) {
//                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
//                String cacheControl = request.cacheControl().toString();
//                return originalResponse.newBuilder()
//                        .header("Cache-Control", cacheControl)
//                        .removeHeader("Pragma")
//                        .build();
//            } else {
//                return originalResponse.newBuilder()
//                        .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
//                        .removeHeader("Pragma")
//                        .build();
//            }
//        }
//    }

}
