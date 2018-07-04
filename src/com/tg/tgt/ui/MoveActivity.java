package com.tg.tgt.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import com.hyphenate.easeui.GlideApp;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chat.EMPushConfigs;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseGroupListener;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.SpUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseExpandGridView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.easeui.widget.ZQImageViewRoundOval;
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

public class MoveActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "GroupDetailsActivity";
    private static final int REQUEST_CODE_ADD_USER = 0;
    private static final int REQUEST_CODE_ADD_USER2 =7;
    private static final int REQUEST_CODE_EDIT_GROUP_DESCRIPTION = 6;
    private EMPushConfigs pushConfigs;
    private String groupId;
    private EMGroup group;
    private GridAdapter membersAdapter;
    private ProgressDialog progressDialog;
    public static MoveActivity instance;
    String st = "";
    private String operationUserId = "";
    private List<GroupUserModel> memberList = Collections.synchronizedList(new ArrayList<GroupUserModel>());
    GroupChangeListener groupChangeListener;
    private GroupModel mGroup;
    private Map<String, GroupUserModel> mGroupUsers;
    private PublishSubject<List<GroupUserModel>> mSubject;
    private Disposable disposable;
    private EaseTitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupId = getIntent().getStringExtra("groupId");
        group = EMClient.getInstance().groupManager().getGroup(groupId);
        if (group == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_move);
        setTitleBarLeftBack();
        mTitleBar = ((EaseTitleBar) findViewById(R.id.title_bar));
        instance = this;
        st = /*getResources().getString(com.tg.tgt.R.string.people)*/")";

        groupChangeListener = new GroupChangeListener();
        EMClient.getInstance().groupManager().addGroupChangeListener(groupChangeListener);
        mGroup = GroupManger.getGroup(groupId);

        mTitleBar.setTitle(mGroup.getGroupName() + "(" + mGroup.getAffiliationsCont() + st);
        mGroupUsers = GroupManger.getGroupUsers(groupId);
        memberList.addAll(mGroupUsers.values());
        sortGroup(memberList);
        membersAdapter = new GridAdapter(this, R.layout.em_grid_owner, memberList);
        EaseExpandGridView userGridview = (EaseExpandGridView) findViewById(R.id.gridview);
        userGridview.setAdapter(membersAdapter);
        // 保证每次进详情看到的都是最新的group
        //		updateGroup();
        updateGroup();
    }

    private void updateGroup() {
        if (mSubject == null) {
            mSubject = PublishSubject.create();
            mSubject.doOnSubscribe(new Consumer<Disposable>() {
                @Override
                public void accept(@NonNull Disposable disposable) throws Exception {
                    MoveActivity.this.disposable = disposable;
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
                                        public List<GroupUserModel> apply(@NonNull List<GroupUserModel> strings) throws Exception {
                                            if (pushConfigs == null) {
                                                EMClient.getInstance().pushManager().getPushConfigsFromServer();
                                            }
                                            group = EMClient.getInstance().groupManager().getGroupFromServer(groupId);
                                            mGroup = GroupManger.fetchGroup(groupId);
                                            List<GroupUserModel> groupUserModels = mGroup.getGroupUserModels();
                                            strings.clear();
                                            strings.addAll(groupUserModels);
                                            sortGroup(strings);
                                            return strings;
                                        }
                                    })
                                    .compose(mActivity.<List<GroupUserModel>>bindToLifeCyclerAndApplySchedulers(null))
                                    .doOnSubscribe(new Consumer<Disposable>() {
                                        @Override
                                        public void accept(@NonNull Disposable disposable) throws Exception {
                                        }
                                    })
                                    .subscribe(new Consumer<List<GroupUserModel>>() {
                                        @Override
                                        public void accept(@NonNull List<GroupUserModel> strings) throws
                                                Exception {
                                            mGroupUsers = GroupManger.getGroupUsers(groupId);
                                            membersAdapter.notifyDataSetChanged();
                                            mTitleBar.setTitle(mGroup.getGroupName() + "(" + mGroup.getAffiliationsCont() + ")");
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(@NonNull Throwable throwable) throws Exception {
                                            throwable.printStackTrace();
                                            EaseAlertDialog dialog = new EaseAlertDialog(mActivity, HttpHelper.handleException
                                                    (throwable), getString(R.string.give_up), getString(R.string.retry), new EaseAlertDialog.AlertDialogUser() {
                                                @Override
                                                public void onResult(boolean confirmed, Bundle bundle) {
                                                    if (confirmed) {
                                                        updateGroup();
                                                    } else {
                                                        onBackPressed();
                                                    }
                                                }
                                            });
                                            dialog.show();
                                        }
                                    });
                        }
                    });
        }
        mSubject.onNext(memberList);
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
                if (!lhsOwner & rhsOwner) {
                    return 1;
                } else if (lhsOwner & !rhsOwner) {
                    return -1;
                } else {
                    return (int) (lhsId - rhsId);
                }
            }
        });
    }

    boolean isCurrentOwner(EMGroup group) {
        return mGroup != null && App.getMyUid().equals(String.valueOf(mGroup.getUserId()));
    }

    boolean isCurrentOwner() {
        return mGroup != null && App.getMyUid().equals(String.valueOf(mGroup.getUserId()));
    }

    boolean isCurrentAdmin(EMGroup group) {
        return mGroupUsers.get(SpUtils.get(mContext, Constant.USERNAME, "")).getGroupAdmin();
    }

    boolean isCanAddMember(EMGroup group) {
        return isCurrentOwner()|| mGroup != null && mGroup.getAllowInvites();
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
        final String st9 = getResources().getString(R.string.Modify_the_group_description_successful);
        if (resultCode == RESULT_OK) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(MoveActivity.this);
                progressDialog.setMessage(st1);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            switch (requestCode) {
                case REQUEST_CODE_ADD_USER2://踢群成员
                final String[] newmember = data.getStringArrayExtra("newmembers");
                if(newmember == null || newmember.length < 1){
                    return;
                }
                ApiManger2.getApiService().deleteUser(mGroup.getId().toString(),getMembers(newmember)).compose(mActivity
                        .<HttpResult<List<GroupUserModel>>>bindToLifeCyclerAndApplySchedulers(false))
                        .subscribe(new BaseObserver2<List<GroupUserModel>>() {
                            @Override
                            protected void onSuccess(List<GroupUserModel> emptyData) {
                                mGroup.setGroupUserModels(emptyData);
                                GroupManger.saveGroup(mGroup);
                                membersAdapter.setData(emptyData);
                                mTitleBar.setTitle(mGroup.getGroupName() + "(" + emptyData.size() + ")");
                            }
                        });
                break;
                case REQUEST_CODE_ADD_USER:// 添加群成员
                    final String[] newmembers = data.getStringArrayExtra("newmembers");
                    if(newmembers == null || newmembers.length < 1){
                        return;
                    }
                    ApiManger2.getApiService().addUser(getMembers(newmembers), mGroup.getId().toString())
                            .compose(this.<HttpResult<List<GroupUserModel>>>bindToLifeCyclerAndApplySchedulers())
                            .subscribe(new BaseObserver2<List<GroupUserModel>>() {
                                @Override
                                protected void onSuccess(List<GroupUserModel> groupModels) {
                                    mGroup.setGroupUserModels(groupModels);
                                    GroupManger.saveGroup(mGroup);
                                    membersAdapter.setData(groupModels);
                                    mTitleBar.setTitle(mGroup.getGroupName() + "(" + groupModels.size() + ")");
                                }
                            });
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
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
    }

    Dialog createMemberMenuDialog() {
        final Dialog dialog = new Dialog(MoveActivity.this);
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
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    dialog.dismiss();
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


    /**
     * 群组成员gridadapter
     *
     * @author admin_new
     *
     */
    private class GridAdapter extends ArrayAdapter<GroupUserModel> {

        public int getHeadCount() {
            return isCurrentOwner()||mGroup.getAllowInvites() ? 2 : 0;
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
                holder.imageView.setType(ZQImageViewRoundOval.TYPE_ROUND);holder.imageView.setRoundRadius(20);
                holder.textView = (TextView) convertView.findViewById(R.id.tv_name);
                holder.ownerstar = (ImageView) convertView.findViewById(R.id.owner_star);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final View button =  convertView.findViewById(R.id.button_avatar);

            // add button
            Log.i("qqq",position+"+"+getCount()+"+"+getHeadCount());
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
                if (isCanAddMember(group)) {
                    convertView.setVisibility(View.VISIBLE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String st11 = getResources().getString(R.string.Add_a_button_was_clicked);
                            EMLog.d(TAG, st11);
                            // 进入选人页面
                            startActivityForResult(
                                    (new Intent(MoveActivity.this, GroupPickContactsActivity.class).putExtra
                                            ("groupId", groupId)),
                                    REQUEST_CODE_ADD_USER);
                        }
                    });
                } else {
                    convertView.setVisibility(View.VISIBLE);
                }
                return convertView;
            }else if(position == getCount() - num){
                holder.ownerstar.setVisibility(View.GONE);
                holder.textView.setText(R.string.move);
//                holder.imageView.setImageResource(R.drawable.em_smiley_add_btn);
                GlideApp.with(mActivity).load(R.drawable.add_jian).placeholder(R.drawable.add_jian)
                        .into(holder.imageView);
                if (isCanAddMember(group)) {
                    convertView.setVisibility(View.VISIBLE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String st11 = getResources().getString(R.string.Add_a_button_was_clicked);
                            EMLog.d(TAG, st11);
                            // 进入踢人页面
                            startActivityForResult(
                                    (new Intent(MoveActivity.this, GroupPickContacts2Activity.class).putExtra
                                            ("groupId", groupId)),
                                    REQUEST_CODE_ADD_USER2);
                        }
                    });
                } else {
                    convertView.setVisibility(View.VISIBLE);
                }
                return convertView;
            }else {
                final GroupUserModel groupUserModel = getItem(position);
                final String username = String.valueOf(groupUserModel.getUserId());
                if(username.equals(mGroup.getUserId().toString())){
                    holder.ownerstar.setVisibility(View.VISIBLE);
                }else {
                    holder.ownerstar.setVisibility(View.GONE);
                }
                GlideApp.with(mActivity).load(groupUserModel.getPicture()).placeholder(R.drawable.default_avatar2)
                        .into(holder.imageView);
                EaseUser userInfo = EaseUserUtils.getUserInfo(groupUserModel.getUsername());
                if (userInfo != null && !TextUtils.isEmpty(userInfo.getRemark()))
                    holder.textView.setText(userInfo.getRemark());
                else
                    holder.textView.setText(TextUtils.isEmpty(groupUserModel.getNickname())?groupUserModel.getUsername():groupUserModel.getNickname());
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //不是群主或管理员，无效
                        if (!isCurrentOwner(group) && !isCurrentAdmin(group)) {
                            return;
                        }
                        //点击群主，无效
                        if (username.equals(mGroup.getUserId().toString())) {
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
                        try {
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
            return memberList.size() + getHeadCount();
        }
        public void setData(List<GroupUserModel> groupModels) {
            sortGroup(groupModels);
            memberList.clear();
            memberList.addAll(groupModels);
            membersAdapter.notifyDataSetChanged();
        }
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        EMClient.getInstance().groupManager().removeGroupChangeListener(groupChangeListener);
        super.onDestroy();
        instance = null;
        if (disposable != null)
            disposable.dispose();
    }

    private static class ViewHolder {
        ZQImageViewRoundOval imageView;
        TextView textView;
        ImageView ownerstar;
    }

    private class GroupChangeListener extends EaseGroupListener {
        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason) {
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
                    Toast.makeText(MoveActivity.this, "onOwnerChanged", Toast.LENGTH_LONG).show();
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
}
