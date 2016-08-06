package com.goldenration.sdbi.xx.timechoose.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenration.sdbi.xx.timechoose.R;
import com.goldenration.sdbi.xx.timechoose.adapter.MyRecordAdapter;
import com.goldenration.sdbi.xx.timechoose.bean.Record;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import static android.content.ContentValues.TAG;

/**
 * Created by Kiuber on 2016/8/4.
 */

public class MyRecordActivity extends AppCompatActivity {

    private TextView mTvLoading;
    private ListView mLvRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initView();
        initData();
    }

    private void initView() {
        mTvLoading = (TextView) findViewById(R.id.tv_loading);
        mLvRecord = (ListView) findViewById(R.id.lv_record);
    }

    private void initData() {
        String stu_ID = getSharedPreferences("user_info", MODE_PRIVATE).getString("Stu_ID", "");
        BmobQuery<Record> recordBmobQuery = new BmobQuery<Record>();
        recordBmobQuery.addWhereEqualTo("Stu_ID", stu_ID);
        recordBmobQuery.findObjects(MyRecordActivity.this, new FindListener<Record>() {
            @Override
            public void onSuccess(List<Record> list) {
                if (list.size() == 0) {
                    mTvLoading.setText("目前还没有记录");
                } else {
                    mTvLoading.setVisibility(View.GONE);
                    mLvRecord.setVisibility(View.VISIBLE);
                    mLvRecord.setAdapter(new MyRecordAdapter(getApplicationContext(), list));
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(MyRecordActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
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
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MyRecordActivity.this);
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
                        Toast.makeText(MyRecordActivity.this, "支付宝账号复制成功，正在跳转到支付宝。", Toast.LENGTH_LONG).show();

                        Intent intent = getPackageManager().getLaunchIntentForPackage("com.eg.android.AlipayGphone");
                        startActivity(intent);
                    }
                });
                builder1.setNegativeButton("确定", null);
                builder1.show();
                break;
            case R.id.action_exit:
                AlertDialog.Builder builder = new AlertDialog.Builder(MyRecordActivity.this);
                builder.setTitle("提示");
                builder.setMessage("你确定要退出管理员账号吗？");
                builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getSharedPreferences("user_info", MODE_PRIVATE).edit().clear().apply();
                        if (getSharedPreferences("user_info", MODE_PRIVATE).getString("Islogin", "已经退出") == "已经退出") {
                            Toast.makeText(MyRecordActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent i = getBaseContext().getPackageManager()
                                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        } else {
                            Toast.makeText(MyRecordActivity.this, "请再试一次", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                break;
            case R.id.action_share:
                mLvRecord.setVisibility(View.GONE);
                mTvLoading.setVisibility(View.VISIBLE);
                initData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
