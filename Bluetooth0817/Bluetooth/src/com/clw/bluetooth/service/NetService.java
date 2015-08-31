package com.clw.bluetooth.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.pc.ioc.internet.AjaxCallBack;
import com.android.pc.ioc.internet.FastHttp;
import com.android.pc.ioc.internet.InternetConfig;
import com.android.pc.ioc.internet.ResponseEntity;
import com.clw.bluetooth.bean.ProductBean;
import com.clw.bluetooth.bean.ReviewProductBean;
import com.clw.bluetooth.util.StaticField;
import com.clw.bluetooth.util.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NetService {
  private static final String TAG = "NetService";

  private static NetService netService = null;

  public static NetService getInstance() {
    if (null == netService) {
      netService = new NetService();
    }
    return netService;
  }

  /**
   * 1.扫装配单扫码取数接口： select f_zc_dsp, f_zcd_no,f_zcd_date, f_cus_no,f_ywy from
   * un_zxdetail_ord where f_txm=‘配货条码’ f_zc_dsp是线路， f_zcd_no是车号, f_cus_no是摊位，
   * f_ywy是客户,f_zcd_date 日期
   * */
  // http://122.226.226.20:8082/data/open?sql=select+*+from+un_zxdetail_ord+where+f_txm%3D'ZC15080003151'&token=&pagesize=5&pageindex=0
  public void getFristData(String code, final Handler handler) {
    String url = StaticField.HOST
        + "open?sql=select+f_zc_dsp%2C+f_zcd_no%2Cf_zcd_date%2C+f_cus_no%2Cf_ywy+from+un_zxdetail_ord++where+f_txm%3D'"
        + code + "'&token=&pagesize=5&pageindex=0";
    // String url=StaticField.HOST +
    // "open?sql=select+f_zcd_date+as+%5B%E8%A3%85%E9%85%8D%E6%97%A5%E6%9C%9F%5D%2Cf_zc_dsp+%5B%E7%BA%BF%E8%B7%AF%5D%2C+f_zcd_no+%5B%E8%A3%85%E8%BD%A6%E5%8F%B7%5D%2C+f_cus_no+%5B%E6%91%8A%E4%BD%8D%5D%2Cf_ywy%5B%E5%AE%A2%E6%88%B7%5D+from+un_zxdetail_ord+where+f_txm%3D'ZC15080003151'&token=&pagesize=5&pageindex=0";
    FastHttp.ajaxGet(url, new AjaxCallBack() {

      @Override
      public void callBack(ResponseEntity status) {
        switch (status.getStatus()) {
          case FastHttp.result_ok:
            Log.i(TAG, status.getContentAsString());
            try {
              // 解析
              JSONObject obj = new JSONObject(status.getContentAsString());
              String statu = obj.getString("status");
              if (Tools.judgeStringEquals(statu, "0")) {
                ProductBean.getInstance().setCount(obj.isNull("count") ? "" : obj.getString("count"));
                JSONArray array = obj.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                  String date=array.getJSONObject(i).getString("f_zcd_date").substring(0, 10);
                  ProductBean.getInstance().setDate(date);
                  ProductBean.getInstance().setLine(array.getJSONObject(i).getString("f_zc_dsp"));
                  ProductBean.getInstance().setLoading(array.getJSONObject(i).getString("f_zcd_no"));
                  ProductBean.getInstance().setBooth(array.getJSONObject(i).getString("f_cus_no"));
                  ProductBean.getInstance().setCustmo(array.getJSONObject(i).getString("f_ywy"));
                }
                Message msg = Message.obtain();
                msg.arg1 = 1;
                handler.sendMessage(msg);
              } else {
                Message msg = Message.obtain();
                msg.arg1 = 10;
                msg.obj = "未能正确请求";
                handler.sendMessage(msg);
              }
            } catch (JSONException e) {
              Log.e(TAG, "", e);
            }

            break;
          case FastHttp.result_net_err: // 网络错误
            Message msg = Message.obtain();
            msg.arg1 = 0;
            handler.sendMessage(msg);
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
    // FastHttp.get();
  }

  /**
   * 复核产品信息 3.数据复核 select f_bw_no,sum(f_qty) from pbcpgbinfo where f_txm
   * in(‘条码1’,’ 条码2’,’ 条码3’) ；其中f_bw_no是品名，f_qty是数量
   * */
  public void reviewProduct(final Handler handler, String[] product,final Context mContext) {
    Log.i(TAG, Arrays.asList(product).toString());
    /* 拼接字符 */
    List<String> b = new ArrayList<String>();
    for (String str : product) {
      b.add("'" + str + "',");
    }
    /* 把字符串数组转为一个字符串 */
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < b.size(); i++) {
      sb.append(b.get(i));
    }

    String newStr = sb.toString();
    String productStr = newStr.substring(0, newStr.length() - 1);
    Log.i(TAG, newStr + "去掉最后一个字符:" + productStr);
//    String url = StaticField.HOST + "open?sql=select+f_bw_no%2Csum(f_qty)+f_qty+from+pbcpgbinfo+where+f_txm+in("
//        + productStr + ")+group+by+f_bw_no&token=&pagesize=5&pageindex=0";
    String url=StaticField.HOST+"open?sql=select+f_bw_no%2Csum(f_qty)+f_qty%2CSUM(f_weight)+f_weight+from+pbcpgbinfo+where+f_txm+in("
        + productStr + ")+group+by+f_bw_no&token=&pagesize=5&pageindex=0";
    FastHttp.ajaxGet(url, new AjaxCallBack() {

      @Override
      public void callBack(ResponseEntity status) {
        switch (status.getStatus()) {
          case FastHttp.result_ok:
            Log.i(TAG, status.getContentAsString());
            try {
              List<ReviewProductBean> reviewProductBeans = new ArrayList<ReviewProductBean>();
              JSONObject obj = new JSONObject(status.getContentAsString());
              String stat = obj.getString("status");
              if (Tools.judgeStringEquals("0", stat)) {
                JSONArray array = obj.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                  ReviewProductBean bean = new ReviewProductBean();
                  bean.setBwNo(array.getJSONObject(i).getString("f_bw_no"));
                  bean.setQty(array.getJSONObject(i).getString("f_qty"));
                  bean.setWeight(array.getJSONObject(i).getString("f_weight"));
                  reviewProductBeans.add(bean);
                }
                SharedPreferences sp=mContext.getSharedPreferences("jsonCache", mContext.MODE_APPEND);
                Editor ed=sp.edit();
                ed.clear();
                ed.putString("json", status.getContentAsString());
                ed.commit();
                

                Message msg = Message.obtain();
                msg.arg1 = 2;
                msg.obj = reviewProductBeans;
                handler.sendMessage(msg);
              } else {
                Message msg = Message.obtain();
                msg.arg1 = 10;
                msg.obj = "未能正确请求";
                handler.sendMessage(msg);
              }
            } catch (JSONException e) {
              Log.e(TAG, "", e);
            }
            break;
          case FastHttp.result_net_err:
            Message msg = Message.obtain();
            msg.arg1 = 0;
            handler.sendMessage(msg);
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
   * 写入明细表 明细表insert into
   * un_zxdetail(f_zx_txm,f_seq_no,f_txm,f_bw_no,f_qty,f_weight) f_zx_txm
   * :箱条码,f_seq_no行号,f_txm[产品条码],f_bw_no[品名],f_qty[数量],f_weight[重量]
   * */
  public void insertZxDetails(final Handler handler, String cartonCode, String[] product, String[] proname,
      String[] pronum) {
    /* 拼接字符 条形码 */
    List<String> b = new ArrayList<String>();
    for (String str : product) {
      b.add("'" + str + "',");
    }
    /* 把字符串数组转为一个字符串 */
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < b.size(); i++) {
      sb.append(b.get(i));
    }

    String newStr = sb.toString();
    String productStr = newStr.substring(0, newStr.length() - 1);

    /* 拼接字符 名字 */
    List<String> c = new ArrayList<String>();
    for (String strname : proname) {
      c.add("'" + strname + "',");
    }
    /* 把字符串数组转为一个字符串 */
    StringBuffer sbName = new StringBuffer();
    for (int i = 0; i < c.size(); i++) {
      sbName.append(c.get(i));
    }

    String newStrName = sbName.toString();
    Log.i(TAG, "条形码"+newStrName);
    String productStrName = newStrName.substring(0, newStrName.length() - 1);
    /* 拼接字符 数量 */
    List<String> d = new ArrayList<String>();
    for (String strNum : pronum) {
      d.add("'" + strNum + "',");
    }
    /* 把字符串数组转为一个字符串 */
    StringBuffer sbNum = new StringBuffer();
    for (int i = 0; i < d.size(); i++) {
      sbNum.append(d.get(i));
    }

    String newStrNum = sbNum.toString();
    String productStrNum = newStrNum.substring(0, newStrNum.length() - 1);

    String url = StaticField.HOST
        + "exec?sql=insert+into+un_zxdetail(f_zx_txm%2Cf_txm%2Cf_bw_no%2Cf_qty%2Cf_weight)+Select+%27" + cartonCode
        + "%27+f_zx_txm%2Cf_txm%2Cf_bw_no%2Cf_qty%2Cf_weight+from+pbcpgbinfo+where+f_txm+in(" + productStr + "%2c"
        + productStrName + "%2c" + productStrNum + ")+&token=";
    FastHttp.ajaxGet(url, new AjaxCallBack() {

      @Override
      public void callBack(ResponseEntity status) {
        switch (status.getStatus()) {
          case FastHttp.result_ok:
            Log.i(TAG, "明细表:" + status.getContentAsString());
            break;
          case FastHttp.result_net_err:
            Message msg = Message.obtain();
            msg.arg1 = 0;
            handler.sendMessage(msg);
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
   * 更新状态 update pbcpgbinfo set f_fh_flag=1 where f_txm
   * ('产品条码1','产品条码2','产品条码3')
   * 
   * */
  public void update(final Handler handler, String[] product) {
    /* 拼接字符 条形码 */
    List<String> b = new ArrayList<String>();
    for (String str : product) {
      b.add("'" + str + "',");
    }
    /* 把字符串数组转为一个字符串 */
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < b.size(); i++) {
      sb.append(b.get(i));
    }

    String newStr = sb.toString();
    String productStr = newStr.substring(0, newStr.length() - 1);

    String url = StaticField.HOST + "exec?sql=update+pbcpgbinfo+set+f_fh_flag%3D1+where+f_txm+in+(" + productStr
        + ")&token=";
    FastHttp.ajaxGet(url, new AjaxCallBack() {

      @Override
      public void callBack(ResponseEntity status) {
        switch (status.getStatus()) {
          case FastHttp.result_ok:
            Log.i(TAG, "更新状态" + status.getContentAsString());
            break;
          case FastHttp.result_net_err:
            Message msg = Message.obtain();
            msg.arg1 = 0;
            handler.sendMessage(msg);
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
   * 写入主表 写入主表insert into un_zxmain(f_zx_txm,f_zcd_no, f_zc_dsp, f_ywy,
   * f_cus_no, f_zx_date,f_chk_flag) Select [箱条码],[车号],[线路],[客户],[摊位],[装配日期],1)
   * */
  public void insertZxMain(final Handler handler, String cartonCode, String carNo, String dspNo, String cust,
      String cusNo, String zxDate,String weight,String flag) {
//    String url = StaticField.HOST
//        + "exec?sql=insert+into+un_zxmain(f_zx_txm%2Cf_zcd_no%2C+f_zc_dsp%2C+f_ywy%2C+f_cus_no%2C+f_zx_date%2Cf_chk_flag)%0A"
//        + "Select+"
//        + "%27"+081415980001+"%27%2C%27"+ZC1508000315+"%27%2C%27"
//        +%E4%B9%891+"%27%2C%27"+1001+"%27%2C%27"
//        +101001+"%27%2C%27"+2015-08-14+"%27%2C"+1+"&token=";
    String url = null;
    try {
      url = StaticField.HOST
          + "exec?sql=insert+into+un_zxmain(f_zx_txm%2Cf_zcd_no%2C+f_zc_dsp%2C+f_ywy%2C+f_cus_no%2C+f_zx_date%2Cf_sum_wei%2Cf_chk_flag)%0A"
          + "Select+"
          + "%27"+cartonCode+"%27%2C%27"+carNo+"%27%2C%27"
          +dspNo+"%27%2C%27"+cust+"%27%2C%27"
          +cusNo+"%27%2C%27"+zxDate+"%27%2C%27"+weight+"%27%2C"+1+"&token=";
      Log.i(TAG," 请求主表:"+url);
    } catch (Exception e) {
      Log.e(TAG,"",e);
    }
    
    InternetConfig config=new InternetConfig();
    HashMap<String, Object> head=new HashMap<>();
    head.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
//    request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
    config.setHead(head);
    FastHttp.ajaxGet(url, config,new AjaxCallBack() {

      @Override
      public void callBack(ResponseEntity status) {
        switch (status.getStatus()) {
          case FastHttp.result_ok:
            Log.i(TAG, "主表:"+status.getContentAsString());
           ;
            break;
          case FastHttp.result_net_err:
            Message msg = Message.obtain();
            msg.arg1 = 0;
            handler.sendMessage(msg);
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
