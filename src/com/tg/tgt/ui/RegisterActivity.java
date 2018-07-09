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
package com.tg.tgt.ui;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyphenate.easeui.utils.PhotoUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpHelper;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.model2.NonceBean;
import com.tg.tgt.utils.CodeUtils;
import com.tg.tgt.utils.RSAHandlePwdUtil;
import com.tg.tgt.utils.ToastUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import top.zibin.luban.Luban;

/**
 * register screen
 * 
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
	private EditText userNameEditText;
	private EditText passwordEditText;
	private EditText confirmPwdEditText;
	private EditText codeEditText;
	private String imgPath="";
	private ImageView headIv;
	private Button mGetCodeBtn;
	private EditText nickNameEditText;
	private ImageView mPwdTypeIv;

	private void setFocus(final View v) {
		RxView.focusChanges(v)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Consumer<Boolean>() {
					@Override
					public void accept(@NonNull Boolean aBoolean) throws Exception {
						if(aBoolean){
							((View)v.getParent()).setSelected(true);
						}else {
							((View)v.getParent()).setSelected(false);
						}
					}
				});
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.tg.tgt.R.layout.em_activity_register);
		mImmersionBar
				.statusBarColor(com.hyphenate.easeui.R.color.white)
				.statusBarDarkFont(true, 0.5f)
				.init();
		userNameEditText = (EditText) findViewById(com.tg.tgt.R.id.username);
		nickNameEditText = (EditText) findViewById(R.id.nickname_et);
		codeEditText = (EditText) findViewById(R.id.code_et);
		mPwdTypeIv = (ImageView) findViewById(R.id.password_type_iv);
		mGetCodeBtn = (Button) findViewById(R.id.get_code_btn);
		headIv = (ImageView) findViewById(R.id.head_iv);
		passwordEditText = (EditText) findViewById(com.tg.tgt.R.id.password);
		confirmPwdEditText = (EditText) findViewById(com.tg.tgt.R.id.confirm_password);

		//userNameEditText.setFilters(new InputFilter[]{CodeUtils.filter});
		nickNameEditText.setFilters(new InputFilter[]{CodeUtils.fil});
		//codeEditText.setFilters(new InputFilter[]{CodeUtils.filter});
		passwordEditText.setFilters(new InputFilter[]{CodeUtils.fil});
		confirmPwdEditText.setFilters(new InputFilter[]{CodeUtils.fil});


		setFocus(userNameEditText);
		setFocus(nickNameEditText);
		setFocus(codeEditText);
		setFocus(passwordEditText);
		setFocus(confirmPwdEditText);

		mPwdTypeIv.setOnClickListener(this);
		confirmPwdEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) &&
						(event.getAction() == KeyEvent.ACTION_DOWN))) {
					register(null);
					return true;
				} else {
					return false;
				}
			}
		});

		initSpinner();

		File file = new File(Environment.getExternalStorageDirectory(), "ClipHeadPhoto/cache/");
		if (!file.exists())
			file.mkdirs();
		photoSavePath = Environment.getExternalStorageDirectory() + "/ClipHeadPhoto/cache/";
		photoSaveName = System.currentTimeMillis() + ".png";
	}

	private String mEmailLast = "+86";
	private android.widget.Spinner emailspinner;
	private void initSpinner() {
		this.emailspinner = (Spinner) findViewById(R.id.email_spinner);
		final String[] strings = getResources().getStringArray(R.array.emails);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.emails, R
				.layout.spinner_item);

		emailspinner.setAdapter(adapter);
		emailspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mEmailLast = strings[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	public void register(View view) {
		final String nickname = nickNameEditText.getText().toString().trim();
		final String username = userNameEditText.getText().toString().trim();
		final String pwd = passwordEditText.getText().toString().trim();
		String confirm_pwd = confirmPwdEditText.getText().toString().trim();
		final String code = codeEditText.getText().toString().trim();
		if (TextUtils.isEmpty(nickname)) {
			Toast.makeText(this, R.string.nickname_cannot_be_empty, Toast.LENGTH_SHORT).show();
			nickNameEditText.requestFocus();
			return;
		}else if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, getResources().getString(com.tg.tgt.R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
			userNameEditText.requestFocus();
			return;
		}else if(TextUtils.isEmpty(code)){
			Toast.makeText(this, R.string.code_cannot_empty, Toast.LENGTH_SHORT).show();
			codeEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, getResources().getString(com.tg.tgt.R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
			passwordEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(confirm_pwd)) {
			Toast.makeText(this, getResources().getString(com.tg.tgt.R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
			confirmPwdEditText.requestFocus();
			return;
		} else if (pwd.length() < 6 || confirm_pwd.length()<6) {
			Toast.makeText(this, getResources().getString(com.tg.tgt.R.string.register_editpassword), Toast.LENGTH_SHORT).show();
			passwordEditText.requestFocus();
			return;
		} else if (!pwd.equals(confirm_pwd)) {
			Toast.makeText(this, getResources().getString(com.tg.tgt.R.string.Two_input_password), Toast.LENGTH_SHORT).show();
			return;
		}
		if(TextUtils.isEmpty(imgPath)) {
			ToastUtils.showToast(App.applicationContext, R.string.ti4);
			return;
		}
		ApiManger2.getApiService()
				.servernonce(Constant.MYUID)
				.compose(this.<HttpResult<NonceBean>>bindToLifeCyclerAndApplySchedulers(null))
				.subscribe(new BaseObserver2<NonceBean>() {
					@Override
					protected void onSuccess(NonceBean emptyData) {
						String ps = pwd + "#" + emptyData.getValue();
						Log.i("dcz",ps);
						try {
							String mi = RSAHandlePwdUtil.jia(ps);
							regis(nickname,username,mi,code,emptyData.getKey());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

//		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
//			final ProgressDialog pd = new ProgressDialog(this);
//			pd.setMessage(getResources().getString(R.string.Is_the_registered));
//			pd.show();
//
//			new Thread(new Runnable() {
//				public void run() {
//					try {
//						// call method in SDK
//						EMClient.getInstance().createAccount(username, pwd);
//						runOnUiThread(new Runnable() {
//							public void run() {
//								if (!RegisterActivity.this.isFinishing())
//									pd.dismiss();
//								// save current user
//								DemoHelper.getInstance().setCurrentUserName(username);
//								Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
//								finish();
//							}
//						});
//					} catch (final HyphenateException e) {
//						runOnUiThread(new Runnable() {
//							public void run() {
//								if (!RegisterActivity.this.isFinishing())
//									pd.dismiss();
//								int errorCode=e.getErrorCode();
//								if(errorCode==EMError.NETWORK_ERROR){
//									Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
//								}else if(errorCode == EMError.USER_ALREADY_EXIST){
//									Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
//								}else if(errorCode == EMError.USER_AUTHENTICATION_FAILED){
//									Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
//								}else if(errorCode == EMError.USER_ILLEGAL_ARGUMENT){
//								    Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name),Toast.LENGTH_SHORT).show();
//								}else{
//									Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed), Toast.LENGTH_SHORT).show();
//								}
//							}
//						});
//					}
//				}
//			}).start();

//		}
	}

	private void regis(final String nickname, final String username, final String pwd, final String code, final String nonce){
		Observable.just(imgPath).flatMap(new Function<String, ObservableSource<HttpResult<EmptyData>>>() {
			@Override
			public ObservableSource<HttpResult<EmptyData>> apply(@NonNull String s) throws Exception {
				Observable<HttpResult<EmptyData>> register;
				if(TextUtils.isEmpty(imgPath)) {
					register = ApiManger2.getApiService().regist(nickname, username, pwd, code,mEmailLast.trim(),nonce);
				}else {
					File file = Luban.with(mActivity).load(imgPath).setTargetDir(PhotoUtils.getTempDirPath
							(mContext)).get(imgPath);
					register = ApiManger2.getApiService().regist(HttpHelper.getPicPart("picture", file.getPath()),
							HttpHelper.toTextPlain(nickname),
							HttpHelper.toTextPlain(username),
							HttpHelper.toTextPlain(pwd),
							HttpHelper.toTextPlain(code),
							HttpHelper.toTextPlain(mEmailLast.trim()),HttpHelper.toTextPlain(nonce));
				}
				return register;
			}
		})
				.compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers())
				.subscribe(new BaseObserver2<EmptyData>() {
					@Override
					protected void onSuccess(EmptyData emptyData) {
						ToastUtils.showToast(App.applicationContext, R.string.Registered_successfully);
						finish();
					}
				});
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.hold, R.anim.topbottom_hidden_anim);
	}

	public void back(View view) {
		finish();
		overridePendingTransition(R.anim.hold, R.anim.topbottom_hidden_anim);
	}

	public void toRegisterEmail(View view) {
		Uri uri = Uri.parse("http://www.qeveworld.com/mail.php/Register/index");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}


	public void getCode(View view) {
		final String nickname = nickNameEditText.getText().toString().trim();
		final String username = userNameEditText.getText().toString().trim();
		if (TextUtils.isEmpty(nickname)) {
			Toast.makeText(this, getResources().getString(com.tg.tgt.R.string.input_nick), Toast.LENGTH_SHORT).show();
			nickNameEditText.requestFocus();
			return;
		}
		if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, getResources().getString(com.tg.tgt.R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
			userNameEditText.requestFocus();
			return;
		}
		/*ApiManger.getApiService()
				.getCode(username, mEmailLast)
				.compose(RxUtils.<BaseHttpResult>applySchedulers())
				.subscribe(new BaseObserver<BaseHttpResult>(this) {
					@Override
					protected void onSuccess(BaseHttpResult result) {
						ToastUtils.showToast(App.applicationContext, result.getTocon());
						count();
					}
				});*/
		ApiManger2.getApiService()
				.sendRegistSms(username,mEmailLast.trim())
				.compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers())
				.subscribe(new BaseObserver2<EmptyData>() {
					@Override
					protected void onSuccess(EmptyData emptyData) {
						count();
						CodeUtils.showToEmailDialog(mActivity);
					}
				});
	}

	/**
	 * 倒计时秒数
	 */
	private int second ;
	private Disposable d;
	private void count() {
		second = 60;
		Observable.interval(1, TimeUnit.SECONDS)
				.take(second + 1)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Long>() {
					@Override
					public void onSubscribe(@NonNull Disposable d) {
						mGetCodeBtn.setEnabled(false);
						RegisterActivity.this.d = d;
					}

					@Override
					public void onNext(@NonNull Long aLong) {
						mGetCodeBtn.setText(""+(second -aLong)+"s");
					}

					@Override
					public void onError(@NonNull Throwable e) {

					}

					@Override
					public void onComplete() {
						mGetCodeBtn.setEnabled(true);
						mGetCodeBtn.setText(R.string.get_code);
					}
				});
	}

	@Override
	protected void onDestroy() {
		if(d != null && !d.isDisposed()){
			d.dispose();
		}
		super.onDestroy();
	}

	public void uploadHead(View view) {
		showPhotoDialog();
	}

	private String photoSaveName;//图片名
	private String photoSavePath;//保存路径
	private String path;//图片全路径
	public static final int PHOTOZOOM = 0; // 相册/拍照
	public static final int PHOTOTAKE = 1; // 相册/拍照
	public static final int IMAGE_COMPLETE = 2; //  结果
	/**
	 * 头像
	 */
	private void showPhotoDialog() {
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
				photoSaveName = String.valueOf(System.currentTimeMillis()) + ".png";
				Uri imageUri = null;
				Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				imageUri = Uri.fromFile(new File(photoSavePath, photoSaveName));
				openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(openCameraIntent, PHOTOTAKE);
			}
		});

		albums.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				Intent openAlbumIntent = new Intent(Intent.ACTION_PICK);
				openAlbumIntent.setType("image/*");
				startActivityForResult(openAlbumIntent, PHOTOZOOM);
			}
		});

		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();

			}
		});
	}

	/**
	 * 图片选择及拍照结果
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		Uri uri = null;
		switch (requestCode) {
			case PHOTOZOOM://相册
				if (data == null) {
					return;
				}
				uri = data.getData();
				uri = geturi(data);
				String[] proj = {MediaStore.Images.Media.DATA};
				Cursor cursor = managedQuery(uri, proj, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				path = cursor.getString(column_index);// 图片在的路径
				/*Intent intent3 = new Intent(RegisterActivity.this, ClipPicAct.class);
				intent3.putExtra("path", path);
				intent3.putExtra(Constant.NOT_FOR_USER, true);
				startActivityForResult(intent3, IMAGE_COMPLETE);*/
				imgPath = path;
				showHead();
				break;
			case PHOTOTAKE://拍照
				path = photoSavePath + photoSaveName;
				imgPath = path;
				showHead();
				/*uri = Uri.fromFile(new File(path));
				Intent intent2 = new Intent(RegisterActivity.this, ClipPicAct.class);
				intent2.putExtra("path", path);
				intent2.putExtra(Constant.NOT_FOR_USER, true);
				startActivityForResult(intent2, IMAGE_COMPLETE);*/
				break;
			case IMAGE_COMPLETE:
				imgPath = data.getStringExtra("path");
				Glide.with(RegisterActivity.this).load(imgPath).into(headIv);
				//head.setImageBitmap(getLoacalBitmap(temppath));
				break;
			default:
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showHead(){
		Glide.with(RegisterActivity.this).load(imgPath).into(headIv);
	}

	/**
	 * 解决小米手机上获取图片路径为null的情况
	 * @param intent
	 * @return
	 */
	public Uri geturi(android.content.Intent intent) {
		Uri uri = intent.getData();
		String type = intent.getType();
		if (uri.getScheme().equals("file") && (type.contains("image/"))) {
			String path = uri.getEncodedPath();
			if (path != null) {
				path = Uri.decode(path);
				ContentResolver cr = this.getContentResolver();
				StringBuffer buff = new StringBuffer();
				buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
						.append("'" + path + "'").append(")");
				Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						new String[] { MediaStore.Images.ImageColumns._ID },
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.password_type_iv:
				changePwdType();
				break;

		}
	}
	//以下控制密码显示 true表示隐藏
	private boolean pwTypeFlag = true;
	public void changePwdType(){

		if (pwTypeFlag) {
			mPwdTypeIv.setImageDrawable(getResources().getDrawable(R.drawable.eye_open));
			passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
			pwTypeFlag = false;
		}else{
			mPwdTypeIv.setImageDrawable(getResources().getDrawable(R.drawable.eye_close));
			passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
			pwTypeFlag = true;
		}
		//切换后将EditText光标置于尾部
		CharSequence charSequence = passwordEditText.getText();
		if (charSequence instanceof Spannable) {
			Spannable spanText = (Spannable) charSequence;
			Selection.setSelection(spanText, charSequence.length());
		}
	}
}
