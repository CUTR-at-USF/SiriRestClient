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

import java.text.DecimalFormat;
import java.util.ArrayList;

import uk.org.siri.siri.Siri;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
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

import edu.usf.cutr.siri.android.client.SiriRestClientConfig;
import edu.usf.cutr.siri.android.client.SiriRestClient;

import edu.usf.cutr.siri.android.util.SiriUtils;

/**
 * The UI for the input fields for the SIRI Vehicle Monitoring Request, which
 * triggers the HTTP request for Vehicle Monitoring Request JSON or XML data.
 * 
 * @author Sean Barbeau
 * 
 */
public class VehicleMonRequestFragment extends SherlockFragment {

	private ProgressDialog progressDialog;

	private boolean destroyed = false;

	/**
	 * EditText fields to hold values typed in by user
	 */
	EditText key;
	EditText operatorRef;
	EditText vehicleRef;
	EditText lineRef;
	EditText directionRef;
	EditText vehicleMonitoringDetailLevel;
	EditText maximumNumberOfCallsOnwards;
	
	/**
	 * String keys for each field so user-entered content can be saved and restored
	 * between executions
	 */
	
	String keyVehKey = "VehKey";
	String keyVehOperatorRef = "VehOperatorRef";
	String keyVehLineRef = "VehLineRef";
	String keyVehDirectionRef = "VehDirectionRef";
	String keyVehicleMonitoringDetailLevel = "VehicleMonitoringDetailLevel";
	String keyVehMaximumNumberOfCallsOnwards = "VehMaximumNumberOfCallsOnwards";

	// Used to format decimals to 3 places
	DecimalFormat df = new DecimalFormat("#,###.###");

	// Elapsed times for a number of sequentially executed requests, based on
	// user setting, in milliseconds
	ArrayList<Double> elapsedTimes = new ArrayList<Double>();

	// User preferences, retrieved from settings
	int responseType;
	int httpConnectionType;
	int jacksonObjectType;
	int numRequests;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.siri_vehicle_mon_request, container,
				false);

		key = (EditText) v.findViewById(R.id.key);		
		operatorRef = (EditText) v.findViewById(R.id.operatorRef);
		vehicleRef = (EditText) v.findViewById(R.id.vehicleRef);
		lineRef = (EditText) v.findViewById(R.id.lineRef);
		directionRef = (EditText) v.findViewById(R.id.directionRef);
		vehicleMonitoringDetailLevel = (EditText) v
				.findViewById(R.id.vehicleMonDetailLevel);
		maximumNumberOfCallsOnwards = (EditText) v
				.findViewById(R.id.maxNumOfCallsOnwards);

		final Button button = (Button) v.findViewById(R.id.submit);

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// Start Async task to make REST request
				new DownloadVehicleInfoTask().execute();

				// TODO Get response back and show in another tab, then switch
				// to that tab
			}
		});
			
		//Set UI fields with saved user-entered text from last run
//		if(savedInstanceState.getString(keyVehKey) != null){  //TODO - This line throws NPE, and it shouldn't.  Check it out later.  
			//Try to get last-used developer key
			//key.setText(savedInstanceState.getString(keyVehKey));
//		}else{
			// Try to get the developer key from a resource file, if it exists
			String strKey = SiriUtils.getKeyFromResource(getActivity());
			key.setText(strKey);
//		}
//		
//		//if any values = null, then just fill the field with an empty string - null should only happen on first execution
//		operatorRef.setText(savedInstanceState.getString(keyVehOperatorRef) != null ? savedInstanceState.getString(keyVehOperatorRef) : "");
//		lineRef.setText(savedInstanceState.getString(keyVehLineRef) != null ? savedInstanceState.getString(keyVehLineRef) : "");
//		directionRef.setText(savedInstanceState.getString(keyVehDirectionRef) != null ? savedInstanceState.getString(keyVehDirectionRef) : "");
//		vehicleMonitoringDetailLevel.setText(savedInstanceState.getString(keyVehicleMonitoringDetailLevel) != null ? savedInstanceState.getString(keyVehicleMonitoringDetailLevel) : "");
//		maximumNumberOfCallsOnwards.setText(savedInstanceState.getString(keyVehMaximumNumberOfCallsOnwards) != null ? savedInstanceState.getString(keyVehMaximumNumberOfCallsOnwards) : "");
		
		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		//Save user-entered UI fields for next execution
		outState.putString(keyVehKey, key.getText().toString());
		outState.putString(keyVehOperatorRef, operatorRef.getText().toString());
		outState.putString(keyVehLineRef, lineRef.getText().toString());
		outState.putString(keyVehDirectionRef, directionRef.getText().toString());		
		outState.putString(keyVehicleMonitoringDetailLevel, vehicleMonitoringDetailLevel.getText().toString());		
		outState.putString(keyVehMaximumNumberOfCallsOnwards, maximumNumberOfCallsOnwards.getText().toString());		
				
		super.onSaveInstanceState(outState);
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

	/**
	 * Get user preferences for connection and parsing types
	 */
	private void getUserPreferences() {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		responseType = Integer.parseInt(sharedPref.getString(
				Preferences.KEY_RESPONSE_TYPE, "0"));
		httpConnectionType = Integer.parseInt(sharedPref.getString(
				Preferences.KEY_HTTP_CONNECTION_TYPE, "0"));
		jacksonObjectType = Integer.parseInt(sharedPref.getString(
				Preferences.KEY_JACKSON_OBJECT_TYPE, "0"));
		// Get number of consecutive requests to execute for time
		// benchmarking
		numRequests = Integer.parseInt(sharedPref.getString(
				Preferences.KEY_NUM_REQUESTS, "1"));
	}

	// ***************************************
	// Private classes
	// ***************************************
	private class DownloadVehicleInfoTask extends
			AsyncTask<Void, Integer, Siri> {
		@Override
		protected void onPreExecute() {
			// before the network request begins, show a progress indicator
			showLoadingProgressDialog();
		}

		@Override
		protected Siri doInBackground(Void... params) {
			// Sample vehicle request:
			// http://bustime.mta.info/api/siri/vehicle-monitoring.json?OperatorRef=MTA%20NYCT&DirectionRef=0&LineRef=MTA%20NYCT_S40
			Siri s = null;

			getUserPreferences();

			// Setup server config
			SiriRestClientConfig config = new SiriRestClientConfig(responseType);
			config.setHttpConnectionType(httpConnectionType);
			config.setJacksonObjectType(jacksonObjectType);

			// Get integer values from TextBoxes
			int directionRefInt = -1, maximumNumberOfCallsOnwardsInt = -1;
			try {
				directionRefInt = Integer.parseInt(directionRef.getText()
						.toString());
			} catch (NumberFormatException e) {
				if (directionRef.getText().toString().length() != 0) {
					Log.w(MainActivity.TAG,
							"Invalid value entered for DirectionRef: " + e);
				}
			}
			try {
				maximumNumberOfCallsOnwardsInt = Integer
						.parseInt(maximumNumberOfCallsOnwards.getText()
								.toString());
			} catch (NumberFormatException e) {
				if (maximumNumberOfCallsOnwards.getText().toString().length() != 0) {
					Log.w(MainActivity.TAG,
							"Invalid value entered for MaximumNumberOfCallsOnwardsInt: "
									+ e);
				}
			}

			// Instantiate client with URLs for server and config
			SiriRestClient client = new SiriRestClient(
					"http://bustime.mta.info/api/siri/vehicle-monitoring",
					"http://bustime.mta.info/api/siri/stop-monitoring", config);

			// Clear current benchmarking test results
			elapsedTimes.clear();

			Log.d(MainActivity.TAG, "Executing " + numRequests + " requests...");

			// Loop for numRequest times
			for (int i = 0; i < numRequests; i++) {
				// Make request to server
				s = client.makeVehicleMonRequest(key.getText().toString(),
						operatorRef.getText().toString(), vehicleRef.getText()
								.toString(), lineRef.getText().toString(),
						directionRefInt, vehicleMonitoringDetailLevel.getText()
								.toString(), maximumNumberOfCallsOnwardsInt);

				// Get benchmark of how long the request took
				final long elapsedTimeNanoSeconds = client.getLastRequestTime();

				final double elapsedTimeMilliSeconds = elapsedTimeNanoSeconds / 1000000.0;

				elapsedTimes.add(elapsedTimeMilliSeconds);

				if (s != null) {
					SiriUtils.printContents(s);
				}

				// If we're running more than one test, show an update in the
				// progress bar
				if (numRequests > 1) {
					publishProgress((int) ((i / (float) numRequests) * 100));
				}
				
				if(isCancelled()){
					//User aborted the task, so we need to cleanly stop this task
					Log.d(MainActivity.TAG,
							"User canceled the task.");
					return s;
				}
			}

			//Print out and display the results
			showResults();			

			//Return most recent Siri object
			return s;
		}
		
		@Override
		protected void onPostExecute(Siri result) {
			// hide the progress indicator when the network request is complete
			dismissProgressDialog();

			// return the list of vehicle info
			refreshStates(result);
		}
					
		@Override
		protected void onProgressUpdate(Integer... progress) {
			// Update the progress UI with a percent complete when running
			// multiple tests
			progressDialog.setIndeterminate(false);
			setProgressPercent(progress[0]);
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
			}

			// Show progress as tests are completed
			progressDialog.setIndeterminate(true);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			//Allow the user to cancel the request, and clean up afterwords
			progressDialog.setOnCancelListener(new OnCancelListener() {
	            public void onCancel(DialogInterface arg0) {
	                 dismissProgressDialog();	                 
	                 cancel(true);
	            }
	        });

			// Show an indeterminate spinner in the Activity title bar,
			// to tell the user something is going on during the initial
			// server request
			getActivity().setProgressBarIndeterminateVisibility(Boolean.TRUE);

			progressDialog.setMessage(message);
			progressDialog.show();
		}

		/**
		 * If we executing several requests, we'll show the user how close we
		 * are to finished.
		 * 
		 * @param percent
		 *            percentage of total tests that are complete
		 */
		public void setProgressPercent(int percent) {
			progressDialog.setProgress(percent);
		}

		public void dismissProgressDialog() {
			if (progressDialog != null && !destroyed) {
				progressDialog.dismiss();				
			}
			
			// Shut down the indeterminate progress indicator on the Activity
			// Action Bar
			getActivity().setProgressBarIndeterminateVisibility(Boolean.FALSE);
		}
		
		public void showResults(){
			if (numRequests == 1) {
				// If there was only one test, then show a Toast of the single
				// result
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						if (elapsedTimes.get(0) != 0) {
							// Request was successful. Show the amount of time
							// it
							// took
							Toast.makeText(
									getActivity(),
									"Elapsed Time = "
											+ df.format(elapsedTimes.get(0))
											+ "ms", Toast.LENGTH_SHORT).show();
							Log.d(MainActivity.TAG,
									"Elapsed Time = "
											+ df.format(elapsedTimes.get(0))
											+ "ms");
						} else {
							// Request was NOT successful. Show error message.
							Toast.makeText(
									getActivity(),
									"An error occured during the last request.",
									Toast.LENGTH_SHORT).show();
							Log.d(MainActivity.TAG,
									"An error occured during the last request.");
						}
					}
				});
			} else {
				// Show multiple results in longer toast
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						// Request was successful. Show the amount of time it
						// took
						Toast.makeText(
								getActivity(),
								"Elapsed Times (ms) = "
										+ elapsedTimes.toString(),
								Toast.LENGTH_SHORT).show();

					}
				});
			}

			Log.d(MainActivity.TAG,
					"Elapsed times test results in milliseconds:");
			Log.d(MainActivity.TAG, elapsedTimes.toString());
		}
	}
}