package com.clw.bluetooth.service;

import com.android.pc.ioc.internet.AjaxCallBack;
import com.android.pc.ioc.internet.FastHttp;
import com.android.pc.ioc.internet.ResponseEntity;
import com.clw.bluetooth.db.DbIp;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UpNetService {
  private static final String TAG = "UpNetService";

  private static UpNetService instance = null;

  public static UpNetService getInstance() {
    if (null == instance) {
      instance = new UpNetService();
    }
    return instance;
  }

  /**
   * @category 根据装车单号和摊位获取对应车次中对应摊位的品名和数量
   * 
   * @param 装配单码
   *          ,摊位
   * 
   *          select f_bw_no,SUM(f_qty) f_qty from un_zxdetail_ord where
   *          f_zcd_no='ZC1508000319' and f_cus_no='101076' group by f_bw_no
   * 
   *          http://122.226.226.20:8082/data/open?sql=select+f_bw_no%2CSUM(
   *          f_qty)+f_qty+from+un_zxdetail_ord+where+f_zcd_no%3D
   *          'ZC1508000319'+and+f_cus_no%3D'101076'+group+by+f_bw_no&token=&pagesize=5&pageinde
   *          x = 0
   * */
  public void getNameAndNum(Context mContext, final Handler mHandler, String f_zcd_no, String f_cus_no) {
    String url = DbIp.getInstance(mContext).selectById()
        + "open?sql=select+f_bw_no%2CSUM(f_qty)+f_qty+from+un_zxdetail_ord+where+f_zcd_no%3D'" + f_zcd_no
        + "'+and+f_cus_no%3D'" + f_cus_no + "'+group+by+f_bw_no&token=&pagesize=5&pageindex=0";
    FastHttp.ajaxGet(url, new AjaxCallBack() {

      @Override
      public void callBack(ResponseEntity status) {
        switch (status.getStatus()) {
          case FastHttp.result_ok:
            Log.i(TAG, "对应车次中的品名和数量:" + status.getContentAsString());
            Message msg = Message.obtain();
            msg.arg1 = 11;
            msg.obj = status.getContentAsString();
            mHandler.sendMessage(msg);
            break;
          case FastHttp.result_net_err:
            Message m = Message.obtain();
            m.arg1 = 0;
            mHandler.sendMessage(m);
            break;

          default:
            break;
        }
      }

      @Override
      public boolean stop() {
        return false;
      }
    });
  }

  /**
   * @category 根据装车单号和摊位获取对应车次中对应摊位已经装箱好的品名和数量
   * 
   * @params 装车单号,摊位
   * 
   *         select b.f_bw_no,SUM(b.f_qty) f_qty from un_zxmain a join
   *         un_zxdetail b on a.f_zx_txm=b.f_zx_txm where
   *         a.f_zcd_no='ZC1508000319' and a.f_cus_no='101076' group by
   *         b.f_bw_no
   * */
  public void getHasNameAndNum(Context mContext, final Handler mHander, String f_zcd_no, String f_cus_no) {
    String url = DbIp.getInstance(mContext).selectById()
        + "open?sql=select+b.f_bw_no%2CSUM(b.f_qty)+f_qty+from+un_zxmain+a+join+un_zxdetail+b+on%0Aa.f_zx_txm%3Db.f_zx_txm+where+a.f_zcd_no%3D'"
        + f_zcd_no + "'+and+a.f_cus_no%3D'" + f_cus_no + "'%0Agroup+by+b.f_bw_no&token=&pagesize=5&pageindex=0";
    FastHttp.ajaxGet(url, new AjaxCallBack() {

      @Override
      public void callBack(ResponseEntity status) {
        switch (status.getStatus()) {
          case FastHttp.result_ok:
            Log.i(TAG, "已经装箱好的返回:" + status.getContentAsString());
            Message msg = Message.obtain();
            msg.arg1 = 22;
            msg.obj = status.getContentAsString();
            mHander.sendMessage(msg);
            break;
          case FastHttp.result_net_err:
            Message m = Message.obtain();
            m.arg1 = 0;
            mHander.sendMessage(m);
            break;
          default:
            break;
        }
      }

      @Override
      public boolean stop() {
        return false;
      }
    });
  }

  /**
   * @category 在扫描商品条码时，根据扫描对应的商品条码，从 ERP系统中获取到该条码对应的品名和数量
   * 
   * @params 商品条码
   * 
   *         http://122.226.226.20:8082/data/open?sql=select+f_bw_no%2Cf_qty+
   *         from+pbcpgbinfo+where+f_txm%3D''&token=&pagesize=5&pageindex=0
   * */
  public void getBWnameAndNum(Context mContext, final Handler mHandler, String f_txm) {
    String url = DbIp.getInstance(mContext).selectById()
        + "open?sql=select+f_bw_no%2Cf_qty+from+pbcpgbinfo+where+f_txm%3D'" + f_txm + "'&token=&pagesize=5&pageindex=0";
    FastHttp.ajaxGet(url, new AjaxCallBack() {

      @Override
      public void callBack(ResponseEntity status) {
        switch (status.getStatus()) {
          case FastHttp.result_ok:
            Log.i(TAG, "商品条码返回:" + status.getContentAsString());
            Message msg = Message.obtain();
            msg.arg1 = 22;
            msg.obj = status.getContentAsString();
            mHandler.sendMessage(msg);
            break;
          case FastHttp.result_net_err:
            Message m = Message.obtain();
            m.arg1 = 0;
            mHandler.sendMessage(m);
            break;
          default:
            break;
        }
      }

      @Override
      public boolean stop() {
        return false;
      }
    });

  }
}
