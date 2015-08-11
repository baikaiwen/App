package com.clw.bluetooth;

import java.util.ArrayList;
import java.util.List;

import com.clw.bluetooth.adapter.CodeAdapter;
import com.clw.bluetooth.util.Tools;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.content.Intent;

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

  private int num = 0;

  /** 数据源 */
  private List<String> listCodes = new ArrayList<String>();

  /** 适配器 */
  private CodeAdapter adapter = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.ac_main);
    mActivity = MainAc.this;

    initView();
  }

  /**
   * 初始化控件
   * */
  private void initView() {
    btn_print = (Button) findViewById(R.id.btn_print);
    btn_replay = (Button) findViewById(R.id.btn_replay);
    btn_review = (Button) findViewById(R.id.btn_review);
    ib_set = (ImageButton) findViewById(R.id.ib_set);
    lv_code = (ListView) findViewById(R.id.lv_code);
    et_input = (EditText) findViewById(R.id.et_input);
    tv_num = (TextView) findViewById(R.id.tv_num);

    ib_set.setOnClickListener(this);
    btn_print.setOnClickListener(this);
    btn_replay.setOnClickListener(this);
    btn_review.setOnClickListener(this);

    adapter = new CodeAdapter(listCodes, mActivity);
    lv_code.setAdapter(adapter);
    et_input.requestFocus();
    et_input.setOnEditorActionListener(new Edit());
//    et_input.addTextChangedListener(new inputCode());
  }
  
  private class Edit implements OnEditorActionListener{

    @Override
    public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
      if (arg1==EditorInfo.IME_ACTION_SEARCH) {
        if (!Tools.isNull(et_input.getText().toString())) {
          num++;
          Message msg = Message.obtain();
          msg.arg1 = 1;
          numHander.sendMessage(msg);
          listCodes.add(et_input.getText().toString());
          et_input.getText().clear();
          // arg0.clear();
          adapter.notifyDataSetChanged();
        }
      }
      return true;
    }
    
  }

  private class inputCode implements TextWatcher {

    @Override
    public void afterTextChanged(Editable arg0) {

      num++;
      Message msg = Message.obtain();
      msg.arg1 = 1;
      numHander.sendMessage(msg);
      listCodes.add(arg0.toString());
      // arg0.clear();
      adapter.notifyDataSetChanged();

    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

  }

  Handler numHander = new Handler() {
    public void handleMessage(android.os.Message msg) {
      switch (msg.arg1) {
        case 1:
          tv_num.setText("已扫描:" + num);
           break;

        default:
          break;
      }
    };
  };

  @Override
  public void onClick(View arg0) {
    switch (arg0.getId()) {
      case R.id.ib_set:
        startActivity(new Intent(mActivity, InputKeyAc.class));
        break;
      case R.id.btn_print:
        break;
      case R.id.btn_replay:
        break;
      case R.id.btn_review:
        for (int i = 0; i < listCodes.size(); i++) {
          Log.i(TAG, "" + listCodes.get(i).toString());
        }
        break;
      default:
        break;
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_TAB) {
      Toast.makeText(mActivity, "天", 200).show();
    }
    // if (keyCode == 140 || keyCode == 139 || keyCode == 139) {
    // if (!Tools.isNull(et_input.getText().toString())) {
    // num++;
    // Message msg = Message.obtain();
    // msg.arg1 = 1;
    // numHander.sendMessage(msg);
    // listCodes.add(et_input.getText().toString());
    // // arg0.clear();
    // adapter.notifyDataSetChanged();
    // }
    // Toast.makeText(mActivity, "扫", 200).show();
    // //
    // }
    return super.onKeyDown(keyCode, event);
  }

}
