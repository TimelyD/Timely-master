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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
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
		/*image.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				new MenuDialogUtils(EaseShowBigImageActivity.this, R.style.registDialog, R.layout.menu_save, new MenuDialogUtils.ButtonClickListener() {
					@Override
					public void onButtonClick(int i) {
						if (i == 0) {
							if (!TextUtils.isEmpty()) {
								MenuDialogUtils.saveImage(mAllImage.get(mCurrentPosition).getPath());
								Toast.makeText(EaseShowBigImageActivity.this,"保存成功",Toast.LENGTH_LONG).show();
							}else {
								Toast.makeText(EaseShowBigImageActivity.this, "图片加载中，无法保存", Toast.LENGTH_LONG).show();
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
		});*/
		rl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected boolean isTran() {
		return true;
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
