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

import uk.org.siri.siri.Siri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import edu.usf.cutr.siri.android.client.SiriRestClient;
import edu.usf.cutr.siri.android.ui.MainActivity;
import edu.usf.cutr.siri.android.ui.R;
import edu.usf.cutr.siri.android.util.SiriUtils;

/**
 * The UI for the input fields for the SIRI Vehicle Monitoring Request, which
 * triggers the HTTP request for Vehicle Monitoring Request JSON or XML data.
 * 
 * @author Sean Barbeau
 * 
 */
public class VehicleMonRequestFragment extends BaseRequestFragment {

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
	 * String keys for each field so user-entered content can be saved and
	 * restored between executions
	 */
	String keyVehKey = "VehKey";
	String keyVehOperatorRef = "VehOperatorRef";
	String keyVehLineRef = "VehLineRef";
	String keyVehDirectionRef = "VehDirectionRef";
	String keyVehicleMonitoringDetailLevel = "VehicleMonitoringDetailLevel";
	String keyVehMaximumNumberOfCallsOnwards = "VehMaximumNumberOfCallsOnwards";

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

		// //Set UI fields with saved user-entered text from last run
		// TODO - savedInstanceState is always null, figure out why
		// if(savedInstanceState != null){
		// Log.d(MainActivity.TAG, "VehicleMon savedInstanceState is NOT null");
		// if(savedInstanceState.getString(keyVehKey) != null){
		// //Try to get last-used developer key
		// key.setText(savedInstanceState.getString(keyVehKey));
		// }else{
		// // Try to get the developer key from a resource file, if it exists
		// String strKey = SiriUtils.getKeyFromResource(getActivity());
		// key.setText(strKey);
		// }
		//
		// //if any values = null, then just fill the field with an empty string
		// - null should only happen on first execution
		// operatorRef.setText(savedInstanceState.getString(keyVehOperatorRef)
		// != null ? savedInstanceState.getString(keyVehOperatorRef) : "");
		// lineRef.setText(savedInstanceState.getString(keyVehLineRef) != null ?
		// savedInstanceState.getString(keyVehLineRef) : "");
		// directionRef.setText(savedInstanceState.getString(keyVehDirectionRef)
		// != null ? savedInstanceState.getString(keyVehDirectionRef) : "");
		// vehicleMonitoringDetailLevel.setText(savedInstanceState.getString(keyVehicleMonitoringDetailLevel)
		// != null ?
		// savedInstanceState.getString(keyVehicleMonitoringDetailLevel) : "");
		// maximumNumberOfCallsOnwards.setText(savedInstanceState.getString(keyVehMaximumNumberOfCallsOnwards)
		// != null ?
		// savedInstanceState.getString(keyVehMaximumNumberOfCallsOnwards) :
		// "");
		// }else{
		// Log.d(MainActivity.TAG, "VehicleMon savedInstanceState is null");
		// Try to get the developer key from a resource file, if it exists
		String strKey = SiriUtils.getKeyFromResource(getActivity());
		key.setText(strKey);
		// }

		return v;
	}

	// @Override
	// public void onSaveInstanceState(Bundle outState) {
	// //Save user-entered UI fields for next execution
	// outState.putString(keyVehKey, key.getText().toString());
	// outState.putString(keyVehOperatorRef, operatorRef.getText().toString());
	// outState.putString(keyVehLineRef, lineRef.getText().toString());
	// outState.putString(keyVehDirectionRef,
	// directionRef.getText().toString());
	// outState.putString(keyVehicleMonitoringDetailLevel,
	// vehicleMonitoringDetailLevel.getText().toString());
	// outState.putString(keyVehMaximumNumberOfCallsOnwards,
	// maximumNumberOfCallsOnwards.getText().toString());
	//
	// super.onSaveInstanceState(outState);
	// }

	protected class DownloadVehicleInfoTask extends BaseDownloadInfoTask {

		/**
		 * Make the vehicle mon request to the server using the passed in client, 
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

			Siri siri = client.makeVehicleMonRequest(key.getText().toString(),
					operatorRef.getText().toString(), vehicleRef.getText()
							.toString(), lineRef.getText().toString(),
					directionRefInt, vehicleMonitoringDetailLevel.getText()
							.toString(), maximumNumberOfCallsOnwardsInt);
			return siri;
		}

	}
}