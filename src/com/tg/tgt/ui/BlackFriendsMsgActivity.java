package com.tg.tgt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.classic.common.MultipleStatusView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.exceptions.HyphenateException;
import com.tg.tgt.R;
import com.tg.tgt.adapter.BlackFriendsAdapter;
import com.tg.tgt.adapter.NewFriendsMsgAdapter;
import com.tg.tgt.db.BlackMessageDao;
import com.tg.tgt.db.InviteMessgeDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by DELL on 2018/7/30.
 */

public class BlackFriendsMsgActivity extends BaseActivity {

    private MultipleStatusView mMultipleStatusView;
    private List<EaseUser> blackList;
    private BlackFriendsAdapter blackFriendsAdapter;

    private EaseUser unEaseUser;

    private ListView listView;

    private static final int UNBLACKSET = 88;
    public static final int UNBLACKSETSUCCESSFUL = 888;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.tg.tgt.R.layout.em_activity_black_friends_msg);

        setTitleBarLeftBack();

        mMultipleStatusView = (MultipleStatusView) findViewById(R.id.multiple_status_layout);
        listView = (ListView) findViewById(com.tg.tgt.R.id.list);
        BlackMessageDao dao = new BlackMessageDao(this);
        blackList = dao.getBlackList();
        if(blackList==null || blackList.size()==0){
            mMultipleStatusView.showEmpty();
        }
        blackFriendsAdapter = new BlackFriendsAdapter(blackList,BlackFriendsMsgActivity.this);
        listView.setAdapter(blackFriendsAdapter);
        blackFriendsAdapter.notifyDataSetChanged();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                unEaseUser = blackList.get(position);
                startActivityForResult(new Intent(BlackFriendsMsgActivity.this,ContextUnBlackActivity.class),UNBLACKSET);
                return true;
            }
        });
        Log.e("Tag",blackList.size()+"====");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UNBLACKSET){
            if (resultCode == UNBLACKSETSUCCESSFUL){
                try {
                if (unEaseUser != null) {
                    EMClient.getInstance().contactManager().removeUserFromBlackList(unEaseUser.getUsername());
                    BlackMessageDao blackMessageDao = new BlackMessageDao(BlackFriendsMsgActivity.this);
                    blackMessageDao.deleteBlack(unEaseUser.getUsername());
                    blackList.remove(unEaseUser);
                    blackFriendsAdapter.notifyDataSetChanged();
                    Toast.makeText(BlackFriendsMsgActivity.this, R.string.set_successful, Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(BlackFriendsMsgActivity.this, R.string.set_fail, Toast.LENGTH_LONG).show();
                }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
