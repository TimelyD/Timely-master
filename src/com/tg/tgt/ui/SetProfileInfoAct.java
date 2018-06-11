package com.tg.tgt.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.easeui.widget.EaseTitleBar;
import com.tg.tgt.R;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpHelper;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.utils.CodeUtils;
import com.tg.tgt.utils.ToastUtils;

/**
 *
 * @author yiyang
 */
public class SetProfileInfoAct extends BaseActivity {
    public static final String TYPE = "type";
    public static final String INFO = "info";
    private int mType;
    private EaseTitleBar mTitleBar;
    /** nixianzzai */
    private TextView mTvHint;
    private EditText mEtInfo;

    private void initView() {
        mTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        mTvHint = (TextView) findViewById(R.id.tv_hint);
        mEtInfo = (EditText) findViewById(R.id.et_info);
        mEtInfo.setFilters(new InputFilter[]{CodeUtils.filter});
    }

    enum InfoType {
        NickName,
        Mood
    }

    public static void set(Activity context, InfoType type, int req) {
        context.startActivityForResult(new Intent(context, SetProfileInfoAct.class).putExtra(TYPE, type.ordinal()), req);
    }
    public static void set(Activity context, InfoType type, int req, String text) {
        context.startActivityForResult(new Intent(context, SetProfileInfoAct.class).putExtra("text", text).putExtra(TYPE, type.ordinal()), req);
    }
    public static void set(Activity context, InfoType type, int req, String text, int maxLength) {
        context.startActivityForResult(new Intent(context, SetProfileInfoAct.class).putExtra("maxLength", maxLength).putExtra("text", text).putExtra(TYPE, type.ordinal()), req);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_set_profile_info);
        initView();
        setTitleBarLeftBack();

        mType = getIntent().getIntExtra(TYPE, -1);
        if (mType == InfoType.NickName.ordinal()) {
            mTitleBar.setTitle(getString(R.string.popName_title));
            mTvHint.setText(R.string.popName_text);
        } else if (mType == InfoType.Mood.ordinal()) {
            mTitleBar.setTitle(getString(R.string.popMood_title));
            mTvHint.setText(R.string.popMood_text);
        }
        String text = getIntent().getStringExtra("text");
        if(!TextUtils.isEmpty(text)){
            mEtInfo.setText(text);
            mEtInfo.setSelection(mEtInfo.getText().length());
        }
        int maxLength = getIntent().getIntExtra("maxLength", -1);
        if(maxLength > 0) {
            mEtInfo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        }
    }

    public void confirm(View view) {
        update(mType, mEtInfo.getText().toString());
    }

    private void update(int type, final String info) {
        if(type == InfoType.Mood.ordinal()){
            ApiManger2.getApiService().addSignature(info)
                    .compose(this.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers())
                    .subscribe(new BaseObserver2<EmptyData>() {
                        @Override
                        protected void onSuccess(EmptyData emptyData) {
                            modifySuccess(info);
                        }
                    });
        }else {
            if(TextUtils.isEmpty(info)){
                finish();
                return;
            }
            ApiManger2.getApiService()
                    .modifyInfo(null, HttpHelper.toTextPlain(info), null, null, null)
                    .compose(this.<HttpResult<String>>bindToLifeCyclerAndApplySchedulers())
                    .subscribe(new BaseObserver2<String>() {
                        @Override
                        protected void onSuccess(String s) {
                            modifySuccess(info);
                        }
                    });
        }
    }

    private void modifySuccess(String info) {
        ToastUtils.showToast(getApplicationContext(), R.string.modify_success);
        setResult(RESULT_OK, new Intent().putExtra(INFO, info));
        finish();
    }
}
