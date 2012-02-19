
package pt.up.fe.mobile.ui.studentservices.tuition;


import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class TuitionRefDetailActivity extends BaseSinglePaneActivity {
	@Override
    protected Fragment onCreatePane() {
    	
        return new TuitionRefDetailFragment();
    }


}
