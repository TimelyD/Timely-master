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

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.redpacket.utils.RedPacketUtil;
import com.easemob.redpacketsdk.constant.RPConstant;
import com.gyf.barlibrary.ImmersionBar;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.ImageUtils;
import com.hyphenate.easeui.utils.L;
import com.hyphenate.easeui.utils.NotificationsUtils;
import com.hyphenate.easeui.utils.SpUtils;
import com.hyphenate.util.EMLog;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.DemoHelper;
import com.tg.tgt.R;
import com.tg.tgt.db.InviteMessgeDao;
import com.tg.tgt.db.UserDao;
import com.tg.tgt.helper.ActionItem;
import com.tg.tgt.helper.DBManager;
import com.tg.tgt.helper.MenuPopup;
import com.tg.tgt.helper.UserHelper;
import com.tg.tgt.logger.Logger;
import com.tg.tgt.moment.ui.activity.MomentAct;
import com.tg.tgt.runtimepermissions.PermissionsManager;
import com.tg.tgt.runtimepermissions.PermissionsResultAction;
import com.tg.tgt.utils.CodeUtils;
import com.tg.tgt.utils.MobileInfoUtils;
import com.tg.tgt.utils.SharedPreStorageMgr;
import com.uuzuche.lib_zxing.activity.QrCodeUtils;

import java.util.List;

import me.tangke.slidemenu.SlideMenu;

@SuppressLint("NewApi")
public class MainActivity extends BaseActivity {

	protected static final String TAG = "MainActivity";
    private static final int REQUEST_SCAN = 123;
    // textview for unread message count
	private TextView unreadLabel;
	// textview for unread event message
	private TextView unreadAddressLable;

	private View[] mTabs;
	private ContactListFragment contactListFragment;
	private Fragment[] fragments;
	private int index;
	private int currentTabIndex;
	// user logged into another device
	public boolean isConflict = false;
	// user account was removed
	private boolean isCurrentAccountRemoved = false;

	private ImageView mMenuHead;
	private SlideMenu mSlideMenu;
	private TextView menunickName;
	private ImageView menusexImage;
	private TextView menuuserName;
	private MyContactListener mMyContactListener;


	/**
	 * check if current user account was remove
	 */
	public boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		    String packageName = getPackageName();
		    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		    if (!pm.isIgnoringBatteryOptimizations(packageName)) {
				try {
					Intent intent = new Intent();
					intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
					intent.setData(Uri.parse("package:" + packageName));
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		//make sure activity will not in background if user is logged into another device or removed
		if (savedInstanceState != null && savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED, false)) {
		    DemoHelper.getInstance().logout(false,null);
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		} else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		}
		setContentView(R.layout.em_activity_main);
		// runtime permission for android 6.0, just require all permissions here for simple
		requestPermissions();

		initView();

		//umeng api
//		MobclickAgent.updateOnlineConfig(this);
//		UmengUpdateAgent.setUpdateOnlyWifi(false);
//		UmengUpdateAgent.update(this);

		showExceptionDialogFromIntent(getIntent());

		inviteMessgeDao = new InviteMessgeDao(this);
		UserDao userDao = new UserDao(this);
		conversationListFragment = new ConversationListFragment();
		contactListFragment = new ContactListFragment();
//		SettingsFragment settingFragment = new SettingsFragment();
		SettingFragment settingFragment = new SettingFragment();
		fragments = new Fragment[] { conversationListFragment, contactListFragment, settingFragment};

		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, conversationListFragment)
				.add(R.id.fragment_container, contactListFragment).hide(contactListFragment).show(conversationListFragment)
				.commit();

		//register broadcast receiver to receive the change of group from DemoHelper
		registerBroadcastReceiver();


		mMyContactListener = new MyContactListener();
		EMClient.getInstance().contactManager().setContactListener(mMyContactListener);
		//debug purpose only
        registerInternalDebugReceiver();

//		VerUtil.checkVersion(this);
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        L.e("DEN", "Density is "+displayMetrics.density+" densityDpi is "+displayMetrics.densityDpi+" height: "+displayMetrics.heightPixels+
                " width: "+displayMetrics.widthPixels+" DP is:"+convertPixelToDp(displayMetrics.widthPixels));
        //jumpStartInterface();

	}
	/**
	 * Jump Start Interface
	 * 提示是否跳转设置自启动界面
	 */
	private void jumpStartInterface() {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("由于Android系统限制，请您手动开启本软件自启动权限，并将此软件设置为受保护应用以及允许后台运行，从而保障软件运行的最佳环境");
			builder.setPositiveButton("立即设置",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							MobileInfoUtils.jumpStartInterface(MainActivity.this);
						}
					});
			builder.setNegativeButton("暂时不设置",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.setCancelable(false);
			builder.create().show();
		} catch (Exception e) {
		}
	}
	private int convertPixelToDp(int pixel) {
		DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
		return (int)(pixel/displayMetrics.density);
	}
	@Override
	protected void onStart() {
		super.onStart();
//		CodeUtils.clearCache();
	}

	@TargetApi(23)
	private void requestPermissions() {
		PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
			@Override
			public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onDenied(String permission) {
				//Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * init views
	 */
	private void initView() {
		//SharedPreStorageMgr.getIntance().getStringValue(this, Constant.MYUID);
		unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
		unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);

		//Menu
		mSlideMenu = (SlideMenu) findViewById(R.id.slideMenu);
		mMenuHead = (ImageView) findViewById(R.id.menu_head);
		menuuserName = (TextView) findViewById(R.id.menu_userName);
		menusexImage = (ImageView) findViewById(R.id.menu_sexImage);
		menunickName = (TextView) findViewById(R.id.menu_nickName);


		mTabs = new View[3];
		mTabs[0] = findViewById(R.id.btn_container_conversation);
		mTabs[1] = findViewById(R.id.btn_container_address_list);
		mTabs[2] = findViewById(R.id.btn_container_setting);
		// select first tab
		mTabs[0].setSelected(true);
		mMenuHead.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, EditProfileAct.class));
			}
		});
	}

	/**
	 * on tab clicked
	 * 
	 * @param view
	 */
	public void onTabClicked(View view) {
		switch (view.getId()) {
		case R.id.btn_container_conversation:
			index = 0;
			break;
		case R.id.btn_container_address_list:
			index = 1;
			break;
		case R.id.btn_container_setting:
			index = 2;
			break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		// set current tab selected
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

	EMMessageListener messageListener = new EMMessageListener() {
		
		@Override
		public void onMessageReceived(List<EMMessage> messages) {
			// notify new message
		    for (EMMessage message : messages) {
		        DemoHelper.getInstance().getNotifier().onNewMsg(message);
		    }
			refreshUIWithMessage();
		}
		
		@Override
		public void onCmdMessageReceived(List<EMMessage> messages) {
			//red packet code : 处理红包回执透传消息
			for (EMMessage message : messages) {
				EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
				final String action = cmdMsgBody.action();//获取自定义action
				if (action.equals(RPConstant.REFRESH_GROUP_RED_PACKET_ACTION)) {
					RedPacketUtil.receiveRedPacketAckMessage(message);
				}
			}
			//end of red packet code
			refreshUIWithMessage();
		}
		
		@Override
		public void onMessageRead(List<EMMessage> messages) {
		}
		
		@Override
		public void onMessageDelivered(List<EMMessage> message) {
		}

		@Override
		public void onMessageRecalled(List<EMMessage> list) {

		}

		@Override
		public void onMessageChanged(EMMessage message, Object change) {}
	};

	private void refreshUIWithMessage() {
		runOnUiThread(new Runnable() {
			public void run() {
				// refresh unread count
				updateUnreadLabel();
				if (currentTabIndex == 0) {
					// refresh conversation list
					if (conversationListFragment != null) {
						conversationListFragment.refresh();
					}
				}
			}
		});
	}

	@Override
	public void back(View view) {
		super.back(view);
	}
	
	private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);
		intentFilter.addAction(RPConstant.REFRESH_GROUP_RED_PACKET_ACTION);
        broadcastReceiver = new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUnreadLabel();
                updateUnreadAddressLable();
                if (currentTabIndex == 0) {
                    // refresh conversation list
                    if (conversationListFragment != null) {
                        conversationListFragment.refresh();
                    }
                } else if (currentTabIndex == 1) {
                    if(contactListFragment != null) {
                        contactListFragment.refresh();
                    }
                }
                String action = intent.getAction();
                if(action.equals(Constant.ACTION_GROUP_CHANAGED)){
                    if (EaseCommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
                        GroupsActivity.instance.onResume();
                    }
                }
				//red packet code : 处理红包回执透传消息
				if (action.equals(RPConstant.REFRESH_GROUP_RED_PACKET_ACTION)){
					if (conversationListFragment != null){
						conversationListFragment.refresh();
					}
				}
				//end of red packet code
			}
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

	public void toggleMenu() {
		if(mSlideMenu.isOpen()){
			mSlideMenu.close(true);
		}else {
			mSlideMenu.open(false, true);
		}
	}


	public class MyContactListener implements EMContactListener {
        @Override
        public void onContactAdded(String username) {}
        @Override
        public void onContactDeleted(final String username) {
            runOnUiThread(new Runnable() {
                public void run() {
					if (ChatActivity.activityInstance != null && ChatActivity.activityInstance.toChatUsername != null &&
							username.equals(ChatActivity.activityInstance.toChatUsername)) {
						//TODO 暂时不需要提示
					    /*String st10 = getResources().getString(R.string.have_you_removed);
					    Toast.makeText(MainActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, Toast.LENGTH_LONG)
					    .show();*/
					    ChatActivity.activityInstance.finish();
					}
                }
            });
	        updateUnreadAddressLable();
        }
        @Override
        public void onContactInvited(String username, String reason) {}
        @Override
        public void onFriendRequestAccepted(String username) {}
        @Override
        public void onFriendRequestDeclined(String username) {}
	}
	
	private void unregisterBroadcastReceiver(){
	    broadcastManager.unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();		
		
		if (exceptionBuilder != null) {
		    exceptionBuilder.create().dismiss();
		    exceptionBuilder = null;
		    isExceptionDialogShow = false;
		}
		unregisterBroadcastReceiver();

		try {
            unregisterReceiver(internalDebugReceiver);
        } catch (Exception e) {
        }

        //如有错误删除掉，环信本身没有添加，自己添加以免内存泄漏的
        if(mMyContactListener != null)
			EMClient.getInstance().contactManager().removeContactListener(mMyContactListener);
	}

	/**
	 * update unread message count
	 */
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			unreadLabel.setText(CodeUtils.getUnreadCount(count));
			unreadLabel.setVisibility(View.VISIBLE);
		} else {
			unreadLabel.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * update the total unread count 
	 */
	public void updateUnreadAddressLable() {
		runOnUiThread(new Runnable() {
			public void run() {
				int count = getUnreadAddressCountTotal();
				if (count > 0) {
					unreadAddressLable.setVisibility(View.VISIBLE);
					unreadAddressLable.setText(CodeUtils.getUnreadCount(count));
				} else {
					unreadAddressLable.setVisibility(View.INVISIBLE);
				}
			}
		});

	}

	/**
	 * get unread event notification count, including application, accepted, etc
	 * 
	 * @return
	 */
	public int getUnreadAddressCountTotal() {
		int unreadAddressCountTotal = 0;
		unreadAddressCountTotal = inviteMessgeDao.getUnreadMessagesCount();
		return unreadAddressCountTotal;
	}

	/**
	 * get unread message count
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		int chatroomUnreadMsgCount = 0;
		unreadMsgCountTotal = EMClient.getInstance().chatManager().getUnreadMessageCount();
		for(EMConversation conversation:EMClient.getInstance().chatManager().getAllConversations().values()){
			if(conversation.getType() == EMConversationType.ChatRoom)
			chatroomUnreadMsgCount=chatroomUnreadMsgCount+conversation.getUnreadMsgCount();
		}
		return unreadMsgCountTotal-chatroomUnreadMsgCount;
	}

	private InviteMessgeDao inviteMessgeDao;

	@Override
	protected void onResume() {
		super.onResume();
		
		if (!isConflict && !isCurrentAccountRemoved) {
			updateUnreadLabel();
			updateUnreadAddressLable();
		}

		// unregister this event listener when this activity enters the
		// background
		DemoHelper sdkHelper = DemoHelper.getInstance();
		sdkHelper.pushActivity(this);

		EMClient.getInstance().chatManager().addMessageListener(messageListener);

		refreshUi();
	}

	private void refreshUi() {
		String userName = SpUtils.get(mContext, Constant.NOT_CLEAR_SP, Constant.USERNAME, "");
		String nickName = SharedPreStorageMgr.getIntance().getStringValue(this, Constant.NICKNAME);
		String headImage = SharedPreStorageMgr.getIntance().getStringValue(this, Constant.HEADIMAGE);
		String sex = SharedPreStorageMgr.getIntance().getStringValue(this, Constant.SEX);
		String myUid = SharedPreStorageMgr.getIntance().getStringValue(this, Constant.MYUID);
		String state = SharedPreStorageMgr.getIntance().getStringValue(this, Constant.STATE);
		String infocode = SharedPreStorageMgr.getIntance().getStringValue(this, Constant.INFOCODE);
		String last = SharedPreStorageMgr.getIntance().getStringValue(this, Constant.EMAIL_LAST);

		App.setMyUid(myUid);
		menunickName.setText(nickName);
		if(TextUtils.isEmpty(last)) {
			menuuserName.setText(userName + "@qeveworld.com");//TODO 有两种邮箱
		}else {
			menuuserName.setText(userName+last);
		}
		menuuserName.setText(App.xin);
		menuuserName.setText(this.getString(R.string.ti6)+SharedPreStorageMgr.getIntance().getStringValue(this, Constant.SN));
		/*if (sex.equals("女")) {
			menusexImage.setImageDrawable(getResources().getDrawable(R.drawable.woman));
		}else if(sex.equals("男")){
			menusexImage.setImageDrawable(getResources().getDrawable(R.drawable.man));
		}else if (sex.equals("保密")) {
			menusexImage.setVisibility(View.INVISIBLE);
		}*/
		int genderDrawableRes = UserHelper.getGenderDrawableRes(mContext);
		if(genderDrawableRes > 0){
			menusexImage.setVisibility(View.VISIBLE);
			menusexImage.setImageResource(genderDrawableRes);
		}else {
			menusexImage.setVisibility(View.INVISIBLE);
		}

//		Glide.with(this).load(headImage).into(mMenuHead);
		ImageUtils.show(this, headImage, R.drawable.default_avatar, mMenuHead);

		Logger.d("userName:"+userName+"\nnickName:"+nickName+"\nheadImage:"+headImage+"\nsex:"+sex
		+"\nmyUid："+myUid+"\nstate:"+state+"\ninfocode:"+infocode);
	}

	@Override
	protected void onStop() {
		EMClient.getInstance().chatManager().removeMessageListener(messageListener);
		DemoHelper sdkHelper = DemoHelper.getInstance();
		sdkHelper.popActivity(this);

		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("isConflict", isConflict);
		outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(mSlideMenu.isOpen()) {
				mSlideMenu.close(true);
				return true;
			}
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private android.app.AlertDialog.Builder exceptionBuilder;
	private boolean isExceptionDialogShow =  false;
    private BroadcastReceiver internalDebugReceiver;
    private ConversationListFragment conversationListFragment;
    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager broadcastManager;

    private int getExceptionMessageId(String exceptionType) {
         if(exceptionType.equals(Constant.ACCOUNT_CONFLICT)) {
             return R.string.connect_conflict;
         } else if (exceptionType.equals(Constant.ACCOUNT_REMOVED)) {
             return R.string.em_user_remove;
         } else if (exceptionType.equals(Constant.ACCOUNT_FORBIDDEN)) {
             return R.string.user_forbidden;
         }
         return R.string.Network_error;
    }
	/**
	 * show the dialog when user met some exception: such as login on another device, user removed or user forbidden
	 */
	private void showExceptionDialog(String exceptionType) {
	    isExceptionDialogShow = true;
		DemoHelper.getInstance().logout(false,null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (exceptionBuilder == null)
				    exceptionBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
				    exceptionBuilder.setTitle(st);
				    exceptionBuilder.setMessage(getExceptionMessageId(exceptionType));	
				    exceptionBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						exceptionBuilder = null;
						isExceptionDialogShow = false;
						finish();
						Intent intent = new Intent(MainActivity.this, LoginActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
				});
				exceptionBuilder.setCancelable(false);
				exceptionBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
			}
		}
	}

	private void showExceptionDialogFromIntent(Intent intent) {
	    EMLog.e(TAG, "showExceptionDialogFromIntent");
	    if (!isExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false)) {
            showExceptionDialog(Constant.ACCOUNT_CONFLICT);
        } else if (!isExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_REMOVED, false)) {
            showExceptionDialog(Constant.ACCOUNT_REMOVED);
        } else if (!isExceptionDialogShow && intent.getBooleanExtra(Constant.ACCOUNT_FORBIDDEN, false)) {
            showExceptionDialog(Constant.ACCOUNT_FORBIDDEN);
        }   
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		showExceptionDialogFromIntent(intent);
	}
	
	/**
	 * debug purpose only, you can ignore this
	 */
	private void registerInternalDebugReceiver() {
	    internalDebugReceiver = new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context context, Intent intent) {
                DemoHelper.getInstance().logout(false,new EMCallBack() {
                    
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                finish();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            }
                        });
                    }
                    
                    @Override
                    public void onProgress(int progress, String status) {}
                    
                    @Override
                    public void onError(int code, String message) {}
                });
            }
        };
        IntentFilter filter = new IntentFilter(getPackageName() + ".em_internal_debug");
        registerReceiver(internalDebugReceiver, filter);
    }

	@Override 
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
			@NonNull int[] grantResults) {
		PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
	}

	/**
	 * 退出登录
	 */
	public void logout(View view) {
		showProgress(getString(R.string.Are_logged_out));
		DemoHelper.getInstance().logout(false,new EMCallBack() {

			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					public void run() {
						dismissProgress();
						// show login screen
						finish();
						startActivity(new Intent(MainActivity.this, LoginActivity.class));

					}
				});
			}

			@Override
			public void onProgress(int progress, String status) {

			}

			@Override
			public void onError(int code, String message) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						dismissProgress();
						Toast.makeText(App.applicationContext, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	public void goSet(View view) {
		startActivity(new Intent(MainActivity.this, SetUpAct.class));
	}
	public void goEditProfile(View view) {
		startActivity(new Intent(MainActivity.this, EditProfileAct.class));
	}


	public void goScan(View view) {
		quan();
	}

	public void goMoment(View view) {
		DBManager.getInstance().saveUnreadMotionActionCount(0);
		startActivity(new Intent(mActivity, MomentAct.class));
	}

	private void quan(){
		if(NotificationsUtils.cameraIsCanUse()==true){
			Log.i("dcz2","有权限");
			startActivityForResult(new Intent(mActivity, ScanAct.class), REQUEST_SCAN);
		}else {
			Log.i("dcz2","没有权限");
			ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, 1);
		}
	}


	/**点击工具栏的查找按钮*/
	public void search() {
		startActivity(new Intent(MainActivity.this, AddContactActivity.class));
	}

	private MenuPopup popup;

	/**显示下拉菜单*/
	public void showMenu(View view) {
		if(popup == null) {
			popup = new MenuPopup(this, MenuPopup.MenuType.BLACK, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
					.LayoutParams.WRAP_CONTENT);
			popup.addAction(new ActionItem(getResources().getDrawable(R.drawable.menu_group), getString(R.string.add_group_chat)));
			popup.addAction(new ActionItem(getResources().getDrawable(R.drawable.menu_friend), getString(R.string.main_addFriend)));
			popup.addAction(new ActionItem(getResources().getDrawable(R.drawable.menu_scan), getString(R.string.menu_scan)));
//			popup.addAction(new ActionItem(getResources().getDrawable(R.drawable.setting_bared), getString(R.string.main_set)));

//			popup.addAction(new ActionItem(getResources().getDrawable(R.drawable.scan), getString(R.string.main_scan)));
//			popup.addAction(new ActionItem(getResources().getDrawable(R.drawable.help), getString(R.string.main_help)));
			popup.setItemOnClickListener(new MenuPopup.OnItemOnClickListener() {
				@Override
				public void onItemClick(ActionItem item, int position) {
					switch (position) {
						case 0:
//							ToastUtils.showToast(getApplicationContext(), "添加群聊");
							startActivity(new Intent(MainActivity.this, NewGroupActivity.class));
//						NetUtils.hasDataConnection(MainActivity.this);
							break;
						case 1:
							startActivity(new Intent(MainActivity.this, AddContactActivity.class));

//							VerUtil.checkVersion(MainActivity.this);
							break;
						case 2:
							quan();
							break;
						case 3:
							goSet(null);
							break;

						default:
							break;
					}
				}
			});
		}
		popup.show(view);
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCAN && resultCode == Activity.RESULT_OK) {
            String chatId = data.getStringExtra(QrCodeUtils.RESULT_STRING);
            startActivity(new Intent(mContext, AddContactByQrCodeActivity.class).putExtra(Constant.USERNAME, chatId));
        }

    }

	@Override
	protected boolean isTran() {
		return true;
	}

	@Override
	protected void initBar(boolean enableKeyBoard) {
			mImmersionBar = ImmersionBar.with(this);
			mImmersionBar
					.statusBarDarkFont(true, 0.5f)
					.init();
	}
}
