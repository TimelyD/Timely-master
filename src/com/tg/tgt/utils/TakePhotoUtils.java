package com.tg.tgt.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.hyphenate.easeui.utils.PhotoUtils;
import com.tg.tgt.R;

import java.io.File;

public class TakePhotoUtils {
    public static void showDialog(final Activity activity, final CallBack callBack){
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_photo_choose, null);
        final Dialog dialog = new Dialog(activity, R.style.TransparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = activity.getWindowManager().getDefaultDisplay().getHeight();
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
                capture(activity, callBack);
            }
        });

        albums.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                album(activity, callBack);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }
    public static void capture(Activity activity, CallBack callBack){
        ActivityResultFragment.requestCapture(activity, callBack);
    }
    public static void album(Activity activity, CallBack callBack){
        ActivityResultFragment.requestAlbum(activity, callBack);
    }

    public interface CallBack {
        void onActivityResult(String path);
    }

    /**
     * 用来处理发送intent并处理回调
     * @author yiyang
     */
    public static class ActivityResultFragment extends Fragment {
        public static final String TAG = "ActivityResultFragment";
        private CallBack callBack;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        enum RequestType {
            /**打开照相*/
            REQUEST_CAPTURE,
            /**打开图库*/
            REQUEST_ALBUM
        }

        public static final int REQUEST_CAPTURE = 556;
        public static final int REQUEST_ALBUM = 557;

        public static void requestCapture(Activity activity, CallBack callBack) {
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String tempImgPath = PhotoUtils.getTempImgPath(activity.getApplicationContext());
            Uri imageUri = Uri.fromFile(new File(tempImgPath));
            openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            newInstance(activity,tempImgPath).startActivityForResult(openCameraIntent, REQUEST_CAPTURE, callBack);
        }

        public static void requestAlbum(Activity activity, CallBack callBack) {
            Intent openAlbumIntent = new Intent(Intent.ACTION_PICK);
            openAlbumIntent.setType("image/*");
            newInstance(activity).startActivityForResult(openAlbumIntent, REQUEST_ALBUM, callBack);
        }

        private static ActivityResultFragment newInstance(Activity activity) {
            return newInstance(activity, null);
        }
        private static ActivityResultFragment newInstance(Activity activity, String path) {
            ActivityResultFragment CallBackFragment = findCallBackFragment(activity);
            boolean isNewInstance = CallBackFragment == null;
            if (isNewInstance) {
                CallBackFragment = new ActivityResultFragment();
                FragmentManager fragmentManager = activity.getFragmentManager();
                fragmentManager
                        .beginTransaction()
                        .add(CallBackFragment, TAG)
                        .commitAllowingStateLoss();
                fragmentManager.executePendingTransactions();
            }
            if(path != null)
                CallBackFragment.mPath = path;
            return CallBackFragment;
        }

        public String mPath;
        private static ActivityResultFragment findCallBackFragment(Activity activity) {
            return (ActivityResultFragment) activity.getFragmentManager().findFragmentByTag(TAG);
        }

        public void startActivityForResult(Intent intent, int requestCode, CallBack callBack) {
            this.callBack = callBack;
            startActivityForResult(intent, requestCode);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode != Activity.RESULT_OK)
                return;
            if (this.callBack == null)
                return;
            if (REQUEST_ALBUM == requestCode) {
                if(data == null)
                    return;
                Uri uri = geturi(getActivity().getApplicationContext(), data);
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().managedQuery(uri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(column_index);// 图片在的路径
                this.callBack.onActivityResult(path);
            } else if (REQUEST_CAPTURE == requestCode) {
//                Uri uri = geturi(getActivity().getApplicationContext(), data);
//                String[] proj = {MediaStore.Images.Media.DATA};
//                Cursor cursor = getActivity().managedQuery(uri, proj, null, null, null);
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                cursor.moveToFirst();
//                String path = cursor.getString(column_index);// 图片在的路径
//                this.callBack.onActivityResult(path);
                if(mPath != null && new File(mPath).exists()){
                    this.callBack.onActivityResult(mPath);
                }
            }
        }

        /**
         * 解决小米手机上获取图片路径为null的情况
         * @param intent
         * @return
         */
        public Uri geturi(Context context, Intent intent) {
            Uri uri = intent.getData();
            String type = intent.getType();
            if (uri.getScheme().equals("file") && (type.contains("image/"))) {
                String path = uri.getEncodedPath();
                if (path != null) {
                    path = Uri.decode(path);
                    ContentResolver cr = context.getContentResolver();
                    StringBuffer buff = new StringBuffer();
                    buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                            .append("'" + path + "'").append(")");
                    Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            new String[]{MediaStore.Images.ImageColumns._ID},
                            buff.toString(), null, null);
                    int index = 0;
                    for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                        index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                        // set _id value
                        index = cur.getInt(index);
                    }
                    if (index == 0) {
                        // do nothing
                    } else {
                        Uri uri_temp = Uri
                                .parse("content://media/external/images/media/"
                                        + index);
                        if (uri_temp != null) {
                            uri = uri_temp;
                        }
                    }
                }
            }
            return uri;
        }
    }
}