package com.jason.xpass;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jason.xpass.adapter.ItemInfoAdapter;
import com.jason.xpass.http.HttpUtils;
import com.jason.xpass.http.XCallBack;
import com.jason.xpass.model.ItemInfo;
import com.jason.xpass.model.SecretDetail;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

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

                LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.dialog, null);
                new AlertDialog.Builder(SecretDetailActivity.this).setTitle("SELECT ITEM").setView(layout)
                        .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO
                            }
                        }).setNegativeButton("CANCEL", null).show();

                List<ItemInfo> itemInfos = (List<ItemInfo>) msg.obj;
                final ArrayAdapter<ItemInfo> adapter = new ItemInfoAdapter(SecretDetailActivity.this,
                        R.layout.detail_item, itemInfos);

                Spinner spinner = (Spinner) layout.findViewById(R.id.planets_spinner);
                assert spinner != null;
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Here you get the current item (a User object) that is selected by its position
                        ItemInfo itemInfo = adapter.getItem(position);
                        // Here you can do the action you want to...
                        Toast.makeText(SecretDetailActivity.this, "ID: " + itemInfo.getId() + "\nDesc: " + itemInfo.getItemDesc(),
                                Toast.LENGTH_SHORT).show();
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

    private List<Map<String, Object>> getItemDetail(List<ItemInfo> itemInfos) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (ItemInfo itemInfo : itemInfos) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", itemInfo.getId());
            map.put("itemName", itemInfo.getItemName());
            map.put("itemDesc", itemInfo.getItemDesc());
            map.put("securityLevel", itemInfo.getSecurityLevel());
            list.add(map);
        }
        return list;
    }
}
