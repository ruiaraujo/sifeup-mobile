package pt.up.beta.mobile.ui.personalarea;

import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

/**
 * Subjects Activity
 * 
 * @author Ângela Igreja
 *
 */
public class SubjectsActivity extends BaseSinglePaneActivity {
    
	@Override
	protected Fragment onCreatePane() {
		return new SubjectsFragment();
	}

}