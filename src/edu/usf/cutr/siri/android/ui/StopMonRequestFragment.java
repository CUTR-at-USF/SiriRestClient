/*
 * Copyright 2012 University of South Florida
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package edu.usf.cutr.siri.android.ui;

/**
 * Spring imports
 */

import java.util.List;

import uk.org.siri.siri.Siri;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

import edu.usf.cutr.siri.android.client.SiriRestClient;
import edu.usf.cutr.siri.android.client.SiriRestClientConfig;
import edu.usf.cutr.siri.android.util.SiriUtils;

/**
 * SIRI imports
 */
/**
 * Java imports
 */
/**
 * Android imports
 */

/**
 * The UI for the input fields for the SIRI Vehicle Monitoring Request
 * 
 * @author Sean Barbeau
 * 
 */
public class StopMonRequestFragment extends SherlockFragment {

	private ProgressDialog progressDialog;

	private boolean destroyed = false;

	public StopMonRequestFragment() {
	}

	/**
	 * EditText fields to hold values typed in by user
	 */
	EditText key;
	EditText operatorRef;
	EditText monitoringRef;
	EditText lineRef;
	EditText directionRef;
	EditText stopMonitoringDetailLevel;
	EditText maximumNumberOfCallsOnwards;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// RuntimeInlineAnnotationReader.cachePackageAnnotation(
		// MonitoredVehicleJourneyStructure.class.getPackage(), new
		// XmlSchemaMine("uk.org.siri.siri.MonitoredVehicleJourneyStructure"));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.siri_stop_mon_request, container,
				false);

		// Try to get the developer key from a resource file, if it exists
		String strKey = SiriUtils.getKeyFromResource(getActivity());

		key = (EditText) v.findViewById(R.id.key);
		key.setText(strKey);
		operatorRef = (EditText) v.findViewById(R.id.operatorRef);
		monitoringRef = (EditText) v.findViewById(R.id.monitoringRef);
		lineRef = (EditText) v.findViewById(R.id.lineRef);
		directionRef = (EditText) v.findViewById(R.id.directionRef);
		stopMonitoringDetailLevel = (EditText) v
				.findViewById(R.id.stopMonDetailLevel);
		maximumNumberOfCallsOnwards = (EditText) v
				.findViewById(R.id.maxNumOfCallsOnwardsStop);

		final Button button = (Button) v.findViewById(R.id.submit);

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// Start Async task to make REST request
				new DownloadVehicleInfoTask().execute();

				// TODO Get response back and show in another tab, then switch
				// to that tab
			}
		});

		return v;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		destroyed = true;
	}

	// ***************************************
	// Private methods
	// ***************************************
	private void refreshStates(Siri states) {
		if (states == null) {
			return;
		}

		// StatesListAdapter adapter = new StatesListAdapter(this, states);
		// setListAdapter(adapter);
	}

	// ***************************************
	// Private classes
	// ***************************************
	private class DownloadVehicleInfoTask extends AsyncTask<Void, Void, Siri> {
		@Override
		protected void onPreExecute() {
			// before the network request begins, show a progress indicator
			showLoadingProgressDialog();
		}

		@Override
		protected Siri doInBackground(Void... params) {
			try {
				// The URL for making the GET request
				// final String url = "http://bustime.mta.info/api/siri" +
				// "/vehicle-monitoring.json?" +
				// "key={key}&OperatorRef={operatorRef}&VehicleRef={vehicleRef}&LineRef={lineRef}&DirectionRef={directionRef}"
				// +
				// "&VehicleMonitoringDetailLevel={vehicleMonitoringDetailLevel}&MaximumNumberOfCallsOnwards={maximumNumberOfCallsOnwards}";

				// Sample stop monitoring request:
				// http://bustime.mta.info/api/siri/stop-monitoring.json?OperatorRef=MTA%20NYCT&MonitoringRef=308214
				Siri s = null;

				// Get user preferences
				SharedPreferences sharedPref = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
				int responseType = Integer.parseInt(sharedPref.getString(
						Preferences.KEY_RESPONSE_TYPE, "0"));
				int httpConnectionType = Integer.parseInt(sharedPref.getString(
						Preferences.KEY_HTTP_CONNECTION_TYPE, "0"));
				int jacksonObjectType = Integer.parseInt(sharedPref.getString(
						Preferences.KEY_JACKSON_OBJECT_TYPE, "0"));

				// Setup server config
				SiriRestClientConfig config = new SiriRestClientConfig(
						responseType);
				config.setHttpConnectionType(httpConnectionType);
				config.setJacksonObjectType(jacksonObjectType);

				// Get integer values from TextBoxes
				int directionRefInt = -1, maximumNumberOfCallsOnwardsInt = -1;
				try {
					directionRefInt = Integer.parseInt(directionRef.getText()
							.toString());
				} catch (NumberFormatException e) {
					Log.w(MainActivity.TAG,
							"Invalid value entered for DirectionRef: " + e);
				}
				try {
					maximumNumberOfCallsOnwardsInt = Integer
							.parseInt(maximumNumberOfCallsOnwards.getText()
									.toString());
				} catch (NumberFormatException e) {
					Log.w(MainActivity.TAG,
							"Invalid value entered for MaximumNumberOfCallsOnwardsInt: "
									+ e);
				}

				// Instantiate client with URLs for server and config
				SiriRestClient client = new SiriRestClient(
						"http://bustime.mta.info/api/siri/vehicle-monitoring",
						"http://bustime.mta.info/api/siri/stop-monitoring",
						config);

				// Make request to server
				s = client.makeStopMonRequest(key.getText().toString(),
						operatorRef.getText().toString(), monitoringRef
								.getText().toString(), lineRef.getText()
								.toString(), directionRefInt,
						stopMonitoringDetailLevel.getText().toString(),
						maximumNumberOfCallsOnwardsInt);

				final long elapsedTime = client.getLastRequestTime();

				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(
								getActivity(),
								"Elapsed Time = " + (elapsedTime) / (1000000L)
										+ "ms", Toast.LENGTH_SHORT).show();
					}
				});

				if (s != null) {
					SiriUtils.printContents(s);
				}

				return s;

			} catch (Exception e) {
				Log.e(MainActivity.TAG, e.getMessage(), e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Siri result) {
			// hide the progress indicator when the network request is complete
			dismissProgressDialog();

			// return the list of vehicle info
			refreshStates(result);
		}

		// ***************************************
		// Public methods
		// ***************************************
		public void showLoadingProgressDialog() {
			this.showProgressDialog("Requesting. Please wait...");
		}

		public void showProgressDialog(CharSequence message) {
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(getActivity());
				progressDialog.setIndeterminate(true);
			}

			progressDialog.setMessage(message);
			progressDialog.show();
		}

		public void dismissProgressDialog() {
			if (progressDialog != null && !destroyed) {
				progressDialog.dismiss();
			}
		}
	}
}