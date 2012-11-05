package pt.up.beta.mobile.ui.personalarea;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
import pt.up.beta.mobile.ui.LauncherActivity;
import android.content.Intent;
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
		if (type.equals(SifeupAPI.STUDENT_TYPE))
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
			Intent i = new Intent(this, LauncherActivity.class).putExtra(
					LauncherActivity.LOGOUT_FLAG, true).addFlags(
					Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_CLEAR_TASK
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


}
