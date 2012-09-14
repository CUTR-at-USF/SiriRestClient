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

package edu.usf.cutr.siri.android.util;

/**
 * Java imports
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import edu.usf.cutr.siri.android.ui.MainActivity;

/**
 * Siri POJO imports
 */
import uk.org.siri.siri.AffectedVehicleJourney;
import uk.org.siri.siri.MonitoredStopVisit;
import uk.org.siri.siri.PtConsequence;
import uk.org.siri.siri.PtSituationElement;
import uk.org.siri.siri.Siri;
import uk.org.siri.siri.SituationExchangeDelivery;
import uk.org.siri.siri.SituationRef;
import uk.org.siri.siri.StopMonitoringDelivery;
import uk.org.siri.siri.VehicleActivity;
import uk.org.siri.siri.VehicleMonitoringDelivery;

/**
 * Android imports
 */
import edu.usf.cutr.siri.android.ui.R;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.util.Log;

/**
 * This class holds utility methods for the application
 * 
 * @author Sean J. Barbeau
 * 
 */
public class SiriUtils {

	/**
	 * Try to grab the developer key from an unversioned resource file, if it
	 * exists
	 * 
	 * @return the developer key from an unversioned resource file, or empty
	 *         string if it doesn't exist
	 */
	public static String getKeyFromResource(Context context) {
		String strKey = new String("");

		try {
			InputStream in = context.getResources().openRawResource(R.raw.devkey);
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			StringBuilder total = new StringBuilder();

			while ((strKey = r.readLine()) != null) {
				total.append(strKey);
			}

			strKey = total.toString();

			strKey.trim(); // Remove any whitespace

		} catch (NotFoundException e) {
			Log.w(MainActivity.TAG,
					"Warning - didn't find the developer key file:" + e);
		} catch (IOException e) {
			Log.w(MainActivity.TAG,
					"Error reading the developer key file:" + e);
		}

		return strKey;
	}
	
	/**
	 * Prints the contents of a Siri object
	 * 
	 * @param siri response from Mobile SIRI API
	 */
	public static void printContents(Siri siri){
		Log.i(MainActivity.TAG, "-----------------------------------------------------");
    	Log.i(MainActivity.TAG, "-               Service Delivery:                   -");
    	Log.i(MainActivity.TAG, "-----------------------------------------------------");
    	Log.i(MainActivity.TAG, "ResponseTimestamp: " + siri.getServiceDelivery().getResponseTimestamp());
		
		Log.i(MainActivity.TAG, "------------------------------------------");
    	Log.i(MainActivity.TAG, "-      Vehicle Monitoring Delivery:      -");
    	Log.i(MainActivity.TAG, "------------------------------------------");
    	
		List<VehicleMonitoringDelivery> listVMD = siri.getServiceDelivery().getVehicleMonitoringDelivery();
		
		if(listVMD != null){
			for(VehicleMonitoringDelivery vmd : listVMD){
            	            	
            	List<VehicleActivity> vaList = vmd.getVehicleActivity();
            		            	            	
            	for(VehicleActivity va : vaList){
            		Log.i(MainActivity.TAG, "------------------------");
	            	Log.i(MainActivity.TAG, "-   Vehicle Activity:  -");
	            	Log.i(MainActivity.TAG, "------------------------");
	            	
            		Log.i(MainActivity.TAG, "LineRef: " + va.getMonitoredVehicleJourney().getLineRef());
            		Log.i(MainActivity.TAG, "DirectionRef: " + va.getMonitoredVehicleJourney().getDirectionRef());
            		Log.i(MainActivity.TAG, "FramedVehicleJourneyRef.DataFrameRef: " + va.getMonitoredVehicleJourney().getFramedVehicleJourneyRef().getDataFrameRef());
            		Log.i(MainActivity.TAG, "FramedVehicleJourneyRef.DatedVehicleJourneyRef: " + va.getMonitoredVehicleJourney().getFramedVehicleJourneyRef().getDatedVehicleJourneyRef());            		
            		Log.i(MainActivity.TAG, "JourneyPatternRef: " + va.getMonitoredVehicleJourney().getJourneyPatternRef());
            		Log.i(MainActivity.TAG, "PublishedLineName: " + va.getMonitoredVehicleJourney().getPublishedLineName());
            		Log.i(MainActivity.TAG, "OperatorRef: " + va.getMonitoredVehicleJourney().getOperatorRef());
            		Log.i(MainActivity.TAG, "OriginRef: " + va.getMonitoredVehicleJourney().getOriginRef());
            		Log.i(MainActivity.TAG, "DestinationRef: " + va.getMonitoredVehicleJourney().getDestinationRef());
            		Log.i(MainActivity.TAG, "DestinationName: " + va.getMonitoredVehicleJourney().getDestinationName());
            		
            		Log.i(MainActivity.TAG, "------------------");
                	Log.i(MainActivity.TAG, "- Situation Ref: -");
                	Log.i(MainActivity.TAG, "------------------");
                	List<SituationRef> srList = va.getMonitoredVehicleJourney().getSituationRef();                	
                	for(SituationRef sr : srList){
                		Log.i(MainActivity.TAG, "SituationRef: " + sr.getSituationSimpleRef());
                		Log.i(MainActivity.TAG, "SituationRef.FullRef: " + sr.getSituationFullRef());
                		
                	}
                	Log.i(MainActivity.TAG, "----------------");
                	
                	Log.i(MainActivity.TAG, "Monitored: " + va.getMonitoredVehicleJourney().isMonitored());
                	Log.i(MainActivity.TAG, "VehicleLocation.Longitude: " + va.getMonitoredVehicleJourney().getVehicleLocation().getLongitude());
                	Log.i(MainActivity.TAG, "VehicleLocation.Latitude: " + va.getMonitoredVehicleJourney().getVehicleLocation().getLatitude());
                	Log.i(MainActivity.TAG, "Bearing: " + va.getMonitoredVehicleJourney().getBearing());
                	Log.i(MainActivity.TAG, "ProgressRate: " + va.getMonitoredVehicleJourney().getProgressRate());
                	Log.i(MainActivity.TAG, "ProgressStatus: " + va.getMonitoredVehicleJourney().getProgressStatus());
                	Log.i(MainActivity.TAG, "BlockRef: " + va.getMonitoredVehicleJourney().getBlockRef());
                	Log.i(MainActivity.TAG, "VehicleRef: " + va.getMonitoredVehicleJourney().getVehicleRef());
                	Log.i(MainActivity.TAG, "MonitoredCall.Extensions.Distances.PresentableDistance: " + va.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getPresentableDistance());
                	Log.i(MainActivity.TAG, "MonitoredCall.Extensions.Distances.DistanceFromCall: " + va.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getDistanceFromCall());
                	Log.i(MainActivity.TAG, "MonitoredCall.Extensions.Distances.StopsFromCall: " + va.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getStopsFromCall());
                	Log.i(MainActivity.TAG, "MonitoredCall.Extensions.Distances.CallDistanceAlongRoute: " + va.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getCallDistanceAlongRoute());
                	Log.i(MainActivity.TAG, "MonitoredCall.StopPointRef: " + va.getMonitoredVehicleJourney().getMonitoredCall().getStopPointRef());
                	Log.i(MainActivity.TAG, "MonitoredCall.VisitNumber: " + va.getMonitoredVehicleJourney().getMonitoredCall().getVisitNumber());
                	Log.i(MainActivity.TAG, "MonitoredCall.StopPointName: " + va.getMonitoredVehicleJourney().getMonitoredCall().getStopPointName());
                	
                	Log.i(MainActivity.TAG, "OnwardCalls.AimedArrivalTime: " + va.getMonitoredVehicleJourney().getOnwardCalls().getAimedArrivalTime());
                	Log.i(MainActivity.TAG, "OnwardCalls.AimedDepartureTime: " + va.getMonitoredVehicleJourney().getOnwardCalls().getAimedDepartureTime());
                	Log.i(MainActivity.TAG, "OnwardCalls.AimedHeadwayInterval: " + va.getMonitoredVehicleJourney().getOnwardCalls().getAimedHeadwayInterval());
                	Log.i(MainActivity.TAG, "OnwardCalls.ArrivalBoardingActivity: " + va.getMonitoredVehicleJourney().getOnwardCalls().getArrivalBoardingActivity());
                	Log.i(MainActivity.TAG, "OnwardCalls.ArrivalPlatformName: " + va.getMonitoredVehicleJourney().getOnwardCalls().getArrivalPlatformName());
                	Log.i(MainActivity.TAG, "OnwardCalls.ArrivalStatus: " + va.getMonitoredVehicleJourney().getOnwardCalls().getArrivalStatus());
                	Log.i(MainActivity.TAG, "OnwardCalls.DepartureBoardingActivity: " + va.getMonitoredVehicleJourney().getOnwardCalls().getDepartureBoardingActivity());
                	Log.i(MainActivity.TAG, "OnwardCalls.DeparturePlatformName: " + va.getMonitoredVehicleJourney().getOnwardCalls().getDeparturePlatformName());
                	Log.i(MainActivity.TAG, "OnwardCalls.DepartureStatus: " + va.getMonitoredVehicleJourney().getOnwardCalls().getDepartureStatus());
                	Log.i(MainActivity.TAG, "OnwardCalls.ExpectedArrivalTime: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExpectedArrivalTime());
                	Log.i(MainActivity.TAG, "OnwardCalls.ExpectedDepartureTime: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExpectedDepartureTime());
                	Log.i(MainActivity.TAG, "OnwardCalls.ExpectedHeadwayInterval: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExpectedHeadwayInterval());                	               	
                	
                	if(va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions() != null){
                		Log.i(MainActivity.TAG, "OnwardCalls.Extensions.Distances.PresentableDistance: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getPresentableDistance());
                		Log.i(MainActivity.TAG, "OnwardCalls.Extensions.Distances.DistanceFromCall: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getDistanceFromCall());
                		Log.i(MainActivity.TAG, "OnwardCalls.Extensions.Distances.StopsFromCall: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getStopsFromCall());
                		Log.i(MainActivity.TAG, "OnwardCalls.Extensions.Distances.CallDistanceAlongRoute: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getCallDistanceAlongRoute());
                	}
                	
                	Log.i(MainActivity.TAG, "RecordedAtTime: " + va.getRecordedAtTime());
                	Log.i(MainActivity.TAG, "------------------------");
            	}
            		            	
            	Log.i(MainActivity.TAG, "ResponseTimestamp: " + vmd.getResponseTimestamp());
            	Log.i(MainActivity.TAG, "ValidUntil: " + vmd.getValidUntil());
            }
		}
		
		Log.i(MainActivity.TAG, "------------------------------------------");
		
		Log.i(MainActivity.TAG, "------------------------------------------");
    	Log.i(MainActivity.TAG, "-        Stop Monitoring Delivery:       -");
    	Log.i(MainActivity.TAG, "------------------------------------------");
    	
    	List<StopMonitoringDelivery> listSMD = siri.getServiceDelivery().getStopMonitoringDelivery();
    	
    	if(listSMD != null){
    		
			for(StopMonitoringDelivery smd : listSMD){
            	            	
            	List<MonitoredStopVisit> msvList = smd.getMonitoredStopVisit();
            		            		            	
            	for(MonitoredStopVisit msv : msvList){
            		
            		Log.i(MainActivity.TAG, "----------------------------");
	            	Log.i(MainActivity.TAG, "-   Monitored Stop Visit:  -");
	            	Log.i(MainActivity.TAG, "----------------------------");
            		
            		Log.i(MainActivity.TAG, "LineRef: " + msv.getMonitoredVehicleJourney().getLineRef());
            		Log.i(MainActivity.TAG, "DirectionRef: " + msv.getMonitoredVehicleJourney().getDirectionRef());
            		Log.i(MainActivity.TAG, "FramedVehicleJourneyRef.DataFrameRef: " + msv.getMonitoredVehicleJourney().getFramedVehicleJourneyRef().getDataFrameRef());
            		Log.i(MainActivity.TAG, "FramedVehicleJourneyRef.DatedVehicleJourneyRef: " + msv.getMonitoredVehicleJourney().getFramedVehicleJourneyRef().getDatedVehicleJourneyRef());            		
            		Log.i(MainActivity.TAG, "JourneyPatternRef: " + msv.getMonitoredVehicleJourney().getJourneyPatternRef());
            		Log.i(MainActivity.TAG, "PublishedLineName: " + msv.getMonitoredVehicleJourney().getPublishedLineName());
            		Log.i(MainActivity.TAG, "OperatorRef: " + msv.getMonitoredVehicleJourney().getOperatorRef());
            		Log.i(MainActivity.TAG, "OriginRef: " + msv.getMonitoredVehicleJourney().getOriginRef());
            		Log.i(MainActivity.TAG, "DestinationRef: " + msv.getMonitoredVehicleJourney().getDestinationRef());
            		Log.i(MainActivity.TAG, "DestinationName: " + msv.getMonitoredVehicleJourney().getDestinationName());
            		
            		Log.i(MainActivity.TAG, "------------------");
                	Log.i(MainActivity.TAG, "- Situation Ref: -");
                	Log.i(MainActivity.TAG, "------------------");
                	List<SituationRef> srList = msv.getMonitoredVehicleJourney().getSituationRef();                	
                	for(SituationRef sr : srList){
                		Log.i(MainActivity.TAG, "SituationRef: " + sr.getSituationSimpleRef());
                		Log.i(MainActivity.TAG, "SituationRef.FullRef: " + sr.getSituationFullRef());
                		
                	}
                	Log.i(MainActivity.TAG, "----------------");
                	
                	Log.i(MainActivity.TAG, "Monitored: " + msv.getMonitoredVehicleJourney().isMonitored());
                	Log.i(MainActivity.TAG, "VehicleLocation.Longitude: " + msv.getMonitoredVehicleJourney().getVehicleLocation().getLongitude());
                	Log.i(MainActivity.TAG, "VehicleLocation.Latitude: " + msv.getMonitoredVehicleJourney().getVehicleLocation().getLatitude());
                	Log.i(MainActivity.TAG, "Bearing: " + msv.getMonitoredVehicleJourney().getBearing());
                	Log.i(MainActivity.TAG, "ProgressRate: " + msv.getMonitoredVehicleJourney().getProgressRate());
                	Log.i(MainActivity.TAG, "ProgressStatus: " + msv.getMonitoredVehicleJourney().getProgressStatus());
                	Log.i(MainActivity.TAG, "BlockRef: " + msv.getMonitoredVehicleJourney().getBlockRef());
                	Log.i(MainActivity.TAG, "VehicleRef: " + msv.getMonitoredVehicleJourney().getVehicleRef());
                	Log.i(MainActivity.TAG, "MonitoredCall.Extensions.Distances.PresentableDistance: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getPresentableDistance());
                	Log.i(MainActivity.TAG, "MonitoredCall.Extensions.Distances.DistanceFromCall: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getDistanceFromCall());
                	Log.i(MainActivity.TAG, "MonitoredCall.Extensions.Distances.StopsFromCall: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getStopsFromCall());
                	Log.i(MainActivity.TAG, "MonitoredCall.Extensions.Distances.CallDistanceAlongRoute: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getCallDistanceAlongRoute());
                	Log.i(MainActivity.TAG, "MonitoredCall.StopPointRef: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getStopPointRef());
                	Log.i(MainActivity.TAG, "MonitoredCall.VisitNumber: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getVisitNumber());
                	Log.i(MainActivity.TAG, "MonitoredCall.StopPointName: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getStopPointName());
                	
                	Log.i(MainActivity.TAG, "OnwardCalls.AimedArrivalTime: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getAimedArrivalTime());
                	Log.i(MainActivity.TAG, "OnwardCalls.AimedDepartureTime: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getAimedDepartureTime());
                	Log.i(MainActivity.TAG, "OnwardCalls.AimedHeadwayInterval: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getAimedHeadwayInterval());
                	Log.i(MainActivity.TAG, "OnwardCalls.ArrivalBoardingActivity: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getArrivalBoardingActivity());
                	Log.i(MainActivity.TAG, "OnwardCalls.ArrivalPlatformName: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getArrivalPlatformName());
                	Log.i(MainActivity.TAG, "OnwardCalls.ArrivalStatus: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getArrivalStatus());
                	Log.i(MainActivity.TAG, "OnwardCalls.DepartureBoardingActivity: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getDepartureBoardingActivity());
                	Log.i(MainActivity.TAG, "OnwardCalls.DeparturePlatformName: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getDeparturePlatformName());
                	Log.i(MainActivity.TAG, "OnwardCalls.DepartureStatus: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getDepartureStatus());
                	Log.i(MainActivity.TAG, "OnwardCalls.ExpectedArrivalTime: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExpectedArrivalTime());
                	Log.i(MainActivity.TAG, "OnwardCalls.ExpectedDepartureTime: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExpectedDepartureTime());
                	Log.i(MainActivity.TAG, "OnwardCalls.ExpectedHeadwayInterval: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExpectedHeadwayInterval());                	               	
                	
                	if(msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions() != null){
                		Log.i(MainActivity.TAG, "OnwardCalls.Extensions.Distances.PresentableDistance: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getPresentableDistance());
                		Log.i(MainActivity.TAG, "OnwardCalls.Extensions.Distances.DistanceFromCall: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getDistanceFromCall());
                		Log.i(MainActivity.TAG, "OnwardCalls.Extensions.Distances.StopsFromCall: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getStopsFromCall());
                		Log.i(MainActivity.TAG, "OnwardCalls.Extensions.Distances.CallDistanceAlongRoute: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getCallDistanceAlongRoute());
                	}
                	
                	Log.i(MainActivity.TAG, "RecordedAtTime: " + msv.getRecordedAtTime());
                	Log.i(MainActivity.TAG, "------------------------");
            	}
            	
            	
            	Log.i(MainActivity.TAG, "ResponseTimestamp: " + smd.getResponseTimestamp());
            	Log.i(MainActivity.TAG, "ValidUntil: " + smd.getValidUntil());
            }
    	
    	}
		
    	Log.i(MainActivity.TAG, "------------------------------------------");
    	
		Log.i(MainActivity.TAG, "------------------------------------------");
    	Log.i(MainActivity.TAG, "-    Situation Exchange Delivery:        -");
    	Log.i(MainActivity.TAG, "------------------------------------------");
    	
    	List<SituationExchangeDelivery> sedList = siri.getServiceDelivery().getSituationExchangeDelivery();
    	
    	for(SituationExchangeDelivery sed : sedList){
    		List<PtSituationElement> ptseList = sed.getSituations().getPtSituationElement();
    		
    		Log.i(MainActivity.TAG, "----------------------------");
        	Log.i(MainActivity.TAG, "-     PtSituationElement:  -");
        	Log.i(MainActivity.TAG, "----------------------------");
    		
    		for(PtSituationElement ptse : ptseList){
    			Log.i(MainActivity.TAG, "PtSituationElement.PublicationWindow.StartTime: " + ptse.getPublicationWindow().getStartTime());
    			Log.i(MainActivity.TAG, "PtSituationElement.PublicationWindow.EndTime: " + ptse.getPublicationWindow().getEndTime());
    			Log.i(MainActivity.TAG, "PtSituationElement.Severity: " + ptse.getSeverity());
    			Log.i(MainActivity.TAG, "PtSituationElement.Summary: " + ptse.getSummary()); //TODO - check this output
    			Log.i(MainActivity.TAG, "PtSituationElement.Description: " + ptse.getDescription()); //TODO - check this output
    			       			
    			List<AffectedVehicleJourney> avjList = ptse.getAffects().getVehicleJourneys().getAffectedVehicleJourney();
    			
    			for(AffectedVehicleJourney avj : avjList){
    				
    				Log.i(MainActivity.TAG, "---------------------------");
                	Log.i(MainActivity.TAG, "- AffectedVehicleJounrey: -");
                	Log.i(MainActivity.TAG, "---------------------------");
                	Log.i(MainActivity.TAG, "LineRef: " + avj.getLineRef()); //TODO - check this output
                	Log.i(MainActivity.TAG, "DirectionRef: " + avj.getDirectionRef()); //TODO - check this output        				
    			}
    			
    			Log.i(MainActivity.TAG, "---------------------------");
    			
    			List<PtConsequence> ptConList = ptse.getConsequences().getConsequence();  //TODO - check this output
    			
    			for(PtConsequence ptCon: ptConList){
    				Log.i(MainActivity.TAG, "----------------------");
                	Log.i(MainActivity.TAG, "-    PtConsequences: -");
                	Log.i(MainActivity.TAG, "----------------------");
                	Log.i(MainActivity.TAG, "Condition: " + ptCon.getCondition().toString());
    			}
    			
    			Log.i(MainActivity.TAG, "----------------------");
    			
    			Log.i(MainActivity.TAG, "PtSituationElement.SituationNumber: " + ptse.getSituationNumber()); //TODO - check this output        			
    		}
    		Log.i(MainActivity.TAG, "----------------------------");
    		
    	}
    	Log.i(MainActivity.TAG, "------------------------------------------");
	}

}
