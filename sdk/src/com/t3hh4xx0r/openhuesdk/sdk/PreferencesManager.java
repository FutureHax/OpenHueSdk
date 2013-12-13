package com.t3hh4xx0r.openhuesdk.sdk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.t3hh4xx0r.openhuesdk.sdk.objects.Bridge;
import com.t3hh4xx0r.openhuesdk.sdk.objects.Bulb;
import com.t3hh4xx0r.openhuesdk.sdk.objects.BulbState;

public class PreferencesManager {

	public class UserName {
		String name;

		public UserName(String name) {
			this.name = name;
		}

		public String get() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isSet() {
			return get().equals("OpenHueSDKExample");
		}

	}

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

	public Bulb getBulb(String bulbName) {
		for (Bulb b : getBulbs()) {
			if (b.getName().equals(bulbName)) {
				return b;
			}
		}
		
		return null;
	}
	public ArrayList<Bulb> getBulbs() {
		ArrayList<Bulb> res = new ArrayList<Bulb>();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(c);
		int bulbCount = prefs.getInt("bulbCount", 0);
		Gson gson = new Gson();
		for (int i = 1; i < bulbCount + 1; i++) {
			String bulbName = prefs.getString("bulb_" + i, "");
			Bulb b = new Bulb();
			b.setName(bulbName);
			b.setNumber((i + "").trim());
			BulbState state = buildBulbState(prefs, i);
			try {
				JSONObject obj = new JSONObject(prefs.getString("bulb_" + i
						+ "_bulbRAW", ""));
				obj.put("number", (i + "").trim());
//				obj.put("state", gson.toJson(state, BulbState.class));
				b.setRawJSON(obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			b.setState(state);
			if (!b.getName().equals("") && !b.getNumber().equals("")) {
				res.add(b);
			}
		}
		return res;
	}

	private BulbState buildBulbState(SharedPreferences prefs, int i) {
		BulbState bState = new BulbState();
		BulbState.State state = bState.new State();

		state.setAlert(prefs.getString("bulb_" + i + "_bulbState_state_Alert",
				"none"));
		state.setColormode(prefs.getString("bulb_" + i
				+ "_bulbState_state_ColorMode", "none"));
		state.setEffect(prefs.getString(
				"bulb_" + i + "_bulbState_state_Effect", "none"));

		state.setBri(prefs.getInt("bulb_" + i + "_bulbState_state_Bri", 0));
		state.setHue(prefs.getInt("bulb_" + i + "_bulbState_state_Hue", 0));
		state.setSat(prefs.getInt("bulb_" + i + "_bulbState_state_Sat", 0));
		state.setCt(prefs.getInt("bulb_" + i + "_bulbState_state_CT", 0));

		state.setOn(prefs
				.getBoolean("bulb_" + i + "_bulbState_state_On", false));
		state.setReachable(prefs.getBoolean("bulb_" + i
				+ "_bulbState_state_Reachable", false));

		bState.setName(prefs.getString("bulb_" + i, "none"));
		bState.setType(prefs.getString("bulb_" + i + "_bulbState_Type", "none"));
		bState.setModelid(prefs.getString("bulb_" + i + "_bulbState_ModelId",
				"none"));
		bState.setSwversion(prefs.getString("bulb_" + i
				+ "_bulbState_SWVersion", "none"));
		bState.setState(state);

		return bState;
	}

	public UserName getUserName() {
		return new UserName(PreferenceManager.getDefaultSharedPreferences(c)
				.getString("userName", "OpenHueSDKExample"));
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

	public ArrayList<String> getGroupNames() {
		ArrayList<String> res = new ArrayList<String>();
//		int gCount = PreferenceManager.getDefaultSharedPreferences(c).getInt(
//				"group_count", 0);
		Map<String, ?> keys = PreferenceManager.getDefaultSharedPreferences(c)
				.getAll();
		for (Map.Entry<String, ?> entry : keys.entrySet()) {			
			if (entry.getKey().startsWith("group_")) {
				res.add(entry.getKey().replace("group_", ""));
			}
		}
		return res;
	}
	
	public ArrayList<Bulb> getGroup(String groupName) {
		ArrayList<Bulb> res = new ArrayList<Bulb>();
		
		Map<String, ?> keys = PreferenceManager.getDefaultSharedPreferences(c)
				.getAll();
		Gson gson = new Gson();
		for (Map.Entry<String, ?> entry : keys.entrySet()) {			
			if (entry.getKey().startsWith("group_" + groupName)) {
				Set<String> bulbSet = (Set<String>) entry.getValue();
				ArrayList<String> bulbList = new ArrayList<String>(bulbSet);
				for (String bulbString : bulbList) {
					res.add(gson.fromJson(bulbString, Bulb.class));
				}
			}
		}
		
		return res;
	}


	public void storeGroup(String groupName, ArrayList<Bulb> bulbGroup) {
		Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();

//		Set<String> storedSet = PreferenceManager
//				.getDefaultSharedPreferences(c).getStringSet(
//						"group_" + groupName, null);
//		if (storedSet == null) {
//			e.putInt(
//					"group_count",
//					PreferenceManager.getDefaultSharedPreferences(c).getInt(
//							"group_count", 0) + 1);
//		}

		HashSet<String> set = new HashSet<String>();
		for (Bulb b : bulbGroup) {
			set.add(b.getRawJSON().toString());
		}
		e.putStringSet("group_" + groupName, set);
		e.apply();
	}

	public void storeBulbs(ArrayList<Bulb> bulbs) {
		Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
		e.putInt("bulbCount", bulbs.size());
		for (int i = 0; i < bulbs.size(); i++) {
			Bulb b = bulbs.get(i);
			e.putString("bulb_" + b.getNumber().trim(), b.getName());

			e.putBoolean(
					"bulb_" + b.getNumber().trim() + "_bulbState_state_On", b
							.getState().getState().isOn());
			e.putBoolean("bulb_" + b.getNumber().trim()
					+ "_bulbState_state_Reachable", b.getState().getState()
					.isReachable());

			e.putString("bulb_" + b.getNumber().trim()
					+ "_bulbState_state_Alert", b.getState().getState()
					.getAlert());
			e.putString("bulb_" + b.getNumber().trim()
					+ "_bulbState_state_ColorMode", b.getState().getState()
					.getColormode());
			e.putString("bulb_" + b.getNumber().trim()
					+ "_bulbState_state_Effect", b.getState().getState()
					.getEffect());

			e.putInt("bulb_" + b.getNumber().trim() + "_bulbState_state_Bri",
					(int) b.getState().getState().getBri());
			e.putInt("bulb_" + b.getNumber().trim() + "_bulbState_state_Hue",
					(int) b.getState().getState().getHue());
			e.putInt("bulb_" + b.getNumber().trim() + "_bulbState_state_Sat",
					(int) b.getState().getState().getSat());
			e.putInt("bulb_" + b.getNumber().trim() + "_bulbState_state_CT",
					(int) b.getState().getState().getCt());

			e.putString("bulb_" + b.getNumber().trim() + "_bulbState_Type", b
					.getState().getType());
			e.putString("bulb_" + b.getNumber().trim() + "_bulbState_ModelId",
					b.getState().getModelid());
			e.putString(
					"bulb_" + b.getNumber().trim() + "_bulbState_SWVersion", b
							.getState().getSwversion());
			e.putString("bulb_" + b.getNumber().trim() + "_bulbRAW", b
					.getRawJSON().toString());

		}
		e.apply();
	}

	public void updateBulb(Bulb b) {
		Editor e = PreferenceManager.getDefaultSharedPreferences(c).edit();
		e.putString("bulb_" + b.getNumber().trim(), b.getName());
		e.apply();
	}
}
