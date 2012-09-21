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

/**
 * Spring imports
 */

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import uk.org.siri.siri.Siri;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
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
import edu.usf.cutr.siri.android.ui.MainActivity;
import edu.usf.cutr.siri.android.ui.Preferences;
import edu.usf.cutr.siri.android.ui.R;
import edu.usf.cutr.siri.android.ui.R.id;
import edu.usf.cutr.siri.android.ui.R.layout;
import edu.usf.cutr.siri.android.util.SiriUtils;

/**
 * The UI for the input fields for the SIRI Stop Monitoring Request, which
 * triggers the HTTP request for Stop Monitoring Request JSON or XML data.
 * 
 * @author Sean Barbeau
 * 
 */
public class StopMonRequestFragment extends BaseRequestFragment {

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
				new DownloadStopInfoTask().execute();

				// TODO Get response back and show in another tab, then switch
				// to that tab
			}
		});

		return v;
	}

	protected class DownloadStopInfoTask extends BaseDownloadInfoTask {

		/**
		 * Make the stop mon request to the server using the passed in client, 
		 * and return the Siri object to the superclass
		 */
		@Override
		protected Siri makeRequest(SiriRestClient client) {
			
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

			Siri siri = client.makeStopMonRequest(key.getText().toString(),
						operatorRef.getText().toString(), monitoringRef
								.getText().toString(), lineRef.getText()
								.toString(), directionRefInt,
						stopMonitoringDetailLevel.getText().toString(),
						maximumNumberOfCallsOnwardsInt);
			return siri;

		}
	}
}
