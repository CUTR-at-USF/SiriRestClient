
package uk.org.siri.siri;

import java.util.List;

public class VehicleMonitoringDelivery{
   	private String responseTimestamp;
   	private String validUntil;
   	private List vehicleActivity;

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
 	public List getVehicleActivity(){
		return this.vehicleActivity;
	}
	public void setVehicleActivity(List vehicleActivity){
		this.vehicleActivity = vehicleActivity;
	}
}
