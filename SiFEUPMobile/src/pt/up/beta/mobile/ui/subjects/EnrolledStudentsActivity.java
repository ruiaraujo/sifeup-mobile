
package pt.up.beta.mobile.ui.subjects;


import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class EnrolledStudentsActivity extends BaseSinglePaneActivity {
	@Override
    protected Fragment onCreatePane() {
    	
        return new EnrolledStudentsFragment();
    }


	

}
