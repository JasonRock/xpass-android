package com.jason.xpass;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jason.xpass.http.HttpUtils;
import com.jason.xpass.http.XCallBack;
import com.jason.xpass.model.ItemInfo;
import com.jason.xpass.model.SecretDetail;
import com.jason.xpass.model.SecretInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecretDetailActivity extends AppCompatActivity {

    private Handler itemDetailHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {

                Bundle bundle = msg.getData();
                int secretId = bundle.getInt("secretId");
                String title = bundle.getString("title");

                List<SecretDetail> list = (List<SecretDetail>) msg.obj;

                // Set title
                TextView detailIdView = (TextView) findViewById(R.id.detail_item_id);
                TextView detailTitleView = (TextView) findViewById(R.id.detail_item_title);
                if (detailTitleView != null && detailIdView != null) {
                    if (list.size() == 0) {
                        detailIdView.setText("NOTHING");
                        return;
                    } else {
                        detailIdView.setText(String.valueOf(secretId));
                        detailTitleView.setText(title);
                    }
                }

                SimpleAdapter simpleAdapter = new SimpleAdapter(SecretDetailActivity.this,
                        getDetails(list),
                        R.layout.item_detail,
                        new String[]{"itemDesc", "itemContent"},
                        new int[]{R.id.detail_item_desc, R.id.detail_item_content});

                ListView listView = (ListView) findViewById(R.id.detail_items);
                if (listView != null) {
                    listView.setAdapter(simpleAdapter);
                }
            } else if (msg.what == 2) {

                List<ItemInfo> itemInfos = (List<ItemInfo>) msg.obj;
                LayoutInflater inflater = getLayoutInflater();

                View layout = inflater.inflate(R.layout.dialog, null);
                new AlertDialog.Builder(SecretDetailActivity.this).setTitle("自定义布局").setView(layout)
                        .setPositiveButton("确定", null).setNegativeButton("取消", null).show();

                final String[] m = {"A型", "B型", "O型", "AB型", "其他"};
                // Select menu
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SecretDetailActivity.this,
                        android.R.layout.simple_spinner_item, m);
                Spinner spinner = (Spinner) findViewById(R.id.Spinner01);
                assert spinner != null;
                final EditText editText = (EditText) findViewById(R.id.etname);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        editText.setText("你的血型是：" + m[position]);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                //设置默认值
                spinner.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secret_detail);

        Intent intent = getIntent();
        final int secretId = intent.getIntExtra("secretId", 0);
        final String title = intent.getStringExtra("title");

        HttpUtils.get("/detail/" + secretId, new XCallBack() {

            @Override
            public void onResponse(String json) {
                Bundle bundle = new Bundle();
                bundle.putInt("secretId", secretId);
                bundle.putString("title", String.valueOf(title));

                Message msg = Message.obtain(itemDetailHandler);
                msg.obj = JSON.parseArray(json, SecretDetail.class);
                msg.what = 1;
                msg.setData(bundle);

                msg.sendToTarget();
            }
        });

        // Load floating button: append item
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_item_add);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView detailView = ((TextView) findViewById(R.id.detail_item_id));
                assert detailView != null;
                int secretId = detailView.getId();
                // Get items that could be appended
                HttpUtils.get("/remainItems/" + secretId, new XCallBack() {
                    @Override
                    public void onResponse(String json) {
                        List<ItemInfo> itemInfos = JSONArray.parseArray(json, ItemInfo.class);

                        Message msg = Message.obtain(itemDetailHandler);
                        msg.obj = itemInfos;
                        msg.what = 2;
                        msg.sendToTarget();
                    }
                });
            }
        });
    }

    private List<Map<String, Object>> getDetails(List<SecretDetail> secretDetails) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (SecretDetail secretDetail : secretDetails) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", secretDetail.getId());
            map.put("title", secretDetail.getTitle());
            map.put("itemName", secretDetail.getItemName());
            map.put("itemDesc", secretDetail.getItemDesc());
            map.put("securityLevel", secretDetail.getSecurityLevel());
            map.put("itemContent", secretDetail.getItemContent());
            list.add(map);
        }
        return list;
    }
}
