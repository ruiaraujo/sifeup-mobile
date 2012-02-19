package pt.up.fe.mobile.ui.studentservices.tuition;


import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class TuitionRefListActivity extends BaseSinglePaneActivity {
	
    protected Fragment onCreatePane() {
    	return new TuitionRefListFragment();
    }
}

