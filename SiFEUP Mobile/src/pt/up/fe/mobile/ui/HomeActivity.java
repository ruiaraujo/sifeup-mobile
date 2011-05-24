package pt.up.fe.mobile.ui;


import pt.up.fe.mobile.R;

import external.com.google.android.apps.iosched.util.AnalyticsUtils;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class HomeActivity extends BaseActivity {
	   @SuppressWarnings("unused")
	private static final String TAG = "HomeActivity";


	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        AnalyticsUtils.getInstance(this).trackPageView("/Home");

	        setContentView(R.layout.activity_home);
	        getActivityHelper().setupActionBar(null, 0);
	    }
	    
	    @Override
	    protected void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        getActivityHelper().setupHomeActivity();
	    }

	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        super.onCreateOptionsMenu(menu);
	        getMenuInflater().inflate(R.menu.home_menu_items, menu);
	        return true;
	    }

		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.menu_home:
				goLogin(true);
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}


}