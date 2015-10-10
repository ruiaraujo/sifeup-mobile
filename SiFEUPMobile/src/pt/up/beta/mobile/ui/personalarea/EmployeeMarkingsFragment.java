package pt.up.beta.mobile.ui.personalarea;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.datatypes.EmployeeMarkings;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.EmployeeUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.ui.BaseLoaderFragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class EmployeeMarkingsFragment extends BaseLoaderFragment implements
		ResponseCommand<EmployeeMarkings[]> {

	/** Contains all subscribed subjects */
	private EmployeeMarkings[] markings;

	private ViewPager viewPager;
	private TitlePageIndicator indicator;
	private LayoutInflater mInflater;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mInflater = inflater;
		View root = inflateMainScreen(R.layout.fragment_view_pager);
		viewPager = (ViewPager) root.findViewById(R.id.pager_menu);
		viewPager.setAdapter(new PagerCourseAdapter());
		// Find the indicator from the layout
		return getParentContainer();// mandatory
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		task = EmployeeUtils.getEmployeeMarkingsReply(
				AccountUtils.getActiveUserCode(getActivity()), this,
				getActivity());
	}


	@Override
	protected void onRepeat() {
		super.onRepeat();
		task = EmployeeUtils.getEmployeeMarkingsReply(
				AccountUtils.getActiveUserCode(getActivity()), this,
				getActivity());

	}

	@Override
	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error),
					Toast.LENGTH_LONG).show();
			finish();
			break;
		case NETWORK:
			showRepeatTaskScreen(getString(R.string.toast_server_error));
			break;
		default:
			showEmptyScreen(getString(R.string.general_error));
			break;
		}
	}

	@Override
	public void onResultReceived(EmployeeMarkings[] results) {
		if (getActivity() == null)
			return;
		markings = results;
		if (markings.length == 0) {
			showEmptyScreen(getString(R.string.lb_no_courses));
			return;
		}
		viewPager.setAdapter(new PagerCourseAdapter());

		setRefreshActionItemState(false);
		showMainScreen();
	}

	class PagerCourseAdapter extends PagerAdapter {

		@Override
		public CharSequence getPageTitle(int position) {
			return markings[position].getDate();
		}

		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((View) view);

		}

		public int getCount() {
			return markings.length;
		}

		public Object instantiateItem(View collection, int position) {
			
			//((ViewPager) collection).addView(list, 0);
			return null;
		}

		public boolean isViewFromObject(View view, Object object) {
			return view == ((View) object);
		}

		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		public Parcelable saveState() {
			return null;
		}

		public void startUpdate(View arg0) {
		}

		public void finishUpdate(View arg0) {
		}

	}

}
