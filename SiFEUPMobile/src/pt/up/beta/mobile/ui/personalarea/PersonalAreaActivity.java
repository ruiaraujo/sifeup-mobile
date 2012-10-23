package pt.up.beta.mobile.ui.personalarea;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Student Area Activity
 * 
 * @author Ângela Igreja
 *
 */
public class PersonalAreaActivity extends BaseSinglePaneActivity {

	@Override
    protected Fragment onCreatePane() {
		final String type = AccountUtils.getActiveUserType(this);
		if ( type.equals(SifeupAPI.STUDENT_TYPE) )
			return new StudentAreaFragment();
		else
			return new EmployeeAreaFragment();
    }
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.logout_menu_items, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_logout:
			goLogin();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
}
