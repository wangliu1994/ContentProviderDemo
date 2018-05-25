package com.example.resolver.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by winnie on 2018/3/12.
 */

public class UserInfoUtils {
    private final static String ID_COLUMNS = AppConstant.ID_COLUMNS;
    private final static String NAME_COLUMNS = AppConstant.NAME_COLUMNS;
    private final static String AGE_COLUMNS = AppConstant.AGE_COLUMNS;
    private final static String URI = "content://" + AppConstant.AUTHORITY + AppConstant.PATH;

    public static ArrayList<UserInfo> queryUsers(Context context) {
        String selection = null;
        String[] selectionArgs = null;
        String orderBy = null;

        ArrayList<UserInfo> userInfos = new ArrayList<>();

        Uri uri = Uri.parse(URI);
        Cursor cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, orderBy);
        if(cursor != null && cursor.getColumnCount() > 0){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                UserInfo userInfo = new UserInfo();
                userInfo.setId(cursor.getLong(cursor.getColumnIndex(ID_COLUMNS)));
                userInfo.setName(cursor.getString(cursor.getColumnIndex(NAME_COLUMNS)));
                userInfo.setAge(cursor.getInt(cursor.getColumnIndex(AGE_COLUMNS)));
                userInfos.add(userInfo);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return userInfos;
    }

    public static ArrayList<UserInfo> queryUserById(Context context, long id) {
        String[] columns = {ID_COLUMNS, NAME_COLUMNS, AGE_COLUMNS};
        String selection = String.format("%s = ?", ID_COLUMNS);
        String[] selectionArgs = {String.valueOf(id)};
        String orderBy = null;

        ArrayList<UserInfo> userInfos = new ArrayList<>();

        Uri uri = Uri.parse(URI + "/" + id);
        Cursor cursor = context.getContentResolver().query(uri, columns, selection, selectionArgs, orderBy);
        if(cursor != null && cursor.getColumnCount() > 0){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                UserInfo userInfo = new UserInfo();
                userInfo.setId(cursor.getLong(cursor.getColumnIndex(ID_COLUMNS)));
                userInfo.setName(cursor.getString(cursor.getColumnIndex(NAME_COLUMNS)));
                userInfo.setAge(cursor.getInt(cursor.getColumnIndex(AGE_COLUMNS)));
                userInfos.add(userInfo);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return userInfos;
    }

    public static void insertUser(Context context, UserInfo userInfo) {
        ContentValues cv = new ContentValues();
//        cv.put(ID_COLUMNS, userInfo.getId());
        cv.put(NAME_COLUMNS, userInfo.getName());
        cv.put(AGE_COLUMNS, userInfo.getAge());

        Uri uri = Uri.parse(URI);
        context.getContentResolver().insert(uri, cv);
    }

    public static void updateUser(Context context, UserInfo userInfo) {
        ContentValues cv = new ContentValues();
//        cv.put(ID_COLUMNS, userInfo.getId());
        cv.put(NAME_COLUMNS, userInfo.getName());
        cv.put(AGE_COLUMNS, userInfo.getAge());

        String whereClause = String.format("%s = ?", ID_COLUMNS);//修改条件
        String[] whereArgs = {String.valueOf(userInfo.getId())};//修改条件的参数

        Uri uri = Uri.parse(URI + "/" + userInfo.getId());
        context.getContentResolver().update(uri, cv, whereClause, whereArgs);
    }

    public static void deleteUser(Context context, UserInfo userInfo) {
        String whereClause = String.format("%s = ?", ID_COLUMNS);//修改条件
        String[] whereArgs = {String.valueOf(userInfo.getId())};//修改条件的参数

        Uri uri = Uri.parse(URI + "/" + userInfo.getId());
        context.getContentResolver().delete(uri, whereClause, whereArgs);
    }
}
