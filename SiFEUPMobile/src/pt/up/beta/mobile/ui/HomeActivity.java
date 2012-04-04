package pt.up.beta.mobile.ui;


import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import external.com.google.android.apps.iosched.util.UIUtils;

import pt.up.beta.mobile.R;


import android.os.Bundle;


public class HomeActivity extends BaseActivity {
	   @SuppressWarnings("unused")
	private static final String TAG = "HomeActivity";


	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_home);
	    }
	    
	    @Override
	    protected void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        // NOTE: there needs to be a content view set before this is called, so this method
	        // should be called in onPostCreate.
	        if ( UIUtils.isTablet(this) )
	            actionbar.setDisplayOptions( 0, ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE); 
	        else
	            actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_USE_LOGO);

	    }

	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        super.onCreateOptionsMenu(menu);
	        getSupportMenuInflater().inflate(R.menu.home_menu_items,  menu);
	        return true;
	    }

		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.menu_home:
				goLogin();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}


}