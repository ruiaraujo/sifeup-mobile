package pt.up.fe.mobile.ui.map;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.tracker.GoogleAnalyticsSessionManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import external.com.google.android.apps.iosched.util.ActivityHelper;



/**
 * Class that creates the FEUP Map Activity  
 *
 * @author Ângela Igreja
 */
public class FeupMapActivity extends MapActivity {
	
	 private final ActivityHelper mActivityHelper = ActivityHelper.createInstance(this);
	 private MapView mapView; 
	 
	 private MapController mc;
	 
	 private  GeoPoint p;
	
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        getActivityHelper().setupActionBar(getTitle(), 0);
        

        final String customTitle = getIntent().getStringExtra(Intent.EXTRA_TITLE);
        getActivityHelper().setActionBarTitle(customTitle != null ? customTitle : getTitle());

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true); 
        mapView.displayZoomControls(true);
        
        mapView.setSatellite(true);
        
        mc = mapView.getController();
        
        //FEUP coordinates
        String coordinates[] = {"41.177992", "-8.595740"};
        double lat = Double.parseDouble(coordinates[0]);
        double lng = Double.parseDouble(coordinates[1]);
 
        p = new GeoPoint(
            (int) (lat * 1E6), 
            (int) (lng * 1E6));
 
        mc.animateTo(p);
        mc.setZoom(17); 
        mapView.invalidate();
        
        
    }
 
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Example of how to track a pageview event
        AnalyticsUtils.getInstance(getApplicationContext()).trackPageView(getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Purge analytics so they don't hold references to this activity
        GoogleAnalyticsTracker.getInstance().dispatch();

        // Need to do this for every activity that uses google analytics
        GoogleAnalyticsSessionManager.getInstance().decrementActivityCount();
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActivityHelper.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    } 

   @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return mActivityHelper.onKeyLongPress(keyCode, event) ||
                super.onKeyLongPress(keyCode, event);
    }
   
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (android.os.Build.VERSION.SDK_INT < 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
        }
        return mActivityHelper.onKeyDown(keyCode, event) ||
                super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mActivityHelper.onCreateOptionsMenu(menu) ||
        			super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mActivityHelper.onOptionsItemSelected(item) ||
        			super.onOptionsItemSelected(item);
    }
    

    /**
     * Returns the {@link ActivityHelper} object associated with this activity.
     */
    protected ActivityHelper getActivityHelper() {
        return mActivityHelper;
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
    }
}
