
package pt.up.mobile.ui.subjects;


import pt.up.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class EnrolledStudentsActivity extends BaseSinglePaneActivity {
	@Override
    protected Fragment onCreatePane() {
    	
        return new EnrolledStudentsFragment();
    }


	

}
