package com.clw.bluetooth;

import com.clw.bluetooth.util.StaticField;
import com.clw.bluetooth.util.Tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class InputKeyAc extends Activity implements OnClickListener {
  private static final String TAG = "InputKeyAc";

  private Activity mActivity = null;

  private ImageButton ib_back;
  private Button btn_confirm;
  private EditText et_key;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.ac_inputkey);
    mActivity = InputKeyAc.this;

    ib_back = (ImageButton) findViewById(R.id.ib_back);
    btn_confirm = (Button) findViewById(R.id.btn_confrim);
    et_key = (EditText) findViewById(R.id.et_key);

    btn_confirm.setOnClickListener(this);
    ib_back.setOnClickListener(this);
  }

  @Override
  public void onClick(View arg0) {
    switch (arg0.getId()) {
      case R.id.ib_back:
        finish();
        break;
      case R.id.btn_confrim:
        String input = et_key.getText().toString();
        if (!Tools.isNull(input)) {
          if (Tools.judgeStringEquals(input, getSharePwd())) {
            StaticField.isConfigPwd = true;
            this.finish();
          }
        } else {
          Toast.makeText(mActivity, "请输入密码", 200).show();
        }
        break;
      default:
        break;
    }
  }

  private String getSharePwd() {
    SharedPreferences sp = getSharedPreferences("pwd", MODE_APPEND);
    String pwd = sp.getString("pwd", "");

    return pwd;
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_TAB:
        String input = et_key.getText().toString();
        if (!Tools.isNull(input)) {
          if (Tools.judgeStringEquals(input, getSharePwd())) {
            StaticField.isConfigPwd = true;
            this.finish();
          }
        } else {
          Toast.makeText(mActivity, "请输入密码", 200).show();
        }
        break;

      default:
        break;
    }
    return super.onKeyDown(keyCode, event);
  }

}
