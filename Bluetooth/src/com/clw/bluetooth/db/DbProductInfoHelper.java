package com.clw.bluetooth.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DbProductInfoHelper extends SQLiteOpenHelper {
  private static final String TAG = "ProductInfoDbHelper";

  /** 数据库版本号 */
  private static final int VERSION = 1;
  /** 数据库名称 */
  private static final String DB_NAME = "huatong.db";

  /** 商品条码表名 */
  private static final String TABLE_NAME = "productno";
  
  /** ip地址表*/
  private static final String TABLE_NAME_IP = "proip";
  
  /** 对应车次应装得数量表*/
  private static final String TABLE_NAME_BW="bwnoqty";
  
  /** 对应车次已装箱好的数量表*/
  private static final String TABLE_NAME_YET="yetbwnoqty";

  public DbProductInfoHelper(Context context) {
    super(context, DB_NAME, null, VERSION);
  }

  public DbProductInfoHelper(Context context, String name, CursorFactory factory) {
    super(context, name, factory, 0);
    // TODO Auto-generated constructor stub
  }

  public DbProductInfoHelper(Context context, String name, CursorFactory factory, int version) {
    super(context, name, factory, version);
    // TODO Auto-generated constructor stub
  }

  @SuppressLint("NewApi")
  public DbProductInfoHelper(Context context, String name, CursorFactory factory, int version,
      DatabaseErrorHandler errorHandler) {
    super(context, name, factory, version, errorHandler);
    // TODO Auto-generated constructor stub
  }

  /**
   * 首次创建数据库时调用 第一次调用后不再调用
   * 
   * */
  @Override
  public void onCreate(SQLiteDatabase sdb) {
    // TODO Auto-generated method stub
    Log.i(TAG, "创建数据库与表...");

    // String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
    // "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
    // + "pro_id VARCHAR(50), " + "productCode VARCHAR(20)," +
    // " showContent VARCHAR(4000) ,"
    // + "price VARCHAR(50)," + "showPictures VARCHAR(400)" + ")";

    /* 商品条码*/
    String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
        + "pro_no VARCHAR(50)"  + ")";
    sdb.execSQL(sql);
   
    /* Ip地址*/
    String sqlIp = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_IP + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
        + "pro_ip VARCHAR(50)"  + ")";
    sdb.execSQL(sqlIp);
    
    /* 对应车次应装*/
    String sqlBw = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_BW + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
        + "f_bw_no VARCHAR(50),"  +"f_qty VARCHAR(50)"+ ")";
    sdb.execSQL(sqlBw);
    
    /* 对应车次已装箱好的*/
    String sqlYet = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_YET + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
        + "f_bw_no VARCHAR(50),"  +"f_qty VARCHAR(50)"+ ")";
    sdb.execSQL(sqlYet);
  }

  /** 此方法用于数据更新表结构 */
  @Override
  public void onUpgrade(SQLiteDatabase sdb, int oldVersion, int newVersion) {
    // TODO Auto-generated method stub
    Log.i(TAG, "更新表结构...");
  }
}
