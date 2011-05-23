package pt.up.fe.mobile.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class StudentActivity extends BaseSinglePaneActivity {
	
	protected Fragment onCreatePane() {
    	
        return new StudentFragment();
    }
	
	/** Called when the activity is first created. */
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		 super.onPostCreate(savedInstanceState);
	     getActivityHelper().setupSubActivity();
	}

}
