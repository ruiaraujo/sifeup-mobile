package pt.up.beta.mobile.ui.facilities;

import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;


/**
 * Class that creates the FEUP Map Activity  
 *
 * @author Ângela Igreja
 */
public class FeupFacilitiesActivity extends BaseSinglePaneActivity {

	protected Fragment onCreatePane() {
		return new FeupFacilitiesFragment();
	}
	
	
}
