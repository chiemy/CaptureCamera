package com.asiacom.capturecamera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

class SaveImage {
	private static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	/**
	 * 保存图片
	 * @param bm 图片
	 * @param path 文件夹
	 * @return
	 */
	public static boolean saveImage(Bitmap bm,String path,String fileName){
		if(!isFileExist(path)){
			File file = new File(path);
			file.mkdirs();
		}
		byte [] data = changeToByteArr(bm);
		return saveImage(data, path,fileName);
	}
	
	
	/**
	 * 将图片转换为字节数组
	 * @param bm 图片
	 * @return 图片转换的字节数组
	 */
	private static byte[] changeToByteArr(Bitmap bm){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte [] data = baos.toByteArray();
		return data;
	}
	
	public static boolean isFileExist(String path){
		File file = new File(path);
		if(file.exists()){
			return true;
		}
		return false;
	}
	
	/**
	 * 以字节数组保存图片
	 * @param data 代表图片的字节数组
	 * @param file 文件路径（包括文件名称）
	 * @return 是否保存成功
	 */
	public static boolean saveImage(byte [] data,String path,String fileName){
		boolean flag = false;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path + File.separator + fileName);
			fos.write(data);
			fos.flush();
			flag = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return flag;
	}
	
	/**
	 * 保存到Android相册中，在相册中可以查看到此图片
	 * @param cr ContentResolver
	 * @param dateTaken 拍摄时间
	 * @param directory 图片保存路径
	 * @param filename 图片名
	 * @return
	 */
	public static Uri insertImageIntoMedia(ContentResolver cr, long dateTaken,
			String directory, String filename) {
		String filePath = directory + File.separator + filename;
		ContentValues values = new ContentValues(5);
		values.put(MediaStore.Images.Media.TITLE, filename);
		values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
		values.put(MediaStore.Images.Media.DATE_TAKEN, dateTaken);
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		values.put(MediaStore.Images.Media.DATA, filePath);
		return cr.insert(IMAGE_URI, values);
	}
	
}
