package pt.up.fe.mobile.ui.studentarea;

import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Subject Content Activity
 * 
 * @author Ângela Igreja
 *
 */
public class SubjectContentActivity extends BaseSinglePaneActivity {

	public final static String SUBJECT_CODE = "pt.up.fe.mobile.ui.studentarea.SUBJECT_CODE"; 
	public final static String SUBJECT_YEAR = "pt.up.fe.mobile.ui.studentarea.SUBJECT_YEAR"; 
	public final static String SUBJECT_PERIOD = "pt.up.fe.mobile.ui.studentarea.SUBJECT_PERIOD"; 
	
	@Override
	protected Fragment onCreatePane() {
		return new SubjectContentFragment();
	}

	/** Called when the activity is first created. */
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);
	    getActivityHelper().setupSubActivity();
	}
}
