package edu.usf.cutr.siri.android.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import edu.usf.cutr.siri.android.client.SiriRestClient;
import edu.usf.cutr.siri.android.client.SiriRestClientConfig;

public class Preferences extends SherlockPreferenceActivity {

	/**
	 * Preference keys
	 * 
	 * NOTE: Keys must match the values in preferences.xml
	 */
	public static final String KEY_RESPONSE_TYPE = "pref_key_response_type";

	public static final String KEY_HTTP_CONNECTION_TYPE = "pref_key_http_connection";

	public static final String KEY_JACKSON_OBJECT_TYPE = "pref_key_jackson_object";

	ListPreference responseTypes;
	ListPreference jacksonJsonObjectTypes;

	/**
	 * Constants for defining connection options are defined in:
	 * edu.usf.cutr.siri.android.util.SiriRestClient
	 */

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		responseTypes = (ListPreference) findPreference(KEY_RESPONSE_TYPE);
		jacksonJsonObjectTypes = (ListPreference) findPreference(KEY_JACKSON_OBJECT_TYPE);

		/**
		 * Set up a PreferenceChangeListener so that when the user selects XML,
		 * we disable the Jackson JSON object type option. If the user selects
		 * JSON, then we re-enable the Jackson JSON object type option
		 */
		responseTypes
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						final int val = Integer.valueOf(newValue.toString());

						checkJacksonJSONObjectTypeSetting(val);

						return true;
					}
				});

		/*
		 *  Check the current response type setting, and enable/disable
		 *  appropriately on start-up
		 */
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		int responseType = Integer.parseInt(sharedPref.getString(
							Preferences.KEY_RESPONSE_TYPE, "0"));
		checkJacksonJSONObjectTypeSetting(responseType);
	}

	/**
	 * If the user selects XML response type, we disable the Jackson JSON object
	 * type option. If the user selects JSON, then we re-enable the Jackson JSON
	 * object type option.
	 * 
	 * @param val
	 *            currently selected server response type
	 */
	private void checkJacksonJSONObjectTypeSetting(int val) {
		if (val == SiriRestClientConfig.RESPONSE_TYPE_XML) {
			jacksonJsonObjectTypes.setEnabled(false);
		} else {
			if (val == SiriRestClientConfig.RESPONSE_TYPE_JSON) {
				jacksonJsonObjectTypes.setEnabled(true);
			}
		}
	}
}
