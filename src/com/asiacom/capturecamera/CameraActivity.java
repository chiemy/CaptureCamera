package com.asiacom.capturecamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class CameraActivity extends Activity implements OnClickListener,
		Camera.PictureCallback {
	private CaptureView captrueView;
	private CameraPreview camera;
	private Button button,saveBt,cancelBt,exitBtn;
	private byte[] picDate;
	private int screenWidth;
	private int screenHeight;
	public static String PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath();
	
	private boolean enableCapture;
	private boolean rectMovable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.camera_layout);
		initView();
		AppInfo info = new AppInfo(this);
		PATH = PATH + File.separator + info.getAppName();
		Intent intent = getIntent();
		//是否显示截取框
		PATH = intent.getStringExtra("savePath");
		enableCapture = intent.getBooleanExtra("capture_available", true);
		rectMovable = intent.getBooleanExtra("capture_rect_moveable", false);
		
		screenWidth = getResources().getDisplayMetrics().widthPixels;
		screenHeight = getResources().getDisplayMetrics().heightPixels;
	}
	
	
	private void initView() {
		captrueView = (CaptureView) this.findViewById(R.id.captureView);
		if(!enableCapture){
			captrueView.setVisibility(View.GONE);
		}
		// 框体可移动性
		captrueView.setRectMoveable(rectMovable);
		// 不设置，默认为true
		//captrueView.setFullScreen(true);
		// 设置边框颜色
		// captrueView.setRectLineColor(0xff558866);
		button = (Button) findViewById(R.id.shutter_button);
		button.setOnClickListener(this);
		camera = (CameraPreview)this.findViewById(R.id.camera);
		saveBt = (Button) this.findViewById(R.id.save);
		cancelBt = (Button) this.findViewById(R.id.cancel);
		exitBtn = (Button) this.findViewById(R.id.close_btn);
		
		saveBt.setOnClickListener(this);
		cancelBt.setOnClickListener(this);
		exitBtn.setOnClickListener(this);
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		camera.stopPreview();
		picDate = data;
		cancelBt.setVisibility(View.VISIBLE);
		saveBt.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.shutter_button:
			button.setClickable(false);
			camera.takePicture(this);
			break;
		case R.id.save:
			RectF rect = captrueView.getCaptureRect();
			
			long dateTaken = System.currentTimeMillis();
			String filename = Producer.produceImgName();
			
			Bitmap bitmap = captureBitmap(rect);
			//保存图片
			SaveImage.saveImage(bitmap, PATH, filename);
			//将图片信息保存到Android相册中(在相册中可看到图片)
			SaveImage.insertImageIntoMedia(getContentResolver(), dateTaken, PATH, filename);
			
			Intent intent = getIntent();
			intent.putExtra("fileName", filename);
			setResult(RESULT_OK, intent);
			finish();
			
			break;
		case R.id.cancel:
			button.setClickable(true);
			cancelBt.setVisibility(View.GONE);
			saveBt.setVisibility(View.GONE);
			camera.startPreview();
			break;
		case R.id.close_btn:
			this.finish();
			break;
		}
	}
	
	/**
	 * 截取图片
	 * @param rect
	 * @return
	 */
	private Bitmap captureBitmap(RectF rect){
		Bitmap bitmap = BitmapFactory.decodeByteArray(picDate, 0, picDate.length);
		Bitmap rotateBitmap = BitmapUtil.getRotateBitmap(bitmap, 90);
		bitmap.recycle();
		Bitmap scaleBitmap = Bitmap.createScaledBitmap(rotateBitmap,screenWidth, screenHeight, true);
		rotateBitmap.recycle();
		Bitmap captureBitmap = BitmapUtil.captureBitmap(scaleBitmap, rect);
		scaleBitmap.recycle();
		return captureBitmap;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		camera.stop();
	}
}
