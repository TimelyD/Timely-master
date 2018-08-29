package com.tg.tgt.http;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.tg.tgt.DaoMaster;
import com.tg.tgt.db.MigrationHelper;
import com.tg.tgt.http.model2.GroupModel;

import org.greenrobot.greendao.database.Database;

/**
 * Created by DELL on 2018/8/29.
 */


public class MyOpenHelper extends DaoMaster.OpenHelper {
    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {

        //把需要管理的数据库表DAO作为最后一个参数传入到方法中
     /*   MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {

            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        }, GroupModel.class);*/
    }
}
