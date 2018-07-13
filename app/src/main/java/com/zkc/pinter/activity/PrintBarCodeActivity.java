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

public class PrintBarCodeActivity extends Activity {
	private TextView et_input;
	private Button bt_bar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print_barcode);
		et_input=(EditText)findViewById(R.id.et_input);
		bt_bar = (Button) findViewById(R.id.bt_bar);
		bt_bar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (MainActivity.pl.getState() != PrinterClass.STATE_CONNECTED) {
					Toast.makeText(
							PrintBarCodeActivity.this,
							PrintBarCodeActivity.this.getResources().getString(
									R.string.str_unconnected), 2000).show();
					return;
				}
				String message = et_input.getText().toString();

				if (message.getBytes().length > message.length()) {
					Toast.makeText(
							PrintBarCodeActivity.this,
							PrintBarCodeActivity.this.getResources().getString(
									R.string.str_cannotcreatebar), 2000).show();
					return;
				}
				if (message.length() > 0) {
					
					byte[] btdata=null;
					try {
						btdata=message.getBytes("ASCII");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//Enable the barcode
					MainActivity.pl.write(new byte[]{0x1d,0x45,0x43,0x01});
					//Set the barcode height is 162
					MainActivity.pl.write(new byte[]{0x1d,0x68,(byte) 0xa2});
					//Set HRI character print location on bottom
					MainActivity.pl.write(new byte[]{0x1d,0x48,0x02});
					//Print the barcode use code128
					
					byte[] qrHead=new byte[]{0x1d,0x6b,0x49,(byte) btdata.length};
					byte[] barCodeData=new byte[qrHead.length+btdata.length];
					System.arraycopy(qrHead, 0, barCodeData, 0, qrHead.length);
					System.arraycopy(btdata, 0, barCodeData, qrHead.length, btdata.length);
					MainActivity.pl.write(barCodeData);
					
				}

			}
		});
	}
}
