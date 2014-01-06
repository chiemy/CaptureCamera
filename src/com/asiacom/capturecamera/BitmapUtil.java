package com.asiacom.capturecamera;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * 对图片进行处理的工具类，可对图片进行旋转、截取、缩放等操作
 * @author asiacom104
 */
public class BitmapUtil {
	/**
	 * 计算采样比例
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	    if (height > reqHeight || width > reqWidth) {
	
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	
	    return inSampleSize;
	}
	
	/**
	 * 从图片字节数组中生产指定宽高的图片
	 * （保持原图的宽高比，采样率取 原图高度/需要高度 与 原图宽度/需要宽度 中比例较小的一个）
	 * @param data 图片字节数组
	 * @param reqWidth	需要的宽度
	 * @param reqHeight	需要的高度
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(byte [] data, int reqWidth, int reqHeight) {

	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    //只获得原始图片的宽度和高度
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeByteArray(data, 0, data.length,options);
	    options.inPurgeable = true;
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    options.inJustDecodeBounds = false;
	    
	    return BitmapFactory.decodeByteArray(data,0,data.length, options);
	}
	
	/**
	 * 从指定的路径下，生成指定宽高的图片
	 * @param filePath 文件路径
	 * @param reqWidth 宽
	 * @param reqHeight 高
	 * @return 
	 */
	public static Bitmap decodeSampledBitmapFromResource(String filePath, int reqWidth, int reqHeight) {

	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(filePath, options);
	    options.inPurgeable = true;
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(filePath, options);
	}
	
	/**
	 * 将图片旋转一定角度
	 * @param bitmap 要旋转的图片
	 * @param degree 旋转角度（正数为逆时针）
	 * @return	旋转后的图片
	 */
	public static Bitmap getRotateBitmap(Bitmap bitmap,float degree){
		Matrix matrix = new Matrix();
		matrix.reset();
		matrix.setRotate(degree,
                (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
		Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),matrix,true);
		return rotateBitmap;
	}
	
	/**
	 * 截取图片
	 * @param bitmap 被截取的图片
	 * @param rect 截取范围
	 * @return
	 */
	public static Bitmap captureBitmap(Bitmap bitmap,RectF rect){
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int captrueWidth = (int)(rect.right-rect.left);
		int captrueHeight = (int)(rect.bottom-rect.top);
		captrueWidth = captrueWidth > width ? width : captrueWidth;
		captrueHeight = captrueHeight > height ? height : captrueHeight;
		return Bitmap.createBitmap(bitmap, (int)rect.left, (int)rect.top,captrueWidth, captrueHeight);
	}
}

