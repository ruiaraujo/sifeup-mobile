package pt.up.fe.mobile.ui.studentarea;

import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Student Area Activity
 * 
 * @author Ângela Igreja
 *
 */
public class StudentAreaActivity extends BaseSinglePaneActivity {

	@Override
    protected Fragment onCreatePane() {
        return new StudentAreaFragment();
    }
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    } 
	
}
