package com.clw.bluetooth;

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
import com.clw.bluetooth.app.App;
import com.clw.bluetooth.bean.BwNoQtyBean;
import com.clw.bluetooth.bean.ProductBean;
import com.clw.bluetooth.bean.ReviewProductBean;
import com.clw.bluetooth.bean.YetBwNoQtyBean;
import com.clw.bluetooth.db.DbBwnoQty;
import com.clw.bluetooth.db.DbProductInfoMannager;
import com.clw.bluetooth.db.DbYetBwnoQty;
import com.clw.bluetooth.service.NetService;
import com.clw.bluetooth.service.UpNetService;
import com.clw.bluetooth.util.StaticField;
import com.clw.bluetooth.util.Tools;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;

/**
 * 主页面
 * */
public class MainAc extends Activity implements OnClickListener {
  private static final String TAG = "MainAc";

  // private long exitTime = 0;

  private Intent mIntent = null;

  private Activity mActivity = null;

  private EditText et_input;

  private TextView tv_num;

  private TextView tv_custmo_name;

  private TextView tv_dspno_name;

  private TextView tv_qty;

  /** 重打 */
  private Button btn_replay;

  /** 复核 */
  private Button btn_review;

  /** 打印 */
  private Button btn_print;

  /** ListView */
  private ListView lv_code;

  /** 蓝牙设备列表 */
  // private ListView lv_drivers;

  /** 扫描次数记录 */
  private int num = 0;

  /** 流水号 */
  private long serialNum = 1000;

  /** 数据源 */
  private List<ReviewProductBean> listBwNo = new ArrayList<ReviewProductBean>();

  /** 蓝牙设备列表 */
  private List<Map<String, String>> listDvice = new ArrayList<Map<String, String>>();

  /** 适配器 */
  private CodeAdapter codeAdapter = null;

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
  private String carTonNo = null;

  private long proNo = 0;

  /** 对应车次的品名,数量 */
  private List<BwNoQtyBean> bwNoQtyBeans = new ArrayList<BwNoQtyBean>();

  /** 对应车次已装箱的品名,数量 */
  private List<YetBwNoQtyBean> yetBwNoQtyBeans = new ArrayList<YetBwNoQtyBean>();

  /** 扫描状态 */
  private boolean isScan = true;

  /** 扫描的条形码 */
  private List<String> codes = new ArrayList<String>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.ac_main);
    mActivity = MainAc.this;
    SelectedBDAddress = StaticField.blueAddress;
    mIntent = getIntent();
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
    lv_code = (ListView) findViewById(R.id.lv_code);
    et_input = (EditText) findViewById(R.id.et_input);
    tv_num = (TextView) findViewById(R.id.tv_num);
    // lv_drivers = (ListView) findViewById(R.id.lv_drivers);
    lv_review = (ListView) findViewById(R.id.lv_review);
    tv_custmo_name = (TextView) findViewById(R.id.tv_custmo_name);
    tv_dspno_name = (TextView) findViewById(R.id.tv_dspno_name);
    tv_qty = (TextView) findViewById(R.id.tv_qty);

    btn_print.setOnClickListener(this);
    btn_replay.setOnClickListener(this);
    btn_review.setOnClickListener(this);

    tv_custmo_name.setText("客户:" + ProductBean.getInstance().getCustmoName());
    tv_dspno_name.setText("摊位:" + ProductBean.getInstance().getBoothName());

    /* 获取对应车次信息 */
    Cursor cbw = DbBwnoQty.getInstance(mActivity).selectAll();
    Cursor cyetBw = DbYetBwnoQty.getInstance(mActivity).selectAll();
    if (cbw.getCount() > 0) {
      while (cbw.moveToNext()) {
        BwNoQtyBean bwNoQtyBean = new BwNoQtyBean();
        bwNoQtyBean.setF_bw_no(cbw.getString(cbw.getColumnIndex("f_bw_no")));
        bwNoQtyBean.setF_qty(cbw.getString(cbw.getColumnIndex("f_qty")));
        bwNoQtyBeans.add(bwNoQtyBean);
      }
    }
    if (cyetBw.getCount() > 0) {
      while (cyetBw.moveToNext()) {
        YetBwNoQtyBean yetBwNoQtyBean = new YetBwNoQtyBean();
        yetBwNoQtyBean.setF_bw_no(cyetBw.getString(cyetBw.getColumnIndex("f_bw_no")));
        yetBwNoQtyBean.setF_qty(cyetBw.getString(cyetBw.getColumnIndex("f_qty")));
        yetBwNoQtyBeans.add(yetBwNoQtyBean);
      }
    }

    /* 初始化扫描列表 */
    codeAdapter = new CodeAdapter(listBwNo, mActivity);
    lv_code.setAdapter(codeAdapter);

    /* 初始化复核列表 */
    reviewAdapter = new ReviewAdapter(mActivity, productBeans);
    lv_review.setAdapter(reviewAdapter);

    /* 获取流水号 */
    // SharedPreferences sp_selnum = getSharedPreferences("selNum",
    // MODE_APPEND);
    // int sel_num = sp_selnum.getInt("sel_num", 0);
    // if (sel_num != 0) {
    // serialNum = sel_num;
    // }
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
          if (isScan) {
            if (!codes.contains(str)) {
              codes.add(str);
              num++;
              Message m = Message.obtain();
              m.arg1 = 1;
              m.obj = str;
              numHander.sendMessage(m);
            } else {
              isScan = true;
            }
          }
        }
        et_input.getText().clear();
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
          int size = codes.size();
          tv_num.setText("已扫描:" + size + "条");
          String str = msg.obj.toString();
          isScan = false;
          UpNetService.getInstance().getBWnameAndNum(mActivity, numHander, str);
          et_input.getText().clear();
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
        case 22: // 扫描单个条码后获取对应信息,品名,数量
          isScan = true;
          String result = msg.obj.toString();
          try {
            JSONObject obj = new JSONObject(result);
            int status = obj.getInt("status");
            if (status == 0) {
              JSONArray data = obj.getJSONArray("data");
              if (data.length() > 0) {
                for (int i = 0; i < data.length(); i++) {
                  ReviewProductBean bean = new ReviewProductBean();
                  String bwno = data.getJSONObject(i).getString("f_bw_no");
                  String qty = data.getJSONObject(i).getString("f_qty");
                  bean.setBwNo(bwno);
                  bean.setQty(qty);
                  listBwNo.add(bean);
                }
                Message m1 = Message.obtain();
                m1.arg1 = 33;
                numHander.sendMessage(m1);
              }
              codeAdapter.notifyDataSetChanged();

              /* 获得对应车次的品名数量 */
              /* 扫到的品名,数量+对应已装箱好的数量不能大于对应车次的数量 */

            } else {
              showMessage("请求错误");
            }
          } catch (JSONException e1) {
            Log.e(TAG, "", e1);
          }

          break;
        case 33: // 计算该车次已装配的数量
          for (int i = 0; i < listBwNo.size(); i++) {
            Message m2 = Message.obtain();
            m2.arg1 = 44;
            m2.obj = listBwNo.get(i).getBwNo();
            numHander.sendMessage(m2);
          }

          if (yetBwNoQtyBeans.size() > 0) {
            Log.i(TAG, "size:" + listBwNo.size());
            int sumNowQty = 0;
            for (ReviewProductBean bean : listBwNo) {
              for (BwNoQtyBean bwNoQtyBean : bwNoQtyBeans) {
                if (bean.getBwNo().equals(bwNoQtyBean.getF_bw_no())) {
                  for (YetBwNoQtyBean yetBwNoQtyBean : yetBwNoQtyBeans) {
                    if (bean.getBwNo().equals(yetBwNoQtyBean.getF_bw_no())) {
                      int nowQty = Integer.parseInt(bean.getQty());
                      int yetQty = Integer.parseInt(yetBwNoQtyBean.getF_qty());
                      int bwQty = Integer.parseInt(bwNoQtyBean.getF_qty());
                      sumNowQty = nowQty + sumNowQty;
                      Log.i(TAG, "对应车该装的数量:" + bwQty + "已经装箱好的数:" + yetQty + "该品扫描的总数量" + sumNowQty);
                      if (sumNowQty + yetQty > bwQty) {
                        showHintDialog("已扫描的数大于装车数");
                      }
                    }
                  }
                }
              }
            }
          } else {
            Log.i(TAG, "size:" + listBwNo.size());
            int sumNowQty = 0;
            for (ReviewProductBean bean : listBwNo) {
              Log.i(TAG, "扫描循环次");
              for (BwNoQtyBean bwNoQtyBean : bwNoQtyBeans) {
                Log.i(TAG, "对应循环次");
                if (bean.getBwNo().contains(bwNoQtyBean.getF_bw_no())) {
                  int nowQty = Integer.parseInt(bean.getQty());
                  int bwQty = Integer.parseInt(bwNoQtyBean.getF_qty());
                  sumNowQty = nowQty + sumNowQty;

                  Log.i(TAG, "此车次的数量" + bwQty + ",单次扫描的数量" + nowQty + "扫描的总数量" + sumNowQty);
                  if (sumNowQty > bwQty) {
                    // showMessage("该次扫描的数大于装车数");
                    showHintDialog("该次扫描的数大于装车数");
                  }
                }
              }
            }
          }
          break;
        case 44: // 显示总数
          String name = msg.obj.toString();
          int sumQty = 0;
          for (int i = 0; i < listBwNo.size(); i++) {
            if (listBwNo.get(i).getBwNo().contains(name)) {
              int qty = Integer.parseInt(listBwNo.get(i).getQty());
              sumQty = qty + sumQty;
            }
          }
          tv_qty.setText("数量:" + sumQty);
          break;
        case 111: // 插入主表后的操作,代表打印了
          // finish();
          break;
        case 110: // 插入主表
          String json = msg.obj.toString();
          Log.i(TAG, "插入主表成功返回:" + json);
          try {
            JSONObject object = new JSONObject(json);
            int sta = object.getInt("status");
            if (sta == 0) {
              Print2(SelectedBDAddress);
            } else {
              showMessage("插入主表未成功,打印取消");
            }
          } catch (JSONException e) {
            Log.e(TAG, "", e);
          }

          break;
        default:
          break;
      }
    };
  };

  /**
   * 
   * 点击事件
   * */
  @Override
  public void onClick(View arg0) {
    switch (arg0.getId()) {
      case R.id.btn_print: // 打印
        break;
      case R.id.btn_replay: // 重打
        break;
      case R.id.btn_review: // 复核
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
          if (!Tools.isEmptyList(codes)) {
            Toast.makeText(mActivity, "复核", Toast.LENGTH_SHORT).show();
            String[] txm_no = new String[codes.size()];
            for (int i = 0; i < codes.size(); i++) {
              txm_no[i] = codes.get(i).toString();
            }
            NetService.getInstance().reviewProduct(numHander, txm_no, MainAc.this);
            tab = false;
          } else {
            Toast.makeText(mActivity, "请扫描商品后复核", Toast.LENGTH_SHORT).show();
          }
        } else {
          if (!Tools.isNull(SelectedBDAddress)) {// 判断是否绑定有打印机设备
            if (!isPlay) {
              callService();
            }
          } else {
            Toast.makeText(mActivity, "请绑定一个蓝牙打印机", Toast.LENGTH_LONG).show();
            ;
          }
        }
        break;
      case 10: // 硬键盘的数字键3为删除 删除对应行
        listBwNo.remove(lv_code.getSelectedItemPosition());
        codes.remove(lv_code.getSelectedItemPosition());
        codeAdapter.notifyDataSetChanged();
        int size = listBwNo.size();
        tv_num.setText("已扫描:" + size + "条");
        break;
      case 4: // 快速按两次返回键为退出,按一次为清空数据
        Log.i(TAG, "退出:清空数据");
        listBwNo.clear();
        yetBwNoQtyBeans.clear();
        bwNoQtyBeans.clear();
        this.finish();
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
    Log.i(TAG, "code列表:" + codes.size());
    String[] product = new String[codes.size()];
    for (int i = 0; i < codes.size(); i++) {
      product[i] = codes.get(i).toString();
    }

    /* 循环相加所有的重量与数量 */
    double sumWeight = 0;
    double sumQtyno = 0;
    for (int i = 0; i < productBeans.size(); i++) {
      double wei = Double.parseDouble(productBeans.get(i).getWeight());
      double qtyno = Double.parseDouble(productBeans.get(i).getQty());
      sumWeight = wei + sumWeight;
      sumQtyno = qtyno + sumQtyno;
    }
    Log.i(TAG, "总重量为:" + sumWeight + ",总数量为:" + sumQtyno);
    ProductBean.getInstance().setWeight(sumWeight + "");
    ProductBean.getInstance().setQtyNo(sumQtyno + "");

    /* 插入数据库 */
    if (!Tools.isNull(DbProductInfoMannager.getInstance(mActivity).selectById())) {
      long no = Long.parseLong(DbProductInfoMannager.getInstance(mActivity).selectById());
      serialNum = no + 1;
      DbProductInfoMannager.getInstance(mActivity).addSingleNo(String.valueOf(serialNum));
    } else {
      DbProductInfoMannager.getInstance(mActivity).addSingleNo(String.valueOf(serialNum));
    }

    String date = ProductBean.getInstance().getDate();
    if (!Tools.isNull(date)) {
      String dateStr = date.replace("-", "");
      String monthAndday = dateStr.substring(4, dateStr.length());
      String year = dateStr.substring(2, 4);
      Log.i(TAG, "年:" + year + ",月日:" + monthAndday);
      carTonNo = monthAndday + year + machine + serialNum;
    }
    NetService.getInstance().insertZxDetails(mActivity, numHander, carTonNo, product, proname, pronum);
    NetService.getInstance().update(mActivity, numHander, product);

    Log.i(TAG, "箱条码-----------------" + carTonNo);
    NetService.getInstance().insertZxMain(mActivity, numHander, String.valueOf(carTonNo),
        ProductBean.getInstance().getLoading(), ProductBean.getInstance().getLine(),
        ProductBean.getInstance().getCustmo(), ProductBean.getInstance().getBooth(),
        ProductBean.getInstance().getDate(), String.valueOf(sumQtyno), String.valueOf(sumWeight), "1");

  }

  /**
   * 打印操作 第二种打印方式 使用芝柯sdk
   * */
  private synchronized void Print2(String BDAddress) {
    App.getProduct(mActivity);
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
    zpSDK.zp_draw_text_ex(0, locationY, "客户:" + ProductBean.getInstance().getCustmoName(), "宋体", 3, 0, true, false,
        false);
    locationY += titleHeight;
    zpSDK.zp_draw_text_ex(0, locationY, "摊位:" + ProductBean.getInstance().getBoothName(), "宋体", 3, 0, true, false,
        false);
    locationY += titleHeight;

    /*
     * x：条形码的x坐标位置。 y：条形码的y坐标位置。 data：条形码的值。 size：条码的值的大小长度。 对日期进行处理
     */
    // String date = ProductBean.getInstance().getDate();
    // if (!Tools.isNull(date)) {
    // String dateStr = date.replace("-", "");
    // String monthAndday = dateStr.substring(4, dateStr.length());
    // String year = dateStr.substring(2, 4);
    // Log.i(TAG, "年:" + year + ",月日:" + monthAndday);
    // carTonNo = monthAndday + year + machine + (serialNum++);
    // }
    zpSDK.zp_draw_barcode(0, locationY, "" + carTonNo, BARCODE_TYPE.BARCODE_CODE128, 7, 2, 0);
    locationY += rowHeight;
    zpSDK.zp_draw_text_ex(0, locationY, "             ", "宋体", 2, 0, false, false, false);
    locationY += rowHeight;
    zpSDK.zp_draw_text_ex(0, locationY, "             ", "宋体", 1, 0, false, false, false);
    locationY += rowHeight;
    zpSDK.zp_draw_text_ex(4, locationY, "" + carTonNo, "宋体", 3, 0, false, false, false); // 把内容打印在条码下方
    locationY += rowHeight;
    zpSDK.zp_draw_text_ex(0, locationY, "             ", "宋体", 1, 0, false, false, false);
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
    SharedPreferences sp = getSharedPreferences("selNum", MODE_APPEND);
    Editor ed = sp.edit();
    ed.clear();
    ed.putLong("sel_num", serialNum);
    ed.commit();
    DbProductInfoMannager.getInstance(mActivity).closeDB();
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

  private void showHintDialog(String text) {
    final Dialog dialog = new Dialog(this, R.style.MyDialog);
    View view = LayoutInflater.from(MainAc.this).inflate(R.layout.dialog_layout, null);
    Button btn_cancle = (Button) view.findViewById(R.id.btn_cancle);
    Button btn_confrim = (Button) view.findViewById(R.id.btn_confrim);
    TextView tv_hint = (TextView) view.findViewById(R.id.tv_msg);
    tv_hint.setText(text);

    dialog.setContentView(view);
    dialog.show();

    Window dialogWindow = dialog.getWindow();
    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
    dialogWindow.setGravity(Gravity.CENTER);

    btn_cancle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View arg0) {
        dialog.dismiss();
        isScan = true;
      }
    });

    btn_confrim.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View arg0) {
        dialog.dismiss();
        codes.remove(codes.size() - 1);
        listBwNo.remove(listBwNo.size() - 1);
        codeAdapter.notifyDataSetChanged();
        int size = listBwNo.size();
        tv_num.setText("已扫描:" + size + "条");
        isScan = true;
      }
    });
  }
}
