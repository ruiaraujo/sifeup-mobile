package pt.up.beta.mobile.ui.services;

import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class ServicesActivity extends BaseSinglePaneActivity {

	@Override
    protected Fragment onCreatePane() {
		final String type = AccountUtils.getActiveUserType(getApplicationContext());
		if ( type.equals(SifeupAPI.STUDENT_TYPE) )
			return new StudentServicesFragment();
		else
			return new EmployeeServicesFragment();
    }
	

}
