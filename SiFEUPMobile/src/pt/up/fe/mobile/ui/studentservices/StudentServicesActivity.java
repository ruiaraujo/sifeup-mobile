package pt.up.fe.mobile.ui.studentservices;

import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class StudentServicesActivity extends BaseSinglePaneActivity {

	@Override
    protected Fragment onCreatePane() {
        return new StudentServicesFragment();
    }
	

}
