package com.goldenration.sdbi.xx.timechoose.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goldenration.sdbi.xx.timechoose.R;
import com.goldenration.sdbi.xx.timechoose.bean.Admin;
import com.goldenration.sdbi.xx.timechoose.bean.Record;
import com.goldenration.sdbi.xx.timechoose.bean.Student;
import com.goldenration.sdbi.xx.timechoose.util.GlideLoader;
import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetServerTimeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class MainActivity extends AppCompatActivity {
    private Dialog dialog;
    private String objectId;
    private Student mStudent;
    private TextView mTvStuID;
    private TextView mTvStuName;
    private TextView mTvTime;
    private LinearLayout mLinearLayout;
    private LinearLayout mLinearLayoutSelectPic;
    private Spinner mSpinner;
    String[] mStrWhere =
            {
                    "去图书馆读书看报",
                    "以老带新活动",
                    "在班级固定教室学习和书写作业",
                    "社团活动和系部专业工作室、互联网+学研基地活动",
                    "参加体育锻炼和系部组织的体育比赛(系部统一组织)"
            };
    private EditText mEtRemarks;
    private Button mBtnSelectPic;
    private ImageView mIvSelectedPic;
    private Button mBtnCommit;
    private String mStrStuID;
    private String mStrStuName;
    private String picPath;
    private TextView mTvCommitResult;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "620ee288644d1af7c8ebd6f951a0e2a4");
        String mStrUserType = getSharedPreferences("user_info", MODE_PRIVATE).getString("User_Type", "没有登陆");
        if (mStrUserType == "Student") {
            setContentView(R.layout.activity_main);
        } else if (mStrUserType == "Admin" && getSharedPreferences("user_info", MODE_PRIVATE).getBoolean("IsLogin", false)) {
            setContentView(R.layout.activity_main);
            startActivity(new Intent(this, AdminActivity.class));
            finish();
        } else {
            setContentView(R.layout.activity_main);
        }
        initView();
        initData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(1000);
                        Message msg = new Message();
                        msg.what = 1;  //消息(一个整型值)
                        mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (true);
            }
        }).start();
        if (!(getSharedPreferences("user_info", MODE_PRIVATE).getBoolean("IsLogin", false))) {
            Login();
            mLinearLayout.setVisibility(View.GONE);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss     ");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String sysTimeStr = formatter.format(curDate);
                    mTvTime.setText(sysTimeStr);
                    break;
            }
        }
    };

    private void initView() {
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_stu_name);
        mLinearLayoutSelectPic = (LinearLayout) findViewById(R.id.ll_select_pic);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mTvStuID = (TextView) findViewById(R.id.tv_stu_id);
        mTvStuName = (TextView) findViewById(R.id.tv_stu_name);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mTvCommitResult = (TextView) findViewById(R.id.tv_commit_result);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    if (mEtRemarks.getVisibility() != View.GONE) {
                        mEtRemarks.setVisibility(View.GONE);
                    }
                } else {
                    if (mEtRemarks.getVisibility() != View.VISIBLE) {
                        mEtRemarks.setVisibility(View.VISIBLE);
                    }
                }

                if (position == 3) {
                    if (mLinearLayoutSelectPic.getVisibility() != View.VISIBLE) {
                        mLinearLayoutSelectPic.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mLinearLayoutSelectPic.getVisibility() != View.GONE) {
                        mLinearLayoutSelectPic.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mEtRemarks = (EditText) findViewById(R.id.et_remarks);
        mBtnSelectPic = (Button) findViewById(R.id.btn_select_pic);
        mIvSelectedPic = (ImageView) findViewById(R.id.iv_selected_pic);
        mBtnSelectPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageConfig imageConfig
                        = new ImageConfig.Builder(new GlideLoader())
                        .steepToolBarColor(getResources().getColor(R.color.blue))
                        .titleBgColor(getResources().getColor(R.color.blue))
                        .titleSubmitTextColor(getResources().getColor(R.color.white))
                        .titleTextColor(getResources().getColor(R.color.white))
                        // 开启单选   （默认为多选）
                        .singleSelect()
                        // 开启拍照功能 （默认关闭）
                        .showCamera()
                        // 拍照后存放的图片路径（默认 /temp/picture） （会自动创建）
                        .filePath("/ImageSelector/Pictures")
                        .build();

                ImageSelector.open(MainActivity.this, imageConfig);   // 开启图片选择器
            }
        });
        mBtnCommit = (Button) findViewById(R.id.btn_commit);
        mBtnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = mSpinner.getSelectedItemPosition();
                if (position == 1 || position == 2 || position == 4) {
                    if (TextUtils.isEmpty(mEtRemarks.getText().toString())) {
                        Toast.makeText(MainActivity.this, "请填写备注信息", Toast.LENGTH_SHORT).show();
                    } else {
                        mBtnCommit.setClickable(false);
                        mBtnCommit.setTextColor(Color.RED);
                        mBtnCommit.setText("正在提交");
                        isTodayCommit();
                    }
                } else if (position == 0) {
                    mBtnCommit.setClickable(false);
                    mBtnCommit.setTextColor(Color.RED);
                    mBtnCommit.setText("正在提交");
                    isTodayCommit();
                } else if (position == 3) {
                    if (TextUtils.isEmpty(mEtRemarks.getText().toString()) || TextUtils.isEmpty(picPath)) {
                        Toast.makeText(MainActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                    } else {
                        mBtnCommit.setClickable(false);
                        mBtnCommit.setTextColor(Color.RED);
                        mBtnCommit.setText("正在提交");
                        isTodayCommit();
                    }
                }
            }
        });
    }

    private void initData() {
        mStrStuID = getSharedPreferences("user_info", MODE_PRIVATE).getString("Stu_ID", "");
        if (mStrStuID != null) {
            mTvStuID.setText(mStrStuID);
        }
        mStrStuName = getSharedPreferences("user_info", MODE_PRIVATE).getString("Stu_Name", "");
        if (mStrStuName != null) {
            mTvStuName.setText(mStrStuName);
        }
    }

    private void Login() {
        final View view = View.inflate(MainActivity.this, R.layout.view_student_login, null);
        buildDialog(1, "学生登陆", null, view);
    }

    private void buildDialog(final int flag, String title, String message, final View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title);
        if (message != null) {
            builder.setMessage(message);
        }
        if (view != null) {
            builder.setView(view);
        }
        switch (flag) {
            case 1:
                final Button mBtnLogin = (Button) view.findViewById(R.id.btn_login);
                mBtnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, null, "正在登陆", true);
                        mBtnLogin.setClickable(false);
                        mBtnLogin.setText("正在登陆");
                        mBtnLogin.setTextColor(Color.RED);
                        EditText mEtStuID = (EditText) view.findViewById(R.id.et_stu_id);
                        final BmobQuery<Student> studentBmobQuery = new BmobQuery<Student>();
                        studentBmobQuery.addWhereEqualTo("Stu_ID", mEtStuID.getText().toString());
                        studentBmobQuery.findObjects(MainActivity.this, new FindListener<Student>() {
                            @Override
                            public void onSuccess(List<Student> list) {
                                if (list.size() == 0) {
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "没有此学号", Toast.LENGTH_SHORT).show();
                                    mBtnLogin.setText("登陆");
                                    mBtnLogin.setTextColor(Color.BLACK);
                                    mBtnLogin.setClickable(true);
                                } else {
                                    progressDialog.dismiss();
                                    mStudent = list.get(0);
                                    objectId = mStudent.getObjectId();
                                    EditText mEtStuPwd = (EditText) view.findViewById(R.id.et_stu_pwd);
                                    if (TextUtils.equals(mStudent.getStu_Pwd(), mEtStuPwd.getText().toString())) {
                                        Toast.makeText(MainActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                        if (TextUtils.equals(mStudent.getStu_DefaultPwdStatus(), "TRUE")) {
                                            buildDialog(2, "提示", "第一次登陆，请设置新密码", null);
                                        } else {
                                            saveStuInfo(mStudent.getStu_ID(), mStudent.getStu_Name(), mStudent.getStu_Class(), objectId);
                                            finish();
                                            startActivity(getIntent());
                                        }
                                    } else {
                                        mBtnLogin.setClickable(true);
                                        Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onError(int i, String s) {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });


                builder.setNeutralButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                builder.setPositiveButton("管理员登陆", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final View view1 = View.inflate(MainActivity.this, R.layout.view_admin_login, null);
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        builder1.setTitle("管理员登陆");
                        builder1.setView(view1);
                        builder1.setCancelable(false);
                        builder1.setNeutralButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        builder1.setPositiveButton("学生登陆", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Login();
                            }
                        });
                        builder1.show();

                        final Button mBtnLogin1 = (Button) view1.findViewById(R.id.btn_login);
                        mBtnLogin1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, null, "正在登陆", true);
                                mBtnLogin1.setClickable(false);
                                mBtnLogin1.setText("正在登陆");
                                mBtnLogin1.setTextColor(Color.RED);
                                EditText mEtAdminID = (EditText) view1.findViewById(R.id.et_admin_id);
                                final EditText mEtAdminPwd = (EditText) view1.findViewById(R.id.et_admin_pwd);
                                BmobQuery<Admin> adminBmobQuery = new BmobQuery<Admin>();
                                adminBmobQuery.addWhereEqualTo("Admin_ID", mEtAdminID.getText().toString());
                                adminBmobQuery.findObjects(MainActivity.this, new FindListener<Admin>() {
                                    @Override
                                    public void onSuccess(List<Admin> list) {
                                        if (list.size() == 0) {
                                            progressDialog.dismiss();
                                            Toast.makeText(MainActivity.this, "没有此管理员", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Admin admin = list.get(0);
                                            if (admin.getAdmin_Pwd().equals(mEtAdminPwd.getText().toString())) {
                                                saveAdminInfo(admin.getAdmin_ID()
                                                        , admin.getAdmin_Name(), admin.getAdmin_Dept(), admin.getObjectId());
                                                Toast.makeText(MainActivity.this, "管理员登陆成功", Toast.LENGTH_SHORT).show();
                                                finish();
                                                startActivity(getIntent());
                                            } else {
                                                Toast.makeText(MainActivity.this, "管理员密码错误", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                                        mBtnLogin1.setClickable(true);
                                    }
                                });
                            }
                        });
                    }
                });

//                view.findViewById(R.id.btn_admin).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        View view1 = View.inflate(MainActivity.this, R.layout.view_admin_login, null);
//                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
//                        builder1.setTitle("管理员登陆");
//                        builder1.setView(view1);
//                        builder1.show();
//                    }
//                });
                break;
            case 2:
                final View view1 = View.inflate(MainActivity.this, R.layout.view_change_pwd, null);
                builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buildDialog(3, "设置新密码", null, view1);
                    }
                });
                break;
            case 3:
                final EditText mEtNewPwd1 = (EditText) view.findViewById(R.id.et_new_pwd1);
                final EditText mEtNewPwd2 = (EditText) view.findViewById(R.id.et_new_pwd2);
                view.findViewById(R.id.btn_change).setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View v) {
                        if (Objects.equals(mEtNewPwd1.getText().toString(), mEtNewPwd2.getText().toString())) {
                            Student student = new Student();
                            student.setStu_Pwd(mEtNewPwd1.getText().toString());
                            student.setStu_DefaultPwdStatus("FALSE");
                            student.update(MainActivity.this, objectId, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(MainActivity.this, "密码修改成功，请牢记！", Toast.LENGTH_LONG).show();
                                    finish();
                                    startActivity(getIntent());
                                    saveStuInfo(mStudent.getStu_ID(), mStudent.getStu_Name(), mStudent.getStu_Class(), objectId);
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "密码输入不一致，请重新输入", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                break;
        }
//        ViewGroup parent = (ViewGroup) view.getParent();
//        if (parent != null) {
//            parent.removeView(view);
//        }
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void saveStuInfo(String stuID, String stuName, String stuClass, String objectId) {
        getSharedPreferences("user_info", MODE_PRIVATE).edit().putString("User_Type", "Student").apply();
        getSharedPreferences("user_info", MODE_PRIVATE).edit().putString("Stu_ID", stuID).apply();
        getSharedPreferences("user_info", MODE_PRIVATE).edit().putString("Stu_Name", stuName).apply();
        getSharedPreferences("user_info", MODE_PRIVATE).edit().putString("Stu_Class", stuClass).apply();
        getSharedPreferences("user_info", MODE_PRIVATE).edit().putString("ObjectId", objectId).apply();

        getSharedPreferences("user_info", MODE_PRIVATE).edit().putBoolean("IsLogin", true).apply();
    }

    private void saveAdminInfo(String adminID, String adminName, String adminDept, String objectId) {
        getSharedPreferences("user_info", MODE_PRIVATE).edit().putString("User_Type", "Admin").apply();
        getSharedPreferences("user_info", MODE_PRIVATE).edit().putString("Admin_ID", adminID).apply();
        getSharedPreferences("user_info", MODE_PRIVATE).edit().putString("Admin_Name", adminName).apply();
        getSharedPreferences("user_info", MODE_PRIVATE).edit().putString("Admin_Dept", adminDept).apply();
        getSharedPreferences("user_info", MODE_PRIVATE).edit().putString("ObjectId", objectId).apply();

        getSharedPreferences("user_info", MODE_PRIVATE).edit().putBoolean("IsLogin", true).apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            // Get Image Path List
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);

            picPath = pathList.get(0);

            Toast.makeText(this, picPath, Toast.LENGTH_SHORT).show();
            Glide.with(MainActivity.this).load(picPath).into(mIvSelectedPic);
        }
    }

    private void showSuccessTip() {
        Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_record:
                startActivity(new Intent(MainActivity.this, MyRecordActivity.class));
                break;
            case R.id.action_about:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
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
                        Toast.makeText(MainActivity.this, "支付宝账号复制成功，正在跳转到支付宝。", Toast.LENGTH_LONG).show();

                        Intent intent = getPackageManager().getLaunchIntentForPackage("com.eg.android.AlipayGphone");
                        startActivity(intent);
                    }
                });
                builder1.setNegativeButton("确定", null);
                builder1.show();
                break;
            case R.id.action_exit:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("提示");
                builder.setMessage("你确定要退出学生账号吗？");
                builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getSharedPreferences("user_info", MODE_PRIVATE).edit().clear().apply();
                        if (getSharedPreferences("user_info", MODE_PRIVATE).getString("Islogin", "已经退出") == "已经退出") {
                            Toast.makeText(MainActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(getIntent());
                        } else {
                            Toast.makeText(MainActivity.this, "请再试一次", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void isTodayCommit() {
        Bmob.getServerTime(MainActivity.this, new GetServerTimeListener() {
            @Override
            public void onSuccess(final long l) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                final String times = formatter.format(new Date(l * 1000L));

                String stu_ID = getSharedPreferences("user_info", MODE_PRIVATE).getString("Stu_ID", "");
                BmobQuery<Record> recordBmobQuery = new BmobQuery<Record>();
                recordBmobQuery.addWhereEqualTo("Stu_ID", stu_ID);
                recordBmobQuery.findObjects(MainActivity.this, new FindListener<Record>() {
                    @Override
                    public void onSuccess(List<Record> list) {
                        if (list.size() == 0) {
                            commitCheck();
                        } else {
                            Collections.reverse(list);
                            Log.d("inSameDay", "onSuccess: " + inSameDay(times, list.get(0).getCreatedAt()));
                            if (inSameDay(times, list.get(0).getCreatedAt())) {
                                Toast.makeText(MainActivity.this, "您今天已经提交审核，无法再次提交", Toast.LENGTH_SHORT).show();
                                mBtnCommit.setText("提交");
                                mBtnCommit.setTextColor(Color.BLACK);
                                mBtnCommit.setClickable(true);
                                mTvCommitResult.setText(mTvTime.getText().toString() + "\n您今天已经提交审核，无法再次提交");
                            } else {
                                commitCheck();
                            }
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public long getTimeDiff(String time1, String time2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long days = 0;
        try {
            Date d1 = df.parse(time1);
            Date d2 = df.parse(time2);
            long diff = d1.getTime() - d2.getTime();//这样得到的差值是微秒级别

            days = diff / (1000 * 60 * 60 * 24);
//            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
//            long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);

//            System.out.println("" + days + "天" + hours + "小时" + minutes + "分");
        } catch (Exception e) {
        }
        return days;

    }

    public static boolean inSameDay(String time1, String time2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = df.parse(time1);
            d2 = df.parse(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d1);
        int year1 = calendar.get(Calendar.YEAR);
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTime(d2);
        int year2 = calendar.get(Calendar.YEAR);
        int day2 = calendar.get(Calendar.DAY_OF_YEAR);

        if ((year1 == year2) && (day1 == day2)) {
            return true;
        }
        return false;
    }

    private void commitCheck() {
        final String mStrStuClass = getSharedPreferences("user_info", MODE_PRIVATE).getString("Stu_Class", "班级错误，请联系开发者");
        if (position == 0) {
            Record record = new Record();
            record.setStu_ID(mStrStuID);
            record.setStu_Name(mStrStuName);
            record.setStudy_Where(mSpinner.getSelectedItem().toString());
            record.setCheck_Status("正在审核");
            record.setWhere_Remarks("图书馆");
            record.setStu_Class(mStrStuClass);
            record.save(MainActivity.this, new SaveListener() {
                @Override
                public void onSuccess() {
                    showSuccessTip();
                    mBtnCommit.setClickable(true);
                    mBtnCommit.setText("提交");
                    mBtnCommit.setTextColor(Color.BLACK);
                    mTvCommitResult.setText(mTvTime.getText().toString() + "\n提交成功\n地点：\n" + mSpinner.getSelectedItem().toString() + "\n备注：\n图书馆");
                }

                @Override
                public void onFailure(int i, String s) {
                    mBtnCommit.setClickable(true);
                    mBtnCommit.setText("提交");
                    mBtnCommit.setTextColor(Color.BLACK);
                    mTvCommitResult.setText(mTvTime.getText().toString() + "\n" + s);
                }
            });
        } else if (position == 1 || position == 2 || position == 4) {
            if (TextUtils.isEmpty(mEtRemarks.getText().toString())) {
                Toast.makeText(MainActivity.this, "请输入备注信息", Toast.LENGTH_SHORT).show();
            } else {
                Record record = new Record();
                record.setStu_ID(mStrStuID);
                record.setStu_Name(mStrStuName);
                record.setStudy_Where(mSpinner.getSelectedItem().toString());
                record.setWhere_Remarks(mEtRemarks.getText().toString());
                record.setCheck_Status("正在审核");
                record.setStu_Class(mStrStuClass);
                record.save(MainActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        showSuccessTip();
                        mBtnCommit.setClickable(true);
                        mBtnCommit.setText("提交");
                        mBtnCommit.setTextColor(Color.BLACK);
                        mTvCommitResult.setText(mTvTime.getText().toString() + "\n提交成功\n地点：\n" + mSpinner.getSelectedItem().toString() + "\n备注：\n" +
                                mEtRemarks.getText().toString());
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        mBtnCommit.setClickable(true);
                        mBtnCommit.setText("提交");
                        mBtnCommit.setTextColor(Color.BLACK);
                        mTvCommitResult.setText(mTvTime.getText().toString() + "\n" + s);
                    }
                });
            }
        } else if (position == 3) {
            if (TextUtils.isEmpty(mEtRemarks.getText().toString())) {
                Toast.makeText(MainActivity.this, "请输入备注信息", Toast.LENGTH_SHORT).show();
            } else {
                if (TextUtils.isEmpty(picPath)) {
                    Toast.makeText(MainActivity.this, "请选择证明照片", Toast.LENGTH_SHORT).show();
                } else {
                    final BmobFile bmobFile = new BmobFile(new File(picPath));
                    bmobFile.uploadblock(MainActivity.this, new UploadFileListener() {
                        @Override
                        public void onSuccess() {
                            String mStrRemarks = mEtRemarks.getText().toString();
                            Record record = new Record();
                            record.setStu_ID(mStrStuID);
                            record.setStu_Name(mStrStuName);
                            record.setStudy_Where(mSpinner.getSelectedItem().toString());
                            record.setWhere_Remarks(mStrRemarks);
                            record.setWhere_Prove(bmobFile.getFileUrl(MainActivity.this));
                            record.setStu_Class(mStrStuClass);
                            record.setCheck_Status("正在审核");
                            record.save(MainActivity.this, new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    showSuccessTip();
                                    mBtnCommit.setClickable(true);
                                    mBtnCommit.setText("提交");
                                    mBtnCommit.setTextColor(Color.BLACK);
                                    mTvCommitResult.setText(mTvTime.getText().toString() + "\n提交成功\n地点：\n" + mSpinner.getSelectedItem().toString() + "\n备注：\n" +
                                            mEtRemarks.getText().toString());
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    mBtnCommit.setClickable(true);
                                    mBtnCommit.setText("提交");
                                    mBtnCommit.setTextColor(Color.BLACK);
                                    mTvCommitResult.setText(mTvTime.getText().toString() + "\n" + s);
                                }
                            });
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                            mTvCommitResult.setText(mTvTime.getText().toString() + "\n" + s);
                        }
                    });
                }
            }
        }
    }
}