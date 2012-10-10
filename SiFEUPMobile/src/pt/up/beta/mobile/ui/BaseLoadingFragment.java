package pt.up.beta.mobile.ui;

import pt.up.beta.mobile.R;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * @author Rui Araújo
 * 
 */
public class BaseLoadingFragment extends BaseFragment{

	private ViewSwitcher switcher;
	private View emptyScreen;
	private View placeHolder; // to be used when the child view is
	// replaced by "emptyScreen" view.
	private LayoutInflater inflater;

	protected AsyncTask<?, ?, ?> task;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		switcher = (ViewSwitcher) inflater.inflate(R.layout.loading_view,
				container, false);
		emptyScreen = inflater.inflate(R.layout.fragment_no_results, switcher,
				false);
		this.inflater = inflater;
		return switcher;
	}

	protected void showLoadingScreen() {
		if (switcher.getCurrentView() != switcher.getChildAt(0))
			switcher.showNext();
	}

	protected void showMainScreen() {
		if (placeHolder != null) {
			switcher.removeViewAt(1);
			switcher.addView(placeHolder, 1);
			placeHolder = null;
		}
		if (switcher.getCurrentView() != switcher.getChildAt(1))
			switcher.showNext();
	}

	protected void showEmptyScreen(final String message) {
		if (switcher.getChildAt(1) != emptyScreen) {
			placeHolder = switcher.getChildAt(1);
			if (placeHolder != null) {
				switcher.removeViewAt(1);
				switcher.addView(emptyScreen, 1);
			}
		}

		TextView text = (TextView) emptyScreen.findViewById(R.id.message);
		emptyScreen.findViewById(R.id.action).setVisibility(View.GONE);
		text.setText(message);
		if (switcher.getCurrentView() == switcher.getChildAt(0))
			switcher.showNext();
	}

	protected View getEmptyScreen(final String message) {
		View emptyScreen = inflater.inflate(R.layout.fragment_no_results, null);
		TextView text = (TextView) emptyScreen.findViewById(R.id.message);
		text.setText(message);
		return emptyScreen;
	}

	protected ViewGroup getParentContainer() {
		if (switcher == null)
			throw new RuntimeException(
					"onCreateView must be called before from super");
		return switcher;
	}

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
	public void onDestroyView() {
		super.onDestroyView();
		if (task != null) {
			task.cancel(true);
		}
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

	protected void showRepeatTaskScreen(final String message) {
		if (switcher.getCurrentView() == switcher.getChildAt(1)) {
			switcher.showNext();
		}
		if (switcher.getChildAt(1) != emptyScreen) {
			placeHolder = switcher.getChildAt(1);
			if (placeHolder != null) {
				switcher.removeViewAt(1);
				switcher.addView(emptyScreen, 1);
			}
		}
		TextView text = (TextView) emptyScreen.findViewById(R.id.message);
		Button repeat = (Button) emptyScreen.findViewById(R.id.action);
		repeat.setVisibility(View.VISIBLE);
		repeat.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				BaseLoadingFragment.this.onRepeat();
			}
		});
		text.setText(message);
		switcher.showNext();
	}

	protected void onRepeat() {

	}

}
