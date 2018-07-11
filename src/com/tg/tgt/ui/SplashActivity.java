package com.tg.tgt.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.DemoHelper;
import com.hyphenate.easeui.utils.PhoneUtil;
import com.tg.tgt.utils.SharedPreStorageMgr;
import com.hyphenate.util.EasyUtils;

import java.util.Locale;

/**
 * 开屏页
 *
 */
public class SplashActivity extends BaseActivity {
	private static final int sleepTime = 2000;
	@Override
	protected void onCreate(Bundle arg0) {
		changeLan();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(com.tg.tgt.R.layout.em_activity_splash);
		super.onCreate(arg0);
		RelativeLayout rootLayout = (RelativeLayout) findViewById(com.tg.tgt.R.id.splash_root);
		TextView versionText = (TextView) findViewById(com.tg.tgt.R.id.tv_version);
		versionText.setText(getVersion());
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);
	}

	@Override
	protected void onStart() {
		super.onStart();
			new Thread(new Runnable() {
				public void run() {
					if(App.sf.getBoolean("first",true)){
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
						}
						startActivity(new Intent(SplashActivity.this,WelcomeActivity.class));
						finish();
					}else if (DemoHelper.getInstance().isLoggedIn()) {
						// 自动登录模式，确保所有组和对话在进入主屏幕前都被删除。
						long start = System.currentTimeMillis();
						EMClient.getInstance().chatManager().loadAllConversations();
						EMClient.getInstance().groupManager().loadAllGroups();
						long costTime = System.currentTimeMillis() - start;
						//等待
						if (sleepTime - costTime > 0) {
							try {
								Thread.sleep(sleepTime - costTime);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						String topActivityName = EasyUtils.getTopActivityName(EMClient.getInstance().getContext());
						if (topActivityName != null && (topActivityName.equals(VideoCallActivity.class.getName()) || topActivityName.equals(VoiceCallActivity.class.getName()))) {
						} else {
							startActivity(new Intent(SplashActivity.this, MainActivity.class));
						}
						finish();
					}else {
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
						}
						startActivity(new Intent(SplashActivity.this, LoginActivity.class));
						finish();
					}
				}
			}).start();
		}
	
	/**
	 * get sdk version
	 */
	private String getVersion() {
		return PhoneUtil.getVerName(this);
//	    return EMClient.getInstance().VERSION;
	}

	private void changeLan() {
		int value = SharedPreStorageMgr.getIntance().getIntegerValue(SharedPreStorageMgr.PREFSNAMES, this,
				Constant.LANGUAGE);
		if(value == -1){
			return;
		}
		Resources resources = getResources();
		Configuration config = resources.getConfiguration();
		// 应用用户选择语言
		if(value == 0){//默认
			config.locale = Locale.getDefault();
		}else if(value == 2){//英文
			config.locale = Locale.ENGLISH;
		}else if(value == 1){//中文
			config.locale = Locale.CHINESE;
		}
		DisplayMetrics dm = resources.getDisplayMetrics();
		resources.updateConfiguration(config, dm);
	}

	@Override
	protected boolean isTran() {
		return true;
	}
}
