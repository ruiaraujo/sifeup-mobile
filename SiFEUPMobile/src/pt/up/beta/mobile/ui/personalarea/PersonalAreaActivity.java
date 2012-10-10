package pt.up.beta.mobile.ui.personalarea;

import pt.up.beta.mobile.sifeup.AccountUtils;
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
		final String type = AccountUtils.getActiveUserType(this);
		if ( type.equals(SifeupAPI.STUDENT_TYPE) ){
			final PersonalAreaFragment frag = new PersonalAreaFragment();
			frag.setCallback(this);
			return frag;
		}
		else
			return new EmployeeAreaFragment();
    }

	
}
