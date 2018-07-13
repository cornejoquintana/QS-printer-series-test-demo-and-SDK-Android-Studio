package com.zkc.pinter.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.printertools.bt.R;
import com.zkc.helper.printer.BarcodeCreater;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PrintTextActivity extends Activity {
	List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
	private TextView et_input;
	private CheckBox checkBoxAuto;
	private Button bt_print;
	private Thread autoprint_Thread;

	int times = 0;// Automatic print time interval
	boolean isPrint = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print_text);
		isPrint = true;
		et_input = (EditText) findViewById(R.id.et_input);
		et_input.setText("¥Ú”°≤‚ ‘(Printer Testing)");


		bt_print = (Button) findViewById(R.id.bt_print);
		bt_print.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String message = et_input.getText().toString();
				MainActivity.pl.printText(message + "\r\n");
			}
		});

		checkBoxAuto = (CheckBox) findViewById(R.id.checkBoxAuto);

		// Auto Print
		autoprint_Thread = new Thread() {
			public void run() {
				while (isPrint) {
					if (checkBoxAuto.isChecked()) {
						String message = et_input.getText().toString();
						MainActivity.pl.printText(message);
						try {
							Thread.sleep(times);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		};
		autoprint_Thread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		Resources res = getResources();
		String[] cmdStr = res.getStringArray(R.array.cmd);
		for (int i = 0; i < cmdStr.length; i++) {
			String[] cmdArray = cmdStr[i].split(",");
			if (cmdArray.length == 2) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("title", cmdArray[0]);
				map.put("description", cmdArray[1]);
				menu.add(0, i, i, cmdArray[0]);
				listData.add(map);
			}
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		Map map = listData.get(item.getItemId());
		String cmd = map.get("description").toString();
		byte[] bt = PrintCmdActivity.hexStringToBytes(cmd);
		MainActivity.pl.write(bt);
		Toast toast = Toast.makeText(this, "Send Success", Toast.LENGTH_SHORT);
		toast.show();
		return false;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		isPrint = false;
		super.onStop();
	}

	private static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();

		if (width > w) {
			float scaleWidth = ((float) w) / width;
			float scaleHeight = ((float) h) / height + 24;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleWidth);
			Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
					height, matrix, true);
			return resizedBitmap;
		} else {
			Bitmap resizedBitmap = Bitmap.createBitmap(w, height + 24,
					Config.RGB_565);
			Canvas canvas = new Canvas(resizedBitmap);
			Paint paint = new Paint();
			canvas.drawColor(Color.WHITE);
			canvas.drawBitmap(bitmap, (w - width) / 2, 0, paint);
			return resizedBitmap;
		}
	}
}
