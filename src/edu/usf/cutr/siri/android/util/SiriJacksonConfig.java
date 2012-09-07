package edu.usf.cutr.siri.android.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.usf.cutr.siri.jackson.PascalCaseStrategy;

/**
 * This class holds a static instance of a Jackson ObjectMapper that is
 * configured for parsing Siri JSON responses
 * 
 * The ObjectMapper is thread-safe after it is configured:
 * http://wiki.fasterxml.com/JacksonFAQThreadSafety
 * 
 * ...so we can configure it once here and then use it in multiple fragments.
 * 
 * @author Sean J. Barbeau
 * 
 */
public class SiriJacksonConfig {

	private static ObjectMapper mapper = null;

	/**
	 * Constructs a thread-safe instance of a Jackson ObjectMapper configured to parse
	 * JSON responses from a Mobile Siri API 
	 * @return thread-safe ObjectMapper configured for SIRI JSON responses
	 */
	public synchronized static ObjectMapper getObjectMapperInstance() {

		if (mapper == null) {
			// Jackson configuration
			mapper = new ObjectMapper();

			mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
			mapper.configure(
					DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			mapper.configure(
					DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
					true);
			mapper.configure(
					DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
			mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING,
					true);

			// Tell Jackson to expect the JSON in PascalCase, instead of
			// camelCase
			mapper.setPropertyNamingStrategy(new PascalCaseStrategy());
		}

		return mapper;
	}
}
