package pt.up.fe.mobile.ui.services;

import pt.up.fe.mobile.sifeup.SessionManager;
import pt.up.fe.mobile.sifeup.SifeupAPI;
import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class ServicesActivity extends BaseSinglePaneActivity {

	@Override
    protected Fragment onCreatePane() {
		final String type = SessionManager.getInstance(this).getUser().getType();
		if ( type.equals(SifeupAPI.STUDENT_TYPE) )
			return new StudentServicesFragment();
		else
			return new EmployeeServicesFragment();
    }
	

}
