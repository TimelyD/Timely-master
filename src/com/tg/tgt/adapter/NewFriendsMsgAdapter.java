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
package com.tg.tgt.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.ImageUtils;
import com.tg.tgt.DemoHelper;
import com.tg.tgt.R;
import com.tg.tgt.db.InviteMessgeDao;
import com.tg.tgt.domain.InviteMessage;
import com.tg.tgt.domain.InviteMessage.InviteMesageStatus;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.model2.UserFriendModel;
import com.tg.tgt.ui.BaseActivity;

import java.util.List;

import io.reactivex.Observable;

public class NewFriendsMsgAdapter extends ArrayAdapter<InviteMessage> {

	private Context context;
	private InviteMessgeDao messgeDao;
	private Gson mGson;

	public NewFriendsMsgAdapter(Context context, int textViewResourceId, List<InviteMessage> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		messgeDao =  new InviteMessgeDao(context);
		mGson = new Gson();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.em_row_invite_msg, null);
			holder.avator = (ImageView) convertView.findViewById(R.id.avatar);
			holder.reason = (TextView) convertView.findViewById(R.id.message);
			holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.agree = (Button) convertView.findViewById(R.id.agree);
			holder.status = (Button) convertView.findViewById(R.id.user_state);
			holder.groupContainer = (LinearLayout) convertView.findViewById(R.id.ll_group);
			holder.groupname = (TextView) convertView.findViewById(R.id.tv_groupName);
			holder.kong = convertView.findViewById(R.id.kong);
			// holder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String str1 = context.getResources().getString(R.string.Has_agreed_to_your_friend_request);
		String str2 = context.getResources().getString(R.string.agree);

		String str3 = context.getResources().getString(R.string.Request_to_add_you_as_a_friend);
		String str4 = context.getResources().getString(R.string.Apply_to_the_group_of);
		String str5 = context.getResources().getString(R.string.Has_agreed_to);
		String str6 = context.getResources().getString(R.string.Has_refused_to);

		String str7 = context.getResources().getString(R.string.refuse);
		String str8 = context.getResources().getString(R.string.invite_join_group);
        String str9 = context.getResources().getString(R.string.accept_join_group);
		String str10 = context.getResources().getString(R.string.refuse_join_group);
		final InviteMessage msg = getItem(position);

		if(position!=0){
			if(msg.getStatus() == InviteMessage.InviteMesageStatus.BEINVITEED ||msg.getStatus() == InviteMessage.InviteMesageStatus.BEAPPLYED ||
					msg.getStatus() == InviteMessage.InviteMesageStatus.GROUPINVITATION){
			}else {
				if(getItem(position-1).getStatus() == InviteMessage.InviteMesageStatus.BEINVITEED ||getItem(position-1).getStatus() == InviteMessage.InviteMesageStatus.BEAPPLYED ||
						getItem(position-1).getStatus() == InviteMessage.InviteMesageStatus.GROUPINVITATION){
					holder.kong.setVisibility(View.VISIBLE);
				}else {
					holder.kong.setVisibility(View.GONE);
				}
			}
		}
		if (msg != null) {
		    holder.agree.setVisibility(View.GONE);
			if(msg.getGroupId() != null){ // show group name
				holder.groupContainer.setVisibility(View.VISIBLE);
				holder.groupname.setText(TextUtils.isEmpty(msg.getGroupName())?msg.getGroupId():msg.getGroupName());
				//是群组
				holder.name.setText(TextUtils.isEmpty(msg.getGroupName())?msg.getGroupId():msg.getGroupName());
			} else{
				holder.groupContainer.setVisibility(View.GONE);
				//不是群
				holder.name.setText(msg.getFrom());
			}

			String reasonFrom = msg.getReason();
			try {
				UserFriendModel model = mGson.fromJson(reasonFrom, UserFriendModel.class);
				try {
					holder.name.setText(model.getNickname());
				} catch (Exception e) {
					holder.name.setText(msg.getFrom());
					e.printStackTrace();
				}
				try {
					ImageUtils.show(context, model.getPicture(), R.drawable.default_avatar, holder.avator);
				} catch (Exception e) {
					ImageUtils.show(context, "", R.drawable.default_avatar, holder.avator);
					e.printStackTrace();
				}
				holder.reason.setText(model.getReason());
			} catch (Exception e) {
				holder.reason.setText(R.string.Add_a_friend);
				e.printStackTrace();
			}
			// holder.time.setText(DateUtils.getTimestampString(new
			// Date(msg.getTime())));
			if (msg.getStatus() == InviteMesageStatus.BEAGREED) {
				holder.agree.setVisibility(View.GONE);
				holder.status.setVisibility(View.GONE);
				holder.reason.setText(str1);
			} else if(msg.getStatus() == InviteMesageStatus.BEREFUSED){
                holder.agree.setVisibility(View.GONE);
                holder.status.setVisibility(View.GONE);
                holder.reason.setText(R.string.refuse_be_you_friend);
            } else if (msg.getStatus() == InviteMesageStatus.BEINVITEED || msg.getStatus() == InviteMesageStatus.BEAPPLYED ||
			        msg.getStatus() == InviteMesageStatus.GROUPINVITATION) {
			    holder.agree.setVisibility(View.VISIBLE);
                holder.agree.setEnabled(true);
//                holder.agree.setBackgroundResource(android.R.drawable.btn_default);
                holder.status.setBackgroundResource(R.drawable.btn_agree_selector);
                holder.agree.setText(str2);

				holder.status.setVisibility(View.VISIBLE);
				holder.status.setEnabled(true);
//				holder.status.setBackgroundResource(android.R.drawable.btn_default);
				holder.agree.setBackgroundResource(R.drawable.btn_reject_selector);
				holder.status.setText(str7);
				if(msg.getStatus() == InviteMesageStatus.BEINVITEED){
					if (msg.getReason() == null) {
						// use default text
						holder.reason.setText(str3);
					}
				}else if (msg.getStatus() == InviteMesageStatus.BEAPPLYED) { //application to join group
					if (TextUtils.isEmpty(msg.getReason())) {
						holder.reason.setText(str4 + msg.getGroupName());
					}
				} else if (msg.getStatus() == InviteMesageStatus.GROUPINVITATION) {
				    if (TextUtils.isEmpty(msg.getReason())) {
                        holder.reason.setText(msg.getGroupInviter()+str8 + msg.getGroupName());
                    }
				}

				// set click listener
                holder.agree.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // accept invitation
                        acceptInvitation(holder.agree, holder.status, msg);
                    }
                });
				holder.status.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// decline invitation
					    refuseInvitation(holder.agree, holder.status, msg);
					}
				});
			} else if (msg.getStatus() == InviteMesageStatus.AGREED) {
				holder.status.setText(str5);
				holder.status.setTextColor(ContextCompat.getColor(context, R.color.tx_black_2));
				holder.status.setBackgroundDrawable(null);
				holder.status.setEnabled(false);
				holder.agree.setVisibility(View.GONE);
			} else if(msg.getStatus() == InviteMesageStatus.REFUSED){
				holder.status.setText(str6);
				holder.status.setBackgroundDrawable(null);
				holder.status.setTextColor(ContextCompat.getColor(context, R.color.tx_black_2));
				holder.status.setEnabled(false);
				holder.agree.setVisibility(View.GONE);
			} else if(msg.getStatus() == InviteMesageStatus.GROUPINVITATION_ACCEPTED){
				String inviter = TextUtils.isEmpty(EaseUserUtils.getUserInfo(msg.getGroupInviter()).safeGetRemark())?msg.getGroupInviter():EaseUserUtils.getUserInfo(msg.getGroupInviter()).safeGetRemark();
			    String str = inviter + str9 + msg.getGroupName();
                holder.status.setText(str);
				holder.status.setTextColor(ContextCompat.getColor(context, R.color.tx_black_2));
                holder.status.setBackgroundDrawable(null);
                holder.status.setEnabled(false);
				holder.agree.setVisibility(View.GONE);
            } else if(msg.getStatus() == InviteMesageStatus.GROUPINVITATION_DECLINED){
				String inviter = TextUtils.isEmpty(EaseUserUtils.getUserInfo(msg.getGroupInviter()).safeGetRemark())?msg.getGroupInviter():EaseUserUtils.getUserInfo(msg.getGroupInviter()).safeGetRemark();
                String str = inviter + str10 + msg.getGroupName();
                holder.status.setText(str);
                holder.status.setBackgroundDrawable(null);
                holder.status.setEnabled(false);
				holder.agree.setVisibility(View.GONE);
            }
		}

		return convertView;
	}

	/**
	 * accept invitation
	 *
	 */
	private void acceptInvitation(final Button buttonAgree, final Button buttonRefuse, final InviteMessage msg) {
			Observable<HttpResult<UserFriendModel>> observable = null;
		if (msg.getStatus() == InviteMesageStatus.BEINVITEED) {//accept be friends
			observable = ApiManger2.getApiService()
					.agreeAddFriend(msg.getMessageId());
		} else if (msg.getStatus() == InviteMesageStatus.BEAPPLYED) { //accept application to join group
			return;
		} else if (msg.getStatus() == InviteMesageStatus.GROUPINVITATION) {
			return;
		}else {
			return;
		}

		final String str2 = context.getResources().getString(R.string.Has_agreed_to);
		final String str3 = context.getResources().getString(R.string.Agree_with_failure);
		observable.compose(((BaseActivity)context).<HttpResult<UserFriendModel>>bindToLifeCyclerAndApplySchedulers())
				.subscribe(new BaseObserver2<UserFriendModel>() {
					@Override
					protected void onSuccess(UserFriendModel model) {
						if(model != null){
							EaseUser easeUser = new EaseUser(model.getId());
							easeUser.setChatid(String.valueOf(model.getId()));
							easeUser.setNickname(model.getNickname());
							easeUser.setAvatar(model.getPicture());
							easeUser.setIsLock(0);

							DemoHelper.getInstance().saveContact(easeUser);
						}
						msg.setStatus(InviteMesageStatus.AGREED);
						// update database
						ContentValues values = new ContentValues();
						values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
						messgeDao.updateMessage(msg.getId(), values);
						((Activity) context).runOnUiThread(new Runnable() {

							@Override
							public void run() {
								buttonRefuse.setText(str2);
								buttonRefuse.setBackgroundDrawable(null);
								buttonRefuse.setTextColor(Color.BLACK);
								buttonRefuse.setEnabled(false);
								buttonAgree.setVisibility(View.GONE);
							}
						});
					}

					@Override
					public void onFaild(int code, String message) {
						super.onFaild(code, message);
					}
				});

		/*final ProgressDialog pd = new ProgressDialog(context);
		String str1 = context.getResources().getString(R.string.Are_agree_with);
		final String str2 = context.getResources().getString(R.string.Has_agreed_to);
		final String str3 = context.getResources().getString(R.string.Agree_with_failure);
		pd.setMessage(str1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();

		new Thread(new Runnable() {
			public void run() {
				// call api
				try {
					if (msg.getStatus() == InviteMesageStatus.BEINVITEED) {//accept be friends
						EMClient.getInstance().contactManager().acceptInvitation(msg.getFrom());
					} else if (msg.getStatus() == InviteMesageStatus.BEAPPLYED) { //accept application to join group
						EMClient.getInstance().groupManager().acceptApplication(msg.getFrom(), msg.getGroupId());
					} else if (msg.getStatus() == InviteMesageStatus.GROUPINVITATION) {
					    EMClient.getInstance().groupManager().acceptInvitation(msg.getGroupId(), msg.getGroupInviter());
					}
                    msg.setStatus(InviteMesageStatus.AGREED);
                    // update database
                    ContentValues values = new ContentValues();
                    values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
                    messgeDao.updateMessage(msg.getId(), values);
					((Activity) context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							pd.dismiss();
							buttonRefuse.setText(str2);
							buttonRefuse.setBackgroundDrawable(null);
							buttonRefuse.setTextColor(Color.BLACK);
							buttonRefuse.setEnabled(false);
							
							buttonAgree.setVisibility(View.GONE);
						}
					});
				} catch (final Exception e) {
					((Activity) context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							pd.dismiss();
							Toast.makeText(context, str3 + e.getMessage(), Toast.LENGTH_LONG).show();
						}
					});

				}
			}
		}).start();*/
	}

	/**
     * decline invitation
     *
     */
    private void refuseInvitation(final Button buttonAgree, final Button buttonRefuse, final InviteMessage msg) {
		Observable<HttpResult<EmptyData>> observable = null;
		if (msg.getStatus() == InviteMesageStatus.BEINVITEED) {//decline the invitation
		    observable = ApiManger2.getApiService().rejectAddFriend(msg.getMessageId(), null);
		} else if (msg.getStatus() == InviteMesageStatus.BEAPPLYED) { //decline application to join group
			return;
		} else if (msg.getStatus() == InviteMesageStatus.GROUPINVITATION) {
			return;
		}else {
			return;
		}
        final String str2 = context.getResources().getString(R.string.Has_refused_to);
		 observable
				.compose(((BaseActivity)context).<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers())
                 .subscribe(new BaseObserver2<EmptyData>() {
                     @Override
                     protected void onSuccess(EmptyData emptyData) {
                         msg.setStatus(InviteMesageStatus.REFUSED);
                         // update database
                         ContentValues values = new ContentValues();
                         values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
                         messgeDao.updateMessage(msg.getId(), values);
                         ((Activity) context).runOnUiThread(new Runnable() {

                             @Override
                             public void run() {
                                 buttonRefuse.setText(str2);
                                 buttonRefuse.setBackgroundDrawable(null);
                                 buttonRefuse.setEnabled(false);
                                 buttonRefuse.setTextColor(ContextCompat.getColor(context, R.color.tx_black_2));
                                 buttonAgree.setVisibility(View.GONE);
                             }
                         });
                     }
                 });

        /*final ProgressDialog pd = new ProgressDialog(context);
        String str1 = context.getResources().getString(R.string.Are_refuse_with);
        final String str2 = context.getResources().getString(R.string.Has_refused_to);
        final String str3 = context.getResources().getString(R.string.Refuse_with_failure);
        pd.setMessage(str1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        new Thread(new Runnable() {
            public void run() {
                // call api
                try {
                    if (msg.getStatus() == InviteMesageStatus.BEINVITEED) {//decline the invitation
                        EMClient.getInstance().contactManager().declineInvitation(msg.getFrom());
                    } else if (msg.getStatus() == InviteMesageStatus.BEAPPLYED) { //decline application to join group
                        EMClient.getInstance().groupManager().declineApplication(msg.getFrom(), msg.getGroupId(), "");
                    } else if (msg.getStatus() == InviteMesageStatus.GROUPINVITATION) {
                        EMClient.getInstance().groupManager().declineInvitation(msg.getGroupId(), msg.getGroupInviter(), "");
                    }
                    msg.setStatus(InviteMesageStatus.REFUSED);
                    // update database
                    ContentValues values = new ContentValues();
                    values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
                    messgeDao.updateMessage(msg.getId(), values);
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            buttonRefuse.setText(str2);
                            buttonRefuse.setBackgroundDrawable(null);
                            buttonRefuse.setEnabled(false);
							buttonRefuse.setTextColor(Color.BLACK);
                            buttonAgree.setVisibility(View.INVISIBLE);
                        }
                    });
                } catch (final Exception e) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(context, str3 + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();*/
    }

	private static class ViewHolder {
		ImageView avator;
		TextView name;
		TextView reason;
        Button agree;
		Button status;
		LinearLayout groupContainer;
		TextView groupname;
		View kong;
		// TextView time;
	}

}
