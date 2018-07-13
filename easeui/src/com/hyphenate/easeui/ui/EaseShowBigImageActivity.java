/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.EaseImageCache;
import com.hyphenate.easeui.utils.EaseLoadLocalBigImgTask;
import com.hyphenate.easeui.utils.MenuDialogUtils;
import com.hyphenate.easeui.widget.photoselect.PreviewImageActivity;
import com.hyphenate.easeui.widget.photoview.EasePhotoView;
import com.hyphenate.util.EMLog;
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

/**
 * download and show original image
 * 
 */
public class EaseShowBigImageActivity extends EaseBaseActivity {
	private static final String TAG = "ShowBigImage"; 
	private ProgressDialog pd;
	private EasePhotoView image;
	private View rl;
	private int default_res = com.hyphenate.easeui.R.drawable.ease_default_image;
	private String localFilePath;
	private Bitmap bitmap;
	private boolean isDownloaded;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(com.hyphenate.easeui.R.layout.ease_activity_show_big_image);
		super.onCreate(savedInstanceState);
		/*全屏*/
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		image = (EasePhotoView) findViewById(com.hyphenate.easeui.R.id.image);
		rl =findViewById(R.id.rl);
		ProgressBar loadLocalPb = (ProgressBar) findViewById(com.hyphenate.easeui.R.id.pb_load_local);
		default_res = getIntent().getIntExtra("default_image", com.hyphenate.easeui.R.drawable.ease_default_avatar);
		Uri uri = getIntent().getParcelableExtra("uri");
		localFilePath = getIntent().getExtras().getString("localUrl");
		String msgId = getIntent().getExtras().getString("messageId");
		EMLog.d(TAG, "show big msgId:" + msgId );
		if (uri != null && new File(uri.getPath()).exists()) {
			EMLog.d(TAG, "showbigimage file exists. directly show it");
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			// int screenWidth = metrics.widthPixels;
			// int screenHeight =metrics.heightPixels;
			bitmap = EaseImageCache.getInstance().get(uri.getPath());
			if (bitmap == null) {
				EaseLoadLocalBigImgTask task = new EaseLoadLocalBigImgTask(this, uri.getPath(), image, loadLocalPb, ImageUtils.SCALE_IMAGE_WIDTH,ImageUtils.SCALE_IMAGE_HEIGHT);
				if (android.os.Build.VERSION.SDK_INT > 10) {
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					task.execute();
				}
			//	SavePicUtil.saveUrl(image,EaseShowBigImageActivity.this,uri.getPath());
			} else {
				image.setImageBitmap(bitmap);
				//SavePicUtil.save(image,EaseShowBigImageActivity.this,bitmap);
			}
		} else if(msgId != null) {
		    downloadImage(msgId);
		}else {
			image.setImageResource(default_res);
		}

		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		rl.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				new MenuDialogUtils(EaseShowBigImageActivity.this, R.style.registDialog, 1,R.layout.menu_save, new MenuDialogUtils.ButtonClickListener() {
					@Override
					public void onButtonClick(int i) {
						if (i == 0) {
							saveBmp2Gallery(bitmap, System.currentTimeMillis()+"APP");
						}
					}
				}, new MenuDialogUtils.ButtonClickCollectListener() {
					@Override
					public void onButtonCollectClick() {
					}
				}).show();
				return false;
			}
		});
	}
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
		MediaStore.Images.Media.insertImage(EaseShowBigImageActivity.this.getContentResolver(),bmp, fileName, null);
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri uri = Uri.fromFile(file);
		intent.setData(uri);
		EaseShowBigImageActivity.this.sendBroadcast(intent);
		Toast.makeText(EaseShowBigImageActivity.this,"保存成功",Toast.LENGTH_LONG).show();
		// ToastUtils.s(getString(R.string.toast_save_successful));
	}
	@Override
	protected boolean isTran() {
		return true;
	}
	public static void savePicture(Bitmap bitmap) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			try {
//                File sdcardDir = Environment
//                        .getExternalStorageDirectory();
//                String filename = sdcardDir.getCanonicalPath() + System.currentTimeMillis() + ".jpg";
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
					EaseShowBigImageActivity.this.sendBroadcast(intent);
					Toast.makeText(EaseShowBigImageActivity.this,"保存成功",Toast.LENGTH_LONG).show();


				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
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
	 * download image
	 * 
	 * @param remoteFilePath
	 */
	@SuppressLint("NewApi")
	private void downloadImage(final String msgId) {
        EMLog.e(TAG, "download with messageId: " + msgId);
		String str1 = getResources().getString(com.hyphenate.easeui.R.string.Download_the_pictures);
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage(str1);
		pd.show();
		File temp = new File(localFilePath);
		final String tempPath = temp.getParent() + "/temp_" + temp.getName();
		final EMCallBack callback = new EMCallBack() {
			public void onSuccess() {
			    EMLog.e(TAG, "onSuccess" );
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
                        new File(tempPath).renameTo(new File(localFilePath));

                        DisplayMetrics metrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(metrics);
						int screenWidth = metrics.widthPixels;
						int screenHeight = metrics.heightPixels;

						bitmap = ImageUtils.decodeScaleImage(localFilePath, screenWidth, screenHeight);
						if (bitmap == null) {
							image.setImageResource(default_res);
						} else {
							image.setImageBitmap(bitmap);
							EaseImageCache.getInstance().put(localFilePath, bitmap);
							isDownloaded = true;
						}
						if (isFinishing() || isDestroyed()) {
						    return;
						}
						if (pd != null) {
							pd.dismiss();
						}
					}
				});
			}

			public void onError(int error, String msg) {
				EMLog.e(TAG, "offline file transfer error:" + msg);
				File file = new File(tempPath);
				if (file.exists()&&file.isFile()) {
					file.delete();
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (EaseShowBigImageActivity.this.isFinishing() || EaseShowBigImageActivity.this.isDestroyed()) {
						    return;
						}
                        image.setImageResource(default_res);
                        pd.dismiss();
					}
				});
			}

			public void onProgress(final int progress, String status) {
				EMLog.d(TAG, "Progress: " + progress);
				final String str2 = getResources().getString(com.hyphenate.easeui.R.string.Download_the_pictures_new);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
                        if (EaseShowBigImageActivity.this.isFinishing() || EaseShowBigImageActivity.this.isDestroyed()) {
                            return;
                        }
						pd.setMessage(str2 + progress + "%");
					}
				});
			}
		};
		
		EMMessage msg = EMClient.getInstance().chatManager().getMessage(msgId);
		msg.setMessageStatusCallback(callback);

		EMLog.e(TAG, "downloadAttachement");
		EMClient.getInstance().chatManager().downloadAttachment(msg);
	}

	@Override
	public void onBackPressed() {
		if (isDownloaded)
			setResult(RESULT_OK);
		finish();
	}
}
