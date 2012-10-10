package pt.up.beta.mobile.ui;

import pt.up.beta.mobile.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * @author Rui Araújo
 * 
 */
public class BaseListFragment extends SherlockListFragment implements FragmentOpener{

	public final static String URL_INTENT = "pt.up.fe.mobile.ui.webclient.URL";
	protected final static String DIALOG = "dialog";
	private final static FragmentOpener sDummy = new FragmentOpener() {
		@Override
		public void openFragment(
				@SuppressWarnings("rawtypes") Class fragmentClass,
				Bundle arguments, CharSequence title) {
		}
	};

	private FragmentOpener callback = sDummy;



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Recovering the Cookie here
		// as every activity will descend from this one.
		// TODO: we need to improve this
		// if (!AccountUtils.isAccountValid(getActivity()))
		// goLogin();
	}


	@Override
	public void onDetach() {
		super.onDetach();
		setCallback(sDummy);
	}

	public void goLogin() {
		if (getActivity() == null)
			return;
		final Intent i = new Intent(getActivity(), LauncherActivity.class);
		i.putExtra(LauncherActivity.LOGOUT_FLAG, true).addFlags(
				Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		getActivity().finish();
		getActivity().overridePendingTransition(R.anim.home_enter,
				R.anim.home_exit);
	}

	protected void removeDialog(String dialog) { // DialogFragment.show() will
													// take care of adding the
													// fragment
		// in a transaction. We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag(dialog);
		if (prev != null) {
			ft.remove(prev).commitAllowingStateLoss();
		}
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

	public FragmentOpener getCallback() {
		return callback;
	}

	public void setCallback(FragmentOpener callback) {
		this.callback = callback;
	}

	@Override
	public void openFragment(@SuppressWarnings("rawtypes") Class fragmentClass, Bundle arguments,
			CharSequence title) {
		callback.openFragment(fragmentClass, arguments, title);
	}

}
