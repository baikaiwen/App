package com.clw.bluetooth.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * 蓝牙服务类
 * */
public class BluetoothService {
  private static final String TAG = "BluetoothService";

  private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

  /**
   * 打开蓝牙
   */
  public void openBluetooth(Activity activity) {
    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    activity.startActivityForResult(enableBtIntent, 1);

  }

  /**
   * 关闭蓝牙
   */
  public void closeBluetooth() {
    this.bluetoothAdapter.disable();
  }

  /**
   * 判断蓝牙是否打开
   * 
   * @return boolean
   */
  public boolean isOpen() {
    return this.bluetoothAdapter.isEnabled();

  }

  /**
   * 蓝牙广播接收器
   */
  private BroadcastReceiver receiver = new BroadcastReceiver() {

    ProgressDialog progressDialog = null;

    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (BluetoothDevice.ACTION_FOUND.equals(action)) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
          // addBandDevices(device);
        } else {
          // addUnbondDevices(device);
        }
      } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
        progressDialog = ProgressDialog.show(context, "请稍等...", "搜索蓝牙设备中...", true);

      } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
        System.out.println("设备搜索完毕");
        progressDialog.dismiss();

        // addUnbondDevicesToListView();
        // addBondDevicesToListView();
        // bluetoothAdapter.cancelDiscovery();
      }
      if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
        if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
          System.out.println("--------打开蓝牙-----------");
          // switchBT.setText("关闭蓝牙");
          // searchDevices.setEnabled(true);
          // bondDevicesListView.setEnabled(true);
          // unbondDevicesListView.setEnabled(true);
        } else if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
          System.out.println("--------关闭蓝牙-----------");
          // switchBT.setText("打开蓝牙");
          // searchDevices.setEnabled(false);
          // bondDevicesListView.setEnabled(false);
          // unbondDevicesListView.setEnabled(false);
        }
      }

    }

  };
}
