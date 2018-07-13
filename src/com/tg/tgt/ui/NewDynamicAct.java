package com.tg.tgt.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gyf.barlibrary.ImmersionBar;
import com.hyphenate.easeui.utils.PhotoUtils;
import com.hyphenate.easeui.utils.photo.MediaBean;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.easeui.widget.photoselect.PreviewImageActivity;
import com.hyphenate.easeui.widget.photoselect.SelectObserable;
import com.tg.tgt.R;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpHelper;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.utils.TakePhotoUtils;
import com.tg.tgt.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import top.zibin.luban.Luban;

/**
 *
 * @author yiyang
 */
public class NewDynamicAct extends BaseActivity{

    private GridViewAdapter mAdapter;
    private EaseTitleBar titlebar;
    private android.widget.EditText etdynamic;
    private GridView gridView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_new_dynamic);
        this.gridView = (GridView) findViewById(R.id.gridView);
        this.etdynamic = (EditText) findViewById(R.id.et_dynamic);
        this.titlebar = (EaseTitleBar) findViewById(R.id.title_bar);

        titlebar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        mPhotos.add(new MediaBean(""));
//        mPhotos.add(new MediaBean(""));
//        mPhotos.add(new MediaBean(""));
//        mPhotos.add(new MediaBean(""));
//        mPhotos.add(new MediaBean(""));
//        mPhotos.add(new MediaBean(""));
//        mPhotos.add(new MediaBean(""));
//        mPhotos.add(new MediaBean(""));
//        mPhotos.add(new MediaBean(""));
        SelectObserable.getInstance().clear();
        String path = getIntent().getStringExtra("path");
        if(path !=null){
            MediaBean e = new MediaBean(path);
            e.selectPosition=1;
            mPhotos.add(e);
            SelectObserable.getInstance().setSelectImages(mPhotos);
        }
        mAdapter = new GridViewAdapter(this, mPhotos);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                List<PhotoFolder> photoFolders = PhotoUtils.loadLocalFolderContainsImage(NewDynamicAct.this);
//                Log.i("photoFolders", photoFolders.toString());
                if(mAdapter.isAdd(position)){
//                    ToastUtils.showToast(getApplicationContext(), "add");
//                    TakePhotoUtils.showDialog(mActivity, new TakePhotoUtils.CallBack() {
//                        @Override
//                        public void onActivityResult(String path) {
////                        Toast.makeText(mActivity, path, Toast.LENGTH_SHORT).show();
//                            //startActivityForResult(new Intent(mActivity, NewDynamicAct.class).putExtra("path", path), REQ_NEW);
//                        }
//                    });
                    camerDialog();
                   // startActivityForResult(new Intent(NewDynamicAct.this, AlbumsAct.class),REQUEST_SELETE_PHOTO);
                }else {
//                    ToastUtils.showToast(getApplicationContext(), "preview");
//                    SelectObserable.getInstance().setFolderAllImages(mPhotos);
//                    PreviewImageActivity.startPreviewPhotoActivity(mActivity, position, false);
                    PreviewImageActivity.startPreviewPhotoActivityForResult(mActivity, mPhotos, position, false, true, REQUEST_PREVIEW_PHOTO);
                }
            }
        });

    }

    private String imgPath;
    private void camerDialog(){
        View view = getLayoutInflater().inflate(R.layout.dialog_photo_choose, null);
        final Dialog dialog = new Dialog(this, R.style.TransparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        Button photograph = (Button) view.findViewById(R.id.photograph);
        Button albums = (Button) view.findViewById(R.id.albums);
        Button cancel = (Button) view.findViewById(R.id.photo_cancel);

        photograph.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String tempImgPath = PhotoUtils.getTempImgPath(getApplicationContext());
                imgPath = tempImgPath;
                Uri imageUri = Uri.fromFile(new File(tempImgPath));
                openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(openCameraIntent,REQUEST_CAMER_PHOTO);
//                newInstance(activity,tempImgPath).startActivityForResult(openCameraIntent, REQUEST_CAPTURE, callBack);

            }
        });

        albums.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                startActivityForResult(new Intent(NewDynamicAct.this, AlbumsAct.class),REQUEST_SELETE_PHOTO);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }

    private  final int REQUEST_SELETE_PHOTO = 1;
    private  final int REQUEST_PREVIEW_PHOTO = 2;
    private  final int REQUEST_CAMER_PHOTO = 3;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(REQUEST_PREVIEW_PHOTO == requestCode){
            mAdapter.notifyDataSetChanged();
        }
        if (REQUEST_CAMER_PHOTO == requestCode){
            if(imgPath != null && new File(imgPath).exists()){
                MediaBean e = new MediaBean(imgPath);
                e.selectPosition= mPhotos.size()+1;
                mPhotos.add(e);
                mAdapter.notifyDataSetChanged();
            }
        }
        if(resultCode!= RESULT_OK){
            return;
        }
        if(REQUEST_SELETE_PHOTO == requestCode){
            mPhotos = SelectObserable.getInstance().getSelectImages();
            mAdapter.setData(mPhotos);
        }
    }

    private List<MediaBean> mPhotos = new ArrayList<>();

    @Override
    protected void onDestroy() {
        SelectObserable.getInstance().clear();
        super.onDestroy();
    }

    class GridViewAdapter extends BaseAdapter{

        public static final int maxSize = 9;
        private Context context;
        private List<MediaBean> mDatas;

        public GridViewAdapter(Context context, List<MediaBean> mDatas){
            this.context = context;
            this.mDatas = mDatas;
        }

        public void setData(List<MediaBean> datas){
            mDatas = datas;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mDatas.size() >= maxSize ? mDatas.size() : mDatas.size()+1;
        }

        @Override
        public Object getItem(int position) {
            return isAdd(position) ? null : mDatas.get(position) ;
        }

        public boolean isAdd(int position) {
            return position == mDatas.size() && mDatas.size() <maxSize;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(context, R.layout.item_dynamic_image, null);
            ImageView iv = (ImageView) convertView.findViewById(R.id.item_image);
            View tvHint = convertView.findViewById(R.id.tv_hint);
            if(getItem(position) == null){
                tvHint.setVisibility(View.GONE);
                Glide.with(mActivity).load(R.drawable.moment_take_photo1).apply(new RequestOptions().centerCrop()).into(iv);
            }else {
//                iv.setImageResource(R.drawable.ic_launcher);
                tvHint.setVisibility(View.GONE);
                Glide.with(mActivity).load(mDatas.get(position).getPath()).apply(new RequestOptions().centerCrop()).into(iv);
            }
            return convertView;
        }
    }

    public void send(View view) {
        switch (view.getId()){
            case R.id.cancel_button:
                finish();
                break;
            case R.id.send_button:
                String content = etdynamic.getText().toString().trim();
                if(TextUtils.isEmpty(content)){
                    ToastUtils.showToast(getApplicationContext(), getString(R.string.dynamic_cannot_empty));
                    return;
                }
                if(content.length()>=1000){
                    ToastUtils.showToast(getApplicationContext(), getString(R.string.new_dynamic_size_more));
                    return;
                }
                Observable.just(mPhotos)
                        .map(new Function<List<MediaBean>, List<File>>() {
                            @Override
                            public List<File> apply(@NonNull List<MediaBean> mediaBeen) throws Exception {
                                ArrayList<String> strings = new ArrayList<>();
                                for (MediaBean mediaBean : mediaBeen) {
                                    strings.add(mediaBean.getPath());
                                }
                                return Luban.with(mActivity).load(strings).setTargetDir(PhotoUtils.getTempDirPath(mContext)).get();
                            }
                        })
                /*.map(new Function<MediaBean, File>() {
                    @Override
                    public File apply(@NonNull MediaBean mediaBean) throws Exception {
                        return Luban.with(mActivity)
                                .load(mediaBean.getPath())
                                .setTargetDir(PhotoUtils.getTempDirPath(mContext))
                                .get(mediaBean.getPath());
                    }
                })
                .buffer(9)*/
                        .flatMap(new Function<List<File>, ObservableSource<HttpResult<EmptyData>>>() {
                            @Override
                            public ObservableSource<HttpResult<EmptyData>> apply(@NonNull List<File> files) throws Exception {
                                int outHeight = 0;
                                int outWidth = 0;
                                if(files.size()>0) {
                                    String path = files.get(0).getPath();
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inJustDecodeBounds = true;//这个参数设置为true才有效，
                                    Bitmap bmp = BitmapFactory.decodeFile(path, options);//这里的bitmap是个空
                                    outHeight = options.outHeight;
                                    outWidth = options.outWidth;
                                }
                                return ApiManger2.getApiService()
                                        .applyMoments(HttpHelper.getMomentPicMap(files), HttpHelper.toTextPlain(etdynamic.getText().toString().trim()),
                                                HttpHelper.toTextPlain(""+outWidth),HttpHelper.toTextPlain(""+outHeight));
                            }
                        })
                        .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers())
                        .subscribe(new BaseObserver2<EmptyData>() {
                            @Override
                            protected void onSuccess(EmptyData emptyData) {
                                ToastUtils.showToast(getApplicationContext(), R.string.publish_success);
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                break;
                default:
                    break;
        }
        /*ApiManger2.getApiService()
                .applyMoments(HttpHelper.getMomentPicMap(mPhotos), HttpHelper.toTextPlain(etdynamic.getText().toString()))
                .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<EmptyData>() {
                    @Override
                    protected void onSuccess(EmptyData emptyData) {
                        ToastUtils.showToast(getApplicationContext(), "上传成功");
                        setResult(RESULT_OK);
                        finish();
                    }
                });*/
    }

    @Override
    protected void initBar(boolean enableKeyBoard) {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar
            //    .transparentStatusBar()
                .statusBarDarkFont(true,.2f)
                .keyboardEnable(true)
                .init();
    }

}
