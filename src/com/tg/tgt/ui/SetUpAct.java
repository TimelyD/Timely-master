package com.tg.tgt.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.easeui.GlideApp;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.tg.tgt.ActMgrs;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.DemoHelper;
import com.tg.tgt.R;
import com.tg.tgt.helper.SecurityDialog;
import com.tg.tgt.utils.SharedPreStorageMgr;
import com.tg.tgt.utils.VerUtils;
import com.tg.tgt.widget.dialog.CommonDialog;

import java.util.Locale;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 *
 * @author yiyang
 */
public class SetUpAct extends BaseActivity implements View.OnClickListener{
    private LinearLayout setPassWorld,setLang,securityPass;
    private TextView logoutBtn;
    private TextView mVersionTv;
    private TextView mEmailTv;
    private TextView mNameTv;
    private ImageView mSexIv;
    private ImageView mHeadIv;
    private AlertDialog mDialog;
    private int mCheckLan = 0;
    private LinearLayout setNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setting);

        EaseTitleBar titleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();

        setPassWorld.setOnClickListener(this);
        setNotify.setOnClickListener(this);
        securityPass.setOnClickListener(this);
        setLang.setOnClickListener(this);
        findViewById(R.id.layout_ver).setOnClickListener(this);

        logoutBtn.setOnClickListener(this);

        VerUtils.check(mActivity, false, new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
                if(aBoolean)
                    findViewById(R.id.iv_new_version).setVisibility(View.VISIBLE);
            }
        });
    }

    private void initView() {
        setPassWorld = (LinearLayout) findViewById(R.id.linear_setpass);
        setNotify = (LinearLayout) findViewById(R.id.linear_set_notify);
        securityPass = (LinearLayout) findViewById(R.id.linear_safepass);
        setLang = (LinearLayout) findViewById(R.id.linear_setlang);
		/*remind = (ImageView) findViewById(R.id.set_remind);
		type = (ImageView) findViewById(R.id.set_type);*/
        logoutBtn = (TextView) findViewById(R.id.text_logout);
        mVersionTv = (TextView) findViewById(R.id.tv_version);
        mNameTv = (TextView) findViewById(R.id.tv_name);
        mEmailTv = (TextView) findViewById(R.id.tv_email);
        mSexIv = (ImageView) findViewById(R.id.iv_sex);
        mHeadIv = (ImageView) findViewById(R.id.iv_head);

        mVersionTv.setText(VerUtils.getVer(this));
        GlideApp.with(this).load(SharedPreStorageMgr.getIntance().getStringValue(this, Constant.HEADIMAGE))/*.diskCacheStrategy(DiskCacheStrategy.RESULT)*/.placeholder(R.drawable.default_avatar).into(mHeadIv);
        mNameTv.setText(SharedPreStorageMgr.getIntance().getStringValue(this, Constant.NICKNAME));
        String last = SharedPreStorageMgr.getIntance().getStringValue(this, Constant.EMAIL_LAST);
        if(TextUtils.isEmpty(last)){
            mEmailTv.setText(SharedPreStorageMgr.getIntance().getStringValue(this, Constant.USERNAME) + "@qeveworld.com");
        }else {
            mEmailTv.setText(SharedPreStorageMgr.getIntance().getStringValue(this, Constant.USERNAME) + last);
        }

        String sex = SharedPreStorageMgr.getIntance().getStringValue(this, Constant.SEX);
        if (sex.equals("女")) {
            mSexIv.setImageDrawable(getResources().getDrawable(R.drawable.woman));
        }else if(sex.equals("男")){
            mSexIv.setImageDrawable(getResources().getDrawable(R.drawable.man));
        }else if (sex.equals("保密")) {
            mSexIv.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.linear_set_notify:
                startActivity(new Intent(mActivity, NotifySettingAct.class));
                break;
            case R.id.layout_ver:
                VerUtils.check(this, true);
                break;
            case R.id.linear_setpass:
                Intent intent = new Intent(SetUpAct.this,UpdatePwdAct.class);
                startActivity(intent);
                break;

            case R.id.text_logout:
                logout();
                break;

            case R.id.linear_setlang:
//                showLanDialog();
                startActivity(new Intent(mActivity, SetLanAct.class));
                break;

            case R.id.linear_safepass:
                startActivity(new Intent(mActivity, SecurityPasswodTypeSelectActivity.class));
                break;
        }
    }

    private void showLanDialog() {

        View view = View.inflate(SetUpAct.this, R.layout.dialog_language, null);
        RadioGroup rg_language = (RadioGroup)view. findViewById(R.id.rg_language);
        RadioButton rbenglish = (RadioButton)view. findViewById(R.id.rb_english);
        RadioButton rbchinese = (RadioButton)view. findViewById(R.id.rb_chinese);
        RadioButton rbsystem = (RadioButton) view.findViewById(R.id.rb_system);
        rg_language.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_system:
                        mCheckLan = 0;
                        break;
                    case R.id.rb_english:
                        mCheckLan = 2;
                        break;
                    case R.id.rb_chinese:
                        mCheckLan = 1;
                        break;
                }
            }
        });
        CommonDialog.show(this, getString(R.string.language), view, new CommonDialog.OnConfirmListener() {
            @Override
            public void onConfirm(AlertDialog dialog) {
                changeLan();
            }
        });
        int value = SharedPreStorageMgr.getIntance().getIntegerValue(SharedPreStorageMgr.PREFSNAMES, this,
                Constant.LANGUAGE);
        if(value == 0){
            rbsystem.setChecked(true);
        }else if(value == 1){
            rbchinese.setChecked(true);
        }else if(value == 2){
            rbenglish.setChecked(true);
        }
    }

    /**
     * 切换语言，切换之后，在每次启动SplashAct的时候就进行设置
     */
    private void changeLan() {
        int value = SharedPreStorageMgr.getIntance().getIntegerValue(SharedPreStorageMgr.PREFSNAMES, this,
                Constant.LANGUAGE);
        if(value == -1){
            if(mCheckLan == 0)
                return;
        }else if(mCheckLan == value){
            return;
        }
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        // 应用用户选择语言
        if(mCheckLan == 0){//默认
            config.locale = Locale.getDefault();
        }else if(mCheckLan == 2){//英文
            config.locale = Locale.ENGLISH;
        }else if(mCheckLan == 1){//中文
            config.locale = Locale.CHINESE;
        }
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(config, dm);
        SharedPreStorageMgr.getIntance().saveIntegerValue(SharedPreStorageMgr.PREFSNAMES, this, Constant.LANGUAGE, mCheckLan);
        ActMgrs.getActManager().popAllActivity();
        Intent intent = new Intent(SetUpAct.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * 退出登录
     */
    private void logout() {
        String st = getResources().getString(R.string.Are_logged_out);
        showProgress(st);
        DemoHelper.getInstance().logout(false,new EMCallBack() {

            @Override
            public void onSuccess() {
                SetUpAct.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgress();
                        ActMgrs.getActManager().popAllActivity();
                        Intent intent = new Intent(SetUpAct.this, LoginActivity.class);
                        startActivity(intent);

                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                SetUpAct.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        dismissProgress();
                        Toast.makeText(App.applicationContext, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
