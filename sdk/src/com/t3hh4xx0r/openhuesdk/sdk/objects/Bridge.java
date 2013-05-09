package com.t3hh4xx0r.openhuesdk.sdk.objects;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;

import com.t3hh4xx0r.openhuesdk.sdk.PreferencesManager;
import com.t3hh4xx0r.openhuesdk.sdk.bulb.BulbManager;
import com.t3hh4xx0r.openhuesdk.sdk.bulb.BulbManager.onLightScanCompledListener;

public class Bridge implements Serializable {
	private static final long serialVersionUID = 8274809280967198013L;

	private String id;

	private String internalipaddress;

	private String macaddress;

	public interface bridgeValidityListener {
		public void onBrigeReturnedValid(Bridge b);

		public void onBrigeReturnedInvalid(Bridge b);
	}

	public Bridge() {
	}

	public Bridge(String id, String internalipaddress, String macaddress) {
		this.id = id;
		this.internalipaddress = internalipaddress;
		this.macaddress = macaddress;
	}

	public String getId() {
		return id;
	}

	public String getInternalipaddress() {
		return internalipaddress;
	}

	public String getMacaddress() {
		return macaddress;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setInternalipaddress(String internalipaddress) {
		this.internalipaddress = internalipaddress;
	}

	public void setMacaddress(String macaddress) {
		this.macaddress = macaddress;
	}

	@Override
	public String toString() {
		return "Bridge [macaddress=" + macaddress + ", getId()=" + getId()
				+ ", getInternalipaddress()=" + getInternalipaddress()
				+ ", getMacaddress()=" + getMacaddress() + "]";
	}

	public void isBridgeStillValid(Activity a,
			final bridgeValidityListener listener) {
		PreferencesManager pMan = new PreferencesManager(a);
		BulbManager lMan = new BulbManager(a, pMan.getBridge());
		lMan.getLights(pMan.getBridge(), new onLightScanCompledListener() {
			@Override
			public void onLightsScanCompletedSuccessfully(
					ArrayList<Bulb> bulbList) {
				listener.onBrigeReturnedValid(Bridge.this);
			}

			@Override
			public void onLightsScanCompletedUnsuccessfully(final String error) {
				listener.onBrigeReturnedInvalid(Bridge.this);
			}
		});
	}
}