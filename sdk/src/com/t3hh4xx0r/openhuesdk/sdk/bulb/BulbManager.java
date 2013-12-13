package com.t3hh4xx0r.openhuesdk.sdk.bulb;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.t3hh4xx0r.openhuesdk.sdk.PreferencesManager;
import com.t3hh4xx0r.openhuesdk.sdk.Utils;
import com.t3hh4xx0r.openhuesdk.sdk.bulb.IBulbManager.onBulbRenamedListener;
import com.t3hh4xx0r.openhuesdk.sdk.bulb.IBulbManager.onBulbStateChangedListener;
import com.t3hh4xx0r.openhuesdk.sdk.bulb.IBulbManager.onBulbStateFetchedListener;
import com.t3hh4xx0r.openhuesdk.sdk.bulb.IBulbManager.onLightScanCompledListener;
import com.t3hh4xx0r.openhuesdk.sdk.objects.Bridge;
import com.t3hh4xx0r.openhuesdk.sdk.objects.Bulb;
import com.t3hh4xx0r.openhuesdk.sdk.objects.BulbState;

public class BulbManager {

	public class BulbListFetcherTask extends
			AsyncTask<Void, Void, ArrayList<Bulb>> {
		Bridge b;
		String userName;
		onLightScanCompledListener listener;
		ProgressDialog progress_dialog;

		public BulbListFetcherTask(Bridge b, String userName,
				onLightScanCompledListener listener,
				ProgressDialog progress_dialog) {
			this.b = b;
			this.userName = userName;
			this.listener = listener;
			this.progress_dialog = progress_dialog;
		}

		@Override
		protected ArrayList<Bulb> doInBackground(Void... arg0) {
			try {
				HttpParams httpParameters = new BasicHttpParams();
				int timeoutConnection = 5000;
				HttpConnectionParams.setConnectionTimeout(httpParameters,
						timeoutConnection);
				int timeoutSocket = 5000;
				HttpConnectionParams
						.setSoTimeout(httpParameters, timeoutSocket);

				HttpClient client = new DefaultHttpClient(httpParameters);
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
								b.setRawJSON(obj);
								
								DefaultHttpClient defaulthttpclient = new DefaultHttpClient();
								HttpGet httpget = new HttpGet("http://"
										+ bridge.getInternalipaddress()
										+ "/api/" + userName + "/lights/"
										+ b.getNumber());
								HttpResponse httpresponse = defaulthttpclient
										.execute(httpget);
								BufferedReader bufferedreader;
								bufferedreader = new BufferedReader(
										new InputStreamReader(httpresponse
												.getEntity().getContent()));
								String content = bufferedreader.readLine();
								Gson g = new Gson();
								BulbState state = g.fromJson(content,
										BulbState.class);
								b.setState(state);
								bulbs.add(b);
							}
						}
						progress_dialog.cancel();
						new PreferencesManager(act).storeBulbs(bulbs);
						listener.onLightsScanCompletedSuccessfully(bulbs);
						return bulbs;
					} else {
						progress_dialog.cancel();
						listener.onLightsScanCompletedUnsuccessfully(
								"Unauthorized user. Have you got a response from registerWithBridge yet?",
								false);
					}
				}
			} catch (HttpHostConnectException e) {
				e.printStackTrace();
				progress_dialog.cancel();
				if (Utils.isWifiConnected(act)) {
					listener.onLightsScanCompletedUnsuccessfully(
							"Unable to find bridge. Perhaps it has moved?",
							true);
				} else {
					listener.onWifiNotAvailable();
				}
			} catch (ConnectTimeoutException e) {
				e.printStackTrace();
				progress_dialog.cancel();
				if (Utils.isWifiConnected(act)) {
					listener.onLightsScanCompletedUnsuccessfully(
							"Unable to find bridge. Perhaps it has moved?",
							true);
				} else {
					listener.onWifiNotAvailable();
				}
			} catch (Exception e) {
				e.printStackTrace();
				progress_dialog.cancel();
				listener.onLightsScanCompletedUnsuccessfully(
						"Unknown error, unable to complete scan.", false);
			}
			return null;
		}
	}

	public class BulbNameChangerTask extends AsyncTask<Void, Void, Void> {
		Bulb b;
		String userName;
		String newName;
		onBulbRenamedListener listener;

		public BulbNameChangerTask(String newName, Bulb b, String userName,
				onBulbRenamedListener listener) {
			this.b = b;
			this.userName = userName;
			this.newName = newName;
			if (listener == null) {
				this.listener = new onBulbRenamedListener() {
					@Override
					public void onBulbRenamedSuccessfully(Bulb b, String newName) {
					}

					@Override
					public void onBulbRenamedUnsuccessfully() {
					}

					@Override
					public void onWifiNotAvailable() {
					}
				};
			}
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

				String str = EntityUtils.toString(resp.getEntity());
				if (str.contains("success") && str.contains(newName)) {
					b.setName(newName);
					b.getRawJSON().put("name", newName);
					
					ArrayList<Bulb> bulbs = pMan.getBulbs();
					for (int i = 0; i < bulbs.size(); i++) {
						if (bulbs.get(i).getNumber().equals(b.getNumber())) {
							bulbs.remove(i);
							bulbs.add(b);
							break;
						}
					}
					pMan.storeBulbs(bulbs);
					if (listener != null) {
						listener.onBulbRenamedSuccessfully(b, newName);
					} else {
						listener.onBulbRenamedUnsuccessfully();
					}
				}
			} catch (HttpHostConnectException e) {
				e.printStackTrace();
				if (Utils.isWifiConnected(act)) {
					listener.onBulbRenamedUnsuccessfully();
				} else {
					listener.onWifiNotAvailable();
				}
			} catch (Exception e) {
				e.printStackTrace();
				listener.onBulbRenamedUnsuccessfully();
			}
			return null;
		}
	}

	public class BulbStateChangerTask extends AsyncTask<Void, Void, Void> {
		BulbStateRequestFactory states;
		Bulb b;
		String userName;

		public BulbStateChangerTask(BulbStateRequestFactory states, Bulb b,
				String userName) {
			super();
			this.states = states;
			this.b = b;
			this.userName = userName;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpPut httpPut = new HttpPut("http://"
						+ bridge.getInternalipaddress() + "/api/" + userName
						+ "/lights/" + b.getNumber() + "/state");
				JSONObject holder = new JSONObject();
				for (int i = 0; i < states.getKeys().size(); i++) {
					String key = states.getKeys().get(i);
					Object value = states.getValues().get(i);
					holder.put(key, value);
				}
				StringEntity se = new StringEntity(holder.toString());
				httpPut.setEntity(se);
				httpPut.setHeader("Accept", "application/json");
				httpPut.setHeader("Content-type", "application/json");
				String str = EntityUtils.toString(httpclient.execute(httpPut)
						.getEntity());
				Log.d("STR RESUT", str);
				new BulbStateFetcherTask(b, userName,
						new onBulbStateFetchedListener() {
							@Override
							public void onStateFetched(BulbState state) {
								states.getListener().onStateChanged(state);
							}

							@Override
							public void onStateUnableToBeFetched(String error) {
								states.getListener().onStateUnableToBeChanged(
										error);
							}

							@Override
							public void onWifiNotAvailable() {
								states.getListener().onWifiNotAvailable();
							}
						}).execute();
			} catch (HttpHostConnectException e) {
				e.printStackTrace();
				if (Utils.isWifiConnected(act)) {
					states.getListener().onStateUnableToBeChanged(
							"Unable to find bridge. Perhaps it has moved?");
				} else {
					states.getListener().onWifiNotAvailable();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	private class BulbStateFetcherTask extends AsyncTask<Void, Void, Bridge> {
		Bulb bulb;
		String userName;
		onBulbStateFetchedListener listener;

		private BulbStateFetcherTask(Bulb b, String userName,
				onBulbStateFetchedListener listener) {
			this.bulb = b;
			this.userName = userName;
			if (listener == null) {
				this.listener = new onBulbStateFetchedListener() {
					@Override
					public void onStateFetched(BulbState state) {
					}

					@Override
					public void onStateUnableToBeFetched(String error) {
					}

					@Override
					public void onWifiNotAvailable() {
					}
				};
			} else {
				this.listener = listener;
			}
		}

		@Override
		protected Bridge doInBackground(Void... arg0) {
			if (bulb != null) {
				try {
					DefaultHttpClient defaulthttpclient = new DefaultHttpClient();
					HttpGet httpget = new HttpGet("http://"
							+ bridge.getInternalipaddress() + "/api/"
							+ userName + "/lights/" + bulb.getNumber());
					HttpResponse httpresponse = defaulthttpclient
							.execute(httpget);
					BufferedReader bufferedreader;
					bufferedreader = new BufferedReader(new InputStreamReader(
							httpresponse.getEntity().getContent()));
					String content = bufferedreader.readLine();
					Gson g = new Gson();
					BulbState state = g.fromJson(content, BulbState.class);
					bulb.setState(state);
					listener.onStateFetched(state);
				} catch (HttpHostConnectException e) {
					e.printStackTrace();
					if (Utils.isWifiConnected(act)) {
						listener.onStateUnableToBeFetched(e.getMessage());
					} else {
						listener.onWifiNotAvailable();
					}
				} catch (Exception e) {
					listener.onStateUnableToBeFetched(e.getMessage());
				}
			}
			return null;
		}
	}

	Context act;
	Bridge bridge;
	PreferencesManager pMan;

	public BulbManager(Context act, Bridge bridge) {
		this.act = act;
		this.bridge = bridge;
		pMan = new PreferencesManager(act);
	}

	public void alert(Bulb b, AlertCodes code) {
		BulbStateRequestFactory req = new BulbStateRequestFactory();
		req.add(StateCodes.ON, true);
		req.add(StateCodes.ALERT, code.getValue());
		BulbStateChangerTask task = new BulbStateChangerTask(req, b, pMan
				.getUserName().get());
		task.execute();
	}

	public void customAlert(final Bulb b, final CustomAlert alert) {
		final BulbState ogState = b.getState();
		setLightValues(b, alert.getColor(), alert.getSat(), alert.getBrightness(), new onBulbStateChangedListener() {			
			@Override
			public void onWifiNotAvailable() {				
			}
			
			@Override
			public void onStateUnableToBeChanged(String error) {
				Log.d("ERROR", error);
			}
			
			@Override
			public void onStateChanged(BulbState state) {
				Thread timer = new Thread() {
					public void run() {
						try {
							sleep((long) alert.getLength());
						} catch (InterruptedException e) {
						} finally {
							setLightValues(b, ogState, null);
							if (alert.getCycleCount() > 1) {
								alert.setCycleCount(alert.getCycleCount() - 1);
								try {
									sleep(300);
								} catch (InterruptedException e) {
								} finally {
									customAlert(b, alert);
								}
							}
						}
					}
				};
				timer.start();
			}
		});
//		setLightValues(b, alert.getColor(), alert.getSat(), alert.getBrightness(), null);
	}
	
	public Bridge getBridge() {
		return bridge;
	}

	public void getLights(onLightScanCompledListener scanListener) {
		final ProgressDialog progress_dialog = new ProgressDialog(act);
		progress_dialog.setIndeterminate(true);
		progress_dialog.setMessage("Scanning for bulbs...");
		progress_dialog.setCancelable(false);
		progress_dialog.show();
		BulbListFetcherTask task = new BulbListFetcherTask(bridge,
				new PreferencesManager(act).getUserName().get(), scanListener,
				progress_dialog);
		task.execute();
	}

	public void getLightState(Bulb b, onBulbStateFetchedListener listener) {
		BulbStateFetcherTask task = new BulbStateFetcherTask(b, pMan
				.getUserName().get(), listener);
		task.execute();
	}

	public void rename(Bulb b, String name, onBulbRenamedListener listener) {
		BulbNameChangerTask task = new BulbNameChangerTask(name, b, pMan
				.getUserName().get(), listener);
		task.execute();
	}

	public void setLightValues(Bulb b, BulbState state,
			onBulbStateChangedListener listener) {
		BulbStateRequestFactory req = new BulbStateRequestFactory();
		req.add(StateCodes.SATURATION, state.getState().getSat());
		req.add(StateCodes.BRIGHTNESS, state.getState().getBri());
		req.add(StateCodes.COLOR, state.getState().getHue());
		req.setListener(listener);

		BulbStateChangerTask task = new BulbStateChangerTask(req, b, pMan
				.getUserName().get());
		task.execute();
	}

	public void setLightValues(Bulb b, double color, double saturation,
			double brightness, onBulbStateChangedListener listener) {
		double finalSat;
		double finalBright;

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

		BulbStateRequestFactory req = new BulbStateRequestFactory();
		req.add(StateCodes.SATURATION, finalSat);
		req.add(StateCodes.BRIGHTNESS, finalBright);
		req.add(StateCodes.COLOR, color);
		req.setListener(listener);

		BulbStateChangerTask task = new BulbStateChangerTask(req, b, pMan
				.getUserName().get());
		task.execute();
	}

	public void turnOff(Bulb b, onBulbStateChangedListener listener) {
		BulbStateRequestFactory req = new BulbStateRequestFactory();
		req.add(StateCodes.ON, false);
		req.setListener(listener);

		BulbStateChangerTask task = new BulbStateChangerTask(req, b, pMan
				.getUserName().get());
		task.execute();
	}

	public void turnOn(Bulb b, onBulbStateChangedListener listener) {
		BulbStateRequestFactory req = new BulbStateRequestFactory();
		req.add(StateCodes.ON, true);
		req.setListener(listener);

		BulbStateChangerTask task = new BulbStateChangerTask(req, b, pMan
				.getUserName().get());
		task.execute();
	}

}
