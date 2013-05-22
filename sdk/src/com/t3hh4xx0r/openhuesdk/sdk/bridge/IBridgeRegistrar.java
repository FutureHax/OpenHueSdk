package com.t3hh4xx0r.openhuesdk.sdk.bridge;

import com.t3hh4xx0r.openhuesdk.sdk.objects.Bridge;

public class IBridgeRegistrar {
	public interface OnBridgeRegisteredListener {
		public void bridgeNotReady(String errorMessage);

		public void bridgeReady();
	}

	public interface OnBridgeReturnedListener {
		public void bridgeNotReady(String errorMessage);

		public void bridgeReady(Bridge b);
	}

	public interface OnPushLinkButtonPressedListener {
		public void bridgeNotReady(String errorMessage);

		public void bridgeReady();
	}
}
