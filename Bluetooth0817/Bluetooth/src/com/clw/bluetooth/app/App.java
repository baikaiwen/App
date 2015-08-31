package com.clw.bluetooth.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class App extends Application {
  private static final String TAG = "App";

  private static SharedPreferences sp = null;

  /** 获取机台号 */
  public static String getMachineNo(Context mContext) {
    sp = mContext.getSharedPreferences("machine_no", MODE_PRIVATE);
    String no = sp.getString("no", "");
    return no;

  }
}
