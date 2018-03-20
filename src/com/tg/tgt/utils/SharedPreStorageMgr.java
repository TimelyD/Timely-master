package com.tg.tgt.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Map;

/**
 * @function 封装存数基本类型数据的SharedPreference
 */
public class SharedPreStorageMgr {
	private static final SharedPreStorageMgr instance = new SharedPreStorageMgr();
	public static final String PREFSNAMES = "timelylock";

	public static SharedPreStorageMgr getIntance() {
		return instance;
	}

	/**
	 * 
	 * 保存字符串
	 * 
	 * @param context
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean saveStringValue(String prefs_names, Context context,
			String key, String value) {
		SharedPreferences sp = context.getSharedPreferences(prefs_names,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(key, value);
		boolean rs = editor.commit();
		return rs;
	}
	public boolean saveStringValue(Context context,
			String key, String value) {
		return saveStringValue(PREFSNAMES, context, key, value);
	}

	/**
	 * 保存多个字符串
     * @return
     */
	public boolean saveStringValueMap(String prefs_names, Context context,
									  Map<String, String> map) {
		SharedPreferences sp = context.getSharedPreferences(prefs_names,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		for (String key : map.keySet()) {
			editor.putString(key, map.get(key));
		}
		return editor.commit();
	}
	public boolean saveStringValueMap(Context context,
									  Map<String, String> map) {
		return saveStringValueMap(PREFSNAMES, context, map);
	}

	/**
	 * 
	 * 获取字符串
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public String getStringValue(String prefs_names, Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(prefs_names,
				Context.MODE_PRIVATE);
		String value = sp.getString(key, "");

		return value;
	}
	public String getStringValue(Context context, String key) {
		return getStringValue(PREFSNAMES, context, key);
	}

	/**
	 * ֵ 保存布尔值ֵ
	 * 
	 * @param context
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean saveBooleanValue(String prefs_names, Context context,
			String key, boolean value) {
		SharedPreferences sp = context.getSharedPreferences(prefs_names,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		boolean rs = editor.commit();

		return rs;
	}

	/**
	 * ֵ 获取布尔值
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public boolean getBooleanValue(String prefs_names, Context context,
			String key) {
		SharedPreferences sp = context.getSharedPreferences(prefs_names,
				Context.MODE_PRIVATE);
		boolean value = sp.getBoolean(key, false);

		return value;
	}

	/**
	 * ֵ 获取布尔值 登录处默认勾选
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public boolean getBooleanLoginValue(String prefs_names, Context context,
			String key) {
		SharedPreferences sp = context.getSharedPreferences(prefs_names,
				Context.MODE_PRIVATE);
		boolean value = sp.getBoolean(key, true);

		return value;
	}

	/**
	 * 
	 * 保存整形值
	 * 
	 * @param context
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean saveIntegerValue(String prefs_names, Context context,
			String key, int value) {
		SharedPreferences sp = context.getSharedPreferences(prefs_names,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(key, value);
		boolean rs = editor.commit();

		return rs;
	}

	/**
	 * 
	 * 获取整形值
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public int getIntegerValue(String prefs_names, Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(prefs_names,
				Context.MODE_PRIVATE);
		int value = sp.getInt(key, -1);

		return value;
	}
	public int getIntegerValue(Context context, String key) {
		return getIntegerValue(PREFSNAMES, context, key);
	}

	public void clear(String prefs_names, Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(prefs_names,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}
	
	public void remove(String prefs_names, Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(prefs_names,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.remove(key);
		editor.commit();
	}
}
