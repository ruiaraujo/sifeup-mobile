
package pt.up.beta.mobile.ui.services.tuition;


import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class TuitionRefDetailActivity extends BaseSinglePaneActivity {
	@Override
    protected Fragment onCreatePane() {
    	
        return new TuitionRefDetailFragment();
    }


}
