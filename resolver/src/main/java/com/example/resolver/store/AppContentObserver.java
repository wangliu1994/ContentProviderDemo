package com.example.resolver.store;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;

/**
 * Created by winnie on 2018/3/12.
 */

public class AppContentObserver extends ContentObserver {
    private Context context;
    private Handler handler;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public AppContentObserver(Context context, Handler handler) {
        super(handler);
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        ArrayList<UserInfo> userInfos = UserInfoUtils.queryUsers(context);
        Message message = new Message();
        message.obj = userInfos;
        handler.sendMessage(message);
    }
}
