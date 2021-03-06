package com.tg.tgt.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.ImageUtils;
import com.hyphenate.easeui.utils.SpUtils;
import com.hyphenate.easeui.utils.photo.MediaBean;
import com.hyphenate.easeui.utils.photo.PhotoBean;
import com.hyphenate.easeui.widget.CircleImageView;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseSwitchButton;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.easeui.widget.PopDiaLog;
import com.hyphenate.easeui.widget.ZQImageViewRoundOval;
import com.hyphenate.easeui.widget.photoselect.PreviewImageActivity;
import com.hyphenate.easeui.widget.photoselect.SelectObserable;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.DemoHelper;
import com.tg.tgt.R;
import com.tg.tgt.helper.SecurityDialog;
import com.tg.tgt.helper.UserHelper;
import com.tg.tgt.http.ApiException;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpHelper;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.model2.UserFriendModel;
import com.tg.tgt.parse.ParseManager;
import com.tg.tgt.utils.CodeUtils;
import com.tg.tgt.utils.ToastUtils;
import com.tg.tgt.widget.dialog.CommonDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class UserProfileActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_SELECT_BUSINESS2 = 182;
    private com.hyphenate.easeui.widget.EaseTitleBar titlebar;
    private com.hyphenate.easeui.widget.ZQImageViewRoundOval ivhead;
    private android.widget.TextView tvname;
    private android.widget.TextView tvnote;
    private android.widget.ImageView ivsex;
    private android.widget.TextView tvemail;
    private android.widget.TextView mood;
    private View setbeizhutv;
    private EaseSwitchButton checkbox;
    private String mUsername;

    //设置密码的requestcode
    private static final int REQUEST_CODE_SET_PWD = 78;
    public static final int REQUEST_CODE_ISCODE = 79;
    private EaseUser mEaseUser;
    private Button btnRelation;
    private boolean isFriend;
    private LinearLayout layoutcode;
    private String mUserId;
    private TextView tvid;
    private TextView tvsex;
    private View layoutclear;
    private String sn;
    private LinearLayout tui;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_user_profile);

        mUsername = getIntent().getStringExtra("username");
        mEaseUser = EaseUserUtils.getUserInfo(mUsername);
        mUserId = getIntent().getStringExtra("userId");
        if(TextUtils.isEmpty(mUserId)){
            isFriend = true;
        }
        initView();
        refreshUi(mEaseUser);
        loadData();
        initEvent();
    }

    @Override
    protected void initBar() {
        setTran();
    }

    private void initEvent() {
        setbeizhutv.setOnClickListener(this);
        btnRelation.setOnClickListener(this);
        layoutclear.setOnClickListener(this);
        checkbox.setOnClickListener(this);
        ivhead.setOnClickListener(this);
        tui.setOnClickListener(this);
        /*checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if(!checkListenerEnable) {
                    checkListenerEnable = true;
                    return;
                }
                String safePwd = SpUtils.get(mContext, Constant.INFOCODE, "");
                if (safePwd == null || safePwd
                        .equals("0") || safePwd.length() < 6) {
                    ToastUtils.showToast(App.applicationContext, getString(R.string.set_info_code));
                    Intent intent = new Intent(mActivity, SecurityPasswordAct.class);
                    intent.putExtra("toChatUsername", mUsername);
                    startActivityForResult(intent, REQUEST_CODE_SET_PWD);
                    return;
                }

                ApiManger2.getApiService()
                        .modifySafe(isChecked, mEaseUser.getChatid())
                        .compose(mActivity.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers(false))
                        .subscribe(new BaseObserver2<EmptyData>() {
                            @Override
                            protected void onSuccess(EmptyData emptyData) {
                                dismissProgress();
                                mEaseUser.setIsLock(isChecked ? 1 : 0);
                                CodeUtils.updateContact(mEaseUser);
                            }

                            @Override
                            public void onFaild(int code, String message) {
                                super.onFaild(code, message);
                                dismissProgress();
                                check();
                            }
                        });


            }
        });*/
    }

    private void initView() {
        this.checkbox = (EaseSwitchButton) findViewById(R.id.switch_btn);
        this.setbeizhutv = findViewById(R.id.set_beizhu_tv);
        this.layoutclear = findViewById(R.id.layout_clear);
        this.mood = (TextView) findViewById(R.id.mood);
        this.tvid = (TextView) findViewById(R.id.tv_id);
        this.tvsex = (TextView) findViewById(R.id.tv_sex);
        this.tvemail = (TextView) findViewById(R.id.tv_email);
        this.ivsex = (ImageView) findViewById(R.id.iv_sex);
        this.tvname = (TextView) findViewById(R.id.tv_name);
        this.tvnote = (TextView) findViewById(R.id.tv_note);
        this.ivhead = (ZQImageViewRoundOval) findViewById(R.id.iv_head);
        ivhead.setType(ZQImageViewRoundOval.TYPE_ROUND);ivhead.setRoundRadius(10);
        this.titlebar = (EaseTitleBar) findViewById(R.id.title_bar);
        this.btnRelation = (Button) findViewById(R.id.btn_relation);
        this.layoutcode = (LinearLayout) findViewById(R.id.layout_code);
        this.tui=(LinearLayout)findViewById(R.id.tui);
        mImmersionBar
                .fitsSystemWindows(true)
                .statusBarColor(com.hyphenate.easeui.R.color.chenjin)
                //.statusBarDarkFont(true, 0.5f)//设置状态栏字体颜色
                .init();
        titlebar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

       /* Observable.just(false)
                .map(new Function<Boolean, EaseUser>() {
                    @Override
                    public EaseUser apply(@NonNull Boolean aBoolean) throws Exception {
                        if(aBoolean)
                            return mEaseUser;
                        HttpResult<UserFriendModel> userFriendModelHttpResult = ApiManger2.getApiService()
                                .showFriendInfo(mEaseUser.getChatid())
                                .blockingFirst();
                        if(HttpHelper.isHttpSuccess(userFriendModelHttpResult)) {
                            return CodeUtils.wrapUser(userFriendModelHttpResult.getData());
                        }
                        throw new ApiException(getString(R.string.default_net_failed));
                    }
                });*/

    }

    private void loadData() {
        ApiManger2.getApiService().showFriendInfo(mUsername)
                .map(new Function<HttpResult<UserFriendModel>, EaseUser>() {
                    @Override
                    public EaseUser apply(@NonNull HttpResult<UserFriendModel> userFriendModelHttpResult) throws Exception {
                        sn = userFriendModelHttpResult.getData().getSn();
                        if(HttpHelper.isHttpSuccess(userFriendModelHttpResult)) {
                            isFriend = UserHelper.isFriend(userFriendModelHttpResult.getData().getRelationStatus());
                            return CodeUtils.wrapUser(userFriendModelHttpResult.getData());
                        }else {
                            throw new ApiException(userFriendModelHttpResult.getMsg());
                        }
                    }
                })
                .compose(this.<EaseUser>bindToLifeCyclerAndApplySchedulers(false))
                .subscribe(new Consumer<EaseUser>() {
                    @Override
                    public void accept(@NonNull EaseUser easeUser) throws Exception {
                        if(isFriend)
                        CodeUtils.updateContact(easeUser);
                        mEaseUser = easeUser;
                        updata();
                        refreshUi(easeUser);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();

                        ToastUtils.showToast(getApplicationContext(), HttpHelper.handleException(throwable));
                        /*EaseAlertDialog dialog = new EaseAlertDialog(mActivity, HttpHelper.handleException
                                (throwable), getString(R.string.give_up), getString(R.string.retry), new EaseAlertDialog.AlertDialogUser() {
                            @Override
                            public void onResult(boolean confirmed, Bundle bundle) {
                                if (confirmed) {
                                    loadData();
                                } else {
                                    onBackPressed();
                                }
                            }
                        });
                        dialog.show();*/
                        /*new AlertDialog.Builder(mActivity)
                                .setCancelable(false)
                                .setMessage(HttpHelper.handleException(throwable))
                                .setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        onBackPressed();
                                    }
                                })
                                .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        loadData();
                                    }
                                })
                                .show();*/
                    }
                });
    }

    private void refreshUi(@NonNull EaseUser easeUser) {
        layoutcode.setVisibility(isFriend?View.VISIBLE:View.GONE);
        setbeizhutv.setVisibility(isFriend?View.VISIBLE:View.GONE);
        btnRelation.setText(isFriend?R.string.delete_friend:R.string.add_friend);
        btnRelation.setBackgroundResource(isFriend?R.drawable.big_delete_btn_selector:R.drawable.btn_selector);
        Log.i("dcz1",EMClient.getInstance().getCurrentUser()+"");
        Log.i("dcz2",mUsername+"");
        btnRelation.setVisibility(mUsername.equals(EMClient.getInstance().getCurrentUser())?View.GONE:View.VISIBLE);
        if(easeUser==null)
            return;
        /*int genderDrawableRes = UserHelper.getGenderDrawableRes(easeUser);
        if(genderDrawableRes > 0) {
            ivsex.setImageResource(genderDrawableRes);
            ivsex.setVisibility(View.VISIBLE);
        }else {
            ivsex.setVisibility(View.INVISIBLE);
        }*/
        int genderRes = UserHelper.getGenderString(easeUser);
        tvsex.setText(genderRes);
        tvid.setText(easeUser.getChatid());
//        checkListenerEnable=false;
//        checkbox.setChecked(easeUser.getIsLock() != 0);
//        checkListenerEnable=true;
        if(easeUser.getIsLock() != 0){
            checkbox.openSwitch();
        }else {
            checkbox.closeSwitch();
        }
        //mood.setText(easeUser.getChatidstate());
        mood.setText(App.xin);
        mood.setText(this.getString(R.string.ti6)+sn);
        ImageUtils.show(mActivity, easeUser.getAvatar(), R.drawable.default_avatar, ivhead);
        tvname.setText(easeUser.safeGetRemark());
        tvnote.setText(easeUser.safeGetRemark());
        //tvemail.setText(easeUser.getEmail());
        tvemail.setText(App.xin);
        tvemail.setText(sn);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tui:
                startActivityForResult(new Intent(mContext,BusinessActivity.class).putExtra("type","2")
                        .putExtra(Constant.BUSSINES_ID,mEaseUser.getChatid())
                        .putExtra(Constant.BUSSINES_NAME,mEaseUser.safeGetRemark())
                        .putExtra(Constant.BUSSINES_NUMBER,mEaseUser.getSn())
                        .putExtra(Constant.BUSSINES_PIC,mEaseUser.getAvatar()),REQUEST_CODE_SELECT_BUSINESS2);
                break;
            case R.id.switch_btn:
                String safePwd = SpUtils.get(mContext, Constant.INFOCODE, "");
                if (safePwd == null || safePwd.equals("0") || safePwd.length() < 6) {
                    ToastUtils.showToast(App.applicationContext, getString(R.string.set_info_code));
                    Intent intent = new Intent(mActivity, SecurityPasswordAct.class);
                    intent.putExtra("toChatUsername", mUsername);
                    startActivityForResult(intent, REQUEST_CODE_SET_PWD);
                    return;
                }
                SecurityDialog.show(mActivity,mContext.getString(R.string.security_title),new SecurityDialog.OnSecurityListener() {
                    @Override
                    public void onPass() {
                        reverseCheck();
                    }
                });
                /*if(checkbox.isSwitchOpen()){
                    SecurityDialog.show(mActivity, new SecurityDialog.OnSecurityListener() {
                        @Override
                        public void onPass() {
                            reverseCheck();
                        }
                    });
                }else {
                    reverseCheck();
                }*/
                break;
            case R.id.set_beizhu_tv:
                updateRemark();
                break;
            case R.id.btn_relation:
                if(isFriend) {
                    showDialog();
                    /*new EaseAlertDialog(mActivity, getString(R.string.sure_delete), getString(R.string.cancel), getString(R.string.confirm), new EaseAlertDialog.AlertDialogUser() {
                        @Override
                        public void onResult(boolean confirmed, Bundle bundle) {
                            if (confirmed) {
                                CodeUtils.deleteContact(mActivity, mEaseUser, new Consumer<Boolean>() {
                                    @Override
                                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                                        if (aBoolean) {
                                            finish();
                                        }
                                    }
                                });
                            }
                        }
                    }).show();*/
                }else {
                    if(mEaseUser!=null){
                        CodeUtils.addContact(mActivity, mEaseUser.getChatid(), mEaseUser.getUsername());
                    }
                }
                break;
            case R.id.layout_clear:
                showDialog2();
                /*String st9 = getResources().getString(R.string.sure_to_empty_record);
                new EaseAlertDialog(mActivity, null, st9, null, new EaseAlertDialog.AlertDialogUser() {

                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (confirmed) {
                            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mUsername,
                                    EMConversation.EMConversationType.Chat);
                            if (conversation != null) {
                                conversation.clearAllMessages();
                            }
                            Toast.makeText(mContext, R.string.messages_are_empty, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, true).show();*/
                break;
            case R.id.iv_head:
                List<MediaBean> beans = new ArrayList<MediaBean>();
                if(mEaseUser.getAvatar()!=null){
                    PhotoBean e = new PhotoBean(mEaseUser.getAvatar());
                    beans.add(e);
                    SelectObserable.getInstance().setFolderAllImages(beans);
                    PreviewImageActivity.startPreviewPhotoActivity(mActivity,0,v,false);
                }

                break;
            default:
                break;
        }

    }

    private void reverseCheck() {
        ApiManger2.getApiService()
                .modifySafe(!checkbox.isSwitchOpen(), mEaseUser.getChatid())
                .delay(2, TimeUnit.SECONDS)
                .compose(mActivity.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers())
                .subscribe(new BaseObserver2<EmptyData>() {
                    @Override
                    protected void onSuccess(EmptyData emptyData) {
                        if(checkbox.isSwitchOpen()){
                            checkbox.closeSwitch();
                        }else {
                            checkbox.openSwitch();
                        }
                        mEaseUser.setIsLock(checkbox.isSwitchOpen() ? 1 : 0);
                        updata();
                        CodeUtils.updateContact(mEaseUser);
                    }

                    @Override
                    public void onFaild(int code, String message) {
                        super.onFaild(code, message);
                    }
                });
    }

    private void updateRemark() {
        View view = View.inflate(this, R.layout.dialog_beizhu, null);
        final EditText edittext = (EditText) view.findViewById(R.id.beizhu_et);
        edittext.setText(mEaseUser.safeGetRemark());
        edittext.setFilters(new InputFilter[]{CodeUtils.fter});
        CommonDialog.show(this, getString(R.string.set_beizhu), view, new CommonDialog.OnConfirmListener() {
            @Override
            public void onConfirm(AlertDialog dialog) {
                final String con = edittext.getText().toString();
//                ApiManger.getApiService()
//                        .beizhu(App.getMyUid(), mIsCodeResult.getChatid(), mIsCodeResult.getType(), con)
//                        .compose(RxUtils.<BaseHttpResult>applySchedulers())
//                        .subscribe(new BaseObserver<BaseHttpResult>(UserProfileActivity.this) {
//                            @Override
//                            protected void onSuccess(BaseHttpResult result) {
//                                mIsCodeResult.setType(1);
//                                mIsCodeResult.setCon(TextUtils.isEmpty(con)?mIsCodeResult.getChatidnickname():con);
//                                tvname.setText(mIsCodeResult.getCon());
//
//                                //由于要刷新通讯录，在这里保存一个值，避免多次刷新
//                                App.needRefresh = mIsCodeResult.getChatid();
//                            }
//                        });
                ApiManger2.getApiService().modifyRemark(mEaseUser.getChatid(), con)
                        .compose(mActivity.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers(false))
                        .subscribe(new Consumer<HttpResult<EmptyData>>() {
                            @Override
                            public void accept(@NonNull HttpResult<EmptyData> emptyDataHttpResult) throws Exception {
                                mEaseUser.setRemark(con);
                                tvname.setText(mEaseUser.safeGetRemark());
                                tvnote.setText(mEaseUser.safeGetRemark());
                                CodeUtils.updateContact(mEaseUser);
                                updata();
                            }
                        });
            }
        });
    }

    private void updata(){
       /* ParseManager.getInstance().getContactInfos(null, new EMValueCallBack<List<EaseUser>>() {
            @Override
            public void onSuccess(List<EaseUser> easeUsers) {
                DemoHelper.getInstance().updateContactList(easeUsers);
            }

            @Override
            public void onError(int i, String s) {
                dismissProgress();
                ToastUtils.showToast(getApplicationContext(), s);
            }
        });*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SET_PWD){
            if(resultCode == RESULT_OK){
                //获得从修改密码界面返回的值
                ToastUtils.showToast(App.applicationContext, getString(R.string.encoded));
                mEaseUser.setIsLock(1);
                checkbox.openSwitch();
                CodeUtils.updateContact(mEaseUser);
                refreshUi(mEaseUser);
                updata();
            }else {
//                check();
            }
        }
    }
    private void showDialog() {
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
        bt1.setText(R.string.delete_friend);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                CodeUtils.deleteContact(mActivity, mEaseUser, new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            finish();
                        }
                    }
                });
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
        ti.setText(R.string.ti8);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mUsername,
                        EMConversation.EMConversationType.Chat);
                if (conversation != null) {
                    conversation.clearAllMessages();
                }
                Toast.makeText(mContext, R.string.messages_are_empty, Toast.LENGTH_SHORT).show();
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        super.finish();
    }
}
