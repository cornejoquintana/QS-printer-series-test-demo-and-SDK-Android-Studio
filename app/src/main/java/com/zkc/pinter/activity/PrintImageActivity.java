package com.zkc.pinter.activity;

import java.io.FileNotFoundException;

import com.example.printertools.bt.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class PrintImageActivity extends Activity {
	
	private static final int REQUEST_EX = 1;
	private Bitmap btMap = null;
	private ImageView iv;
	private Button bt_image;// ͼƬ��ӡ
	private Button bt_openpic;// ��ͼƬĿ¼
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print_image);
		
		iv = (ImageView) findViewById(R.id.iv_test);
		
		bt_openpic = (Button) findViewById(R.id.bt_openpci);
		bt_openpic.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, REQUEST_EX);
			}
		});
		
		bt_image = (Button) findViewById(R.id.bt_image);
		bt_image.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (btMap != null) {
					MainActivity.pl.printImage(btMap);
					return;
				}
			}
		});
		btMap=BitmapFactory.decodeResource(getResources(), R.drawable.demo);
		iv.setImageBitmap(btMap);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_EX && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			ContentResolver cr = this.getContentResolver();

			try {
				btMap = BitmapFactory.decodeStream(cr
						.openInputStream(selectedImage));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			iv.setImageBitmap(btMap);
		}

	}
}
