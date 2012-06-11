package uk.org.siri.siri;

import java.util.List;

public class ServiceDelivery {
  private String responseTimestamp;
  private List vehicleMonitoringDelivery;
  private List stopMonitoringDelivery;

  public String getResponseTimestamp() {
    return this.responseTimestamp;
  }

  public void setResponseTimestamp(String responseTimestamp) {
    this.responseTimestamp = responseTimestamp;
  }

  public List getVehicleMonitoringDelivery() {
    return this.vehicleMonitoringDelivery;
  }

  public void setVehicleMonitoringDelivery(List vehicleMonitoringDelivery) {
    this.vehicleMonitoringDelivery = vehicleMonitoringDelivery;
  }

  public List getStopMonitoringDelivery() {
    return this.stopMonitoringDelivery;
  }

  public void setStopMonitoringDelivery(List stopMonitoringDelivery) {
    this.stopMonitoringDelivery = stopMonitoringDelivery;
  }
}
