package pt.up.fe.mobile.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class StudentsSearchActivity extends BaseSinglePaneActivity {
	
	public static String query = "myQueryIsEmpty";
	
	/** Called when the activity is first created. */
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);
	    getActivityHelper().setupSubActivity();
	}

	@Override
	protected Fragment onCreatePane() {
		// get query from search bar  
        //Intent intent = getIntent();        
        //query = intent.getStringExtra(SearchManager.QUERY);
        //Log.e("APAPAPAP", StudentsSearchActivity.query);
		
		return new StudentsSearchFragment();
	}

}
