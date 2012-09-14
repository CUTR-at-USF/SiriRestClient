package edu.usf.cutr.siri.android.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Build;
import android.util.Log;
import uk.org.siri.siri.Siri;
import edu.usf.cutr.siri.android.util.SiriJacksonConfig;

/**
 * This class is used to make a request to a RESTful SIRI API server, parse the
 * response, and return a Siri data object to the caller.
 * 
 * @author Sean J. Barbeau
 * 
 */
public class SiriRestClient {

	ServerConfig config;

	// Base URL for vehicle monitoring requests
	String vehMonBaseUrl;

	// Base URL for stop monitoring requests
	String stopMonBaseUrl;

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
			ServerConfig config) {
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
	public void setConfig(ServerConfig config) {
		this.config = config;
	}

	/**
	 * Gets the current configuration for requests to be made to the server
	 * 
	 * @return the current configuration for requests to be made to the server
	 */
	public ServerConfig getConfig() {
		return config;
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
	 */
	public Siri makeVehicleMonRequest(String devKey, String operatorRef,
			String vehicleRef, String lineRef, int directionRef,
			String vehicleMonitoringDetailLevel, int maximumNumberOfCallsOnwards) {

		StringBuffer sb = new StringBuffer();

		sb.append(vehMonBaseUrl); // Base URL for stop mon request

		sb.append("." + getRequestTypeFileExtension()); // .json or .xml

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

		sb.append("." + getRequestTypeFileExtension()); // .json or .xml

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
	 * on request type
	 * 
	 * @return "json" if request is JSON, or "xml" if request type is XML
	 */
	private String getRequestTypeFileExtension() {
		switch (config.getResponseType()) {
		case ServerConfig.RESPONSE_TYPE_JSON:
			return "json";
		case ServerConfig.RESPONSE_TYPE_XML:
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
			
			Log.i(MainActivity.TAG, "Using URL = " + url.toString());

			switch (config.getResponseType()) {

			case ServerConfig.RESPONSE_TYPE_JSON:
				// JSON
				if (config.getHttpConnectionType() == ServerConfig.HTTP_CONNECTION_TYPE_JACKSON) {
					// Use integrated Jackson HTTP connection by passing URL
					// directly into Jackson object

					if (config.getJacksonObjectType() == ServerConfig.JACKSON_OBJECT_TYPE_READER) {
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
								"Using ObjectReader Jackson parser, Jackson HTTP Connection");
						s = SiriJacksonConfig.getObjectReaderInstance()
								.readValue(url);
					} else {
						// Use ObjectMapper, read from URL directly
						Log.v(MainActivity.TAG,
								"Using ObjectMapper Jackson parser, Jackson HTTP Connection");
						s = SiriJacksonConfig.getObjectMapperInstance()
								.readValue(url, Siri.class);
					}
				} else {
					// Use Android HttpURLConnection
					urlConnection = (HttpURLConnection) url.openConnection();

					if (config.getJacksonObjectType() == ServerConfig.JACKSON_OBJECT_TYPE_READER) {
						// Use ObjectReader with Android HttpURLConnection
						Log.v(MainActivity.TAG,
								"Using ObjectReader Jackson parser, Android HttpURLConnection");
						s = SiriJacksonConfig.getObjectReaderInstance()
								.readValue(urlConnection.getInputStream());
					} else {
						// Use ObjectMapper with Android HttpURLConnection
						Log.v(MainActivity.TAG,
								"Using ObjectMapper Jackson parser, Android HttpURLConnection");
						s = SiriJacksonConfig.getObjectMapperInstance()
								.readValue(urlConnection.getInputStream(),
										Siri.class);
					}
				}

				break;
			case ServerConfig.RESPONSE_TYPE_XML:
				// XML
				if (config.getHttpConnectionType() == ServerConfig.HTTP_CONNECTION_TYPE_JACKSON) {
					// Use integrated Jackson HTTP connection by passing URL
					// directly into Jackson object
					// Parse the SIRI XML response
					s = SiriJacksonConfig.getXmlMapperInstance().readValue(url,
							Siri.class);

				} else {
					// Use Android HttpURLConnection
					urlConnection = (HttpURLConnection) url.openConnection();

					// Parse the SIRI XML response
					s = SiriJacksonConfig.getXmlMapperInstance().readValue(
							urlConnection.getInputStream(), Siri.class);
				}

				break;
			}

		} catch (IOException e) {
			Log.e(MainActivity.TAG, "Error fetching JSON or XML: " + e);
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

	public class ServerConfig {
		/**
		 * Specifies that the request type be to return Javascript Object
		 * Notation (JSON)
		 */
		public static final int RESPONSE_TYPE_JSON = 0;

		/**
		 * Specifies that the request type be in XML
		 */
		public static final int RESPONSE_TYPE_XML = 1;

		/**
		 * Specifies that the HTTP connection being used is the connection
		 * embedded within Jackson (default setting)
		 */
		public static final int HTTP_CONNECTION_TYPE_JACKSON = 0;
		/**
		 * Specifies that the HTTP connection being used is the connection
		 * defined by the Android HTTPURLConnection
		 */
		public static final int HTTP_CONNECTION_TYPE_ANDROID = 1;

		/**
		 * Specifies that the Jackson object used for parsing JSON is the
		 * ObjectReader (default setting)
		 */
		public static final int JACKSON_OBJECT_TYPE_READER = 0;
		/**
		 * Specifies that the Jackson object used for parsing JSON is the
		 * ObjectMapper
		 */
		public static final int JACKSON_OBJECT_TYPE_MAPPER = 1;

		// Holds the current selected values, based on the above constants
		private int responseType;
		private int httpConnectionType;
		private int jacksonObjectType;

		/**
		 * Constructor used to set up the SiriRestClient with various options,
		 * based on the constants defined in this class
		 * 
		 * @param responseType
		 *            RESPONSE_TYPE_JSON for JSON, RESPONSE_TYPE_XML for XML
		 * @param httpConnectionType
		 *            HTTP_CONNECTION_TYPE_JACKSON to use the connection type
		 *            internal to Jackson, HTTP_CONNECTION_TYPE_ANDROID to use
		 *            the Android HTTPURLConnection *
		 */
		public ServerConfig(int responseType) {

			if (responseType > 1 || responseType < 0) {
				throw new IllegalArgumentException(
						"Input must be constants defined in this class");
			}

			this.responseType = responseType;
			this.httpConnectionType = HTTP_CONNECTION_TYPE_JACKSON; // Default
																	// setting
			this.jacksonObjectType = JACKSON_OBJECT_TYPE_READER; // Default
																	// setting
		}

		/**
		 * Returns HTTP_CONNECTION_TYPE_JACKSON for the connection type internal
		 * to Jackson, HTTP_CONNECTION_TYPE_ANDROID for the Android
		 * HTTPURLConnection
		 * 
		 * @return HTTP_CONNECTION_TYPE_JACKSON for the connection type internal
		 *         to Jackson, HTTP_CONNECTION_TYPE_ANDROID for the Android
		 *         HTTPURLConnection
		 */
		public int getHttpConnectionType() {
			return httpConnectionType;
		}

		/**
		 * Returns the current Jackson Object Type
		 * 
		 * @return JACKSON_OBJECT_TYPE_READER for the Jackson ObjectReader,
		 *         JACKSON_OBJECT_TYPE_MAPPER for the Jackson ObjectMapper, or
		 *         -1 if not parsing JSON
		 */
		public int getJacksonObjectType() {
			return jacksonObjectType;
		}

		/**
		 * Sets Http connection type
		 * 
		 * @param httpConnectionType
		 *            HTTP_CONNECTION_TYPE_JACKSON for the connection type
		 *            internal to Jackson, HTTP_CONNECTION_TYPE_ANDROID for the
		 *            Android HTTPURLConnection
		 */
		public void setHttpConnectionType(int httpConnectionType) {
			this.httpConnectionType = httpConnectionType;
		}

		/**
		 * Sets the current Jackson Object Type
		 * 
		 * @param jacksonObjectType
		 *            JACKSON_OBJECT_TYPE_READER for the Jackson ObjectReader,
		 *            JACKSON_OBJECT_TYPE_MAPPER for the Jackson ObjectMapper,
		 *            or -1 if not parsing JSON
		 */
		public void setJacksonObjectType(int jacksonObjectType) {
			this.jacksonObjectType = jacksonObjectType;
		}

		/**
		 * Sets the requested response type
		 * 
		 * @param responseType
		 *            RESPONSE_TYPE_JSON for JSON, RESPONSE_TYPE_XML for XML
		 */
		public void setResponseType(int responseType) {
			this.responseType = responseType;
		}

		/**
		 * Gets the requested response type
		 * 
		 * @return RESPONSE_TYPE_JSON for JSON, RESPONSE_TYPE_XML for XML
		 */
		public int getResponseType() {
			return responseType;
		}

	}
}
