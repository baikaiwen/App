package com.clw.bluetooth.util;

import android.os.Environment;

/**
 * 常量公共类
 * */
public class StaticField {
  private static final String TAG = "StaticField";
  
  /** 蓝牙地址*/
  public static String blueAddress="";
  
  public static boolean isConfigPwd=false;

  /** 屏幕大小 */
  public static int SCREEN_HEIGHT = 800;
  public static int SCREEN_WIDHT = 480;

  /** sdcard 路径 */
  public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath() + "/cloud";
  public static final String SDCARD_IMG_TEMP = SDCARD_PATH + "/clwCache/";

  /** token值 */
  public static String TOKEN = "";
  
  /** Url地址*/
//  public static  String HOST="http://122.226.226.20:8082/data/";
  
  public  String IP="";
  
  public String getIP() {
    return this.IP;
  }

  public void setIP(String iP) {
    IP = iP;
  }

  /** 内网地址*/
//  public static String HOST="http://192.168.1.4:8082/data/";
  
  public String HOST="http://"+getIP()+":8082/data/";

  public String getHOST() {
    return this.HOST;
  }

  public void setHOST(String hOST) {
    HOST = hOST;
  }

  /** 1.扫装配单扫码取数接口：*/
  
}
