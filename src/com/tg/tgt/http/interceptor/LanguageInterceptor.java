package com.tg.tgt.http.interceptor;

import android.content.Context;
import android.text.TextUtils;

import com.hyphenate.easeui.utils.L;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 添加语言标志
 * @author yiyang
 */
public class LanguageInterceptor implements Interceptor {
    public static final String ZH = "zh";
    public static final String EN = "en";
    private Context context;

    public LanguageInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (chain == null)
            L.i("http", "Addchain == null");
        final Request.Builder builder = chain.request().newBuilder();
        Observable.just(context.getResources().getConfiguration().locale.getLanguage().contains("zh")? ZH : EN)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String lang) throws Exception {
                        if (TextUtils.isEmpty(lang))
                            return;
                        //添加cookie
                        L.i("http", "AddLangInterceptor:" + lang);
                        builder.addHeader("Accept-Language", lang);
                    }
                });
        return chain.proceed(builder.build());
    }
}
