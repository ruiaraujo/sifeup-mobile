package pt.up.beta.mobile.ui;

import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.beta.mobile.syncadapter.SigarraSyncAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

public abstract class BaseLoaderFragment extends BaseFragment {
	@Override
	public void setRefreshActionItemState(boolean refreshing) {
		super.setRefreshActionItemState(refreshing);
		interestedInSyncAdapter = refreshing;
	}

	@Override
	protected void showLoadingScreen() {
		super.showLoadingScreen();
		interestedInSyncAdapter = true;
	}

	@Override
	protected void showMainScreen() {
		super.showMainScreen();
		interestedInSyncAdapter = false;
	}

	private LocalBroadcastManager broadcastManager;
	private SyncAdapterStatus syncAdapterStatus;
	private boolean interestedInSyncAdapter = true;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		broadcastManager = LocalBroadcastManager.getInstance(getActivity()
				.getApplicationContext());
		syncAdapterStatus = new SyncAdapterStatus();
	}

	@Override
	@TargetApi(11)
	public void onPause() {
		super.onPause();
		broadcastManager.unregisterReceiver(syncAdapterStatus);
	}

	@Override
	public void onResume() {
		super.onResume();
		broadcastManager.registerReceiver(syncAdapterStatus, new IntentFilter(
				SigarraSyncAdapter.SIGARRASYNCADAPTER_STATUS));
	}

	private class SyncAdapterStatus extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (!interestedInSyncAdapter)
				return;
			final String error = intent
					.getStringExtra(SigarraSyncAdapter.SIGARRASYNCADAPTER_STATUS);
			setRefreshActionItemState(false);
			if (SigarraSyncAdapter.GENERAL_ERROR.equals(error)) {
				onError(ResponseCommand.ERROR_TYPE.GENERAL);
			} else if (SigarraSyncAdapter.AUTHENTICATION_ERROR.equals(error)) {
				onError(ResponseCommand.ERROR_TYPE.AUTHENTICATION);
			} else if (SigarraSyncAdapter.NETWORK_ERROR.equals(error)) {
				onError(ResponseCommand.ERROR_TYPE.NETWORK);
			} else if (SigarraSyncAdapter.CANCELLED_ERROR.equals(error)) {
				onError(ResponseCommand.ERROR_TYPE.CANCELLED);
			}
		}

	}

	abstract public void onError(ERROR_TYPE error);
}
