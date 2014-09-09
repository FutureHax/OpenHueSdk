package com.t3hh4xx0r.openhuesdk.sdk.objects;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Bulb implements Serializable {

	private static final long serialVersionUID = 6575352713587090023L;
	BulbState state;
	String name;
	String number;
	JSONObject rawJSON;
	
	public JSONObject getRawJSON() {
		return rawJSON;
	}

	public void setRawJSON(JSONObject rawJSON) {
		this.rawJSON = rawJSON;		
	}

	public String getName() {
		return name;
	}

	public String getNumber() {
		return number;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public BulbState getState() {
		return state;
	}

	public void setState(BulbState state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return getName();
	}

}
