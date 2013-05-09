package com.t3hh4xx0r.openhuesdk.sdk.objects;

import java.io.Serializable;

public class Bulb implements Serializable {

	private static final long serialVersionUID = 6575352713587090023L;
	
	String name;
	String number;

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

	@Override
	public String toString() {
		return "Bulb [getName()=" + getName() + ", getNumber()=" + getNumber()
				+ "]";
	}


}
