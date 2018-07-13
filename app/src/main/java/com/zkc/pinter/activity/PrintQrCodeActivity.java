package com.zkc.pinter.activity;

import java.io.UnsupportedEncodingException;

import com.example.printertools.bt.R;
import com.zkc.helper.printer.PrinterClass;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PrintQrCodeActivity extends Activity {
	private TextView et_input;
	private Button bt_2d;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print_qrcode);

		et_input = (EditText) findViewById(R.id.et_input);
		bt_2d = (Button) findViewById(R.id.bt_2d);
		bt_2d.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (MainActivity.pl.getState() != PrinterClass.STATE_CONNECTED) {
					Toast.makeText(
							PrintQrCodeActivity.this,
							PrintQrCodeActivity.this.getResources().getString(
									R.string.str_unconnected), 2000).show();
					return;
				}
				String str=et_input.getText().toString();
				byte[] btdata=null;
				try {
					btdata=str.getBytes("ASCII");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String strdata=bytesToString(btdata).toString();
				
				
				short datalen=(short) (btdata.length+3);
				byte pL=(byte)(datalen&0xff);
				byte pH=(byte)(datalen>>8);
				
				
				//����ȼ� 30%
				MainActivity.pl.write(new byte[]{0x1d,0x28,0x6b,0x03,0x00,0x31,0x43,0x33});
				
				//���
				MainActivity.pl.write(new byte[]{0x1d,0x28,0x6b,0x03,0x00,0x31,0x45,0x05});

				byte[] qrHead=new byte[]{0x1d,0x28,0x6b,pL,pH,0x31,0x50,0x30};
				byte[] qrData=new byte[qrHead.length+datalen];
				System.arraycopy(qrHead, 0, qrData, 0, qrHead.length);
				System.arraycopy(btdata, 0, qrData, qrHead.length, btdata.length);
				MainActivity.pl.write(qrData);
				MainActivity.pl.write(new byte[]{0x1d,0x28,0x6b,0x03,0x00,0x31,0x51,0x30});
			}
		});
	}
	
	/**
	  * ��byte����ת��Ϊ�ַ�����ʽ��ʾ��ʮ������������鿴
	  */
	 public static StringBuffer bytesToString(byte[] bytes)
	 {
	  StringBuffer sBuffer = new StringBuffer();
	  for (int i = 0; i < bytes.length; i++)
	  {
	   String s = Integer.toHexString(bytes[i] & 0xff);
	   if (s.length() < 2)
	    sBuffer.append('0');
	   sBuffer.append(s + " ");
	  }
	  return sBuffer;
	 }


	 private static byte charToByte(char c)
	 {
	  return (byte) "0123456789abcdef".indexOf(c);
	 }
}
