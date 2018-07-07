package com.hyphenate.easeui.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import com.hyphenate.easeui.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by DELL on 2018/7/7.
 */

public class SavePicUtil {
    public static void save(View imageView, final Activity INSTANCE, final Bitmap bm){
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new MenuDialogUtils(INSTANCE, R.style.registDialog, R.layout.menu_save, new MenuDialogUtils.ButtonClickListener() {
                    @Override
                    public void onButtonClick(int i) {
                        if (i == 0) {
                            if (bm != null) {
                                savePicture(bm);
                                ToastUtils.showToast(INSTANCE,"图片保存成功");
                                INSTANCE.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
                            } else {
                                ToastUtils.showToast(INSTANCE,"图片加载中,无法保存!");
                            }
                        }
                    }
                }).show();
                return false;
            }
        });
    }
    public static void saveUrl(View imageView, final Activity INSTANCE, final String bm){
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new MenuDialogUtils(INSTANCE, R.style.registDialog, R.layout.menu_save, new MenuDialogUtils.ButtonClickListener() {
                    @Override
                    public void onButtonClick(int i) {
                        if (i == 0) {
                            if (bm != null) {
                                saveImage(bm);
                                ToastUtils.showToast(INSTANCE,"图片保存成功");
                                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                mediaScanIntent.setData(Uri.fromFile(new File(bm)));
                                INSTANCE.sendBroadcast(mediaScanIntent);
                            } else {
                                ToastUtils.showToast(INSTANCE,"图片加载中,无法保存!");
                            }
                        }
                    }
                }).show();
                return false;
            }
        });
    }
        //保存图片
    public static void saveImage(final String url_){
        //开启子线程
        new Thread(){
            public void run() {
                try {
                    URL url = new URL(url_);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(6 * 1000);  // 注意要设置超时，设置时间不要超过10秒，避免被android系统回收
                    if (conn.getResponseCode() != 200) throw new RuntimeException("请求url失败");
                    InputStream inSream = conn.getInputStream();
                    //把图片保存到项目的根目录
                    readAsFile(inSream, new File(Environment.getExternalStorageDirectory()+"/"+ getTempFileName()+".jpg"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }
    public static void savePicture(Bitmap bitmap) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                File files=new File("/storage/emulated/legacy/RedStar");
                if(!files.exists()){
                    files.mkdirs();
                }
                File file=new File(files,System.currentTimeMillis()+".jpg");
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                // Log.i("yc", "FileNotFoundException保存失败");
            } catch (IOException e) {
                // Log.i("yc", "IOException保存失败");
                e.printStackTrace();
            }
        }
    }
    public static void readAsFile(InputStream inSream, File file) throws Exception{
        FileOutputStream outStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int len = -1;
        while( (len = inSream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inSream.close();
    }
    /**
     * 使用当前时间戳拼接一个唯一的文件名
     *
     * // format
     * @return
     */
    public static String getTempFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SS");
        String fileName = format.format(new Timestamp(System
                .currentTimeMillis()));
        return fileName;
    }
}
