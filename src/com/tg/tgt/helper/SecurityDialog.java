package com.tg.tgt.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.utils.SharedPreStorageMgr;
import com.tg.tgt.utils.ToastUtils;
import com.tg.tgt.widget.lock.KurtEditText;

/**
 *
 * @author yiyang
 */
public class SecurityDialog {

    public interface OnSecurityListener {
        void onPass();

//        /**
//         * @return 返回true表示处理，返回false表示不处理
//         */
//        boolean onCancel(Dialog dialog){ return false;}
    }

    /**
     * @param context 需要实现 IView
     */
    public static void show(final Activity context, final OnSecurityListener listener) {
        final String[] infocode = new String[1];

        View view = LayoutInflater.from(context).inflate(com.tg.tgt.R.layout.pop_security_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(view);
        final AlertDialog dialog = builder.show();
        final KurtEditText kurtedit = (KurtEditText) view.findViewById(com.tg.tgt.R.id.pop_kurtet);
        kurtedit.setKurtListener(new KurtEditText.KurtListener() {

            @Override
            public void keyword(String str) {
                infocode[0] = str;
                if (str.length() == 6) {
                    checkIsRight(context, kurtedit, dialog, infocode[0], listener);
                }
            }
        });
        /*Button cancel = (Button) view.findViewById(com.tg.tgt.R.id.pop_security_cancel);
        Button confirm = (Button) view.findViewById(com.tg.tgt.R.id.pop_security_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(infocode[0]) || infocode[0].length() < 6) {
                    ToastUtils.showToast(App.applicationContext, R.string.input_six_num);
//                    Toast.makeText(App.applicationContext, R.string.input_six_num, Toast.LENGTH_SHORT).show();
                    return;
                }
                checkIsRight(context, kurtedit, dialog, infocode[0], listener);
            }


        });
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                if (listener != null) {
//                    if(!listener.onCancel(dialog))
//                }
            }
        });*/
        view.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(kurtedit,InputMethodManager.SHOW_FORCED);
    }

    private static void checkIsRight(Activity context, final KurtEditText kurtedit, final Dialog dialog, String
            infocode, final OnSecurityListener listener) {
        String right = SharedPreStorageMgr.getIntance().getStringValue(context, Constant.INFOCODE);
        if(!TextUtils.isEmpty(right) && right.length() == 6){
            if(right.equals(infocode)){
                dialog.dismiss();
                listener.onPass();
            }else {
                kurtedit.clearText();
                ToastUtils.showToast(App.applicationContext, R.string.retry_pwd);
//                Toast.makeText(App.applicationContext, R.string.retry_pwd, Toast.LENGTH_SHORT).show();
            }
            return;
        }
        /*ApiManger.getApiService().isRight(App.getMyUid(), infocode)
                .compose(RxUtils.<BaseHttpResult>applySchedulers())
                .subscribe(new BaseObserver<BaseHttpResult>((IView) context) {
                    @Override
                    protected void onSuccess(BaseHttpResult baseHttpResult) {
                        dialog.dismiss();
                        listener.onPass();
                    }

                    @Override
                    public void onFaild(int code, String message) {
                        super.onFaild(code, message);
                        if (code == 0) {
                            kurtedit.clearText();
//                            Toast.makeText(App.applicationContext, R.string.retry_pwd, Toast.LENGTH_SHORT).show();
                            ToastUtils.showToast(App.applicationContext, R.string.retry_pwd);
                        }
                    }
                });*/
    }
}
