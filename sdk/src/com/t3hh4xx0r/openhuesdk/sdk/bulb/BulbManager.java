package com.t3hh4xx0r.openhuesdk.sdk.bulb;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.t3hh4xx0r.openhuesdk.sdk.PreferencesManager;
import com.t3hh4xx0r.openhuesdk.sdk.objects.Bridge;
import com.t3hh4xx0r.openhuesdk.sdk.objects.Bulb;

public class BulbManager {
	//	Alert Status Codes
	public static final String NONE = "none";
	public static final String SELECT = "select";
	public static final String lSELECT = "lselect";
	
	public class BulbColorChangerTask extends AsyncTask<Void, Void, Void> {
		Context c;
		Bulb b;
		int color = 999999999;
		int finalSat = 420;
		int finalBright = 420;

		public BulbColorChangerTask(Context c, Bulb b, int color, int finalSat,
				int finalBright) {
			this.color = color;
			this.c = c;
			this.b = b;
			this.finalBright = finalBright;
			this.finalSat = finalSat;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpPut httpPut = new HttpPut("http://"
						+ bridge.getInternalipaddress() + "/api/"
						+ new PreferencesManager(c).getUserName() + "/lights/"
						+ b.getNumber() + "/state");
				JSONObject holder = new JSONObject();
				holder.put("on", true);
				if (finalSat != 420) {
					holder.put("sat", finalSat);
				}
				if (finalBright != 420) {
					holder.put("bri", finalBright);
				}
				if (color != 999999999) {
					holder.put("hue", color);
				}
				StringEntity se = new StringEntity(holder.toString());
				httpPut.setEntity(se);
				httpPut.setHeader("Accept", "application/json");
				httpPut.setHeader("Content-type", "application/json");
				String str = EntityUtils.toString(httpclient.execute(httpPut)
						.getEntity());
				Log.d("REPSONSE", str);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public class BulbStateChangerTask extends AsyncTask<Void, Void, Void> {
		HashMap<String, Object> states;
		Bulb b;
		String userName;
		Bridge bridge;

		public BulbStateChangerTask(HashMap<String, Object> states, Bulb b, String userName,
				Bridge bridge) {
			super();
			this.states = states;
			this.b = b;
			this.userName = userName;
			this.bridge = bridge;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpPut httpPut = new HttpPut("http://"
						+ bridge.getInternalipaddress() + "/api/" + userName
						+ "/lights/" + b.getNumber() + "/state");
				JSONObject holder = new JSONObject();
				for (Map.Entry<String, Object> entry : states.entrySet()) {
				    String key = entry.getKey();
				    Object value = entry.getValue();
					holder.put(key, value);
				}
				StringEntity se = new StringEntity(holder.toString());
				httpPut.setEntity(se);
				httpPut.setHeader("Accept", "application/json");
				httpPut.setHeader("Content-type", "application/json");
				String str = EntityUtils.toString(httpclient.execute(httpPut)
						.getEntity());
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

	}

	public class BulbNameChangerTask extends AsyncTask<Void, Void, Void> {
		Bulb b;
		String userName;
		String newName;
		Bridge bridge;
		onBulbRenamedListener listener;
		
		public BulbNameChangerTask(String newName, Bulb b, String userName,
				Bridge bridge) {
			super();
			this.b = b;
			this.userName = userName;
			this.bridge = bridge;
			this.newName = newName;
		}

		public BulbNameChangerTask(String newName, Bulb b, String userName,
				Bridge bridge, onBulbRenamedListener listener) {
			super();
			this.b = b;
			this.userName = userName;
			this.bridge = bridge;
			this.newName = newName;
			this.listener = listener; 
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpPut httpPut = new HttpPut("http://"
						+ bridge.getInternalipaddress() + "/api/" + userName
						+ "/lights/" + b.getNumber());
				JSONObject holder = new JSONObject();
				holder.put("name", newName);
				StringEntity se = new StringEntity(holder.toString());
				httpPut.setEntity(se);
				httpPut.setHeader("Accept", "application/json");
				httpPut.setHeader("Content-type", "application/json");
				HttpResponse resp = httpclient.execute(httpPut);
				
				if (listener != null) {
					String str = EntityUtils.toString(resp
							.getEntity());
					if (str.contains("success") &&
							str.contains(newName)) {
						b.setName(newName);
						ArrayList<Bulb> bulbs = pMan.getBulbs();	
						for (int i = 0; i < bulbs.size(); i++) {
							if (bulbs.get(i).getNumber().equals(b.getNumber())) {
								bulbs.remove(i);
								bulbs.add(b);
								break;
							}
						}
						pMan.storeBulbs(bulbs);
						listener.onBulbRenamedSuccessfully(b, newName);
					} else {
						listener.onBulbRenamedUnsuccessfully();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				listener.onBulbRenamedUnsuccessfully();
			}

			return null;
		}

	}

	public class GetLightsListTask extends AsyncTask<Void, Void, Void> {
		Bridge b;
		String userName;
		onLightScanCompledListener listener;
		ProgressDialog progress_dialog;

		public GetLightsListTask(Bridge b, String userName,
				onLightScanCompledListener listener,
				ProgressDialog progress_dialog) {
			this.b = b;
			this.userName = userName;
			this.listener = listener;
			this.progress_dialog = progress_dialog;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				HttpClient client = new DefaultHttpClient();				
				String getURL = "http://" + b.getInternalipaddress() + "/api/"
						+ userName + "/lights";
				HttpGet get = new HttpGet(getURL);
				HttpResponse responseGet = client.execute(get);
				HttpEntity resEntityGet = responseGet.getEntity();
				if (resEntityGet != null) {
					InputStream is = resEntityGet.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is, "UTF-8"), 8);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					is.close();
					if (!sb.toString().contains("unauthorized user")) {
						JSONObject json = new JSONObject(sb.toString());
						Iterator<?> keys = json.keys();
						ArrayList<Bulb> bulbs = new ArrayList<Bulb>();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							if (json.get(key) instanceof JSONObject) {
								Bulb b = new Bulb();
								JSONObject obj = (JSONObject) json.get(key);
								b.setName(obj.getString("name"));
								b.setNumber(key);
								bulbs.add(b);
							}
						}
						progress_dialog.cancel();
						new PreferencesManager(act).storeBulbs(bulbs);
						listener.onLightsScanCompletedSuccessfully(bulbs);
					} else {
						progress_dialog.cancel();
						listener.onLightsScanCompletedUnsuccessfully("Unauthorized user. Have you got a response from registerWithBridge yet?");	
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				progress_dialog.cancel();
				listener.onLightsScanCompletedUnsuccessfully("Unknown error, unable to complete scan.");
			}
			return null;
		}

	}

	public interface onLightScanCompledListener {
		public void onLightsScanCompletedSuccessfully(ArrayList<Bulb> bulbList);

		public void onLightsScanCompletedUnsuccessfully(String error);
	}
	
	public interface onBulbRenamedListener {
		public void onBulbRenamedSuccessfully(Bulb b, String newName);
		public void onBulbRenamedUnsuccessfully();
	}

	Context act;
	Bridge bridge;
	PreferencesManager pMan;
	
	public Bridge getBridge() {
		return bridge;
	}

	public BulbManager(Context act, Bridge bridge) {
		this.act = act;
		this.bridge = bridge;
		pMan = new PreferencesManager(act);
	}

	public void getLights(onLightScanCompledListener listener) {
		final ProgressDialog progress_dialog = new ProgressDialog(act);
		progress_dialog.setIndeterminate(true);
		progress_dialog.setMessage("Scanning for bulbs...");
		progress_dialog.setCancelable(false);
		progress_dialog.show();
		GetLightsListTask task = new GetLightsListTask(bridge,
				new PreferencesManager(act).getUserName(), listener,
				progress_dialog);
		task.execute();
	}

	public void setLightValues(Bulb b, int color, double saturation,
			int brightness) {
		int finalSat;
		int finalBright;

		if (saturation == 420) {
			finalSat = 420;
		} else {
			double step1 = (saturation / 100D);
			Double step2 = step1 * 255D;
			finalSat = step2.intValue();
		}
		if (brightness == 420) {
			finalBright = 420;
		} else {
			double step1 = (brightness / 100D);
			Double step2 = step1 * 255D;
			finalBright = step2.intValue();
		}

		BulbColorChangerTask task = new BulbColorChangerTask(act, b, color,
				finalSat, finalBright);
		task.execute();
	}

	public void turnOff(Bulb b) {
		HashMap<String, Object> states = new HashMap<String, Object>();
		states.put("on", false);
		BulbStateChangerTask task = new BulbStateChangerTask(states, b,
				pMan.getUserName(), bridge);
		task.execute();
	}

	public void turnOn(Bulb b) {
		HashMap<String, Object> states = new HashMap<String, Object>();
		states.put("on", true);
		BulbStateChangerTask task = new BulbStateChangerTask(states, b, pMan.getUserName(),
				bridge);
		task.execute();
	}

	public void alert(Bulb b, String alertType) {
		HashMap<String, Object> states = new HashMap<String, Object>();
		states.put("on", true);
		states.put("alert", alertType);
		BulbStateChangerTask task = new BulbStateChangerTask(states, b, pMan.getUserName(),
				bridge);
		task.execute();
	}

	public void rename(Bulb b, String name) {
		BulbNameChangerTask task = new BulbNameChangerTask(name, b, pMan.getUserName(),
				bridge);
		task.execute();		
	}
	
	public void rename(Bulb b, String name, onBulbRenamedListener listener) {
		BulbNameChangerTask task = new BulbNameChangerTask(name, b, pMan.getUserName(),
				bridge, listener);
		task.execute();		
	}

}
