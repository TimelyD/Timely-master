package com.hyphenate.easeui.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * 获取手机相关
 * 
 * @author liujian
 * 
 */
public class PhoneUtil {
	/**
	 * dp值转化成px值
	 * 
	 * @param ctx
	 * @param dpValue
	 * @return
	 */
	public static int dp2px(Context ctx, float dpValue) {
		final float scale = ctx.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dp(Context ctx, float pxValue) {
		final float scale = ctx.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	// 将sp值转换为px值
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 
	 * 功能：获取屏幕宽度
	 * 
	 * @param ctx
	 * @return
	 */
	public static int getScreenWidth(Context ctx) {
		return ((Activity) ctx).getWindowManager().getDefaultDisplay()
				.getWidth();
	}

	/**
	 * 
	 * 功能：获取屏幕高度
	 * 
	 * @param ctx
	 * @return
	 */
	public static int getScreenHeight(Context ctx) {
		return ((Activity) ctx).getWindowManager().getDefaultDisplay()
				.getHeight();
	}

	/**
	 * 获取当前屏幕分辨率 px 高x宽
	 */
	public static String getScreenPx(Context ctx) {
		// double STANDARD_DENSITY = 160;//标准的密度
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) ctx).getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		double CURRENT_SCREEN_WIDTH_PX = displayMetrics.widthPixels;// px
		double CURRENT_SCREEN_HEIGHT_PX = displayMetrics.heightPixels;// px
		double CURRENT_DENSITY_RATIO = displayMetrics.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
		// double CURRENT_DENSITY = displayMetrics.densityDpi;//
		// 屏幕密度（每寸像素：120/160/240/320）
		int CURRENT_SCREEN_WIDTH_DP = (int) (CURRENT_SCREEN_WIDTH_PX
				/ CURRENT_DENSITY_RATIO + 0.5f);
		int CURRENT_SCREEN_HEIGHT_DP = (int) (CURRENT_SCREEN_HEIGHT_PX
				/ CURRENT_DENSITY_RATIO + 0.5f);
		return CURRENT_SCREEN_HEIGHT_PX + "x" + CURRENT_SCREEN_WIDTH_PX;
	}

	/**
	 * 
	 * 功能：获取程序版本名
	 * 
	 * @param ctx
	 * @return verName 版本名称
	 */
	public static String getVerName(Context ctx) {
		return String.valueOf(getPi(ctx).versionName);
	}

	/**
	 * 
	 * 功能：获取程序版本码
	 * 
	 * @param ctx
	 * @return verCode 版本码
	 */
	public static String getVerCode(Context ctx) {
		return String.valueOf(getPi(ctx).versionCode);
	}

	public static PackageInfo getPi(Context ctx) {
		PackageManager pm = ctx.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(ctx.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}// getPackageName表示获取当前ctx所在的包的名称，0表示获取版本信息
		return pi;
	}

	/**
	 * 去掉标题栏，并全屏显示
	 * 
	 * @param activity
	 */
	public static void setNoTitleFullScreen(Activity activity) {
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		activity.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/**
	 * 获取sdk版本号 2.1: 7; 2.2: 8; 2.3.1: 9; 2.3.4: 10
	 * 
	 * @return
	 */
	static public int getSDK() {
		return Integer.valueOf(android.os.Build.VERSION.SDK_INT);
	}

	/**
	 * 获取手机型号 如: Nexus One,I9100
	 * 
	 * @return
	 */
	static public String getModel() {
		return android.os.Build.MODEL;
	}

	/**
	 * 获取发布版本号 2.1;2.2;2.3.4...
	 * 
	 * @return
	 */
	static public String getRelease() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * 获取CPU处理器型号，如：armeabi-v7a
	 */
	static public String getCpu() {
		return android.os.Build.CPU_ABI;
	}

	/**
	 * 获取手机制造商，如：samsung
	 */
	static public String getManuFacturer() {
		return android.os.Build.MANUFACTURER;
	}

	/**
	 * 获取商标，如：samsung
	 */
	static public String getBrand() {
		return android.os.Build.BRAND;
	}

	/**
	 * 获取主板,如：GT-I9100
	 */
	static public String getBoard() {
		return android.os.Build.BOARD;
	}

	/**
	 * 获取用户,如：root
	 */
	static public String getUser() {
		return android.os.Build.USER;
	}

	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
	public static int getNavigationBarHeight(Activity context) {
		int navigationBarHeight = 0;
		Resources rs = context.getResources();
		int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
		if (id > 0 && checkDeviceHasNavigationBar(context)) {
			navigationBarHeight = rs.getDimensionPixelSize(id);
		}
		return navigationBarHeight;
	}
	private static boolean checkDeviceHasNavigationBar(Activity context) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			Display display = context.getWindowManager().getDefaultDisplay();
			Point size = new Point();
			Point realSize = new Point();
			display.getSize(size);
			display.getRealSize(realSize);
			return realSize.y!=size.y;
		}else {
			boolean menu = ViewConfiguration.get(context).hasPermanentMenuKey();
			boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
			if(menu || back) {
				return false;
			}else {
				return true;
			}
		}

	}

	public static void hideInput (Activity context) {
		InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(inputMethodManager.isActive()){
			try {
				inputMethodManager.hideSoftInputFromWindow( context.getCurrentFocus().getWindowToken(), 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
