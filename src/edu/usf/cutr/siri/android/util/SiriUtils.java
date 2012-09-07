package edu.usf.cutr.siri.android.util;

/**
 * Java imports
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import edu.usf.cutr.siri.android.client.R;
import edu.usf.cutr.siri.android.client.SiriRestClientActivity;

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
import android.content.res.Resources.NotFoundException;
import android.support.v4.app.Fragment;
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
	public static String getKeyFromResource(Fragment fragment) {
		String strKey = new String("");

		try {
			InputStream in = fragment.getResources().openRawResource(R.raw.devkey);
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			StringBuilder total = new StringBuilder();

			while ((strKey = r.readLine()) != null) {
				total.append(strKey);
			}

			strKey = total.toString();

			strKey.trim(); // Remove any whitespace

		} catch (NotFoundException e) {
			Log.w(SiriRestClientActivity.TAG,
					"Warning - didn't find the developer key file:" + e);
		} catch (IOException e) {
			Log.w(SiriRestClientActivity.TAG,
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
		Log.i(SiriRestClientActivity.TAG, "-----------------------------------------------------");
    	Log.i(SiriRestClientActivity.TAG, "-               Service Delivery:                   -");
    	Log.i(SiriRestClientActivity.TAG, "-----------------------------------------------------");
    	Log.i(SiriRestClientActivity.TAG, "ResponseTimestamp: " + siri.getServiceDelivery().getResponseTimestamp());
		
		Log.i(SiriRestClientActivity.TAG, "------------------------------------------");
    	Log.i(SiriRestClientActivity.TAG, "-      Vehicle Monitoring Delivery:      -");
    	Log.i(SiriRestClientActivity.TAG, "------------------------------------------");
    	
		List<VehicleMonitoringDelivery> listVMD = siri.getServiceDelivery().getVehicleMonitoringDelivery();
		
		if(listVMD != null){
			for(VehicleMonitoringDelivery vmd : listVMD){
            	            	
            	List<VehicleActivity> vaList = vmd.getVehicleActivity();
            		            	            	
            	for(VehicleActivity va : vaList){
            		Log.i(SiriRestClientActivity.TAG, "------------------------");
	            	Log.i(SiriRestClientActivity.TAG, "-   Vehicle Activity:  -");
	            	Log.i(SiriRestClientActivity.TAG, "------------------------");
	            	
            		Log.i(SiriRestClientActivity.TAG, "LineRef: " + va.getMonitoredVehicleJourney().getLineRef());
            		Log.i(SiriRestClientActivity.TAG, "DirectionRef: " + va.getMonitoredVehicleJourney().getDirectionRef());
            		Log.i(SiriRestClientActivity.TAG, "FramedVehicleJourneyRef.DataFrameRef: " + va.getMonitoredVehicleJourney().getFramedVehicleJourneyRef().getDataFrameRef());
            		Log.i(SiriRestClientActivity.TAG, "FramedVehicleJourneyRef.DatedVehicleJourneyRef: " + va.getMonitoredVehicleJourney().getFramedVehicleJourneyRef().getDatedVehicleJourneyRef());            		
            		Log.i(SiriRestClientActivity.TAG, "JourneyPatternRef: " + va.getMonitoredVehicleJourney().getJourneyPatternRef());
            		Log.i(SiriRestClientActivity.TAG, "PublishedLineName: " + va.getMonitoredVehicleJourney().getPublishedLineName());
            		Log.i(SiriRestClientActivity.TAG, "OperatorRef: " + va.getMonitoredVehicleJourney().getOperatorRef());
            		Log.i(SiriRestClientActivity.TAG, "OriginRef: " + va.getMonitoredVehicleJourney().getOriginRef());
            		Log.i(SiriRestClientActivity.TAG, "DestinationRef: " + va.getMonitoredVehicleJourney().getDestinationRef());
            		Log.i(SiriRestClientActivity.TAG, "DestinationName: " + va.getMonitoredVehicleJourney().getDestinationName());
            		
            		Log.i(SiriRestClientActivity.TAG, "------------------");
                	Log.i(SiriRestClientActivity.TAG, "- Situation Ref: -");
                	Log.i(SiriRestClientActivity.TAG, "------------------");
                	List<SituationRef> srList = va.getMonitoredVehicleJourney().getSituationRef();                	
                	for(SituationRef sr : srList){
                		Log.i(SiriRestClientActivity.TAG, "SituationRef: " + sr.getSituationSimpleRef());
                		Log.i(SiriRestClientActivity.TAG, "SituationRef.FullRef: " + sr.getSituationFullRef());
                		
                	}
                	Log.i(SiriRestClientActivity.TAG, "----------------");
                	
                	Log.i(SiriRestClientActivity.TAG, "Monitored: " + va.getMonitoredVehicleJourney().isMonitored());
                	Log.i(SiriRestClientActivity.TAG, "VehicleLocation.Longitude: " + va.getMonitoredVehicleJourney().getVehicleLocation().getLongitude());
                	Log.i(SiriRestClientActivity.TAG, "VehicleLocation.Latitude: " + va.getMonitoredVehicleJourney().getVehicleLocation().getLatitude());
                	Log.i(SiriRestClientActivity.TAG, "Bearing: " + va.getMonitoredVehicleJourney().getBearing());
                	Log.i(SiriRestClientActivity.TAG, "ProgressRate: " + va.getMonitoredVehicleJourney().getProgressRate());
                	Log.i(SiriRestClientActivity.TAG, "ProgressStatus: " + va.getMonitoredVehicleJourney().getProgressStatus());
                	Log.i(SiriRestClientActivity.TAG, "BlockRef: " + va.getMonitoredVehicleJourney().getBlockRef());
                	Log.i(SiriRestClientActivity.TAG, "VehicleRef: " + va.getMonitoredVehicleJourney().getVehicleRef());
                	Log.i(SiriRestClientActivity.TAG, "MonitoredCall.Extensions.Distances.PresentableDistance: " + va.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getPresentableDistance());
                	Log.i(SiriRestClientActivity.TAG, "MonitoredCall.Extensions.Distances.DistanceFromCall: " + va.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getDistanceFromCall());
                	Log.i(SiriRestClientActivity.TAG, "MonitoredCall.Extensions.Distances.StopsFromCall: " + va.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getStopsFromCall());
                	Log.i(SiriRestClientActivity.TAG, "MonitoredCall.Extensions.Distances.CallDistanceAlongRoute: " + va.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getCallDistanceAlongRoute());
                	Log.i(SiriRestClientActivity.TAG, "MonitoredCall.StopPointRef: " + va.getMonitoredVehicleJourney().getMonitoredCall().getStopPointRef());
                	Log.i(SiriRestClientActivity.TAG, "MonitoredCall.VisitNumber: " + va.getMonitoredVehicleJourney().getMonitoredCall().getVisitNumber());
                	Log.i(SiriRestClientActivity.TAG, "MonitoredCall.StopPointName: " + va.getMonitoredVehicleJourney().getMonitoredCall().getStopPointName());
                	
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.AimedArrivalTime: " + va.getMonitoredVehicleJourney().getOnwardCalls().getAimedArrivalTime());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.AimedDepartureTime: " + va.getMonitoredVehicleJourney().getOnwardCalls().getAimedDepartureTime());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.AimedHeadwayInterval: " + va.getMonitoredVehicleJourney().getOnwardCalls().getAimedHeadwayInterval());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.ArrivalBoardingActivity: " + va.getMonitoredVehicleJourney().getOnwardCalls().getArrivalBoardingActivity());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.ArrivalPlatformName: " + va.getMonitoredVehicleJourney().getOnwardCalls().getArrivalPlatformName());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.ArrivalStatus: " + va.getMonitoredVehicleJourney().getOnwardCalls().getArrivalStatus());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.DepartureBoardingActivity: " + va.getMonitoredVehicleJourney().getOnwardCalls().getDepartureBoardingActivity());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.DeparturePlatformName: " + va.getMonitoredVehicleJourney().getOnwardCalls().getDeparturePlatformName());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.DepartureStatus: " + va.getMonitoredVehicleJourney().getOnwardCalls().getDepartureStatus());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.ExpectedArrivalTime: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExpectedArrivalTime());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.ExpectedDepartureTime: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExpectedDepartureTime());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.ExpectedHeadwayInterval: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExpectedHeadwayInterval());                	               	
                	
                	if(va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions() != null){
                		Log.i(SiriRestClientActivity.TAG, "OnwardCalls.Extensions.Distances.PresentableDistance: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getPresentableDistance());
                		Log.i(SiriRestClientActivity.TAG, "OnwardCalls.Extensions.Distances.DistanceFromCall: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getDistanceFromCall());
                		Log.i(SiriRestClientActivity.TAG, "OnwardCalls.Extensions.Distances.StopsFromCall: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getStopsFromCall());
                		Log.i(SiriRestClientActivity.TAG, "OnwardCalls.Extensions.Distances.CallDistanceAlongRoute: " + va.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getCallDistanceAlongRoute());
                	}
                	
                	Log.i(SiriRestClientActivity.TAG, "RecordedAtTime: " + va.getRecordedAtTime());
                	Log.i(SiriRestClientActivity.TAG, "------------------------");
            	}
            		            	
            	Log.i(SiriRestClientActivity.TAG, "ResponseTimestamp: " + vmd.getResponseTimestamp());
            	Log.i(SiriRestClientActivity.TAG, "ValidUntil: " + vmd.getValidUntil());
            }
		}
		
		Log.i(SiriRestClientActivity.TAG, "------------------------------------------");
		
		Log.i(SiriRestClientActivity.TAG, "------------------------------------------");
    	Log.i(SiriRestClientActivity.TAG, "-        Stop Monitoring Delivery:       -");
    	Log.i(SiriRestClientActivity.TAG, "------------------------------------------");
    	
    	List<StopMonitoringDelivery> listSMD = siri.getServiceDelivery().getStopMonitoringDelivery();
    	
    	if(listSMD != null){
    		
			for(StopMonitoringDelivery smd : listSMD){
            	            	
            	List<MonitoredStopVisit> msvList = smd.getMonitoredStopVisit();
            		            		            	
            	for(MonitoredStopVisit msv : msvList){
            		
            		Log.i(SiriRestClientActivity.TAG, "----------------------------");
	            	Log.i(SiriRestClientActivity.TAG, "-   Monitored Stop Visit:  -");
	            	Log.i(SiriRestClientActivity.TAG, "----------------------------");
            		
            		Log.i(SiriRestClientActivity.TAG, "LineRef: " + msv.getMonitoredVehicleJourney().getLineRef());
            		Log.i(SiriRestClientActivity.TAG, "DirectionRef: " + msv.getMonitoredVehicleJourney().getDirectionRef());
            		Log.i(SiriRestClientActivity.TAG, "FramedVehicleJourneyRef.DataFrameRef: " + msv.getMonitoredVehicleJourney().getFramedVehicleJourneyRef().getDataFrameRef());
            		Log.i(SiriRestClientActivity.TAG, "FramedVehicleJourneyRef.DatedVehicleJourneyRef: " + msv.getMonitoredVehicleJourney().getFramedVehicleJourneyRef().getDatedVehicleJourneyRef());            		
            		Log.i(SiriRestClientActivity.TAG, "JourneyPatternRef: " + msv.getMonitoredVehicleJourney().getJourneyPatternRef());
            		Log.i(SiriRestClientActivity.TAG, "PublishedLineName: " + msv.getMonitoredVehicleJourney().getPublishedLineName());
            		Log.i(SiriRestClientActivity.TAG, "OperatorRef: " + msv.getMonitoredVehicleJourney().getOperatorRef());
            		Log.i(SiriRestClientActivity.TAG, "OriginRef: " + msv.getMonitoredVehicleJourney().getOriginRef());
            		Log.i(SiriRestClientActivity.TAG, "DestinationRef: " + msv.getMonitoredVehicleJourney().getDestinationRef());
            		Log.i(SiriRestClientActivity.TAG, "DestinationName: " + msv.getMonitoredVehicleJourney().getDestinationName());
            		
            		Log.i(SiriRestClientActivity.TAG, "------------------");
                	Log.i(SiriRestClientActivity.TAG, "- Situation Ref: -");
                	Log.i(SiriRestClientActivity.TAG, "------------------");
                	List<SituationRef> srList = msv.getMonitoredVehicleJourney().getSituationRef();                	
                	for(SituationRef sr : srList){
                		Log.i(SiriRestClientActivity.TAG, "SituationRef: " + sr.getSituationSimpleRef());
                		Log.i(SiriRestClientActivity.TAG, "SituationRef.FullRef: " + sr.getSituationFullRef());
                		
                	}
                	Log.i(SiriRestClientActivity.TAG, "----------------");
                	
                	Log.i(SiriRestClientActivity.TAG, "Monitored: " + msv.getMonitoredVehicleJourney().isMonitored());
                	Log.i(SiriRestClientActivity.TAG, "VehicleLocation.Longitude: " + msv.getMonitoredVehicleJourney().getVehicleLocation().getLongitude());
                	Log.i(SiriRestClientActivity.TAG, "VehicleLocation.Latitude: " + msv.getMonitoredVehicleJourney().getVehicleLocation().getLatitude());
                	Log.i(SiriRestClientActivity.TAG, "Bearing: " + msv.getMonitoredVehicleJourney().getBearing());
                	Log.i(SiriRestClientActivity.TAG, "ProgressRate: " + msv.getMonitoredVehicleJourney().getProgressRate());
                	Log.i(SiriRestClientActivity.TAG, "ProgressStatus: " + msv.getMonitoredVehicleJourney().getProgressStatus());
                	Log.i(SiriRestClientActivity.TAG, "BlockRef: " + msv.getMonitoredVehicleJourney().getBlockRef());
                	Log.i(SiriRestClientActivity.TAG, "VehicleRef: " + msv.getMonitoredVehicleJourney().getVehicleRef());
                	Log.i(SiriRestClientActivity.TAG, "MonitoredCall.Extensions.Distances.PresentableDistance: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getPresentableDistance());
                	Log.i(SiriRestClientActivity.TAG, "MonitoredCall.Extensions.Distances.DistanceFromCall: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getDistanceFromCall());
                	Log.i(SiriRestClientActivity.TAG, "MonitoredCall.Extensions.Distances.StopsFromCall: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getStopsFromCall());
                	Log.i(SiriRestClientActivity.TAG, "MonitoredCall.Extensions.Distances.CallDistanceAlongRoute: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getExtensions().getDistances().getCallDistanceAlongRoute());
                	Log.i(SiriRestClientActivity.TAG, "MonitoredCall.StopPointRef: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getStopPointRef());
                	Log.i(SiriRestClientActivity.TAG, "MonitoredCall.VisitNumber: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getVisitNumber());
                	Log.i(SiriRestClientActivity.TAG, "MonitoredCall.StopPointName: " + msv.getMonitoredVehicleJourney().getMonitoredCall().getStopPointName());
                	
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.AimedArrivalTime: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getAimedArrivalTime());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.AimedDepartureTime: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getAimedDepartureTime());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.AimedHeadwayInterval: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getAimedHeadwayInterval());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.ArrivalBoardingActivity: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getArrivalBoardingActivity());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.ArrivalPlatformName: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getArrivalPlatformName());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.ArrivalStatus: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getArrivalStatus());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.DepartureBoardingActivity: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getDepartureBoardingActivity());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.DeparturePlatformName: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getDeparturePlatformName());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.DepartureStatus: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getDepartureStatus());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.ExpectedArrivalTime: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExpectedArrivalTime());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.ExpectedDepartureTime: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExpectedDepartureTime());
                	Log.i(SiriRestClientActivity.TAG, "OnwardCalls.ExpectedHeadwayInterval: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExpectedHeadwayInterval());                	               	
                	
                	if(msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions() != null){
                		Log.i(SiriRestClientActivity.TAG, "OnwardCalls.Extensions.Distances.PresentableDistance: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getPresentableDistance());
                		Log.i(SiriRestClientActivity.TAG, "OnwardCalls.Extensions.Distances.DistanceFromCall: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getDistanceFromCall());
                		Log.i(SiriRestClientActivity.TAG, "OnwardCalls.Extensions.Distances.StopsFromCall: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getStopsFromCall());
                		Log.i(SiriRestClientActivity.TAG, "OnwardCalls.Extensions.Distances.CallDistanceAlongRoute: " + msv.getMonitoredVehicleJourney().getOnwardCalls().getExtensions().getDistances().getCallDistanceAlongRoute());
                	}
                	
                	Log.i(SiriRestClientActivity.TAG, "RecordedAtTime: " + msv.getRecordedAtTime());
                	Log.i(SiriRestClientActivity.TAG, "------------------------");
            	}
            	
            	
            	Log.i(SiriRestClientActivity.TAG, "ResponseTimestamp: " + smd.getResponseTimestamp());
            	Log.i(SiriRestClientActivity.TAG, "ValidUntil: " + smd.getValidUntil());
            }
    	
    	}
		
    	Log.i(SiriRestClientActivity.TAG, "------------------------------------------");
    	
		Log.i(SiriRestClientActivity.TAG, "------------------------------------------");
    	Log.i(SiriRestClientActivity.TAG, "-    Situation Exchange Delivery:        -");
    	Log.i(SiriRestClientActivity.TAG, "------------------------------------------");
    	
    	List<SituationExchangeDelivery> sedList = siri.getServiceDelivery().getSituationExchangeDelivery();
    	
    	for(SituationExchangeDelivery sed : sedList){
    		List<PtSituationElement> ptseList = sed.getSituations().getPtSituationElement();
    		
    		Log.i(SiriRestClientActivity.TAG, "----------------------------");
        	Log.i(SiriRestClientActivity.TAG, "-     PtSituationElement:  -");
        	Log.i(SiriRestClientActivity.TAG, "----------------------------");
    		
    		for(PtSituationElement ptse : ptseList){
    			Log.i(SiriRestClientActivity.TAG, "PtSituationElement.PublicationWindow.StartTime: " + ptse.getPublicationWindow().getStartTime());
    			Log.i(SiriRestClientActivity.TAG, "PtSituationElement.PublicationWindow.EndTime: " + ptse.getPublicationWindow().getEndTime());
    			Log.i(SiriRestClientActivity.TAG, "PtSituationElement.Severity: " + ptse.getSeverity());
    			Log.i(SiriRestClientActivity.TAG, "PtSituationElement.Summary: " + ptse.getSummary()); //TODO - check this output
    			Log.i(SiriRestClientActivity.TAG, "PtSituationElement.Description: " + ptse.getDescription()); //TODO - check this output
    			       			
    			List<AffectedVehicleJourney> avjList = ptse.getAffects().getVehicleJourneys().getAffectedVehicleJourney();
    			
    			for(AffectedVehicleJourney avj : avjList){
    				
    				Log.i(SiriRestClientActivity.TAG, "---------------------------");
                	Log.i(SiriRestClientActivity.TAG, "- AffectedVehicleJounrey: -");
                	Log.i(SiriRestClientActivity.TAG, "---------------------------");
                	Log.i(SiriRestClientActivity.TAG, "LineRef: " + avj.getLineRef()); //TODO - check this output
                	Log.i(SiriRestClientActivity.TAG, "DirectionRef: " + avj.getDirectionRef()); //TODO - check this output        				
    			}
    			
    			Log.i(SiriRestClientActivity.TAG, "---------------------------");
    			
    			List<PtConsequence> ptConList = ptse.getConsequences().getConsequence();  //TODO - check this output
    			
    			for(PtConsequence ptCon: ptConList){
    				Log.i(SiriRestClientActivity.TAG, "----------------------");
                	Log.i(SiriRestClientActivity.TAG, "-    PtConsequences: -");
                	Log.i(SiriRestClientActivity.TAG, "----------------------");
                	Log.i(SiriRestClientActivity.TAG, "Condition: " + ptCon.getCondition().toString());
    			}
    			
    			Log.i(SiriRestClientActivity.TAG, "----------------------");
    			
    			Log.i(SiriRestClientActivity.TAG, "PtSituationElement.SituationNumber: " + ptse.getSituationNumber()); //TODO - check this output        			
    		}
    		Log.i(SiriRestClientActivity.TAG, "----------------------------");
    		
    	}
    	Log.i(SiriRestClientActivity.TAG, "------------------------------------------");
	}

}
