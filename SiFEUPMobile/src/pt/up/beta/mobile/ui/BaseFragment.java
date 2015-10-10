package pt.up.beta.mobile.ui;

import pt.up.mobile.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Rui Araújo
 * 
 */
public class BaseFragment extends SherlockFragment {

	public final static String URL_INTENT = "pt.up.fe.mobile.ui.webclient.URL";
	protected final static String DIALOG = "dialog";
	private FrameLayout base;
	private View loadingScreen;
	private View emptyScreen;
	private View mainScreen; // to be used when the child view is
	// replaced by "emptyScreen" view.
	private LayoutInflater inflater;

	protected AsyncTask<?, ?, ?> task;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		this.inflater = inflater;
		base = (FrameLayout) inflater.inflate(R.layout.fragment_base,
				container, false);
		loadingScreen = inflater.inflate(R.layout.loading_view,
				base, false);
		base.addView(loadingScreen,0);
		return base;
	}

	protected View inflateMainScreen(int resource) {
		return mainScreen = inflater.inflate(resource, base, false);
	}

	protected void showLoadingScreen() {
		if (base.getChildAt(0) == loadingScreen)
			return;
		base.removeAllViews();
		base.addView(loadingScreen,0);
	}

	protected void showMainScreen() {
		if (base.getChildAt(0) == mainScreen)
			return;
		base.removeAllViews();
		base.addView(mainScreen,0);
	}

	protected void showEmptyScreen(final String message) {
		if (emptyScreen == null)
			emptyScreen = inflater.inflate(R.layout.fragment_no_results, base,
					false);
		TextView text = (TextView) emptyScreen.findViewById(R.id.message);
		emptyScreen.findViewById(R.id.action).setVisibility(View.GONE);
		text.setText(message);
		base.removeAllViews();
		base.addView(emptyScreen,0);
	}

	protected View getEmptyScreen(final String message) {
		View emptyScreen = inflater.inflate(R.layout.fragment_no_results, null);
		TextView text = (TextView) emptyScreen.findViewById(R.id.message);
		text.setText(message);
		return emptyScreen;
	}

	protected ViewGroup getParentContainer() {
		return base;
	}

	@Override
	public void startActivity(Intent intent) {
		final BaseActivity activity = (BaseActivity) getActivity();
		activity.openActivityOrFragment(intent);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (task != null) {
			task.cancel(true);
		}
	}

	@TargetApi(11)
	@Override
	public void onPause() {
		super.onPause();
	}

	public void finish() {
		if (getActivity() == null)
			return;
		getActivity().finish();
	}

	protected void showRepeatTaskScreen(final String message) {
		if (emptyScreen == null)
			emptyScreen = inflater.inflate(R.layout.fragment_no_results, base,
					false);
		TextView text = (TextView) emptyScreen.findViewById(R.id.message);
		Button repeat = (Button) emptyScreen.findViewById(R.id.action);
		repeat.setVisibility(View.VISIBLE);
		repeat.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				BaseFragment.this.onRepeat();
			}
		});
		text.setText(message);
		base.removeAllViews();
		base.addView(emptyScreen);
	}

	protected void onRepeat() {
		showLoadingScreen();
	}

	private final static int MSG_REMOVE_DIALOG = 0;

	private static class DialogHandler extends Handler {
		private final FragmentManager mFragmentManager;

		private DialogHandler(FragmentManager mFragmentManager) {
			this.mFragmentManager = mFragmentManager;
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_REMOVE_DIALOG) {
				// DialogFragment.show() will
				// take care of adding the
				// fragment
				// in a transaction. We also want to remove any currently
				// showing
				// dialog, so make our own transaction and take care of that
				// here.
				FragmentTransaction ft = mFragmentManager.beginTransaction();
				Fragment prev = mFragmentManager.findFragmentByTag(msg.obj
						.toString());
				if (prev != null) {
					ft.remove(prev).commitAllowingStateLoss();
				}
			}
		}
	}

	private DialogHandler handler;

	protected void removeDialog(String dialog) {
		if (handler == null)
			handler = new DialogHandler(getFragmentManager());
		handler.sendMessage(Message.obtain(handler, MSG_REMOVE_DIALOG, dialog));
	}

	public BaseActivity getBaseActivity() {
		if (getActivity() == null || !(getActivity() instanceof BaseActivity))
			return null;
		return ((BaseActivity) getActivity());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// first saving my state, so the bundle wont be empty.
		// http://code.google.com/p/android/issues/detail?id=19917
		outState.putString("WORKAROUND_FOR_BUG_19917_KEY",
				"WORKAROUND_FOR_BUG_19917_VALUE");
		super.onSaveInstanceState(outState);
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		mOptionsMenu = menu;
		super.onCreateOptionsMenu(menu, inflater);
	}

	private Menu mOptionsMenu;
	private View mRefreshIndeterminateProgressView = null;

	public void setRefreshActionItemState(boolean refreshing) {
		// On Honeycomb, we can set the state of the refresh button by giving it
		// a custom
		// action view.
		if (mOptionsMenu == null) {
			return;
		}

		final MenuItem refreshItem = mOptionsMenu.findItem(R.id.menu_refresh);
		if (refreshItem != null) {
			if (refreshing) {
				if (mRefreshIndeterminateProgressView == null) {
					LayoutInflater inflater = (LayoutInflater) getActivity()
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					mRefreshIndeterminateProgressView = inflater.inflate(
							R.layout.actionbar_indeterminate_progress, null);
				}

				refreshItem.setActionView(mRefreshIndeterminateProgressView);
			} else {
				refreshItem.setActionView(null);
			}
		}
	}

}
