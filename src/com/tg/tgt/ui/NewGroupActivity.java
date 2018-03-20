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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.tg.tgt.R;
import com.tg.tgt.helper.GroupManger;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.model2.GroupModel;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class NewGroupActivity extends BaseActivity {
	private EditText groupNameEditText;
	private ProgressDialog progressDialog;
	private EditText introductionEditText;
	private CheckBox publibCheckBox;
	private CheckBox memberCheckbox;
	private TextView secondTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_activity_new_group);
		groupNameEditText = (EditText) findViewById(R.id.edit_group_name);
		introductionEditText = (EditText) findViewById(R.id.edit_group_introduction);
		publibCheckBox = (CheckBox) findViewById(R.id.cb_public);
		memberCheckbox = (CheckBox) findViewById(R.id.cb_member_inviter);
		secondTextView = (TextView) findViewById(R.id.second_desc);
		
		publibCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

		    @Override
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if(isChecked){
		            secondTextView.setText(R.string.join_need_owner_approval);
		        }else{
                    secondTextView.setText(R.string.Open_group_members_invited);
		        }
		    }
		});
		setTitleBarLeftBack();
	}

	/**
	 * @param v
	 */
	public void save(View v) {
		String name = groupNameEditText.getText().toString();
		if (TextUtils.isEmpty(name)) {
		    new EaseAlertDialog(this, R.string.Group_name_cannot_be_empty).show();
		} else {
			// select from contact list
			startActivityForResult(new Intent(this, GroupPickContactsActivity.class).putExtra("groupName", name), 0);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);


		String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
		final String st2 = getResources().getString(R.string.Failed_to_create_groups);
		if (resultCode == RESULT_OK) {


			/*final String groupName = groupNameEditText.getText().toString().trim();
			String desc = "";
//			String desc = introductionEditText.getText().toString();
			final String[] members = data.getStringArrayExtra("newmembers");
			String s = new Gson().toJson(members);
			ApiManger.getApiService().createGroup(groupName,desc,0,200,1, SharedPreStorageMgr.getIntance().getStringValue(this, Constant.USERNAME), s)
					.compose(RxUtils.<BaseHttpResult>applySchedulers())
					.subscribe(new BaseObserver<BaseHttpResult>(this) {
						@Override
						protected void onSuccess(BaseHttpResult baseHttpResult) {
							setResult(RESULT_OK);
							finish();
							sendInviteMsg(members);
						}

						@Override
						public void onFaild(int code, String message) {
							super.onFaild(code, message);
							sendInviteMsg(members);
						}

						@Override
						public void onFaild(int code, @StringRes int message) {
							super.onFaild(code, message);
							sendInviteMsg(members);
						}
					});*/

			//new group
			/*progressDialog = new ProgressDialog(this);
			progressDialog.setMessage(st1);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();

			new Thread(new Runnable() {
				@Override
				public void run() {
					final String groupName = groupNameEditText.getText().toString().trim();
					String desc = introductionEditText.getText().toString();
					final String[] members = data.getStringArrayExtra("newmembers");
					try {
						EMGroupOptions option = new EMGroupOptions();
					    option.maxUsers = 200;
						option.inviteNeedConfirm = false;

					    String reason = NewGroupActivity.this.getString(R.string.invite_join_group);
					    reason  = EMClient.getInstance().getCurrentUser() + reason + groupName;

						if(publibCheckBox.isChecked()){
						    option.style = memberCheckbox.isChecked() ? EMGroupStyle.EMGroupStylePublicJoinNeedApproval : EMGroupStyle.EMGroupStylePublicOpenJoin;
						}else{
						    option.style = memberCheckbox.isChecked()?EMGroupStyle.EMGroupStylePrivateMemberCanInvite:EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
						}
						final EMGroup group = EMClient.getInstance().groupManager().createGroup(groupName, desc, members,
								reason, option);
						runOnUiThread(new Runnable() {
							public void run() {
								progressDialog.dismiss();
								setResult(RESULT_OK);
								finish();
								Intent intent = new Intent(NewGroupActivity.this, ChatActivity.class);
								// it is group chat
								intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
								intent.putExtra("userId", group.getGroupId());
								startActivity(intent);
//								saveCreateGroupHint(group.getGroupId(), members);
								GroupManger.sendInviteMsg(group, members);
							}
						});
					} catch (final HyphenateException e) {
						runOnUiThread(new Runnable() {
							public void run() {
								progressDialog.dismiss();
								Toast.makeText(NewGroupActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
							}
						});
					}

				}
			}).start();*/

            final String groupName = groupNameEditText.getText().toString().trim();
            String desc = introductionEditText.getText().toString();
            final String[] members = data.getStringArrayExtra("newmembers");
			ApiManger2.getApiService()
					.createGroup(groupName, desc, 500, memberCheckbox.isChecked(), false, getMembers(members))
					.compose(this.<HttpResult<GroupModel>>bindToLifeCyclerAndApplySchedulers(false))
					.subscribe(new BaseObserver2<GroupModel>() {
						@Override
						protected void onSuccess(GroupModel groupModel) {


							GroupManger.saveGroup(groupModel);

							GroupManger.toChat(mActivity, groupModel.getGroupSn(), new Consumer<Boolean>() {
								@Override
								public void accept(@NonNull Boolean aBoolean) throws Exception {
									if(aBoolean) {
										setResult(RESULT_OK);
										finish();
									}
								}
							});
							/*Intent intent = new Intent(NewGroupActivity.this, ChatActivity.class);
							// it is group chat
							intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
							intent.putExtra("userId", groupModel.getGroupSn());
							startActivity(intent);*/
						}
					});
            /*ApiManger2.getApiService()
                    .createGroup(groupName, "12")
                    .compose(this.<HttpResult<GroupEntity>>bindToLifeCyclerAndApplySchedulers(false))
                    .subscribe(new BaseObserver2<GroupEntity>() {
                        @Override
                        protected void onSuccess(GroupEntity emptyData) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    });*/
		}
	}


	public String getMembers(String[] members){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < members.length; i++) {
			sb.append(members[i]);
			if(i!=members.length-1)
				sb.append(",");
		}
		return sb.toString();
	}

	public void back(View view) {
		finish();
	}
}
