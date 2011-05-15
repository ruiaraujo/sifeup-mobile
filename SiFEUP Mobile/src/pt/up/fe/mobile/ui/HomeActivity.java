package pt.up.fe.mobile.ui;

import pt.up.fe.mobile.R;

import com.google.android.apps.iosched.service.SyncService;
import com.google.android.apps.iosched.ui.BaseActivity;
import com.google.android.apps.iosched.util.AnalyticsUtils;
import com.google.android.apps.iosched.util.DetachableResultReceiver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class HomeActivity extends BaseActivity {
	   private static final String TAG = "HomeActivity";

	    private SyncStatusUpdaterFragment mSyncStatusUpdaterFragment;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        AnalyticsUtils.getInstance(this).trackPageView("/Home");

	        setContentView(R.layout.activity_home);
	        getActivityHelper().setupActionBar(null, 0);

	        FragmentManager fm = getSupportFragmentManager();


	        mSyncStatusUpdaterFragment = (SyncStatusUpdaterFragment) fm
	                .findFragmentByTag(SyncStatusUpdaterFragment.TAG);
	        if (mSyncStatusUpdaterFragment == null) {
	            mSyncStatusUpdaterFragment = new SyncStatusUpdaterFragment();
	            fm.beginTransaction().add(mSyncStatusUpdaterFragment,
	                    SyncStatusUpdaterFragment.TAG).commit();

	            triggerRefresh();
	        }
	    }


	    @Override
	    protected void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        getActivityHelper().setupHomeActivity();
	    }

	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.refresh_menu_items, menu);
	        super.onCreateOptionsMenu(menu);
	        return true;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        if (item.getItemId() == R.id.menu_refresh) {
	            triggerRefresh();
	            return true;
	        }
	        return super.onOptionsItemSelected(item);
	    }

	    private void triggerRefresh() {
	        final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SyncService.class);
	        intent.putExtra(SyncService.EXTRA_STATUS_RECEIVER, mSyncStatusUpdaterFragment.mReceiver);
	        startService(intent);

	       
	    }

	    private void updateRefreshStatus(boolean refreshing) {
	        getActivityHelper().setRefreshActionButtonCompatState(refreshing);
	    }

	    /**
	     * A non-UI fragment, retained across configuration changes, that updates its activity's UI
	     * when sync status changes.
	     */
	    public static class SyncStatusUpdaterFragment extends Fragment
	            implements DetachableResultReceiver.Receiver {
	        public static final String TAG = SyncStatusUpdaterFragment.class.getName();

	        private boolean mSyncing = false;
	        private DetachableResultReceiver mReceiver;

	        @Override
	        public void onCreate(Bundle savedInstanceState) {
	            super.onCreate(savedInstanceState);
	            setRetainInstance(true);
	            mReceiver = new DetachableResultReceiver(new Handler());
	            mReceiver.setReceiver(this);
	        }

	        /** {@inheritDoc} */
	        public void onReceiveResult(int resultCode, Bundle resultData) {
	            HomeActivity activity = (HomeActivity) getActivity();
	            if (activity == null) {
	                return;
	            }

	            switch (resultCode) {
	                case SyncService.STATUS_RUNNING: {
	                    mSyncing = true;
	                    break;
	                }
	                case SyncService.STATUS_FINISHED: {
	                    mSyncing = false;
	                    break;
	                }
	                case SyncService.STATUS_ERROR: {
	                    // Error happened down in SyncService, show as toast.
	                    mSyncing = false;
	                    final String errorText = getString(R.string.toast_sync_error, resultData
	                            .getString(Intent.EXTRA_TEXT));
	                    Toast.makeText(activity, errorText, Toast.LENGTH_LONG).show();
	                    break;
	                }
	            }

	            activity.updateRefreshStatus(mSyncing);
	        }

	        @Override
	        public void onActivityCreated(Bundle savedInstanceState) {
	            super.onActivityCreated(savedInstanceState);
	            ((HomeActivity) getActivity()).updateRefreshStatus(mSyncing);
	        }
	    }
}