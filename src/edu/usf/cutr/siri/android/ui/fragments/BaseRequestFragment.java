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

package edu.usf.cutr.siri.android.ui.fragments;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import uk.org.siri.siri.Siri;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

import edu.usf.cutr.siri.android.client.SiriRestClient;
import edu.usf.cutr.siri.android.client.SiriRestClientConfig;
import edu.usf.cutr.siri.android.ui.MainActivity;
import edu.usf.cutr.siri.android.ui.Preferences;
import edu.usf.cutr.siri.android.util.SiriUtils;

/**
 * This class defines the basic implementation that's shared by both the
 * StopMonRequestFragment and VehicelMonRequestFragment for the user interface
 * progress bar, test logic, and logic for sending a request to the server, and
 * leaves some methods abstract for those sub-classes to fill in the
 * implementation relevant to each request type.
 * 
 * @author Sean J. Barbeau
 * 
 */
public abstract class BaseRequestFragment extends SherlockFragment {
	protected ProgressDialog progressDialog;

	protected boolean destroyed = false;

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
	double timeBetweenRequests;
	boolean beepOnTestComplete;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		destroyed = true;
	}

	// ***************************************
	// Private methods
	// ***************************************
	protected void refreshStates(Siri states) {
		if (states == null) {
			return;
		}

		// StatesListAdapter adapter = new StatesListAdapter(this, states);
		// setListAdapter(adapter);
	}

	/**
	 * Get user preferences for connection and parsing types
	 */
	protected void getUserPreferences() {
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
		// Get amount of time between consecutive requests, in seconds
		timeBetweenRequests = Double.parseDouble(sharedPref
				.getString(Preferences.KEY_TIME_BETWEEN_REQUESTS, "0"));
		//Get whether or not to beep when tests are complete
		beepOnTestComplete = sharedPref
				.getBoolean(Preferences.KEY_BEEP_ON_TEST_COMPLETE, false);
	}

	// ***************************************
	// Protected classes
	// ***************************************
	protected abstract class BaseDownloadInfoTask extends
			AsyncTask<Void, Integer, Siri> {

		/**
		 * Client object that makes request to the server
		 */
		protected SiriRestClient client;

		@Override
		protected void onPreExecute() {
			// before the network request begins, show a progress indicator
			showLoadingProgressDialog();
		}

		@Override
		protected Siri doInBackground(Void... params) {
			// Sample vehicle request:
			// http://bustime.mta.info/api/siri/vehicle-monitoring.json?OperatorRef=MTA%20NYCT&DirectionRef=0&LineRef=MTA%20NYCT_S40
			getUserPreferences();

			Siri s = null;

			// Setup server config
			SiriRestClientConfig config = new SiriRestClientConfig(responseType);
			config.setHttpConnectionType(httpConnectionType);
			config.setJacksonObjectType(jacksonObjectType);

			// Instantiate client with URLs for server and config
			client = new SiriRestClient(
					"http://bustime.mta.info/api/siri/vehicle-monitoring",
					"http://bustime.mta.info/api/siri/stop-monitoring", config);

			// Clear current benchmarking test results
			elapsedTimes.clear();

			Log.d(MainActivity.TAG, "Executing " + numRequests + " requests...");

			// Loop for numRequest times
			for (int i = 0; i < numRequests; i++) {
				// Make request to server via subclass and get Siri response
				s = makeRequest(client);

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

				if (isCancelled()) {
					// User aborted the task, so we need to cleanly stop this
					// task
					Log.d(MainActivity.TAG, "User canceled the task.");
					return s;
				}

				//If the user-specific amount of time between tests
				//is greater than 0, and we still have tests left to execute, then wait
				if(((numRequests - i) > 1) && timeBetweenRequests > 0 ){
					Log.d(MainActivity.TAG, "Sleeping " + timeBetweenRequests + " second(s)...");
					try {
						Thread.sleep((long) (timeBetweenRequests * 1000));
					} catch (InterruptedException e) {
						Log.e(MainActivity.TAG, "Error sleeping in AsyncTask between requests: " + e);
					}
					
				}
			}

			// Print out and display the results
			showResults();
			
			//Beep on test completion, if the preference is set
			if(beepOnTestComplete){
				try {
					playSound(getActivity());
				} catch (Exception e) {
					Log.w(MainActivity.TAG, "Error trying to beep on test completion: " + e);					
				}
			}

			// Return most recent Siri object
			return s;
		}

		/**
		 * Left to the implementing sub-class to make the exact query to the
		 * server and return a Siri object
		 * 
		 * @param client
		 *            SiriRestClient object that should be used to make the
		 *            request
		 * @return Siri object containing real-time transit information
		 */
		protected abstract Siri makeRequest(SiriRestClient client);

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
			// Allow the user to cancel the request, and clean up afterwords
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

		public void showResults() {
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
	
	/**
	 * Make a beep to indicate the test is done
	 * 
	 * @param context	
	 */
	public void playSound(Context context) throws Exception {
		final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
	    tg.startTone(ToneGenerator.TONE_PROP_PROMPT);
	}

}
