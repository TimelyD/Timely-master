package com.tg.tgt.ui;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.tg.tgt.Constant;
import com.tg.tgt.R;
import com.tg.tgt.helper.SecurityDialog;
import com.tg.tgt.http.model.IsCodeResult;
import com.tg.tgt.utils.CodeUtils;

public class BusinessActivity extends PickContactNoCheckboxActivity{
    private EaseUser selectUser;
    private String type="1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type=getIntent().getStringExtra("type");
    }

    @Override
    protected void onListItemClick(int position) {
        selectUser = contactAdapter.getItem(position);
        if(type.equals("1")){
            new EaseAlertDialog(this, null, getString(R.string.send_to, selectUser.getNick()), null, new EaseAlertDialog.AlertDialogUser() {
                @Override
                public void onResult(boolean confirmed, Bundle bundle) {
                    if (confirmed) {
                        if (selectUser == null)
                            return;
                        final IsCodeResult isCodeResult = CodeUtils.getIsCodeResult(BusinessActivity.this, selectUser.getUsername());

                        if(isCodeResult.getIscode() == 1){
                            SecurityDialog.show(BusinessActivity.this,mContext.getString(R.string.security_title),new SecurityDialog.OnSecurityListener() {
                                @Override
                                public void onPass() {
                                    toChat();
                                }
                            });
                        }else {
                            toChat();
                        }
                    }
                }
            }, true).show();
        }
        if(type.equals("2")){
            new EaseAlertDialog(this, null, getString(R.string.send2_to, selectUser.getNick()), null, new EaseAlertDialog.AlertDialogUser() {
                @Override
                public void onResult(boolean confirmed, Bundle bundle) {
                    if (confirmed) {
                        if (selectUser == null)
                            return;
                        EMMessage message = EMMessage.createTxtSendMessage("名片",selectUser.getChatid());
                        message.setAttribute(Constant.BUSSINES_ID,getIntent().getStringExtra(Constant.BUSSINES_ID));
                        message.setAttribute(Constant.BUSSINES_NAME,getIntent().getStringExtra(Constant.BUSSINES_NAME));
                        message.setAttribute(Constant.BUSSINES_NUMBER,getIntent().getStringExtra(Constant.BUSSINES_NUMBER));
                        message.setAttribute(Constant.BUSSINES_PIC,getIntent().getStringExtra(Constant.BUSSINES_PIC));
                        //message.setAttribute(Constant.MESSAGE_ATTR_IS_BUSSINES, true);
                        EMClient.getInstance().chatManager().sendMessage(message);
                        Toast.makeText(mContext,mContext.getString(R.string.ti11),Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            }, true).show();
        }

    }

    private void toChat() {
        Log.i("参数",toJson(selectUser,1));
        setResult(RESULT_OK, new Intent().putExtra(Constant.BUSSINES_ID,selectUser.getUsername())
                .putExtra(Constant.BUSSINES_NAME,selectUser.getNick())
                .putExtra(Constant.BUSSINES_NUMBER,selectUser.getSn())
                .putExtra(Constant.BUSSINES_PIC,selectUser.getAvatar()));
        finish();
        /*mOnPhotoMenuListener.onPhotoSend(mAdapter.getSelectlist().toArray(new MediaBean[mAdapter
                .getSelectlist().size()]));*/
    }

    private String toJson(Object obj,int method) {
        // TODO Auto-generated method stub
        if (method==1) {
            //字段是首字母小写，其余单词首字母大写
            Gson gson = new Gson();
            String obj2 = gson.toJson(obj);
            return obj2;
        }else if(method==2){
            // FieldNamingPolicy.LOWER_CASE_WITH_DASHES    全部转换为小写，并用空格或者下划线分隔
            //FieldNamingPolicy.UPPER_CAMEL_CASE    所以单词首字母大写
            Gson gson2=new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            String obj2=gson2.toJson(obj);
            return obj2;
        }
        return "";
    }
}
