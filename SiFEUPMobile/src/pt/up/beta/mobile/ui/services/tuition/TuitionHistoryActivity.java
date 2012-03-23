package pt.up.beta.mobile.ui.services.tuition;


import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class TuitionHistoryActivity extends BaseSinglePaneActivity {
	
    protected Fragment onCreatePane() {
    	return new TuitionHistoryFragment();
    }

}

