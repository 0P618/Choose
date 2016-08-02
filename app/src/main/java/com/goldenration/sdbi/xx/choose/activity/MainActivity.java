package com.goldenration.sdbi.xx.choose.activity;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.goldenration.sdbi.xx.choose.R;
import com.goldenration.sdbi.xx.choose.bean.User;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class MainActivity extends AppCompatActivity {

    private User mUserList;
    private View view1;
    private int currentViewId;
    private View view2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "620ee288644d1af7c8ebd6f951a0e2a4");
        view1 = View.inflate(this, R.layout.activity_main, null);
        setContentView(view1);
        if (getSharedPreferences("user_info", MODE_PRIVATE).getBoolean("isLogin", false)) {
        } else {
            login();
        }
        findViewById(R.id.btn_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view2 = View.inflate(MainActivity.this, R.layout.view_change_pwd, null);
                setContentView(view2);
            }
        });
    }

    private void login() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("登陆");
        View view = View.inflate(MainActivity.this, R.layout.view_login, null);
        builder.setView(view);
        builder.setCancelable(false);

        final Dialog dialog = builder.create();
        dialog.show();
        final EditText mEtStuID = (EditText) view.findViewById(R.id.et_stu_id);
        final EditText mEtPwd = (EditText) view.findViewById(R.id.et_pwd);
        view.findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobQuery<User> userBmobQuery = new BmobQuery<User>();
                userBmobQuery.addWhereEqualTo("User_StuID", mEtStuID.getText().toString());
                userBmobQuery.findObjects(MainActivity.this, new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> list) {
                        if (list.size() == 0) {
                            Toast.makeText(MainActivity.this, "没有此学号，请联系开发人员", Toast.LENGTH_SHORT).show();
                        } else {
                            if (list.size() == 1) {
                                mUserList = list.get(0);
                                if (TextUtils.equals(mUserList.getUser_Pwd().trim(), mEtPwd.getText().toString().trim())) {
                                    if (TextUtils.equals(mUserList.getUser_DefaultPwd(), "true")) {
                                        showUserConfirmDialog();
                                    }
                                    Toast.makeText(MainActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showUserConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("身份确认");
        View view = View.inflate(MainActivity.this, R.layout.view_user_confirm, null);
        builder.setView(view);
        builder.setCancelable(false);

        final Dialog dialog = builder.create();
        dialog.show();

        final EditText mEtName = (EditText) view.findViewById(R.id.et_name);
        view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.equals(mEtName.getText().toString().trim(), mUserList.getUser_Name())) {
                    Toast.makeText(MainActivity.this, "身份确认成功", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    showChangePwdDialog();
                } else {
                    Toast.makeText(MainActivity.this, "身份确认失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showChangePwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("修改默认密码");
        View view = View.inflate(MainActivity.this, R.layout.view_change_pwd, null);
        builder.setView(view);
        builder.setCancelable(false);

        Dialog dialog = builder.create();
        dialog.show();

        final EditText mEtNewPwd = (EditText) view.findViewById(R.id.et_new_pwd);
        view.findViewById(R.id.btn_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle("密码确认");
                final String mStrNewPwd = mEtNewPwd.getText().toString().trim();
                builder1.setMessage("密码将改为\n\n" + mStrNewPwd + "\n\n切记密码，找回密码可能将会浪费几晚上的时间");
                builder1.setCancelable(false);
                builder1.setPositiveButton("确认修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        User user = new User();
                        user.setUser_Pwd(mStrNewPwd);
                        user.save(MainActivity.this, new SaveListener() {
                            @Override
                            public void onSuccess() {
                                saveUserInfo(mUserList.getUser_StuID(), mUserList.getUser_Name());
                                Toast.makeText(MainActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                                Intent k = MainActivity.this.getPackageManager().getLaunchIntentForPackage("com.goldenration.sdbi.xx.choose");
                                k.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                MainActivity.this.startActivity(k);
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                Dialog dialog1 = builder1.create();
                dialog1.show();
                builder1.setNegativeButton("重修修改", null);
            }
        });
    }

    private void saveUserInfo(String userName, String userID) {
        getSharedPreferences("user_info", MODE_PRIVATE).edit().putString("User_StuID", userName).commit();
        getSharedPreferences("user_info", MODE_PRIVATE).edit().putString("User_Name", userID).commit();

        getSharedPreferences("user_info", MODE_PRIVATE).edit().putBoolean("isLogin", true).commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentViewId == view2.getId()) {
                setCurrentView(view1);
            } else {
                System.exit(0);
            }
            return false;
        }
        return false;
    }

    private void setCurrentView(View view) {
        currentViewId = view.getId();
        setContentView(view);
    }
}
