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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.easeui.EaseApp;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.adapter.GroupAdapter;
import com.tg.tgt.helper.GroupManger;
import com.tg.tgt.helper.SecurityDialog;
import com.tg.tgt.http.model.IsCodeResult;
import com.tg.tgt.http.model2.GroupModel;
import com.tg.tgt.utils.CodeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GroupsActivity extends BaseActivity {
	public static final String TAG = "GroupsActivity";
	private ListView groupListView;
	protected List<GroupModel> grouplist;
	private GroupAdapter groupAdapter;
	private InputMethodManager inputMethodManager;
	public static GroupsActivity instance;
	private View progressBar;
	private SwipeRefreshLayout swipeRefreshLayout;
	private EaseTitleBar bar;
	private String forward_msg_id="0";
	
	Handler handler = new Handler(){
	    public void handleMessage(android.os.Message msg) {
	        swipeRefreshLayout.setRefreshing(false);
	        switch (msg.what) {
            case 0:
                refresh();
                break;
            case 1:
                Toast.makeText(GroupsActivity.this, R.string.Failed_to_get_group_chat_information, Toast.LENGTH_LONG).show();
                break;

            default:
                break;
            }
	    }
	};

		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_fragment_groups);
		setTitleBarLeftBack();

		instance = this;
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		grouplist = EMClient.getInstance().groupManager().getAllGroups();
		grouplist = GroupManger.getGroupList();
		forward_msg_id = getIntent().getStringExtra("forward_msg_id");
		if(forward_msg_id==null){
			forward_msg_id="0";
		}
		sortGroup();
		groupListView = (ListView) findViewById(R.id.list);
		bar= (EaseTitleBar) findViewById(R.id.title_bar);
		//show group list
        groupAdapter = new GroupAdapter(this, 1, grouplist);
        groupListView.setAdapter(groupAdapter);
		
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
		swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
		//pull down to refresh
		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new Thread(){
					@Override
					public void run(){
						try {
//							EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
							GroupManger.fetchAllGroup();
							handler.sendEmptyMessage(0);
						} catch (Exception e) {
							e.printStackTrace();
							handler.sendEmptyMessage(1);
						}
					}
				}.start();
			}
		});
		
		groupListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				/*if(groupAdapter.getItemViewType(position) == GroupAdapter.TYPE_ADD){
					startActivityForResult(new Intent(GroupsActivity.this, NewGroupActivity.class), 0);
				}else {
					GroupManger.toChat(mActivity, groupAdapter.getItem(position - GroupAdapter.headCount).getGroupSn());
				}*/
				if(forward_msg_id.equals("0")){
					GroupManger.toChat(mActivity, groupAdapter.getItem(position - GroupAdapter.headCount).getGroupSn());
				}else {
					toChat(groupAdapter.getItem(position - GroupAdapter.headCount).getGroupName(),groupAdapter.getItem(position - GroupAdapter.headCount).getGroupSn());
				}
			}

		});
		groupListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
					if (getCurrentFocus() != null)
						inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}
		});
		bar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(GroupsActivity.this, NewGroupActivity.class), 0);
			}
		});
		
	}

	//排序，当前用户创建的排在前
	private void sortGroup() {
		grouplist = new ArrayList<>(grouplist);
		Collections.sort(grouplist, new Comparator<GroupModel>() {
			@Override
			public int compare(GroupModel lhs, GroupModel rhs) {
				/*boolean lhsOwner = App.getMyUid().equals(String.valueOf(lhs.getUserId()));
				boolean rhsOwner = App.getMyUid().equals(String.valueOf(rhs.getUserId()));*/
				boolean lhsOwner = lhs.getGroupOwner();
				boolean rhsOwner = rhs.getGroupOwner();
				/*if(lhsOwner && rhsOwner){
					return 0;
				}else */if(!lhsOwner & rhsOwner){
					return 1;
				}else if(lhsOwner & !rhsOwner){
					return -1;
				}else {
					return 0;
				}
//				return EMClient.getInstance().getCurrentUser().equals(rhs.getOwner()) ? 1 :0;
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onResume() {
		new Thread(){
			@Override
			public void run(){
				try {
					GroupManger.fetchAllGroup();
					handler.sendEmptyMessage(0);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(1);
				}
			}
		}.start();
        //refresh();
		super.onResume();
	}
	
	private void refresh(){
	    grouplist = GroupManger.getGroupList();
		sortGroup();
        groupAdapter = new GroupAdapter(this, 1, grouplist);
        groupListView.setAdapter(groupAdapter);
        groupAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
	}
	private String uname;
	private void toChat(String name,String username) {
		uname=username;
		new EaseAlertDialog(this, null, getString(R.string.confirm_forward_to,name), null, new EaseAlertDialog.AlertDialogUser() {
			@Override
			public void onResult(boolean confirmed, Bundle bundle) {
				if (confirmed) {
					Intent intent = new Intent(GroupsActivity.this, ChatActivity.class);
					// it is single chat
					intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP);
					intent.putExtra("userId",uname);
					intent.putExtra("forward_msg_id", forward_msg_id);
					startActivity(intent);forward_msg_id="0";
					finish();
				}
			}
		}, true).show();
		/*Intent intent = new Intent(GroupsActivity.this, ChatActivity.class);
		// it is single chat
		intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP);
		intent.putExtra("userId",username);
		intent.putExtra("forward_msg_id", forward_msg_id);
		startActivity(intent);
		finish();*/
        /*mOnPhotoMenuListener.onPhotoSend(mAdapter.getSelectlist().toArray(new MediaBean[mAdapter
                .getSelectlist().size()]));*/
	}
	public void back(View view) {
		finish();
	}
}
