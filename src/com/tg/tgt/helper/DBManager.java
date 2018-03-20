package com.tg.tgt.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.hyphenate.easeui.utils.L;
import com.hyphenate.easeui.utils.SpUtils;
import com.tg.tgt.ActionBean;
import com.tg.tgt.App;
import com.tg.tgt.Constant;
import com.tg.tgt.DaoMaster;
import com.tg.tgt.DaoSession;
import com.tg.tgt.DbOpenHelper;
import com.tg.tgt.http.model2.GroupModel;
import com.tg.tgt.http.model2.GroupModelDao;
import com.tg.tgt.http.model2.GroupUserModel;
import com.tg.tgt.http.model2.GroupUserModelDao;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author yiyang
 */
public class DBManager {
    public static final String TAG = "DBManager";
    private final static String dbName = "db.db";
    static private DBManager dbMgr = new DBManager();
    private Context context;

    private DaoMaster.DevOpenHelper openHelper;
    public DBManager(){
        context =App.applicationContext;
//        openHelper = new DaoMaster.DevOpenHelper(context, SpUtils.get(context, Constant.USERNAME,"")+dbName,null);
    }

    synchronized public static DBManager getInstance(){
        if(dbMgr == null){
            dbMgr = new DBManager();
        }
        return dbMgr;
    }

    synchronized public void closeDB(){
        if(openHelper != null){
            try {
                getWritableDatabase().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dbMgr = null;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase(){
        if (openHelper == null) {
            openHelper = new DbOpenHelper(context, SpUtils.get(context, Constant.USERNAME,"")+dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }
    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, SpUtils.get(context, Constant.USERNAME,"")+dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    public void insertGroupList(List<GroupModel> models){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        GroupModelDao groupModelDao = daoSession.getGroupModelDao();
        groupModelDao.deleteAll();
        groupModelDao.insertOrReplaceInTx(models);
        L.i(TAG, "insertGroupList----------------------");
        /*GroupUserModelDao groupUserModelDao = daoSession.getGroupUserModelDao();
        groupModelDao.deleteAll();
        for (GroupModel model : models) {
            groupUserModelDao.insertOrReplaceInTx(model.getGroupUserModels());
        }*/

    }
/*
    public void insertGroupUserList(List<GroupUserModel> model){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        GroupUserModelDao groupModelDao = daoSession.getGroupUserModelDao();
        groupModelDao.insertOrReplaceInTx(model);
    }*/

    public void insertGroup(GroupModel model){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        GroupModelDao groupModelDao = daoSession.getGroupModelDao();
        groupModelDao.delete(model);
        groupModelDao.insertOrReplace(model);
        GroupUserModelDao groupUserModelDao = daoSession.getGroupUserModelDao();
        List<GroupUserModel> groupUserModels = model.getGroupUserModels();
        /*for (GroupUserModel userModel : groupUserModels) {
        	userModel.setGroupId(Long.parseLong(model.getGroupSn()));
        }*/
        groupUserModelDao.insertOrReplaceInTx(groupUserModels);
        L.i(TAG, "insertGroup----------------------");
    }

    public void deleteGroup(GroupModel model){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        GroupModelDao groupModelDao = daoSession.getGroupModelDao();
        groupModelDao.delete(model);
    }

    public synchronized Map<String,GroupModel> queryAllGroup(){
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        List<GroupModel> list = daoSession.getGroupModelDao().queryBuilder().list();
        Map<String, GroupModel> map = new Hashtable<>();
        for (GroupModel model : list) {
        	map.put(model.getGroupSn(), model);
        }
        return map;
    }

    public void deleteGroupUser(Long userId) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        GroupUserModelDao groupUserModelDao = daoSession.getGroupUserModelDao();
        groupUserModelDao.deleteByKey(userId);
    }

    public void insertMomentAction(ActionBean bean){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        daoSession.getActionBeanDao().insertOrReplace(bean);
    }

    public List<ActionBean> queryAllMomentActions(){
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        return daoSession.getActionBeanDao().queryBuilder().list();
    }

    public void saveUnreadMotionActionCount(int count){
        synchronized (Constant.MOTION_ACTION_COUNT) {
            if(count >0) {
                count += getUnreadMotionActionCount();
            }else {
                count = 0;
            }
            SpUtils.put(App.applicationContext, Constant.MOTION_ACTION_COUNT, count);
        }
    }

    public int getUnreadMotionActionCount(){
        return SpUtils.get(App.applicationContext, Constant.MOTION_ACTION_COUNT, 0);
    }
}
