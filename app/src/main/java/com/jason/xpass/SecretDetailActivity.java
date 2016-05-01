package com.jason.xpass;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jason.xpass.http.HttpUtils;
import com.jason.xpass.http.XCallBack;
import com.jason.xpass.model.SecretDetail;

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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
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
