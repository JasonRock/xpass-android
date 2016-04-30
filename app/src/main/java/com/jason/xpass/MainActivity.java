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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import com.jason.xpass.http.HttpUtils;
import com.jason.xpass.http.XCallBack;
import com.jason.xpass.model.SecretInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static String EXTRA_MESSAGE = "com.example.jslee.MESSAGE";
    public final static String INFO = "com.jason.xpass.info";

    private TextView textView;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            textView.setTextSize(40);
            textView.setText(msg.getData().getString("info"));

            TableLayout layout = (TableLayout) findViewById(R.id.content_main);
            layout.addView(textView);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        // Inflate the menu; this adds items to the action bar if it is present.
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

            SimpleAdapter simpleAdapter = new SimpleAdapter(this, getData(), R.layout.items,
                    new String[]{"title", "info", "image"}, new int[]{R.id.item_title, R.id.item_info, R.id.item_img});

            ListView listView = (ListView) findViewById(R.id.info_list);
            if (listView != null) {
                listView.setAdapter(simpleAdapter);
            }


        } else if (id == R.id.nav_gallery) {

            TextView editMessage = (TextView) findViewById(R.id.edit_message);
            if (editMessage != null) {
                editMessage.clearComposingText();
                Random random = new Random();
                int r = random.nextInt(100);
                editMessage.setText(String.valueOf(r));
            }

        } else if (id == R.id.nav_slideshow) {

            textView = new TextView(this);
            HttpUtils.get("http://localhost:9000/info/1", new XCallBack<SecretInfo>() {

                @Override
                public void onResponse(SecretInfo o) {
                    Message msg = new Message();
                    msg.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putString("info", o.toString());
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            });

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private List<Map<String, Object>> getData() {
        //map.put(参数名字,参数值)
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "摩托罗拉");
        map.put("info", "google 1");
        map.put("img", R.drawable.ic_menu_camera);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "诺基亚");
        map.put("info", "google 2");
        map.put("img", R.drawable.ic_menu_gallery);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "三星");
        map.put("info", "google 3");
        map.put("img", R.drawable.ic_menu_send);
        list.add(map);
        return list;
    }
}
