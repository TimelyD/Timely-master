package com.tg.tgt.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tg.tgt.ActMgrs;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.utils.SharedPreStorageMgr;

import java.util.Locale;

/**
 *
 * @author yiyang
 */
public class SetLanAct extends BaseActivity {

    private int mCheckLan = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_set_lan);
        setTitleBarLeftBack();
        RadioGroup rg_language = (RadioGroup) findViewById(R.id.rg_language);
        RadioButton rbenglish = (RadioButton) findViewById(R.id.rb_english);
        RadioButton rbchinese = (RadioButton) findViewById(R.id.rb_chinese);
        RadioButton rbsystem = (RadioButton) findViewById(R.id.rb_system);

        int value = SharedPreStorageMgr.getIntance().getIntegerValue(SharedPreStorageMgr.PREFSNAMES, this,
                Constant.LANGUAGE);
        if(value == 0){
            rbsystem.setChecked(true);
        }else if(value == 1){
            rbchinese.setChecked(true);
        }else if(value == 2){
            rbenglish.setChecked(true);
        }
        mCheckLan = value;

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
    }

    public void save(View view) {
        changeLan();
    }

    /**
     * 切换语言，切换之后，在每次启动SplashAct的时候就进行设置
     */
    private void changeLan() {
        int value = SharedPreStorageMgr.getIntance().getIntegerValue(SharedPreStorageMgr.PREFSNAMES, this,
                Constant.LANGUAGE);
        if(value == -1){
            if(mCheckLan == 0) {
                finish();
                return;
            }
        }else if(mCheckLan == value){
            finish();
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
        Intent intent = new Intent(mActivity, MainActivity.class);
        startActivity(intent);
    }
}
