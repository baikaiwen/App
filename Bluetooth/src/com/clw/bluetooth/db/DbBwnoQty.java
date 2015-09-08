package com.clw.bluetooth.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 对应车次要装的数据保存
 * @category sql
 * */
public class DbBwnoQty {
  private static final String TAG = "DbBwnoQty";
  
  private DbProductInfoHelper helper;
  private SQLiteDatabase db;

  private static final String TABLE_NAME = "bwnoqty";

  private static DbBwnoQty dbBwnoQty;
  
  public static DbBwnoQty getInstance(Context mContext) {
    if (null == dbBwnoQty) {
      dbBwnoQty = new DbBwnoQty(mContext);
    }
    return dbBwnoQty;
  }
  
  public DbBwnoQty(Context mContext){
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
  public synchronized void add(String f_bw_no,String f_qty){
    db=helper.getWritableDatabase();
    Log.i(TAG, "添加单条数据到Sql....");
    ContentValues cv = new ContentValues();
    cv.put("f_bw_no", f_bw_no);
    cv.put("f_qty", f_qty);
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
      proOldNo = c.getString(c.getColumnIndex("f_bw_no"));
    }
    return proOldNo;
  }
  
  /**
   * @ 查询所有
   * 
   * */
  public Cursor selectAll(){
    db = helper.getWritableDatabase();
    Cursor c = db.rawQuery("select * from " + TABLE_NAME,null);
    return c;
  }
  
  
  /** 
   * 判断该条记录是否已存在
   * */
  public boolean judgeId(String f_bw_no){
    db = helper.getWritableDatabase();
    Cursor c = db.rawQuery("select * from " + TABLE_NAME
        + " where f_bw_no=?", new String[] { f_bw_no });
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
  public void deleteSingle(String f_bw_no){
    db=helper.getWritableDatabase();
//    String sql = "delete from "+TABLE_NAME+" where pro_id=?"+productId;//删除操作的SQL语句
    String whereClause = "f_bw_no=?";//删除的条件
    String[] whereArgs = {f_bw_no};//删除的条件参数
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
