package com.clw.bluetooth.util;

import android.os.Environment;

/**
 * 常量公共类
 * */
public class StaticField {
  private static final String TAG = "StaticField";

  /** 屏幕大小 */
  public static int SCREEN_HEIGHT = 800;
  public static int SCREEN_WIDHT = 480;

  /** sdcard 路径 */
  public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath() + "/cloud";
  public static final String SDCARD_IMG_TEMP = SDCARD_PATH + "/clwCache/";

  /** token值 */
  public static String TOKEN = "";
  
  /** Url地址*/
  public static  String HOST="http://122.226.226.20:8082/data/";

  /** 1.扫装配单扫码取数接口：*/
  
}
