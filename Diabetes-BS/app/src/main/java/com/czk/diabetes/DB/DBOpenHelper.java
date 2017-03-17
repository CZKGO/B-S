package com.czk.diabetes.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xuezaishao on 2017/3/16.
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String name = "blood_sugar.db"; //数据库名称

    private static final int version = 1; //数据库版本

    public DBOpenHelper(Context context) {
        super(context, name, null, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // 构建创建表的SQL语句（可以从SQLite Expert工具的DDL粘贴过来加进StringBuffer中）
        StringBuffer sBuffer = new StringBuffer();

        sBuffer.append("CREATE TABLE [blood_sugar_record] (");
        sBuffer.append("[_id] TEXT PRIMARY KEY, ");
        sBuffer.append("[date] TEXT,");
        sBuffer.append("[time_slot] TEXT,");
        sBuffer.append("[value] REAL)");

        // 执行创建表的SQL语句
        db.execSQL(sBuffer.toString());

        // 即便程序修改重新运行，只要数据库已经创建过，就不会再进入这个onCreate方法

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
