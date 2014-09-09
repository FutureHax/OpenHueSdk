package com.t3hh4xx0r.openhuesdk.sdk.bulb;
public enum AlertCodes {
	NONE("none"), SELECT("select"), lSELECT("lselect");

	public String getValue() {
		return value;
	}

	String value;

	private AlertCodes(String value) {
		this.value = value;
	}
}