package com.hyphenate.easeui.utils.photo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.hyphenate.util.PathUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author yiyang
 */
public class PhotoUtils {
    /**
     * 加载本地图库图片
     * @param pageSize 每页加载个数
     * @param pageIndex 加载第几页
     * @return
     */
    public static Map<String, PhotoFolder> getPhotos(Context context, int pageSize, int pageIndex) {
        Map<String, PhotoFolder> folderMap = new HashMap<>();

        String allPhotosKey = "all";
        PhotoFolder allFolder = new PhotoFolder();
        allFolder.setName(allPhotosKey);
        allFolder.setDirPath(allPhotosKey);
        allFolder.setPhotoList(new ArrayList<MediaBean>());
        folderMap.put(allPhotosKey, allFolder);

//        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = context.getContentResolver();

        String[] projection = new String[]{
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_MODIFIED,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DISPLAY_NAME
        };

        // 只查询jpeg和png的图片
        Cursor mCursor = mContentResolver.query(imageUri, projection,
                MediaStore.Images.Media.MIME_TYPE + " in(?, ?)",
                new String[]{"image/jpeg", "image/png"},
                //asc 按升序排列
                //desc 按降序排列
                MediaStore.Images.Media.DATE_MODIFIED + " desc limit " + pageSize + " offset " + pageSize *
                        (pageIndex - 1));

        int pathIndex = mCursor
                .getColumnIndex(MediaStore.Images.Media.DATA);

        int dataModifiedIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED);
        int dataAddIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
        int nameIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);

        if (mCursor.moveToFirst()) {
            do {
                // 获取图片的路径
                String path = mCursor.getString(pathIndex);
                long dataModified = mCursor.getLong(dataModifiedIndex);
//                long dataAdd = mCursor.getLong(dataAddIndex);
//                String name = mCursor.getString(nameIndex);
//                Log.i("dataModified", "" + dataModified);

                // 获取该图片的父路径名
                File parentFile = new File(path).getParentFile();
                if (parentFile == null) {
                    continue;
                }
                String dirPath = parentFile.getAbsolutePath();

                MediaBean photo = new MediaBean(path);
                photo.setDataModify(dataModified);

                if (folderMap.containsKey(dirPath)) {
                    PhotoFolder photoFolder = folderMap.get(dirPath);
                    photoFolder.getPhotoList().add(photo);
                    folderMap.get(allPhotosKey).getPhotoList().add(photo);
                    continue;
                } else {
                    // 初始化imageFolder
                    PhotoFolder photoFolder = new PhotoFolder();
                    List<MediaBean> photoList = new ArrayList<>();
//                    MediaBean photo = new MediaBean(path);
//                    photoList.add(photo);
                    photoFolder.setPhotoList(photoList);
                    photoFolder.setDirPath(dirPath);
                    photoFolder.setName(dirPath.substring(dirPath.lastIndexOf(File.separator) + 1, dirPath.length()));
                    folderMap.put(dirPath, photoFolder);
                    folderMap.get(allPhotosKey).getPhotoList().add(photo);
                }
            } while (mCursor.moveToNext());
        }
//        Collections.sort(folderMap.get("all").getPhotoList());
        mCursor.close();
        return folderMap;
    }

    public static Map<String, PhotoFolder> getVideo(Context context, int pageSize, int pageIndex) {
        Map<String, PhotoFolder> folderMap = new HashMap<>();

        String allPhotosKey = "all";
        PhotoFolder allFolder = new PhotoFolder();
        allFolder.setName(allPhotosKey);
        allFolder.setDirPath(allPhotosKey);
        allFolder.setPhotoList(new ArrayList<MediaBean>());
        folderMap.put(allPhotosKey, allFolder);

//        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri imageUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = context.getContentResolver();

        // 只查询mp4
        Cursor mCursor = mContentResolver.query(imageUri, null,
                MediaStore.Video.Media.MIME_TYPE + " in(?)",
                new String[]{"video/mp4"},
                MediaStore.Video.Media.DATE_MODIFIED + " desc limit " + pageSize + " offset " + pageSize * (pageIndex
                        - 1));

        int pathIndex = mCursor.getColumnIndex(MediaStore.Video.Media.DATA);
        int dataModifiedIndex = mCursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED);

        int idIndex = mCursor.getColumnIndex(MediaStore.Video.Media._ID);
        if (mCursor.moveToFirst()) {
            do {
                // 获取图片的路径
                String path = mCursor.getString(pathIndex);
                int videoId = mCursor.getInt(idIndex);
                int duration = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                long size = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                if (size < 0) {
                    //某些设备获取size<0，直接计算
                    Log.e("dml", "this video size < 0 " + path);
                    size = new File(path).length();
                }

                // 获取该video的父路径名
//                String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.VideoBean.Media
// .DISPLAY_NAME));
                long modifyTime = mCursor.getLong(dataModifiedIndex);//暂未用到

                //TODO glide可以直接加载视频第一帧
                String thumbPath = "";
                //提前生成缩略图，再获取：http://stackoverflow
                // .com/questions/27903264/how-to-get-the-video-thumbnail-path-and-not-the-bitmap
                MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), videoId, MediaStore.Video
                        .Thumbnails.MICRO_KIND, null);
                String[] projection = {MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA};
                Cursor cursor = context.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI
                        , projection
                        , MediaStore.Video.Thumbnails.VIDEO_ID + "=?"
                        , new String[]{videoId + ""}
                        , null);
                while (cursor.moveToNext()) {
                    thumbPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                }
                cursor.close();


                File parentFile = new File(path).getParentFile();
                if (parentFile == null) {
                    continue;
                }
                String dirPath = parentFile.getAbsolutePath();

                VideoBean video = new VideoBean(path, thumbPath, duration);
                video.setDataModify(modifyTime);
                video.setSize(size);
                if (folderMap.containsKey(dirPath)) {
                    PhotoFolder photoFolder = folderMap.get(dirPath);
                    photoFolder.getPhotoList().add(video);
                    folderMap.get(allPhotosKey).getPhotoList().add(video);
                    continue;
                } else {
                    // 初始化imageFolder
                    PhotoFolder photoFolder = new PhotoFolder();
                    List<MediaBean> photoList = new ArrayList<>();
//                    VideoBean photo = new VideoBean(path, thumbPath, duration);
//                    photoList.add(photo);
                    photoFolder.setPhotoList(photoList);
                    photoFolder.setDirPath(dirPath);
                    photoFolder.setName(dirPath.substring(dirPath.lastIndexOf(File.separator) + 1, dirPath.length()));
                    folderMap.put(dirPath, photoFolder);
                    folderMap.get(allPhotosKey).getPhotoList().add(video);
                }
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        return folderMap;
    }

    /**
     * 根据视频地址获取缩略图，并将缩略图保存到一定位置
     * @param videoPath
     * @return
     */
    public static String getVideoThumbPath(String videoPath) {
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(videoPath);
        Bitmap frameAtTime = retr.getFrameAtTime();

        int w = frameAtTime.getWidth();
        int h = frameAtTime.getHeight();
        while (w > 600 || h > 600) {
            w *= 0.5;
            h *= 0.5;
        }

//        Bitmap bm = Bitmap.createBitmap(frameAtTime, 0, 0, frameAtTime.getWidth(),
//                frameAtTime.getHeight(), matrix, true);
        Bitmap bm = Bitmap.createScaledBitmap(frameAtTime, w, h, true);

        if (!PathUtil.getInstance().getVideoPath().exists())
            PathUtil.getInstance().getVideoPath().mkdirs();
        String thumbPath = PathUtil
                .getInstance().getVideoPath() + "/"
                + System.currentTimeMillis() + "out.jpg";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bm.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，把压缩后的数据存放到baos中 (100表示不压缩，0表示压缩到最小)
        while (baos.toByteArray().length > 50 * 1024) {//循环判断如果压缩后图片是否大于指定大小,大于继续压缩
            baos.reset();//重置baos即让下一次的写入覆盖之前的内容
            options -= 5;//图片质量每次减少5
            if (options <= 5)
                options = 5;//如果图片质量小于5，为保证压缩后的图片质量，图片最底压缩质量为5
            bm.compress(Bitmap.CompressFormat.JPEG, options, baos);//将压缩后的图片保存到baos中
            if (options == 5)
                break;//如果图片的质量已降到最低则，不再进行压缩
        }

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(thumbPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        bm.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

        try {
            fileOutputStream.write(baos.toByteArray());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bm.recycle();
        frameAtTime.recycle();
        return thumbPath;
    }


    /**
     * <p>加载所有包含图片的文件夹</p>
     */
    public static List<PhotoFolder> loadLocalFolderContainsImage(final Context context) {

        ArrayList<PhotoFolder> imageFolders = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
                /*查询id、  缩略图、原图、文件夹ID、 文件夹名、 文件夹分类的图片总数*/
        String[] columns = {MediaStore.Images.Media._ID, MediaStore.Images.Thumbnails.DATA, MediaStore.Images.Media
                .DATA, MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, "COUNT(1) AS count"};
        String selection = "0==0) and "+MediaStore.Images.Media.MIME_TYPE + " in(?, ?)"+" GROUP BY (" + MediaStore.Images.Media.BUCKET_ID;
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED;
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, selection, new String[]{"image/jpeg", "image/png"},
                    sortOrder);
            if (cursor != null && cursor.moveToFirst()) {

                int columnPath = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int columnId = cursor.getColumnIndex(MediaStore.Images.Media._ID);

                int columnFileName = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                int columnCount = cursor.getColumnIndex("count");

                do {
                    PhotoFolder folderBean = new PhotoFolder();
                    String path = cursor.getString(columnPath);
                    folderBean.setPath(path);
                    folderBean.setNum(cursor.getInt(columnCount));
                    folderBean.setDirPath(new File(path).getParentFile().getAbsolutePath());
//                    folderBean.path = cursor.getString(columnPath);
//                    folderBean._id = cursor.getInt(columnId);
//                    folderBean.pisNum = cursor.getInt(columnCount);
//                    folderBean.

                    String bucketName = cursor.getString(columnFileName);
//                    folderBean.fileName = bucketName;
                    folderBean.setName(bucketName);

//                    if (!Environment.getExternalStorageDirectory().getPath().contains(bucketName)) {
                    imageFolders.add(0, folderBean);
//                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return imageFolders;
    }


    public static List<MediaBean> queryGalleryPicture(final Context c, String folderPath){
        return queryGalleryPicture(c, folderPath, null);
    }
    /**
     * 获取相册指定目录下的全部图片路径
     */
    public static List<MediaBean> queryGalleryPicture(final Context c, String folderPath, List<MediaBean> selects) {
        List<MediaBean> list = new ArrayList<>();
        String[] columns = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};

                /*查询文件路径包含上面指定的文件夹路径的图片--这样才能保证查询到的文件属于当前文件夹下*/
//        String whereclause = MediaStore.Images.ImageColumns.DATA + " like'" + folderPath + "/%'";
        String whereclause = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =? and "
                +MediaStore.Images.Media.MIME_TYPE + " in(?, ?)";
        Log.i("queryGalleryPicture", "galleryPath:" + folderPath);

        Cursor corsor = null;

//        List<MediaBean> selects = ImageSelectObservable.getInstance().getSelectImages();

        try {
            corsor = c.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, whereclause,
                    new String[]{folderPath.substring(folderPath.lastIndexOf(File.separator)+1),"image/jpeg", "image/png"},
                    null);
            if (corsor != null && corsor.getCount() > 0 && corsor.moveToFirst()) {
                do {
                    String path = corsor.getString(corsor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                    int id = corsor.getInt(corsor.getColumnIndex(MediaStore.Images.ImageColumns._ID));

                    MediaBean photoItem = new MediaBean(path);

                    /**遍历查询之前选择的图片是否在其中*/
                    for (int index = 0, len = selects == null ? 0 : selects.size(); index < len; index++) {
                        if (selects.get(index).getPath().equals(photoItem.getPath())) {
//                            photoItem.selectPosition = selects.get(index).selectPosition;
//                            selects.remove(index);
//                            selects.add(photoItem);
                            photoItem = selects.get(index);
                            break;
                        }
                    }

                    list.add(0, photoItem);
                } while (corsor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (corsor != null)
                corsor.close();
        }

        return list;
    }
}
