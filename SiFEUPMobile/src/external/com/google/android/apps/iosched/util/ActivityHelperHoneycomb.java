package external.com.google.android.apps.iosched.util;


import pt.up.fe.mobile.R;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * An extension of {@link ActivityHelper} that provides Android 3.0-specific functionality for
 * Honeycomb tablets. It thus requires API level 11.
 */
public class ActivityHelperHoneycomb extends ActivityHelper {
    private Menu mOptionsMenu;

    protected ActivityHelperHoneycomb(Activity activity) {
        super(activity);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        // Do nothing in onPostCreate. ActivityHelper creates the old action bar, we don't
        // need to for Honeycomb.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Handle the HOME / UP affordance. Since the app is only two levels deep
                // hierarchically, UP always just goes home.
                goHome();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** {@inheritDoc} */
    @Override
    public void setupHomeActivity() {
        super.setupHomeActivity();
        final ActionBar actionbar = mActivity.getActionBar();
        // NOTE: there needs to be a content view set before this is called, so this method
        // should be called in onPostCreate.
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME);
    }

    /** {@inheritDoc} */
    @Override
    public void setupSubActivity() {
        super.setupSubActivity();
        final ActionBar actionbar = mActivity.getActionBar();
        // NOTE: there needs to be a content view set before this is called, so this method
        // should be called in onPostCreate.
        actionbar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP|ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME);

    }

    /**
     * No-op on Honeycomb. The action bar title always remains the same.
     */
    @Override
    public void setActionBarTitle(CharSequence title) {
        final ActionBar actionbar = mActivity.getActionBar();
        actionbar.setTitle(title);
    }

    /**
     * No-op on Honeycomb. The action bar color always remains the same.
     */
    @Override
    public void setActionBarColor(int color) {
        if (!UIUtils.isTablet(mActivity)) {
            super.setActionBarColor(color);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setRefreshActionButtonCompatState(boolean refreshing) {
        // On Honeycomb, we can set the state of the refresh button by giving it a custom
        // action view.
        if (mOptionsMenu == null) {
            return;
        }

        final MenuItem refreshItem = mOptionsMenu.findItem(R.id.menu_refresh);
        if (refreshItem != null) {
            if (refreshing) {
                refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
            } else {
                refreshItem.setActionView(null);
            }
        }
    }
}
