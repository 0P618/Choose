package com.goldenration.sdbi.xx.timechoose.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenration.sdbi.xx.timechoose.R;
import com.goldenration.sdbi.xx.timechoose.bean.Admin;
import com.goldenration.sdbi.xx.timechoose.bean.Record;

import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Kiuber on 2016/8/4.
 */

public class MyRecordAdapter extends BaseAdapter {

    private Context context;
    private List<Record> list;
    private LayoutInflater mInflater;

    public MyRecordAdapter(Context context, List<Record> list) {
        this.context = context;
        this.list = list;
        Collections.reverse(this.list);
        this.mInflater = LayoutInflater.from(context);
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
            convertView = mInflater.inflate(R.layout.item_listview_my_record, null);
            viewHolder = new ViewHolder();
            viewHolder.initView(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.initData(position);

        return convertView;
    }

    class ViewHolder {
        TextView mTvStudyWhere;
        TextView mTvName;
        TextView mTvTime;
        ImageView mIvStatus;
        TextView mTvStatus;
        LinearLayout mLlCheckStatus;

        void initView(View convertView) {
            mTvName = (TextView) convertView.findViewById(R.id.tv_name);
            mTvTime = (TextView) convertView.findViewById(R.id.tv_time);
            mTvStudyWhere = (TextView) convertView.findViewById(R.id.tv_where);

            mIvStatus = (ImageView) convertView.findViewById(R.id.iv_check_status);
            mTvStatus = (TextView) convertView.findViewById(R.id.tv_check_status);
            mLlCheckStatus = (LinearLayout) convertView.findViewById(R.id.ll_check_status);
        }

        void initData(final int position) {
            mTvName.setText(getItem(position).getStu_Name());
            mTvTime.setText(getItem(position).getCreatedAt());
            mTvStudyWhere.setText(getItem(position).getStudy_Where());

            switch (getItem(position).getCheck_Status()) {
                case "正在审核":
                    mIvStatus.setBackgroundResource(R.drawable.ic_chrome_reader_mode_black_24dp);
                    mTvStatus.setTextColor(context.getResources().getColor(R.color.check_status_ing));
                    mTvStatus.setText(getItem(position).getCheck_Status());
//                    Glide.with(context).load(R.drawable.ic_chrome_reader_mode_black_24dp).into(mIvStatus);
                    break;
                case "审核通过":
                    mIvStatus.setBackgroundResource(R.drawable.ic_check_circle_black_24dp);
                    mTvStatus.setTextColor(context.getResources().getColor(R.color.check_status_success));
                    mTvStatus.setText(getItem(position).getCheck_Status());
                    BmobQuery<Admin> adminBmobQuery = new BmobQuery<Admin>();
                    adminBmobQuery.addWhereEqualTo("objectId", getItem(position).getCheck_By_Who());
                    adminBmobQuery.findObjects(context, new FindListener<Admin>() {
                        @Override
                        public void onSuccess(List<Admin> list) {
                            if (list.size() == 0) {
                                mTvStatus.setText(mTvStatus.getText() + "\n审核人获取失败");
                            } else {
                                mTvStatus.setText(mTvStatus.getText() + "\n审核人：" + list.get(0).getAdmin_Name());
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(context, "审核人查询失败，请一定与开发者联系，联系方式请点击关于", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case "审核失败":
                    mIvStatus.setBackgroundResource(R.drawable.ic_cancel_black_24dp);
                    mTvStatus.setTextColor(context.getResources().getColor(R.color.check_status_fail));
                    mTvStatus.setText(getItem(position).getCheck_Status());
                    break;
                default:
                    mTvStatus.setText("未知状态");
                    break;
            }
        }
    }
}
