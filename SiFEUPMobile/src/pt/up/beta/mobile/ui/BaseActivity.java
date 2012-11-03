package pt.up.beta.mobile.ui;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.ui.personalarea.PersonalAreaActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.KeyEvent;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import external.com.google.android.apps.iosched.util.UIUtils;

/**
 * A base activity that defers common functionality across app activities. This
 * class shouldn't be used directly; instead, activities should inherit from
 * {@link BaseSinglePaneActivity} or {@link BaseMultiPaneActivity}.
 */
public abstract class BaseActivity extends SlidingFragmentActivity {
	protected ActionBar actionbar;

	private Handler mHandler = new Handler();

	public void onCreate(Bundle o) {
		super.onCreate(o);
		// GoogleAnalyticsSessionManager.getInstance(getApplication())
		// .incrementActivityCount();
		actionbar = getSupportActionBar();

		if (!UIUtils.isTablet(getApplicationContext())) {
			// set the Behind View
			setBehindContentView(R.layout.menu_frame);
			FragmentTransaction t = this.getSupportFragmentManager()
					.beginTransaction();
			t.replace(R.id.menu_frame, new MenuFragment());
			t.commit();

			// customize the SlidingMenu
			final SlidingMenu sm = getSlidingMenu();
			sm.setShadowWidthRes(R.dimen.shadow_width);
			sm.setShadowDrawable(R.drawable.shadow);
			sm.setBehindOffsetRes(R.dimen.actionbar_home_width);
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

			setSlidingActionBarEnabled(false);
		}
		else{
			setBehindContentView(new View(getApplicationContext()));
			getSlidingMenu().setSlidingEnabled(false);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// TODO: we need to improve this
		// if (!AccountUtils.isAccountValid(this)) {
		// goLogin();
		// }
		// Example of how to track a pageview event
		// AnalyticsUtils.getInstance(getApplicationContext()).trackPageView(
		// getClass().getSimpleName());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Purge analytics so they don't hold references to this activity
		// GoogleAnalyticsSessionManager.getInstance().dispatch();

		// Need to do this for every activity that uses google analytics
		// GoogleAnalyticsSessionManager.getInstance().decrementActivityCount();
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// NOTE: there needs to be a content view set before this is called, so
		// this method
		// should be called in onPostCreate.
		actionbar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
				| ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (android.os.Build.VERSION.SDK_INT < 5
				&& keyCode == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0) {
			onBackPressed();
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			goUp();
			return true;
		}
		return super.onKeyLongPress(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.default_menu_items, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(this, Preferences.class));
			return true;
		case R.id.menu_search:
			startSearch(null, false, Bundle.EMPTY, false);
			return true;
		case android.R.id.home:
			if (UIUtils.isTablet(getApplicationContext()))
				goUp();
			else
				toggle();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Invoke "home" action, returning to {@link HomeActivity}.
	 */
	protected void goUp() {
		if (this instanceof PersonalAreaActivity) {
			return;
		}
		final Intent upIntent = new Intent(this, PersonalAreaActivity.class);
		if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
			// This activity is not part of the application's task, so create a
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
		overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
	}

	/**
	 * Takes a given intent and either starts a new activity to handle it (the
	 * default behavior), or creates/updates a fragment (in the case of a
	 * multi-pane activity) that can handle the intent.
	 * 
	 * Must be called from the main (UI) thread.
	 * 
	 * @param intent
	 */
	public void openActivityOrFragment(final Intent intent) {
		// Default implementation simply calls startActivity
		if (getSlidingMenu().isBehindShowing()) {
			// delay a bit to help prevent jankyness
			showAbove();
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					startActivity(intent);
					overridePendingTransition(R.anim.fade_in,
							android.R.anim.fade_out);
				}
			}, 200);
		} else {
			startActivity(intent);
			overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
		}
	}

	/**
	 * Converts an intent into a {@link Bundle} suitable for use as fragment
	 * arguments.
	 * 
	 * @param intent
	 * @return the bundle with the argument
	 */
	public static Bundle intentToFragmentArguments(Intent intent) {
		Bundle arguments = new Bundle();
		if (intent == null) {
			return arguments;
		}

		final Uri data = intent.getData();
		if (data != null) {
			arguments.putParcelable(BaseFragment.URL_INTENT, data);
		}

		final Bundle extras = intent.getExtras();
		if (extras != null) {
			arguments.putAll(intent.getExtras());
		}

		return arguments;
	}

	/**
	 * Converts a fragment arguments bundle into an intent.
	 * 
	 * @param arguments
	 * @return the argument in a intent
	 */
	public static Intent fragmentArgumentsToIntent(Bundle arguments) {
		Intent intent = new Intent();
		if (arguments == null) {
			return intent;
		}

		final Uri data = arguments.getParcelable("_uri");
		if (data != null) {
			intent.setData(data);
		}

		intent.putExtras(arguments);
		intent.removeExtra("_uri");
		return intent;
	}

	/**
	 * Starts the login activity. the param is used for the login activity to
	 * know whether it should start logging in as soon as it is starts or not.
	 * 
	 */
	public void goLogin() {
		Intent i = new Intent(this, LauncherActivity.class).putExtra(
				LauncherActivity.LOGOUT_FLAG, true).addFlags(
				Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
		overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
	}

	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out);
	}

}
