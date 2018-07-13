package com.zkc.helper.printer.bt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.printertools.bt.R;
import com.zkc.helper.printer.BlueToothService;
import com.zkc.helper.printer.Device;
import com.zkc.helper.printer.PrintService;
import com.zkc.helper.printer.PrinterClass;
import com.zkc.helper.printer.BlueToothService.OnReceiveDataHandleEvent;
import com.zkc.pinter.activity.MainActivity;

public class BtService extends PrintService implements PrinterClass {

	Context context;
	Handler mhandler, handler;
	public static BlueToothService mBTService = null;

	public BtService(Context _context, Handler _mhandler, Handler _handler) {
		context = _context;
		mhandler = _mhandler;
		handler = _handler;
		mBTService = new BlueToothService(context, mhandler);

		mBTService.setOnReceive(new OnReceiveDataHandleEvent() {
			@Override
			public void OnReceive(final BluetoothDevice device) {
				// TODO Auto-generated method stub
				if (device != null) {
					Device d = new Device();
					d.deviceName = device.getName();
					d.deviceAddress = device.getAddress();
					Message msg = new Message();
					msg.what = 1;
					msg.obj = d;
					handler.sendMessage(msg);
					setState(STATE_SCANING);
				} else {
					Message msg = new Message();
					msg.what = 8;
					handler.sendMessage(msg);
				}
			}
		});
	}

	@Override
	public boolean open(Context context) {
		// TODO Auto-generated method stub
		mBTService.OpenDevice();
		return true;
	}

	@Override
	public boolean close(Context context) {
		// TODO Auto-generated method stub
		mBTService.CloseDevice();
		return false;
	}

	@Override
	public void scan() {
		// TODO Auto-generated method stub
		if (!mBTService.IsOpen()) {// 锟叫讹拷6锟斤拷锟角凤拷锟�
			mBTService.OpenDevice();
			return;
		}
		if (mBTService.getState() == STATE_SCANING)
			return;

		new Thread() {
			public void run() {
				mBTService.ScanDevice();
			}
		}.start();
	}

	@Override
	public boolean connect(String device) {
		// TODO Auto-generated method stub
		if (mBTService.getState() == STATE_SCANING) {
			stopScan();
		}
		if (mBTService.getState() == STATE_CONNECTING) {
			return false;
		}
		if (mBTService.getState() == STATE_CONNECTED) {
			mBTService.DisConnected();
		}
		mBTService.ConnectToDevice(device);// l锟斤拷6锟斤拷
		return true;
	}

	@Override
	public boolean disconnect() {
		// TODO Auto-generated method stub
		mBTService.DisConnected();
		return true;
	}

	@Override
	public int getState() {
		// TODO Auto-generated method stub
		return mBTService.getState();
	}

	@Override
	public boolean write(byte[] bt) {
		if (getState() != PrinterClass.STATE_CONNECTED) {
			Toast toast = Toast.makeText(context, context.getResources()
					.getString(R.string.str_lose), Toast.LENGTH_SHORT);
			toast.show();
			return false;
		}
		mBTService.write(bt);
		return true;
	}

	@Override
	public boolean printText(String textStr) {
		// TODO Auto-generated method stub

		/*
		 * byte[] buffer = getText(textStr);
		 * 
		 * if (buffer.length <= 100) { return write(buffer); } int sendSize =
		 * 100; int issendfull=0; for (int j = 0; j < buffer.length; j +=
		 * sendSize) {
		 * 
		 * byte[] btPackage = new byte[sendSize]; if (buffer.length - j <
		 * sendSize) { btPackage = new byte[buffer.length - j]; }
		 * System.arraycopy(buffer, j, btPackage, 0, btPackage.length);
		 * write(btPackage);
		 * 
		 * try { Thread.sleep(86); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } }
		 * 
		 * return true;
		 */
		return write(getText(textStr));
	}

	@Override
	public boolean printImage(Bitmap bitmap) {
		if (getBufferState(10000)) {
			write(getImage(bitmap));
			return write(new byte[] { 0x0a });
		}
		return false;
	}

	@Override
	public boolean printUnicode(String textStr) {
		// TODO Auto-generated method stub
		return write(getTextUnicode(textStr));
	}

	@Override
	public boolean IsOpen() {
		// TODO Auto-generated method stub
		return mBTService.IsOpen();
	}

	@Override
	public void stopScan() {
		// TODO Auto-generated method stub
		if (MainActivity.pl.getState() == PrinterClass.STATE_SCANING) {
			mBTService.StopScan();
			mBTService.setState(PrinterClass.STATE_SCAN_STOP);
		}
	}

	@Override
	public void setState(int state) {
		// TODO Auto-generated method stub
		mBTService.setState(state);
	}

	@Override
	public List<Device> getDeviceList() {
		List<Device> devList = new ArrayList<Device>();
		// TODO Auto-generated method stub
		Set<BluetoothDevice> devices = mBTService.GetBondedDevice();
		for (BluetoothDevice bluetoothDevice : devices) {
			Device d = new Device();
			d.deviceName = bluetoothDevice.getName();
			d.deviceAddress = bluetoothDevice.getAddress();
			devList.add(d);
		}
		return devList;
	}

	private boolean getBufferState(int sleepTime) {
		PrintService.getState = true;
		for (int i = 0; i < sleepTime; i++) {
			write(new byte[] { 0x1b, 0x76 });
			if (!PrintService.getState) {
				PrintService.getState = false;
				return true;
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		PrintService.getState = false;
		return false;
	}

	@Override
	public boolean printImage2(Bitmap bitmap) {
		write(getImage(bitmap));
		return write(new byte[] { 0x0a });
	}
}
