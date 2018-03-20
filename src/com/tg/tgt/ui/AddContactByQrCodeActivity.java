package com.tg.tgt.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.ImageUtils;
import com.hyphenate.easeui.widget.CircleImageView;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.tg.tgt.Constant;
import com.tg.tgt.DemoHelper;
import com.tg.tgt.R;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.model2.UserRelationInfoModel;
import com.tg.tgt.utils.CodeUtils;
import com.tg.tgt.widget.dialog.CommonDialog;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 *
 * @author yiyang
 */
public class AddContactByQrCodeActivity extends BaseActivity {
    private com.hyphenate.easeui.widget.EaseTitleBar titlebar;
    private android.widget.TextView tvnick;
    private android.widget.TextView tvid;
    private android.widget.TextView tvemail;
    private android.widget.TextView tvstate;
    private com.hyphenate.easeui.widget.CircleImageView ivavatar;
    private String mUrl;
    private UserRelationInfoModel mModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcontact_by_qrcode);
        this.ivavatar = (CircleImageView) findViewById(R.id.iv_avatar);
        this.tvstate = (TextView) findViewById(R.id.tv_state);
        this.tvemail = (TextView) findViewById(R.id.tv_email);
        this.tvid = (TextView) findViewById(R.id.tv_id);
        this.tvnick = (TextView) findViewById(R.id.tv_nick);
        this.titlebar = (EaseTitleBar) findViewById(R.id.title_bar);


        setTitleBarLeftBack();

        mUrl = getIntent().getStringExtra(Constant.USERNAME);
        /*ApiManger.getApiService().search(mUrl)
                .compose(RxUtils.<SearchResult>applySchedulers())
                .subscribe(new BaseObserver<SearchResult>(this) {
                    @Override
                    protected void onSuccess(SearchResult searchResult) {
                        List<SearchResult.SearchBean> search = searchResult.getSearch();
                        if (search != null && search.size() > 0) {
                            SearchResult.SearchBean searchBean = search.get(0);
                            ImageUtils.show(mActivity, searchBean.getCover(), R.drawable.default_avatar, ivavatar);
                            tvnick.setText(searchBean.getNickname());
                            tvstate.setText(searchBean.getState());
                        }

                    }
                });*/
        ApiManger2.getApiService()
                .qrSearch(mUrl)
                .compose(this.<HttpResult<UserRelationInfoModel>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<UserRelationInfoModel>() {
                    @Override
                    protected void onSuccess(UserRelationInfoModel model) {
                        mModel = model;
                        ImageUtils.show(mActivity, model.getPicture(), R.drawable.default_avatar, ivavatar);
                        tvstate.setText(model.getSignature());
                        tvemail.setText(model.getEmail());
                        tvid.setText("ID:"+ model.getId());
                        tvnick.setText(model.getNickname());
                    }
                });
    }

    public void addContact(View view) {
        /*if(mId != 0) {
            saySomething(String.valueOf(mId));
        }*/
        if(mModel!=null){
            CodeUtils.addContact(mActivity, mModel.getId(), mModel.getUsername(), new Consumer<Boolean>() {
                @Override
                public void accept(@NonNull Boolean aBoolean) throws Exception {
                    if(aBoolean){
                        finish();
                    }
                }
            });
        }
    }

    private void saySomething(final String chatid) {
        if(EMClient.getInstance().getCurrentUser().equals(chatid)){
            new EaseAlertDialog(this, R.string.not_add_myself).show();
            return;
        }

        if(DemoHelper.getInstance().getContactList().containsKey(chatid)){
            //let the user know the contact already in your contact list
            if(EMClient.getInstance().contactManager().getBlackListUsernames().contains(chatid)){
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
                if(TextUtils.isEmpty(say))
                    say = getResources().getString(R.string.Add_a_friend);
                addContact(chatid, say);
            }
        });
    }
    /**
     *  add contact
     * @param chatid
     */
    public void addContact(final String chatid, final String say){
        ApiManger2.getApiService()
                .applyFriend(chatid, say)
                .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<EmptyData>() {
                    @Override
                    protected void onSuccess(EmptyData emptyData) {
                        String s1 = getResources().getString(R.string.send_successful);
                        Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
