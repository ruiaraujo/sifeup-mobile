package pt.up.mobile.ui.personalarea;

import pt.up.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

/**
 * Subjects Activity
 * 
 * @author Ângela Igreja
 *
 */
public class EmployeeMarkingsActivity extends BaseSinglePaneActivity {
    
	@Override
	protected Fragment onCreatePane() {
		return new EmployeeMarkingsFragment();
	}

}
