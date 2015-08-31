package com.clw.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.clw.bluetooth.adapter.CodeAdapter;
import com.clw.bluetooth.service.BluetoothService;
import com.clw.bluetooth.util.Tools;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;

/**
 * 主页面
 * */
public class MainAc extends Activity implements OnClickListener {
  private static final String TAG = "MainAc";

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

  /** 扫描次数记录*/
  private int num = 0;

  /** 数据源 */
  private List<String> listCodes = new ArrayList<String>();

  /** 蓝牙设备列表 */
  private List<Map<String, String>> listDvice = new ArrayList<Map<String, String>>();

  /** 适配器 */
  private CodeAdapter adapter = null;

  private BluetoothService bluetoothService = null;

  /** 蓝牙 */
  public static BluetoothAdapter myBluetoothAdapter;

  /** 蓝牙地址 */
  public String SelectedBDAddress = "";

  /** 是否第一次按下TAB键 */
  private boolean tab = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.ac_main);
    mActivity = MainAc.this;

    initView();
  }

  /**
   * 初始化控件
   * 
   * */
  private void initView() {
    btn_print = (Button) findViewById(R.id.btn_print);
    btn_replay = (Button) findViewById(R.id.btn_replay);
    btn_review = (Button) findViewById(R.id.btn_review);
    ib_set = (ImageButton) findViewById(R.id.ib_set);
    lv_code = (ListView) findViewById(R.id.lv_code);
    et_input = (EditText) findViewById(R.id.et_input);
    tv_num = (TextView) findViewById(R.id.tv_num);
    lv_drivers = (ListView) findViewById(R.id.lv_drivers);

    ib_set.setOnClickListener(this);
    btn_print.setOnClickListener(this);
    btn_replay.setOnClickListener(this);
    btn_review.setOnClickListener(this);

    if (!ListBluetoothDevice()) {
      finish();
    }
    bluetoothService = new BluetoothService();

    adapter = new CodeAdapter(listCodes, mActivity);
    lv_code.setAdapter(adapter);
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

  /*
   * private class inputCode implements TextWatcher {
   * 
   * @Override public void afterTextChanged(Editable arg0) {
   * 
   * num++; Message msg = Message.obtain(); msg.arg1 = 1;
   * numHander.sendMessage(msg); listCodes.add(arg0.toString()); //
   * arg0.clear(); adapter.notifyDataSetChanged();
   * 
   * }
   * 
   * @Override public void beforeTextChanged(CharSequence arg0, int arg1, int
   * arg2, int arg3) { }
   * 
   * @Override public void onTextChanged(CharSequence arg0, int arg1, int arg2,
   * int arg3) { }
   * 
   * }
   */
  /**
   * Handler处理
   * */
  Handler numHander = new Handler() {
    public void handleMessage(android.os.Message msg) {
      switch (msg.arg1) {
        case 1:
          tv_num.setText("已扫描次数:" + num);
          adapter.notifyDataSetChanged();
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
      case R.id.ib_set:  //输入
        startActivity(new Intent(mActivity, InputKeyAc.class));
        break;
      case R.id.btn_print:  //打印
        break;
      case R.id.btn_replay: //重打
        break;
      case R.id.btn_review:  //复核
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
   * 按键监听
   * 监听键盘按键
   * */
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_TAB: // 监听TAB键
        if (tab) {
          Toast.makeText(mActivity, "天", 200).show();
          tab = false;
        } else { 
          if (!Tools.isNull(SelectedBDAddress)) {//判断是否有打印机设备
            
          }else{
            Toast.makeText(mActivity, "请选择一个打印机", 200).show();;
          }
        }
        break;

      default:
        break;
    }

    // if (keyCode == 140 || keyCode == 139 || keyCode == 141) {
    // }
    return super.onKeyDown(keyCode, event);
  }

  /**
   * 
   * 蓝牙列表
   * 
   * */
  private boolean ListBluetoothDevice() {
    SimpleAdapter m_adapter = new SimpleAdapter(this, listDvice, android.R.layout.simple_list_item_2, new String[] {
        "DeviceName", "BDAddress" }, new int[] { android.R.id.text1, android.R.id.text2 });
    lv_drivers.setAdapter(m_adapter);

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
    for (BluetoothDevice device : pairedDevices) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("DeviceName", device.getName());
      map.put("BDAddress", device.getAddress());
      listDvice.add(map);
    }

    /* 监听蓝牙列表点击事件 */
    lv_drivers.setOnItemClickListener(new ListView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SelectedBDAddress = listDvice.get(position).get("BDAddress");
        if (((ListView) parent).getTag() != null) {
          ((View) ((ListView) parent).getTag()).setBackgroundDrawable(null);
        }
        ((ListView) parent).setTag(view);
        view.setBackgroundColor(Color.BLUE);
      }
    });

    return true;

  }
}
