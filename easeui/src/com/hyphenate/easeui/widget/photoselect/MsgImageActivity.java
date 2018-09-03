package com.hyphenate.easeui.widget.photoselect;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.GlideApp;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.EaseImageCache;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.easeui.utils.MenuDialogUtils;
import com.hyphenate.easeui.utils.ToastUtils;
import com.hyphenate.easeui.utils.photo.MediaBean;
import com.hyphenate.easeui.utils.photo.PhotoBean;
import com.hyphenate.easeui.utils.photo.VideoBean;
import com.hyphenate.easeui.widget.CircleProgressView;
import com.hyphenate.util.ImageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.jessyan.progressmanager.ProgressListener;
import me.jessyan.progressmanager.ProgressManager;
import me.jessyan.progressmanager.body.ProgressInfo;

import static com.hyphenate.easeui.widget.photoselect.PhotoAdapter.maxCount;


public class MsgImageActivity extends EaseBaseActivity implements OnClickListener {
    /** 显示/隐藏 过程持续时间 */
    private static final int SHOW_HIDE_CONTROL_ANIMATION_TIME = 500;
    private ViewPager mPhotoPager;
    /**选择按钮*/
    private TextView mCheckedTv;
    /**控制显示、隐藏顶部标题栏*/
    private boolean isHeadViewShow = true;
    private View mCtrlCheck;

    /**需要预览的所有图片*/
    private List<MediaBean> mAllImage;
    /**x选择的所有图片*/
    private List<MediaBean> mSelectImage;
    private View mTitleBar;
    private TextView mTitle;
    private PreviewAdapter mAdapter;
    private BroadcastReceiver mReceiver;
    private boolean canCheck;
    private boolean canDelete;
    private View mLayoutCtrl;
    private View mLayoutBottom;
    private TextView mBtnConfirm;
    private View mCurIv;
    private boolean mIsReturning;
    private int mCurrentPosition;
    private int startPosition;

    /**
     * 预览文件夹下所有图片
     */
    public static void startPreviewPhotoActivity(Context context, int position) {
        startPreviewPhotoActivity(context, position, true);
    }
    public static void startPreviewPhotoActivityForResult(Fragment context, int position, boolean canCheck, int reqCode) {
        Intent intent = new Intent(context.getContext(), MsgImageActivity.class);
        intent.putExtra("canCheck", canCheck);
        intent.putExtra("position", position);
        context.startActivityForResult(intent, reqCode);
    }

    /**
     * 预览文件夹下所有图片
     */
    public static void startPreviewPhotoActivity(Context context, int position, boolean canCheck) {
        Intent intent = new Intent(context,MsgImageActivity.class);
        if (canCheck)
            intent.putExtra("canCheck", canCheck);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    public static final String TRANSITIONNAME = "transitionName";
    /**
     * 共享元素预览文件夹下所有图片
     */
    public static void startPreviewPhotoActivity(Context context, int position, View transitionView, boolean canCheck) {
        Intent intent = new Intent(context,MsgImageActivity.class);
        if (canCheck)
            intent.putExtra("canCheck", canCheck);
        intent.putExtra("position", position);
        ViewCompat.setTransitionName(transitionView, position+"");
        intent.putExtra(TRANSITIONNAME, ViewCompat.getTransitionName(transitionView));
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)
                context, transitionView, ViewCompat.getTransitionName(transitionView));
//        context.startActivity(intent);
        ActivityCompat.startActivity(context, intent, activityOptionsCompat.toBundle());
    }

    /**
     * 预览文件夹下所有图片
     */
    public static void startPreviewPhotoActivityForResult(Activity context, List<MediaBean> list, int position, boolean canCheck, int requestCode) {
        SelectObserable.getInstance().setFolderAllImages(list);
        Intent intent = new Intent(context, MsgImageActivity.class);
        if (canCheck)
            intent.putExtra("canCheck", canCheck);
        intent.putExtra("position", position);
        context.startActivityForResult(intent, requestCode);
    }
    public static void startPreviewPhotoActivityForResult(Activity context, List<MediaBean> list, int position, boolean canCheck, boolean canDelete, int requestCode) {
        SelectObserable.getInstance().setFolderAllImages(list);
        Intent intent = new Intent(context,MsgImageActivity.class);
        intent.putExtra(CAN_CHECK, canCheck);
        intent.putExtra(CAN_DELETE, canDelete);
        intent.putExtra("position", position);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 预览文件夹下所有图片，使用共享元素跳转动画
     */
    public static void startPreviewPhotoActivity(ImageView iv, Context context, int position) {
        Intent intent = new Intent(context, MsgImageActivity.class);
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, iv, "PHOTO").toBundle();
        intent.putExtra("position", position);
//        context.startActivity(intent,bundle);
        ActivityCompat.startActivity((Activity) context, intent, bundle);
    }

    /**
     * 预览选择的图片
     * @param activity Activity
     * @param requestCode requestCode
     */
    public static void startPreviewActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, MsgImageActivity.class);
        intent.putExtra("preview", true);
        activity.startActivityForResult(intent, requestCode);
    }

    public static final String CAN_CHECK = "canCheck";
    public static final String CAN_DELETE = "canDelete";
    public static List<MediaBean> cacheMediaBean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_image_activity);
		/*全屏*/
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        canCheck = getIntent().getBooleanExtra("canCheck", false);
        canDelete = getIntent().getBooleanExtra(CAN_DELETE, false);
        //延时
        ActivityCompat.postponeEnterTransition(this);
        initImages();

        initView();

        initAdapter();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        ActivityCompat.setEnterSharedElementCallback(this, new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
                if(mIsReturning&&mCurrentPosition!=startPosition)
                {
                    names.clear();
                    names.add(ViewCompat.getTransitionName(mCurIv));
                    sharedElements.clear();
                    sharedElements.put(ViewCompat.getTransitionName(mCurIv), mCurIv);
                }
            }
        });
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mAllImage.clear();
                mAllImage.addAll(SelectObserable.getInstance().getFolderAllImages());
                mAdapter.notifyDataSetChanged();
            }
        };
        //监听数据发生变化
        registerReceiver(mReceiver, new IntentFilter(PhotoSelectMenu.DATA_CHANGE));
    }

    @Override
    protected boolean isTran() {
        return true;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        ProgressManager.getInstance().removeAllResponseListener();
        cacheMediaBean = null;
        super.onDestroy();
    }

    /**
     * 初始化图片数组
     */
    private void initImages() {
        mAllImage = new ArrayList<>(SelectObserable.getInstance().getFolderAllImages());
        if(cacheMediaBean!= null) {
            mSelectImage = cacheMediaBean;
        }else {
            mSelectImage = SelectObserable.getInstance().getSelectImages();
        }
    }

    /**初始化控件*/
    private void initView() {
        mLayoutCtrl = findViewById(R.id.layout_ctrl);
        mLayoutBottom = findViewById(R.id.layout_bottom);
        mTitleBar = findViewById(R.id.title_bar);
        mTitle = (TextView) findViewById(R.id.title);

		/*底部菜单栏*/
        mCtrlCheck = findViewById(R.id.rl_check);
        mBtnConfirm = (TextView)findViewById(R.id.confirm_btn);
        mCheckedTv = (TextView) findViewById(R.id.ctv_check);

        mCurrentPosition = getIntent().getIntExtra("position", 0);
        startPosition = mCurrentPosition;
        if(mAllImage.size()==1){
            mTitle.setText("");
        }else {
            String text = (mCurrentPosition + 1) + "/" + mAllImage.size();
            mTitle.setText(text);
        }
        mBtnConfirm.setOnClickListener(this);
        mCtrlCheck.setOnClickListener(this);
        mPhotoPager = (ViewPager) findViewById(R.id.vp_preview);

        if(canDelete){
            mBtnConfirm.setText(R.string.delete);
            mLayoutBottom.setVisibility(View.GONE);
            return;
        }else if (!canCheck) {
            mBtnConfirm.setVisibility(View.GONE);
            mLayoutBottom.setVisibility(View.GONE);
            return;
        }
        if (mSelectImage.contains(mAllImage.get(mCurrentPosition))) {
            mCheckedTv.setEnabled(true);
            mCheckedTv.setText(String.valueOf(mAllImage.get(mCurrentPosition).selectPosition));
        } else {
            mCheckedTv.setEnabled(false);
            mCheckedTv.setText("");
        }


    }

    /**
     * 更新选择的顺序
     */
    private void subSelectPosition() {
        for (int index = 0, len = mSelectImage.size(); index < len; index++) {
            MediaBean folderBean = mSelectImage.get(index);
            folderBean.selectPosition = index + 1;
        }
    }

    /**
     * adapter的初始化
     */
    private void initAdapter() {
        mPhotoPager = (ViewPager) findViewById(R.id.vp_preview);
        mAdapter = new PreviewAdapter(mAllImage);
        mPhotoPager.setAdapter(mAdapter);
        mPhotoPager.setPageMargin(5);
        mPhotoPager.setCurrentItem(getIntent().getIntExtra("position", 0));
        mPhotoPager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                mCurrentPosition = arg0;
                mCurIv = mPhotoPager.findViewWithTag(arg0).findViewById(R.id.iv_image_item);
//                mCurIv.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        new MenuDialogUtils(MsgImageActivity.this, R.style.registDialog, R.layout.menu_save, new MenuDialogUtils.ButtonClickListener() {
//                            @Override
//                            public void onButtonClick(int i) {
//                                if (i == 0) {
//                                    if (!TextUtils.isEmpty(mAllImage.get(mCurrentPosition).getPath())) {
//                                        saveImage(mAllImage.get(mCurrentPosition).getPath());
//                                       // ToastUtils.showToast(this,"图片保存成功");
//                                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                                        mediaScanIntent.setData(Uri.fromFile(new File(mAllImage.get(mCurrentPosition).getPath())));
//                                      //  this.sendBroadcast(mediaScanIntent);
//                                    } else {
//                                        Toast.makeText(MsgImageActivity.this,"图片加载中，无法保存",Toast.LENGTH_LONG).show();
//                                        //ToastUtils.showToast(INSTANCE,"图片加载中,无法保存!");
//                                    }
//                                }
//                            }
//                        }).show();
//                        return false;
//                    }
//                });
                if(mAllImage.size()==1){
                    mTitle.setText("");
                }else {
                    String text = (arg0 + 1) + "/" + mAllImage.size();
                    mTitle.setText(text);
                }
                SelectObserable.getInstance().setChange(arg0);

                if (!canCheck)
                    return;
                if (mSelectImage.contains(mAllImage.get(arg0))) {
                    mCheckedTv.setEnabled(true);
                    mCheckedTv.setText(String.valueOf(mAllImage.get(arg0).selectPosition));
                } else {
                    mCheckedTv.setEnabled(false);
                    mCheckedTv.setText("");
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }


    //保存图片
    public void saveImage(final String url_){
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
                    MediaStore.Images.Media.insertImage(getContentResolver(),bitmap, fileName, null);
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(file);
                    intent.setData(uri);
                    MsgImageActivity.this.sendBroadcast(intent);
                    Toast.makeText(MsgImageActivity.this,"保存成功",Toast.LENGTH_LONG).show();


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

    /**
     * 简单的适配器
     */
    class PreviewAdapter extends PagerAdapter {
        private List<MediaBean> photos;

        public PreviewAdapter(List<MediaBean> photoList) {
            super();
            this.photos = photoList;
        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return canDelete?POSITION_NONE:POSITION_UNCHANGED;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(MsgImageActivity.this);
            View view = inflater.inflate(R.layout.preview_image_item, container, false);
            view.setTag(position);
            final ImageView bigPhotoIv = (ImageView) view.findViewById(R.id.iv_image_item);
            ViewCompat.setTransitionName(bigPhotoIv, position+"");
            if(getIntent().getIntExtra("position",0) == position)
                bigPhotoIv.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        bigPhotoIv.getViewTreeObserver().removeOnPreDrawListener(this);
                        ActivityCompat.startPostponedEnterTransition(MsgImageActivity.this);
                        return true;
                    }
                });
            bigPhotoIv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 微信是点击退出，嗯，现在如果是点击退出不太ok
                    /*if(!canCheck){
                        onBackPressed();
                        return;
                    }*/
                    onBackPressed();
//                    if (isHeadViewShow) {
//                        hideControls();
//                    } else {
//                        showControls();
//                    }
                }
            });
            bigPhotoIv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new MenuDialogUtils(MsgImageActivity.this, R.style.registDialog,0, R.layout.menu_save, new MenuDialogUtils.ButtonClickListener() {
                        @Override
                        public void onButtonClick(int i) {
                            if (i == 0) {
                                if (!TextUtils.isEmpty(mAllImage.get(mCurrentPosition).getPath())) {
//                                    saveImage(mAllImage.get(mCurrentPosition).getPath());
//                                    // ToastUtils.showToast(this,"图片保存成功");
//                                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                                    mediaScanIntent.setData(Uri.fromFile(new File(mAllImage.get(mCurrentPosition).getPath())));
                                    //   saveImage(mAllImage.get(mCurrentPosition).getPath());
                                    bigPhotoIv.setDrawingCacheEnabled(true);
                                    saveBmp2Gallery(bigPhotoIv.getDrawingCache(), System.currentTimeMillis()+"APP");
                                    bigPhotoIv.setDrawingCacheEnabled(false);
                                    //  this.sendBroadcast(mediaScanIntent);
                                }else {
                                    Toast.makeText(MsgImageActivity.this, "图片加载中，无法保存", Toast.LENGTH_LONG).show();
                                    //ToastUtils.showToast(INSTANCE,"图片加载中,无法保存!");
                                }
                            }
                        }
                    }, new MenuDialogUtils.ButtonClickCollectListener() {
                        @Override
                        public void onButtonCollectClick() {
                            if (EaseConversationListFragment.mCollectEHandler != null) {
                                Log.e("Tag", "不为空发送");
                                Message msg = new Message();
                                msg.what = 1;
                                msg.obj = mAllImage.get(mCurrentPosition).getPath();
                                EaseConversationListFragment.mCollectEHandler.sendMessage(msg);
                            }
                        }
                    }).show();
                    return false;
                }
            });


            MediaBean mediaBean = photos.get(position);
            String path = mediaBean.getPath();

            final CircleProgressView progressView = (CircleProgressView) view.findViewById(R.id.circle_progress_view);
            if(path!=null && path.startsWith("http://")){
                ProgressManager.getInstance().addResponseListener(path, new ProgressListener() {
                    @Override
                    public void onProgress(ProgressInfo progressInfo) {
                        int progress = progressInfo.getPercent();
                        progressView.setProgress(progress);
//                        ToastUtils.showToast(getApplicationContext(), "progress:"+progress);
                        if(progressInfo.isFinish()){
                            progressView.setVisibility(View.GONE);
//                            ToastUtils.showToast(getApplicationContext(), "load succes");
                        }else {
                            progressView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(long id, Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressView.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }

//            if(mediaBean instanceof PhotoBean && !TextUtils.isEmpty(((PhotoBean) mediaBean).getThumbnail())) {
//                GlideApp.with(MsgImageActivity.this).load(((PhotoBean) mediaBean).getThumbnail()).into(bigPhotoIv);
//            }
//
//            SimpleTarget<File> simpleTarget = GlideApp.with(MsgImageActivity.this).load(path).downloadOnly(new SimpleTarget<File>() {
//                @Override
//                public void onResourceReady(File resource, Transition<? super File> transition) {
//                    progressView.setVisibility(View.GONE);
//                    GlideApp.with(MsgImageActivity.this).load(resource).placeholder(bigPhotoIv.getDrawable()).into(bigPhotoIv);
//                }
//            });
            File file = new File(path);
            if (file.exists()) {
                GlideApp.with(MsgImageActivity.this)
                        .load(path)
                        .thumbnail(GlideApp.with(MsgImageActivity.this)
                                .load(mediaBean instanceof PhotoBean && !TextUtils.isEmpty(((PhotoBean) mediaBean).getThumbnail())?((PhotoBean) mediaBean).getThumbnail():null))
                        .into(bigPhotoIv);
            } else {
                downloadImage(bigPhotoIv,mediaBean.getMsg_id(),path);
            }
            container.addView(view);
            return view;
        }
        // ProgressDialog pd;
        private int default_res = com.hyphenate.easeui.R.drawable.ease_default_image;
        /**
         * download image
         */
        @SuppressLint("NewApi")
        private void downloadImage(final ImageView image, final String msgId, final String localFilePath) {
           /* String str1 = getResources().getString(com.hyphenate.easeui.R.string.Download_the_pictures);
            pd = new ProgressDialog(MsgImageActivity.this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage(str1);
            pd.show();*/
            File temp = new File(localFilePath);
            final String tempPath = temp.getParent() + "/temp_" + temp.getName();
            final EMCallBack callback = new EMCallBack() {
                public void onSuccess() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new File(tempPath).renameTo(new File(localFilePath));
                            DisplayMetrics metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            int screenWidth = metrics.widthPixels;
                            int screenHeight = metrics.heightPixels;

                            Bitmap bitma = ImageUtils.decodeScaleImage(localFilePath, screenWidth, screenHeight);
                            if (bitma == null) {
                                image.setImageResource(default_res);
                            } else {
                                image.setImageBitmap(bitma);
                                EaseImageCache.getInstance().put(localFilePath, bitma);
                            }
                            if (isFinishing() || isDestroyed()) {
                                return;
                            }
                       /*     if (pd != null) {
                                pd.dismiss();
                            }*/
                        }
                    });
                }

                public void onError(int error, String msg) {
                    File file = new File(tempPath);
                    if (file.exists()&&file.isFile()) {
                        file.delete();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (MsgImageActivity.this.isFinishing() || MsgImageActivity.this.isDestroyed()) {
                                return;
                            }
                            image.setImageResource(default_res);
                            //    pd.dismiss();
                        }
                    });
                }
                public void onProgress(final int progress, String status) {
                    final String str2 = getResources().getString(com.hyphenate.easeui.R.string.Download_the_pictures_new);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (MsgImageActivity.this.isFinishing() || MsgImageActivity.this.isDestroyed()) {
                                return;
                            }
                            //   pd.setMessage(str2 + progress + "%");
                        }
                    });
                }
            };
            EMMessage msg = EMClient.getInstance().chatManager().getMessage(msgId);
            msg.setMessageStatusCallback(callback);
            EMClient.getInstance().chatManager().downloadAttachment(msg);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
//            mCurIv = ((View) object).findViewById(R.id.iv_image_item);
        }
    }

    private String[] perimissionCheck = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
    };

    /**
     * @param bmp 获取的bitmap数据
     * @param picName 自定义的图片名
     */
    private void saveBmp2Gallery(Bitmap bmp, String picName) {

        String fileName = null;
        //系统相册目录
        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator;


        // 声明文件对象
        File file = null;
        // 声明输出流
        FileOutputStream outStream = null;
        try {
            // 如果有目标文件，直接获得文件对象，否则创建一个以filename为名称的文件
            file = new File(galleryPath, picName + ".png");
            // 获得文件相对路径
            fileName = file.toString();
            // 获得输出流，如果文件中有内容，追加内容
            outStream = new FileOutputStream(fileName);
            if (null != outStream) {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
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
        MediaStore.Images.Media.insertImage(MsgImageActivity.this.getContentResolver(),bmp, fileName, null);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        MsgImageActivity.this.sendBroadcast(intent);
        Toast.makeText(MsgImageActivity.this,"保存成功",Toast.LENGTH_LONG).show();
        // ToastUtils.s(getString(R.string.toast_save_successful));
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_check) {
            addOrRemoveImage();
        }else if(id == R.id.confirm_btn){
            if(canDelete){
                int currentItem = mPhotoPager.getCurrentItem();
                mAllImage.remove(currentItem);
                List<MediaBean> folderAllImages = SelectObserable.getInstance().getFolderAllImages();
                folderAllImages.remove(currentItem);
                for (int index = 0, len = folderAllImages.size(); index < len; index++) {
                    MediaBean folderBean = folderAllImages.get(index);
                    folderBean.selectPosition = index + 1;
                }
                if(mAllImage.size()<1){
                    onBackPressed();
                    return;
                }
                if(currentItem <= mAllImage.size()-1){
                    if(mAllImage.size()==1){
                        mTitle.setText("");
                    }else {
                        String text = (currentItem + 1) + "/" + mAllImage.size();
                        mTitle.setText(text);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }else {
                SelectObserable.getInstance().setSelectImages(mSelectImage);
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(canDelete)
            setResult(RESULT_OK);
        super.onBackPressed();
    }

    /**
     * 添加或者删除当前操作的图片
     */
    private void addOrRemoveImage() {
        int currentItem = mPhotoPager.getCurrentItem();
        MediaBean imageBean = mAllImage.get(currentItem);

        if (mSelectImage.contains(imageBean)) {
            mSelectImage.remove(imageBean);
            subSelectPosition();
            mCheckedTv.setEnabled(false);
            mCheckedTv.setText("");
        } else {
            if (mSelectImage.size() >= PhotoAdapter.maxCount) {
                ToastUtils.makeText(getApplicationContext(), getString(R.string
                        .publish_select_photo_max, maxCount), Toast.LENGTH_SHORT).show();
                return;
            }
            if (mSelectImage.size() > 0) {
                if (mSelectImage.get(0) instanceof VideoBean) {
                    if (!(imageBean instanceof VideoBean)) {
                        ToastUtils.makeText(getApplicationContext(), getString(R.string
                                .cannot_select_photo_and_video), Toast
                                .LENGTH_SHORT).show();
                    } else {
                        ToastUtils.makeText(getApplicationContext(), getString(R.string.only_can_select_one_movie),
                                Toast
                                        .LENGTH_SHORT).show();
                    }
                    return;
                } else {
                    if (imageBean instanceof VideoBean) {
                        ToastUtils.makeText(getApplicationContext(), getString(R.string
                                .cannot_select_photo_and_video), Toast
                                .LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            mSelectImage.add(imageBean);
            imageBean.selectPosition = mSelectImage.size();
            mCheckedTv.setEnabled(true);
            mCheckedTv.setText(String.valueOf(imageBean.selectPosition));
        }
        SelectObserable.getInstance().setChange(currentItem);

    }

    /**
     * <br>显示顶部，底部view动画 </br>
     */
    private void showControls() {
        AlphaAnimation animation = new AlphaAnimation(0f, 1f);
        animation.setFillAfter(true);
        animation.setDuration(SHOW_HIDE_CONTROL_ANIMATION_TIME);
        isHeadViewShow = true;
//        mTitleView.startAnimation(animation);
//        mTitleView.setVisibility(View.VISIBLE);
//        mCtrlCheck.startAnimation(animation);
//        mCtrlCheck.setVisibility(View.VISIBLE);
        mLayoutCtrl.startAnimation(animation);
        mLayoutCtrl.setVisibility(View.VISIBLE);
    }

    /**
     * <br> 隐藏顶部，底部view 动画</br>
     */
    private void hideControls() {
        AlphaAnimation animation = new AlphaAnimation(1f, 0f);
        animation.setFillAfter(true);
        animation.setDuration(SHOW_HIDE_CONTROL_ANIMATION_TIME);
        isHeadViewShow = false;
//        mTitleView.startAnimation(animation);
//        mTitleView.setVisibility(View.GONE);
//
//        mCtrlCheck.startAnimation(animation);
//        mCtrlCheck.setVisibility(View.GONE);
        mLayoutCtrl.startAnimation(animation);
        mLayoutCtrl.setVisibility(View.GONE);
    }

    @Override
    public void finish() {
        super.finish();
    }
    @Override
    public void finishAfterTransition() {
        mIsReturning = true;
        Intent data = new Intent();
        data.putExtra("currentItem", mCurrentPosition);
        setResult(112233, data);
        super.finishAfterTransition();
    }
}
