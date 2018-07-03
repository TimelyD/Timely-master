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
package com.hyphenate.easeui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easeui.R;


public class PopDiaLog extends Dialog {

	private String rightBtn;
	private String leftBtn;
	protected static RelativeLayout pup;        //弹框
	protected static LinearLayout pup2;         //弹框里面的内容

	public interface AlertDialogUser {
		void onResult(boolean confirmed, Bundle bundle);
	}

	private String title;
	private String msg;
	private AlertDialogUser user;
	private Bundle bundle;
	private boolean showCancel = false;

	public PopDiaLog(Context context, int msgId) {
		super(context);
		this.title = context.getResources().getString(R.string.prompt);
		this.msg = context.getResources().getString(msgId);
		this.setCanceledOnTouchOutside(true);
	}

	public PopDiaLog(Context context, String msg) {
		super(context);
		this.title = context.getResources().getString(R.string.prompt);
		this.msg = msg;
		this.setCanceledOnTouchOutside(true);
	}

	public PopDiaLog(Context context, int titleId, int msgId) {
		super(context);
		this.title = context.getResources().getString(titleId);
		this.msg = context.getResources().getString(msgId);
		this.setCanceledOnTouchOutside(true);
	}

	public PopDiaLog(Context context, String title, String msg) {
		super(context);
		this.title = title;
		this.msg = msg;
		this.setCanceledOnTouchOutside(true);
	}

	public PopDiaLog(Context context, String msg, String leftBtn, String rightBtn, AlertDialogUser user) {
		super(context);
		this.title = context.getResources().getString(R.string.prompt);
		this.msg = msg;
		this.leftBtn = leftBtn;
		this.rightBtn = rightBtn;
		this.user = user;
		this.setCanceledOnTouchOutside(true);
	}

	public PopDiaLog(Context context, int titleId, int msgId, Bundle bundle, AlertDialogUser user, boolean showCancel) {
		super(context);
		this.title = context.getResources().getString(titleId);
		this.msg = context.getResources().getString(msgId);
		this.user = user;
		this.bundle = bundle;
		this.showCancel = showCancel;
		this.setCanceledOnTouchOutside(true);
	}

	public PopDiaLog(Context context, String title, String msg, Bundle bundle, AlertDialogUser user, boolean showCancel) {
		super(context);
		this.title = title;
		this.msg = msg;
		this.user = user;
		this.bundle = bundle;
		this.showCancel = showCancel;
		this.setCanceledOnTouchOutside(true);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pop);

		/*Button cancel = (Button)findViewById(R.id.btn_cancel);
		Button ok = (Button)findViewById(R.id.btn_ok);
		TextView titleView = (TextView) findViewById(R.id.title);
		setTitle(title);
		
		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (view.getId() == R.id.btn_ok) {
					onOk(view);
				} else if (view.getId() == R.id.btn_cancel) {
					onCancel(view);
				}
			}
		};
		cancel.setOnClickListener(listener);
		ok.setOnClickListener(listener);

		if (title != null)
		    titleView.setText(title);
		
		if (showCancel || !TextUtils.isEmpty(rightBtn)) {
			cancel.setVisibility(View.VISIBLE);
		}

		if(!TextUtils.isEmpty(leftBtn))
			cancel.setText(leftBtn);

		if(!TextUtils.isEmpty(rightBtn))
			ok.setText(rightBtn);

		if (msg != null)
		    ((TextView)findViewById(R.id.alert_message)).setText(msg);*/
	}
	
	public void onOk(View view){
		this.dismiss();
		if (this.user != null) {
			this.user.onResult(true, this.bundle);
		}
	}
	
	public void onCancel(View view) {
		this.dismiss();
		if (this.user != null) {
			this.user.onResult(false, this.bundle);
		}
	}
}
