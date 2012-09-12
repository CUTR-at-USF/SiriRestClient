package edu.usf.cutr.siri.android.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Build;
import android.util.Log;
import uk.org.siri.siri.Siri;
import edu.usf.cutr.siri.android.client.Preferences;
import edu.usf.cutr.siri.android.client.SiriRestClientActivity;

/**
 * This class is used to make a request to a RESTful SIRI API server, parse the
 * response, and return a Siri data object to the caller.
 * 
 * @author Sean J. Barbeau
 * 
 */
public class SiriRestClient {

	/**
	 * Specifies that the request type be to return Javascript Object Notation
	 * (JSON)
	 */
	public static final int RESPONSE_TYPE_JSON = 0;

	/**
	 * Specifies that the request type be in XML
	 */
	public static final int RESPONSE_TYPE_XML = 1;

	/**
	 * Specifies that the HTTP connection being used is the connection embedded
	 * within Jackson
	 */
	public static final int HTTP_CONNECTION_TYPE_JACKSON = 0;
	/**
	 * Specifies that the HTTP connection being used is the connection defined
	 * by the Android HTTPURLConnection
	 */
	public static final int HTTP_CONNECTION_TYPE_ANDROID = 1;

	/**
	 * Specifies that the Jackson object used for parsing JSON is the
	 * ObjectReader
	 */
	public static final int JACKSON_OBJECT_TYPE_READER = 0;
	/**
	 * Specifies that the Jackson object used for parsing JSON is the
	 * ObjectMapper
	 */
	public static final int JACKSON_OBJECT_TYPE_MAPPER = 1;

	ServerConfig config;

	String baseUrl;

	/**
	 * Creates a new SiriRestClient object that can make vehicle monitoring and
	 * stop monitoring REST requests, parse responses, and return an
	 * instantiated Siri object
	 * 
	 * @param baseUrl
	 *            the entire URL up to the file extension (e.g.,
	 *            http://bustime.mta.info/api/siri/vehicle-monitoring)
	 * @param config
	 *            configuration for requests to be made to the server
	 */
	public SiriRestClient(String baseUrl, ServerConfig config) {
		this.baseUrl = baseUrl;
		this.config = config;
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
	 *            a filter by GTFS direction ID (optional). Either 0 or 1.
	 * @param vehicleMonitoringDetailLevel
	 *            Determines whether or not the response will include the stops
	 *            ("calls" in SIRI-speak) each vehicle is going to make
	 *            (optional). To get calls data, use value calls, otherwise use
	 *            value normal (default is normal).
	 * @param maximumNumberOfCallsOnwards
	 *            Limit on the number of OnwardCall elements for each vehicle
	 *            when VehicleMonitoringDetailLevel=calls
	 * @return a Siri object containing the parsed VehicleMonRequest response
	 *         from the server
	 */
	public Siri makeVehicleMonRequest(String devKey, String operatorRef,
			String vehicleRef, String lineRef, int directionRef,
			String vehicleMonitoringDetailLevel,
			String maximumNumberOfCallsOnwards) {

		// TODO - set up parameters in string based on passed-in values

		// Make actual HTTP call to server
		return makeRequest("");

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
	 *            a developer API key
	 * @param operatorRef
	 *            the GTFS agency ID to be monitored
	 * @param monitoringRef
	 *            the GTFS stop ID of the stop to be monitored (required). For
	 *            example, 308214 for the stop at 5th Avenue and Union St
	 *            towards Bay Ridge.
	 * @param lineRef
	 *            a filter by GTFS route ID
	 * @param directionRef
	 *            a filter by GTFS direction ID (optional). Either 0 or 1.
	 * @param stopMonitoringDetailLevel
	 *            Determines whether or not the response will include the stops
	 *            ("calls" in SIRI-speak) each vehicle is going to make after it
	 *            serves the selected stop (optional). To get calls data, use
	 *            value calls, otherwise use value normal (default is normal).
	 * @param maximumNumberOfCallsOnwards
	 *            Limits the number of OnwardCall elements returned in the query
	 * @return a Siri object containing the parsed StopMonRequest response from
	 *         the server
	 */
	public Siri makeStopMonRequest(String devKey, String operatorRef,
			String monitoringRef, String lineRef, int directionRef,
			String stopMonitoringDetailLevel, String maximumNumberOfCallsOnwards) {

		// TODO - set up parameters in string based on passed-in values

		// Make actual HTTP call to server
		return makeRequest("");

	}

	/**
	 * Internal method to make actual request to server
	 * 
	 * @param parameters
	 *            the parameters used to defined what type of elements should be
	 *            included in a response (e.g.,
	 *            ?OperatorRef=MTA%20NYCT&DirectionRef=0&LineRef=MTA%20NYCT_S40)
	 *            *
	 */
	private Siri makeRequest(String parameters) {

		Siri s = null;

		URL url = null;

		HttpURLConnection urlConnection = null;

		String urlString = "";

		try {

			disableConnectionReuseIfNecessary(); // For bugs in
													// HttpURLConnection
													// pre-Froyo

			switch (config.getResponseType()) {

			case RESPONSE_TYPE_JSON:

				urlString = baseUrl + ".json" + parameters;

				url = new URL(urlString);

				Log.i(SiriRestClientActivity.TAG,
						"Using URL = " + url.toString());

				// JSON
				if (config.getHttpConnectionType() == HTTP_CONNECTION_TYPE_JACKSON) {
					// Use integrated Jackson HTTP connection by passing URL
					// directly into Jackson object

					if (config.getJacksonObjectType() == JACKSON_OBJECT_TYPE_READER) {
						/*
						 * Use an ObjectReader (instead of ObjectMapper), read
						 * from URL directly
						 * 
						 * According to Jackson Best Practices
						 * (http://wiki.fasterxml
						 * .com/JacksonBestPracticesPerformance), this should be
						 * most efficient of the 4 combinations.
						 */
						Log.v(SiriRestClientActivity.TAG,
								"Using ObjectReader Jackson parser, Jackson HTTP Connection");
						s = SiriJacksonConfig.getObjectReaderInstance()
								.readValue(url);
					} else {
						// Use ObjectMapper, read from URL directly
						Log.v(SiriRestClientActivity.TAG,
								"Using ObjectMapper Jackson parser, Jackson HTTP Connection");
						s = SiriJacksonConfig.getObjectMapperInstance()
								.readValue(url, Siri.class);
					}
				} else {
					// Use Android HttpURLConnection
					urlConnection = (HttpURLConnection) url.openConnection();

					if (config.getJacksonObjectType() == JACKSON_OBJECT_TYPE_READER) {
						// Use ObjectReader with Android HttpURLConnection
						Log.v(SiriRestClientActivity.TAG,
								"Using ObjectReader Jackson parser, Android HttpURLConnection");
						s = SiriJacksonConfig.getObjectReaderInstance()
								.readValue(urlConnection.getInputStream());
					} else {
						// Use ObjectMapper with Android HttpURLConnection
						Log.v(SiriRestClientActivity.TAG,
								"Using ObjectMapper Jackson parser, Android HttpURLConnection");
						s = SiriJacksonConfig.getObjectMapperInstance()
								.readValue(urlConnection.getInputStream(),
										Siri.class);
					}
				}

				break;
			case RESPONSE_TYPE_XML:
				// XML
				urlString = baseUrl + ".xml" + parameters;

				break;
			}

		} catch (IOException e) {
			Log.e(SiriRestClientActivity.TAG, "Error fetching JSON: " + e);
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
		 *            the Android HTTPURLConnection
		 * @param jacksonObjectType
		 *            JACKSON_OBJECT_TYPE_READER to use the Jackson
		 *            ObjectReader, JACKSON_OBJECT_TYPE_MAPPER to use the
		 *            Jackson ObjectMapper, or -1 if not parsing JSON
		 */
		public ServerConfig(int responseType, int httpConnectionType,
				int jacksonObjectType) {

			if (responseType > 1 || httpConnectionType > 1
					|| jacksonObjectType > 1 || responseType < 0
					|| httpConnectionType < 0 || jacksonObjectType < -1) {
				throw new IllegalArgumentException(
						"Input must be constants defined in this class");
			}

			this.responseType = responseType;
			this.httpConnectionType = httpConnectionType;
			this.jacksonObjectType = jacksonObjectType;
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
