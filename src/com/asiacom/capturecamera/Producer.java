package com.asiacom.capturecamera;

import android.text.format.DateFormat;

class Producer {
	public static String produceImgName(){
		long date = System.currentTimeMillis();
		String filename = DateFormat.format("yyyy-MM-dd kk.mm.ss",
				date).toString()
				+ ".jpg";
		return filename;
	}
}
