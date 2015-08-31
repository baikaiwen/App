package com.clw.bluetooth.adapter;

import java.util.List;

import com.clw.bluetooth.R;
import com.clw.bluetooth.bean.ReviewProductBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ReviewAdapter extends BaseAdapter {
  private static final String TAG = "ReviewAdapter";

  private Context mContext;

  private List<ReviewProductBean> beans;

  public ReviewAdapter(Context mContext, List<ReviewProductBean> beans) {
    this.mContext = mContext;
    this.beans = beans;
  }

  @Override
  public int getCount() {
    return beans.size();
  }

  @Override
  public Object getItem(int arg0) {
    return beans.get(arg0);
  }

  @Override
  public long getItemId(int arg0) {
    return 0;
  }

  @Override
  public View getView(int arg0, View convertView, ViewGroup arg2) {
    Views views = null;
    if (null == convertView) {
      views = new Views();
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_review, null);
      views.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
      views.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
      convertView.setTag(views);
    } else {
      views = (Views) convertView.getTag();
    }

    ReviewProductBean bean = beans.get(arg0);
    views.tv_name.setText("品名:" + bean.getBwdsp());
    views.tv_num.setText("数量:" + bean.getQty());
    return convertView;
  }

  private class Views {
    TextView tv_name;
    TextView tv_num;
  }
}
