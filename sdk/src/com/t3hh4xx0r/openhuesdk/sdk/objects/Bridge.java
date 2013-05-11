package com.t3hh4xx0r.openhuesdk.sdk.objects;

import java.io.Serializable;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.t3hh4xx0r.openhuesdk.sdk.PreferencesManager;
import com.t3hh4xx0r.openhuesdk.sdk.Utils;
import com.t3hh4xx0r.openhuesdk.sdk.bridge.BridgeRegistrar.OnBridgeReturnedListener;

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

	public boolean isPlaceHolder() {
		return (getId().equals("") ||
				getInternalipaddress().equals("") ||
				getMacaddress().equals(""));
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

	private void isBridgeStillValid(Activity a,
			final bridgeValidityListener listener) {
		BridgeValidityTesterTask t = new BridgeValidityTesterTask(this, a, listener);
		t.execute();
	}
	
	private class BridgeValidityTesterTask extends AsyncTask<Void, Void, Boolean> {
		Bridge b;
		Context c;
		PreferencesManager pMan;
		bridgeValidityListener listener;
		
		private BridgeValidityTesterTask(Bridge b, Context c, bridgeValidityListener listener) {
			this.b = b;
			this.c = c;
			pMan = new PreferencesManager(c);
			this.listener = listener;
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			String content = "not pressed";
			try {
				DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
				HttpPost localHttpPost = new HttpPost("http://"
						+ b.getInternalipaddress() + "/api/");
				Gson localGson = new Gson();
				RegistrationRequest localRegistrationRequest = new RegistrationRequest(
						Utils.getDeviceType(c).getType(), pMan.getUserName());

				localHttpPost.setEntity(new StringEntity(localGson
						.toJson(localRegistrationRequest)));
				localHttpPost.setHeader("Accept", "application/json");
				localHttpPost.setHeader("Content-type", "application/json");
				content = EntityUtils.toString(localDefaultHttpClient
						.execute(localHttpPost).getEntity());
				Log.d("CONTENT FROM VALIDATION!", content + "");

			} catch (Exception e) {
				
			}
			return !content.contains("not pressed");
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			Log.d("RESULT FROM VALIDATION!", result + "");
			if (result) {
				listener.onBrigeReturnedValid(b);
			} else {
				listener.onBrigeReturnedInvalid(b);
			}
		}

	}

}