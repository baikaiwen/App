package com.clw.bluetooth.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




import com.clw.bluetooth.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout.LayoutParams;


/**
 * 工具类
 * 
 * @author bkw
 * 
 */
public class Tools {

	/**
	 * webview 设置html文本
	 * 
	 * @param webview
	 * @param content
	 */
	public static void setWebView(final WebView webview, String content) {
		// webview.getSettings().setBlockNetworkImage(true);
		// webview.getSettings().setUseWideViewPort(false);
		// webview.getSettings()
		// .setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				webview.getSettings().setBlockNetworkImage(false);
			}
		});
		webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webview.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
		webview.setBackgroundColor(Color.parseColor("#00000000"));
		webview.setBackgroundResource(R.color.white);
	}

	/**
	 * 根据年 月 获取对应的月份 天数
	 * */
	public static int getDaysByYearMonth(int year, int month) {
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month - 1);
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int daynum = a.get(Calendar.DATE);
		return daynum;
	}

	/**
	 * 将dp类型的尺寸转换成px类型的尺寸
	 * 
	 * @param size
	 * @param context
	 * @return
	 */
	public static int dip2px(Context context, int dipValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 获取加密签名
	 * 
	 * @param timestamp
	 * @return
	 */
//	public static String getSign(String timestamp) {
//		String sign = "";
//		String key = timestamp + StaticField.VERSION + StaticField.SECURITY_KEY;
//		sign = Tools.MD5(key.toLowerCase());
//		return sign;
//	}

	/**
	 * MD5加密
	 * 
	 * @param plainText
	 * @return
	 */
	public static String MD5(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0) {
					i += 256;
				}

				if (i < 16) {
					buf.append("0");
				}

				buf.append(Integer.toHexString(i));
			}

			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 获取手机唯一设备号
	 */
	private static TelephonyManager telephonyManager;

	public static String getDeviceId(Activity activity) {
		String imei = "";
		telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
		imei = telephonyManager.getDeviceId();
		return imei;
	}

	/**
	 * 
	 *
	 * @param activity
	 * @return
	 */
//	public static String getRegId(Activity activity) {
//		PushBean pushBean = (PushBean) FileUtil.getInstance().getObject("push");
//		if(null != pushBean && !Tools.isNull(pushBean.getUserId())){
//			 return pushBean.getUserId();
//		}
//			String imei = "";
//			telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
//			imei = telephonyManager.getDeviceId();
//			FileUtil.getInstance().saveObject(pushBean, "push");
//			return imei;
//	}
	/**
	 * 根据时间戳返回时分秒 --- 倒计时
	 * 
	 * @param time
	 * @return
	 */
	public static String getMSTime(long time1) {
		long time = time1 / 1000;
		long a, b, c = 0;
		a = time % 60;
		b = time / 60;
		if (b > 60) {
			c = b / 60;
			b = b % 60;
		}
		return c + ":" + b + ":" + a;
	}

	/**
	 * 判断当前是够有网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkIsOnLine(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			// 获取网络连接管理的对象
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null) {
				// 判断当前网络是否已经连接
				if (info.isConnected()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 将Bitmap转换base64加密的字符串
	 */
	public static String bitmaptoString(Bitmap bitmap) {
		String string = null;
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, bStream);
		byte[] bytes = bStream.toByteArray();
		string = Base64.encodeToString(bytes, Base64.DEFAULT);
		return string;
	}

	/** 获取字符串 */
	public static String getString(Context context, int stringid) {
		String content;
		if (context == null || stringid == 0) {
			content = "error";
		} else {
			content = context.getString(stringid);
		}
		return content;
	}

	/** 把字符串转成数字 */
	public static int stringToInt(String num) {
		if (isNull(num)) {
			num = "0";
		}
		return Integer.parseInt(num);
	}

	/**
	 * 地图初始位置定位
	 */
	private static double LONGITUDE = 0;
	private static double LATITUDE = 0;

	// // 存入当前定位城市
	// public static void setCurrLocationCity(Context c, String city) {
	// SharedPreferences sp = c.getSharedPreferences("curr_location_city",
	// Context.MODE_PRIVATE);
	// sp.edit().putString("city", city).commit();
	// }
	//
	// // 取出当前定位城市
	// public static String getCurrLocationCity(Context c) {
	// String str = "";
	// SharedPreferences sp = c.getSharedPreferences("curr_location_city",
	// Context.MODE_PRIVATE);
	// if (sp != null) {
	// str = sp.getString("city", "");
	// }
	// return str;
	// }
	//
	// // 存入当前选择城市
	// public static void setCurrChoisedCity(Context c, String city) {
	// SharedPreferences sp = c.getSharedPreferences("curr_choised_city",
	// Context.MODE_PRIVATE);
	// sp.edit().putString("city", city).commit();
	// }
	//
	// // 取出当前选择城市
	// public static String getCurrChoisedCity(Context c) {
	// String str = "";
	// SharedPreferences sp = c.getSharedPreferences("curr_choised_city",
	// Context.MODE_PRIVATE);
	// if (sp != null) {
	// str = sp.getString("city", "");
	// }
	// return str;
	// }

	// public static double getLongitude(Context c) {// 经度
	// if (LONGITUDE == 0) {
	// SharedPreferences sp = c.getSharedPreferences("longitude",
	// Context.MODE_PRIVATE);
	// LONGITUDE = Double.parseDouble(sp.getString("num", "-1"));
	// }
	// return LONGITUDE;
	// }
	//
	// public static double getLatitude(Context c) {// 纬度
	// if (LATITUDE == 0) {
	// SharedPreferences sp = c.getSharedPreferences("latitude",
	// Context.MODE_PRIVATE);
	// LATITUDE = Double.parseDouble(sp.getString("num", "-1"));
	// }
	// return LATITUDE;
	// }
	//
	// public static void setLongitude(Context c, double longitude) {
	// LONGITUDE = longitude;
	// SharedPreferences sp = c.getSharedPreferences("longitude",
	// Context.MODE_PRIVATE);
	// sp.edit().putString("num", String.valueOf(longitude)).commit();
	// }
	//
	// public static void setLatitude(Context c, double latitude) {
	// LATITUDE = latitude;
	// SharedPreferences sp = c.getSharedPreferences("latitude",
	// Context.MODE_PRIVATE);
	// sp.edit().putString("num", String.valueOf(latitude)).commit();
	// }

	/**
	 * 获取屏幕信息
	 * 
	 * @author Michael.Zhang 2013-10-31 下午5:16:01
	 */
	public static void getScreenWidth(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
//		StaticField.SCREEN_HEIGHT = dm.heightPixels;
//		StaticField.SCREEN_WIDHT = dm.widthPixels;
	}

	public static int[] getScreenSize(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return new int[] { dm.widthPixels, dm.heightPixels };
	}

	/**
	 * 压缩图片
	 * 
	 * @param bm
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap scaleImg(Bitmap bm, int newWidth, int newHeight) {
		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 设置想要的大小
		int newWidth1 = newWidth;
		int newHeight1 = newHeight;
		// 计算缩放比例
		float scaleWidth = ((float) newWidth1) / width;
		float scaleHeight = ((float) newHeight1) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		matrix.postRotate(0);

		return Bitmap.createScaledBitmap(bm, newWidth, newHeight, false);
	}

	/**
	 * 存图片到sdcard
	 * 
	 * @author Michael.Zhang
	 * @param bitmap1
	 */
	public static void storeInSD(Bitmap bitmap, String img_name) {
//		File file = new File(StaticField.SDCARD_PATH);
//		File imageFile = new File(file, img_name);
//		try {
//			imageFile.createNewFile();
//			FileOutputStream fos = new FileOutputStream(imageFile);
//			bitmap.compress(CompressFormat.JPEG, 80, fos);
//			fos.flush();
//			fos.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * 将图片存入sd卡中缓存目录
	 * 
	 * @param bitmap
	 *            图片
	 * @param img_name
	 *            图片名字
	 */
//	public static String cacheImgToSd(Bitmap bitmap, String img_name) {
//		File file = new File(StaticField.SDCARD_IMG_TEMP);
//		if (!file.exists()) {
//			file.mkdirs();
//		}
//		File imageFile = new File(file, img_name);
//		try {
//			imageFile.createNewFile();
//			FileOutputStream fos = new FileOutputStream(imageFile);
//			bitmap.compress(CompressFormat.JPEG, 80, fos);
//			fos.flush();
//			fos.close();
//			return imageFile.getAbsolutePath();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "";
//		}

//	}

	public static void Log(Object s) {
		if (s == null) {
			s = "传进来的是null";
		}

		Log.i("info", s.toString());
	}

	public static void Toast(Context context, String s) {
		if (context != null && s != null) {
			android.widget.Toast.makeText(context, s, android.widget.Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 判断 多个字段的值否为空
	 * 
	 * @return true为null或空; false不null或空
	 */
	public static boolean isNull(String... ss) {
		for (int i = 0; i < ss.length; i++) {
			if (null == ss[i] || ss[i].equals("") || ss[i].equalsIgnoreCase("null")) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 判断 一个字段的值否为空
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNull(String s) {
		if (null == s || s.equals("") || s.equalsIgnoreCase("null")) {
			return true;
		}

		return false;
	}

	/**
	 * 判断两个字段是否一样
	 * 
	 */
	public static boolean judgeStringEquals(String s0, String s1) {
		return s0 != null && null != s1 && s0.equals(s1);
	}

	/**
	 * 根据给定的格式化参数，将给定的字符串时间转换为需要的字符串
	 * 
	 * @param dateString
	 * @param dateFormat
	 * @return java.util.Date
	 */
	public static String parse(String dateString, String dateFormat) {
		if ("".equals(dateString.trim()) || dateString == null) {
			return null;
		}
		long da = Long.parseLong(dateString);
		DateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = new Date(da);

		return sdf.format(date);
	}

	/**
	 * 
	 * 将时间戳转为字符串 到秒
	 * 
	 * 
	 * @param cc_time
	 * @return
	 */
	public static String getStrTime(String cc_time) {
		String re_StrTime = null;
		if (cc_time == null) {
			cc_time = System.currentTimeMillis() + "";
		}

		if (cc_time.length() == 10) { // 单位 秒
			cc_time += "000";
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time));

		return re_StrTime;

	}

	/**
	 * 
	 * 将时间戳转为字符串 到日
	 * 
	 * 
	 * @param cc_time
	 * @return
	 */
	public static String getStrDate(String cc_time) {
		String re_StrTime = "";
		if (cc_time == null) {
			cc_time = System.currentTimeMillis() + "";
		}

		if (cc_time.length() == 10) {
			cc_time += "000";
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long lcc_time = Long.valueOf(cc_time);
		re_StrTime = sdf.format(new Date(lcc_time));

		return re_StrTime;
	}

	/**
	 * 将字符串到分 转换为时间戳
	 * 
	 * @param user_time
	 * @return
	 */
	public static String getTime(String user_time) {
		String re_time = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date d;
		try {
			d = sdf.parse(user_time);
			long l = d.getTime();
			String str = String.valueOf(l);
			re_time = str.substring(0, 13);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return re_time;
	}

	/**
	 * 将字符串到日 转化为时间戳
	 * 
	 * @param time
	 * @return
	 */
	public static String getTimeMillisByDate(String time) {
		String re_time = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date d;
		try {
			d = sdf.parse(time);
			long l = d.getTime();
			String str = String.valueOf(l);
			re_time = str.substring(0, 10);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return re_time;
	}

	/**
	 * 获取空格前面的字符串
	 * 
	 * 
	 * @param time
	 * @return
	 */
	public static String getDate(String time) {
		String date = "";
		date = time.substring(0, time.indexOf(' '));
		return date;
	}

	/**
	 * 获取当前系统时间
	 * 
	 */
	public static String getCurrentTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);

		return str;
	}

	/**
	 * @Title: getCutMinute
	 * @Description: TODO(计算两个时间差，换算出分钟数量)
	 * @param time1
	 *            小的时间
	 * @param time2
	 *            大的时间
	 * @date 2014-6-10 上午11:55:09
	 * @return long 返回类型
	 */
	public static long getCutMinute(String time1, String time2) {
		// 这个是在创建足迹的时候，调用的getCurrentTime方法里面，给date设置的format格式，这里通过这个相同的格式将时间转换回date格式
		String formateString = "yyyy-MM-dd HH:mm:ss ";

		SimpleDateFormat formatter = new SimpleDateFormat(formateString);
		long min = 0;

		try {
			Date date1 = formatter.parse(time1);
			Date date2 = formatter.parse(time2);

			long l = date2.getTime() - date1.getTime();
			min = l / (1000 * 60);
		} catch (ParseException e) {
		}

		return min;
	}

	// 计算两点距离
	public static final double EARTH_RADIUS = 6378137.0;

	/**
	 * @Title: gps2m
	 * @Description: TODO(根据两个经纬度，计算两点之间的距离，得到的单位是米)
	 * @author windy
	 * @date 2014-6-10 下午12:02:53
	 * @return double 返回类型
	 */
	public static double getMileFromTwoLocation(double lat1, double lng1, double lat2, double lng2) {
		double radLat1 = (lat1 * Math.PI / 180.0);
		double radLat2 = (lat2 * Math.PI / 180.0);
		double a = radLat1 - radLat2;
		double b = (lng1 - lng2) * Math.PI / 180.0;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	/**
	 * 将分转为元
	 * 
	 * @return
	 */
	public static double getMoney(String money) {
		if (money != null && !money.equals("") && !money.equals("null")) {
			return Double.parseDouble(money) / 100.0;
		}

		return 0.00;
	}

	/**
	 * 验证身份证号码
	 * 
	 * @param idCard
	 * @return
	 */
	public static boolean isIdCard(String idCard) {
		if (isNull(idCard))
			return false;
		String pattern = "^[0-9]{17}[0-9|xX]{1}$";
		return idCard.matches(pattern);
	}

	/**
	 * 验证手机号码
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean isPhone(String phone) {
		if (isNull(phone))
			return false;
		String pattern = "^((13[0-9])|(147)|(15[^4,\\D])|(183)|(18[0,5-9]))\\d{8}$";

		return phone.matches(pattern);
	}

	/**
	 * 判断email格式是否正确
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * 简单的验证一下银行卡号
	 * 
	 * @param bankCard
	 *            信用卡是16位，其他的是13-19位
	 * @return
	 */
	public static boolean isBankCard(String bankCard) {
		if (isNull(bankCard))
			return false;
		String pattern = "^\\d{13,19}$";
		return bankCard.matches(pattern);
	}

	/**
	 * 将px类型的尺寸转换成dp类型的尺寸
	 * 
	 * @param size
	 * @param context
	 * @return
	 */
	public static int PXtoDP(Context context, int pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dp类型的尺寸转换成px类型的尺寸
	 * 
	 * @param size
	 * @param context
	 * @return
	 */
	public static int DPtoPX(Context context, int dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * double 整理
	 * 
	 * @return
	 */
	public static Double roundDouble(double val, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b = ((0 == val) ? new BigDecimal("0.0") : new BigDecimal(Double.toString(val)));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 判断 列表是否为空
	 * 
	 * @return true为null或空; false不null或空
	 */
	public static boolean isEmptyList(List list) {
		return list == null || list.size() == 0;
	}

	/**
	 * 判断sd卡是否存在
	 * 
	 * @return
	 */
	public static boolean judgeSDCard() {
		String status = Environment.getExternalStorageState();
		return status.equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 判断 http 链接
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isUrl(String url) {
		return null != url && url.startsWith("http://");
	}

	/**
	 * 判断图品路径
	 * 
	 * @return
	 */
	public static boolean isImgUrl(String imgUrl) {
		return isUrl(imgUrl) && imgUrl.endsWith(".jpg");
	}

	/**
	 * 获得hashmap中value的值,以List 返回
	 * 
	 * @param hashMap
	 * @return
	 */
	public static List<String> getListByHashMap(HashMap<String, String> hashMap) {
		List<String> list = new ArrayList<String>();
		Iterator iter = hashMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			list.add((String) val);
		}

		return list;
	}

	/**
	 * 获取版本号 给用户看的
	 * 
	 * @return
	 */
	public static String getVersionName(Activity activity) {
		String versionName = "0";
		if (getPackageInfo(activity) != null) {
			versionName = getPackageInfo(activity).versionName;
		}

		return versionName;
	}

	/**
	 * 获取版本号 系统识别用的
	 * 
	 * @return
	 */
	public static int getVersionCode(Activity activity) {
		int versionCode = 0;
		if (getPackageInfo(activity) != null) {
			versionCode = getPackageInfo(activity).versionCode;
		}

		return versionCode;
	}

	private static PackageInfo getPackageInfo(Activity activity) {
		String packageName = activity.getPackageName();
		try {
			return activity.getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 获取保存到View的Tag中的字符串
	 * 
	 * @param v
	 * @return
	 */
	public static String getViewTagString(View v) {
		try {
			return v.getTag().toString();
		} catch (Exception e) {
			return "0";
		}
	}

	/**
	 * 格式化价格 支付宝用
	 * 
	 * @return
	 */
	public static String FormatPrice(double price) {
		DecimalFormat format = new DecimalFormat("0.00");
		String totalprice = format.format(price);
		return totalprice;
	}
	
	/**
   * 判断是否有网络
   * 
   * @param context
   * @return
   */
  public static boolean isNetworkConnected(Context context) {
    ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
    return (mNetworkInfo != null && mNetworkInfo.isAvailable());
  }
  

  
  
}
