package com.goldenration.sdbi.xx.timechoose.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goldenration.sdbi.xx.timechoose.R;
import com.goldenration.sdbi.xx.timechoose.bean.Record;

import java.util.List;
import java.util.Random;

import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Kiuber on 2016/8/4.
 */

public class AllRecordAdapter extends BaseAdapter {

    private Context context;
    private List<Record> list;

    private int[] timeLineColors =

            {
                    R.color.lightyellow, R.color.mistyrose, R.color.darkorange,
                    R.color.lightgoldenrodyellow, R.color.azure, R.color.burlywood,
                    R.color.tan, R.color.firebrick, R.color.darkorchid, R.color.lightskyblue,
                    R.color.mediumslateblue, R.color.cornflowerblue, R.color.limegreen,
                    R.color.springgreen, R.color.mediumblue,
            };

    public AllRecordAdapter(Context context, List<Record> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Record getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_listview_all_record, null);
            viewHolder.initView(convertView, position);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.initData(position);

        return convertView;
    }

    class ViewHolder {
        TextView mTvStuName;
        TextView mTvStuClass;
        TextView mTvStudyWhere;
        TextView mTvWhereRemarks;
        TextView mIvCheckSuccess;
        TextView mIvCheckFail;
        TextView mTvCheckStatus1;
        TextView mTvCheckStatus2;
        LinearLayout mLlCheck;
        ImageView mIvTimeLine;
        Button mBtnShowProve;

        void initView(View convertView, final int position) {
            mTvStuName = (TextView) convertView.findViewById(R.id.tv_stu_name);
            mTvStuClass = (TextView) convertView.findViewById(R.id.tv_stu_class);
            mTvStudyWhere = (TextView) convertView.findViewById(R.id.tv_study_where);
            mTvWhereRemarks = (TextView) convertView.findViewById(R.id.tv_where_remarks);
            mIvCheckSuccess = (TextView) convertView.findViewById(R.id.tv_check_success);
            mIvCheckFail = (TextView) convertView.findViewById(R.id.tv_check_fail);
            mTvCheckStatus1 = (TextView) convertView.findViewById(R.id.tv_check_status_1);
            mTvCheckStatus2 = (TextView) convertView.findViewById(R.id.tv_check_status_2);
            mLlCheck = (LinearLayout) convertView.findViewById(R.id.ll_check);
            mIvTimeLine = (ImageView) convertView.findViewById(R.id.iv_time_line);
            mBtnShowProve = (Button) convertView.findViewById(R.id.btn_show_prove);
            mIvCheckSuccess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCheckTip(position, mTvCheckStatus1, mTvCheckStatus2, mLlCheck, true);
                }
            });
            mIvCheckFail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCheckTip(position, mTvCheckStatus1, mTvCheckStatus2, mLlCheck, false);
                }
            });
        }

        @TargetApi(Build.VERSION_CODES.M)
        void initData(final int position) {
            mTvStuName.setText("姓名：" + getItem(position).getStu_Name());
            mTvStuClass.setText("班级：" + getItem(position).getStu_Class());
            mTvStudyWhere.setText("地点：" + getItem(position).getStudy_Where());
            mTvWhereRemarks.setText("备注：" + getItem(position).getWhere_Remarks());

            final String mStrProve = getItem(position).getWhere_Prove();

            if (mStrProve != null) {
                mBtnShowProve.setVisibility(View.VISIBLE);
                mBtnShowProve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = View.inflate(context, R.layout.dialog_view_record_prove, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("查看证明");
                        builder.setView(view);
                        builder.setPositiveButton("确定", null);
                        builder.show();
                        Glide.with(context).load(mStrProve).into((ImageView) view.findViewById(R.id.iv_record_prove));
                    }
                });
            }
            Random random = new Random();
            int i = random.nextInt(14);
            mIvTimeLine.setBackgroundColor(context.getResources().getColor(timeLineColors[i]));
            String mStrCheckStatus = getItem(position).getCheck_Status();
            if (!(mStrCheckStatus.equals("正在审核"))) {
                Drawable img_on, img_off;
                Resources res = context.getResources();
                img_off = res.getDrawable(R.drawable.ic_drafts_black_24dp);
                //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
                mTvCheckStatus1.setCompoundDrawables(null, img_off, null, null); //设置左图标
                mTvCheckStatus1.setText("已审核");
                mTvCheckStatus1.setTextColor(context.getResources().getColor(R.color.check_status_success));
                mLlCheck.setVisibility(View.GONE);
                mTvCheckStatus2.setVisibility(View.VISIBLE);

                if (mStrCheckStatus.equals("审核失败")) {
                    Drawable img_off1;
                    img_off1 = res.getDrawable(R.drawable.ic_cancel_black_24dp);
                    //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                    img_off1.setBounds(0, 0, img_off1.getMinimumWidth(), img_off1.getMinimumHeight());
                    mTvCheckStatus2.setCompoundDrawables(null, img_off1, null, null); //设置Top图标
                    mTvCheckStatus2.setText("审核失败");
                }

                mTvCheckStatus2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("提示");
                        builder.setMessage("重新审核该选课？");
                        builder.setPositiveButton("重新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mTvCheckStatus1.setText("正在重新审核");
                                mTvCheckStatus1.setTextColor(Color.LTGRAY);
                                mTvCheckStatus2.setVisibility(View.GONE);
                                mLlCheck.setVisibility(View.VISIBLE);
                            }
                        });
                        builder.setNegativeButton("取消", null);
                        builder.show();
                    }
                });

            }
        }
    }

    private void showCheckTip(final int position, final TextView status1, final TextView status2, final LinearLayout status3, final boolean flag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");

        if (flag) {
            builder.setMessage(Html.fromHtml("确定要让<font color=blue>" + getItem(position).getStu_Name() + "</font>" + "<font color=green>"
                    + getItem(position).getStudy_Where() + "</font>" + "审核通过吗？"));
        } else {
            builder.setMessage(Html.fromHtml("确定要让<font color=blue>" + getItem(position).getStu_Name() + "</font>" + "<font color=green>"
                    + getItem(position).getStudy_Where() + "</font>" + "审核失败吗？"));
        }
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Record record = new Record();
                if (!flag) {
                    record.setCheck_Status("审核失败");
                } else {
                    record.setCheck_Status("审核通过");
                }
                String mStrCheckByWho = context.getSharedPreferences("user_info", Context.MODE_PRIVATE).getString("ObjectId", "");
                record.setCheck_By_Who(mStrCheckByWho);
                record.update(context, getItem(position).getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context, "记录操作成功", Toast.LENGTH_SHORT).show();
                        Drawable img_on, img_off;
                        Resources res = context.getResources();
                        img_off = res.getDrawable(R.drawable.ic_drafts_black_24dp);
                        //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                        img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
                        status1.setCompoundDrawables(null, img_off, null, null); //设置Top图标
                        status1.setText("已审核");
                        status1.setTextColor(context.getResources().getColor(R.color.check_status_success));
                        status2.setVisibility(View.VISIBLE);
                        if (!flag) {
                            Drawable img_off1;
                            img_off1 = res.getDrawable(R.drawable.ic_cancel_black_24dp);
                            //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
                            img_off1.setBounds(0, 0, img_off1.getMinimumWidth(), img_off1.getMinimumHeight());
                            status2.setCompoundDrawables(null, img_off1, null, null); //设置Top图标
                            status2.setText("审核失败");
                        }
                        status3.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.show();
    }
}
