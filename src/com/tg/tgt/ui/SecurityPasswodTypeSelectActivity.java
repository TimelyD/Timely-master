package com.tg.tgt.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.helper.SecurityDialog;
import com.tg.tgt.utils.SharedPreStorageMgr;

public class SecurityPasswodTypeSelectActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_passwod_type_select);
        setTitleBarLeftBack();
        findViewById(R.id.linear_modify).setOnClickListener(this);
        findViewById(R.id.linear_reset).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
                String infocode = SharedPreStorageMgr.getIntance().getStringValue(mActivity, Constant.INFOCODE);
        switch (v.getId()) {
            case R.id.linear_modify:
                if(TextUtils.isEmpty(infocode)|| infocode.equals("0")||infocode.length()<6){
                    Intent intentSecurity = new Intent(mActivity,SecurityPasswordAct.class);
                    startActivity(intentSecurity);
                    return;
                }
                SecurityDialog.show(mActivity, new SecurityDialog.OnSecurityListener() {
                    @Override
                    public void onPass() {
                        Intent intentSecurity = new Intent(mActivity,SecurityPasswordAct.class);
                        startActivity(intentSecurity);
                    }
                });
                break;
            case R.id.linear_reset:
                if(TextUtils.isEmpty(infocode)|| infocode.equals("0")||infocode.length()<6){
                    Intent intentSecurity = new Intent(mActivity,SecurityPasswordAct.class);
                    startActivity(intentSecurity);
                    return;
                }
//                Toast.makeText(mActivity, R.string.developing_toast, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(mActivity, VerifySafeCodeAct.class));
                break;

            default:
                break;
        }
    }
}
