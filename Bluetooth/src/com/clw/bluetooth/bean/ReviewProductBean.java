package com.clw.bluetooth.bean;
/** 
 * 复核商品实体类
 * 
 * */
public class ReviewProductBean {
  private static final String TAG = "ReviewProductBean";
  
  private String bwNo; //编码
  private String qty; //数量
  private String weight; //重量
  private String bwdsp; //名字
  
  public String getBwdsp() {
    return this.bwdsp;
  }
  public void setBwdsp(String bwdsp) {
    this.bwdsp = bwdsp;
  }
  public String getWeight() {
    return this.weight;
  }
  public void setWeight(String weight) {
    this.weight = weight;
  }
  public String getBwNo() {
    return this.bwNo;
  }
  public void setBwNo(String bwNo) {
    this.bwNo = bwNo;
  }
  public String getQty() {
    return this.qty;
  }
  public void setQty(String qty) {
    this.qty = qty;
  }
  
  

}
