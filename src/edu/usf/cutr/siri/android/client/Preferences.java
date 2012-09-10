package edu.usf.cutr.siri.android.client;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class Preferences extends SherlockPreferenceActivity {

	/**
	 * Preference keys
	 * 
	 * NOTE: These must match the values in preferences.xml
	 */
	public static final String KEY_HTTP_CONNECTION_TYPE = "pref_key_http_connection";	
	public static final int HTTP_CONNECTION_TYPE_JACKSON = 0;
	public static final int HTTP_CONNECTION_TYPE_ANDROID = 1;
	
	public static final String KEY_JACKSON_OBJECT_TYPE = "pref_key_jackson_object";
	public static final int JACKSON_OBJECT_TYPE_READER = 0;
	public static final int JACKSON_OBJECT_TYPE_MAPPER = 1;
		
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
