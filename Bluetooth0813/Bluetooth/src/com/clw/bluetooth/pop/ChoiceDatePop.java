package com.clw.bluetooth.pop;

import com.clw.bluetooth.R;
import com.clw.bluetooth.util.DateWheel;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;


/**
 * 选择起始日期日期
 * 
 * @author ZhangYi 2014年7月16日 下午3:32:14
 * 
 */

public class ChoiceDatePop extends PopupWindow implements OnClickListener {
	public interface GetDateListener {
		/**
		 * endTime可为null或""
		 */
		void getTime(String startTime, String endTime);
	}

	private Activity mActivity;
	private GetDateListener mGetDateListener;
	private DateWheel mDateWheel; // 滚轮开始时间

	public ChoiceDatePop(Activity activity, GetDateListener getDateListener) {
		mActivity = activity;
		this.mGetDateListener = getDateListener;

		View view = LayoutInflater.from(activity).inflate(
				R.layout.pop_choice_date, null);
		mDateWheel = new DateWheel(view);
		mDateWheel.initDateTimePicker();

		view.findViewById(R.id.btn_cancel).setOnClickListener(this);
		view.findViewById(R.id.btn_confirm).setOnClickListener(this);

		setContentView(view);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setAnimationStyle(R.style.anim_issue_popwindow_bottom);
		setOutsideTouchable(false);
	}

	/**
	 * 显示
	 * 
	 * @author ZhangYi 2014年7月16日 下午3:35:34
	 */
	public void show(View parentView) {
		if (!isShowing()) {
			showAtLocation(parentView, Gravity.BOTTOM
					| Gravity.CENTER_HORIZONTAL, 0, 0);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			if (isShowing()) {
				dismiss();
			}
			break;
		case R.id.btn_confirm:
			mGetDateListener.getTime(mDateWheel.getTime(), null);
			if (isShowing()) {
				dismiss();
			}
			break;
		}
	}
}
