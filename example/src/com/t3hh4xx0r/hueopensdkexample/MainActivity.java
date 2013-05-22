package com.t3hh4xx0r.hueopensdkexample;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.t3hh4xx0r.openhuesdk.sdk.PreferencesManager;
import com.t3hh4xx0r.openhuesdk.sdk.Utils;
import com.t3hh4xx0r.openhuesdk.sdk.bridge.BridgeRegistrar;
import com.t3hh4xx0r.openhuesdk.sdk.bridge.IBridgeRegistrar.OnBridgeRegisteredListener;
import com.t3hh4xx0r.openhuesdk.sdk.bridge.IBridgeRegistrar.OnBridgeReturnedListener;
import com.t3hh4xx0r.openhuesdk.sdk.bulb.BulbManager;
import com.t3hh4xx0r.openhuesdk.sdk.bulb.IBulbManager.onLightScanCompledListener;
import com.t3hh4xx0r.openhuesdk.sdk.objects.Bridge;
import com.t3hh4xx0r.openhuesdk.sdk.objects.Bulb;

public class MainActivity extends SherlockActivity {
	BridgeRegistrar reg;
	View registerBridge;
	View findLights;
	View manageLights;
	PreferencesManager pMan;

	public void findAndWatch() {
		manageLights = findViewById(R.id.manage);
		manageLights.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), BulbManagerActivity.class);
				startActivity(i);
			}
		});
		registerBridge = findViewById(R.id.register);
		registerBridge.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				reg.getBridge(new OnBridgeReturnedListener() {
					@Override
					public void bridgeNotReady(String errorMessage) {
						Toast.makeText(MainActivity.this, errorMessage,
								Toast.LENGTH_LONG).show();
					}

					@Override
					public void bridgeReady(Bridge b) {
						registerBridge(b);
					}
				});
			}
		});

		findLights = findViewById(R.id.scan);
		findLights.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BulbManager lMan = new BulbManager(MainActivity.this, pMan
						.getBridge());
				lMan.getLights(new onLightScanCompledListener() {
							@Override
							public void onLightsScanCompletedSuccessfully(
									ArrayList<Bulb> bulbList) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										manageLights
												.setVisibility(View.VISIBLE);
									}
								});
							}

							@Override
							public void onLightsScanCompletedUnsuccessfully(
									final String error, boolean couldHaveMoved) {
								MainActivity.this.runOnUiThread(new Runnable() {
									public void run() {
										Toast.makeText(
												MainActivity.this,
												"Failed to find bulbs. "
														+ error,
												Toast.LENGTH_LONG).show();
									}
								});

							}

							@Override
							public void onWifiNotAvailable() {
								// TODO Auto-generated method stub
								
							}
						});
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pMan = new PreferencesManager(this);
		reg = new BridgeRegistrar(this, pMan.getUserName().get());
		findAndWatch();
		setVisibilities();
	}

	protected void registerBridge(Bridge b) {
		reg.registerWithBridge(this, b, Utils.getDeviceType(this),
				new OnBridgeRegisteredListener() {
					@Override
					public void bridgeNotReady(String errorMessage) {
						Toast.makeText(MainActivity.this,
								errorMessage,
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void bridgeReady() {
						Toast.makeText(MainActivity.this, "SUCCESS!",
								Toast.LENGTH_LONG).show();
						findLights.setVisibility(View.VISIBLE);
					}
				});
	}

	private void setVisibilities() {
		if (pMan.getBridge().isPlaceHolder()) {
			findLights.setVisibility(View.GONE);
			manageLights.setVisibility(View.GONE);
		} else {
			manageLights.setVisibility(View.GONE);									
		}
//			pMan.getBridge().isBridgeStillValid(this,
//					new bridgeValidityListener() {
//						@Override
//						public void onBrigeReturnedValid(Bridge b) {
//							if (pMan.getBulbs().isEmpty()) {
//								MainActivity.this.runOnUiThread(new Runnable() {
//									@Override
//									public void run() {
//									}
//								});	
//							}
//						}
//
//						@Override
//						public void onBrigeReturnedInvalid(Bridge b) {
//							MainActivity.this.runOnUiThread(new Runnable() {
//								@Override
//								public void run() {
//									findLights.setVisibility(View.GONE);
//									manageLights.setVisibility(View.GONE);									
//								}
//							});							
//						}
//					});
	}

}
