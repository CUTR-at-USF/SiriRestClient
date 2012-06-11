
package uk.org.siri.siri;

import java.util.List;

public class StopMonitoringDelivery{
   	private List monitoredStopVisit;
   	private String responseTimestamp;
   	private String validUntil;

 	public List getMonitoredStopVisit(){
		return this.monitoredStopVisit;
	}
	public void setMonitoredStopVisit(List monitoredStopVisit){
		this.monitoredStopVisit = monitoredStopVisit;
	}
 	public String getResponseTimestamp(){
		return this.responseTimestamp;
	}
	public void setResponseTimestamp(String responseTimestamp){
		this.responseTimestamp = responseTimestamp;
	}
 	public String getValidUntil(){
		return this.validUntil;
	}
	public void setValidUntil(String validUntil){
		this.validUntil = validUntil;
	}
}
