package com.clw.bluetooth.bean;

import java.io.Serializable;

/**
 * 用于已经装箱好的品名与数量
 * */
public class YetBwNoQtyBean implements Serializable{
  private static final long serialVersionUID = 1L;
  private static final String TAG = "YetBwNoQtyBean";
  
  private String f_bw_no;  //品名
  private String f_qty;   //数量
  public String getF_bw_no() {
    return this.f_bw_no;
  }
  public void setF_bw_no(String f_bw_no) {
    this.f_bw_no = f_bw_no;
  }
  public String getF_qty() {
    return this.f_qty;
  }
  public void setF_qty(String f_qty) {
    this.f_qty = f_qty;
  }
  
  
}
