package com.clw.bluetooth.adapter;

import java.util.List;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * code数据适配器
 * */
public class CodeAdapter extends BaseAdapter {
  private static final String TAG = "CodeAdapter";

  private List<String> codes;

  private Context mContext;

  public CodeAdapter(List<String> codes, Context mContext) {
    this.mContext = mContext;
    this.codes = codes;
  }

  @Override
  public int getCount() {
    return codes.size();
  }

  @Override
  public Object getItem(int arg0) {
    return codes.get(arg0);
  }

  @Override
  public long getItemId(int arg0) {
    return 0;
  }

  @Override
  public View getView(int arg0, View convertView, ViewGroup arg2) {
    Views views = null;
    if (convertView == null) {
      views = new Views();
      convertView = LayoutInflater.from(mContext).inflate(R.layout.simple_list_item_1, null);
      views.tv_code = (TextView) convertView.findViewById(R.id.text1);
      convertView.setTag(views);
    } else {
      views = (Views) convertView.getTag();
    }

    views.tv_code.setText(codes.get(arg0).toString());

    return convertView;
  }

  private class Views {
    TextView tv_code;
  }
}
