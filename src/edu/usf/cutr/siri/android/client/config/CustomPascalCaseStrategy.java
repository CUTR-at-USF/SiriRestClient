package edu.usf.cutr.siri.android.client.config;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.PascalCaseStrategy;

public class CustomPascalCaseStrategy extends PascalCaseStrategy {

	public static final String XML_LANG = "lang";

	/**
	 * Adds a special case to PascaCaseStrategy, for XML namespace elements such
	 * as "xml:lang" that appear in XML documents where the rest of the elements
	 * are in PascalCase.
	 * 
	 * For example: <Description xml:lang="EN">b/d 1:00pm until f/n. loc al and
	 * express buses run w/delays & detours. POTUS visit in MANH. Allow
	 * additional travel time Details at www.mta.info</Description>
	 * 
	 * We want to convert normal elements to PascalCase, but not elements in the
	 * reserved XML namespace.
	 * 
	 * @param input
	 *            formatted as camelCase string
	 * @return input converted to PascalCase format, unless the input is in the
	 *         reserved XML namespace (e.g., xml:lang) and must remain
	 *         lowercase.
	 */
	@Override
	public String translate(String input) {
		if (input.equals(XML_LANG)) {
			// Don't modify reserved XML namespace elements
			return input;
		} else {
			// Use the normal PascalCaseStrategy
			return super.translate(input);
		}
	}
}