package pt.up.fe.mobile.ui;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.sifeup.SessionManager;
import pt.up.fe.mobile.sifeup.SifeupUtils;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.tracker.GoogleAnalyticsSessionManager;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

/**
 * A base activity that defers common functionality across app activities to an
 * {@link ActivityHelper}. This class shouldn't be used directly; instead,
 * activities should inherit from {@link BaseSinglePaneActivity} or
 * {@link BaseMultiPaneActivity}.
 */
public abstract class BaseActivity extends FragmentActivity {
    protected ActionBar actionbar;
    protected void onCreate(Bundle o) {
        super.onCreate(o);
        GoogleAnalyticsSessionManager.getInstance(getApplication())
                .incrementActivityCount();
        actionbar = getSupportActionBar();
        }

    @Override
    protected void onResume() {
        super.onResume();
        // Recovering the Cookie here
        // as every activity will descend from this one.
        if (SessionManager.getInstance().getCookie() != null) {

            if (!SifeupUtils.checkCookie(this))
                goLogin(LoginActivity.EXTRA_DIFFERENT_LOGIN_REVALIDATE);
        }
        // Example of how to track a pageview event
        AnalyticsUtils.getInstance(getApplicationContext()).trackPageView(
                getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Purge analytics so they don't hold references to this activity
        GoogleAnalyticsTracker.getInstance().dispatch();

        // Need to do this for every activity that uses google analytics
        GoogleAnalyticsSessionManager.getInstance().decrementActivityCount();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // NOTE: there needs to be a content view set before this is called, so this method
        // should be called in onPostCreate.
        actionbar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME);

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
            goHome();
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
            case R.id.menu_search:
                startSearch(null, false, Bundle.EMPTY, false);
                return true;
            case android.R.id.home:
                // Handle the HOME / UP affordance. Since the app is only two levels deep
                // hierarchically, UP always just goes home.
                goHome();
                return true;
        }   
        return super.onOptionsItemSelected(item);
    }
    /**
     * Invoke "home" action, returning to {@link com.google.android.apps.iosched.ui.HomeActivity}.
     */
    public void goHome() {
        if (this instanceof HomeActivity) {
            return;
        }

        final Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
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
    public void openActivityOrFragment(Intent intent) {
        // Default implementation simply calls startActivity
        startActivity(intent);
    }

    /**
     * Converts an intent into a {@link Bundle} suitable for use as fragment
     * arguments.
     * 
     * @param intent
     * @return
     */
    public static Bundle intentToFragmentArguments(Intent intent) {
        Bundle arguments = new Bundle();
        if (intent == null) {
            return arguments;
        }

        final Uri data = intent.getData();
        if (data != null) {
            arguments.putParcelable("_uri", data);
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
     * @return
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

    public static final int DIALOG_FETCHING = 3000;

    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_FETCHING: {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.lb_data_fetching));
            progressDialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    removeDialog(DIALOG_FETCHING);
                    finish();
                }
            });
            progressDialog.setIndeterminate(false);
            return progressDialog;
        }
        }
        return null;
    }

    public void goLogin() {
        goLogin(0);
    }

    /**
     * Starts the login activity. the param is used for the login activity to
     * know whether it should start logging in as soon as it is starts or not.
     * 
     * @param logOff
     */
    public void goLogin(final int logOff) {
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra(LoginActivity.EXTRA_DIFFERENT_LOGIN, logOff);
        startActivity(i);
        if (logOff == 0)
            finish();
        overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
    }

}
