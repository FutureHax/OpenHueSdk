package com.t3hh4xx0r.openhuesdk.sdk;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.t3hh4xx0r.openhuesdk.sdk.objects.Bridge;
import com.t3hh4xx0r.openhuesdk.sdk.objects.Bulb;

public class PreferencesManager {

	Context c;

	public PreferencesManager(Context c) {
		super();
		this.c = c;
	}

	public Bridge getBridge() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(c);
		Bridge res = new Bridge(prefs.getString("id", ""), prefs.getString(
				"internalipaddress", ""), prefs.getString("macaddress", ""));
		return res;
	}

	public ArrayList<Bulb> getBulbs() {
		ArrayList<Bulb> res = new ArrayList<Bulb>();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(c);
		int bulbCount = prefs.getInt("bulbCount", 0);
		for (int i = 1; i < bulbCount + 1; i++) {
			String bulbName = prefs.getString("bulb_" + i, "");
			Bulb b = new Bulb();
			b.setName(bulbName);
			b.setNumber((i + "").trim());
			if (!b.getName().equals("") && !b.getNumber().equals("")) {
				res.add(b);
			}
		}
		return res;
	}

	public String getUserName() {
		return PreferenceManager.getDefaultSharedPreferences(c).getString(
				"userName", "OpenHueSDKExample");
	}

	public void setUserName(String userName) {
		Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
		e.putString("userName", userName);
		e.apply();
	}

	public void storeBridge(Bridge b) {
		Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
		e.putString("id", b.getId());
		e.putString("internalipaddress", b.getInternalipaddress());
		e.putString("macaddress", b.getMacaddress());
		e.apply();
	}

	public void storeBulbs(ArrayList<Bulb> bulbs) {
		Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
		e.putInt("bulbCount", bulbs.size());
		for (int i = 0; i < bulbs.size(); i++) {
			e.putString("bulb_" + bulbs.get(i).getNumber().trim(), bulbs.get(i)
					.getName());
		}
		e.apply();
	}
}
