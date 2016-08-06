package com.goldenration.sdbi.xx.timechoose.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.ClipboardManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenration.sdbi.xx.timechoose.R;
import com.goldenration.sdbi.xx.timechoose.adapter.AllRecordAdapter;
import com.goldenration.sdbi.xx.timechoose.bean.Record;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Kiuber on 2016/8/4.
 */

public class AdminActivity extends AppCompatActivity {

    private ListView mLvRecord;
    private TextView mTvLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mTvLoading = (TextView) findViewById(R.id.tv_loading);
        recordDataQuery();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.record, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(AdminActivity.this);
                builder1.setTitle("关于");
                builder1.setMessage("山东商务职业学院信息与艺术设计系选课系统\nV1.0\n@GoldenRatio");
                builder1.setCancelable(false);
                builder1.setPositiveButton("联系", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = "mqqwpa://im/chat?chat_type=wpa&uin=582279412";
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                });
                builder1.setNeutralButton("请开发者们喝咖啡", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 从API11开始android推荐使用android.content.ClipboardManager
                        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 将文本内容放到系统剪贴板里。
                        cm.setText("582279412@qq.com");
                        Toast.makeText(AdminActivity.this, "支付宝账号复制成功，正在跳转到支付宝。", Toast.LENGTH_LONG).show();

                        Intent intent = getPackageManager().getLaunchIntentForPackage("com.eg.android.AlipayGphone");
                        startActivity(intent);
                    }
                });
                builder1.setNegativeButton("确定", null);
                builder1.show();
                break;
            case R.id.action_exit:
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                builder.setTitle("提示");
                builder.setMessage("你确定要退出管理员账号吗？");
                builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getSharedPreferences("user_info", MODE_PRIVATE).edit().clear().apply();
                        if (getSharedPreferences("user_info", MODE_PRIVATE).getString("Islogin", "已经退出") == "已经退出") {
                            Toast.makeText(AdminActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent i = getBaseContext().getPackageManager()
                                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        } else {
                            Toast.makeText(AdminActivity.this, "请再试一次", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                break;
            case R.id.action_share:
                recordDataQuery();
                mLvRecord.setVisibility(View.GONE);
                mTvLoading.setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void recordDataQuery() {
        BmobQuery<Record> recordBmobQuery = new BmobQuery<Record>();
        recordBmobQuery.order("-createdAt");
        recordBmobQuery.findObjects(AdminActivity.this, new FindListener<Record>() {
            @Override
            public void onSuccess(List<Record> list) {
                if (list.size() == 0) {
                    Toast.makeText(AdminActivity.this, "目前没有选课记录", Toast.LENGTH_SHORT).show();
                    mTvLoading.setText("没有选课记录");
                } else {
                    mLvRecord = (ListView) findViewById(R.id.lv_record);
                    mLvRecord.setAdapter(new AllRecordAdapter(AdminActivity.this, list));
                    mTvLoading.setVisibility(View.GONE);
                    mLvRecord.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(int i, String s) {
            }
        });
    }
}
