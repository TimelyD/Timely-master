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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.hyphenate.easeui.EaseApp;
import com.hyphenate.easeui.model.KeyBean;
import com.hyphenate.easeui.utils.photo.MediaBean;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseAlertDialog.AlertDialogUser;
import com.tg.tgt.helper.SecurityDialog;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.model.IsCodeResult;
import com.tg.tgt.utils.CodeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ForwardMessageActivity extends PickContactNoCheckboxActivity {
	private EaseUser selectUser;
	private String forward_msg_id;

	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		forward_msg_id = getIntent().getStringExtra("forward_msg_id");
	}
    private HashMap<String,List<KeyBean>> map=new HashMap<>();
    private String ID;
    private void getRecvChatKey(String toChatUsername, final IsCodeResult isCodeResult){//单聊
        ID=toChatUsername;
        ApiManger2.getApiService()
                .getRecvChatKey(ID)
                .compose((this).<HttpResult<KeyBean>>bindToLifeCyclerAndApplySchedulers(null))
                .subscribe(new BaseObserver2<KeyBean>() {
                    @Override
                    protected void onSuccess(KeyBean bean) {
                        EaseApp.receiver_pub=bean;
                        List<KeyBean> list = new ArrayList<KeyBean>();list.add(bean);
                        if(map==null){
                            map=new HashMap<>();
                        }
                        map.put(ID,list);
                        String string = CodeUtils.toJson(map, 1);
                        EaseApp.sf.edit().putString(EaseApp.map_receiver,string).commit();
                        toChat(isCodeResult);
                    }
                });
    }
	@Override
	protected void onListItemClick(int position) {
		selectUser = contactAdapter.getItem(position);
		new EaseAlertDialog(this, null, getString(R.string.confirm_forward_to, selectUser.getNick()), null, new AlertDialogUser() {
            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {
                    if (selectUser == null)
                        return;
                    try {
                        ChatActivity.activityInstance.finish();
                    } catch (Exception e) {
                    }
                    final IsCodeResult isCodeResult = CodeUtils.getIsCodeResult(ForwardMessageActivity.this, selectUser.getUsername());

                    if(isCodeResult.getIscode() == 1){
                        SecurityDialog.show(ForwardMessageActivity.this,mContext.getString(R.string.security_title),new SecurityDialog.OnSecurityListener() {
                            @Override
                            public void onPass() {
                                String z = EaseApp.sf.getString(EaseApp.map_receiver, null);//得到总map
                                map = CodeUtils.toMap(z);
                                if(z==null||map.get(selectUser.getUsername())==null){
                                    getRecvChatKey(selectUser.getUsername(),isCodeResult);
                                }else {
                                    toChat(isCodeResult);
                                }
                            }
                        });
                    }else {
                        String z = EaseApp.sf.getString(EaseApp.map_receiver, null);//得到总map
                        map = CodeUtils.toMap(z);
                        if(z==null||map.get(selectUser.getUsername())==null){
                            getRecvChatKey(selectUser.getUsername(),isCodeResult);
                        }else {
                            toChat(isCodeResult);
                        }
                    }

                }
            }
        }, true).show();
	}

    private void toChat(IsCodeResult isCodeResult) {
        Intent intent = new Intent(ForwardMessageActivity.this, ChatActivity.class);
        // it is single chat
        intent.putExtra("userId", selectUser.getUsername());
        intent.putExtra("forward_msg_id", forward_msg_id);
        intent.putExtra(Constant.ISCODE, isCodeResult);
        startActivity(intent);
        finish();
        /*mOnPhotoMenuListener.onPhotoSend(mAdapter.getSelectlist().toArray(new MediaBean[mAdapter
                .getSelectlist().size()]));*/
    }

}
