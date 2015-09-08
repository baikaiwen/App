package com.clw.bluetooth.bean;

import java.io.Serializable;

/**
 * 用于保存对应车次中的品名,数量
 * */
public class BwNoQtyBean implements Serializable{
  private static final long serialVersionUID = 1318786463655209402L;

  private static final String TAG = "BwNoQtyBean";
  
  private String f_bw_no;
  private String f_qty;
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
