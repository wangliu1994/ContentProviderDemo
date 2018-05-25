package com.example.resolver;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.resolver.store.AppConstant;
import com.example.resolver.store.AppContentObserver;
import com.example.resolver.store.UserInfo;
import com.example.resolver.store.UserInfoUtils;

import java.util.ArrayList;

/**
 * Created by winnie on 2018/3/8.
 */

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private ListView listView;
    private ListAdapter adapter;
    private ArrayList<UserInfo> userInfos;

    private EditText userIdEdt;
    private EditText userNameEdt;
    private EditText userAgeEdt;

    private Button addBtn1;
    private Button updateBtn1;
    private Button deleteBtn1;
    private Button queryBtn1;

    private AppContentObserver observer;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(observer);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasWriteContactsPermission = checkSelfPermission("edu.android.demos.permission.READ_APPS");
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{"edu.android.demos.permission.READ_APPS"},REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

        listView = (ListView) findViewById(R.id.list_view);
        userInfos  = UserInfoUtils.queryUsers(MainActivity.this);
        adapter = new ListAdapter(MainActivity.this, userInfos);
        listView.setAdapter(adapter);

        userIdEdt = (EditText) findViewById(R.id.user_id);
        userNameEdt = (EditText) findViewById(R.id.user_name);
        userAgeEdt = (EditText) findViewById(R.id.user_age);

        addBtn1 = (Button) findViewById(R.id.add1);
        addBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserInfoUtils.insertUser(MainActivity.this, getUserInfo());
                clearEdt();
            }
        });

        updateBtn1 = (Button) findViewById(R.id.update1);
        updateBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoUtils.updateUser(MainActivity.this, getUserInfo());
                clearEdt();
            }
        });

        deleteBtn1 = (Button) findViewById(R.id.delete1);
        deleteBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoUtils.deleteUser(MainActivity.this, getUserInfo());
                clearEdt();
            }
        });

        queryBtn1 = (Button) findViewById(R.id.query1);
        queryBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = 0 ;
                try{
                    id = Long.parseLong(userIdEdt.getText().toString());
                }catch (Exception e){

                }
                if(id == 0){
                    userInfos  = UserInfoUtils.queryUsers(MainActivity.this);
                }else {
                    userInfos = UserInfoUtils.queryUserById(MainActivity.this, id);
                }
                adapter = new ListAdapter(MainActivity.this, userInfos);
                listView.setAdapter(adapter);
            }
        });

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                userInfos = (ArrayList<UserInfo>) msg.obj;
                adapter = new ListAdapter(MainActivity.this, userInfos);
                listView.setAdapter(adapter);
            }
        };
        Uri uri = Uri.parse("content://" + AppConstant.AUTHORITY + AppConstant.PATH);
        observer = new AppContentObserver(MainActivity.this, handler);
        /**
         * notifyForDescendents：如果为true表示以这个Uri为开头的所有Uri都会被匹配到，
         * 如果为false表示精确匹配，即只会匹配这个给定的Uri。
         */
        getContentResolver().registerContentObserver(uri, true, observer);
    }

    private UserInfo getUserInfo(){

        String name = userNameEdt.getText().toString();
        if(TextUtils.isEmpty(name)){
            name = userNameEdt.getHint().toString();
        }

        long id = 0 ;
        int age = 0;
        try{
            id = Long.parseLong(userIdEdt.getText().toString());
            age = Integer.parseInt(userAgeEdt.getText().toString());
        }catch (Exception e){

        }

        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setName(name);
        userInfo.setAge(age);

        return userInfo;
    }

    private void clearEdt(){
        userIdEdt.setText("");
        userNameEdt.setText("");
        userAgeEdt.setText("");
    }

    private class ListAdapter extends BaseAdapter{
        private Context context;
        private ArrayList<UserInfo> userInfos;

        public ListAdapter(Context context, ArrayList<UserInfo> userInfos) {
            this.context = context;
            if(userInfos == null){
                this.userInfos = new ArrayList<>();
            }else {
                this.userInfos = userInfos;
            }
        }

        @Override
        public int getCount() {
            return userInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return userInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.view_user_list_item, null);
            }

            final UserInfo userInfo = (UserInfo) getItem(position);

            TextView userIdText = (TextView) convertView.findViewById(R.id.user_id);
            userIdText.setText("编号：" + userInfo.getId());
            TextView userNameText = (TextView) convertView.findViewById(R.id.user_name);
            userNameText.setText("姓名：" + userInfo.getName());
            TextView userAgeText = (TextView) convertView.findViewById(R.id.user_age);
            userAgeText.setText("年龄：" + userInfo.getAge());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userIdEdt.setText(String.valueOf(userInfo.getId()));
                    userNameEdt.setText(userInfo.getName());
                    userAgeEdt.setText(String.valueOf(userInfo.getAge()));
                }
            });
            return convertView;
        }
    }
}
