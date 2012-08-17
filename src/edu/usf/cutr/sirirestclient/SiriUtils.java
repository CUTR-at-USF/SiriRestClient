package edu.usf.cutr.sirirestclient;

/**
 * Java imports
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Android imports
 */
import android.content.res.Resources.NotFoundException;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * This class holds utility methods for the application
 * 
 * @author Sean J. Barbeau
 * 
 */
public class SiriUtils {

	/**
	 * Try to grab the developer key from an unversioned resource file, if it
	 * exists
	 * 
	 * @return the developer key from an unversioned resource file, or empty
	 *         string if it doesn't exist
	 */
	public static String getKeyFromResource(Fragment fragment) {
		String strKey = new String("");

		try {
			InputStream in = fragment.getResources().openRawResource(R.raw.devkey);
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			StringBuilder total = new StringBuilder();

			while ((strKey = r.readLine()) != null) {
				total.append(strKey);
			}

			strKey = total.toString();

			strKey.trim(); // Remove any whitespace

		} catch (NotFoundException e) {
			Log.w(SiriRestClientActivity.TAG,
					"Warning - didn't find the developer key file:" + e);
		} catch (IOException e) {
			Log.w(SiriRestClientActivity.TAG,
					"Error reading the developer key file:" + e);
		}

		return strKey;
	}

}
