package edu.usf.cutr.sirirestclient;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.widget.Toast;

/**
 * This is a reference implementation for using the RESTful SIRI API from an android app.
 * 
 * This activity is the entry point for the app, which contains multiple fragments shown as tabs
 * using the Android action bar.
 * 
 * @author Sean Barbeau
 *
 */
public class SiriRestClientActivity extends Activity {
  
    public static String TAG = "SiriRestClientActivity";
        
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
        
        bar.setTitle(getApplicationContext().getText(R.string.app_name));
        
//        bar.addTab(bar.newTab()
//                .setText("Veh_Mon_Request")
//                .setTabListener(new TabListener<FragmentStack.CountingFragment>(
//                        this, "vehmon", FragmentStack.CountingFragment.class)));  
        bar.addTab(bar.newTab()
            .setText("Veh_Mon_Request")
            .setTabListener(new TabListener<SiriVehicleMonRequest>(
                    this, "vehmon", SiriVehicleMonRequest.class)));
        bar.addTab(bar.newTab()
            .setText("Stop_Mon_Request")
            .setTabListener(new TabListener<SiriStopMonRequest>(
                    this, "vehmon", SiriStopMonRequest.class)));
//        bar.addTab(bar.newTab()
//            .setText("Stop_Mon_Request")
//            .setTabListener(new TabListener<FragmentStack.CountingFragment>(
//                    this, "stopmon", FragmentStack.CountingFragment.class)));
//        bar.addTab(bar.newTab()
//                .setText("Apps")
//                .setTabListener(new TabListener<LoaderCustom.AppListFragment>(
//                        this, "apps", LoaderCustom.AppListFragment.class)));
//        bar.addTab(bar.newTab()
//                .setText("Throttle")
//                .setTabListener(new TabListener<LoaderThrottle.ThrottledLoaderListFragment>(
//                        this, "throttle", LoaderThrottle.ThrottledLoaderListFragment.class)));
        
        if (savedInstanceState != null) {
            bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());        
    }

    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        private final Bundle mArgs;
        private Fragment mFragment;

        public TabListener(Activity activity, String tag, Class<T> clz) {
            this(activity, tag, clz, null);
        }

        public TabListener(Activity activity, String tag, Class<T> clz, Bundle args) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
            mArgs = args;

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
            if (mFragment != null && !mFragment.isDetached()) {
                FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
                ft.detach(mFragment);
                ft.commit();
            }
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            if (mFragment == null) {
                mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                ft.attach(mFragment);
            }
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            Toast.makeText(mActivity, "Reselected!", Toast.LENGTH_SHORT).show();
        }
    }
}