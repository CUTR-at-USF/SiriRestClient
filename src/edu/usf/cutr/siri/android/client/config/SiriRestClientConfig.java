package edu.usf.cutr.siri.android.client.config;

/**
 * Container class for general configuration options for the SiriRestClient
 * 
 * @author Sean J. Barbeau
 *
 */
public class SiriRestClientConfig {
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
	public SiriRestClientConfig(int responseType) {

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