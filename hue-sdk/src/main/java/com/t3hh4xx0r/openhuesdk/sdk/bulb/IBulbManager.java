package com.t3hh4xx0r.openhuesdk.sdk.bulb;

import java.util.ArrayList;

import com.t3hh4xx0r.openhuesdk.sdk.objects.Bulb;
import com.t3hh4xx0r.openhuesdk.sdk.objects.BulbState;

public class IBulbManager {
	public interface onLightScanCompledListener {
		public void onLightsScanCompletedSuccessfully(ArrayList<Bulb> bulbList);

		public void onLightsScanCompletedUnsuccessfully(String error, boolean couldHaveMoved);

		public void onWifiNotAvailable();
	}

	public interface onBulbRenamedListener {
		public void onBulbRenamedSuccessfully(Bulb b, String newName);

		public void onBulbRenamedUnsuccessfully();

		public void onWifiNotAvailable();
	}

	public interface onBulbStateFetchedListener {
		public void onStateFetched(BulbState state);

		public void onStateUnableToBeFetched(String error);

		public void onWifiNotAvailable();
	}

	public interface onBulbStateChangedListener {
		public void onStateChanged(BulbState state);

		public void onStateUnableToBeChanged(String error);

		public void onWifiNotAvailable();
	}
}
