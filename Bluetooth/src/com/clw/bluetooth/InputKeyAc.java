package com.clw.bluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

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
    et_key=(EditText) findViewById(R.id.et_key);

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
        break;
      default:
        break;
    }
  }
}
