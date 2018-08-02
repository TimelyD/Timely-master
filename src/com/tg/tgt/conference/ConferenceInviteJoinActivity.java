package com.tg.tgt.conference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseApp;
import com.hyphenate.easeui.GlideApp;
import com.hyphenate.easeui.adapter.EaseContactAdapter;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.ToastUtils;
import com.hyphenate.easeui.widget.EaseSidebar;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.helper.GroupManger;
import com.tg.tgt.http.model2.GroupUserModel;
import com.tg.tgt.ui.BaseActivity;
import com.tg.tgt.ui.GroupPickContactsActivity;
import com.tg.tgt.utils.CodeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ConferenceInviteJoinActivity extends BaseActivity {
    private PickContactAdapter contactAdapter;
    private List<EaseUser> mAlluserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference_invite_join);
        setTitleBarLeftBack();

        String groupId = getIntent().getStringExtra(Constant.GROUP_ID);
        List<GroupUserModel> allUser = GroupManger.getGroup(groupId).getGroupUserModels();

        List<String> memberList = EMClient.getInstance().conferenceManager().getConferenceMemberList();

        //需要展示的
        mAlluserList = new ArrayList<EaseUser>();
        for (GroupUserModel model : allUser) {
        	if(!memberList.contains(model.getUsername())
                    && !ConferenceActivity.allUsers.contains(model.getUsername())
                    && !model.getUsername().equals(App.getMyUid())) {
                EaseUser easeUser = new EaseUser(model.getUsername());
                easeUser.setNickname(model.getNickname());
                easeUser.setAvatar(model.getPicture());
                easeUser.setInitialLetter(model.getInitialLetter());
                mAlluserList.add(easeUser);
            }
        }

        // sort the list
        Collections.sort(mAlluserList, new Comparator<EaseUser>() {
            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if(lhs.getInitialLetter().equals(rhs.getInitialLetter())){
                    return lhs.getNickname().compareTo(rhs.getNickname());
                }else{
                    if("#".equals(lhs.getInitialLetter())){
                        return 1;
                    }else if("#".equals(rhs.getInitialLetter())){
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }

            }
        });
        ListView listView = (ListView) findViewById(R.id.list);
        contactAdapter = new PickContactAdapter(this, R.layout.em_row_contact_with_checkbox, mAlluserList);
        listView.setAdapter(contactAdapter);
        ((EaseSidebar) findViewById(R.id.sidebar)).setListView(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                checkBox.toggle();

            }
        });
    }
    /**
     * adapter
     */
    private class PickContactAdapter extends EaseContactAdapter {

        private boolean[] isCheckedArray;

        public PickContactAdapter(Context context, int resource, List<EaseUser> users) {
            super(context, resource, users);
            isCheckedArray = new boolean[users.size()];
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            final String username = getItem(position).getUsername();

            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            ImageView avatarView = (ImageView) view.findViewById(R.id.avatar);
            TextView nameView = (TextView) view.findViewById(R.id.name);
            EaseUser easeUser = mAlluserList.get(position);

            GlideApp.with(mActivity).load(easeUser.getAvatar()).into(avatarView);
            nameView.setText(easeUser.getNickname());
            if(ConferenceActivity.allUsers.size()+getToBeAddMembers().size()>8){

                if(!checkBox.isChecked())
                    checkBox.setEnabled(false);
                else
                    checkBox.setEnabled(true);
            }

            if (checkBox != null) {

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked && ConferenceActivity.allUsers.size()+getToBeAddMembers().size()>Constant.MEMBERS_SIZE){
                            checkBox.setChecked(false);
                            Toast.makeText(mContext, "最多只能有9个人同时进行会议", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        isCheckedArray[position] = isChecked;

                    }
                });
                    checkBox.setChecked(isCheckedArray[position]);
            }

            return view;
        }
    }

    /**
     * get selected members
     *
     * @return
     */
    private List<String> getToBeAddMembers() {
        List<String> members = new ArrayList<String>();EaseApp.nick.clear();
        EaseApp.mAlluserList.clear();EaseApp.mAlluserList.addAll(mAlluserList);
        int length = contactAdapter.isCheckedArray.length;
        for (int i = 0; i < length; i++) {
            if (contactAdapter.isCheckedArray[i]) {
                members.add(mAlluserList.get(i).getUsername());
                EaseApp.nick.add(mAlluserList.get(i).getNick());
            }
        }

        return members;
    }
    public void save(View view) {
        List<String> var = getToBeAddMembers();
        if(var.size()==0){
            ToastUtils.showToast(getApplicationContext(), R.string.select_at_least_one);
            return;
        }
        setResult(RESULT_OK, new Intent().putExtra(Constant.MEMBERS, var.toArray(new String[var.size()])));
        finish();
    }
}
