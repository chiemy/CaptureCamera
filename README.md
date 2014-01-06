CaptureCamera
==============

![alt text](/assets/capture.png "")

此工程包含以下内容：

- 图片处理的工具类（旋转、截取、缩放等操作）：`BitmapUtil`
- 对拍照内容进行截取的相机:`CameraActivity`


用法
-------

**1.相机**

-------

将所有代码和布局文件`camera_layout.xml`、资源文件直接复制到所需工程中，拍照时启动`CameraActivity`即可，图片默认保存到SD卡下与应用名称相同的文件夹下，也可以自定义保存路径

    Intent intent = new Intent(MainActivity.this,CameraActivity.class);
	String savePath = "图片保存路径"; 
	intent.putExtra("savePath",savePath);
	boolean capture_available = true; //是否显示选择框
	intent.putExtra("capture_available",capture_available);
	boolean moveable = false; //选择框可移动性
	intent.putExtra("capture_rect_moveable",moveable);
	startActivityForResult(intent,requestcode);

在`onActivityResult()`中接收结果
	
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
			case requestcode:
				if(resultCode == RESULT_OK){
					//得到保存后的图片名称
					String fileName = data.getStringExtra("fileName");
				}
				break;
		}
	}

如果对`camera_layout.xml`进行重新布局，需要注意，`CameraPreview`父控件必须为`RelativeLayout`，否则报转型错误。同时，`CaptureView`和`CameraPreview`的大小要相同

	<?xml version="1.0" encoding="utf-8"?>
    	<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    		xmlns:tools="http://schemas.android.com/tools"
    		android:layout_width="match_parent"
    		android:layout_height="match_parent"
    		tools:context=".MainActivity" >
    
    		<com.asiacom.capturecamera.CameraPreview
   				android:id="@+id/camera"
    			android:layout_width="match_parent"
    			android:layout_height="match_parent"
     		/>
			<com.asiacom.capturecamera.CaptureView
        		android:id="@+id/captureView"
        		android:layout_width="match_parent"
       			android:layout_height="match_parent"
        		android:layout_centerInParent="true"
         	****/>
    	</RelativeLayout>

如果使用其他布局，需要对`CameraPreiew.java`类中`adjustSurfaceLayoutSize`方法中的部分代码进行修改。

**2.图片处理**

---

`BitmapUtil`是对图片进行处理的工具类，包括以下静态方法：

(1)缩放处理

- `public static Bitmap decodeSampledBitmapFromResource(byte [] data, int reqWidth, int reqHeight)；`

	此方法主要对图片进行缩放处理，根据reqWidth和reqHeight对缩放比例进行计算

	（保持原图的宽高比，采样率取 原图高度/需要高度 与 原图宽度/需要宽度 中比例较小的一个）

	data：图片的字节数组

	reqWidth：需要的宽度
	
	reqHeight：需要的高度




- `public static Bitmap decodeSampledBitmapFromResource(String filePath, int reqWidth, int reqHeight)`

	上边方法的重载方法，第一个参数为图片路径，其他参数相同。

(2)图片旋转

- `public static Bitmap getRotateBitmap(Bitmap bitmap,float degree)`
	
	将图片进行旋转，正数代表逆时针旋转。

(3)图片截取

- `public static Bitmap captureBitmap(Bitmap bitmap,RectF rect)`
	
	RectF rect 为截取范围