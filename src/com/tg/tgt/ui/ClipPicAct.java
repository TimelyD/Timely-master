package com.tg.tgt.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.DemoHelper;
import com.tg.tgt.R;
import com.hyphenate.easeui.utils.PhoneUtil;
import com.tg.tgt.utils.SharedPreStorageMgr;
import com.tg.tgt.widget.clip.ClipImageLayout;
import com.tg.tgt.widget.clip.ImageTools;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 *
 * @author yiyang
 */
public class ClipPicAct extends BaseActivity{
    private ClipImageLayout mClipImageLayout;
    private String path;
    String s;
    String Imagepath;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        requestWindowFeature(Window.FEATURE_NO_TITLE);//
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipimage);


        ((EaseTitleBar)findViewById(R.id.title_bar)).setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //这步必须要加
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        path=getIntent().getStringExtra("path");
        if(TextUtils.isEmpty(path)||!(new File(path).exists())){
            Toast.makeText(this, "图片加载失败",Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap= ImageTools.convertToBitmap(path, PhoneUtil.getScreenWidth(this)/2,PhoneUtil.getScreenWidth(this)/2);
        if(bitmap==null){
            Toast.makeText(this, "图片加载失败",Toast.LENGTH_SHORT).show();
            return;
        }
        mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
        mClipImageLayout.setBitmap(bitmap);
        findViewById(R.id.id_action_clip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showProgress(R.string.loading);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = mClipImageLayout.clip();
                        Imagepath= Environment.getExternalStorageDirectory()+"/ClipHeadPhoto/cache/"+System.currentTimeMillis()+ ".png";
                        ImageTools.savePhotoToSDCard(bitmap,path);

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        options.inPurgeable = true;
                        options.inSampleSize = 2;

                        Random ran =new Random(System.currentTimeMillis());
                        for (int i = 0; i < 10; i++) {
                            s =s + ran.nextInt(100);
                        }
                        try {
                            Bitmap clipBitmap = BitmapFactory.decodeFile(path, options);
                                String url;
                            if(getIntent().getBooleanExtra(Constant.NOT_FOR_USER, false)) {
                                url = "http://live.qevedkc.net/time/Login/uploadcover/";
                            }else {
                                url = "http://live.qevedkc.net/time/Login/cover/";
                            }
                            saveFile(url,ratio(clipBitmap,480,640),s+".jpg");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }).start();
            }
        });
    }

    private Bitmap ratio(Bitmap clipBitmap, float pixelW, float pixelH) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        clipBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        if( os.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            os.reset();//重置baos即清空baos
            clipBitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        //压缩好比例大小后再进行质量压缩
//      return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    private void saveFile(String url, Bitmap ratio,
                          String fileName) throws IOException {
        String path = getSDPath() +"/cuteyep/";
        File dirFile = new File(path);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File myCaptureFile = new File(path + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        ratio.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        uploadFile(url,path+fileName);
    }
    private void uploadFile(String uploadUrl, String srcPath) {
        //注册时候NOT_FOR_USER为true，不需要加入uid
        if(!getIntent().getBooleanExtra(Constant.NOT_FOR_USER, false))
            uploadUrl +="?uid="+ App.getMyUid();
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try
        {
            URL url =new URL(uploadUrl);
            HttpURLConnection con=(HttpURLConnection)url.openConnection();
            con.setChunkedStreamingMode(300*1024);
            //remark.setFixedLengthStreamingMode(8*1024);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
          /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary="+boundary);
            DataOutputStream ds =
                    new DataOutputStream(con.getOutputStream());
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; " +
                    "name=\"uploadfile\"; filename=\"" +
                    srcPath.substring(srcPath.lastIndexOf("/") + 1) +"\"" + end);
            ds.writeBytes(end);

            FileInputStream fStream = new FileInputStream(srcPath);
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int length = -1;
            while((length = fStream.read(buffer)) != -1)
            {
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

          /* close streams */
            fStream.close();
            ds.flush();

            InputStream is = con.getInputStream();
            int ch;
            StringBuffer b =new StringBuffer();
            while( ( ch = is.read() ) != -1 )
            {
                b.append( (char)ch );
            }
            String pic = b.toString();

            SharedPreStorageMgr.getIntance().saveStringValue(ClipPicAct.this, Constant.HEADIMAGE, pic);
            SharedPreStorageMgr.getIntance().saveStringValue(App.applicationContext, DemoHelper.getInstance().getCurrentUsernName(),pic+"-"+EaseUserUtils.getUserInfo(EMClient.getInstance().getCurrentUser()).getNickname());
            EaseUserUtils.getUserInfo(EMClient.getInstance().getCurrentUser()).setAvatar(pic);

            dismissProgress();

            Intent intent = new Intent();
            intent.putExtra("path",pic);
            setResult(RESULT_OK, intent);

            finish();
            System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMMMMM" + pic);

            ds.close();
        }
        catch(Exception e)
        {
            //Toast.makeText(this, ""+e, Toast.LENGTH_LONG).show();
            //showDialog(""+e);
            e.printStackTrace();
        }
    }

    public String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if(sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }
}
