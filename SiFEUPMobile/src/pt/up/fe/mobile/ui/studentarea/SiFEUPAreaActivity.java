package pt.up.fe.mobile.ui.studentarea;

import pt.up.fe.mobile.sifeup.SessionManager;
import pt.up.fe.mobile.sifeup.SifeupAPI;
import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

/**
 * Student Area Activity
 * 
 * @author Ângela Igreja
 *
 */
public class SiFEUPAreaActivity extends BaseSinglePaneActivity {

	@Override
    protected Fragment onCreatePane() {
		final String type = SessionManager.getInstance(this).getUser().getType();
		if ( type.equals(SifeupAPI.STUDENT_TYPE) )
			return new StudentAreaFragment();
		else
			return new EmployeeAreaFragment();
    }

	
}
