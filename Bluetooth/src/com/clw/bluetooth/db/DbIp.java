package com.clw.bluetooth.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DbIp {
  private static final String TAG = "DbIp";
  
  private DbProductInfoHelper helper;
  private SQLiteDatabase db;

  private static final String TABLE_NAME = "proip";

  private static DbIp dbIp;

  public static DbIp getInstance(Context mContext) {
    if (null == dbIp) {
      dbIp = new DbIp(mContext);
    }
    return dbIp;
  }
  
  public DbIp(Context mContext){
    helper=new DbProductInfoHelper(mContext);
 // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
    // mFactory);
    // 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
    db = helper.getWritableDatabase();
  }
  
  /**
   * 添加一条数据到sql
   * @return 
   * 
   * */
  public synchronized void addIp(String ip){
    db=helper.getWritableDatabase();
    Log.i(TAG, "添加单条数据到Sql....");
    ContentValues cv = new ContentValues();
    cv.put("pro_ip", ip);
    db.insert(TABLE_NAME, null, cv);
    cv.clear();
  }
  
  
  /**
   * 
   * 
   * 通过rawQuery实现的带参数查询
   * */
  public String selectById() {
    db = helper.getWritableDatabase();
    Cursor c = db.rawQuery("select * from " + TABLE_NAME,null);
    String proOldNo="";
    if (c.moveToLast()) {
      proOldNo = c.getString(c.getColumnIndex("pro_ip"));
    }
    return proOldNo;
  }
  
  /**
   * 查询数据库是否为空
   * 
   * */
  
  /** 
   * 判断该条记录是否已存在
   * */
  public boolean judgeId(String ip){
    db = helper.getWritableDatabase();
    Cursor c = db.rawQuery("select * from " + TABLE_NAME
        + " where pro_ip=?", new String[] { ip });
    if (c.moveToFirst()) {
      int i=c.getCount();
      if (i>0) {
        return true;
      }
    }
    return false;
   
  }
  
  /**
   * 删除一条指定数据
   * 
   * */
  public void deleteSingle(String ip){
    db=helper.getWritableDatabase();
//    String sql = "delete from "+TABLE_NAME+" where pro_id=?"+productId;//删除操作的SQL语句
    String whereClause = "pro_ip=?";//删除的条件
    String[] whereArgs = {ip};//删除的条件参数
    Log.i(TAG, "删除一条指定数据");
    db.delete(TABLE_NAME,whereClause,whereArgs);//执行删除
//    db.execSQL(sql);//执行删除操作
  }
  
  /**
   * 
   * 删除所有数据库数据
   * */
  public void deleteTableData() {
    db = helper.getWritableDatabase();
    String sql = "delete  from " + TABLE_NAME;// 删除操作的SQL语句
    Log.i(TAG, "删除sql所有数据...");
    db.execSQL(sql);// 执行删除操作
  }
  

  /**
   * close database 关闭数据库
   */
  public void closeDB() {
    db = helper.getWritableDatabase();
    db.close();
  }
}
