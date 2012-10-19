package edu.usf.cutr.siri.android.client.config;

import uk.org.siri.siri.Siri;

import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.fasterxml.aalto.stax.OutputFactoryImpl;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * This class holds a static instance of a Jackson ObjectMapper and ObjectReader
 * that are configured for parsing SIRI JSON responses, and a Jackson XmlMapper
 * for parsing SIRI XML responses.
 * 
 * The ObjectMapper, ObjectReader, and XmlMapper are thread-safe after it is
 * configured: http://wiki.fasterxml.com/JacksonFAQThreadSafety
 * 
 * ...so we can configure it once here and then use it in multiple fragments.
 * 
 * @author Sean J. Barbeau
 * 
 */
public class SiriJacksonConfig {

	// For JSON
	private static ObjectMapper mapper = null;
	private static ObjectReader reader = null;

	// For XML
	private static XmlMapper xmlMapper = null;

	// Private empty constructor since this object shouldn't be instantiated
	private SiriJacksonConfig() {
	}

	/**
	 * Constructs a thread-safe instance of a Jackson ObjectMapper configured to
	 * parse JSON responses from a Mobile Siri API.
	 * 
	 * According to Jackson Best Practices
	 * (http://wiki.fasterxml.com/JacksonBestPracticesPerformance), for
	 * efficiency reasons you should use the ObjectReader (via
	 * getObjectReaderInstance()) instead of the ObjectMapper.
	 * 
	 * @deprecated
	 * @return thread-safe ObjectMapper configured for SIRI JSON responses
	 */
	public synchronized static ObjectMapper getObjectMapperInstance() {
		return initObjectMapper();
	}

	/**
	 * Constructs a thread-safe instance of a Jackson ObjectReader configured to
	 * parse JSON responses from a Mobile Siri API
	 * 
	 * According to Jackson Best Practices
	 * (http://wiki.fasterxml.com/JacksonBestPracticesPerformance), this should
	 * be more efficient than the ObjectMapper.
	 * 
	 * @return thread-safe ObjectMapper configured for SIRI JSON responses
	 */
	public synchronized static ObjectReader getObjectReaderInstance() {

		if (reader == null) {
			reader = initObjectMapper().reader(Siri.class);
		}

		return reader;
	}

	/**
	 * Internal method used to init main ObjectMapper for JSON parsing
	 * 
	 * @return initialized ObjectMapper ready for JSON parsing
	 */
	private static ObjectMapper initObjectMapper() {
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
			mapper.setPropertyNamingStrategy(new PropertyNamingStrategy.PascalCaseStrategy());
		}
		return mapper;
	}

	/**
	 * Constructs a thread-safe instance of a Jackson XmlMapper configured to
	 * parse XML responses from a Mobile Siri API.
	 * 
	 * @return thread-safe ObjectMapper configured for SIRI XML responses
	 */
	public synchronized static ObjectMapper getXmlMapperInstance() {
		return initXmlMapper();
	}

	/**
	 * Internal method used to init main XmlMapper for XML parsing
	 * 
	 * @return initialized XmlMapper ready for XML parsing
	 */
	private static XmlMapper initXmlMapper() {
		if (xmlMapper == null) {

			// Use Aalto StAX implementation explicitly
			XmlFactory f = new XmlFactory(new InputFactoryImpl(),
					new OutputFactoryImpl());

			JacksonXmlModule module = new JacksonXmlModule();

			/**
			 * Tell Jackson that Lists are using "unwrapped" style (i.e., there
			 * is no wrapper element for list). This fixes the error
			 * "com.fasterxml.jackson.databind.JsonMappingException: Can not >>
			 * instantiate value of type [simple type, class >>
			 * uk.org.siri.siri.VehicleMonitoringDelivery] from JSON String; no
			 * >> single-String constructor/factory method (through reference
			 * chain: >> uk.org.siri.siri.Siri["ServiceDelivery"]->
			 * uk.org.siri.siri.ServiceDel >>
			 * ivery["VehicleMonitoringDelivery"])"
			 * 
			 * NOTE - This requires Jackson v2.1.
			 */
			module.setDefaultUseWrapper(false);

			/**
			 * Handles "xml:lang" attribute, which is used in SIRI
			 * NaturalLanguage String, and looks like: <Description
			 * xml:lang="EN">b/d 1:00pm until f/n. loc al and express buses run
			 * w/delays & detours. POTUS visit in MANH. Allow additional travel
			 * time Details at www.mta.info</Description>
			 * 
			 * Passing "Value" (to match expected name in XML to map,
			 * considering naming strategy) will make things work. This is since
			 * JAXB uses pseudo-property name of "value" for XML Text segments,
			 * whereas Jackson by default uses "" (to avoid name collisions).
			 * 
			 * NOTE - This requires vJackson 2.1.
			 * 
			 * NOTE - This still requires a CustomPascalCaseStrategy to work.
			 * Please see the CustomPascalCaseStrategy in this app that is used
			 * below.
			 */
			module.setXMLTextElementName("Value");

			xmlMapper = new XmlMapper(f, module);

			xmlMapper.configure(
					DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			xmlMapper.configure(
					DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
					true);
			xmlMapper.configure(
					DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
			xmlMapper.configure(
					DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);

			/**
			 * Tell Jackson to expect the XML in PascalCase, instead of
			 * camelCase NOTE: We need the CustomPascalStrategy here to handle
			 * XML namespace attributes such as xml:lang. See the comments in
			 * CustomPascalStrategy for details.
			 */
			xmlMapper.setPropertyNamingStrategy(new CustomPascalCaseStrategy());
		}

		return xmlMapper;

	}
}
