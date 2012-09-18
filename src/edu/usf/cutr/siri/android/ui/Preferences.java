package edu.usf.cutr.siri.android.ui;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import edu.usf.cutr.siri.android.client.SiriRestClient;
import edu.usf.cutr.siri.android.client.SiriRestClientConfig;

public class Preferences extends SherlockPreferenceActivity implements
		OnSharedPreferenceChangeListener {

	/**
	 * Preference keys
	 * 
	 * NOTE: Keys must match the values in preferences.xml
	 */
	public static final String KEY_RESPONSE_TYPE = "pref_key_response_type";

	public static final String KEY_HTTP_CONNECTION_TYPE = "pref_key_http_connection";

	public static final String KEY_JACKSON_OBJECT_TYPE = "pref_key_jackson_object";

	public static final String KEY_NUM_REQUESTS = "pref_key_num_requests";

	ListPreference listResponseTypes;
	ListPreference listJacksonJsonObjectTypes;
	ListPreference listHttpConnectionType;
	EditTextPreference txtNumRequests;
	
	SharedPreferences sharedPreferences;

	/**
	 * Constants for defining connection options are defined in:
	 * edu.usf.cutr.siri.android.util.SiriRestClient
	 */

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		listResponseTypes = (ListPreference) findPreference(KEY_RESPONSE_TYPE);
		listJacksonJsonObjectTypes = (ListPreference) findPreference(KEY_JACKSON_OBJECT_TYPE);
		listHttpConnectionType = (ListPreference) findPreference(KEY_HTTP_CONNECTION_TYPE);
		txtNumRequests = (EditTextPreference) findPreference(KEY_NUM_REQUESTS);		
		
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		
		txtNumRequests.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		
		//Verify number of requests entry
		txtNumRequests.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				boolean isInt = verifyInt(newValue);
				
				if(!isInt){
					//Tell user that entry must be valid integer
					Toast.makeText(
							Preferences.this,
							"Number of requests to execute must be an integer.",
							Toast.LENGTH_SHORT).show();
					Log.d(MainActivity.TAG,
							"User tried to enter invalid value for numRequests.");
					return false;
				}else{
					return true;
				}
			}
		});
	}
	
	/**
	 * Verify that the numRequests entry is a valid integer
	 * @param newValue entered value
	 * @return true if its a valid integer, false if its not
	 */
	private boolean verifyInt(Object newValue){
		try{
			Integer.parseInt(newValue.toString());
			return true;
		}catch(Exception e){
			return false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		PreferenceManager.getDefaultSharedPreferences(this)
				.registerOnSharedPreferenceChangeListener(this);		
		
		//Change the descriptions of the preferences based on user's selections
		changePreferenceDescription(KEY_RESPONSE_TYPE);
		changePreferenceDescription(KEY_JACKSON_OBJECT_TYPE);
		changePreferenceDescription(KEY_HTTP_CONNECTION_TYPE);
		changePreferenceDescription(KEY_NUM_REQUESTS);
	}

	/**
	 * Change the descriptions of the preferences based on what the user has
	 * selected
	 * 
	 * @param sharedPreferences
	 * @param key
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {	
		Log.d(MainActivity.TAG, "Preference '" + key + "' changed, changing preference description...");
		changePreferenceDescription(key);
	}
	
	/**
	 * Changes the preference descriptions based on the user's choice
	 * @param key key value of the preference that changed
	 */
	private void changePreferenceDescription(String key){
		if (key.equalsIgnoreCase(KEY_RESPONSE_TYPE)) {
			toggleJSONObjectTypeSettingEnabled(Integer.valueOf(sharedPreferences.getString(key, "0")));
			
			if (Integer.valueOf(sharedPreferences.getString(key, "0")) == SiriRestClientConfig.RESPONSE_TYPE_XML) {
				listResponseTypes.setSummary("XML response is selected.");
			}else{
				if (Integer.valueOf(sharedPreferences.getString(key, "0")) == SiriRestClientConfig.RESPONSE_TYPE_JSON) {
					listResponseTypes.setSummary("JSON response is selected.");
				}
			}
		}
		
		if (key.equalsIgnoreCase(KEY_JACKSON_OBJECT_TYPE)) {						
			if (Integer.valueOf(sharedPreferences.getString(key, "0")) == SiriRestClientConfig.JACKSON_OBJECT_TYPE_MAPPER) {
				listJacksonJsonObjectTypes.setSummary("ObjectMapper is selected.");
			}else{
				if (Integer.valueOf(sharedPreferences.getString(key, "0")) == SiriRestClientConfig.JACKSON_OBJECT_TYPE_READER) {
					listJacksonJsonObjectTypes.setSummary("ObjectReader is selected.");
				}
			}
		}
		
		if (key.equalsIgnoreCase(KEY_HTTP_CONNECTION_TYPE)) {						
			if (Integer.valueOf(sharedPreferences.getString(key, "0")) == SiriRestClientConfig.HTTP_CONNECTION_TYPE_ANDROID) {
				listHttpConnectionType.setSummary("Android HttpURLConnection is selected.");
			}else{
				if (Integer.valueOf(sharedPreferences.getString(key, "0")) == SiriRestClientConfig.HTTP_CONNECTION_TYPE_JACKSON) {
					listHttpConnectionType.setSummary("Jackson internal connection is selected.");
				}
			}
		}
		
		if (key.equalsIgnoreCase(KEY_NUM_REQUESTS)) {						
			txtNumRequests.setSummary(Integer.valueOf(sharedPreferences.getString(key, "1")) + " requests will be executed consecutively");			
		}
	}
	
	/**
	 * If the user selects XML response type, we disable the Jackson JSON object
	 * type option. If the user selects JSON, then we re-enable the Jackson JSON
	 * object type option.
	 * 
	 * @param val currently selected server response type
	 */
	private void toggleJSONObjectTypeSettingEnabled(int val) {
		if (val == SiriRestClientConfig.RESPONSE_TYPE_XML) {
			listJacksonJsonObjectTypes.setEnabled(false);
		} else {
			if (val == SiriRestClientConfig.RESPONSE_TYPE_JSON) {
				listJacksonJsonObjectTypes.setEnabled(true);
			}
		}
	}
}
