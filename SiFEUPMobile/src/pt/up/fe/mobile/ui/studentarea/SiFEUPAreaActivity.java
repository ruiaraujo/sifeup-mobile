package pt.up.fe.mobile.ui.studentarea;

import pt.up.fe.mobile.datatypes.User;
import pt.up.fe.mobile.sifeup.SessionManager;
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
		final User user = SessionManager.getInstance().getUser();
		if ( user.getType().equals("A") )
			return new StudentAreaFragment();
		else
			return new EmployeeAreaFragment();
    }

	
}
