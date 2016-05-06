package com.jason.xpass;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jason.xpass.activity.AddSecretInfoActivity;
import com.jason.xpass.http.HttpUtils;
import com.jason.xpass.http.XCallBack;
import com.jason.xpass.model.SecretInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Handler infoListHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {

                SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this,
                        getInfos((List<SecretInfo>) msg.obj),
                        R.layout.secret_info,
                        new String[]{"id", "title", "classify", "description", "createTime", "updateTime"},
                        new int[]{R.id.info_id, R.id.info_title, R.id.info_classify, R.id.info_description, R.id.info_create_time, R.id.info_update_time});

                ListView listView = (ListView) findViewById(R.id.info_list);
                if (listView != null) {
                    listView.setAdapter(simpleAdapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            SecretInfo select = JSON.parseObject(JSON.toJSONString(parent.getItemAtPosition(position)), SecretInfo.class);
                            int secretId = select.getId();
                            Intent intent = new Intent(MainActivity.this, SecretDetailActivity.class);
                            intent.putExtra("secretId", secretId);
                            intent.putExtra("title", select.getTitle());
                            startActivity(intent);
                        }
                    });
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add secret info button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddSecretInfoActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Load homepage's data: secret info list
        HttpUtils.get("/infos", new XCallBack() {

            @Override
            public void onResponse(String json) {
                Message msg = Message.obtain(infoListHandler);
                msg.obj = JSON.parseArray(json, SecretInfo.class);
                msg.what = 1;
                msg.sendToTarget();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds item_detail to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            TextView textView = new TextView(this);
            HttpUtils.get("/infos", new XCallBack() {

                @Override
                public void onResponse(String json) {
                    Message msg = Message.obtain(infoListHandler);
                    msg.obj = JSON.parseArray(json, SecretInfo.class);
                    msg.what = 1;
                    msg.sendToTarget();
                }
            });
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private List<Map<String, Object>> getInfos(List<SecretInfo> secretInfos) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(SecretInfo secretInfo : secretInfos) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", secretInfo.getId());
            map.put("title", secretInfo.getTitle());
            map.put("description", secretInfo.getDescription());
            map.put("createTime", secretInfo.getCreateTime());
            map.put("updateTime", secretInfo.getUpdateTime());
            list.add(map);
        }
        return list;
    }
}
