package pt.up.fe.mobile.ui.studentarea;

import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Subject Description Activity
 * 
 * @author Ângela Igreja
 *
 */
public class SubjectDescriptionActivity extends BaseSinglePaneActivity {

	@Override
	protected Fragment onCreatePane() {
		return new SubjectDescriptionFragment();
	}

	/** Called when the activity is first created. */
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);
	    getActivityHelper().setupSubActivity();
	}
}
