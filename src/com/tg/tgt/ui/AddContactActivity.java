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

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.ImageUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.tg.tgt.DemoHelper;
import com.tg.tgt.R;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.model2.UserRelationInfoModel;
import com.tg.tgt.utils.CodeUtils;
import com.tg.tgt.utils.ToastUtils;
import com.tg.tgt.widget.dialog.CommonDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class AddContactActivity extends BaseActivity implements TextView.OnEditorActionListener {
    private EditText editText;
    //	private RelativeLayout searchedUserLayout;
//	private TextView nameText;
//	private Button searchBtn;
//	private String toAddUsername;
//	private ProgressDialog progressDialog;
    private ListView mListView;
    private FriendsAdapter mAdapter;
    private ImageView x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_add_contact);
//        TextView mTextView = (TextView) findViewById(R.id.add_list_friends);
        EaseTitleBar titleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        setTitleBarLeftBack();
        editText = (EditText) findViewById(R.id.edit_note);
        x = (ImageView) findViewById(R.id.x);
        //String strUserName = getResources().getString(R.string.user_name);
        String strUserName = getResources().getString(R.string.ti5);
        editText.setHint(strUserName);
        editText.setOnEditorActionListener(this);
//		searchedUserLayout = (RelativeLayout) findViewById(R.id.ll_user);
//		nameText = (TextView) findViewById(R.id.name);
//		searchBtn = (Button) findViewById(R.id.search);

        mListView = (ListView) findViewById(R.id.friends_lv);
        mAdapter = new FriendsAdapter();
        mListView.setAdapter(mAdapter);

        RxTextView.textChanges(editText)
                .debounce(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(@NonNull CharSequence charSequence) throws Exception {
                        String name = charSequence.toString();
                        if (TextUtils.isEmpty(name)) {
                            x.setVisibility(View.GONE);
                            return;
                        }
                        x.setVisibility(View.VISIBLE);
                        search(name, false);
                    }
                });
        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
    }

    private List<UserRelationInfoModel> mDatas = new ArrayList<>();

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            // 当按了搜索之后关闭软键盘
            ((InputMethodManager) editText.getContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    mActivity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            searchContact(editText);
            return true;
        }
        return false;
    }

    private class FriendsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(AddContactActivity.this).inflate(R.layout.item_friend, null);
                holder.avatar = (ImageView) convertView.findViewById(R.id.avatar_iv);
                holder.name = (TextView) convertView.findViewById(R.id.name_tv);
                holder.add = (Button) convertView.findViewById(R.id.add_btn);
                holder.id = (TextView) convertView.findViewById(R.id.ID);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final UserRelationInfoModel bean = mDatas.get(position);
//			Glide.with(AddContactActivity.this).load(bean.getCover()).placeholder(R.drawable.default_avatar).error(R
// .drawable.default_avatar).into(holder.avatar);
            ImageUtils.show(AddContactActivity.this, bean.getPicture(), R.drawable.default_avatar, holder.avatar);
            holder.name.setText(bean.getNickname() == null ? bean.getEmail() : bean.getNickname());
            holder.id.setText("ID:"+bean.getSn());
            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CodeUtils.addContact(mActivity, bean.getId(), bean.getUsername());
//                    saySomething(String.valueOf(bean.getId()));
//addContact(bean.getChatid());
                }
            });

            return convertView;
        }

        class ViewHolder {
            ImageView avatar;
            TextView name;
            Button add;
            TextView id;
        }
    }

    /**
     * search contact
     * @param v
     */
    public void searchContact(View v) {
        final String name = editText.getText().toString();
//		String saveText = searchBtn.getText().toString();
//
//		if (getString(R.string.button_search).equals(saveText)) {
//			toAddUsername = name;
//			if(TextUtils.isEmpty(name)) {
//				new EaseAlertDialog(this, R.string.Please_enter_a_username).show();
//				return;
//			}
//
//			// TODO you can search the user from your app server here.
//
//			//show the userame and add button if user exist
//			searchedUserLayout.setVisibility(View.VISIBLE);
//			nameText.setText(toAddUsername);
//
//		}

        if (TextUtils.isEmpty(name)) {
            new EaseAlertDialog(this, R.string.Please_enter_a_username).show();
            return;
        }

        search(name, true);
    }

    private void search(String name, final boolean showDialog) {
        /*ApiManger.getApiService()
				.search(name)
				.compose(RxUtils.<SearchResult>applySchedulers())
				.subscribe(new BaseObserver<SearchResult>(this, showDialog) {
					@Override
					protected void onSuccess(SearchResult result) {
						mDatas.clear();
						mDatas.addAll(result.getSearch());
						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void onFaild(SearchResult result) {
						super.onFaild(result);
						if(result.getTotal() == 0){
							//空
							mDatas.clear();
							mAdapter.notifyDataSetChanged();
						}
					}
				});*/
        ApiManger2.getApiService()
                .find(name)
                .compose(this.<HttpResult<List<UserRelationInfoModel>>>bindToLifeCyclerAndApplySchedulers(showDialog
						? getString(R.string.loading) : null))
                .subscribe(new BaseObserver2<List<UserRelationInfoModel>>(showDialog) {
                    @Override
                    protected void onSuccess(List<UserRelationInfoModel> models) {
                        if(models == null && showDialog){
                            ToastUtils.showToast(getApplicationContext(), R.string.search_nobody);
                        }
                        mDatas.clear();
                        if (models!=null&&models.size() > 0) {
                            mDatas.addAll(models);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }


    private void saySomething(final String chatid) {
        if (EMClient.getInstance().getCurrentUser().equals(chatid)) {
            new EaseAlertDialog(this, R.string.not_add_myself).show();
            return;
        }

        if (DemoHelper.getInstance().getContactList().containsKey(chatid)) {
            //let the user know the contact already in your contact list
            if (EMClient.getInstance().contactManager().getBlackListUsernames().contains(chatid)) {
                new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
                return;
            }
            new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
            return;
        }

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_saysomething, null);
        final EditText editText = (EditText) view.findViewById(R.id.say_et);
        CommonDialog.show(this, getString(R.string.say_something), view, new CommonDialog.OnConfirmListener() {
            @Override
            public void onConfirm(AlertDialog dialog) {
                String say = editText.getText().toString().trim();
                if (TextUtils.isEmpty(say))
                    say = getResources().getString(R.string.Add_a_friend);
                addContact(chatid, say);
            }
        });
    }


    /**
     *  add contact
     * @param chatid
     */
    public void addContact(final String chatid, final String say) {

//		progressDialog = new ProgressDialog(this);
//		String stri = getResources().getString(R.string.Is_sending_a_request);
//		progressDialog.setMessage(stri);
//		progressDialog.setCanceledOnTouchOutside(false);
//		progressDialog.show();

		/*showProgress(R.string.Is_sending_a_request);

		new Thread(new Runnable() {
			public void run() {
				
				try {
					//demo use a hardcode reason here, you need let user to input if you like

					//添加好友格式:原因-昵称-图片
					// reason + "-" + name + "-" + avatar

					String nickname = SharedPreStorageMgr.getIntance().getStringValue(AddContactActivity.this,
							Constant.NICKNAME);
					nickname = TextUtils.isEmpty(nickname) ? SharedPreStorageMgr.getIntance().getStringValue
					(AddContactActivity.this, Constant.USERNAME) : nickname;
					EMClient.getInstance().contactManager().addContact(chatid, String.format("%s-%s-%s", say,
							nickname, SharedPreStorageMgr.getIntance().getStringValue(AddContactActivity.this,
									Constant.HEADIMAGE)));
					runOnUiThread(new Runnable() {
						public void run() {
//							progressDialog.dismiss();
							dismissProgress();
							String s1 = getResources().getString(R.string.send_successful);
							Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
//							progressDialog.dismiss();
							dismissProgress();
							String s2 = getResources().getString(R.string.Request_add_buddy_failure);
							Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		}).start();*/
        //添加好友格式:原因-昵称-图片
        // reason + "-" + name + "-" + avatar

//        String nickname = SharedPreStorageMgr.getIntance().getStringValue(AddContactActivity.this,
//                Constant.NICKNAME);
//        nickname = TextUtils.isEmpty(nickname) ? SharedPreStorageMgr.getIntance().getStringValue
//                (AddContactActivity.this, Constant.USERNAME) : nickname;
//        String avatar = SharedPreStorageMgr.getIntance().getStringValue(AddContactActivity.this,
//                Constant.HEADIMAGE);
//        HashMap<String,String> stringHashMap = new HashMap<>();
//        //通过json带过去
//        stringHashMap.put(Constant.INVITE_REASON, say);
//        stringHashMap.put(Constant.INVITE_NICKNAME, nickname);
//        stringHashMap.put(Constant.INVITE_AVATAR, avatar);

        ApiManger2.getApiService()
                .applyFriend(chatid, /*new Gson().toJson(stringHashMap)*/say)
                .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<EmptyData>() {
                    @Override
                    protected void onSuccess(EmptyData emptyData) {
                        String s1 = getResources().getString(R.string.send_successful);
                        Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void back(View v) {
        finish();
    }
}
