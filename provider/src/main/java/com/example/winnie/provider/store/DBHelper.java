package com.example.winnie.provider.store;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by winnie on 2018/3/7.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE = "userInfoDatabase";

    private static final String CREATE_TABLE = String.format(
            "CREATE TABLE %s (%s integer primary key autoincrement, %s String, %s int);",
            AppConstant.USER_INFO_TABLE, AppConstant.ID_COLUMNS, AppConstant.NAME_COLUMNS, AppConstant.AGE_COLUMNS);


    public DBHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
