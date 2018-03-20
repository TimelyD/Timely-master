package com.tg.tgt.http;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.tg.tgt.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * @author yiyang
 */
public class RxUtils {
    /**
     * 无进度Schedulers
     */
    public static <T> ObservableTransformer<T, T> applySchedulers() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }


    /**
     * 有进度Schedulers
     */
    public static <T> ObservableTransformer<T, T> applySchedulers(@NonNull final Dialog dialog) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream
                        .delay(1, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(@NonNull final Disposable disposable) throws Exception {
                                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        disposable.dispose();
                                    }
                                });
                                dialog.show();
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnTerminate(new Action() {
                            @Override
                            public void run() throws Exception {
                                dialog.dismiss();
                            }
                        });
            }
        };
    }
     public static <T> ObservableTransformer<T, T> applyRetry(@NonNull final Activity activity, final Consumer<Boolean> consumer) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream
                        .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(@NonNull final Observable<Throwable> throwableObservable) throws
                                    Exception {
                                //这里进行错误重试，错误超过3次，显示退出窗口
                                return throwableObservable.zipWith(Observable.range(1, 4), new BiFunction<Throwable, Integer, Integer>() {
                                    @Override
                                    public Integer apply(@NonNull Throwable throwable, @NonNull Integer integer) throws
                                            Exception {
                                        if(integer>3){
                                            EaseAlertDialog dialog = new EaseAlertDialog(activity, HttpHelper.handleException
                                                    (throwable), activity.getString(R.string.give_up), activity.getString(R.string.retry), new EaseAlertDialog.AlertDialogUser() {
                                                @Override
                                                public void onResult(boolean confirmed, Bundle bundle) {
                                                    try {
                                                        consumer.accept(confirmed);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            dialog.show();
                                            //这里必然是出现网络异常
                                            throw new ApiException("");
                                        }
                                        return integer;
                                    }
                                }).flatMap(new Function<Integer, ObservableSource<?>>() {
                                    @Override
                                    public ObservableSource<?> apply(@NonNull Integer integer) throws Exception {
                                        //在i*5秒后重试，暂时不需要
//								return Observable.timer((long) Math.pow(5,integer), TimeUnit.SECONDS);
                                        return Observable.timer(2, TimeUnit.SECONDS);
                                    }
                                });
                            }
                        });
            }
        };
    }


}
