package com.hyphenate.easeui.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.widget.photoselect.PreviewImageActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;


/**
 * Created by Administrator on 2015/12/10.
 */
public class MenuDialogUtils extends Dialog {
   public ButtonClickListener listener_b;
   public ButtonClickCollectListener listener_c;
   public Context context;

    public MenuDialogUtils(final Context context, int theme, int type,int resource,ButtonClickListener listener,ButtonClickCollectListener listener1) {
        super(context, theme);
        View view = View.inflate(context, resource, null);
        this.listener_b=listener;
        this.listener_c = listener1;
        this.context=context;
        setContentView(view);
        setCancelable(true);

        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
       // window.setWindowAnimations(R.anim.menu_dialog_in); //添加动画
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        if(type==1){
            view.findViewById(R.id.rl_collection).setVisibility(View.GONE);
        }else {
            view.findViewById(R.id.rl_collection).setVisibility(View.VISIBLE);
        }
        view.findViewById(R.id.rl_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener_b.onButtonClick(0);
                MenuDialogUtils.this.dismiss();
            }
        });
        findViewById(R.id.tv_cancle_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuDialogUtils.this.dismiss();
            }
        });
        view.findViewById(R.id.rl_collection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener_c.onButtonCollectClick();
                MenuDialogUtils.this.dismiss();
            }
        });

    }
    //保存图片
   /* public static void saveImage(final String url_){
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


                    String fileName = null;
                    //系统相册目录
                    String galleryPath = Environment.getExternalStorageDirectory()
                            + File.separator + Environment.DIRECTORY_DCIM
                            + File.separator + "Camera" + File.separator;

                    Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/"+ getTempFileName()+".jpg");
                    // 声明文件对象
                    File file = null;
                    // 声明输出流
                    FileOutputStream outStream = null;
                    try {
                        // 如果有目标文件，直接获得文件对象，否则创建一个以filename为名称的文件
                        file = new File(galleryPath, System.currentTimeMillis() + "circle.png");
                        // 获得文件相对路径
                        fileName = file.toString();
                        // 获得输出流，如果文件中有内容，追加内容
                        outStream = new FileOutputStream(fileName);
                        if (null != outStream) {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                        }
                    } catch (Exception e) {
                        e.getStackTrace();
                    } finally {
                        try {
                            if (outStream != null) {
                                outStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //通知相册更新
                    MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap, fileName, null);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(file);
                    intent.setData(uri);
                    context.sendBroadcast(intent);
                    Toast.makeText(context,"保存成功",Toast.LENGTH_LONG).show();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }*/

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

    public interface ButtonClickListener {
        public void onButtonClick(int i);
    }

    public interface ButtonClickCollectListener{
        public void onButtonCollectClick();
    }

}
