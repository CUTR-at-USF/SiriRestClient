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

		sb.append(vehMonBaseUrl); // Base URL for veh mon request

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
				sb.append("MaximumNumberOfCallsOnwards="
						+ maximumNumberOfCallsOnwards + "&");
			} else {
				throw new IllegalArgumentException(
						"MaximumNumberOfCallsOnwards must be 1 or greater, or -1 if not to be used");
			}
		}
		
		//Clean up string to create final URL
		String url = cleanUpUrl(sb.toString());

		// Make actual HTTP call to server using parameters string we just
		// built, pre-fixed with the base URL and correct response type
		// extension
		return makeRequest(url);

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

		//Clean up string to create final URL
		String url = cleanUpUrl(sb.toString());

		// Make actual HTTP call to server using parameters string we just
		// built, pre-fixed with the base URL and correct response type
		// extension
		return makeRequest(url); 
	}

	/**
	 * Utility method that takes in a URL string and cleans it up
	 * 
	 * @param url url to be cleaned
	 * @return clean URL
	 */
	public String cleanUpUrl(String url){
		String cleanUrl = url;
		
		//If there is a trailing '&' left over from concatination of variables, remove it
		 if (url.charAt(url.length()-1)=='&'){
			 cleanUrl = url.substring(0, url.length()-1);
		 }
		 
		 //Replace any spaces with equivalent characters
		 cleanUrl = cleanUrl.replace(" ", "%20");
		 
		 return cleanUrl;
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
			
			/**
			 * The below switch statement tests a variety of different configurations 
			 * for 1) requesting JSON or XML data from a server and 2) parsing the 
			 * response into a Siri object via Jackson data binding.
			 *  
			 *  For "#1 - Requesting Data", there are two options:
			 *  	a. Use embedded Jackson HTTP connection - simply pass the URL into
			 *  	   the Jackson object (e.g., ObjectMapper, ObjectReader, or XmlMapper)
			 *  	   and Jackson handles establishing an HTTP connection and retrieving
			 *  	   the data.
			 *  	b. Use the Android HttpURLConnection - Instantiate our own HTTP connection
			 *  	   via Android's HttpURLConnection, and pass the InputStream from that
			 *  	   connection into the Jackson object.
			 *  
			 *  	   This article states that HttpURLConnection should be the most efficient 
			 *  	   connection type on Android:
			 *         http://android-developers.blogspot.com/2011/09/androids-http-clients.html
			 *         ...but according to Jackson Best Practices (http://goo.gl/7cRGh), we 
			 *         should just pass in the URL and let Jackson handle the connection - so,
			 *         overall best practice is unclear from documentation.  As of 
			 *         2.1 Jackson seems to just use the default FileInputStream connection
			 *         (http://goo.gl/zm7Hs) from the normal Java platform, so it would seem
			 *         that the Android HttpURLConnection would be more efficient.  
			 *  
			 * For "#2 - Parsing Response", there are two options for Jackson objects that can
			 * be used when parsing JSON:
			 * 		a. Use the ObjectMapper directly - the core class that must be instantiated 
			 * 		   before parsing JSON content in Jackson.  According to Jackson Best Practices 
			 * 		   (http://goo.gl/7cRGh), the ObjectReader should be used instead.  Therefore,
			 * 		   The ObjectMapper configuration is included only for benchmarking performance. 		   
			 * 		b. Use the ObjectReader - this object is retrieved from the ObjectMapper
			 * 		   after the ObjectMapper is instantiated, and can be used to parse JSON
			 * 		   instead of using the ObjectMapper directly.
			 * 		   According to Jackson Best Practices (http://goo.gl/7cRGh), using the 
			 * 		   ObjectReader should be more efficient than the ObjectMapper, especially 
			 * 		   for Jackson versions 2.1 and later. 
			 * 
			 * 		Check out the class "SiriJacksonConfig" included in this project to see
			 * 	    the instantiation and configuration of the ObjectMapper and ObjectReader 
			 *      classes for JSON parsing.
			 * 
			 *      For parsing XML, there is only one option: XmlMapper.
			 * 
			 * So, according to Jackson Best Practices Performance: 
			 * http://wiki.fasterxml.com/JacksonBestPracticesPerformance
			 * 
			 * ...and the Android docs:
			 * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
			 *	
			 * ...it seems the most efficient combinations should be:
			 * 
			 *  JSON - Android HttpURLConnection + ObjectReader
			 * 
			 *  XML - Android HttpURLConnection + XmlMapper
			 */

			switch (config.getResponseType()) {

			case SiriRestClientConfig.RESPONSE_TYPE_JSON:
				// JSON
				if (config.getHttpConnectionType() == SiriRestClientConfig.HTTP_CONNECTION_TYPE_JACKSON) {
					/**
					 * Use integrated Jackson HTTP connection by passing URL directly into Jackson object
					 * for below two options
					 */

					if (config.getJacksonObjectType() == SiriRestClientConfig.JACKSON_OBJECT_TYPE_READER) {
						/*
						 * Use an ObjectReader (instead of ObjectMapper), read
						 * from URL directly using internal Jackson connection
						 * 
						 * According to Jackson Best Practices
						 * (http://wiki.fasterxml
						 * .com/JacksonBestPracticesPerformance), this should be
						 * most efficient of the 4 JSON configuration combinations.
						 * 
						 * However, Android docs suggest that HttpURLConnection is more efficient.
						 */
						Log.d(MainActivity.TAG,
								"Using "+ getResponseTypeFileExtension().toUpperCase() + ", ObjectReader Jackson parser, Jackson HTTP Connection");
						startTime= System.nanoTime();
						s = SiriJacksonConfig.getObjectReaderInstance()
								.readValue(url);
						endTime= System.nanoTime();
					} else {
						/* Use ObjectMapper, read from URL directly
						 * ObjectReader should be more efficient than the ObjectMapper.  
						 * So, we include this only for performance benchmarking tests.
						 */
						Log.v(MainActivity.TAG,
								"Using "+ getResponseTypeFileExtension().toUpperCase() + ", ObjectMapper Jackson parser, Jackson HTTP Connection");
						startTime= System.nanoTime();
						s = SiriJacksonConfig.getObjectMapperInstance()
								.readValue(url, Siri.class);
						endTime= System.nanoTime();
					}
				} else {
					/**
					 * Use Android HttpURLConnection for below two options
					 */
					if (config.getJacksonObjectType() == SiriRestClientConfig.JACKSON_OBJECT_TYPE_READER) {
						/*
						 *  Use ObjectReader with Android HttpURLConnection
						 *  
						 *  From our analysis of both Android and Jackson docs,
						 *  this should be the most efficient of the 4 JSON combinations.
						 */
						Log.v(MainActivity.TAG,
								"Using "+ getResponseTypeFileExtension().toUpperCase() + ", ObjectReader Jackson parser, Android HttpURLConnection");
						startTime= System.nanoTime();
						// Use Android HttpURLConnection - this should be more efficient than internal JSON HTTP connection
						urlConnection = (HttpURLConnection) url.openConnection();
						
						s = SiriJacksonConfig.getObjectReaderInstance()
								.readValue(urlConnection.getInputStream());
						endTime= System.nanoTime();
					} else {
						/* Use ObjectMapper with Android HttpURLConnection
						 * 
						 * According to Jackson Best Practices (http://wiki.fasterxml.com/JacksonBestPracticesPerformance),
						 * the ObjectReader should be more efficient than the ObjectMapper.  So, we include this only for
						 * performance benchmarking tests.
						 */
						Log.v(MainActivity.TAG,
								"Using "+ getResponseTypeFileExtension().toUpperCase() + ", ObjectMapper Jackson parser, Android HttpURLConnection");
						startTime= System.nanoTime();
						// Use Android HttpURLConnection - this should be more efficient than internal JSON HTTP connection
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
					/*
					 *  Use integrated Jackson HTTP connection by passing URL directly into Jackson object
					 *  Jackson Best Practices says this should be most efficient, but Android docs suggest 
					 *  Android HttpURLConnection is better.
					 */
					Log.v(MainActivity.TAG,
							"Using "+ getResponseTypeFileExtension().toUpperCase() + ", Jackson HTTP Connection");
					
					// Parse the SIRI XML response					
					startTime= System.nanoTime();
					s = SiriJacksonConfig.getXmlMapperInstance().readValue(url,
							Siri.class);
					endTime= System.nanoTime();

				} else {
					/*
					 *  Use Android HttpURLConnection.  Android docs say this is best, but
					 *  Jackson Best Practices says integrated HTTP connection is better for normal Java platform.
					 */
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
			//Reset timestamps to show there was an error
			startTime = 0;
			endTime = 0;
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
