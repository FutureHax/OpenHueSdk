package com.t3hh4xx0r.openhuesdk.sdk.bridge;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.t3hh4xx0r.openhuesdk.sdk.CountDownTimer;
import com.t3hh4xx0r.openhuesdk.sdk.NumberToWords;
import com.t3hh4xx0r.openhuesdk.sdk.PreferencesManager;
import com.t3hh4xx0r.openhuesdk.sdk.Utils;
import com.t3hh4xx0r.openhuesdk.sdk.Utils.DeviceType;
import com.t3hh4xx0r.openhuesdk.sdk.bridge.IBridgeRegistrar.OnBridgeRegisteredListener;
import com.t3hh4xx0r.openhuesdk.sdk.bridge.IBridgeRegistrar.OnBridgeReturnedListener;
import com.t3hh4xx0r.openhuesdk.sdk.bridge.IBridgeRegistrar.OnPushLinkButtonPressedListener;
import com.t3hh4xx0r.openhuesdk.sdk.objects.Bridge;
import com.t3hh4xx0r.openhuesdk.sdk.objects.RegistrationRequest;
import com.t3hh4xx0r.openhuesdk.sdk.objects.RegistrationResponse;

public class BridgeRegistrar {

	private class BridgeFetcherTask extends AsyncTask<Void, Void, Bridge> {
		OnBridgeReturnedListener listener;

		private BridgeFetcherTask(OnBridgeReturnedListener listener) {
			this.listener = listener;
		}

		@Override
		protected Bridge doInBackground(Void... arg0) {
			Bridge b = null;
			try {
				DefaultHttpClient defaulthttpclient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(
						"http://www.meethue.com/api/nupnp");
				HttpResponse httpresponse = defaulthttpclient.execute(httpget);
				BufferedReader bufferedreader;
				bufferedreader = new BufferedReader(new InputStreamReader(
						httpresponse.getEntity().getContent()));
				String content = bufferedreader.readLine();
				Gson gson = new Gson();
				b = gson.fromJson(content, Bridge[].class)[0];
			} catch (Exception e) {
				e.printStackTrace();
			}
			return b;
		}

		@Override
		protected void onPostExecute(Bridge b) {
			super.onPostExecute(b);
			if (b == null) {
				if (!Utils.isWifiConnected(act)) {
					listener.bridgeNotReady("Please connect to wifi and try again.");
				} else {
					listener.bridgeNotReady("Unable to find Hue Bridge. Please be sure it is plugged in and connected to the same network as your device.");
				}
			} else {
				new PreferencesManager(act).storeBridge(b);
				listener.bridgeReady(b);
			}
		}

	}

	public class BridgeRegisterTask extends AsyncTask<Void, Void, Void> {
		Bridge b;
		DeviceType t;
		String uName;
		OnPushLinkButtonPressedListener listener;

		public BridgeRegisterTask(Bridge b, DeviceType t, String uName,
				OnPushLinkButtonPressedListener onPushLinkButtonPressedListener) {
			super();
			this.b = b;
			this.t = t;
			this.uName = uName;
			this.listener = onPushLinkButtonPressedListener;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
				HttpPost localHttpPost = new HttpPost("http://"
						+ b.getInternalipaddress() + "/api/");
				Gson localGson = new Gson();
				RegistrationRequest localRegistrationRequest = new RegistrationRequest(
						t.getType(), uName);

				localHttpPost.setEntity(new StringEntity(localGson
						.toJson(localRegistrationRequest)));
				localHttpPost.setHeader("Accept", "application/json");
				localHttpPost.setHeader("Content-type", "application/json");
				String str = EntityUtils.toString(localDefaultHttpClient
						.execute(localHttpPost).getEntity());
				if (localGson.fromJson(
						str.substring(1, -1 + str.length()),
						RegistrationResponse.class).success != null) {
					listener.bridgeReady();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

	}

	Activity act;
	String uName;
	boolean finished = false;
	AlertDialog diag = null;
	int count = 0;
	CountDownTimer countDownTimer;

	/**
	 * @param act
	 * @param userName
	 */
	public BridgeRegistrar(Activity act, String userName) {
		super();
		this.act = act;
		this.uName = userName;
	}

	public void getBridge(OnBridgeReturnedListener listener) {
		BridgeFetcherTask task = new BridgeFetcherTask(listener);
		task.execute();
	}

	public void registerWithBridge(Activity act, final Bridge b,
			final DeviceType deviceType,
			final OnBridgeRegisteredListener listener) {
		finished = false;
		final AlertDialog.Builder builder = new AlertDialog.Builder(act);
		builder.setTitle("Push the PUSHLINK button on your bridge.");
		builder.setCancelable(false);
		View v = buildPushPopup(act);
		builder.setView(v);
		final ProgressBar progressBar = (ProgressBar) v
				.findViewWithTag("progressBar1");
		final TextView timeLeft = (TextView) v.findViewWithTag("timeLeft");
		countDownTimer = new CountDownTimer(15000L, 1000L) {
			@Override
			public void onFinish() {
				try {
					diag.cancel();
				} catch (IllegalArgumentException e) {

				}
				count = 0;
				if (!finished) {
					listener.bridgeNotReady("Didn't press the button in time.");
				} else {
					// whyd it take so long?
					listener.bridgeReady();
				}
			}

			@Override
			public void onTick(long left) {
				count = count + 1;
				progressBar
						.setProgress((int) ((100D * (15000L - left)) / 15000D));
				int secsLeftNumber = 15 - count;
				String secsLeft = NumberToWords.convert(secsLeftNumber);
				timeLeft.setText("Push the button within " + secsLeft + " ("
						+ secsLeftNumber + ") seconds.");

				if (finished) {
					countDownTimer.cancel();
					try {
						diag.dismiss();
					} catch (IllegalArgumentException e) {
						
					}
					listener.bridgeReady();
				} else {
					BridgeRegisterTask task = new BridgeRegisterTask(b,
							deviceType, uName,
							new OnPushLinkButtonPressedListener() {
								@Override
								public void bridgeNotReady(String errorMessage) {
									listener.bridgeNotReady(errorMessage);
								}

								@Override
								public void bridgeReady() {
									finished = true;
								}
							});
					task.execute();
				}
			}
		};

		diag = builder.create();
		diag.show();
		countDownTimer.start();
	}
	
	private View buildPushPopup(Activity a) {
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		TextView t = new TextView(a, null, android.R.attr.textAppearanceLarge);
		t.setText("Push the button with fifteen (15) seconds.");
		t.setTag("timeLeft");
		LinearLayout root = new LinearLayout(a);
		root.setOrientation(LinearLayout.VERTICAL);
		root.setPadding(10, 10, 10, 10);
		root.setLayoutParams(lp);
		root.addView(t);
		ProgressBar p = new ProgressBar(a,
                null, 
                android.R.attr.progressBarStyleHorizontal);
		p.setTag("progressBar1");
		p.setIndeterminate(false);
		root.addView(p, lp);
		return root;
	}
}
