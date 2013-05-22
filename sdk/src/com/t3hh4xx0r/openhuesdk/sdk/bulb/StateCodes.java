package com.t3hh4xx0r.openhuesdk.sdk.bulb;
public enum StateCodes {
	BRIGHTNESS("bri"), ALERT("alert"), ON("on"), COLOR("hue"), SATURATION(
			"sat");

	public String getValue() {
		return value;
	}

	String value;

	private StateCodes(String value) {
		this.value = value;
	}
}