package com.t3hh4xx0r.openhuesdk.sdk.objects;

public class BulbState {

	public class State {
		boolean on, reachable;

		String alert, effect, colormode;

		double bri, hue, sat, ct;

		public String getAlert() {
			return alert;
		}

		public double getBri() {
			return bri;
		}

		public String getColormode() {
			return colormode;
		}

		public double getCt() {
			return ct;
		}

		public String getEffect() {
			return effect;
		}

		public double getHue() {
			return hue;
		}

		public double getSat() {
			return sat;
		}

		public boolean isOn() {
			return on;
		}

		public boolean isReachable() {
			return reachable;
		}

		public void setAlert(String alert) {
			this.alert = alert;
		}

		public void setBri(double bri) {
			this.bri = bri;
		}

		public void setColormode(String colormode) {
			this.colormode = colormode;
		}

		public void setCt(double ct) {
			this.ct = ct;
		}

		public void setEffect(String effect) {
			this.effect = effect;
		}

		public void setHue(double hue) {
			this.hue = hue;
		}

		public void setOn(boolean on) {
			this.on = on;
		}

		public void setReachable(boolean reachable) {
			this.reachable = reachable;
		}

		public void setSat(double sat) {
			this.sat = sat;
		}

		@Override
		public String toString() {
			return "State [isReachable()=" + isReachable() + ", getSat()="
					+ getSat() + ", getAlert()=" + getAlert() + ", getBri()="
					+ getBri() + ", getColormode()=" + getColormode()
					+ ", getCt()=" + getCt() + ", getEffect()=" + getEffect()
					+ ", getHue()=" + getHue() + ", isOn()=" + isOn() + "]";
		}

	}

	String type, name, modelid, swversion;
	State state;

	public String getModelid() {
		return modelid;
	}

	public String getName() {
		return name;
	}

	public State getState() {
		return state;
	}

	public String getSwversion() {
		return swversion;
	}

	public String getType() {
		return type;
	}

	public void setModelid(String modelid) {
		this.modelid = modelid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setSwversion(String swversion) {
		this.swversion = swversion;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "BulbState [getModelid()=" + getModelid() + ", getName()="
				+ getName() + ", getState()=" + getState()
				+ ", getSwversion()=" + getSwversion() + ", getType()="
				+ getType() + "]";
	}

}
