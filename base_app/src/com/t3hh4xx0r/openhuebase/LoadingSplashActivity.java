package com.t3hh4xx0r.openhuebase;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.widget.Toast;

import com.t3hh4xx0r.openhuesdk.sdk.PreferencesManager;
import com.t3hh4xx0r.openhuesdk.sdk.Utils;
import com.t3hh4xx0r.openhuesdk.sdk.bridge.BridgeRegistrar;
import com.t3hh4xx0r.openhuesdk.sdk.bridge.BridgeRegistrar.OnBridgeRegisteredListener;
import com.t3hh4xx0r.openhuesdk.sdk.bridge.BridgeRegistrar.OnBridgeReturnedListener;
import com.t3hh4xx0r.openhuesdk.sdk.bulb.BulbManager;
import com.t3hh4xx0r.openhuesdk.sdk.bulb.BulbManager.onLightScanCompledListener;
import com.t3hh4xx0r.openhuesdk.sdk.objects.Bridge;
import com.t3hh4xx0r.openhuesdk.sdk.objects.Bulb;

public class LoadingSplashActivity extends Activity {
	PreferencesManager pMan;
	BridgeRegistrar reg;
	long startTime;

	private void getBridge(final boolean register) {
		if (pMan.getBridge().isPlaceHolder()) {
			reg.getBridge(new OnBridgeReturnedListener() {
				@Override
				public void bridgeNotReady(final String errorMessage) {
					LoadingSplashActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(LoadingSplashActivity.this,
									errorMessage, Toast.LENGTH_LONG).show();
						}
					});
				}

				@Override
				public void bridgeReady(final Bridge b) {
					if (register) {
						registerBridge();
					}
				}
			});
		} else {
			getBulbs();
		}
	}

	private void getBulbs() {
		BulbManager bMan = new BulbManager(this, pMan.getBridge());
		bMan.getLights(new onLightScanCompledListener() {
			@Override
			public void onLightsScanCompletedSuccessfully(
					ArrayList<Bulb> bulbList) {
				long now = System.currentTimeMillis();
				final long openTime = now - startTime;
				final long MIN_OPEN_TIME = 500;

				if (openTime > MIN_OPEN_TIME) {
					Intent i = new Intent(LoadingSplashActivity.this,
							MainActivity.class);
					startActivity(i);
					finish();
				} else {
					Thread timer = new Thread() {
						public void run() {
							try {
								sleep(MIN_OPEN_TIME - openTime);
							} catch (InterruptedException e) {
								e.printStackTrace();
							} finally {
								Intent openMain = new Intent(
										LoadingSplashActivity.this,
										MainActivity.class);
								startActivity(openMain);
								finish();
							}
						}
					};
					timer.start();
				}
			}

			@Override
			public void onLightsScanCompletedUnsuccessfully(final String error,
					boolean couldHaveMoved) {
				if (couldHaveMoved) {
					reg.getBridge(new OnBridgeReturnedListener() {
						@Override
						public void bridgeNotReady(final String errorMessage) {
							LoadingSplashActivity.this
									.runOnUiThread(new Runnable() {
										@Override
										public void run() {
											Toast.makeText(
													LoadingSplashActivity.this,
													errorMessage,
													Toast.LENGTH_LONG).show();
										}
									});
						}

						@Override
						public void bridgeReady(final Bridge b) {
							LoadingSplashActivity.this
									.runOnUiThread(new Runnable() {
										@Override
										public void run() {
											Toast.makeText(
													LoadingSplashActivity.this,
													"Found the new bridge.",
													Toast.LENGTH_LONG).show();
										}
									});
							getBulbs();
						}
					});
				} else {
					LoadingSplashActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(LoadingSplashActivity.this,
									"Unable to find lights. " + error,
									Toast.LENGTH_LONG).show();
						}
					});
				}
			}

			@Override
			public void onWifiNotAvailable() {
				LoadingSplashActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						AlertDialog.Builder b = new AlertDialog.Builder(
								LoadingSplashActivity.this);
						b.setTitle("Error...");
						b.setMessage("Unable to find your lights.\n"
								+ "Your WiFi must be connected."
								+ "\nPlease try again.");
						b.setPositiveButton("Try Again.",
								new OnClickListener() {
									@Override
									public void onClick(DialogInterface d,
											int arg1) {
										d.dismiss();
										getBulbs();
									}
								});

						b.create().show();
					}
				});
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		startTime = System.currentTimeMillis();
		setContentView(R.layout.activity_splash);
		pMan = new PreferencesManager(this);
		if (!pMan.getUserName().isSet()) {
			pMan.setUserName("OpenHueBase");
		}

		reg = new BridgeRegistrar(this, pMan.getUserName().get());
		getBridge(true);
	}

	public void registerBridge() {
		reg.registerWithBridge(this, pMan.getBridge(),
				Utils.getDeviceType(this), new OnBridgeRegisteredListener() {

					@Override
					public void bridgeNotReady(final String errorMessage) {
						LoadingSplashActivity.this
								.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										AlertDialog.Builder b = new AlertDialog.Builder(
												LoadingSplashActivity.this);
										b.setTitle("Error...");
										b.setMessage("Unable to register with your bridge.\n"
												+ errorMessage
												+ "\nPlease try again.");
										b.setPositiveButton("Try Again.",
												new OnClickListener() {
													@Override
													public void onClick(
															DialogInterface d,
															int arg1) {
														d.dismiss();
														registerBridge();
													}
												});

										b.create().show();
									}
								});
					}

					@Override
					public void bridgeReady() {
						Toast.makeText(LoadingSplashActivity.this,
								"Bridge registered", Toast.LENGTH_LONG).show();
						getBulbs();
					}
				});
	}

}
