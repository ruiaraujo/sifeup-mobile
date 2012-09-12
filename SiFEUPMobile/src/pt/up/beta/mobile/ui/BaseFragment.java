package pt.up.beta.mobile.ui;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.ui.utils.ImageDownloader;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * @author Rui Araújo
 * 
 */
public class BaseFragment extends SherlockFragment {

	public final static String URL_INTENT = "pt.up.fe.mobile.ui.webclient.URL";
	protected final static String DIALOG = "dialog";
	private static ImageDownloader imageDownloader;

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
		if (switcher.getCurrentView() == switcher.getChildAt(1))
			return;
		placeHolder = switcher.getChildAt(1);
		if (placeHolder != null) {
			if (switcher.getChildAt(1) != emptyScreen) {
				placeHolder = switcher.getChildAt(1);
				if (placeHolder != null) {
					switcher.removeViewAt(1);
					switcher.addView(emptyScreen, 1);

				}
			}
		}
		TextView text = (TextView) emptyScreen.findViewById(R.id.message);
		emptyScreen.findViewById(R.id.action).setVisibility(View.GONE);
		text.setText(message);
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
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		getActivity().overridePendingTransition(R.anim.slide_right_in,
				R.anim.slide_right_out);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Recovering the Cookie here
		// as every activity will descend from this one.
		if (!AccountUtils.isAccountValid(getActivity()))
			goLogin();
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

	public void goLogin() {
		if (getActivity() == null)
			return;
		final Intent i = new Intent(getActivity(), LauncherActivity.class);
		i.putExtra(LauncherActivity.LOGOUT_FLAG, true).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		getActivity().finish();
		getActivity().overridePendingTransition(R.anim.home_enter,
				R.anim.home_exit);
	}

	public static ImageDownloader getImagedownloader() {
		if (imageDownloader == null)
			imageDownloader = new ImageDownloader();
		return imageDownloader;
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
				BaseFragment.this.onRepeat();
			}
		});
		text.setText(message);
		switcher.showNext();
	}

	protected void onRepeat() {

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

}
