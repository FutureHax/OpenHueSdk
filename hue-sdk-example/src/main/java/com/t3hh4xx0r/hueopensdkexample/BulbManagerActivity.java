package com.t3hh4xx0r.hueopensdkexample;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.t3hh4xx0r.openhuesdk.sdk.ColorPickerView;
import com.t3hh4xx0r.openhuesdk.sdk.ColorPickerView.OnColorChangedListener;
import com.t3hh4xx0r.openhuesdk.sdk.PreferencesManager;
import com.t3hh4xx0r.openhuesdk.sdk.bulb.AlertCodes;
import com.t3hh4xx0r.openhuesdk.sdk.bulb.BulbManager;
import com.t3hh4xx0r.openhuesdk.sdk.bulb.CustomAlert;
import com.t3hh4xx0r.openhuesdk.sdk.bulb.IBulbManager.*;
import com.t3hh4xx0r.openhuesdk.sdk.objects.Bridge;
import com.t3hh4xx0r.openhuesdk.sdk.objects.Bulb;

public class BulbManagerActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {
	public static class BulbFragment extends Fragment {
		BulbManager bulbMan;
		Bulb b;

		public BulbFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_bulb_manager,
					container, false);
			bulbMan = new BulbManager(getActivity(), bridge);
			b = (Bulb) getArguments().getSerializable("bulb");
			View on = rootView.findViewById(R.id.on);
			on.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					bulbMan.turnOn(b, null);
				}
			});
			View off = rootView.findViewById(R.id.off);
			off.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					bulbMan.turnOff(b, null);
				}
			});

			View colorPickers = rootView.findViewById(R.id.color_pickers);
			colorPickers.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					final AlertDialog.Builder builder = new AlertDialog.Builder(
							v.getContext());
					builder.setCancelable(true);
					builder.setItems(new String[] {
							"Picker w/Saturation",
							"Picker w/Brightness",
							"Picker w/Brightness & Saturation"}, new DialogInterface.OnClickListener() {						
						@Override
						public void onClick(DialogInterface d, int pos) {
							d.dismiss();
							showColorPicker(pos, v.getContext(), bulbMan, b);						
						}
					});
					final Dialog d = builder.create();
					d.show();
				}
			});

			View alerts = rootView.findViewById(R.id.alerts);
			alerts.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					final AlertDialog.Builder builder = new AlertDialog.Builder(
							v.getContext());
					builder.setCancelable(true);
					builder.setItems(new String[] {
							"Alert",
							"Long Alert",
							"None", 
							"Custom"}, new DialogInterface.OnClickListener() {						
						@Override
						public void onClick(DialogInterface d, int pos) {
							d.dismiss();
							doAlert(pos, v.getContext(), bulbMan, b);						
						}
					});
					final Dialog d = builder.create();
					d.show();
				}
			});

			View rename = rootView.findViewById(R.id.rename);
			rename.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					final AlertDialog.Builder builder = new AlertDialog.Builder(
							v.getContext());
					bulbMan.alert(b, AlertCodes.lSELECT);
					builder.setCancelable(true);
					builder.setTitle("Rename " + b.getName());
					builder.setMessage("Enter a new name below.");
					LayoutInflater inflate = LayoutInflater.from(v.getContext());
					View root = inflate.inflate(R.layout.rename_popup, null);
					final EditText name = (EditText) root
							.findViewById(R.id.rename);
					builder.setView(root);
					builder.setPositiveButton("Done!",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface d, int arg1) {
									bulbMan.rename(b,
											name.getText().toString(),
											new onBulbRenamedListener() {
												@Override
												public void onBulbRenamedUnsuccessfully() {
													getActivity().runOnUiThread(new Runnable() {														
														@Override
														public void run() {
															Toast.makeText(getActivity(), "Unable to rename the bulb.", Toast.LENGTH_LONG).show();
														}
													});
												};

												@Override
												public void onBulbRenamedSuccessfully(
														Bulb b, String newName) {
													PreferencesManager p = new PreferencesManager(
															v.getContext());
													ArrayList<Bulb> bulbs = p
															.getBulbs();
													String[] bulbNames = new String[bulbs
															.size()];
													int pos = -1;
													for (int i = 0; i < bulbs
															.size(); i++) {
														if (bulbs.get(i).getNumber().equals(b.getNumber())) {
															pos = i;
														}
														bulbNames[i] = bulbs
																.get(i)
																.getName();
													}
													((BulbManagerActivity) getActivity())
															.setNavItems(bulbNames, pos);
													
												}

												@Override
												public void onWifiNotAvailable() {
													// TODO Auto-generated method stub
													
												}
											});
									bulbMan.alert(b, AlertCodes.NONE);
									d.dismiss();
								}
							});
					final Dialog d = builder.create();
					d.show();
				}
			});

			bulbMan.alert(b, AlertCodes.SELECT);
			return rootView;
		}
	}
	
	private static void doAlert(int pos, Context c,
			final BulbManager bulbMan, final Bulb b) {
		if (pos == 0) {
			bulbMan.alert(b, AlertCodes.SELECT);
		} else if (pos == 1) {
			bulbMan.alert(b, AlertCodes.lSELECT);
		} else if (pos == 2) {
			bulbMan.alert(b, AlertCodes.NONE);
		} else if (pos == 3) {
			final CustomAlert alert = new CustomAlert();
			alert.setLength(1000);
			final AlertDialog.Builder builder = new AlertDialog.Builder(c);
			builder.setCancelable(true);
			LayoutInflater inflate = LayoutInflater.from(c);
			View colorViews = inflate.inflate(
					R.layout.color_picker_popup_sb, null);
			final ColorPickerView pickers = (ColorPickerView) colorViews
					.findViewById(R.id.picker);
			final SeekBar satBar = (SeekBar) colorViews
					.findViewById(R.id.saturation);
			final SeekBar brightBar = (SeekBar) colorViews
					.findViewById(R.id.brightness);
			
			builder.setView(colorViews);
			builder.setPositiveButton("Done!",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface d, int arg1) {
							alert.setBrightness(((brightBar == null) ? 420 : brightBar.getProgress()));
							alert.setColor( pickers.getHue());
							alert.setSat(((satBar == null) ? 420 : satBar.getProgress()));
							d.dismiss();
							bulbMan.customAlert(b, alert);
						}
					});
			final Dialog d = builder.create();
			d.show();	
		}
		
	}
	
	private static void showColorPicker(int pos, Context c, final BulbManager bulbMan, final Bulb b) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setCancelable(true);
		int layout;
		if (pos == 0) {
			layout = R.layout.color_picker_popup_s;
		} else if (pos == 1)  {
			layout = R.layout.color_picker_popup_b;
		} else {
			layout = R.layout.color_picker_popup_sb;

		}
		LayoutInflater inflate = LayoutInflater.from(c);
		View colorViews = inflate.inflate(
				layout, null);
		final ColorPickerView pickers = (ColorPickerView) colorViews
				.findViewById(R.id.picker);
		final SeekBar satBar = (SeekBar) colorViews
				.findViewById(R.id.saturation);
		final SeekBar brightBar = (SeekBar) colorViews
				.findViewById(R.id.brightness);
		
		pickers.setOnColorChangedListener(new OnColorChangedListener() {
			@Override
			public void colorChanged(int color, int hue) {
				bulbMan.setLightValues(b, hue,((satBar == null) ? 420 : satBar.getProgress()), 
						((brightBar == null) ? 420 : brightBar.getProgress()), null);
			}
		});
		builder.setView(colorViews);
		builder.setPositiveButton("Done!",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface d, int arg1) {
						bulbMan.setLightValues(b, pickers.getHue(),
								((satBar == null) ? 420 : satBar.getProgress()), 
								((brightBar == null) ? 420 : brightBar.getProgress()), null);
						d.dismiss();
					}
				});
		final Dialog d = builder.create();
		d.show();		
	}

	ArrayList<Bulb> bulbs;
	static Bridge bridge;

	PreferencesManager pMan;

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bulb_manager);
		pMan = new PreferencesManager(this);
		bulbs = pMan.getBulbs();
		bridge = pMan.getBridge();

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		String[] bulbNames = new String[bulbs.size()];
		for (int i = 0; i < bulbs.size(); i++) {
			bulbNames[i] = bulbs.get(i).getName();
		}
		setNavItems(bulbNames, -1);
	}

	private void setNavItems(final String[] bulbNames, final int pos) {
		BulbManagerActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				getActionBar().setListNavigationCallbacks(
						new ArrayAdapter<String>(
								getActionBarThemedContextCompat(),
								android.R.layout.simple_list_item_1,
								android.R.id.text1, bulbNames),
						BulbManagerActivity.this);
				if (pos != -1) {
					getActionBar().setSelectedNavigationItem(pos);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.bulb_manager, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		Fragment fragment = new BulbFragment();
		Bundle args = new Bundle();
		args.putSerializable("bulb", bulbs.get(position));
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
		return true;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

}
