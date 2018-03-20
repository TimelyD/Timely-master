package com.chiclam.android.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.chiclam.android.updater.Logger;
import com.chiclam.android.updater.UpdaterUtils;

import java.io.File;

public class ApkInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long downloadApkId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            long localDownloadId = UpdaterUtils.getLocalDownloadId(context);
            if (downloadApkId == localDownloadId) {
                Logger.get().d("download complete. downloadId is " + downloadApkId);
                installApk(context, downloadApkId);
            }
        } else if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
            //处理 如果还未完成下载，用户点击Notification
            Intent viewDownloadIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
            viewDownloadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(viewDownloadIntent);
        }
    }

    private  void installApk(Context context, long downloadApkId) {

        DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadApkId);
        Cursor c = dManager.query(query);
        if(c != null) {
            if (c.moveToFirst()) {
                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                    String downloadFileUrl = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    startInstall(context, Uri.parse(downloadFileUrl));
                }
            }
            c.close();
        }
    }

    private boolean startInstall(Context context, Uri uri) {
        if(!new File( uri.getPath()).exists()) {
            System.out.println( " local file has been deleted! ");
            return false;
        }
        Intent intent = new Intent();
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction( Intent.ACTION_VIEW);
        intent.setDataAndType( uri, "application/vnd.android.package-archive");
        context.startActivity( intent);
        return true;
    }
}
