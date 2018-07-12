package com.tg.tgt.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import com.chiclam.android.updater.Updater;
import com.chiclam.android.updater.UpdaterConfig;
import com.tg.tgt.BuildConfig;
import com.tg.tgt.R;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.ApiService2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.model2.VerModel;
import com.tg.tgt.ui.BaseActivity;

import io.reactivex.functions.Consumer;

/**
 *
 * @author yiyang
 */
public class VerUtils {
    public static String getVer(Context ctx) {
        return String.valueOf(getPi(ctx).versionName);
    }

    public static PackageInfo getPi(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(ctx.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }// getPackageName表示获取当前ctx所在的包的名称，0表示获取版本信息
        return pi;
    }
    public static void check(final BaseActivity mActivity, final boolean isForeground) {
        check(mActivity, isForeground, null);
    }
    public static void check(final BaseActivity mActivity, final boolean isForeground, final Consumer<Boolean> consumer) {
        ApiManger2.getApiService()
                .ver("2")
                .compose(mActivity.<HttpResult<VerModel>>bindToLifeCyclerAndApplySchedulers(isForeground?mActivity.getString(R.string.loading):null))
                .subscribe(new BaseObserver2<VerModel>() {
                    @Override
                    protected void onSuccess(final VerModel model) {
                        if(model == null){
                            if(isForeground)
                            ToastUtils.showToast(mActivity.getApplicationContext(), R.string.not_update);
                            return;
                        }
                        int i = model.getVersion().compareTo(getVer(mActivity));
                        if (i <= 0) {
                            if(isForeground)
                                ToastUtils.showToast(mActivity.getApplicationContext(), R.string.not_update);
                            return;
                        }
                        if(consumer != null){
                            try {
                                consumer.accept(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                        if (model.getIsUpdate() == 0) {
                            new AlertDialog.Builder(mActivity)
                                    .setTitle(R.string.need_to_update)
                                    .setMessage(mActivity.getResources().getConfiguration().locale.getLanguage().contains("zh") ? model.getContent() + "\n大小:" + model.getSize() : model.getContentEn() + "\nsize:" + model.getSize())
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //update(mActivity, model.getUrl(), model.getVersion());
                                            Intent intent = new Intent();
                                            intent.setAction("android.intent.action.VIEW");
                                            Uri content_url = Uri.parse(ApiService2.downUrl);
                                            intent.setData(content_url);
                                            mActivity.startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }else {
                            new AlertDialog.Builder(mActivity)
                                    .setTitle(R.string.need_to_update)
                                    .setMessage(mActivity.getResources().getConfiguration().locale.getLanguage().contains("zh") ? model.getContent() + "\n大小:" + model.getSize() : model.getContentEn() + "\nsize:" + model.getSize())
                                    .setCancelable(true)
                                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //update(mActivity, model.getUrl(), model.getVersion());
                                            Intent intent = new Intent();
                                            intent.setAction("android.intent.action.VIEW");
                                            Uri content_url = Uri.parse(ApiService2.downUrl);
                                            intent.setData(content_url);
                                            mActivity.startActivity(intent);
                                        }
                                    })
                                    .show();
                        }
                    }
                });
    }

    public static void update(Activity activity, String url, String ver) {
        UpdaterConfig config = new UpdaterConfig.Builder(activity)
                .setTitle(activity.getResources().getString(R.string.app_name))
                .setDescription(activity.getString(R.string.system_download_description))
                .setFilename(String.format("Timely_v%s.apk", ver))
                .setFileUrl(url)
                .setCanMediaScanner(true)
                .build();
        Updater.get().showLog(!BuildConfig.DEBUG).download(config);

    }
}
