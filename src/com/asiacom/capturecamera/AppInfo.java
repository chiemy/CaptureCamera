package com.asiacom.capturecamera;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

class AppInfo {
	private Context context;
	private PackageManager pm;

	public AppInfo(Context context) {
		this.context = context;
		pm = context.getPackageManager();
	}

	/*
	 * 获取程序的名字
	 */
	public String getAppName() {
		String name = "";
		try {
			ApplicationInfo info = pm.getApplicationInfo(context.getPackageName(), 0);
			name = info.loadLabel(pm).toString();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return name;
	}
}
