package com.clw.bluetooth.service;

import com.android.pc.ioc.internet.FastHttp;
import com.clw.bluetooth.util.StaticField;

import android.os.Handler;

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
  public void getFristData(String code,Handler handler){
    FastHttp.get(StaticField.HOST+"open?sql=select+f_zc_dsp%2C+f_zcd_no%2Cf_zcd_date%2C+f_cus_no%2Cf_ywy+from+un_zxdetail_ord&token="+507a81c9a803441aab80339e8bb2d2f0+"&pagesize=5&pageindex=0");
  }
}
