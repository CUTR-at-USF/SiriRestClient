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

package edu.usf.cutr.siri.android.client;

/**
 * Spring imports
 */

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.actionbarsherlock.app.SherlockFragment;

import edu.usf.cutr.siri.android.util.SiriUtils;

/**
 * SIRI imports
 */
import uk.org.siri.siri.ServiceDelivery;
import uk.org.siri.siri.Siri;

/**
 * Java imports
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Android imports
 */
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
//      RuntimeInlineAnnotationReader.cachePackageAnnotation(
//          MonitoredVehicleJourneyStructure.class.getPackage(), new XmlSchemaMine("uk.org.siri.siri.MonitoredVehicleJourneyStructure"));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
      View v = inflater.inflate(R.layout.siri_stop_mon_request, container, false);
            
      //Try to get the developer key from a resource file, if it exists
      String strKey = SiriUtils.getKeyFromResource(this); 
            
      key = (EditText) v.findViewById(R.id.key); 
      key.setText(strKey);
      operatorRef = (EditText) v.findViewById(R.id.operatorRef);
      monitoringRef = (EditText) v.findViewById(R.id.monitoringRef);
      lineRef = (EditText) v.findViewById(R.id.lineRef);
      directionRef = (EditText) v.findViewById(R.id.directionRef);
      stopMonitoringDetailLevel = (EditText) v.findViewById(R.id.stopMonDetailLevel);
      maximumNumberOfCallsOnwards= (EditText) v.findViewById(R.id.maxNumOfCallsOnwards);
      
      final Button button = (Button) v.findViewById(R.id.submit);
      
      button.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
                           
          //Start Async task to make REST request
          new DownloadVehicleInfoTask().execute();
          
          //TODO Get response back and show in another tab, then switch to that tab
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
  private void refreshStates(List<Siri> states) {
      if (states == null) {
          return;
      }

      //StatesListAdapter adapter = new StatesListAdapter(this, states);
      //setListAdapter(adapter);
  }
 
  //***************************************
  // Private classes
  // ***************************************
  private class DownloadVehicleInfoTask extends AsyncTask<Void, Void, List<Siri>> {
      @Override
      protected void onPreExecute() {
          // before the network request begins, show a progress indicator
          showLoadingProgressDialog();
      }

      @Override
      protected List<Siri> doInBackground(Void... params) {
          try {
              // The URL for making the GET request
              //String url = "http://bustime.mta.info/api/siri" + "/vehicle-monitoring.json?OperatorRef=MTA NYCT";
              String url = "http://bustime.mta.info/api/siri/vehicle-monitoring.json?OperatorRef=MTA%20NYCT&DirectionRef=0&LineRef=MTA%20NYCT_S40";
//              final String url = "http://bustime.mta.info/api/siri" + "/vehicle-monitoring.json?" +
//              		"key={key}&OperatorRef={operatorRef}&VehicleRef={vehicleRef}&LineRef={lineRef}&DirectionRef={directionRef}" +
//              		"&VehicleMonitoringDetailLevel={vehicleMonitoringDetailLevel}&MaximumNumberOfCallsOnwards={maximumNumberOfCallsOnwards}";
              
              //Sample vehicle request: http://bustime.mta.info/api/siri/vehicle-monitoring.json?OperatorRef=MTA%20NYCT&DirectionRef=0&LineRef=MTA%20NYCT_S40
              //Sample stop monitoring request: http://bustime.mta.info/api/siri/stop-monitoring.json?OperatorRef=MTA%20NYCT&MonitoringRef=308214
              url.replace(" ", "%20");  //Handle spaces
            
              // Set the Accept header for "application/json"
              HttpHeaders requestHeaders = new HttpHeaders();
              List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
              acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
              //acceptableMediaTypes.add(MediaType.TEXT_PLAIN);
              requestHeaders.setAccept(acceptableMediaTypes);

              // Populate the headers in an HttpEntity object to use for the request
              HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
              
              // Create a new RestTemplate instance
              RestTemplate restTemplate = new RestTemplate();
              restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
              //restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                         
              // Perform the HTTP GET request w/ specified parameters
              ResponseEntity<Siri[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Siri[].class);
//              ResponseEntity<Siri[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Siri[].class, 
//                  key.getText().toString(), operatorRef.getText().toString(), vehicleRef.getText().toString(), 
//                  lineRef.getText().toString(), directionRef.getText().toString(), 
//                  vehicleMonitoringDetailLevel.getText().toString(), maximumNumberOfCallsOnwards.getText().toString());
                            
              //convert the array to a list
              List<Siri> list = Arrays.asList(responseEntity.getBody());
              
//              for(Siri l : list){
//                List<VehicleMonitoringDelivery> listVMD = l.getServiceDelivery().getVehicleMonitoringDelivery();
//                for(VehicleMonitoringDelivery v : listVMD){
//                  Log.d(SiriRestClientActivity.TAG, "ResponseTime = " + v.getResponseTimestamp());
//                  Log.d(SiriRestClientActivity.TAG, "ValidUntil = " + v.getValidUntil());
//                                    
//                }
//              }
              
              // return list
              return list;
          } catch (Exception e) {
              Log.e(SiriRestClientActivity.TAG, e.getMessage(), e);
          }

          return null;
      }

      @Override
      protected void onPostExecute(List<Siri> result) {
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