package com.tg.tgt;

import android.app.Activity;
import android.content.Context;

import com.tg.tgt.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity管理
 * 
 * @author liujian
 * 
 */
public class ActMgrs {

	private static List<Activity> activityStack;
	private static ActMgrs instance;

	private ActMgrs() {
	}

	public static ActMgrs getActManager() {
		if (instance == null) {
			instance = new ActMgrs();
		}
		return instance;
	}

	/**
	 * 移除最上层Activity
	 */
	public void popActivity() {
		Activity activity = null;
		try {
			activity = activityStack.get(activityStack.size()-1);
		} catch (Exception e) {
		}
		if (activity != null) {
			activity.finish();
			activityStack.remove(activity);
			activity = null;// 消除引用关系，便于系统GC
		}
	}
	/**
	 * 移除最上层Activity
	 * @param activity
	 */
	public void popActivityWithoutFinish(BaseActivity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity = null;// 消除引用关系，便于系统GC
		}
	}

	/**
	 * 移除指定的Activity
	 */
	public void popActivity(Activity activity) {
		if (activity != null) {
			activity.finish();
			activityStack.remove(activity);
			activity = null;// 消除引用关系，便于系统GC
		}
	}

	/**
	 * 得到当前栈中最上面的Activity
	 */
	public Activity currentActivity() {
		Activity activity = null;
		try {
			activity = activityStack.get(activityStack.size()-1);
		} catch (Exception e) {
		}

		return activity;
	}

	/**
	 * 判断要弹对话框的act是不是在栈的最上面,防止出现act已经被销毁后弹对话框出现的异常退出
	 * 
	 * @param cls
	 * @return
	 */
	public boolean isExistAct(Class<?> cls) {
		if (currentActivity().getClass().equals(cls)) {
			return true;
		}
		return false;
	}

	/**
	 * Activity进栈
	 */
	public void pushActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new ArrayList<>();
		}
		if (activityStack.contains(activity)) {
			return;
		}
		activityStack.add(activity);
	}

	/**
	 * 移除指定的Activity的上面Activity 回到指定的Activity
	 */
	public void popAllActivityExceptOne(Class<?> cls) {
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			if (activity.getClass().equals(cls)) {
				break;
			}
			popActivity(activity);
		}
	}

	/**
	 * 清除所有activity
	 */
	public void popAllActivity() {
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				activityStack = null;
				break;
			}
			popActivity(activity);
		}

	}

	/**
	 * 退出应用程序
	 */
	public void AppExit(Context context) {
		try {
			popAllActivity();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

}
