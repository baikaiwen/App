package com.clw.bluetooth.bean;

public class ProductBean {
  private static final String TAG = "ProductBean";

  private static ProductBean pd = null;

  public static ProductBean getInstance() {
    if (null == pd) {
      pd = new ProductBean();
    }
    return pd;
  }

  private String count;
  private String total;
  private String date; // 日期
  private String line; // 线路
  private String loading; // 装车号
  private String booth; // 摊位
  private String custmo; // 客户
  private String weight; //重量
  

  public String getWeight() {
    return this.weight;
  }

  public void setWeight(String weight) {
    this.weight = weight;
  }

  public String getCount() {
    return this.count;
  }

  public void setCount(String count) {
    this.count = count;
  }

  public String getTotal() {
    return this.total;
  }

  public void setTotal(String total) {
    this.total = total;
  }

  public String getDate() {
    return this.date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getLine() {
    return this.line;
  }

  public void setLine(String line) {
    this.line = line;
  }

  public String getLoading() {
    return this.loading;
  }

  public void setLoading(String loading) {
    this.loading = loading;
  }

  public String getBooth() {
    return this.booth;
  }

  public void setBooth(String booth) {
    this.booth = booth;
  }

  public String getCustmo() {
    return this.custmo;
  }

  public void setCustmo(String custmo) {
    this.custmo = custmo;
  }

}
