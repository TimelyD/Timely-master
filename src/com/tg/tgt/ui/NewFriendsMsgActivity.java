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

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.classic.common.MultipleStatusView;
import com.tg.tgt.R;
import com.tg.tgt.adapter.NewFriendsMsgAdapter;
import com.tg.tgt.db.InviteMessgeDao;
import com.tg.tgt.domain.InviteMessage;

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

		if(msgs==null || msgs.size()==0){
			mMultipleStatusView.showEmpty();
		}

		NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs);
		listView.setAdapter(adapter);
		dao.saveUnreadMessageCount(0);
		
	}

	public void back(View view) {
		finish();
	}
}
