package com.t3hh4xx0r.openhuesdk.sdk.objects;

public class RegistrationRequest {
	public String devicetype;

	public String username;

	public RegistrationRequest(String devicetype, String username) {
		this.devicetype = devicetype;
		this.username = username;
	}
}