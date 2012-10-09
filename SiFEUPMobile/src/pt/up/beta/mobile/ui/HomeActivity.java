package pt.up.beta.mobile.ui;

import pt.up.beta.mobile.R;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.CanvasTransformer;

import external.com.google.android.apps.iosched.util.UIUtils;

public class HomeActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		SlidingMenu sm = getSlidingMenu();
		sm.setBehindScrollScale(0.0f);
		sm.setBehindCanvasTransformer(
				//Zoom effect
				new CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen * 0.25 + 0.75);
				canvas.scale(scale, scale, canvas.getWidth() / 2,
						canvas.getHeight() / 2);
			}
		});
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// NOTE: there needs to be a content view set before this is called, so
		// this method
		// should be called in onPostCreate.
		if (UIUtils.isTablet(this))
			actionbar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_HOME
					| ActionBar.DISPLAY_SHOW_TITLE);
		else
			actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE
					| ActionBar.DISPLAY_SHOW_HOME);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getSupportMenuInflater().inflate(R.menu.home_menu_items, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_logout:

			final Intent upIntent = new Intent(this, LauncherActivity.class)
					.putExtra(LauncherActivity.LOGOUT_FLAG, true);

			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				// This activity is not part of the application's task, so
				// create a
				// new task
				// with a synthesized back stack.
				TaskStackBuilder.create(this).addNextIntent(upIntent)
						.startActivities();
				finish();
			} else {
				// This activity is part of the application's task, so simply
				// navigate up to the hierarchical parent activity.
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(upIntent);
					finish();
				} else
					NavUtils.navigateUpTo(this, upIntent);
			}
			overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}