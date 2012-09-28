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

import static edu.usf.cutr.siri.android.client.SiriRestClient.TAG;

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
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.util.Log;

/**
 * This class holds utility methods for the library
 * 
 * @author Sean J. Barbeau
 * 
 */
public class SiriUtils {
	
	/**
	 * Prints the contents of a Siri object
	 * 
	 * @param siri response from Mobile SIRI API
	 */
	public static void printContents(Siri siri){
		Log.i(TAG, "-----------------------------------------------------");
    	Log.i(TAG, "-               Service Delivery:                   -");
    	Log.i(TAG, "-----------------------------------------------------");
    	Log.i(TAG, "ResponseTimestamp: " + siri.getServiceDelivery().getResponseTimestamp());
		
		Log.i(TAG, "------------------------------------------");
    	Log.i(TAG, "-      Vehicle Monitoring Delivery:      -");
    	Log.i(TAG, "------------------------------------------");
    	
		List<VehicleMonitoringDelivery> listVMD = siri.getServiceDelivery().getVehicleMonitoringDelivery();
		
		if(listVMD != null){
			for(VehicleMonitoringDelivery vmd : listVMD){
            	            	
            	List<VehicleActivity> vaList = vmd.getVehicleActivity();
            		            	            
            	if(vaList != null){
	            	for(VehicleActivity va : vaList){
	            		Log.i(TAG, "------------------------");
		            	Log.i(TAG, "-   Vehicle Activity:  -");
		            	Log.i(TAG, "------------------------");
		            	
	            		Log.i(TAG, "LineRef: " + va.getMonitoredVehicleJourney().getLineRef());
	            		Log.i(TAG, "DirectionRef: " + va.getMonitoredVehicleJourney().getDirectionRef());
	            		Log.i(TAG, "FramedVehicleJourneyRef.DataFrameRef: " + va.getMonitoredVehicleJourney().getFramedVehicleJourneyRef().getDataFrameRef());
	            		Log.i(TAG, "FramedVehicleJourneyRef.DatedVehicleJourneyRef: " + va.getMonitoredVehicleJourney().getFramedVehicleJourneyRef().getDatedVehicleJourneyRef());            		
	            		Log.i(TAG, "JourneyPatternRef: " + va.getMonitoredVehicleJourney().getJourneyPatternRef());
	            		Log.i(TAG, "PublishedLineName: " + va.getMonitoredVehicleJourney().getPublishedLineName());
	            		Log.i(TAG, "OperatorRef: " + va.getMonitoredVehicleJourney().getOperatorRef());
	            		Log.i(TAG, "OriginRef: " + va.getMonitoredVehicleJourney().getOriginRef());
	            		Log.i(TAG, "DestinationRef: " + va.getMonitoredVehicleJourney().getDestinationRef());
	            		Log.i(TAG, "DestinationName: " + va.getMonitoredVehicleJourney().getDestinationName());
	            		
	            		Log.i(TAG, "------------------");
	                	Log.i(TAG, "- Situation Ref: -");
	                	Log.i(TAG, "------------------");
	                	List<SituationRef> srList = va.getMonitoredVehicleJourney().getSituationRef();
	                	if(srList != null){
		                	for(SituationRef sr : srList){
		                		Log.i(TAG, "SituationRef: " + sr.getSituationSimpleRef());
		                		Log.i(TAG, "SituationRef.FullRef: " + sr.getSituationFullRef());
		                		
		                	}
	                	}
	                	Log.i(TAG, "----------------");
	                	
	                	Log.i(TAG, "Monitored: " + va.getMonitoredVehicleJourney().isMonitored());
	                	Log.i(TAG, "VehicleLocation.Longitude: " + va.getMonitoredVehicleJourney().getVehicleLocation().getLongitude());
	                	Log.i(TAG, "VehicleLocation.Latitude: " + va.getMonitoredVehicleJourney().getVehicleLocation().getLatitude());
	                	Log.i(TAG, "Bearing: " + va.getMonitoredVehicleJourney().getBearing());
	                	Log.i(TAG, "ProgressRate: " + va.getMonitoredVehicleJourney().getProgressRate());
	                	Log.i(TAG, "ProgressStatus: " + va.getMonitoredVehicleJourney().getProgressStatus());
	                	Log.i(TAG, "BlockRef: " + va.getMonitoredVehicleJourney().getBlockRef());
	                	Log.i(TAG, "VehicleRef: " + va.getMonitoredVehicleJourney().getVehicleRef());
	                	if(va.getMonitoredVehicleJourney().getMonitoredCall() != null){
		                	Log.i(TAG, "MonitoredCall.Extensions.Distances.PresentableDistance: " + va.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getPresentableDistance());
		                	Log.i(TAG, "MonitoredCall.Extensions.Distances.DistanceFromCall: " + va.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getDistanceFromCall());
		                	Log.i(TAG, "MonitoredCall.Extensions.Distances.StopsFromCall: " + va.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getStopsFromCall());
		                	Log.i(TAG, "MonitoredCall.Extensions.Distances.CallDistanceAlongRoute: " + va.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getCallDistanceAlongRoute());
		                	Log.i(TAG, "MonitoredCall.StopPointRef: " + va.getMonitoredVehicleJourney().getMonitoredCall().getStopPointRef());
		                	Log.i(TAG, "MonitoredCall.VisitNumber: " + va.getMonitoredVehicleJourney().getMonitoredCall().getVisitNumber());
		                	Log.i(TAG, "MonitoredCall.StopPointName: " + va.getMonitoredVehicleJourney().getMonitoredCall().getStopPointName());
	                	}
	                	if(va.getMonitoredVehicleJourney().getOnwardCalls() != null){
		                	Log.i(TAG, "OnwardCalls.AimedArrivalTime: " + va.getMonitoredVehicleJourney().getOnwardCalls().getAimedArrivalTime());
		                	Log.i(TAG, "OnwardCalls.AimedDepartureTime: " + va.getMonitoredVehicleJourney().getOnwardCalls().getAimedDepartureTime());
		                	Log.i(TAG, "OnwardCalls.AimedHeadwayInterval: " + va.getMonitoredVehicleJourney().getOnwardCalls().getAimedHeadwayInterval());
		                	Log.i(TAG, "OnwardCalls.ArrivalBoardingActivity: " + va.getMonitoredVehicleJourney().getOnwardCalls().getArrivalBoardingActivity());
		                	Log.i(TAG, "OnwardCalls.ArrivalPlatformName: " + va.getMonitoredVehicleJourney().getOnwardCalls().getArrivalPlatformName());
		                	Log.i(TAG, "OnwardCalls.ArrivalStatus: " + va.getMonitoredVehicleJourney().getOnwardCalls().getArrivalStatus());
		                	Log.i(TAG, "OnwardCalls.DepartureBoardingActivity: " + va.getMonitoredVehicleJourney().getOnwardCalls().getDepartureBoardingActivity());
		                	Log.i(TAG, "OnwardCalls.DeparturePlatformName: " + va.getMonitoredVehicleJourney().getOnwardCalls().getDeparturePlatformName());
		                	Log.i(TAG, "OnwardCalls.DepartureStatus: " + va.getMonitoredVehicleJourney().getOnwardCalls().getDepartureStatus());
		                	Log.i(TAG, "OnwardCalls.ExpectedArrivalTime: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExpectedArrivalTime());
		                	Log.i(TAG, "OnwardCalls.ExpectedDepartureTime: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExpectedDepartureTime());
		                	Log.i(TAG, "OnwardCalls.ExpectedHeadwayInterval: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExpectedHeadwayInterval());                	               	
		                	
		                	if(va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions() != null){
		                		Log.i(TAG, "OnwardCalls.Extensions.Distances.PresentableDistance: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getPresentableDistance());
		                		Log.i(TAG, "OnwardCalls.Extensions.Distances.DistanceFromCall: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getDistanceFromCall());
		                		Log.i(TAG, "OnwardCalls.Extensions.Distances.StopsFromCall: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getStopsFromCall());
		                		Log.i(TAG, "OnwardCalls.Extensions.Distances.CallDistanceAlongRoute: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getCallDistanceAlongRoute());
		                	}
	                	}
	                	
	                	Log.i(TAG, "RecordedAtTime: " + va.getRecordedAtTime());
	                	Log.i(TAG, "------------------------");
	            	}
            	}
            		            	
            	Log.i(TAG, "ResponseTimestamp: " + vmd.getResponseTimestamp());
            	Log.i(TAG, "ValidUntil: " + vmd.getValidUntil());
            }
		}
		
		Log.i(TAG, "------------------------------------------");
		
		Log.i(TAG, "------------------------------------------");
    	Log.i(TAG, "-        Stop Monitoring Delivery:       -");
    	Log.i(TAG, "------------------------------------------");
    	
    	List<StopMonitoringDelivery> listSMD = siri.getServiceDelivery().getStopMonitoringDelivery();
    	
    	if(listSMD != null){
    		
			for(StopMonitoringDelivery smd : listSMD){
            	            	
            	List<MonitoredStopVisit> msvList = smd.getMonitoredStopVisit();
            	
            	if(msvList != null){
            		            		            	
	            	for(MonitoredStopVisit msv : msvList){
	            		
	            		Log.i(TAG, "----------------------------");
		            	Log.i(TAG, "-   Monitored Stop Visit:  -");
		            	Log.i(TAG, "----------------------------");
	            		
	            		Log.i(TAG, "LineRef: " + msv.getMonitoredVehicleJourney().getLineRef());
	            		Log.i(TAG, "DirectionRef: " + msv.getMonitoredVehicleJourney().getDirectionRef());
	            		Log.i(TAG, "FramedVehicleJourneyRef.DataFrameRef: " + msv.getMonitoredVehicleJourney().getFramedVehicleJourneyRef().getDataFrameRef());
	            		Log.i(TAG, "FramedVehicleJourneyRef.DatedVehicleJourneyRef: " + msv.getMonitoredVehicleJourney().getFramedVehicleJourneyRef().getDatedVehicleJourneyRef());            		
	            		Log.i(TAG, "JourneyPatternRef: " + msv.getMonitoredVehicleJourney().getJourneyPatternRef());
	            		Log.i(TAG, "PublishedLineName: " + msv.getMonitoredVehicleJourney().getPublishedLineName());
	            		Log.i(TAG, "OperatorRef: " + msv.getMonitoredVehicleJourney().getOperatorRef());
	            		Log.i(TAG, "OriginRef: " + msv.getMonitoredVehicleJourney().getOriginRef());
	            		Log.i(TAG, "DestinationRef: " + msv.getMonitoredVehicleJourney().getDestinationRef());
	            		Log.i(TAG, "DestinationName: " + msv.getMonitoredVehicleJourney().getDestinationName());
	            		
	            		Log.i(TAG, "------------------");
	                	Log.i(TAG, "- Situation Ref: -");
	                	Log.i(TAG, "------------------");
	                	List<SituationRef> srList = msv.getMonitoredVehicleJourney().getSituationRef();
	                	if(srList != null){
		                	for(SituationRef sr : srList){
		                		Log.i(TAG, "SituationRef: " + sr.getSituationSimpleRef());
		                		Log.i(TAG, "SituationRef.FullRef: " + sr.getSituationFullRef());
		                		
		                	}
	                	}
	                	Log.i(TAG, "----------------");
	                	
	                	Log.i(TAG, "Monitored: " + msv.getMonitoredVehicleJourney().isMonitored());
	                	Log.i(TAG, "VehicleLocation.Longitude: " + msv.getMonitoredVehicleJourney().getVehicleLocation().getLongitude());
	                	Log.i(TAG, "VehicleLocation.Latitude: " + msv.getMonitoredVehicleJourney().getVehicleLocation().getLatitude());
	                	Log.i(TAG, "Bearing: " + msv.getMonitoredVehicleJourney().getBearing());
	                	Log.i(TAG, "ProgressRate: " + msv.getMonitoredVehicleJourney().getProgressRate());
	                	Log.i(TAG, "ProgressStatus: " + msv.getMonitoredVehicleJourney().getProgressStatus());
	                	Log.i(TAG, "BlockRef: " + msv.getMonitoredVehicleJourney().getBlockRef());
	                	Log.i(TAG, "VehicleRef: " + msv.getMonitoredVehicleJourney().getVehicleRef());
	                	
	                	if(msv.getMonitoredVehicleJourney().getMonitoredCall() != null){
		                	Log.i(TAG, "MonitoredCall.Extensions.Distances.PresentableDistance: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getPresentableDistance());
		                	Log.i(TAG, "MonitoredCall.Extensions.Distances.DistanceFromCall: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getDistanceFromCall());
		                	Log.i(TAG, "MonitoredCall.Extensions.Distances.StopsFromCall: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getStopsFromCall());
		                	Log.i(TAG, "MonitoredCall.Extensions.Distances.CallDistanceAlongRoute: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getCallDistanceAlongRoute());
		                	Log.i(TAG, "MonitoredCall.StopPointRef: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getStopPointRef());
		                	Log.i(TAG, "MonitoredCall.VisitNumber: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getVisitNumber());
		                	Log.i(TAG, "MonitoredCall.StopPointName: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getStopPointName());
	                	}
	                	
	                	if(msv.getMonitoredVehicleJourney().getOnwardCalls() != null){
		                	Log.i(TAG, "OnwardCalls.AimedArrivalTime: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getAimedArrivalTime());
		                	Log.i(TAG, "OnwardCalls.AimedDepartureTime: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getAimedDepartureTime());
		                	Log.i(TAG, "OnwardCalls.AimedHeadwayInterval: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getAimedHeadwayInterval());
		                	Log.i(TAG, "OnwardCalls.ArrivalBoardingActivity: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getArrivalBoardingActivity());
		                	Log.i(TAG, "OnwardCalls.ArrivalPlatformName: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getArrivalPlatformName());
		                	Log.i(TAG, "OnwardCalls.ArrivalStatus: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getArrivalStatus());
		                	Log.i(TAG, "OnwardCalls.DepartureBoardingActivity: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getDepartureBoardingActivity());
		                	Log.i(TAG, "OnwardCalls.DeparturePlatformName: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getDeparturePlatformName());
		                	Log.i(TAG, "OnwardCalls.DepartureStatus: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getDepartureStatus());
		                	Log.i(TAG, "OnwardCalls.ExpectedArrivalTime: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExpectedArrivalTime());
		                	Log.i(TAG, "OnwardCalls.ExpectedDepartureTime: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExpectedDepartureTime());
		                	Log.i(TAG, "OnwardCalls.ExpectedHeadwayInterval: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExpectedHeadwayInterval());                	               	
		                	
		                	if(msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions() != null){
		                		Log.i(TAG, "OnwardCalls.Extensions.Distances.PresentableDistance: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getPresentableDistance());
		                		Log.i(TAG, "OnwardCalls.Extensions.Distances.DistanceFromCall: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getDistanceFromCall());
		                		Log.i(TAG, "OnwardCalls.Extensions.Distances.StopsFromCall: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getStopsFromCall());
		                		Log.i(TAG, "OnwardCalls.Extensions.Distances.CallDistanceAlongRoute: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getCallDistanceAlongRoute());
		                	}
	                	}
	                	
	                	Log.i(TAG, "RecordedAtTime: " + msv.getRecordedAtTime());
	                	Log.i(TAG, "------------------------");
	            	}
			}
            	
            	
            	Log.i(TAG, "ResponseTimestamp: " + smd.getResponseTimestamp());
            	Log.i(TAG, "ValidUntil: " + smd.getValidUntil());
            }
    	
    	}
		
    	Log.i(TAG, "------------------------------------------");
    	
		Log.i(TAG, "------------------------------------------");
    	Log.i(TAG, "-    Situation Exchange Delivery:        -");
    	Log.i(TAG, "------------------------------------------");
    	
    	List<SituationExchangeDelivery> sedList = siri.getServiceDelivery().getSituationExchangeDelivery();
    	
    	if(sedList != null){
	    	for(SituationExchangeDelivery sed : sedList){
	    		List<PtSituationElement> ptseList = sed.getSituations().getPtSituationElement();
	    		
	    		Log.i(TAG, "----------------------------");
	        	Log.i(TAG, "-     PtSituationElement:  -");
	        	Log.i(TAG, "----------------------------");
	    		
	    		for(PtSituationElement ptse : ptseList){
	    			Log.i(TAG, "PtSituationElement.PublicationWindow.StartTime: " + ptse.getPublicationWindow().getStartTime());
	    			Log.i(TAG, "PtSituationElement.PublicationWindow.EndTime: " + ptse.getPublicationWindow().getEndTime());
	    			Log.i(TAG, "PtSituationElement.Severity: " + ptse.getSeverity());
	    			Log.i(TAG, "PtSituationElement.Summary: " + ptse.getSummary()); //TODO - check this output
	    			Log.i(TAG, "PtSituationElement.Description: " + ptse.getDescription()); //TODO - check this output
	    			       			
	    			List<AffectedVehicleJourney> avjList = ptse.getAffects().getVehicleJourneys().getAffectedVehicleJourney();
	    			
	    			for(AffectedVehicleJourney avj : avjList){
	    				
	    				Log.i(TAG, "---------------------------");
	                	Log.i(TAG, "- AffectedVehicleJounrey: -");
	                	Log.i(TAG, "---------------------------");
	                	Log.i(TAG, "LineRef: " + avj.getLineRef()); //TODO - check this output
	                	Log.i(TAG, "DirectionRef: " + avj.getDirectionRef()); //TODO - check this output        				
	    			}
	    			
	    			Log.i(TAG, "---------------------------");
	    			
	    			List<PtConsequence> ptConList = ptse.getConsequences().getConsequence();  //TODO - check this output
	    			
	    			for(PtConsequence ptCon: ptConList){
	    				Log.i(TAG, "----------------------");
	                	Log.i(TAG, "-    PtConsequences: -");
	                	Log.i(TAG, "----------------------");
	                	Log.i(TAG, "Condition: " + ptCon.getCondition().toString());
	    			}
	    			
	    			Log.i(TAG, "----------------------");
	    			
	    			Log.i(TAG, "PtSituationElement.SituationNumber: " + ptse.getSituationNumber()); //TODO - check this output        			
	    		}
	    		Log.i(TAG, "----------------------------");	    		
	    	}
    	}
    	Log.i(TAG, "------------------------------------------");
	}

}
