package com.tg.tgt.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.utils.L;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.DemoHelper;
import com.tg.tgt.R;
import com.tg.tgt.db.UserDao;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.BaseObserver2;
import com.tg.tgt.http.EmptyData;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.IView;
import com.tg.tgt.http.RxUtils;
import com.tg.tgt.http.model.IsCodeResult;
import com.tg.tgt.http.model2.UserFriendModel;
import com.tg.tgt.ui.BaseActivity;
import com.tg.tgt.widget.dialog.CommonDialog;

import io.reactivex.functions.Consumer;

/**
 *
 * @author yiyang
 */
public class CodeUtils {
  /*  *//**缓存个数*//*
    private static final int sCacheSize = 100;
    *//**用来保存用户是否已缓存的*//* //根据cacheSize和加载因子计算hashmap的capactiy，+1确保当达到cacheSize上限时不会触发hashmap的扩容，
    private static Map<String, IsCodeResult> sResultMap = new LinkedHashMap<String, IsCodeResult>((int) Math.ceil(sCacheSize / 0.75f) + 1,0.75f, true){
        @Override
        protected boolean removeEldestEntry(Entry<String, IsCodeResult> eldest) {
            return size() > sCacheSize;
        }
    };

    *//**清楚所有用户缓存,所有用户信息重新加载*//*
    public static void clearCache(){
        sResultMap.clear();
    }

    public static IsCodeResult getIsCodeResult(String username){
        return sResultMap.get(username);
    }*/
    private static long lastQueryTime;
    /**
     * 设置用户头像、名字、是否加锁
     * @param react
     * @param username
     * @param msgClock
     * @param avatar
     * @param name
     */
    public static void setIsMsgClock(final IView react, final String username, final ImageView msgClock,
                                     final ImageView avatar, final TextView name, final CodeUtilsHelper helper) {

        //这里要做缓存
//        DemoDBManager.getInstance()

//        if(mCache.contains(username)) {
//            Log.i("setIsMsgClock:",username+"已缓存");
//            return;
//        }
/*
        if(sResultMap.containsKey(username)){
            Log.i("setIsMsgClock:",username+"已缓存");
            msgClock.setTag(R.id.msg_lock_tag, true);
            msgClock.setTag(R.id.msg_result_tag, sResultMap.get(username));
            return;
        }
*/

        msgClock.setTag(username);

        EaseUser easeUser = EaseUserUtils.getUserInfo(username);

        if(easeUser!=null && !TextUtils.isEmpty(easeUser.getChatid())) {
            //在此加入初始化完成标志
            msgClock.setTag(R.id.msg_lock_tag, true);
            //将返回的数据注入
            msgClock.setTag(R.id.msg_result_tag, easeUser);
            if (easeUser.getIsLock() == 1) {
                //加密了
                msgClock.setVisibility(View.VISIBLE);
            } else {
                msgClock.setVisibility(View.INVISIBLE);
            }

            if (helper != null && App.needRefresh != null && App.needRefresh.equals(easeUser.getChatid())) {
                if(helper!=null)
                helper.onResult(null);
                App.needRefresh = null;
            }
        }else {
            msgClock.setTag(R.id.msg_lock_tag, false);
            if(System.currentTimeMillis() - lastQueryTime < 1000){
                return;
            }
            L.i("CODEUTILS", "asyncFetchContactsFromServer。。。。。。。。。。。。");
            lastQueryTime = System.currentTimeMillis();
            DemoHelper.getInstance().asyncFetchContactsFromServer(null);
        }

        /*ApiManger.getApiService()
                .isCode(App.getMyUid(), username)
                .compose(RxUtils.<IsCodeResult>applySchedulers())
                .subscribe(new BaseObserver<IsCodeResult>(react, false) {
                    @Override
                    public void onNext(final IsCodeResult result) {
                        super.onNext(result);
                        if (username.equals(msgClock.getTag())) {
                            //在此加入初始化完成标志
                            msgClock.setTag(R.id.msg_lock_tag, true);
                            //将返回的数据注入
                            msgClock.setTag(R.id.msg_result_tag, result);
//                            Glide.with((Activity) react).load(result.getChatidcover()).crossFade().skipMemoryCache
// (false).diskCacheStrategy
//                                    (DiskCacheStrategy.RESULT).placeholder(R.drawable.default_avatar).error(R
//                                    .drawable.default_avatar).into(avatar);
                            Glide.with((Activity) react).load(result.getChatidcover()).apply(new RequestOptions().placeholder(R.drawable
                                    .default_avatar).dontAnimate()).into(avatar);
                            name.setText(result.safeGetRemark());
                            //tag一样，可以显示
                            if (result.getIscode() == 1) {
                                //加密了
                                msgClock.setVisibility(View.VISIBLE);
                            } else {
                                msgClock.setVisibility(View.INVISIBLE);
                            }

                            ThreadPoolManager.getInstance().execute(new Runnable() {
                                @Override
                                public void run() {
                                    //先使用环信的userdao
                                    EaseUser easeUser = new EaseUser(username);
                                    easeUser.setNick(result.getChatidnickname());
                                    easeUser.setInfo(result.getChatidcover());
                                    easeUser.setIsLock(result.getIscode());
                                    easeUser.setRemark(result.safeGetRemark());
                                    easeUser.setChatid(result.getChatid());
                                    easeUser.setChatidsex(result.getChatidsex());
                                    easeUser.setChatidstate(result.getChatidstate());
//                            DemoDBManager.getInstance().updateContact(easeUser);
                                    DemoHelper.getInstance().updateContact(easeUser);
                                    msgClock.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (helper != null && App.needRefresh!=null && App.needRefresh.equals(result.getChatid())) {
                                                helper.onResult(result);
                                                App.needRefresh = null;
                                            }
                                        }
                                    });
                                }
                            });

                            sResultMap.put(username, result);
                        }
                    }

                    @Override
                    protected void onSuccess(IsCodeResult isCodeResult) {

                    }
                });*/
    }

    /**
     * 设置用户头像、名字、是否加锁
     * @param react
     * @param username
     * @param msgClock
     * @param avatar
     * @param name
     */
    public static void setIsMsgClock(final IView react, final String username, final ImageView msgClock,
                                     final ImageView avatar, final TextView name) {
        setIsMsgClock(react, username, msgClock, avatar, name, null);
    }


    public static void fetchUser(IView react, String userName, boolean showLoading, final Consumer<EaseUser> consumer) {
            ApiManger2.getApiService()
                    .showFriendInfo(userName)
                    .retry(3)
                    .compose(react==null?RxUtils.<HttpResult<UserFriendModel>>applySchedulers():react.<HttpResult<UserFriendModel>>bindToLifeCyclerAndApplySchedulers(showLoading?App.applicationContext.getString(R.string.loading):null, true))
                    .subscribe(new BaseObserver2<UserFriendModel>(showLoading) {
                        @Override
                        protected void onSuccess(UserFriendModel model) {
                            EaseUser user = wrapUser(model);
                            DemoHelper.getInstance().saveContact(user);
                            try {
                                consumer.accept(user);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFaild(int code, String message) {
                            super.onFaild(code, message);
                            try {
                                consumer.accept(null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

    }

    public static EaseUser wrapUser(UserFriendModel model){
        if(model==null)
            return null;
        EaseUser easeUser = new EaseUser(model.getUsername());
        easeUser.setIsLock(model.isSafe()?1:0);
        easeUser.setNickname(model.getNickname());
        easeUser.setAvatar(model.getPicture());
        easeUser.setChatid(String.valueOf(model.getId()));
        easeUser.setChatidstate(model.getSignature());
        easeUser.setChatidsex(model.getSex());
        easeUser.setRemark(model.getRemark());
        easeUser.setEmail(model.getEmail());
        EaseCommonUtils.setUserInitialLetter(easeUser);
        return easeUser;
    }

    public static void updateContact(EaseUser easeUser){
        DemoHelper.getInstance().saveContact(easeUser);
    }

    /**
     * 跳转到chat界面需要该参数
     * @param appContext
     * @param from
     * @return
     */
    public static IsCodeResult getIsCodeResult(Context appContext, String from) {
        IsCodeResult isCodeResult = new IsCodeResult();
        EaseUser userInfo = EaseUserUtils.getUserInfo(from);
        if (userInfo != null) {
            isCodeResult.setCon(userInfo.safeGetRemark());
            isCodeResult.setIscode(userInfo.getIsLock());
            isCodeResult.setInfocode(SharedPreStorageMgr.getIntance().getStringValue(appContext, Constant.INFOCODE));
            isCodeResult.setChatidcover(userInfo.getAvatar());
            isCodeResult.setChatidsex(userInfo.getChatidsex());
            isCodeResult.setChatid(userInfo.getChatid());
            isCodeResult.setChatidstate(userInfo.getChatidstate());
            //getChatidsex
            //getChatid
            //getChatidstate
        }
        return isCodeResult;
    }



    public interface CodeUtilsHelper {
        void onResult(IsCodeResult result);
    }

    public static void deleteContact(final BaseActivity activity, final EaseUser tobeDeleteUser, final Consumer<Boolean> result){
        try {
            // delete contact
            ApiManger2.getApiService()
                    .deleteFriend(tobeDeleteUser.getChatid())
                    .compose(activity.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers(false))
                    .subscribe(new BaseObserver2<EmptyData>() {
                        @Override
                        protected void onSuccess(EmptyData emptyData) {
                            UserDao dao = new UserDao(App.applicationContext);
                            dao.deleteContact(tobeDeleteUser.getUsername());
                            DemoHelper.getInstance().getContactList().remove(tobeDeleteUser.getUsername());
                            ToastUtils.showToast(App.applicationContext, R.string.delete_success);
                            try {
                                result.accept(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFaild(int code, String message) {
                            super.onFaild(code, message);
                            try {
                                result.accept(false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            //TODO don't remove invitation message
//            InviteMessgeDao dao = new InviteMessgeDao(App.applicationContext);
//            dao.deleteMessage(tobeDeleteUser.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
            try {
                result.accept(false);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void addContact(final BaseActivity activity, final String userId, final String chatId){
        addContact(activity, userId, chatId, null);
    }
    public static void addContact(final BaseActivity activity, final String userId, final String chatId, final Consumer<Boolean> consumer){

        if (EMClient.getInstance().getCurrentUser().equals(chatId)) {
            new EaseAlertDialog(activity, R.string.not_add_myself).show();
            return;
        }

        if (DemoHelper.getInstance().getContactList().containsKey(chatId)) {
            //let the user know the contact already in your contact list
            if (EMClient.getInstance().contactManager().getBlackListUsernames().contains(chatId)) {
                new EaseAlertDialog(activity, R.string.user_already_in_contactlist).show();
                return;
            }
            new EaseAlertDialog(activity, R.string.This_user_is_already_your_friend).show();
            return;
        }

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_saysomething, null);
        final EditText editText = (EditText) view.findViewById(R.id.say_et);
        CommonDialog.show(activity, activity.getString(R.string.say_something), view, new CommonDialog.OnConfirmListener() {
            @Override
            public void onConfirm(AlertDialog dialog) {
                String say = editText.getText().toString().trim();
                if (TextUtils.isEmpty(say))
                    say = activity.getString(R.string.Add_a_friend);
                ApiManger2.getApiService()
                        .applyFriend(userId, say)
                        .compose(activity.<HttpResult<EmptyData>>bindToLifeCyclerAndApplySchedulers())
                        .subscribe(new BaseObserver2<EmptyData>() {
                            @Override
                            protected void onSuccess(EmptyData emptyData) {
                                String s1 = activity.getString(R.string.send_successful);
                                Toast.makeText(App.applicationContext, s1, Toast.LENGTH_LONG).show();
                                if(consumer!= null)
                                    try {
                                        consumer.accept(true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                            }
                        });
            }
        });
    }

    public static String getUnreadCount(int unreadMsgCount){
        return String.valueOf(unreadMsgCount>99?"···":unreadMsgCount);
    }

    public static void showToEmailDialog(final Activity mActivity){
        new EaseAlertDialog(mActivity, mActivity.getString(R.string.code_sended), mActivity.getString(R.string.cancel), mActivity.getString(R.string.to_look_email), new EaseAlertDialog.AlertDialogUser() {
            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if(confirmed){
                    Uri uri = Uri.parse("http://www.qeveworld.com");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    mActivity.startActivity(intent);
                }
            }
        }).show();
    }
}
