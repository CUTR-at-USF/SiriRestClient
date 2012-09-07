/*
 * Copyright 2012 University of South Florida
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package edu.usf.cutr.siri.jackson;

import org.codehaus.jackson.map.PropertyNamingStrategy.PropertyNamingStrategyBase;


/**
 * This class defines a custom naming strategy for Jackson so that SIRI
 * PascalCase JSON properties can be mapped to their camelCase counterparts. 
 * 
 * For example, camelCase would be "responseTimestamp", instead of
 * "ResponseTimestamp", which is PascalCase. By using this
 * PropertyNamingStrategy, Jackson will be able to property parse SIRI
 * PascalCase JSON properties without requiring @JsonProperty(X) annotations for
 * each getter and setter method.
 * 
 * @author Sean J. Barbeau
 * 
 */
public class PascalCaseStrategy extends PropertyNamingStrategyBase  {

	/**
	 * Converts camelCase to PascalCase
	 * 
	 * For example, "responseTimestamp" would be converted to
	 * "ResponseTimestamp".
	 * 
	 * This allows Jackson to expect the PascalCase version of the class
	 * field and method names instead of the camelCase version.
	 * 
	 * @param input formatted as camelCase string
	 * @return input converted to PascalCase format
	 */
	@Override
	public String translate(String input) {
		// Replace first lower-case letter with upper-case equivalent
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}	
}
