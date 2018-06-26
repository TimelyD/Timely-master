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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.easeui.widget.EaseContactList;
import com.hyphenate.util.EMLog;
import com.tg.tgt.App;
import com.tg.tgt.DemoHelper;
import com.tg.tgt.R;
import com.tg.tgt.db.InviteMessgeDao;
import com.tg.tgt.db.UserDao;
import com.tg.tgt.helper.SecurityDialog;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.IView;
import com.tg.tgt.parse.ParseManager;
import com.tg.tgt.utils.CodeUtils;
import com.tg.tgt.utils.ToastUtils;
import com.tg.tgt.widget.ContactItemView;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * contact list
 * 
 */
public class ContactListFragment extends EaseContactListFragment {
	
    private static final String TAG = ContactListFragment.class.getSimpleName();
    private ContactSyncListener contactSyncListener;
    private BlackListSyncListener blackListSyncListener;
    private ContactInfoSyncListener contactInfoSyncListener;
    private View loadingView;
    private ContactItemView applicationItem;
    private InviteMessgeDao inviteMessgeDao;

    @SuppressLint("InflateParams")
    @Override
    protected void initView() {
        super.initView();
        @SuppressLint("InflateParams") View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.em_contacts_header, null);
        HeaderItemClickListener clickListener = new HeaderItemClickListener();
        applicationItem = (ContactItemView) headerView.findViewById(R.id.application_item);
        applicationItem.setOnClickListener(clickListener);
        headerView.findViewById(R.id.group_item).setOnClickListener(clickListener);
//        headerView.findViewById(R.id.chat_room_item).setOnClickListener(clickListener);
//        headerView.findViewById(R.id.robot_item).setOnClickListener(clickListener);
        listView.addHeaderView(headerView);
        //add loading view
        loadingView = LayoutInflater.from(getActivity()).inflate(R.layout.em_layout_loading_data, null);
        contentContainer.addView(loadingView);

        registerForContextMenu(listView);
       /* ParseManager.getInstance().getContactInfos(null, new EMValueCallBack<List<EaseUser>>() {
            @Override
            public void onSuccess(List<EaseUser> easeUsers) {
                DemoHelper.getInstance().updateContactList(easeUsers);
            }

            @Override
            public void onError(int i, String s) {
                ToastUtils.showToast(getActivity(), s);
            }
        });*/
    }
    
    @Override
    public void refresh() {
        Map<String, EaseUser> m = DemoHelper.getInstance().getContactList();
        Log.i("dcz",m.size()+"");
        if (m instanceof Hashtable<?, ?>) {
            //noinspection unchecked
            m = (Map<String, EaseUser>) ((Hashtable<String, EaseUser>)m).clone();
        }
        setContactsMap(m);
        super.refresh();
        if(inviteMessgeDao == null){
            inviteMessgeDao = new InviteMessgeDao(getActivity());
        }
        int unreadMessagesCount = inviteMessgeDao.getUnreadMessagesCount();
        if(unreadMessagesCount > 0){
            applicationItem.setUnreadCount(unreadMessagesCount);
            applicationItem.showUnreadMsgView();
        }else{
            applicationItem.hideUnreadMsgView();
        }
    }
    
    
    @SuppressWarnings("unchecked")
    @Override
    protected void setUpView() {
//        titleBar.setRightImageResource(R.drawable.em_add);
//        titleBar.setRightLayoutClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
////                startActivity(new Intent(getActivity(), AddContactActivity.class));
//                NetUtils.hasDataConnection(getActivity());
//            }
//        });
        titleBar.setLeftImageResource(R.drawable.menu);
        titleBar.setLeftLayoutVisibility(View.INVISIBLE);
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).toggleMenu();
            }
        });
        titleBar.setRightImageResource(R.drawable.more);
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).showMenu(titleBar);
            }
        });
        /*titleBar.setSecondRightImageResource(R.drawable.search);
        titleBar.setSecondRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).search();
            }
        });*/
        //设置联系人数据
        Map<String, EaseUser> m = DemoHelper.getInstance().getContactList();
        if (m instanceof Hashtable<?, ?>) {
            m = (Map<String, EaseUser>) ((Hashtable<String, EaseUser>)m).clone();
        }
        setContactsMap(m);
        contactListLayout.setEaseContactListHelper(new EaseContactList.EaseContactListHelper() {
            @Override
            public void onSetIsMsgClock(String username, ImageView msgClock, ImageView avatar, TextView name) {
                CodeUtils.setIsMsgClock((IView) getActivity(), username, msgClock, avatar, name);
            }
        });
        super.setUpView();
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EaseUser user = (EaseUser)listView.getItemAtPosition(position);
                if (user != null) {
                    final String username = user.getUsername();
                    View lock = view.findViewById(com.hyphenate.easeui.R.id.iv_msg_lock);
                    if(!(boolean)lock.getTag(R.id.msg_lock_tag)){
                        //没有初始化，需要判断
                        /*CodeUtils.checkIsCode((IView) getActivity(), username, new CodeUtils.CodeUtilsHelper() {
                            @Override
                            public void onResult(IsCodeResult result) {
                                if (result.getIscode() == 1) {//加密
                                    showSecurity(username, result);
                                } else {
                                    toChatAct(username, result);
                                }
                            }
                        });*/
//                        ToastUtils.showToast(mContext, "未初始化");
                        /*((BaseActivity)mContext).showProgress(R.string.loading);
                        DemoHelper.getInstance().asyncFetchContactsFromServer(new EMValueCallBack<List<EaseUser>>() {
                            @Override
                            public void onSuccess(List<EaseUser> easeUsers) {
                                ((BaseActivity)mContext).dismissProgress();
                                EaseUser userInfo = EaseUserUtils.getUserInfo(username);
                                toChatAct(username, userInfo);
                            }

                            @Override
                            public void onError(int i, String s) {
                                ((BaseActivity)mContext).dismissProgress();
                            }
                        });*/
                        CodeUtils.fetchUser(((BaseActivity) mContext), username, true, new Consumer<EaseUser>() {
                            @Override
                            public void accept(@NonNull EaseUser easeUser) throws Exception {
                                if(easeUser != null){
                                    toChatAct(username, easeUser);
                                }else {
                                    ToastUtils.showToast(App.applicationContext, R.string.user_not_exist);
                                }
                            }
                        });
                    }else if(lock.getVisibility() == View.INVISIBLE){
                        //没有加密
                        toChatAct(username, (EaseUser) lock.getTag(R.id.msg_result_tag));
                    }else {
                        //加密
                        showSecurity(username, (EaseUser) lock.getTag(R.id.msg_result_tag));
                    }
                }
            }
        });


        // 进入添加好友页
//        titleBar.getRightLayout().setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), AddContactActivity.class));
//            }
//        });
        
        
        contactSyncListener = new ContactSyncListener();
        DemoHelper.getInstance().addSyncContactListener(contactSyncListener);
        
        blackListSyncListener = new BlackListSyncListener();
        DemoHelper.getInstance().addSyncBlackListListener(blackListSyncListener);
        
        contactInfoSyncListener = new ContactInfoSyncListener();
        DemoHelper.getInstance().getUserProfileManager().addSyncContactInfoListener(contactInfoSyncListener);

        if (DemoHelper.getInstance().isSyncingContactsWithServer()) {
            loadingView.setVisibility(View.VISIBLE);
        } else if (DemoHelper.getInstance().isContactsSyncedWithServer()) {
            loadingView.setVisibility(View.GONE);
        }
    }

    private void showSecurity(final String username, final EaseUser result) {
        SecurityDialog.show(getActivity(),this.getString(R.string.security_title),new SecurityDialog.OnSecurityListener(){
            @Override
            public void onPass() {
                toChatAct(username, result);
            }
        });
    }

    private void toChatAct(String username, EaseUser result) {
        // demo中直接进入聊天页面，实际一般是进入用户详情页
        Intent intent = new Intent(getActivity(), ChatActivity.class);

        intent.putExtra("userId", username);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (contactSyncListener != null) {
            DemoHelper.getInstance().removeSyncContactListener(contactSyncListener);
            contactSyncListener = null;
        }
        
        if(blackListSyncListener != null){
            DemoHelper.getInstance().removeSyncBlackListListener(blackListSyncListener);
        }
        
        if(contactInfoSyncListener != null){
            DemoHelper.getInstance().getUserProfileManager().removeSyncContactInfoListener(contactInfoSyncListener);
        }
    }
    
	
	protected class HeaderItemClickListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.application_item:
                // 进入申请与通知页面
                startActivity(new Intent(getActivity(), NewFriendsMsgActivity.class));
                break;
            case R.id.group_item:
                // 进入群聊列表页面
                startActivity(new Intent(getActivity(), GroupsActivity.class));
                break;
//            case R.id.chat_room_item:
//                //进入聊天室列表页面
//                startActivity(new Intent(getActivity(), PublicChatRoomsActivity.class));
//                break;
//            case R.id.robot_item:
//                //进入Robot列表页面
//                startActivity(new Intent(getActivity(), RobotsActivity.class));
//                break;

            default:
                break;
            }
        }
	    
	}
	

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	    toBeProcessUser = (EaseUser) listView.getItemAtPosition(((AdapterContextMenuInfo) menuInfo).position);
	    toBeProcessUsername = toBeProcessUser.getUsername();
		getActivity().getMenuInflater().inflate(R.menu.em_context_contact_list, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_contact) {
			/*try {
                // delete contact
                deleteContact(toBeProcessUser);
                // remove invitation message
                InviteMessgeDao dao = new InviteMessgeDao(getActivity());
                dao.deleteMessage(toBeProcessUser.getUsername());
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            CodeUtils.deleteContact(((BaseActivity) mContext), toBeProcessUser, new Consumer<Boolean>() {
                @Override
                public void accept(@NonNull Boolean aBoolean) throws Exception {
                    if(aBoolean){
                        contactListLayout.refresh();
                    }
                }
            });
			return true;
		}/*else if(item.getItemId() == R.id.add_to_blacklist){
			moveToBlacklist(toBeProcessUsername);
			return true;
		}*/
		return super.onContextItemSelected(item);
	}


	/**
	 * delete contact
	 * 
	 */
	public void deleteContact(final EaseUser tobeDeleteUser) {
        ApiManger2.getApiService()
                .deleteFriend(tobeDeleteUser.getChatid())
                .compose(((BaseActivity)mContext).<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<EmptyData>() {
                    @Override
                    protected void onSuccess(EmptyData emptyData) {
                        UserDao dao = new UserDao(getActivity());
                        dao.deleteContact(tobeDeleteUser.getUsername());
                        DemoHelper.getInstance().getContactList().remove(tobeDeleteUser.getUsername());
                                contactList.remove(tobeDeleteUser);
                                contactListLayout.refresh();
                    }

                    @Override
                    public void onFaild(int code, String message) {
                        super.onFaild(code, message);
                    }
                });
        /*String st1 = getResources().getString(R.string.deleting);
		final String st2 = getResources().getString(R.string.Delete_failed);
		final ProgressDialog pd = new ProgressDialog(getActivity());
		pd.setMessage(st1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					EMClient.getInstance().contactManager().deleteContact(tobeDeleteUser.getUsername());
					// remove user from memory and database
					UserDao dao = new UserDao(getActivity());
					dao.deleteContact(tobeDeleteUser.getUsername());
					DemoHelper.getInstance().getContactList().remove(tobeDeleteUser.getUsername());
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							contactList.remove(tobeDeleteUser);
							contactListLayout.refresh();

						}
					});
				} catch (final Exception e) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getActivity(), st2 + e.getMessage(), Toast.LENGTH_LONG).show();
						}
					});

				}

			}
		}).start();*/

	}
	
	class ContactSyncListener implements DemoHelper.DataSyncListener {
        @Override
        public void onSyncComplete(final boolean success) {
            EMLog.d(TAG, "on contact list sync success:" + success);
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    getActivity().runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            if(success){
                                loadingView.setVisibility(View.GONE);
                                refresh();
                            }else{
                                String s1 = getResources().getString(R.string.get_failed_please_check);
                                Toast.makeText(getActivity(), s1, Toast.LENGTH_LONG).show();
                                loadingView.setVisibility(View.GONE);
                            }
                        }
                        
                    });
                }
            });
        }
    }
    
    class BlackListSyncListener implements DemoHelper.DataSyncListener {

        @Override
        public void onSyncComplete(boolean success) {
            getActivity().runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    refresh();
                }
            });
        }
        
    }

    class ContactInfoSyncListener implements DemoHelper.DataSyncListener {

        @Override
        public void onSyncComplete(final boolean success) {
            EMLog.d(TAG, "on contactinfo list sync success:" + success);
            getActivity().runOnUiThread(new Runnable() {
                
                @Override
                public void run() {
                    loadingView.setVisibility(View.GONE);
                    if(success){
                        refresh();
                    }
                }
            });
        }
        
    }
	
}
