package edu.usf.cutr.sirirestclient;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This is a reference implementation for using the RESTful SIRI API from an
 * Android app. This activity is the entry point for the app, which contains
 * multiple fragments shown as tabs using the Android action bar.
 * 
 * @author Sean J. Barbeau
 * 
 */
public class SiriRestClientActivity extends FragmentActivity implements
		ActionBar.TabListener {

	public static String TAG = "SiriRestClientActivity";

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Create the adapter that will return a fragment for each of the three
		// primary sections
		// of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setTitle(getApplicationContext().getText(R.string.app_name));

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab.
		// We can also use ActionBar.Tab#select() to do this if we have a
		// reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the listener for when this tab is
			// selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the primary sections of the app.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public static final int NUMBER_OF_TABS = 4; // Used to set up
													// TabListener

		// Constants for the different fragments that will be displayed in tabs
		public static final int VEH_REQUEST_FRAGMENT = 0;
		public static final int VEH_RESPONSE_FRAGMENT = 1;
		public static final int STOP_REQUEST_FRAGMENT = 2;
		public static final int STOP_RESPONSE_FRAGMENT = 3;

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {

			switch (i) {
			case VEH_REQUEST_FRAGMENT:
				// Vehicle Monitoring Request
				return new SiriVehicleMonRequestFragment();
			case VEH_RESPONSE_FRAGMENT:
				return new SiriVehicleMonRequestFragment();
			case STOP_REQUEST_FRAGMENT:
				// Stop Monitoring Request
				return new SiriStopMonRequestFragment();
			case STOP_RESPONSE_FRAGMENT:
				// Stop Monitoring Response
				return new SiriStopMonRequestFragment();
			}

			return null; // This should never happen
		}

		@Override
		public int getCount() {
			return NUMBER_OF_TABS;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				// Vehicle Monitoring Request
				return getString(R.string.vehicle_req_tab_title).toUpperCase();
			case 1:
				// Vehicle Monitoring Response
				return getString(R.string.vehicle_res_tab_title).toUpperCase();
			case 2:
				// Stop Monitoring Request
				return getString(R.string.stop_req_tab_title).toUpperCase();
			case 3:
				// Stop Monitoring Response
				return getString(R.string.stop_res_tab_title).toUpperCase();
			}
			return null;
		}
	}

}
