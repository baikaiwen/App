package com.clw.bluetooth;


import com.clw.bluetooth.app.App;
import com.clw.bluetooth.pop.ChoiceDatePop;
import com.clw.bluetooth.pop.ChoiceDatePop.GetDateListener;
import com.clw.bluetooth.util.Tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeAc extends Activity implements OnClickListener{
  private static final String TAG = "WelcomeAc";
  
  private Context mContext=null;
  
  /** 选择器*/
  private ChoiceDatePop mChoiceDatePop;
  
  private TextView tv_time;
  
  /** 输入框*/
  private EditText et_input;
  
  
  /** 日期*/
  private String date="";
  
  private SharedPreferences sp_machine_no=null;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.ac_welcome);
    mContext=WelcomeAc.this;
    initView();
    
  }
  
  /**
   * 初始化
   * */
  private void initView() {
    et_input=(EditText) findViewById(R.id.et_input);
    tv_time=(TextView) findViewById(R.id.tv_time);
    tv_time.setOnClickListener(this);
    
    /* 首选项*/
    sp_machine_no=getSharedPreferences("machine_no", MODE_PRIVATE);
    
    if (!Tools.isNull(App.getMachineNo(mContext))) {
      startActivity(new Intent(mContext,MainAc.class));
    }
  }

  private void showDate(TextView tv) {
    if (null == mChoiceDatePop) {
      mChoiceDatePop = new ChoiceDatePop(this, mGetDateListener);
    }
    mChoiceDatePop.show(tv);
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
   * 按键事件监听
   * 
   * */
  public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_TAB:
//        date=tv_time.getText().toString();
        String machine=et_input.getText().toString();
        if (!Tools.isNull(machine)) {
          Editor ed=sp_machine_no.edit();
          ed.clear();
          ed.putString("no", machine);
          ed.commit();
          startActivity(new Intent(mContext,MainAc.class));
        }else{
          Toast.makeText(mContext, "请输入机台号",Toast.LENGTH_LONG);
        }
        break;

      default:
        break;
    }
    return super.onKeyDown(keyCode, event);
  }

  
}
