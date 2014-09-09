package com.t3hh4xx0r.openhuesdk.sdk;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
	public static class DeviceType {
		String type;

		public DeviceType() {
			this.type = "LOL NOOB";
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}

	//
	public static DeviceType getDeviceType(Context c) {
		PackageManager pm = c.getPackageManager();
		DeviceType t = new DeviceType();
		try {
			t.setType(pm.getApplicationLabel(
					pm.getApplicationInfo(c.getPackageName(), 0)).toString());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return t;
	}
	
	public static boolean isWifiConnected(Context c) {
		ConnectivityManager connManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isConnected();
	}

}
