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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.classic.common.MultipleStatusView;
import com.easemob.redpacketsdk.constant.RPConstant;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.adapter.NewFriendsMsgAdapter;
import com.tg.tgt.db.InviteMessgeDao;
import com.tg.tgt.domain.InviteMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Application and notification
 *
 */
public class NewFriendsMsgActivity extends BaseActivity {
	private MultipleStatusView mMultipleStatusView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.tg.tgt.R.layout.em_activity_new_friends_msg);
		setTitleBarLeftBack();

		mMultipleStatusView = (MultipleStatusView) findViewById(R.id.multiple_status_layout);
		ListView listView = (ListView) findViewById(com.tg.tgt.R.id.list);
		InviteMessgeDao dao = new InviteMessgeDao(this);
		List<InviteMessage> msgs = dao.getMessagesList();
		List<InviteMessage> ce = new ArrayList<>();
		List<InviteMessage> shi = new ArrayList<>();
		if(msgs==null || msgs.size()==0){
			mMultipleStatusView.showEmpty();
		}
		for(int i=0;i<msgs.size();i++){
			if(msgs.get(i).getStatus() == InviteMessage.InviteMesageStatus.BEINVITEED ||msgs.get(i).getStatus() == InviteMessage.InviteMesageStatus.BEAPPLYED ||
					msgs.get(i).getStatus() == InviteMessage.InviteMesageStatus.GROUPINVITATION){
				ce.add(msgs.get(i));
			}else {
				shi.add(msgs.get(i));
			}
		}
		Collections.reverse(ce);
		Collections.reverse(shi);
		for(InviteMessage a:shi){
			ce.add(a);
		}
		NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1,ce);
		listView.setAdapter(adapter);
		dao.saveUnreadMessageCount(0);
		registerBroadcastReceiver();
	}
	private BroadcastReceiver broadcastReceiver;
	private LocalBroadcastManager broadcastManager;
	private void registerBroadcastReceiver() {
		broadcastManager = LocalBroadcastManager.getInstance(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED2);
		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if(action.equals(Constant.ACTION_CONTACT_CHANAGED2)){
					recreate();
				}
			}
		};
		broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
	}

	private void unregisterBroadcastReceiver(){
		broadcastManager.unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterBroadcastReceiver();
	}

	public void back(View view) {
		finish();
	}
}
