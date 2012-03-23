package pt.up.beta.mobile.ui.services;

import pt.up.beta.mobile.sifeup.SessionManager;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
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
