package com.example.winnie.provider.store;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by winnie on 2018/3/9.
 */

public class AppCotentProvider extends ContentProvider {

    private static final int ALL = 1;
    private static final int SINGLE = 2;

    private static final String MULTIPLE_LINE = "vnd.android.cursor.dir/";// dir代表多行数据
    private static final String SINGLE_LINE = "vnd.android.cursor.item/";// item单行

    private UriMatcher matcher;
    private DBHelper helper;

    @Override
    public boolean onCreate() {
        helper = new DBHelper(getContext());
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AppConstant.AUTHORITY, AppConstant.USER_INFO_TABLE, ALL);
        matcher.addURI(AppConstant.AUTHORITY, AppConstant.USER_INFO_TABLE + "/#", SINGLE);// #匹配所有数字，*匹配所有字符
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int code = matcher.match(uri);
        Cursor cursor = null;

        String table = uri.getPathSegments().get(0);
        String groupBy = null;
        String having = null;

        if(code == ALL){
            cursor = helper.getReadableDatabase().query(table, projection, selection, selectionArgs, groupBy, having, sortOrder);
        }else if(code == SINGLE){
            long id = ContentUris.parseId(uri);// 从uri中取出id
            String selection1 = String.format("%s = ?", AppConstant.ID_COLUMNS);
            String[] selectionArgs1 = new String[] { String.valueOf(id) };

            cursor = helper.getReadableDatabase().query(table, projection, selection1, selectionArgs1, groupBy, having, sortOrder);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int code = matcher.match(uri);
        String type = null;
        if (code == ALL) {
            type = MULTIPLE_LINE + AppConstant.USER_INFO_TABLE;

        } else if (code == SINGLE) {
            type = SINGLE_LINE + AppConstant.USER_INFO_TABLE;
        }
        return type;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int code = matcher.match(uri);
        String table = uri.getPathSegments().get(0);
        if (code != ALL && code != SINGLE) {
            throw new RuntimeException("地址不能匹配");
        }

        SQLiteDatabase database = helper.getWritableDatabase();
        database.beginTransaction();
        long rowId = database.insert(table, null, values);
        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
        if(rowId > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ContentUris.withAppendedId(uri, rowId);// 返回值代表访问新添加数据的uri
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int code = matcher.match(uri);
        String table = uri.getPathSegments().get(0);
        int row = 0;//影响行数

        if (code == ALL) {
            throw new RuntimeException("不能删除所有数据");
        } else if (code == SINGLE) {
            long id = ContentUris.parseId(uri);
            String whereClause = String.format("%s = ?", AppConstant.ID_COLUMNS);//修改条件
            String[] whereArgs = {String.valueOf(id)};//修改条件的参数

            SQLiteDatabase database = helper.getReadableDatabase();
            database.beginTransaction();
            row = database.delete(table, whereClause, whereArgs);
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
            if(row > 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return row;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int code = matcher.match(uri);
        String table = uri.getPathSegments().get(0);
        int row = 0;//影响行数
        if (code == SINGLE) {
//            long id = ContentUris.parseId(uri);
//            String whereClause = String.format("%s = ?", DBHelper.ID_COLUMNS);//修改条件
//            String[] whereArgs = {String.valueOf(id)};//修改条件的参数

            SQLiteDatabase database = helper.getWritableDatabase();
            database.beginTransaction();
            row = database.update(table, values, selection, selectionArgs);
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
            if(row > 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return row;
    }
}
