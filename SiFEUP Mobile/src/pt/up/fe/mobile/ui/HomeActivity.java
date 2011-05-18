package pt.up.fe.mobile.ui;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.util.ByteArrayBuffer;

import pt.fe.up.mobile.service.SessionCookie;
import pt.fe.up.mobile.service.SifeupAPI;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class HomeActivity extends BaseActivity {
	   @SuppressWarnings("unused")
	private static final String TAG = "HomeActivity";

	    private SyncStatusUpdaterFragment mSyncStatusUpdaterFragment;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        AnalyticsUtils.getInstance(this).trackPageView("/Home");
	        testCookie();
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
	    // Simple function to test cookie storage TODO:remove this
	    private void testCookie(){
	    	InputStream in = null;
			String page = "";
			try {

				Log.e("Login and cookie",SessionCookie.getInstance().getCookie() );
				HttpsURLConnection httpConn = LoginActivity.getUncheckedConnection(SifeupAPI.getStudentUrl("080503281"));
				
				//This sets the cookie 
				httpConn.setRequestProperty("Cookie", SessionCookie.getInstance().getCookie());
				httpConn.connect();
				in = httpConn.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(in);
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int read = 0;
				int bufSize = 512;
				byte[] buffer = new byte[bufSize];
				while ( true ) {
					read = bis.read( buffer );
					if( read == -1 ){
						break;
					}
					baf.append(buffer, 0, read);
				}
				page = new String(baf.toByteArray());
				bis.close();
				in.close();
				httpConn.disconnect();
				Log.e("Cookie Test", page);
				
			} catch (MalformedURLException e) {
			 // DEBUG
			 Log.e("DEBUG url exceptop: ", e.toString());
			} catch (IOException e) {
			 // DEBUG
			 Log.e("DEBUG: ioexcep ", e.toString());
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