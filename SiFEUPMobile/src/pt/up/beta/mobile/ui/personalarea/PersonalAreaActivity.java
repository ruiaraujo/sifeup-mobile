package pt.up.beta.mobile.ui.personalarea;

import pt.up.beta.mobile.sifeup.SessionManager;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

/**
 * Student Area Activity
 * 
 * @author Ângela Igreja
 *
 */
public class PersonalAreaActivity extends BaseSinglePaneActivity {

	@Override
    protected Fragment onCreatePane() {
		final String type = SessionManager.getInstance(this).getUser().getType();
		if ( type.equals(SifeupAPI.STUDENT_TYPE) )
			return new StudentAreaFragment();
		else
			return new EmployeeAreaFragment();
    }

	
}
