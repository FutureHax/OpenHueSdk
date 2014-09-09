package com.t3hh4xx0r.openhuesdk.sdk.bulb;

public class CustomAlert {
	double length;
	double sat;
	double brightness;
	double color;

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getSat() {
		return sat;
	}

	public void setSat(double sat) {
		this.sat = sat;
	}

	public double getBrightness() {
		return brightness;
	}

	public void setBrightness(double brightness) {
		this.brightness = brightness;
	}

	public double getColor() {
		return color;
	}

	public void setColor(double color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "CustomAlert ["
				+ ", getLength()=" + getLength() + ", getSat()=" + getSat()
				+ ", getBrightness()=" + getBrightness() + ", getColor()="
				+ getColor() + "]";
	}

}
