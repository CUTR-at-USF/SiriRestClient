package edu.usf.cutr.siri.android.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Build;
import android.util.Log;
import uk.org.siri.siri.Siri;
import edu.usf.cutr.siri.android.ui.MainActivity;
import edu.usf.cutr.siri.android.util.SiriJacksonConfig;

/**
 * This class is used to make a request to a RESTful SIRI API server, parse the
 * response, and return a Siri data object to the caller.
 * 
 * @author Sean J. Barbeau
 * 
 */
public class SiriRestClient {

	//Config settings for server
	SiriRestClientConfig config;

	// Base URL for vehicle monitoring requests
	String vehMonBaseUrl;

	// Base URL for stop monitoring requests
	String stopMonBaseUrl;
	
	//Used to time response and parsing
	long startTime = 0;
	long endTime = 0;

	/**
	 * Creates a new SiriRestClient object that can make vehicle monitoring and
	 * stop monitoring REST requests, parse responses, and return an
	 * instantiated Siri object
	 * 
	 * @param vehMonBaseUrl
	 *            the entire URL up to the file extension for vehicle monitoring
	 *            (e.g., http://bustime.mta.info/api/siri/vehicle-monitoring),
	 *            or empty string if vehicle monitoring is not supported
	 * @param stopMonBaseUrl
	 *            the entire URL up to the file extension for stop monitoring
	 *            (e.g., http://bustime.mta.info/api/siri/stop-monitoring), or
	 *            empty string if stop monitoring is not supported
	 * @param config
	 *            configuration for requests to be made to the server
	 */
	public SiriRestClient(String vehMonBaseUrl, String stopMonBaseUrl,
			SiriRestClientConfig config) {
		this.vehMonBaseUrl = vehMonBaseUrl;
		this.stopMonBaseUrl = stopMonBaseUrl;
		this.config = config;
	}

	/**
	 * Sets the configuration for requests to be made to the server
	 * 
	 * @param config
	 *            the configuration for requests to be made to the server
	 */
	public void setConfig(SiriRestClientConfig config) {
		this.config = config;
	}

	/**
	 * Gets the current configuration for requests to be made to the server
	 * 
	 * @return the current configuration for requests to be made to the server
	 */
	public SiriRestClientConfig getConfig() {
		return config;
	}
	
	/**
	 * Returns a benchmark of the amount of time the last request/response/parsing took (in nanoseconds)
	 *  
	 * @return a benchmark of the amount of time the last request/response/parsing took (in nanoseconds)
	 */
	public long getLastRequestTime(){
		return endTime - startTime;
	}

	/**
	 * Makes the HTTP request to the SIRI VehicleMonitoring REST API on the
	 * server, parses the response, and returns a Siri object containing the
	 * response
	 * 
	 * For options fields in request (i.e., DirectionRef,
	 * StopMonitoringDetailLevel, MaximumNumberOfCallsOnwards), pass in an empty
	 * string if they aren't being used.
	 * 
	 * @param devKey
	 *            a developer API key
	 * @param operatorRef
	 *            the GTFS agency ID to be monitored
	 * @param vehicleRef
	 *            the ID of the vehicle to be monitored (optional). This is the
	 *            4-digit number painted on the side of the bus, for example
	 *            7560. Response will include all buses if not included.
	 * @param lineRef
	 *            a filter by GTFS route ID (optional).
	 * @param directionRef
	 *            a filter by GTFS direction ID (optional). Either 0 or 1, or -1
	 *            if not to be used.
	 * @param vehicleMonitoringDetailLevel
	 *            Determines whether or not the response will include the stops
	 *            ("calls" in SIRI-speak) each vehicle is going to make
	 *            (optional). To get calls data, use value calls, otherwise use
	 *            value normal (default is normal).
	 * @param maximumNumberOfCallsOnwards
	 *            Limit on the number of OnwardCall elements for each vehicle
	 *            when VehicleMonitoringDetailLevel=calls, or -1 not to limit
	 * @return a Siri object containing the parsed VehicleMonRequest response
	 *         from the server
	 * @throws IllegalArgumentException
	 *             if the required parameters aren't provided or any parameter
	 *             is invalid
	 */
	public Siri makeVehicleMonRequest(String devKey, String operatorRef,
			String vehicleRef, String lineRef, int directionRef,
			String vehicleMonitoringDetailLevel, int maximumNumberOfCallsOnwards) throws IllegalArgumentException {

		StringBuffer sb = new StringBuffer();

		sb.append(vehMonBaseUrl); // Base URL for stop mon request

		sb.append("." + getResponseTypeFileExtension()); // .json or .xml

		sb.append("?");

		if (!devKey.equals("")) {
			sb.append("key=" + devKey + "&");
		}

		if (!operatorRef.equals("")) {
			sb.append("OperatorRef=" + operatorRef + "&");
		} else {
			throw new IllegalArgumentException(
					"OperatorRef is a required paramater and cannot be an emptry string");
		}

		if (!vehicleRef.equals("")) {
			sb.append("VehicleRef=" + vehicleRef + "&");
		}

		if (!lineRef.equals("")) {
			sb.append("LineRef=" + lineRef + "&");
		}

		if (directionRef != -1) {
			if (directionRef == 0 || directionRef == 1) {
				sb.append("DirectionRef=" + directionRef + "&");
			} else {
				throw new IllegalArgumentException(
						"DirectionRef must be 0 or 1, or -1 if not to be used");
			}
		}

		if (!vehicleMonitoringDetailLevel.equals("")) {
			if (vehicleMonitoringDetailLevel.equalsIgnoreCase("calls")
					|| vehicleMonitoringDetailLevel.equalsIgnoreCase("normal")) {
				sb.append("VehicleMonitoringDetailLevel="
						+ vehicleMonitoringDetailLevel + "&");
			} else {
				throw new IllegalArgumentException(
						"VehicleMonitoringDetailLevel  must be 'calls' or 'normal'");
			}
		} else {
			// Set default
			sb.append("VehicleMonitoringDetailLevel=normal");
		}

		if (maximumNumberOfCallsOnwards != -1) {
			if (maximumNumberOfCallsOnwards >= 1) {
				sb.append("MaximumNumberOfCallsOnwards ="
						+ maximumNumberOfCallsOnwards + "&");
			} else {
				throw new IllegalArgumentException(
						"MaximumNumberOfCallsOnwards must be 1 or greater, or -1 if not to be used");
			}
		}

		// Make actual HTTP call to server using parameters string we just
		// built, pre-fixed with the base URL and correct response type
		// extension
		return makeRequest(sb.toString().replace(" ", "%20")); // Handle spaces

	}

	/**
	 * Makes the HTTP request to the SIRI Stop Monitoring REST API on the
	 * server, parses the response, and returns a Siri object containing the
	 * response.
	 * 
	 * For options fields in request (i.e., DirectionRef,
	 * StopMonitoringDetailLevel, MaximumNumberOfCallsOnwards), pass in an empty
	 * string if they aren't being used.
	 * 
	 * @param devKey
	 *            a developer API key (optional in some SIRI implementations)
	 * @param operatorRef
	 *            the GTFS agency ID to be monitored (required)
	 * @param monitoringRef
	 *            the GTFS stop ID of the stop to be monitored (required). For
	 *            example, 308214 for the stop at 5th Avenue and Union St
	 *            towards Bay Ridge.
	 * @param lineRef
	 *            a filter by GTFS route ID (optional)
	 * @param directionRef
	 *            a filter by GTFS direction ID (optional). Either 0 or 1, or -1
	 *            if not to be used.
	 * @param stopMonitoringDetailLevel
	 *            Determines whether or not the response will include the stops
	 *            ("calls" in SIRI-speak) each vehicle is going to make after it
	 *            serves the selected stop (optional). To get calls data, use
	 *            value calls, otherwise use value normal (default is normal).
	 * @param maximumNumberOfCallsOnwards
	 *            Limit on the number of OnwardCall elements for each vehicle
	 *            when CtopMonitoringDetailLevel=calls, or -1 not to limit
	 * @return a Siri object containing the parsed StopMonRequest response from
	 *         the server
	 * @throws IllegalArgumentException
	 *             if the required parameters aren't provided or any parameter
	 *             is invalid
	 */
	public Siri makeStopMonRequest(String devKey, String operatorRef,
			String monitoringRef, String lineRef, int directionRef,
			String stopMonitoringDetailLevel, int maximumNumberOfCallsOnwards)
			throws IllegalArgumentException {

		StringBuffer sb = new StringBuffer();

		sb.append(stopMonBaseUrl); // Base URL for stop mon request

		sb.append("." + getResponseTypeFileExtension()); // .json or .xml

		sb.append("?"); // parameters

		if (!devKey.equals("")) {
			sb.append("key=" + devKey + "&");
		}

		if (!operatorRef.equals("")) {
			sb.append("OperatorRef=" + operatorRef + "&");
		} else {
			throw new IllegalArgumentException(
					"OperatorRef is a required paramater and cannot be an emptry string");
		}

		if (!monitoringRef.equals("")) {
			sb.append("MonitoringRef=" + monitoringRef + "&");
		} else {
			throw new IllegalArgumentException(
					"MonitoringRef is a required paramater and cannot be an emptry string");
		}

		if (!lineRef.equals("")) {
			sb.append("LineRef=" + lineRef + "&");
		}

		if (directionRef != -1) {
			if (directionRef == 0 || directionRef == 1) {
				sb.append("DirectionRef=" + directionRef + "&");
			} else {
				throw new IllegalArgumentException(
						"DirectionRef must be 0 or 1, or -1 if not to be used");
			}
		}

		if (!stopMonitoringDetailLevel.equals("")) {
			if (stopMonitoringDetailLevel.equalsIgnoreCase("calls")
					|| stopMonitoringDetailLevel.equalsIgnoreCase("normal")) {
				sb.append("StopMonitoringDetailLevel="
						+ stopMonitoringDetailLevel + "&");
			} else {
				throw new IllegalArgumentException(
						"StopMonitoringDetailLevel must be 'calls' or 'normal'");
			}
		} else {
			// Set default
			sb.append("StopMonitoringDetailLevel=normal");
		}

		if (maximumNumberOfCallsOnwards != -1) {
			if (maximumNumberOfCallsOnwards >= 1) {
				sb.append("MaximumNumberOfCallsOnwards="
						+ maximumNumberOfCallsOnwards + "&");
			} else {
				throw new IllegalArgumentException(
						"MaximumNumberOfCallsOnwards must be 1 or greater, or -1 if not to be used");
			}
		}

		// Make actual HTTP call to server using parameters string we just
		// built, pre-fixed with the base URL and correct response type
		// extension
		return makeRequest(sb.toString().replace(" ", "%20")); // Handle spaces
	}

	/**
	 * Utility method that returns the file extension (e.g., JSON or XML) based
	 * on response type
	 * 
	 * @return "json" if request is JSON, or "xml" if request type is XML
	 */
	private String getResponseTypeFileExtension() {
		switch (config.getResponseType()) {
		case SiriRestClientConfig.RESPONSE_TYPE_JSON:
			return "json";
		case SiriRestClientConfig.RESPONSE_TYPE_XML:
			return "xml";
		default:
			return ""; // should never happen
		}
	}

	/**
	 * Internal method to make actual request to server
	 * 
	 * @param full
	 *            url for a JSON or XML request to the server (e.g.,
	 *            http://bustime
	 *            .mta.info/api/siri/vehicle-monitoring.json?OperatorRef
	 *            =MT%20A%20NYCT&DirectionRef=0&LineRef=MTA%20NYCT_S40&)
	 */
	@SuppressWarnings({ "deprecation" })
	private Siri makeRequest(String urlString) {

		Siri s = null;

		URL url = null;

		HttpURLConnection urlConnection = null;

		try {

			disableConnectionReuseIfNecessary(); // For bugs in
													// HttpURLConnection
													// pre-Froyo
			url = new URL(urlString);
			
			Log.d(MainActivity.TAG, "Using URL = " + url.toString());

			switch (config.getResponseType()) {

			case SiriRestClientConfig.RESPONSE_TYPE_JSON:
				// JSON
				if (config.getHttpConnectionType() == SiriRestClientConfig.HTTP_CONNECTION_TYPE_JACKSON) {
					// Use integrated Jackson HTTP connection by passing URL
					// directly into Jackson object

					if (config.getJacksonObjectType() == SiriRestClientConfig.JACKSON_OBJECT_TYPE_READER) {
						/*
						 * Use an ObjectReader (instead of ObjectMapper), read
						 * from URL directly
						 * 
						 * According to Jackson Best Practices
						 * (http://wiki.fasterxml
						 * .com/JacksonBestPracticesPerformance), this should be
						 * most efficient of the 4 combinations.
						 */
						Log.v(MainActivity.TAG,
								"Using "+ getResponseTypeFileExtension().toUpperCase() + ", ObjectReader Jackson parser, Jackson HTTP Connection");
						startTime= System.nanoTime();
						s = SiriJacksonConfig.getObjectReaderInstance()
								.readValue(url);
						endTime= System.nanoTime();
					} else {
						// Use ObjectMapper, read from URL directly
						Log.v(MainActivity.TAG,
								"Using "+ getResponseTypeFileExtension().toUpperCase() + ", ObjectMapper Jackson parser, Jackson HTTP Connection");
						startTime= System.nanoTime();
						s = SiriJacksonConfig.getObjectMapperInstance()
								.readValue(url, Siri.class);
						endTime= System.nanoTime();
					}
				} else {
					if (config.getJacksonObjectType() == SiriRestClientConfig.JACKSON_OBJECT_TYPE_READER) {
						// Use ObjectReader with Android HttpURLConnection
						Log.v(MainActivity.TAG,
								"Using "+ getResponseTypeFileExtension().toUpperCase() + ", ObjectReader Jackson parser, Android HttpURLConnection");
						startTime= System.nanoTime();
						// Use Android HttpURLConnection
						urlConnection = (HttpURLConnection) url.openConnection();
						
						s = SiriJacksonConfig.getObjectReaderInstance()
								.readValue(urlConnection.getInputStream());
						endTime= System.nanoTime();
					} else {
						// Use ObjectMapper with Android HttpURLConnection
						Log.v(MainActivity.TAG,
								"Using "+ getResponseTypeFileExtension().toUpperCase() + ", ObjectMapper Jackson parser, Android HttpURLConnection");
						startTime= System.nanoTime();
						// Use Android HttpURLConnection
						urlConnection = (HttpURLConnection) url.openConnection();
						s = SiriJacksonConfig.getObjectMapperInstance()
								.readValue(urlConnection.getInputStream(),
										Siri.class);
						endTime= System.nanoTime();
					}
				}

				break;
			case SiriRestClientConfig.RESPONSE_TYPE_XML:
				// XML
				if (config.getHttpConnectionType() == SiriRestClientConfig.HTTP_CONNECTION_TYPE_JACKSON) {
					// Use integrated Jackson HTTP connection by passing URL
					// directly into Jackson object
					Log.v(MainActivity.TAG,
							"Using "+ getResponseTypeFileExtension().toUpperCase() + ", Jackson HTTP Connection");
					
					// Parse the SIRI XML response					
					startTime= System.nanoTime();
					s = SiriJacksonConfig.getXmlMapperInstance().readValue(url,
							Siri.class);
					endTime= System.nanoTime();

				} else {
					Log.v(MainActivity.TAG,
							"Using "+ getResponseTypeFileExtension().toUpperCase() + ", Android HttpURLConnection");
					startTime= System.nanoTime();
					// Use Android HttpURLConnection
					urlConnection = (HttpURLConnection) url.openConnection();

					// Parse the SIRI XML response					
					s = SiriJacksonConfig.getXmlMapperInstance().readValue(
							urlConnection.getInputStream(), Siri.class);
					endTime= System.nanoTime();
				}

				break;
			}

		} catch (IOException e) {
			Log.e(MainActivity.TAG, "Error fetching JSON or XML: " + e);
			e.printStackTrace();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}

		return s;
	}

	/**
	 * Disable HTTP connection reuse which was buggy pre-froyo
	 */
	private void disableConnectionReuseIfNecessary() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}
}
