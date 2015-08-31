package com.clw.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import zpSDK.zpSDK.zpSDK;
import zpSDK.zpSDK.zpSDK.BARCODE_TYPE;

import com.android.pc.ioc.verification.annotation.IpAddress;
import com.clw.bluetooth.app.App;
import com.clw.bluetooth.bean.ProductBean;
import com.clw.bluetooth.bean.ReviewProductBean;
import com.clw.bluetooth.db.DbIp;
import com.clw.bluetooth.db.DbProductInfoMannager;
import com.clw.bluetooth.pop.ChoiceDatePop;
import com.clw.bluetooth.pop.ChoiceDatePop.GetDateListener;
import com.clw.bluetooth.service.NetService;
import com.clw.bluetooth.util.StaticField;
import com.clw.bluetooth.util.Tools;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class WelcomeAc extends Activity implements OnClickListener {
  private static final String TAG = "WelcomeAc";

  private Context mContext = null;

  /** 选择器 */
  private ChoiceDatePop mChoiceDatePop;

  private TextView tv_time;

  private TextView tv_shebei;

  /** 机台号输入框 */
  private EditText et_input;

  /** 条形码输入框 */
  private EditText et_scan;
  
  /** ip输入框*/
  private EditText et_ip;

  /** 日期 */
  private String date = "";

  /** 保存机台号到本地 */
  private SharedPreferences sp_machine_no = null;

  private SharedPreferences sp_json = null;

  /** 机台号 */
  private String machine = "";

  /** 扫描得到的单据条码 */
  private String strCode = "";
  
  /** 输入的ip*/
  private String strIp="";

  /** 数据 */
  private List<ReviewProductBean> productBeans = new ArrayList<ReviewProductBean>();

  /** 流水号 */
  private long serialNum = 1000;

  /** 蓝牙 */
  public static BluetoothAdapter myBluetoothAdapter;

  /** 蓝牙地址 */
  public String SelectedBDAddress = "";

  /** 箱条码 */
  private String carTonNo = "";

  private Button btn_set;

  /** 赋值首选项存取的机台号*/
  private String no = "";
  
  private long proNo=0;
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.ac_welcome);
    mContext = WelcomeAc.this;
    SharedPreferences sp = getSharedPreferences("pwd", MODE_PRIVATE);
    Editor ed = sp.edit();
    ed.putString("pwd", "111000");
    ed.commit();
    initView();

  }

  /**
   * 初始化
   * */
  private void initView() {
    et_input = (EditText) findViewById(R.id.et_input);
    et_scan = (EditText) findViewById(R.id.et_scan);
    tv_time = (TextView) findViewById(R.id.tv_time);
    tv_shebei = (TextView) findViewById(R.id.tv_shebei);
    btn_set = (Button) findViewById(R.id.btn_set);
    et_ip=(EditText) findViewById(R.id.et_ip);

    btn_set.setOnClickListener(this);
    tv_time.setOnClickListener(this);

    if (!ListBluetoothDevice()) {
      finish();
    }

    /* 首选项 */
    sp_machine_no = getSharedPreferences("machine_no", MODE_PRIVATE);
    no = App.getMachineNo(mContext);
    String ip=DbIp.getInstance(mContext).selectById();
    et_scan.requestFocus();
    if (!Tools.isNull(no,ip)) {
      et_input.setText(no);
      et_ip.setText(ip);
      et_ip.setEnabled(false);
      et_input.setEnabled(false);
      // startActivity(new Intent(mContext, MainAc.class));
    }
    et_scan.setOnEditorActionListener(new EditorListener());

  }

  @Override
  protected void onResume() {
    super.onResume();
    // 判断输入的管理密码是否正确
    if (StaticField.isConfigPwd || Tools.isNull(no)) {
      et_input.setEnabled(true);
      et_ip.setEnabled(true);
    } else {
      et_input.setEnabled(false);
      et_ip.setEnabled(false);
    }
    sp_json = getSharedPreferences("jsonCache", MODE_APPEND);
    String json = sp_json.getString("json", "");
    loadLocal(json);

  }

  /**
   * 加载本地json
   * 
   * */
  private void loadLocal(String json) {
    try {
      productBeans.clear();
      JSONObject obj = new JSONObject(json);
      String stat = obj.getString("status");
      if (Tools.judgeStringEquals("0", stat)) {
        JSONArray array = obj.getJSONArray("data");
        for (int i = 0; i < array.length(); i++) {
          ReviewProductBean bean = new ReviewProductBean();
          bean.setBwNo(array.getJSONObject(i).getString("f_bw_no"));
          bean.setQty(array.getJSONObject(i).getString("f_qty"));
          bean.setBwdsp((array.getJSONObject(i).getString("f_bw_dsp")));
          bean.setWeight(array.getJSONObject(i).getString("f_weight"));
          productBeans.add(bean);
        }
      }
    } catch (JSONException e) {
      Log.e(TAG, "", e);
    }

  }

  /**
   * 条形码输入监听
   * 
   * */
  private class EditorListener implements OnEditorActionListener {

    @Override
    public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
      if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
        strCode = et_input.getText().toString();
      }
      return true;
    }

  }

  /**
   * 点击事件监听
   * */
  @Override
  public void onClick(View arg0) {
    switch (arg0.getId()) {
      case R.id.tv_time:
        showDate(tv_time);
        break;
      case R.id.btn_set:
        startActivity(new Intent(mContext, InputKeyAc.class));
        break;

      default:
        break;
    }
  };

  /**
   * pop监听
   */
  GetDateListener mGetDateListener = new GetDateListener() {

    @Override
    public void getTime(String startTime, String endTime) {
      tv_time.setText(startTime);
    }
  };

  /**
   * 按键事件监听 数字键1A: keycode=204; //重新打印上次数据
   * */
  private long exitTime = 0;

  public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_TAB: // 按下tab键时判断机台号与单据条码是否为空
        // date=tv_time.getText().toString();
        machine = et_input.getText().toString();
        strCode = et_scan.getText().toString();
        strIp=et_ip.getText().toString();
//        strCode = str.substring(0, str.length() - 1);
        if (!Tools.isNull(machine, strCode,strIp)) {
          Editor ed = sp_machine_no.edit();
          ed.clear();
          ed.putString("no", machine);
          ed.commit();
          DbIp.getInstance(mContext).deleteTableData();
          
          if (!strIp.contains("http://")) {
            DbIp.getInstance(mContext).addIp("http://"+strIp+"/data/");
          }else{
            DbIp.getInstance(mContext).addIp(strIp);
          }
          Log.i(TAG, "输入的IP地址:"+strIp);
          NetService.getInstance().getFristData(mContext,strCode, handler);

        } else {
          Toast.makeText(mContext, "请输入完整信息", Toast.LENGTH_LONG);
        }
        break;
      case 204: // 重新打印
        if (!Tools.isNull(StaticField.blueAddress)) {
          Print2(StaticField.blueAddress);
        } else {
          Toast.makeText(mContext, "您还没有进行商品复核!", Toast.LENGTH_SHORT).show();
        }
        break;
      case KeyEvent.KEYCODE_BACK:
        if ((System.currentTimeMillis() - exitTime) > 2000) {
          exitTime = System.currentTimeMillis();
          et_scan.getText().clear();
          showMessage("再按一次退出!");
          return false;
        } else {
          this.finish();
        }
        break;
      default:
        break;
    }
    return super.onKeyDown(keyCode, event);
  }

  /**
   * 网络请求Handler消息处理
   * 
   * */
  Handler handler = new Handler() {
    public void handleMessage(android.os.Message msg) {
      switch (msg.arg1) {
        case 1:
          et_scan.getText().clear();
          startActivity(new Intent(mContext, MainAc.class).putExtra("jt_no", machine));
          Log.i(TAG, "线路:" + ProductBean.getInstance().getLine() + ",客户:" + ProductBean.getInstance().getCustmo()
              + ",摊位:" + ProductBean.getInstance().getBooth());
          break;
        case 0:
          Toast.makeText(mContext, "网络错误", 200).show();
          break;
        default:
          break;
      }
    };
  };

  private void showDate(TextView tv) {
    if (null == mChoiceDatePop) {
      mChoiceDatePop = new ChoiceDatePop(this, mGetDateListener);
    }
    mChoiceDatePop.show(tv);
  }

  /**
   * 
   * 蓝牙列表
   * 
   * */
  private boolean ListBluetoothDevice() {

    if ((myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()) == null) {
      Toast.makeText(this, "没有找到蓝牙适配器", Toast.LENGTH_LONG).show();
      return false;
    }

    if (!myBluetoothAdapter.isEnabled()) {
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBtIntent, 2);
    }

    Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
    if (pairedDevices.size() <= 0)
      return false;
    // BluetoothDevice device;
    for (BluetoothDevice device : pairedDevices) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("DeviceName", device.getName());
      map.put("BDAddress", device.getAddress());
      SelectedBDAddress = device.getAddress();
      StaticField.blueAddress = SelectedBDAddress;
      tv_shebei.setText("已绑定连接的设备:" + device.getName());
      Log.i(TAG, "已绑定连接的蓝牙设备地址:" + SelectedBDAddress);
    }

    return true;

  }

  /**
   * 第二种打印方式
   * */
  private synchronized void Print2(String BDAddress) {
    App.getProduct(mContext);
    if (!OpenPrinter(BDAddress)) {
      showMessage(zpSDK.ErrorMessage);
      return;
    }
    if (!zpSDK.zp_page_create(80, 60)) {
      showMessage("创建打印页面失败");
      return;
    }

    DrawContent(0);
    zpSDK.zp_page_print(false);
    zpSDK.zp_page_clear();
    zpSDK.zp_page_free();
    zpSDK.zp_close();
  }

  /**
   * 打印内容布局
   * */
  private void DrawContent(int offsetMM) {
    double locationY = new Double(5 - offsetMM);
    double rowHeight = new Double(3.5);
    double titleHeight = new Double(4);
    zpSDK.zp_draw_text_ex(0, locationY, "客户:" + ProductBean.getInstance().getCustmoName(), "宋体", 3, 0, true, false,
        false);
    locationY += titleHeight;
    zpSDK.zp_draw_text_ex(0, locationY, "摊位:" + ProductBean.getInstance().getBoothName(), "宋体", 3, 0, true, false,
        false);
    locationY += titleHeight;

    /*
     * x：条形码的x坐标位置。 y：条形码的y坐标位置。 data：条形码的值。 size：条码的值的大小长度。 对日期进行处理
     */
    /* 插入数据库 */
    if (!Tools.isNull(DbProductInfoMannager.getInstance(mContext).selectById())) {
      long no = Long.parseLong(DbProductInfoMannager.getInstance(mContext).selectById());
      serialNum = no + 1;
      DbProductInfoMannager.getInstance(mContext).addSingleNo(String.valueOf(serialNum));
    } else {
      DbProductInfoMannager.getInstance(mContext).addSingleNo(String.valueOf(serialNum));
    }

    zpSDK.zp_draw_barcode(0, locationY, proNo+"", BARCODE_TYPE.BARCODE_CODE128, 7, 2, 0);
    locationY += rowHeight;
    zpSDK.zp_draw_text_ex(0, locationY, "             ", "宋体", 2, 0, false, false, false);
    locationY += rowHeight;
    zpSDK.zp_draw_text_ex(0, locationY, "             ", "宋体", 1, 0, false, false, false);
    locationY += rowHeight;
    zpSDK.zp_draw_text_ex(4, locationY, proNo+"", "宋体", 3, 0, false, false, false); // 把内容打印在条码下方
    locationY += rowHeight;
    // zpSDK.zp_draw_text_ex(0, locationY, "             ", "宋体", 1, 0, false,
    // false, false);
    // locationY += rowHeight;
    // zpSDK.zp_draw_text_ex(0, locationY, "             ", "宋体", 1, 0, false,
    // false, false);
    for (int i = 0; i < productBeans.size(); i++) {
      locationY += rowHeight;
      zpSDK.zp_draw_text_ex(0, locationY, "" + String.valueOf(i + 1) + "." + productBeans.get(i).getBwdsp(), "宋体", 3,
          0, false, false, false);
      zpSDK.zp_draw_text_ex(32, locationY, "数量:" + String.valueOf(productBeans.get(i).getQty()), "宋体", 3, 0, false,
          false, false);
    }

  }

  public boolean OpenPrinter(String BDAddress) {
    if (BDAddress == "" || BDAddress == null) {
      Toast.makeText(this, "没有选择打印机", Toast.LENGTH_LONG).show();
      return false;
    }
    BluetoothDevice myDevice;
    myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (myBluetoothAdapter == null) {
      Toast.makeText(this, "读取蓝牙设备错误", Toast.LENGTH_LONG).show();
      return false;
    }
    myDevice = myBluetoothAdapter.getRemoteDevice(BDAddress);
    if (myDevice == null) {
      Toast.makeText(this, "读取蓝牙设备错误", Toast.LENGTH_LONG).show();
      return false;
    }
    if (zpSDK.zp_open(myBluetoothAdapter, myDevice) == false) {
      Toast.makeText(this, zpSDK.ErrorMessage, Toast.LENGTH_LONG).show();
      return false;
    }
    return true;
  }

  public void showMessage(String str) {
    Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    DbProductInfoMannager.getInstance(mContext).closeDB();
  }
}
