package com.clw.bluetooth;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import zpSDK.zpSDK.zpSDK;
import zpSDK.zpSDK.zpSDK.BARCODE_TYPE;

import com.clw.bluetooth.adapter.CodeAdapter;
import com.clw.bluetooth.adapter.ReviewAdapter;
import com.clw.bluetooth.bean.ProductBean;
import com.clw.bluetooth.bean.ReviewProductBean;
import com.clw.bluetooth.service.BluetoothService;
import com.clw.bluetooth.service.NetService;
import com.clw.bluetooth.util.BtSPP;
import com.clw.bluetooth.util.StaticField;
import com.clw.bluetooth.util.Tools;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.SumPathEffect;

/**
 * 主页面
 * */
public class MainAc extends Activity implements OnClickListener {
  private static final String TAG = "MainAc";

  private long exitTime = 0;

  private Intent mIntent = null;

  private Activity mActivity = null;

  private ImageButton ib_set;

  private EditText et_input;

  private TextView tv_num;

  /** 重打 */
  private Button btn_replay;

  /** 复核 */
  private Button btn_review;

  /** 打印 */
  private Button btn_print;

  /** ListView */
  private ListView lv_code;

  /** 蓝牙设备列表 */
  private ListView lv_drivers;

  /** 扫描次数记录 */
  private int num = 0;

  /** 流水号 */
  private int serialNum = 1000;

  /** 数据源 */
  private List<String> listCodes = new ArrayList<String>();

  /** 蓝牙设备列表 */
  private List<Map<String, String>> listDvice = new ArrayList<Map<String, String>>();

  /** 适配器 */
  private CodeAdapter codeAdapter = null;

  private BluetoothService bluetoothService = null;

  /** 蓝牙 */
  public static BluetoothAdapter myBluetoothAdapter;

  /** 蓝牙地址 */
  public String SelectedBDAddress = "";

  /** 是否第一次按下TAB键 */
  private boolean tab = true;

  /** 复核列表 */
  private List<ReviewProductBean> productBeans = new ArrayList<ReviewProductBean>();

  /** 复核列表适配器 */
  private ReviewAdapter reviewAdapter = null;

  private ListView lv_review;

  /** 机台号 */
  private String machine = "";

  /** 箱条码 */
  private String carTonNo = "";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.ac_main);
    mActivity = MainAc.this;
    SelectedBDAddress = StaticField.blueAddress;
    initView();
  }

  /**
   * 初始化控件
   * 
   * */
  private void initView() {
    machine = getIntent().getExtras().getString("jt_no");
    btn_print = (Button) findViewById(R.id.btn_print);
    btn_replay = (Button) findViewById(R.id.btn_replay);
    btn_review = (Button) findViewById(R.id.btn_review);
    ib_set = (ImageButton) findViewById(R.id.ib_set);
    lv_code = (ListView) findViewById(R.id.lv_code);
    et_input = (EditText) findViewById(R.id.et_input);
    tv_num = (TextView) findViewById(R.id.tv_num);
    lv_drivers = (ListView) findViewById(R.id.lv_drivers);
    lv_review = (ListView) findViewById(R.id.lv_review);

    // ib_set.setOnClickListener(this);
    btn_print.setOnClickListener(this);
    btn_replay.setOnClickListener(this);
    btn_review.setOnClickListener(this);

    /* 初始化扫描列表 */
    codeAdapter = new CodeAdapter(listCodes, mActivity);
    lv_code.setAdapter(codeAdapter);

    /* 初始化复核列表 */
    reviewAdapter = new ReviewAdapter(mActivity, productBeans);
    lv_review.setAdapter(reviewAdapter);

    /* 获取流水号 */
    SharedPreferences sp_selnum = getSharedPreferences("selNum", MODE_APPEND);
    serialNum = sp_selnum.getInt("sel_num", 0);
    et_input.requestFocus();
    et_input.setOnEditorActionListener(new Edit());
    // et_input.addTextChangedListener(new inputCode());

  }

  // List<String> strs = new ArrayList<String>();

  /**
   * 输入框监听事件
   * */
  private class Edit implements OnEditorActionListener {

    @Override
    public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
      if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
        String str = et_input.getText().toString();
        if (!Tools.isNull(str)) {
          num++;
          Message msg = Message.obtain();
          msg.arg1 = 1;
          numHander.sendMessage(msg);
          if (!listCodes.contains(str)) {
            listCodes.add(str);
          }
          et_input.getText().clear();

        }
      }
      return true;
    }

  }

  /**
   * Handler处理
   * */
  Handler numHander = new Handler() {
    public void handleMessage(android.os.Message msg) {
      switch (msg.arg1) {
        case 1: // 添加扫描次数
          tv_num.setText("已扫描次数:" + num);
          codeAdapter.notifyDataSetChanged();
          break;
        case 2: // 复核访问成功
          lv_review.setVisibility(View.VISIBLE);
          productBeans.addAll((Collection<? extends ReviewProductBean>) msg.obj);
          if (!Tools.isEmptyList(productBeans)) {
            reviewAdapter.notifyDataSetChanged();
          }
          break;
        case 0:
          Toast.makeText(mActivity, "网络错误", 200).show();
          break;
        case 10:
          Toast.makeText(mActivity, msg.obj.toString(), 200).show();
          break;
        default:
          break;
      }
    };
  };

  /**
   * 把List集合转为数组
   * 
   * 返回一个数组
   * */
  private String[] listOrArray() {
    if (!Tools.isEmptyList(listCodes)) {
      int size = listCodes.size();
      String[] txm_no = new String[size];
      for (int i = 0; i < listCodes.size(); i++) {
        txm_no[i] = listCodes.get(i).toString();
      }
      return txm_no;
    }
    return null;
  }

  /**
   * 
   * 点击事件
   * */
  @Override
  public void onClick(View arg0) {
    switch (arg0.getId()) {
      case R.id.ib_set: // 输入
        startActivity(new Intent(mActivity, InputKeyAc.class));
        break;
      case R.id.btn_print: // 打印
        break;
      case R.id.btn_replay: // 重打
        break;
      case R.id.btn_review: // 复核
        for (int i = 0; i < listCodes.size(); i++) {
          Log.i(TAG, "" + listCodes.get(i).toString());
        }
        break;
      default:
        break;
    }
  }

  /**
   * 
   * 按键监听 监听键盘按键 TAB键: //第一次按下为复核数据,第二次按下为打印; 数字键3:keycode=10; //删除
   * 返回键:keyCode=4; //清空该次扫描的数据 数字键1A: keycode=204; //重新打印
   * */
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_TAB: // 监听TAB键
        if (tab) {
          // String[] str = new String[] { "081315181846000001",
          // "081315300046000001" };
          if (!Tools.isEmptyList(listCodes)) {
            Toast.makeText(mActivity, "复核", Toast.LENGTH_SHORT).show();
            String[] txm_no = new String[listCodes.size()];
            for (int i = 0; i < listCodes.size(); i++) {
              txm_no[i] = listCodes.get(i).toString();
            }
            NetService.getInstance().reviewProduct(numHander, txm_no, MainAc.this);
            tab = false;
          } else {
            Toast.makeText(mActivity, "请扫描商品后复核", Toast.LENGTH_SHORT).show();
          }
        } else {
          if (!Tools.isNull(SelectedBDAddress)) {// 判断是否绑定有打印机设备
            // 品名数组 //数量数组
            // 13371967600
            if (isPlay) {
              Print2(SelectedBDAddress);
              // Print1(SelectedBDAddress);
            } else {
              callService();
              Print2(SelectedBDAddress);
              // Print1(SelectedBDAddress);
            }

          } else {
            Toast.makeText(mActivity, "请绑定一个蓝牙打印机", Toast.LENGTH_LONG).show();
            ;
          }
        }
        break;
      case 10: // 硬键盘的数字键3为删除 删除对应行
        listCodes.remove(lv_code.getSelectedItemPosition());
        codeAdapter.notifyDataSetChanged();
        break;
      case KeyEvent.KEYCODE_BACK: // 快速按两次返回键为退出,按一次为清空数据
        // if ((System.currentTimeMillis() - exitTime) > 2000) {
        // listCodes.clear();
        // productBeans.clear();
        // exitTime = System.currentTimeMillis();
        // } else {
        // this.finish();
        // }

        if (isSecondTime) {
          /* 清空用户信息 */
          /* 退出App */
          super.onBackPressed();
        } else {
          listCodes.clear();
          productBeans.clear();
          isSecondTime = true;
          mExitHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
              mExitHandler.sendEmptyMessage(0);
            }
          }, 2000);
        }
        break;
      case 204: // 重新打印
        if (!Tools.isNull(SelectedBDAddress)) {
          SharedPreferences sp_json = getSharedPreferences("jsonCache", MODE_APPEND);
          String json = sp_json.getString("json", "");
          loadLocal(json);

        } else {
          Toast.makeText(mActivity, "当前没有打印机!", Toast.LENGTH_SHORT).show();
        }
        break;
      default:
        break;
    }

    return super.onKeyDown(keyCode, event);
  }

  /**
   * 请求网络 上传本次打印的条码
   * */
  /** 是否已经上传过 */
  private boolean isPlay = false;

  private void callService() {
    isPlay = true;
    int size = productBeans.size();
    Log.i(TAG, "复核过的列表长度为" + productBeans.size());
    String[] proname = new String[size];
    String[] pronum = new String[size];
    for (int j = 0; j < productBeans.size(); j++) {
      proname[j] = productBeans.get(j).getBwNo();
      pronum[j] = productBeans.get(j).getQty();

    }
    Log.i(TAG, "code列表:" + listCodes.size());
    String[] product = new String[listCodes.size()];
    for (int i = 0; i < listCodes.size(); i++) {
      product[i] = listCodes.get(i).toString();
    }

    /* 循环相加所有的重量与数量 */
    int sumWeight = 0;
    for (int i = 0; i < productBeans.size(); i++) {
      int wei = Integer.parseInt(productBeans.get(i).getWeight());
      sumWeight = wei + sumWeight;
    }
    Log.i(TAG, "总重量为:" + sumWeight);
    ProductBean.getInstance().setWeight(sumWeight + "");

    NetService.getInstance().insertZxDetails(numHander, carTonNo, product, proname, pronum);
    NetService.getInstance().update(numHander, product);
    NetService.getInstance().insertZxMain(numHander, carTonNo, ProductBean.getInstance().getLoading(),
        ProductBean.getInstance().getLine(), ProductBean.getInstance().getCustmo(),
        ProductBean.getInstance().getBooth(), ProductBean.getInstance().getDate(), String.valueOf(sumWeight), "1");
  }

  /***
   * 打印操作 此方法暂不使用
   * */
  public synchronized void Print1(String BDAddress) {
    if (!BtSPP.OpenPrinter(BDAddress)) {
      Toast.makeText(this, BtSPP.ErrorMessage, Toast.LENGTH_SHORT).show();
      return;
    }
    try {
      BtSPP.SPPWrite(new byte[] { 0x1B, 0x40 }); // 打印机复位
      BtSPP.SPPWrite(new byte[] { 0x1B, 0x33, 0x00 }); // 设置行间距为0
      BtSPP.SPPWrite("     \n".getBytes("GBK"));
      BtSPP.SPPWrite(new byte[] { 0x1B, 0x61, 0x01 }); // 设置不居中 1居中0不居中
      BtSPP.SPPWrite(new byte[] { 0x1d, 0x21, 0x00 }); // 设置倍高 1倍高0不倍高
      BtSPP.SPPWrite(String.format("  客户:%-16s\n", ProductBean.getInstance().getCustmo()).getBytes("GBK"));
      // BtSPP.SPPWrite("\n".getBytes("GBK"));
      // BtSPP.SPPWrite(new byte[] { 0x1B, 0x61, 0x01 }); // 设置不居中 1居中0不居中
      BtSPP.SPPWrite(String.format(" 摊位:%-16s", ProductBean.getInstance().getBooth()).getBytes("GBK"));
      BtSPP.SPPWrite("\n".getBytes("GBK"));
      Log.i(TAG, "开始打印条码...");
      BtSPP.SPPWrite(new byte[] { 0x1d, 0x48, 0x02 }); // 设置条码内容打印在条码下方
      BtSPP.SPPWrite(new byte[] { 0x1d, 0x57, 0x08 }); // 设置条码宽度0.375
      BtSPP.SPPWrite(new byte[] { 0x1d, 0x68, 0x40 }); // 设置条码高度64
      // 打印code128条码
      BtSPP.SPPWrite(new byte[] { 0x1D, 0x6B, 0x08 });
      Log.i(TAG, "打印条码后...");
      /* 对日期进行处理 */
      String date = ProductBean.getInstance().getDate();
      String dateStr = date.replace("-", "");
      carTonNo = dateStr + machine + (serialNum++);
      BtSPP.SPPWrite((carTonNo + "\0\n").getBytes("GBK"));// 条码字符串
      Log.i(TAG, "打印条码字符串...");
      BtSPP.SPPWrite("                \n".getBytes("GBK"));
      // BtSPP.SPPWrite(String.format("流水号: %6d\n",
      // serialNum++).getBytes("GBK")); //流水号
      int no = 1;
      for (int i = 0; i < productBeans.size(); i++) {
        Log.i(TAG, "打印数据中..");
        BtSPP.SPPWrite(String.format((no++) + ".%-16s 数量:%-16s\n", productBeans.get(i).getBwNo(),
            productBeans.get(i).getQty()).getBytes("GBK"));
      }
      // BtSPP.SPPWrite(new byte[] { 0x1d, 0x21, 0x00 }); // 设置不倍高
      // BtSPP.SPPWrite(new byte[] { 0x1b, 0x61, 0x01 }); // 设置居中
      // BtSPP.SPPWrite(new byte[] { 0x1d, 0x21, 0x01 }); // 设置倍高
      // BtSPP.SPPWrite(String.format("日期：%10s\n", date).getBytes("GBK"));

      // BtSPP.SPPWrite("\n\n\n".getBytes("GBK"));
    } catch (UnsupportedEncodingException e) {
      BtSPP.SPPClose();
    }

    BtSPP.SPPClose();
    Log.i(TAG, "打印结束...");
  }

  /**
   * 第二种打印方式
   * */
  private synchronized void Print2(String BDAddress) {
    if (!OpenPrinter(BDAddress)) {
      showMessage("sdk:" + zpSDK.ErrorMessage);
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
    zpSDK.zp_draw_text_ex(0, locationY, "客户:" + ProductBean.getInstance().getCustmo(), "宋体", 3, 0, true, false, false);
    locationY += titleHeight;
    zpSDK.zp_draw_text_ex(0, locationY, "摊位:" + ProductBean.getInstance().getBooth(), "宋体", 3, 0, true, false, false);
    locationY += titleHeight;

    /*
     * x：条形码的x坐标位置。 y：条形码的y坐标位置。 data：条形码的值。 size：条码的值的大小长度。 对日期进行处理
     */
    String date = ProductBean.getInstance().getDate();
    if (!Tools.isNull(date)) {
      String dateStr = date.replace("-", "");
      String monthAndday = dateStr.substring(4, dateStr.length());
      String year = dateStr.substring(2, 4);
      Log.i(TAG, "年:" + year + ",月日:" + monthAndday);
      carTonNo = monthAndday + year + machine + (serialNum++);
    }
    zpSDK.zp_draw_barcode(0, locationY, carTonNo, BARCODE_TYPE.BARCODE_CODE128, 7, 2, 0);
    locationY += rowHeight;
    zpSDK.zp_draw_text_ex(0, locationY, "             ", "宋体", 2, 0, false, false, false);
    locationY += rowHeight;
    zpSDK.zp_draw_text_ex(0, locationY, "             ", "宋体", 1, 0, false, false, false);
    locationY += rowHeight;
    zpSDK.zp_draw_text_ex(4, locationY, carTonNo, "宋体", 3, 0, false, false, false); // 把内容打印在条码下方
    locationY += rowHeight;
    zpSDK.zp_draw_text_ex(0, locationY, "             ", "宋体", 1, 0, false, false, false);
    // locationY += rowHeight;
    // zpSDK.zp_draw_text_ex(0, locationY, "             ", "宋体", 1, 0, false,
    // false, false);
    for (int i = 0; i < productBeans.size(); i++) {
      locationY += rowHeight;
      zpSDK.zp_draw_text_ex(0, locationY, "" + String.valueOf(i + 1) + "." + productBeans.get(i).getBwNo(), "宋体", 3, 0,
          false, false, false);
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
    SharedPreferences sp = getSharedPreferences("selNum", MODE_APPEND);
    Editor ed = sp.edit();
    ed.clear();
    ed.putInt("sel_num", serialNum);
    ed.commit();
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
          productBeans.add(bean);
        }
        // 调用打印
        Print2(SelectedBDAddress);
      }
    } catch (JSONException e) {
      Log.e(TAG, "", e);
    }

  }

  /** 双击退出 */
  private boolean isSecondTime = false;
  private Handler mExitHandler = new Handler() {
    public void handleMessage(android.os.Message msg) {
      isSecondTime = false;
    };
  };
}
