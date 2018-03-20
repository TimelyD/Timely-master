package com.tg.tgt.helper;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.hyphenate.easeui.utils.L;
import com.hyphenate.util.HanziToPinyin;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.DemoHelper;
import com.tg.tgt.R;
import com.tg.tgt.http.ApiManger2;
import com.tg.tgt.http.HttpHelper;
import com.tg.tgt.http.HttpResult;
import com.tg.tgt.http.model2.GroupModel;
import com.tg.tgt.http.model2.GroupUserModel;
import com.tg.tgt.ui.BaseActivity;
import com.tg.tgt.ui.ChatActivity;
import com.tg.tgt.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 *
 * @author yiyang
 */
public class GroupManger {
    public static final String TAG = "GroupManger";

    private static final Handler mHandler =new Handler(Looper.getMainLooper());

    private static class SingletonHolder {
        private static final GroupManger INSTANCE = new GroupManger();
    }

    public static GroupManger getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static Map<String, GroupModel> sGroupMap;

    public static List<GroupModel> fetchAllGroup() throws Exception {
        try {
            final HttpResult<List<GroupModel>> httpResult = ApiManger2.getApiService().showMyGroup()
                    .blockingFirst();
            if (HttpHelper.isHttpSuccess(httpResult)) {
                List<GroupModel> data = httpResult.getData();
                if(data==null){
                    return null;
                }
                DBManager.getInstance().insertGroupList(data);
                L.i(TAG, "insertGroupList------------------>success");
                Map<String, GroupModel> groupMap = getGroupMap();
                groupMap.clear();
                for (GroupModel model : data) {
                    groupMap.put(model.getGroupSn(), model);
                }
                return data;
            }else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                Toast.makeText(App.applicationContext, httpResult.getMsg(), Toast.LENGTH_SHORT).show();

                    }
                });
                return null;
            }
        } catch (final Exception e) {
            e.printStackTrace();
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(App.applicationContext, HttpHelper.handleException(e), Toast.LENGTH_SHORT).show();
//                }
//            });
            throw e;
        }
    }

    public static GroupModel fetchGroup(String groupId) throws Exception{
        try {
            final HttpResult<GroupModel> httpResult = ApiManger2.getApiService().groupInfo(groupId)
                    .blockingFirst();
            if (HttpHelper.isHttpSuccess(httpResult)) {
                GroupModel data = httpResult.getData();
                return saveGroup(data);
            }else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(App.applicationContext, httpResult.getMsg(), Toast.LENGTH_SHORT).show();

                    }
                });
                return null;
            }
        } catch (final Exception e) {
            e.printStackTrace();
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(App.applicationContext, HttpHelper.handleException(e), Toast.LENGTH_SHORT).show();
//                }
//            });
            throw e;
        }
    }

    public static GroupModel saveGroup(GroupModel groupModel) {
        //保存的时候先设置下首字母
        for (GroupUserModel model : groupModel.getGroupUserModels()) {
            String nickname = model.getNickname();
            if (Character.isDigit(nickname.charAt(0))) {
                model.setInitialLetter("#");
            } else {
                model.setInitialLetter(HanziToPinyin.getInstance().get(nickname.substring(0, 1)).get(0).target
                        .substring(0, 1).toUpperCase());
                char header = model.getInitialLetter().toLowerCase().charAt(0);
                if (header < 'a' || header > 'z') {
                    model.setInitialLetter("#");
                }
            }
        }
        getGroupMap().put(groupModel.getGroupSn(), groupModel);
        Long id = groupModel.getId();
        for (GroupUserModel model : groupModel.getGroupUserModels()) {
        	model.setGroupId(id);
        }
        DBManager.getInstance().insertGroup(groupModel);
        return groupModel;
    }
/*

    public static void saveGroupUser(String groupSn, List<GroupUserModel> models) {
        getGroupMap().get(groupSn).getGroupUserModels().addAll(models);
        DBManager.getInstance().insertGroupUserList(models);
    }

*/


    public static void deleteGroup(GroupModel groupModel) {
        if (sGroupMap != null)
            sGroupMap.remove(groupModel.getGroupSn());

        DBManager.getInstance().deleteGroup(groupModel);
    }

    public static void deleteGroupUser(String groupId, String member) {
        List<GroupUserModel> groupUserModels = getGroupMap().get(groupId).getGroupUserModels();
        for (GroupUserModel model : groupUserModels) {
            if(model.getUsername().equals(member)){
                groupUserModels.remove(model);
                DBManager.getInstance().deleteGroupUser(model.getUserId());
            }
        }
    }

    public static List<GroupModel> getGroupList() {
        if (DemoHelper.getInstance().isLoggedIn() && sGroupMap == null) {
            sGroupMap = DBManager.getInstance().queryAllGroup();
        }

        // return a empty non-null object to avoid app crash
        if (sGroupMap == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(sGroupMap.values());
    }
    public static Map<String, GroupModel> getGroupMap() {
        if (DemoHelper.getInstance().isLoggedIn() && sGroupMap == null) {
            sGroupMap = DBManager.getInstance().queryAllGroup();
        }

        // return a empty non-null object to avoid app crash
        if (sGroupMap == null) {
            return new Hashtable<>();
        }
        return sGroupMap;
    }

    public static GroupModel getGroup(String groupId) {
        for (GroupModel model : getGroupList()) {
        	if(model.getGroupSn().equals(groupId)){
                return model;
            }
        }
        return null;
    }

    public static Map<String, GroupUserModel> getGroupUsers(String groupId) {
        List<GroupUserModel> list = getGroupMap().get(groupId).getGroupUserModels();
        Map<String, GroupUserModel> map = new Hashtable<>();
        for (GroupUserModel model : list) {
            try {
                map.put(String.valueOf(model.getUsername().toLowerCase()), model);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static void reset() {
        if (sGroupMap != null) {
            sGroupMap.clear();
        }
        DBManager.getInstance().closeDB();
    }
    public static boolean hasGroupUserInfo(String username){
        return !(GroupManger.getGroup(username) == null || GroupManger.getGroup(username).getGroupUserModels()==null || GroupManger.getGroup(username).getGroupUserModels().size()<1);
    }
    public static void toChat(final BaseActivity activity, final String username){toChat(activity, username, null);}
    /**
     * 群跳转到聊天界面
     * @param username 环信群sn
     */
    public static void toChat(final BaseActivity activity, final String username, final Consumer<Boolean> result){
        final Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP);
        intent.putExtra(Constant.EXTRA_USER_ID, username);
        //如果没用群资料先拉取群资料
        if(!hasGroupUserInfo(username)){
            fetchGroup(activity, username, new Consumer<Boolean>() {
                @Override
                public void accept(@NonNull Boolean aBoolean) throws Exception {
                    if(aBoolean)
                        activity.startActivity(intent);
                    if(result!=null)
                    result.accept(aBoolean);

                }
            }, true);
        }else {
            activity.startActivity(intent);
            //TODO 进群获取群最新消息避免出错
            fetchGroup(activity, username, null, false);
            try {
                if(result!=null)
                result.accept(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void fetchGroup(final BaseActivity activity, final String username, final Consumer<Boolean> result, boolean showLoading){

            try {
                Observable.fromCallable(new Callable<GroupModel>() {
                    @Override
                    public GroupModel call() throws Exception {
                        return GroupManger.fetchGroup(username);
                    }
                }).compose(activity.<GroupModel>bindToLifeCyclerAndApplySchedulers(showLoading?activity.getString(R
                        .string.loading):null))
                        .subscribe(new Consumer<GroupModel>() {
                            @Override
                            public void accept(@NonNull GroupModel groupModel) throws Exception {
                                if(result!=null)
                                result.accept(true);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                ToastUtils.showToast(App.applicationContext, HttpHelper.handleException(throwable));
                                if(result!=null)
                                result.accept(false);
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    result.accept(false);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
    }
}
