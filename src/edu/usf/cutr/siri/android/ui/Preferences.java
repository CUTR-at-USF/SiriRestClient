package edu.usf.cutr.siri.android.ui;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class Preferences extends SherlockPreferenceActivity {

	/**
	 * Preference keys
	 * 
	 * NOTE: Keys must match the values in preferences.xml
	 */
	public static final String KEY_RESPONSE_TYPE = "pref_key_response_type";	
	
	public static final String KEY_HTTP_CONNECTION_TYPE = "pref_key_http_connection";	
	
	public static final String KEY_JACKSON_OBJECT_TYPE = "pref_key_jackson_object";
	
	
	/**
	 * Constants for defining connection options are defined in:
	 * edu.usf.cutr.siri.android.util.SiriRestClient
	 */
		
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
