package com.tg.tgt.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.ToastUtils;
import com.hyphenate.easeui.widget.EaseSidebar;
import com.tg.tgt.R;
import com.tg.tgt.adapter.EaseContact2Adapter;
import com.tg.tgt.helper.GroupManger;
import com.tg.tgt.http.model2.GroupModel;
import com.tg.tgt.http.model2.GroupUserModel;

import java.util.ArrayList;
import java.util.List;

public class GroupPickContacts2Activity extends BaseActivity {
    /** if this is a new group */
    protected boolean isCreatingNewGroup;
    private PickContactAdapter contactAdapter;
    /** members already in the group */
    private List<String> existMembers;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_pick_contacts2);
        setTitleBarLeftBack();
        groupId = getIntent().getStringExtra("groupId");
        if (groupId == null) {// create new group
            isCreatingNewGroup = true;
        } else {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
            GroupModel group1 = GroupManger.getGroup(groupId);
            List<GroupUserModel> groupUserModels = group1.getGroupUserModels();
            Log.i("dcz",groupUserModels.size()+"");
            if(groupUserModels != null){
                existMembers = new ArrayList<>();
                for (GroupUserModel model : groupUserModels) {
                    existMembers.add(String.valueOf(model.getUsername()));
                }
            }
        }
        if(existMembers == null)
            existMembers = new ArrayList<String>();
        List<GroupUserModel> list = GroupDetailsActivity2.memberList2;
        if(list.get(0).getGroupOwner()){
            list.remove(list.get(0));
        }
       /* List<GroupUserModel> list = GroupDetailsActivity2.memberList;
        for(int i=0;i<list.size();i++){
            if(i!=0){
                EaseUser a=new EaseUser(list.get(i).getUsername());
                a.setNickname(list.get(i).getNickname());
                a.setChatid(list.get(i).getUserId()+"");
                a.setAvatar(list.get(i).getPicture());
                alluserList.add(a);
                Log.i("zzz2",toJson(list.get(i),1));
                Log.i("zzz",toJson(a,1));
            }
        }*/
       /* for (EaseUser user : DemoHelper.getInstance().getContactList().values()) {
            if (!user.getUsername().equals(Constant.NEW_FRIENDS_USERNAME)
                    & !user.getUsername().equals(Constant.GROUP_USERNAME)
                    & !user.getUsername().equals(Constant.CHAT_ROOM)
                    & !user.getUsername().equals(Constant.CHAT_ROBOT)
                    &existMembers.contains(user.getUsername())
                //添加判断，如果这个用户已存在则不显示
					*//*& !existMembers.contains(user.getUsername())*//*)
                alluserList.add(user);
        }
        Collections.sort(alluserList, new Comparator<EaseUser>() {

            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if(lhs.getInitialLetter().equals(rhs.getInitialLetter())){
                    return lhs.getNick().compareTo(rhs.getNick());
                }else{
                    if("#".equals(lhs.getInitialLetter())){
                        return 1;
                    }else if("#".equals(rhs.getInitialLetter())){
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }

            }
        });*/

        ListView listView = (ListView) findViewById(R.id.list);
        contactAdapter = new PickContactAdapter(this, R.layout.em_row_contact_with_checkbox,list);
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
     * save selected members
     *
     * @param v
     */
    public void save(View v) {
        final List<String> var = getToBeAddMembers();
        if(var.size()==0){
            ToastUtils.showToast(getApplicationContext(), R.string.select_at_least_one);
            return;
        }
        setResult(RESULT_OK, new Intent().putExtra("newmembers", var.toArray(new String[var.size()])));
        finish();
    }

    /**
     * get selected members
     *
     * @return
     */
    private List<String> getToBeAddMembers() {
        List<String> members = new ArrayList<String>();
        Log.i("size",contactAdapter.isCheckedArray.length+"");
        int length = contactAdapter.isCheckedArray.length;
        for (int i = 0; i < length; i++) {
            String username = contactAdapter.getItem(i).getUsername();
            if (contactAdapter.isCheckedArray[i]) {
                // 这里不适用环信id
                members.add(EaseUserUtils.getUserInfo(username).getChatid());
            }
        }
        return members;
    }
    /**
     * adapter
     */
    private class PickContactAdapter extends EaseContact2Adapter {
        private boolean[] isCheckedArray;
        public PickContactAdapter(Context context, int resource, List<GroupUserModel> users) {
            super(context, resource, users);
            isCheckedArray = new boolean[users.size()];
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            if (checkBox != null) {
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        isCheckedArray[position] = isChecked;
                    }
                });
            }
            return view;
        }
    }
    public void back(View view){
        finish();
    }
}
