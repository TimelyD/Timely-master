package com.hyphenate.easeui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.storage.StorageManager;

import com.hyphenate.easeui.EaseApp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author yiyang
 */
public class PhotoUtils {

    public static String getTempDirPath(Context mContext){
        String dir = FilesUtils.getExternalCacheDirectory(mContext, Environment.DIRECTORY_PICTURES) +
                "/Temp/";
        if(!new File(dir).exists())
            new File(dir).mkdirs();
        return dir;
    }

    public static String getTempImgPath(Context mContext){
//        String dir = FilesUtils.getExternalCacheDirectory(mContext, Environment.DIRECTORY_PICTURES) +
//                "/Temp/";
//        if(!new File(dir).exists())
//            new File(dir).mkdirs();
        String dir = getTempDirPath(mContext);
        String fileName= dir+ UUID.randomUUID().toString() + ".jpg";
        return fileName;
    }
    /**
     * 保存图片到本地,使用自定义名称 fullPath:文件路径（不包括文件名） photoName:保存的文件名
     */
    public static boolean savePhotoToLocal(Bitmap bmp, String fullPath,
                                           String photoName) {

        if (!(Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState()))) {
            return false; // sd卡不能读写
        }

        if (bmp == null) {
            return false;
        }

        File dirFile = new File(fullPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        File pictureFile = new File(fullPath + photoName);
        BufferedOutputStream bos = null;

        try {
            bos = new BufferedOutputStream(new FileOutputStream(pictureFile));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.flush();
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    /**旋转图片*/
    public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 判断图片是否被旋转，导致宽高比例不对
     */
    public static boolean isPicRotate(String path){
        int i = readPictureDegree(path);
        return i /90==1 || i/270==1;
    }

    public static boolean isSDCardEnable(){
        return !getSDCardPaths().isEmpty();
    }

    /**
     * 获取 SD 卡路径
     *
     * @return SD 卡路径
     */
    @SuppressWarnings("TryWithIdenticalCatches")
    public static List<String> getSDCardPaths() {
        StorageManager storageManager = (StorageManager) EaseApp.applicationContext.getSystemService(Context.STORAGE_SERVICE);
        List<String> paths = new ArrayList<>();
        try {
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths");
            getVolumePathsMethod.setAccessible(true);
            Object invoke = getVolumePathsMethod.invoke(storageManager);
            paths = Arrays.asList((String[]) invoke);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return paths;
    }
}
