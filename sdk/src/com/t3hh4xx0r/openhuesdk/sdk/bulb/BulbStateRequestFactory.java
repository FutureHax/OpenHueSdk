package com.t3hh4xx0r.openhuesdk.sdk.bulb;

import java.util.ArrayList;

import com.t3hh4xx0r.openhuesdk.sdk.bulb.IBulbManager.onBulbStateChangedListener;
import com.t3hh4xx0r.openhuesdk.sdk.objects.BulbState;

public class BulbStateRequestFactory {

	public ArrayList<String> getKeys() {
		return keys;
	}

	public ArrayList<Object> getValues() {
		return values;
	}

	ArrayList<String> keys;
	ArrayList<Object> values;

	onBulbStateChangedListener listener = new onBulbStateChangedListener() {
		@Override
		public void onWifiNotAvailable() {
		}

		@Override
		public void onStateUnableToBeChanged(String error) {
		}

		@Override
		public void onStateChanged(BulbState state) {
		}
	};

	public onBulbStateChangedListener getListener() {
		if (listener == null) {
			listener = new onBulbStateChangedListener() {					
				@Override
				public void onWifiNotAvailable() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onStateUnableToBeChanged(String error) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onStateChanged(BulbState state) {
					// TODO Auto-generated method stub
					
				}
			};
		}
		return listener;
	}

	public void setListener(onBulbStateChangedListener listener) {
		this.listener = listener;
	}

	public BulbStateRequestFactory() {
		keys = new ArrayList<String>();
		values = new ArrayList<Object>();
	}

	public void add(StateCodes key, Object value) {
		keys.add(key.value);
		values.add(value);
	}

	public void add(AlertCodes key, Object value) {
		keys.add(key.value);
		values.add(value);
	}

}