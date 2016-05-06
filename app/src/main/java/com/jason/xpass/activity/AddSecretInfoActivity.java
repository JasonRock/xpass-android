package com.jason.xpass.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import com.jason.xpass.R;
import com.jason.xpass.http.HttpUtils;
import com.jason.xpass.http.XCallBack;
import com.jason.xpass.model.SecretInfo;

import java.util.Date;

/**
 * Description:
 * <p/>
 * Created by js.lee on 5/7/16.
 */
public class AddSecretInfoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secret_info_add);

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    public void addSecretInfo(View view) {

        TableLayout tableLayout = (TableLayout) findViewById(R.id.secret_info_add);
        final String title = ((EditText) tableLayout.getTouchables().get(0)).getText().toString();
        final String description = ((EditText) tableLayout.getTouchables().get(1)).getText().toString();
        final String classify = ((EditText) tableLayout.getTouchables().get(2)).getText().toString();
        SecretInfo secretInfo = new SecretInfo();
        secretInfo.setTitle(title);
        secretInfo.setDescription(description);
        secretInfo.setCreateTime("");
        HttpUtils.post("/addSecretInfo", secretInfo, new XCallBack() {
            @Override
            public void onResponse(String json) {
                Toast.makeText(AddSecretInfoActivity.this, "New Secret info added",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
