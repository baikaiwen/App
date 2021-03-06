package com.clw.bluetooth.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BtSPP {
	public static String ErrorMessage="No Error";
	private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static BluetoothAdapter myBluetoothAdapter;
	private static BluetoothDevice myDevice;
	private static BluetoothSocket mySocket = null;
	private static OutputStream myOutStream = null;
	private static InputStream myInStream = null;
	
	public static boolean OpenPrinter(String BDAddr)
	{
    	if(BDAddr==""||BDAddr==null)
    	{
    		ErrorMessage="û��ѡ���ӡ��";
    		return false;
    	}
    	myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	if(myBluetoothAdapter==null)
    	{
    		ErrorMessage="����ϵͳ����";
    		return false;
    	}
    	myDevice = myBluetoothAdapter.getRemoteDevice(BDAddr);
    	if(myDevice==null)
    	{
    		ErrorMessage="��ȡ�����豸����";
    		return false;
    	}
		if(!BtSPP.SPPOpen(myBluetoothAdapter, myDevice))
		{
			return false;
		}
    	return true;
	}
	public static boolean SPPOpen(BluetoothAdapter bluetoothAdapter, BluetoothDevice btDevice)
	{
		boolean error=false;
		myBluetoothAdapter = bluetoothAdapter;
		myDevice = btDevice;

		if(!myBluetoothAdapter.isEnabled())
		{
			ErrorMessage = "����������û�д�";
	        return false;
		}
		myBluetoothAdapter.cancelDiscovery();

		try
		{
			//mySocket = myDevice.createRfcommSocketToServiceRecord(SPP_UUID);
			Method m = myDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
			mySocket = (BluetoothSocket) m.invoke(myDevice, 1); 
		}
		catch (SecurityException e){
			mySocket = null;
			ErrorMessage = "�����˿ڴ���";
			return false;
		} 
		catch (NoSuchMethodException e) {
			mySocket = null;
			ErrorMessage = "�����˿ڴ���";
			return false;
		} catch (IllegalArgumentException e) {
			mySocket = null;
			ErrorMessage = "�����˿ڴ���";
			return false;
		} catch (IllegalAccessException e) {
			mySocket = null;
			ErrorMessage = "�����˿ڴ���";
			return false;
		} catch (InvocationTargetException e) {
			mySocket = null;
			ErrorMessage = "�����˿ڴ���";
			return false;
		}

		try 
		{
			mySocket.connect();
		} 
		catch (IOException e2) 
		{
			ErrorMessage = e2.getLocalizedMessage();//"�޷�����������ӡ��";
			mySocket = null;
			return false;
		}

		try 
		{
			myOutStream = mySocket.getOutputStream();
		} 
		catch (IOException e3) 
		{
			myOutStream = null;
			error = true;
		}

		try 
		{
			myInStream = mySocket.getInputStream();
		} 
		catch (IOException e3) 
		{
			myInStream = null;
			error = true;
		}

		if(error)
		{
			SPPClose();
			return false;
		}
		
		return true;
	}
	public static boolean SPPClose()
	{
		try {Thread.sleep(1000);} catch (InterruptedException e) {}
		if(myOutStream!=null)
		{
			try{myOutStream.flush();}catch (IOException e1){}
			try{myOutStream.close();}catch (IOException e){}
			myOutStream=null;
		}
		if(myInStream!=null)
		{
			try{myInStream.close();}catch(IOException e){}
			myInStream=null;
		}
		if(mySocket!=null)
		{
			try{mySocket.close();}catch (IOException e){}
			mySocket=null;
		}
		try {Thread.sleep(200);} catch (InterruptedException e) {}
		return true;
	}
	
	public static boolean SPPWrite(byte[] Data)
	{
		try 
		{
			myOutStream.write(Data);
		} 
		catch (IOException e) 
		{
			ErrorMessage = "������������ʧ��";
			return false;
		}
		return true;
	}
	public static boolean SPPWrite(byte[] Data,int DataLen)
	{
		try 
		{
			myOutStream.write(Data,0,DataLen);
		} 
		catch (IOException e) 
		{
			ErrorMessage = "������������ʧ��";
			return false;
		}
		return true;
	}
	public static void SPPFlush()
	{
		int i=0,DataLen=0;
		try 
		{
			DataLen = myInStream.available();
		} 
		catch (IOException e1) 
		{
		}
		for(i=0;i<DataLen;i++)
		{
			try 
			{
				myInStream.read();
			} 
			catch (IOException e) 
			{
			}
		}
	}
	public static boolean SPPRead(byte[] Data,int DataLen)
	{
		return SPPReadTimeout(Data,DataLen,2000);
	}
	public static boolean SPPReadTimeout(byte[] Data,int DataLen,int Timeout)
	{
		int i;
		for(i=0;i<(Timeout/50);i++)
		{
			try 
			{
				if(myInStream.available()>=DataLen)
				{
					try 
					{
						myInStream.read(Data,0,DataLen);
						return true;
					} 
					catch (IOException e) 
					{
						ErrorMessage = "��ȡ��������ʧ��";
						return false;
					}
				}
			} 
			catch (IOException e) 
			{
				ErrorMessage = "��ȡ��������ʧ��";
				return false;
			}
			try 
			{
				Thread.sleep(50);
			} 
			catch (InterruptedException e) 
			{
				ErrorMessage = "��ȡ��������ʧ��";
				return false;
			}
		}
		ErrorMessage = "���������ݳ�ʱ";
		return false;
	}
}
