package com.goldenration.sdbi.xx.timechoose.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
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
import com.goldenration.sdbi.xx.timechoose.bean.Student;
import com.goldenration.sdbi.xx.timechoose.util.GlideLoader;
import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

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
    private String[] mStrWhere =
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "620ee288644d1af7c8ebd6f951a0e2a4");
        setContentView(R.layout.activity_main);
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
        if (!(getSharedPreferences("stu_info", MODE_PRIVATE).getBoolean("IsLogin", false))) {
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
    }

    private void initData() {
        String mStrStuID = getSharedPreferences("stu_info", MODE_PRIVATE).getString("Stu_ID", "");
        if (mStrStuID != null) {
            mTvStuID.setText(mStrStuID);
        }
        String mStrStuName = getSharedPreferences("stu_info", MODE_PRIVATE).getString("Stu_Name", "");
        if (mStrStuName != null) {
            mTvStuName.setText(mStrStuName);
        }
    }

    private void Login() {
        final View view = View.inflate(MainActivity.this, R.layout.view_login, null);
        buildDialog(1, "登陆", null, view);
    }

    private void buildDialog(int flag, String title, String message, final View view) {
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
                Button mBtnLogin = (Button) view.findViewById(R.id.btn_login);
                mBtnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText mEtStuID = (EditText) view.findViewById(R.id.et_stu_id);
                        final BmobQuery<Student> studentBmobQuery = new BmobQuery<Student>();
                        studentBmobQuery.addWhereEqualTo("Stu_ID", mEtStuID.getText().toString());
                        studentBmobQuery.findObjects(MainActivity.this, new FindListener<Student>() {

                            @Override
                            public void onSuccess(List<Student> list) {
                                mStudent = list.get(0);
                                if (list.size() == 0) {
                                    Toast.makeText(MainActivity.this, "没有此学号", Toast.LENGTH_SHORT).show();
                                } else {
                                    objectId = mStudent.getObjectId();
                                    EditText mEtStuPwd = (EditText) view.findViewById(R.id.et_stu_pwd);
                                    if (TextUtils.equals(mStudent.getStu_Pwd(), mEtStuPwd.getText().toString())) {
                                        Toast.makeText(MainActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                        if (TextUtils.equals(mStudent.getStu_DefaultPwdStatus(), "TRUE")) {
                                            buildDialog(2, "提示", "请设置新密码", null);
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
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
                final EditText mEtNewPwd = (EditText) view.findViewById(R.id.et_new_pwd);
                view.findViewById(R.id.btn_change).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Student student = new Student();
                        student.setStu_Pwd(mEtNewPwd.getText().toString());
                        student.update(MainActivity.this, objectId, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(MainActivity.this, "密码修改成功，请牢记！", Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(getIntent());
                                saveUserInfo(mStudent.getStu_ID(), mStudent.getStu_Name());
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                            }
                        });
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

    private void saveUserInfo(String stuID, String stuName) {
        getSharedPreferences("stu_info", MODE_PRIVATE).edit().putString("Stu_ID", stuID).commit();
        getSharedPreferences("stu_info", MODE_PRIVATE).edit().putString("Stu_Name", stuName).commit();

        getSharedPreferences("stu_info", MODE_PRIVATE).edit().putBoolean("IsLogin", true).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            // Get Image Path List
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);

            for (String path : pathList) {
                Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
                Glide.with(MainActivity.this).load(path).into(mIvSelectedPic);
            }
        }
    }
}
