package pt.up.fe.mobile.ui.studentservices;

import pt.up.fe.mobile.datatypes.User;
import pt.up.fe.mobile.sifeup.SessionManager;
import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class SiFEUPServicesActivity extends BaseSinglePaneActivity {

	@Override
    protected Fragment onCreatePane() {
		final User user = SessionManager.getInstance().getUser();
		if ( user.getType().equals("A") )
			return new StudentServicesFragment();
		else
			return new EmployeeServicesFragment();
    }
	

}
