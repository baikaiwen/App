package com.clw.bluetooth.app;

import com.clw.bluetooth.bean.ProductBean;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class App extends Application {
  private static final String TAG = "App";

  private static SharedPreferences sp = null;

  /** 获取机台号 */
  public static String getMachineNo(Context mContext) {
    sp = mContext.getSharedPreferences("machine_no", MODE_PRIVATE);
    String no = sp.getString("no", "");
    return no;

  }
  
  /** 获取Ip地址*/
  public static String getIp(Context mContext){
    sp=mContext.getSharedPreferences("ip", MODE_PRIVATE);
    String ip=sp.getString("ip", "");
    return ip;
  }

  /** 装配单数据保存本地 */
  public static void saveProduct(Context mContext, ProductBean bean) {
    SharedPreferences sp = mContext.getSharedPreferences("product", MODE_PRIVATE);
    Editor ed = sp.edit();
    ed.clear();
    ed.putString("date", bean.getDate());
    ed.putString("line", bean.getLine());
    ed.putString("loading", bean.getLoading());
    ed.putString("booth", bean.getBooth());
    ed.putString("boothName", bean.getBoothName());
    ed.putString("custmo", bean.getCustmo());
    ed.putString("custmoName", bean.getCustmoName());
    ed.commit();

  }

  /** 取本地上次装配单的数据 */
  public static ProductBean getProduct(Context mContext) {
    SharedPreferences sp = mContext.getSharedPreferences("product", MODE_PRIVATE);
    ProductBean.getInstance().setDate(sp.getString("date", ""));
    ProductBean.getInstance().setBooth(sp.getString("booth", ""));
    ProductBean.getInstance().setBoothName(sp.getString("boothName", ""));
    ProductBean.getInstance().setCustmo(sp.getString("custmo", ""));
    ProductBean.getInstance().setCustmoName(sp.getString("custmoName", ""));
    ProductBean.getInstance().setLine(sp.getString("line", ""));
    return ProductBean.getInstance();

  }
}
