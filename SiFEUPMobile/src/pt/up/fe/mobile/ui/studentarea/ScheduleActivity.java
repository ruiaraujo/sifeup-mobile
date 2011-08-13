package pt.up.fe.mobile.ui.studentarea;

import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Schedule Activity
 * 
 * @author Ângela Igreja
 *
 */
public class ScheduleActivity extends BaseSinglePaneActivity {
	
	/** Called when the activity is first created. */
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
	}

	@Override
	protected Fragment onCreatePane() {
		return new ScheduleFragment();
	}
	
}
