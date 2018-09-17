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

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chat.EMPushConfigs;
import com.hyphenate.easeui.GlideApp;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseGroupListener;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.GroupHelper;
import com.hyphenate.easeui.utils.SpUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseExpandGridView;
import com.hyphenate.easeui.widget.EaseSwitchButton;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.easeui.widget.ZQImageViewRoundOval;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.helper.GroupManger;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpHelper;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.model2.GroupModel;
import com.tg.tgt.http.model2.GroupUserModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;

public class GroupDetailsActivity2 extends BaseActivity implements OnClickListener {
    private static final String TAG = "GroupDetailsActivity";
    private static final int REQUEST_CODE_ADD_USER = 0;
    private static final int REQUEST_CODE_ADD_USER2 =7;
    private static final int REQUEST_CODE_ADD_USER3 =8;
    private static final int REQUEST_CODE_EXIT = 1;
    private static final int REQUEST_CODE_EXIT_DELETE = 2;
    private static final int REQUEST_CODE_EDIT_GROUPNAME = 5;
    private static final int REQUEST_CODE_EDIT_GROUP_DESCRIPTION = 6;


    private String groupId;
    private ProgressBar loadingPB;
    private Button exitBtn;
    private Button deleteBtn;
    private EMGroup group;
    private GridAdapter membersAdapter;
    private ProgressDialog progressDialog;
    private LinearLayout zhuan;
    private Boolean State=false;
    public static GroupDetailsActivity2 instance;


    String st = "";

    private EaseSwitchButton switchButton;
    private EaseSwitchButton offlinePushSwitch;
    private EMPushConfigs pushConfigs;

    private String operationUserId = "";

    private List<GroupUserModel> memberList = Collections.synchronizedList(new ArrayList<GroupUserModel>());
    //public static List<GroupUserModel> memberList2=new ArrayList<>();
    GroupChangeListener groupChangeListener;
    private TextView mTvMemberCountTv;
    private GroupModel mGroup;
    private Map<String, GroupUserModel> mGroupUsers;
    private PublishSubject<List<GroupUserModel>> mSubject;
    private Disposable disposable;
    private EaseTitleBar mTitleBar;
    private View more;
    private int n=0;

    private boolean isLoading = false;
    private boolean isClickDelete = false;
    private Handler mHandlerLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        groupId = getIntent().getStringExtra("groupId");
        group = EMClient.getInstance().groupManager().getGroup(groupId);

        // we are not supposed to show the group if we don't find the group
        if (group == null) {
            finish();
            return;
        }

        isLoading = true;
        isClickDelete = false;

        setContentView(R.layout.em_activity_group_details);
  //      setTitleBarLeftBack();
        mTitleBar = ((EaseTitleBar) findViewById(R.id.title_bar));

        mTitleBar.setLeftLayoutClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mSubject != null) {
//                    Disposable subscribe = mSubject.subscribe();
//                    subscribe.dispose();
//                }
                finish();
            }
        });
        instance = this;
        st = /*getResources().getString(com.tg.tgt.R.string.people)*/")";

        mTvMemberCountTv = (TextView) findViewById(R.id.tv_group_member_count);

        RelativeLayout clearAllHistory = (RelativeLayout) findViewById(R.id.clear_all_history);
        loadingPB = (ProgressBar) findViewById(R.id.progressBar);
        exitBtn = (Button) findViewById(R.id.btn_exit_grp);
        deleteBtn = (Button) findViewById(R.id.btn_exitdel_grp);
        more = findViewById(R.id.more);
        zhuan= (LinearLayout) findViewById(R.id.zhuan);
        RelativeLayout changeGroupNameLayout = (RelativeLayout) findViewById(R.id.rl_change_group_name);
        RelativeLayout changeGroupDescriptionLayout = (RelativeLayout) findViewById(R.id.rl_change_group_description);
        RelativeLayout idLayout = (RelativeLayout) findViewById(R.id.rl_group_id);
        idLayout.setVisibility(View.VISIBLE);
        TextView idText = (TextView) findViewById(R.id.tv_group_id_value);

        RelativeLayout rl_switch_block_groupmsg = (RelativeLayout) findViewById(R.id.rl_switch_block_groupmsg);
        switchButton = (EaseSwitchButton) findViewById(R.id.switch_btn);
        RelativeLayout searchLayout = (RelativeLayout) findViewById(R.id.rl_search);

        RelativeLayout blockOfflineLayout = (RelativeLayout) findViewById(R.id.rl_switch_block_offline_message);
        offlinePushSwitch = (EaseSwitchButton) findViewById(R.id.switch_block_offline_message);

        groupChangeListener = new GroupChangeListener();
        EMClient.getInstance().groupManager().addGroupChangeListener(groupChangeListener);

        mGroup = GroupManger.getGroup(groupId);
        idText.setText(mGroup.getGroupSn());
//        mTvMemberCountTv.setText(String.format(getString(R.string.number_of_group_member), mGroup.getAffiliationsCont
//                ()));
        //mTitleBar.setTitle(mGroup.getGroupName() + "(" + mGroup.getAffiliationsCont() + st);

        mGroupUsers = GroupManger.getGroupUsers(groupId);
        memberList.addAll(mGroupUsers.values());
        sortGroup(memberList);
        n = isCurrentOwner() || mGroup.getGroupOwner() ? 2 : 0;
        //int size = memberList.size() + n > 10 ? 10 : memberList.size() + n;
        Log.i("zzz",n+"+"+memberList.size());
        if(memberList.size() + n > 10){
            more.setVisibility(View.VISIBLE);
        }else {
            more.setVisibility(View.GONE);
        }
      /*  memberList2.clear();
        memberList2=memberList;*/
        mTitleBar.setTitle(mGroup.getGroupName() + "(" + memberList.size() +")");
        zhuan.setVisibility(isCurrentOwner()||mGroup.getGroupOwner()?View.VISIBLE:View.GONE);
        membersAdapter = new GridAdapter(this, R.layout.em_grid_owner,memberList);
        EaseExpandGridView userGridview = (EaseExpandGridView) findViewById(R.id.gridview);
        userGridview.setAdapter(membersAdapter);

        // 保证每次进详情看到的都是最新的group
//		updateGroup();
        refreshUi();
        updateGroup();
        more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,MoveActivity.class);
                State=true;
                intent.putExtra("groupId",groupId);
                startActivity(intent);
            }
        });
        zhuan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult((new Intent(GroupDetailsActivity2.this, GroupPickContacts3Activity.class).putExtra
                                ("groupId", groupId)),
                        REQUEST_CODE_ADD_USER3);
            }
        });
        clearAllHistory.setOnClickListener(this);
        changeGroupNameLayout.setOnClickListener(this);
        changeGroupDescriptionLayout.setOnClickListener(this);
        rl_switch_block_groupmsg.setOnClickListener(this);
        searchLayout.setOnClickListener(this);
        blockOfflineLayout.setOnClickListener(this);

        mHandlerLoading = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 100001:
                        if (isClickDelete && isLoading){
                            Log.e("Tag","isClickDelete="+isClickDelete+"-----isLoading="+isLoading);
                            isClickDelete = false;
                            isLoading = false;
                            startActivityForResult((new Intent(GroupDetailsActivity2.this, GroupPickContacts2Activity.class).putExtra
                                            ("groupId", groupId)),
                                    REQUEST_CODE_ADD_USER2);
                        }
                        break;
                }
            }
        };
    }
    private void shua(){
        mGroup = GroupManger.getGroup(groupId);
        mGroupUsers = GroupManger.getGroupUsers(groupId);
        memberList.clear();
        memberList.addAll(mGroupUsers.values());
        sortGroup(memberList);
        n = isCurrentOwner() || mGroup.getGroupOwner() ? 2 : 0;
        Log.i("zzz",n+"+"+memberList.size());
        if(memberList.size() + n > 10){
            more.setVisibility(View.VISIBLE);
        }else {
            more.setVisibility(View.GONE);
        }
        mTitleBar.setTitle(mGroup.getGroupName() + "(" + memberList.size() +")");
        membersAdapter.notifyDataSetChanged();
        // 保证每次进详情看到的都是最新的group
//		updateGroup();
        refreshUi();
        updateGroup();
    }
    private void setEn(Boolean a){
        if(a==false){
            mTitleBar.setLeftLayoutClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    return;
                }
            });
        }else {
            mTitleBar.setLeftLayoutClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
    private Boolean back=true;
    private void updateGroup() {
        //mTitleBar.setLeftLayoutVisibility(View.INVISIBLE);
        setEn(false);
        if (mSubject == null) {
            synchronized (loadingPB) {
                if (mSubject == null) {
                    mSubject = PublishSubject.create();
                    back=false;
                    mSubject.doOnSubscribe(new Consumer<Disposable>() {
                                @Override
                                public void accept(@NonNull Disposable disposable) throws Exception {
                                    GroupDetailsActivity2.this.disposable = disposable;
                                }
                            })
                            .doOnError(new Consumer<Throwable>() {
                                @Override
                                public void accept(@NonNull Throwable throwable) throws Exception {
                                    back = true;
                                    //mTitleBar.setLeftLayoutVisibility(View.VISIBLE);
                                    setEn(true);
                                }
                            })
                            .debounce(500, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<List<GroupUserModel>>() {
                                @Override
                                public void accept(@NonNull List<GroupUserModel> models) throws Exception {
                                        Observable.just(memberList)
                                                .map(new Function<List<GroupUserModel>, List<GroupUserModel>>() {
                                                    @Override
                                                    public List<GroupUserModel> apply(@NonNull List<GroupUserModel> strings)
                                                            throws Exception {

                                                        if (pushConfigs == null) {
                                                            EMClient.getInstance().pushManager().getPushConfigsFromServer();
                                                        }
                                                        group = EMClient.getInstance().groupManager().getGroupFromServer
                                                                (groupId);
                                                        mGroup = GroupManger.fetchGroup(groupId);
                                                        List<GroupUserModel> groupUserModels = mGroup.getGroupUserModels();
                                                        strings.clear();
                                                        strings.addAll(groupUserModels);
                                                        sortGroup(strings);
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                back = true;
                                                                //mTitleBar.setLeftLayoutVisibility(View.VISIBLE);
                                                                setEn(true);
                                                            }
                                                        });
                                                        return strings;
                                                    }
                                                })
                                                .compose(mActivity.<List<GroupUserModel>>bindToLifeCyclerAndApplySchedulers
                                                        (null))
                                                .doOnSubscribe(new Consumer<Disposable>() {
                                                    @Override
                                                    public void accept(@NonNull Disposable disposable) throws Exception {
                                                        loadingPB.setVisibility(View.VISIBLE);
                                                        back = false;
                                                        //mTitleBar.setLeftLayoutVisibility(View.INVISIBLE);
                                                        setEn(false);
                                                    }
                                                })
                                                .subscribe(new Consumer<List<GroupUserModel>>() {
                                                    @Override
                                                    public void accept(@NonNull List<GroupUserModel> strings) throws
                                                            Exception {
                                                        mGroupUsers = GroupManger.getGroupUsers(groupId);
//                                                    mTvMemberCountTv.setText(String.format(getString(R.string
//                                                            .number_of_group_member), mGroup.getAffiliationsCont()));
                                                        membersAdapter.notifyDataSetChanged();

                                                        //mTitleBar.setTitle(mGroup.getGroupName() + "(" + mGroup.getAffiliationsCont() + ")");
                                                        mTitleBar.setTitle(mGroup.getGroupName() + "(" + memberList.size() +")");
                                                        if(memberList.size() + n > 10){
                                                            more.setVisibility(View.VISIBLE);
                                                        }else {
                                                            more.setVisibility(View.GONE);
                                                        }
                                                        back = true;
                                                        loadingPB.setVisibility(View.INVISIBLE);
                                                        //mTitleBar.setLeftLayoutVisibility(View.VISIBLE);
                                                        setEn(true);
                                                        // update block
                                                        refreshUi();
                                                    }
                                                }, new Consumer<Throwable>() {
                                                    @Override
                                                    public void accept(@NonNull Throwable throwable) throws Exception {
                                                        throwable.printStackTrace();
                                                        loadingPB.setVisibility(View.INVISIBLE);
                                                        //mTitleBar.setLeftLayoutVisibility(View.VISIBLE);
                                                        setEn(true);
                                                        back=true;
                                                        EaseAlertDialog dialog = new EaseAlertDialog(mActivity,
                                                                HttpHelper.handleException
                                                                        (throwable), getString(R.string.give_up), getString(R
                                                                .string.retry), new EaseAlertDialog.AlertDialogUser() {
                                                            @Override
                                                            public void onResult(boolean confirmed, Bundle bundle) {
                                                                if (confirmed) {
                                                                    updateGroup();
                                                                } else {
                                                                    onBackPressed();
                                                                }
                                                            }
                                                        });
                                                     //   if(back==false){
                                                            dialog.show();
                                                      //  }
                                                    }
                                                });
                                    }
                            });
                }
            }
        }

        loadingPB.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSubject.onNext(memberList);
            }
        }, 50);
    }

    private void refreshUi() {
        EMLog.d(TAG, "group msg is blocked:" + mGroup.getBlocks());
        if (group.isMsgBlocked()) {
            switchButton.openSwitch();
        } else {
            switchButton.closeSwitch();
        }
        List<String> disabledIds = EMClient.getInstance().pushManager().getNoPushGroups();
        if (disabledIds != null && disabledIds.contains(groupId)) {
            offlinePushSwitch.openSwitch();
        } else {
            offlinePushSwitch.closeSwitch();
        }

        RelativeLayout changeGroupNameLayout = (RelativeLayout) findViewById(R.id.rl_change_group_name);
        RelativeLayout changeGroupDescriptionLayout = (RelativeLayout) findViewById(R.id
                .rl_change_group_description);
        boolean isOwner = isCurrentOwner();
        exitBtn.setVisibility(isOwner ? View.GONE : View.VISIBLE);
        deleteBtn.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        changeGroupNameLayout.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        zhuan.setVisibility(isOwner ? View.VISIBLE : View.GONE);
//        changeGroupDescriptionLayout.setVisibility(isOwner ? View.VISIBLE : View.GONE);
    }

    //排序，当前用户创建的排在前
    private void sortGroup(List<GroupUserModel> list) {
        Collections.sort(list, new Comparator<GroupUserModel>() {
            @Override
            public int compare(GroupUserModel lhs, GroupUserModel rhs) {
                boolean lhsOwner = lhs.getGroupOwner();
                boolean rhsOwner = rhs.getGroupOwner();
                long lhsId = lhs.getUserId();
                long rhsId = rhs.getUserId();
                /*if(lhsOwner && rhsOwner){
                    return 0;
				}else */
                if (!lhsOwner & rhsOwner) {
                    return 1;
                } else if (lhsOwner & !rhsOwner) {
                    return -1;
                } else {
                    return (int) (lhsId - rhsId);
                }
//				return EMClient.getInstance().getCurrentUser().equals(rhs.getOwner()) ? 1 :0;
            }
        });
    }

    boolean isCurrentOwner(EMGroup group) {
        //return mGroup != null && App.getMyUid().equals(String.valueOf(mGroup.getUserId()));
        return mGroup != null && mGroup.getGroupOwner()==true;
		/*String owner = group.getOwner();
		if (owner == null || owner.isEmpty()) {
			return false;
		}
		return owner.equals(EMClient.getInstance().getCurrentUser());*/
    }

    boolean isCurrentOwner() {
        //return mGroup != null && App.getMyUid().equals(String.valueOf(mGroup.getUserId()));
        return mGroup != null && mGroup.getGroupOwner()==true;
    }

    boolean isCurrentAdmin(EMGroup group) {
        return mGroupUsers.get(SpUtils.get(mContext, Constant.USERNAME, "")).getGroupAdmin();
		/*synchronized (adminList) {
			String currentUser = EMClient.getInstance().getCurrentUser();
			for (String admin : adminList) {
				if (currentUser.equals(admin)) {
					return true;
				}
			}
		}
		return false;*/
    }

    boolean isAdmin(String id) {
        return mGroupUsers.get(id).getGroupAdmin();
		/*synchronized (adminList) {
			for (String admin : adminList) {
				if (id.equals(admin)) {
					return true;
				}
			}
		}
		return false;*/
    }

    boolean isInAdminist(String id) {
        return mGroupUsers.get(id).getGroupAdmin();
		/*synchronized (adminList) {
			if (id != null && !id.isEmpty()) {
				for (String item : adminList) {
					if (id.equals(item)) {
						return true;
					}
				}
			}
		}
		return false;*/
    }

    boolean isInBlackList(String id) {
        return mGroupUsers.get(id).getBlocks();
		/*synchronized (blackList) {
			if (id != null && !id.isEmpty()) {
				for (String item : blackList) {
					if (id.equals(item)) {
						return true;
					}
				}
			}
		}
		return false;*/
    }

    boolean isInMuteList(String id) {
        return mGroupUsers.get(id).getMute();
		/*synchronized (muteList) {
			if (id != null && !id.isEmpty()) {
				for (String item : muteList) {
					if (id.equals(item)) {
						return true;
					}
				}
			}
		}
		return false;*/
    }

    boolean isCanAddMember(EMGroup group) {
        return isCurrentOwner()|| mGroup != null && mGroup.getAllowInvites();
		/*if (group.isMemberAllowToInvite() ||
				isAdmin(EMClient.getInstance().getCurrentUser()) ||
				isCurrentOwner(group)) {
			return true;
		}
		return false;*/
    }

    public String getMembers(String[] members) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < members.length; i++) {
            sb.append(members[i]);
            if (i != members.length - 1)
                sb.append(",");
        }
        return sb.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String st1 = getResources().getString(R.string.being_added);
        String st2 = getResources().getString(R.string.is_quit_the_group_chat);
        String st3 = getResources().getString(R.string.chatting_is_dissolution);
        String st4 = getResources().getString(R.string.are_empty_group_of_news);
        final String st5 = getResources().getString(R.string.is_modify_the_group_name);
        final String st6 = getResources().getString(R.string.Modify_the_group_name_successful);
        final String st7 = getResources().getString(R.string.change_the_group_name_failed_please);

        final String st8 = getResources().getString(R.string.is_modify_the_group_description);
        final String st9 = getResources().getString(R.string.Modify_the_group_description_successful);
        final String st10 = getResources().getString(R.string.change_the_group_description_failed_please);

        if (resultCode == RESULT_OK) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(GroupDetailsActivity2.this);
                progressDialog.setMessage(st1);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            switch (requestCode) {
                case REQUEST_CODE_ADD_USER3://转让群主
                    String zhuan_id = data.getStringExtra("zhuan_id");
                    ApiManger2.getApiService().transfer(mGroup.getId().toString(),zhuan_id).compose(mActivity
                            .<HttpResult<List<GroupUserModel>>>bindToLifeCyclerAndApplySchedulers(false))
                            .subscribe(new BaseObserver2<List<GroupUserModel>>() {
                                @Override
                                protected void onSuccess(List<GroupUserModel> emptyData) {
                                    shua();
                                }
                            });
                    break;
                case REQUEST_CODE_ADD_USER2://踢群成员
                    final String[] newmember = data.getStringArrayExtra("newmembers");
                    final String[] name = data.getStringArrayExtra("name");
                    if(newmember == null || newmember.length < 1){
                        return;
                    }
                    final List<GroupUserModel> menlist = memberList;
                    ApiManger2.getApiService().deleteUser(mGroup.getId().toString(),getMembers(newmember)).compose(mActivity
                        .<HttpResult<List<GroupUserModel>>>bindToLifeCyclerAndApplySchedulers(false))
                        .subscribe(new BaseObserver2<List<GroupUserModel>>() {
                            @Override
                            protected void onSuccess(List<GroupUserModel> emptyData) {
                                mGroup.setGroupUserModels(emptyData);
                                GroupManger.saveGroup(mGroup);
                                membersAdapter.setData(emptyData);
                                mTitleBar.setTitle(mGroup.getGroupName() + "(" + emptyData.size() + ")");
                                if(memberList.size() + n > 10){
                                    more.setVisibility(View.VISIBLE);
                                }else {
                                    more.setVisibility(View.GONE);
                                }
                                isLoading = true;
                                //if (isClickDelete)
                                mHandlerLoading.sendEmptyMessage(100001);
                                String content = null;
                                for(String str:name){
                                    if(content==null){
                                        content=str;
                                    }else {
                                        content=content+"、"+str;
                                    }
                                }
                                GroupHelper.sendKickedMsg(group,content);
                                //GroupHelper.sendKickedMsg(group,content,newmember);
                            }
                        });

                    break;
                case REQUEST_CODE_ADD_USER:// 添加群成员
                    final String[] newmembers = data.getStringArrayExtra("newmembers");
				/*progressDialog.setMessage(st1);
				progressDialog.show();
				addMembersToGroup(newmembers);*/
				if(newmembers == null || newmembers.length < 1){
                    return;
                }
                    ApiManger2.getApiService().addUser(getMembers(newmembers), mGroup.getId().toString())
                            .compose(this.<HttpResult<List<GroupUserModel>>>bindToLifeCyclerAndApplySchedulers())
                            .subscribe(new BaseObserver2<List<GroupUserModel>>() {
                                @Override
                                protected void onSuccess(List<GroupUserModel> groupModels) {
//                                    GroupManger.saveGroupUser(mGroup.getGroupSn(), groupModels);
                                    mGroup.setGroupUserModels(groupModels);
                                    GroupManger.saveGroup(mGroup);
                                    membersAdapter.setData(groupModels);
                                    mTitleBar.setTitle(mGroup.getGroupName() + "(" + groupModels.size() + ")");
                                    if(memberList.size() + n > 10){
                                        more.setVisibility(View.VISIBLE);
                                    }else {
                                        more.setVisibility(View.GONE);
                                    }
//                                    mTvMemberCountTv.setText(String.format(getString(R.string
//                                            .number_of_group_member), groupModels.size()));
                                }
                            });
                    break;
                case REQUEST_CODE_EXIT: // 退出群
//				progressDialog.setMessage(st2);
//				progressDialog.show();
                    exitGrop();
                    break;
                case REQUEST_CODE_EXIT_DELETE: // 解散群
//				progressDialog.setMessage(st3);
//				progressDialog.show();
                    deleteGrop();
                    break;

                case REQUEST_CODE_EDIT_GROUPNAME: //修改群名称
                    final String returnData = data.getStringExtra("data");
                    if (!TextUtils.isEmpty(returnData)) {
                        ApiManger2.getApiService().modifyGroupInfo(mGroup.getId().toString(), returnData, null)
                                .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers(false))
                                .subscribe(new BaseObserver2<EmptyData>() {
                                    @Override
                                    protected void onSuccess(EmptyData emptyData) {
                                        Toast.makeText(getApplicationContext(), st6, Toast.LENGTH_SHORT).show();
                                        mGroup.setGroupName(returnData);
                                        GroupManger.saveGroup(mGroup);
                                        //mTitleBar.setTitle(mGroup.getGroupName() + "(" + mGroup.getAffiliationsCont() + ")");
                                        mTitleBar.setTitle(mGroup.getGroupName() + "(" + memberList.size() +")");
                                    }
                                });
                    }
                    break;
                case REQUEST_CODE_EDIT_GROUP_DESCRIPTION:
                    final String returnData1 = data.getStringExtra("data");
                    if (!TextUtils.isEmpty(returnData1)) {
                        ApiManger2.getApiService().modifyGroupInfo(mGroup.getId().toString(), null, returnData1)
                                .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers(false))
                                .subscribe(new BaseObserver2<EmptyData>() {
                                    @Override
                                    protected void onSuccess(EmptyData emptyData) {
                                        Toast.makeText(getApplicationContext(), st9, Toast.LENGTH_SHORT).show();
                                        mGroup.setGroupDescription(returnData1);
                                        GroupManger.saveGroup(mGroup);
                                    }
                                });
					/*progressDialog.setMessage(st5);
					progressDialog.show();

					new Thread(new Runnable() {
						public void run() {
							try {
								EMClient.getInstance().groupManager().changeGroupDescription(groupId, returnData1);
								runOnUiThread(new Runnable() {
									public void run() {
										progressDialog.dismiss();
										Toast.makeText(getApplicationContext(), st9, Toast.LENGTH_SHORT).show();
									}
								});
							} catch (HyphenateException e) {
								e.printStackTrace();
								runOnUiThread(new Runnable() {
									public void run() {
										progressDialog.dismiss();
										Toast.makeText(getApplicationContext(), st10, Toast.LENGTH_SHORT).show();
									}
								});
							}
						}
					}).start();*/
                    }
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 点击退出群组按钮
     *
     * @param view
     */
    public void exitGroup(View view) {
        //startActivityForResult(new Intent(this, ExitGroupDialog.class), REQUEST_CODE_EXIT);
        ShowDialog(getString(R.string.exit_group),0);
    }

    /**
     * 点击解散群组按钮
     *
     * @param view
     */
    public void exitDeleteGroup(View view) {
        /*startActivityForResult(new Intent(this, ExitGroupDialog.class).putExtra("deleteToast", getString(R.string
                        .dissolution_group_hint)),
                REQUEST_CODE_EXIT_DELETE);*/
        ShowDialog(getString(R.string.dismiss_group),1);
    }

    /**
     * 清空群聊天记录
     */
    private void clearGroupHistory() {

        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(group.getGroupId(),
                EMConversationType.GroupChat);
        if (conversation != null) {
            conversation.clearAllMessages();
        }
        Toast.makeText(this, R.string.messages_are_empty, Toast.LENGTH_SHORT).show();
    }

    /**
     * 退出群组
     *
     */
    private void exitGrop() {
		/*String st1 = getResources().getString(R.string.Exit_the_group_chat_failure);
		new Thread(new Runnable() {
			public void run() {
				try {
					EMClient.getInstance().groupManager().leaveGroup(groupId);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							setResult(RESULT_OK);
							finish();
							if(ChatActivity.activityInstance != null)
							    ChatActivity.activityInstance.finish();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), getResources().getString(R.string
							.Exit_the_group_chat_failure) + " " + e.getMessage(), Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		}).start();*/
        ApiManger2.getApiService().backGroup(mGroup.getId().toString())
                .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers(false))
                .subscribe(new BaseObserver2<EmptyData>() {
                    @Override
                    protected void onSuccess(EmptyData emptyData) {
                        GroupManger.deleteGroup(mGroup);
                        setResult(RESULT_OK);
                        finish();
                        if (ChatActivity.activityInstance != null)
                            ChatActivity.activityInstance.finish();
                    }
                });
    }

    /**
     * 解散群组
     *
     */
    private void deleteGrop() {
		/*final String st5 = getResources().getString(R.string.Dissolve_group_chat_tofail);
		new Thread(new Runnable() {
			public void run() {
				try {
					EMClient.getInstance().groupManager().destroyGroup(groupId);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							setResult(RESULT_OK);
							finish();
							if(ChatActivity.activityInstance != null)
							    ChatActivity.activityInstance.finish();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), st5 + e.getMessage(), Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		}).start();*/
        ApiManger2.getApiService().deleteGroup(mGroup.getId().toString())
                .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers(false))
                .subscribe(new BaseObserver2<EmptyData>() {
                    @Override
                    protected void onSuccess(EmptyData emptyData) {
                        GroupManger.deleteGroup(mGroup);
                        setResult(RESULT_OK);
                        finish();
                        if (ChatActivity.activityInstance != null)
                            ChatActivity.activityInstance.finish();
                    }
                });
    }

    /**
     * 增加群成员
     *
     * @param newmembers
     */
    private void addMembersToGroup(final String[] newmembers) {
        final String st6 = getResources().getString(R.string.Add_group_members_fail);
        new Thread(new Runnable() {

            public void run() {
                try {
                    // 创建者调用add方法
                    if (EMClient.getInstance().getCurrentUser().equals(group.getOwner())) {
                        EMClient.getInstance().groupManager().addUsersToGroup(groupId, newmembers);
                    } else {
                        // 一般成员调用invite方法
                        EMClient.getInstance().groupManager().inviteUser(groupId, newmembers, null);
                    }
//					updateGroup();
//					refreshMembersAdapter();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mTitleBar.setTitle(group.getGroupName() + "(" + group.getMemberCount() + st);
                            //发送邀请信息
                            GroupHelper.sendInviteMsg(group, newmembers);
                            progressDialog.dismiss();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), st6 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_switch_block_groupmsg: // 屏蔽或取消屏蔽群组
                toggleBlockGroup();
                break;

            case R.id.clear_all_history: // 清空聊天记录
                showDialog2();
                /*String st9 = getResources().getString(R.string.sure_to_empty_this);
                new EaseAlertDialog(GroupDetailsActivity2.this, null, st9, null, new EaseAlertDialog.AlertDialogUser() {

                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (confirmed) {
                            clearGroupHistory();
                        }
                    }
                }, true).show();*/
                break;

            case R.id.rl_change_group_name:
                startActivityForResult(new Intent(this, EditActivity.class).putExtra("data", mGroup.getGroupName()),
                        REQUEST_CODE_EDIT_GROUPNAME);
                break;
            case R.id.rl_change_group_description:
                startActivityForResult(new Intent(this, EditActivity.class).putExtra("data", mGroup
                                .getGroupDescription()).
                                putExtra("title", getString(R.string.change_the_group_description)),
                        REQUEST_CODE_EDIT_GROUP_DESCRIPTION);
                break;
            case R.id.rl_search:
                startActivity(new Intent(this, GroupSearchMessageActivity.class).putExtra("groupId", groupId));

                break;
            case R.id.rl_switch_block_offline_message:
                toggleBlockOfflineMsg();
                break;
            default:
                break;
        }
    }

    private void toggleBlockOfflineMsg() {
        if (EMClient.getInstance().pushManager().getPushConfigs() == null) {
            return;
        }
        createProgressDialog();
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
//		final ArrayList list = (ArrayList) Arrays.asList(groupId);
        final List<String> list = new ArrayList<String>();
        list.add(groupId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (offlinePushSwitch.isSwitchOpen()) {
                        EMClient.getInstance().pushManager().updatePushServiceForGroup(list, false);
                    } else {
                        EMClient.getInstance().pushManager().updatePushServiceForGroup(list, true);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            if (offlinePushSwitch.isSwitchOpen()) {
                                offlinePushSwitch.closeSwitch();
                            } else {
                                offlinePushSwitch.openSwitch();
                            }
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(GroupDetailsActivity2.this, "progress failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private ProgressDialog createProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(GroupDetailsActivity2.this);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        return progressDialog;
    }

    private void toggleBlockGroup() {
        if (switchButton.isSwitchOpen()) {
            EMLog.d(TAG, "change to unblock group msg");
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(GroupDetailsActivity2.this);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.setMessage(getString(R.string.Is_unblock));
            progressDialog.show();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().unblockGroupMessage(groupId);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                switchButton.closeSwitch();
                                progressDialog.dismiss();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), R.string.remove_group_of, Toast.LENGTH_LONG)
                                        .show();
                            }
                        });

                    }
                }
            }).start();
            /*ApiManger2.getApiService()
                    .deleteBlocks(mGroup.getId().toString())
                    .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers(false))
                    .subscribe(new BaseObserver2<EmptyData>() {
                        @Override
                        protected void onSuccess(EmptyData emptyData) {
                            switchButton.closeSwitch();
                        }
                    });*/
        } else {
            String st8 = getResources().getString(R.string.group_is_blocked);
            final String st9 = getResources().getString(R.string.group_of_shielding);
            EMLog.d(TAG, "change to block group msg");
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(GroupDetailsActivity2.this);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            progressDialog.setMessage(st8);
            progressDialog.show();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().blockGroupMessage(groupId);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                switchButton.openSwitch();
                                progressDialog.dismiss();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), st9, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
            }).start();
            /*ApiManger2.getApiService().addBlocks(mGroup.getId().toString())
                    .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers(false))
                    .subscribe(new BaseObserver2<EmptyData>() {
                        @Override
                        protected void onSuccess(EmptyData emptyData) {
                            switchButton.openSwitch();
                        }
                    });*/
        }
    }

    Dialog createMemberMenuDialog() {
        final Dialog dialog = new Dialog(GroupDetailsActivity2.this);
        dialog.setTitle("group");
        dialog.setContentView(R.layout.em_chatroom_member_menu);

        int ids[] = {R.id.menu_item_add_admin,
                R.id.menu_item_rm_admin,
                R.id.menu_item_remove_member,
                R.id.menu_item_add_to_blacklist,
                R.id.menu_item_remove_from_blacklist,
                R.id.menu_item_transfer_owner,
                R.id.menu_item_mute,
                R.id.menu_item_unmute};

        for (int id : ids) {
            LinearLayout linearLayout = (LinearLayout) dialog.findViewById(id);
            linearLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    dialog.dismiss();
					/*loadingPB.setVisibility(View.VISIBLE);

					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								switch (v.getId()) {
									case R.id.menu_item_add_admin:
										EMClient.getInstance().groupManager().addGroupAdmin(groupId, operationUserId);
										break;
									case R.id.menu_item_rm_admin:
										EMClient.getInstance().groupManager().removeGroupAdmin(groupId,
										operationUserId);
										break;
									case R.id.menu_item_remove_member:
										EMClient.getInstance().groupManager().removeUserFromGroup(groupId,
										operationUserId);
										break;
									case R.id.menu_item_add_to_blacklist:
										EMClient.getInstance().groupManager().blockUser(groupId, operationUserId);
										break;
									case R.id.menu_item_remove_from_blacklist:
										EMClient.getInstance().groupManager().unblockUser(groupId, operationUserId);
										break;
									case R.id.menu_item_mute:
										List<String> muteMembers = new ArrayList<String>();
										muteMembers.add(operationUserId);
										EMClient.getInstance().groupManager().muteGroupMembers(groupId, muteMembers,
										20 * 60 * 1000);
										break;
									case R.id.menu_item_unmute:
										List<String> list = new ArrayList<String>();
										list.add(operationUserId);
										EMClient.getInstance().groupManager().unMuteGroupMembers(groupId, list);
										break;
									case R.id.menu_item_transfer_owner:
										EMClient.getInstance().groupManager().changeOwner(groupId, operationUserId);
										break;
									default:
										break;
								}
//								updateGroup();
							} catch (final HyphenateException e) {
								runOnUiThread(new Runnable() {
									              @Override
									              public void run() {
										              Toast.makeText(GroupDetailsActivity2.this, e.getDescription(),
										              Toast.LENGTH_SHORT).show();
									              }
								              }
								);
								e.printStackTrace();

							} finally {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										loadingPB.setVisibility(View.INVISIBLE);
									}
								});
							}
						}
					}).start();*/
                    Observable<HttpResult<List<GroupUserModel>>> observable = null;
                    switch (v.getId()) {
                        case R.id.menu_item_add_admin:
                            break;
                        case R.id.menu_item_rm_admin:
                            break;
                        case R.id.menu_item_remove_member:
                            observable = ApiManger2.getApiService()
                                    .deleteUser(mGroup.getId().toString(), operationUserId);
                            break;
                        case R.id.menu_item_add_to_blacklist:
                            break;
                        case R.id.menu_item_remove_from_blacklist:
                            break;
                        case R.id.menu_item_mute:
                            break;
                        case R.id.menu_item_unmute:
                            break;
                        case R.id.menu_item_transfer_owner:
                            break;
                        default:
                            break;
                    }
                    if (observable != null) {
                        observable.compose(mActivity
                                .<HttpResult<List<GroupUserModel>>>bindToLifeCyclerAndApplySchedulers(false))
                                .subscribe(new BaseObserver2<List<GroupUserModel>>() {
                                    @Override
                                    protected void onSuccess(List<GroupUserModel> emptyData) {
                                        updateGroup();
                                    }
                                });
                    }
                }
            });
        }
        return dialog;
    }

    void setVisibility(Dialog viewGroups, int[] ids, boolean[] visibilities) throws Exception {
        if (ids.length != visibilities.length) {
            throw new Exception("");
        }

        for (int i = 0; i < ids.length; i++) {
            View view = viewGroups.findViewById(ids[i]);
            view.setVisibility(visibilities[i] ? View.VISIBLE : View.GONE);
        }
    }

    int[] ids = {
            R.id.menu_item_transfer_owner,
            R.id.menu_item_add_admin,
            R.id.menu_item_rm_admin,
            R.id.menu_item_remove_member,
            R.id.menu_item_add_to_blacklist,
            R.id.menu_item_remove_from_blacklist,
            R.id.menu_item_mute,
            R.id.menu_item_unmute
    };

    private String toJson(Object obj,int method) {
        // TODO Auto-generated method stub
        if (method==1) {
            //字段是首字母小写，其余单词首字母大写
            Gson gson = new Gson();
            String obj2 = gson.toJson(obj);
            return obj2;
        }else if(method==2){
            // FieldNamingPolicy.LOWER_CASE_WITH_DASHES    全部转换为小写，并用空格或者下划线分隔
            //FieldNamingPolicy.UPPER_CAMEL_CASE    所以单词首字母大写
            Gson gson2=new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            String obj2=gson2.toJson(obj);
            return obj2;
        }
        return "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(State==true){
            State=false;
            shua();
        }
    }

    private int gethed(){
        int size = isCurrentOwner() || mGroup.getGroupOwner() ? 2 : 1;//判断是否是管理员
        if(isCurrentOwner() || mGroup.getGroupOwner()){
            if(memberList.size()<10){
                size=1;
            }
            if(memberList.size()<9){
                size=2;
            }else if(memberList.size()>=10){
                size=0;
            }
        }else {
            if(memberList.size()<10){
                size=1;
            }else {
                size=0;
            }
        }
        return size;
    }
    /**
     * 群组成员gridadapter
     *
     * @author admin_new
     *
     */
    private class GridAdapter extends ArrayAdapter<GroupUserModel> {

        public int getHeadCount() {
            //return isCurrentOwner()||mGroup.getGroupOwner() ? 2 : 1;
            return gethed();
        }

        private int res;

        public GridAdapter(Context context, int textViewResourceId, List<GroupUserModel> objects) {
            super(context, textViewResourceId, objects);
            res = textViewResourceId;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(res, null);
                holder.imageView = (ZQImageViewRoundOval) convertView.findViewById(R.id.iv_avatar);
                holder.imageView.setType(ZQImageViewRoundOval.TYPE_ROUND);holder.imageView.setRoundRadius(10);
                holder.textView = (TextView) convertView.findViewById(R.id.tv_name);
                holder.ownerstar = (ImageView) convertView.findViewById(R.id.owner_star);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final View button =  convertView.findViewById(R.id.button_avatar);

            // add button
            int num=0;
            if(getHeadCount()==0){
                num=0;
            }else {
                num=1;
            }
            if (position == getCount() - getHeadCount()) {
                holder.ownerstar.setVisibility(View.GONE);
                holder.textView.setText(R.string.invite_member);
//                holder.imageView.setImageResource(R.drawable.em_smiley_add_btn);
                GlideApp.with(mActivity).load(R.drawable.add_contract2).placeholder(R.drawable.add_contract2)
                        .into(holder.imageView);
                //if (isCanAddMember(group)) {
                    convertView.setVisibility(View.VISIBLE);
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String st11 = getResources().getString(R.string.Add_a_button_was_clicked);
                            EMLog.d(TAG, st11);
                            // 进入选人页面
                            if (back) {
                                startActivityForResult(
                                        (new Intent(GroupDetailsActivity2.this, GroupPickContactsActivity.class).putExtra
                                                ("groupId", groupId)),
                                        REQUEST_CODE_ADD_USER);
                            }
                        }
                    });
               /* } else {
                    convertView.setVisibility(View.VISIBLE);
                }*/
                return convertView;
            }else if(position == getCount() - num){
                holder.ownerstar.setVisibility(View.GONE);
                holder.textView.setText(R.string.move);
//                holder.imageView.setImageResource(R.drawable.em_smiley_add_btn);
                GlideApp.with(mActivity).load(R.drawable.add_jian).placeholder(R.drawable.add_jian)
                        .into(holder.imageView);
                if (isCanAddMember(group)) {
                    convertView.setVisibility(View.VISIBLE);
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (back) {
                                startActivityForResult((new Intent(GroupDetailsActivity2.this, GroupPickContacts2Activity.class).putExtra
                                                ("groupId", groupId)),
                                        REQUEST_CODE_ADD_USER2);
                            }
                            /*if (!isClickDelete) {
                                isClickDelete = true;
                                final String st11 = getResources().getString(R.string.Add_a_button_was_clicked);
                                EMLog.d(TAG, st11);
                                // 进入踢人页面
                            //    if (isLoading)
                                mHandlerLoading.sendEmptyMessage(100001);
                            }*/
                        }
                    });
                } else {
                    convertView.setVisibility(View.VISIBLE);
                }
                return convertView;
            }else {
                // members
                final GroupUserModel groupUserModel = getItem(position);
                Log.i("qqq",toJson(groupUserModel,1));
                final String username = String.valueOf(groupUserModel.getUserId());
//				EaseUserUtils.setUserNick(username, holder.textView);
//				EaseUserUtils.setUserAvatar(getContext(), username, holder.imageView);
                if(groupUserModel.getGroupOwner()==true){
                    holder.ownerstar.setVisibility(View.VISIBLE);
                }else {
                    holder.ownerstar.setVisibility(View.GONE);
                }
                /*if(username.equals(mGroup.getUserId().toString())){
                    holder.ownerstar.setVisibility(View.VISIBLE);
                }else {
                    holder.ownerstar.setVisibility(View.GONE);
                }*/
                GlideApp.with(mActivity).load(groupUserModel.getPicture()).placeholder(R.drawable.default_avatar2)
                        .into(holder.imageView);
                EaseUser userInfo = EaseUserUtils.getUserInfo(groupUserModel.getUsername());
                if (userInfo != null && !TextUtils.isEmpty(userInfo.getRemark()))
                    holder.textView.setText(userInfo.getRemark());
                else
                    holder.textView.setText(TextUtils.isEmpty(groupUserModel.getNickname())?groupUserModel.getUsername():groupUserModel.getNickname());

                /*LinearLayout id_background = (LinearLayout) convertView.findViewById(R.id.l_bg_id);
                if (groupUserModel.getMute()) {
                    id_background.setBackgroundColor(convertView.getResources().getColor(R.color.gray_normal));
                } else if (false) {
                    id_background.setBackgroundColor(convertView.getResources().getColor(R.color.holo_black));
                } else {
                    id_background.setBackgroundColor(convertView.getResources().getColor(R.color.holo_blue_bright));
                }*/

                button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(true){
                            return;
                        }
                        //不是群主或管理员，无效
                        if (!isCurrentOwner(group) && !isCurrentAdmin(group)) {
                            return;
                        }

                        //点击群主，无效
                       /* if (username.equals(mGroup.getUserId().toString())) {
                            return;*/
                        if (groupUserModel.getGroupOwner()==true) {
                            return;
                            //不是群主点击管理员无效
                        } else if (groupUserModel.getGroupAdmin() && !isCurrentOwner(group)) {
                            return;
                        }

                        operationUserId = username;
                        Dialog dialog = createMemberMenuDialog();
                        dialog.show();

                        boolean[] normalVisibilities = {
                                false,      //R.id.menu_item_transfer_owner,
                                false, /*isCurrentOwner(group) ? true : false,       //R.id.menu_item_add_admin,
                                暂时不支持添加管理员*/
                                false,      //R.id.menu_item_rm_admin,
                                true,       //R.id.menu_item_remove_member,
                                false, /*true,       //R.id.menu_item_add_to_blacklist,*/
                                false,      //R.id.menu_item_remove_from_blacklist,
                                false,       //R.id.menu_item_mute,
                                false,      //R.id.menu_item_unmute
                        };

                        boolean[] blackListVisibilities = {
                                false,      //R.id.menu_item_transfer_owner,
                                false,      //R.id.menu_item_add_admin,
                                false,      //R.id.menu_item_rm_admin,
                                false,      //R.id.menu_item_remove_member,
                                false,      //R.id.menu_item_add_to_blacklist,
                                true,       //R.id.menu_item_remove_from_blacklist,
                                false,      //R.id.menu_item_mute,
                                false,      //R.id.menu_item_unmute
                        };

                        boolean[] muteListVisibilities = {
                                false,      //R.id.menu_item_transfer_owner,
                                isCurrentOwner(group) ? true : false,       //R.id.menu_item_add_admin,
                                false,      //R.id.menu_item_rm_admin,
                                true,       //R.id.menu_item_remove_member,
                                true,       //R.id.menu_item_add_to_blacklist,
                                false,      //R.id.menu_item_remove_from_blacklist,
                                false,      //R.id.menu_item_mute,
                                true,       //R.id.menu_item_unmute
                        };

                        boolean[] adminVisibilities = {
                                true,       //R.id.menu_item_transfer_owner,
                                false,      //R.id.menu_item_add_admin,
                                true,       //R.id.menu_item_rm_admin,
                                false,      //R.id.menu_item_remove_member,
                                false,      //R.id.menu_item_add_to_blacklist,
                                false,      //R.id.menu_item_remove_from_blacklist,
                                false,      //R.id.menu_item_mute,
                                false,      //R.id.menu_item_unmute
                        };

//                        boolean inBlackList = isInBlackList(username);
//                        boolean inMuteList = isInMuteList(username);
                        try {
							/*if (inBlackList) {
								setVisibility(dialog, ids, blackListVisibilities);
							} else if (inMuteList) {
								setVisibility(dialog, ids, muteListVisibilities);
							} else if(isInAdminist(username)){
								setVisibility(dialog, ids, adminVisibilities);
							}else 这里暂时只判断为普通用户*/
                            {
                                setVisibility(dialog, ids, normalVisibilities);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            return convertView;
        }

        @Override
        public int getCount() {
            /*int a = memberList.size() > 8 ? 8 + getHeadCount() : memberList.size() + getHeadCount();
            int b = isCurrentOwner() || mGroup.getAllowInvites() ? 2 : 0;*/
            return memberList.size() + getHeadCount()>10?10:memberList.size() + getHeadCount();
        }

        public void setData(List<GroupUserModel> groupModels) {
            sortGroup(groupModels);
            memberList.clear();
            memberList.addAll(groupModels);
           // memberList2=memberList;
            membersAdapter.notifyDataSetChanged();
        }
    }

    public void back(View view) {
        setResult(RESULT_OK);
//        if(back==true){
//            finish();
//        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        if(back==true){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        EMClient.getInstance().groupManager().removeGroupChangeListener(groupChangeListener);
        super.onDestroy();
        if (disposable != null)
            disposable.dispose();
        instance = null;
    }

    private static class ViewHolder {
        ZQImageViewRoundOval imageView;
        TextView textView;
        ImageView badgeDeleteView;
        ImageView ownerstar;
    }

    private class GroupChangeListener extends EaseGroupListener {

        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason) {
			/*runOnUiThread(new Runnable(){

				@Override
				public void run() {
					ToastUtils.showToast(getApplicationContext(), "onInvitationAccepted");
					memberList = group.getMembers();
					memberList.remove(group.getOwner());
					memberList.removeAll(adminList);
					memberList.removeAll(muteList);
					refreshMembersAdapter();
				}
            });*/
        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            finish();
        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName) {
            finish();
        }

        @Override
        public void onMuteListAdded(String groupId, final List<String> mutes, final long muteExpire) {
            updateGroup();
        }

        @Override
        public void onMuteListRemoved(String groupId, final List<String> mutes) {
            updateGroup();
        }

        @Override
        public void onAdminAdded(String groupId, String administrator) {
            updateGroup();
        }

        @Override
        public void onAdminRemoved(String groupId, String administrator) {
            updateGroup();
        }

        @Override
        public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(GroupDetailsActivity2.this, "onOwnerChanged", Toast.LENGTH_LONG).show();
                }
            });
            updateGroup();
        }

        @Override
        public void onMemberJoined(String groupId, String member) {
            EMLog.d(TAG, "onMemberJoined");
            updateGroup();
        }

        @Override
        public void onMemberExited(String groupId, String member) {
            EMLog.d(TAG, "onMemberExited");
            updateGroup();
        }

        @Override
        public void onAnnouncementChanged(String s, String s1) {

        }

        @Override
        public void onSharedFileAdded(String s, EMMucSharedFile emMucSharedFile) {

        }

        @Override
        public void onSharedFileDeleted(String s, String s1) {

        }
    }

    private void ShowDialog(final String ti, final int i) {
        View view = getLayoutInflater().inflate(R.layout.pop, null);
        final Dialog dialog = new Dialog(this, R.style.TransparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        TextView bt1 = (TextView) view.findViewById(R.id.bt1);
        TextView cancle = (TextView) view.findViewById(R.id.cancle);
        bt1.setText(ti);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                if(i==0){
                    exitGrop();
                }else {
                    deleteGrop();
                }

            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }
    private void showDialog2() {
        View view = getLayoutInflater().inflate(R.layout.pup2, null);
        final Dialog dialog = new Dialog(this, R.style.TransparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        TextView bt1 = (TextView) view.findViewById(R.id.ok);
        TextView ti = (TextView) view.findViewById(R.id.ti);
        TextView cancle = (TextView) view.findViewById(R.id.cancle);
        ti.setText(R.string.ti9);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                clearGroupHistory();
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }


}
