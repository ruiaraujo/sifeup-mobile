
package pt.up.beta.mobile.ui.subjects;


import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class OtherOccurrencesActivity extends BaseSinglePaneActivity {
	@Override
    protected Fragment onCreatePane() {
    	
        return new OtherOccurrencesFragment();
    }


	

}
