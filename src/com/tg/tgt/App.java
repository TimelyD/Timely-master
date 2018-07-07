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
package com.tg.tgt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.easemob.redpacketsdk.RPInitRedPacketCallback;
import com.easemob.redpacketsdk.RPValueCallback;
import com.easemob.redpacketsdk.RedPacket;
import com.easemob.redpacketsdk.bean.RedPacketInfo;
import com.easemob.redpacketsdk.bean.TokenData;
import com.easemob.redpacketsdk.constant.RPConstant;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMJobService;
import com.hyphenate.easeui.EaseApp;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.L;
import com.marswin89.marsdaemon.DaemonClient;
import com.marswin89.marsdaemon.DaemonConfigurations;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.bugly.crashreport.CrashReport;
import com.tg.tgt.keepservice.Receiver1;
import com.tg.tgt.keepservice.Receiver2;
import com.tg.tgt.keepservice.Service2;
import com.tg.tgt.moment.bean.PicBean;

import java.util.ArrayList;
import java.util.List;

public class App extends EaseApp {

	public static String xin="******";
	private static App instance;
	public static List<PicBean> pic=new ArrayList<>();

	/**
	 * 通过这个值来判断是否需要刷新通讯录，如果为空则不刷新
	 */
	public static String needRefresh;
	// login user name
	public final String PREF_USERNAME = "username";
	
	/**
	 * nickname for current user, the nickname instead of ID be shown when user receive notification from APNs
	 */
	public static String currentUserNick = "";
	private  static RefWatcher mRefWatcher;
	public static SharedPreferences sf;
	public static Boolean zq;


	//6.0以下的常驻service
	private DaemonClient mDaemonClient;

	@Override
	public void onCreate() {
		MultiDex.install(this);
		super.onCreate();
        applicationContext = this;
        instance = this;
		sf= PreferenceManager.getDefaultSharedPreferences(this);
		zq=sf.getBoolean("zq",false);
		//init demo helper
        DemoHelper.getInstance().init(applicationContext);
		//red packet code : 初始化红包SDK，开启日志输出开关
		RedPacket.getInstance().initRedPacket(applicationContext, RPConstant.AUTH_METHOD_EASEMOB, new RPInitRedPacketCallback() {

			@Override
			public void initTokenData(RPValueCallback<TokenData> callback) {
				TokenData tokenData = new TokenData();
				tokenData.imUserId = EMClient.getInstance().getCurrentUser();
				//此处使用环信id代替了appUserId 开发者可传入App的appUserId
				tokenData.appUserId = EMClient.getInstance().getCurrentUser();
				tokenData.imToken = EMClient.getInstance().getAccessToken();
				//同步或异步获取TokenData 获取成功后回调onSuccess()方法
				callback.onSuccess(tokenData);
			}

			@Override
			public RedPacketInfo initCurrentUserSync() {
				//这里需要同步设置当前用户id、昵称和头像url
				String fromAvatarUrl = "";
				String fromNickname = EMClient.getInstance().getCurrentUser();
				EaseUser easeUser = EaseUserUtils.getUserInfo(fromNickname);
				if (easeUser != null) {
					fromAvatarUrl = TextUtils.isEmpty(easeUser.getAvatar()) ? "none" : easeUser.getAvatar();
					fromNickname = TextUtils.isEmpty(easeUser.getNick()) ? easeUser.getUsername() : easeUser.getNick();
				}
				RedPacketInfo redPacketInfo = new RedPacketInfo();
				redPacketInfo.fromUserId = EMClient.getInstance().getCurrentUser();
				redPacketInfo.fromAvatarUrl = fromAvatarUrl;
				redPacketInfo.fromNickName = fromNickname;
				return redPacketInfo;
			}
		});
		RedPacket.getInstance().setDebugMode(true);
		//end of red packet code

		CrashReport.initCrashReport(getApplicationContext(), "fc44b98036", BuildConfig.DEBUG);
		L.isDebug = BuildConfig.IS_DEBUG;

		if (LeakCanary.isInAnalyzerProcess(this)) {
			// This process is dedicated to LeakCanary for heap analysis.
			// You should not init your app in this process.
			return;
		}
		enabledStrictMode();
		mRefWatcher = LeakCanary.install(this);

	}

	private static void enabledStrictMode() {
		if(!BuildConfig.IS_DEBUG)
			return;
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() //
				.detectAll() //
				.penaltyLog() //
				.penaltyDeath() //
				.build());
	}

	public static App getInstance() {
		return instance;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
		mDaemonClient = new DaemonClient(createDaemonConfigurations());
		mDaemonClient.onAttachBaseContext(base);
	}



	public static RefWatcher getRefWatcher() {
		return mRefWatcher;
	}
	public static String toJson(Object obj,int method) {
		// TODO Auto-generated method stub
		if (method==1) {
			//字段是首字母小写，其余单词首字母大写
			Gson gson = new Gson();
			String obj2 = gson.toJson(obj);
			return obj2;
		}else if(method==2){
			// FieldNamingPolicy.LOWER_CASE_WITH_DASHES    全部转换为小写，并用空格或者下划线分隔
			//FieldNamingPolicy.UPPER_CAMEL_CASE    所以单词首字母大写
			Gson gson2=new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
			String obj2=gson2.toJson(obj);
			return obj2;
		}
		return "";
	}

	private DaemonConfigurations createDaemonConfigurations(){
		DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
				"com.marswin89.marsdaemon.demo:process1",
				EMJobService.class.getCanonicalName(),
				Receiver1.class.getCanonicalName());
		DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
				"com.marswin89.marsdaemon.demo:process2",
				Service2.class.getCanonicalName(),
				Receiver2.class.getCanonicalName());
		DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
		//return new DaemonConfigurations(configuration1, configuration2);//listener can be null
		return new DaemonConfigurations(configuration1, configuration2, listener);
	}


	class MyDaemonListener implements DaemonConfigurations.DaemonListener{
		@Override
		public void onPersistentStart(Context context) {
		}

		@Override
		public void onDaemonAssistantStart(Context context) {
		}

		@Override
		public void onWatchDaemonDaed() {
		}
	}

}
