package pt.up.beta.mobile.ui.personalarea;

import java.util.ArrayList;
import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.Canteen;
import pt.up.beta.mobile.datatypes.Dish;
import pt.up.beta.mobile.loaders.CanteenLoader;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.beta.mobile.syncadapter.SigarraSyncAdapterUtils;
import pt.up.beta.mobile.ui.BaseLoaderFragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TitlePageIndicator;

/**
 * Lunch Menu Fragment
 * 
 * @author Ângela Igreja
 * 
 */
public class LunchMenuFragment extends BaseLoaderFragment implements
		LoaderCallbacks<Canteen[]> {
	private final static String CANTEEN_KEY = "pt.up.fe.mobile.ui.studentarea.CANTEENS";

	private PagerMenuAdapter pagerAdapter;
	private ViewPager viewPager;
	private TitlePageIndicator indicator;
	private List<Canteen> canteens;
	private LayoutInflater mInflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mInflater = inflater;
		View root = inflateMainScreen(R.layout.fragment_view_pager);
		viewPager = (ViewPager) root.findViewById(R.id.pager_menu);

		// Find the indicator from the layout
		indicator = (TitlePageIndicator) root.findViewById(R.id.indicator_menu);
		return getParentContainer();// mandatory
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			canteens = savedInstanceState.getParcelableArrayList(CANTEEN_KEY);
			if (canteens == null)
				getActivity().getSupportLoaderManager().initLoader(0, null,
						this);
			else {
				buildPages();
				showMainScreen();
			}
		} else {
			getActivity().getSupportLoaderManager().initLoader(0, null, this);
		}
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.refresh_menu_items, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh) {
			setRefreshActionItemState(true);
			SigarraSyncAdapterUtils.syncCanteens(AccountUtils
					.getActiveUserName(getActivity()));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onRepeat() {
		super.onRepeat();
		setRefreshActionItemState(true);
		SigarraSyncAdapterUtils.syncCanteens(AccountUtils
				.getActiveUserName(getActivity()));

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

	/**
	 * Build Pages
	 */
	private void buildPages() {
		// Create our custom adapter to supply pages to the viewpager.
		pagerAdapter = new PagerMenuAdapter();

		viewPager.setAdapter(pagerAdapter);
		indicator.setViewPager(viewPager);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (canteens != null)
			outState.putParcelableArrayList(CANTEEN_KEY,
					(ArrayList<? extends Parcelable>) canteens);
	}

	/**
	 * Pager Menu Adapter
	 * 
	 * @author Ângela Igreja
	 * 
	 */
	class PagerMenuAdapter extends PagerAdapter {

		@Override
		public CharSequence getPageTitle(int position) {
			return canteens.get(position).getDescription();
		}

		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((View) view);

		}

		public int getCount() {
			return canteens.size();
		}

		public Object instantiateItem(View collection, int position) {
			View root = mInflater.inflate(R.layout.menu, viewPager, false);
			final String timetable = canteens.get(position).getTimetable();
			if (!TextUtils.isEmpty(timetable)) {
				TextView schedule = (TextView) root
						.findViewById(R.id.canteen_schedule);
				schedule.setText(Html.fromHtml(getString(
						R.string.canteen_schedule, timetable)));
			}
			ExpandableListView list = (ExpandableListView) root
					.findViewById(R.id.menu_list);
			list.setAdapter(new MenusAdapter(canteens.get(position)));
			list.expandGroup(0);
			((ViewPager) collection).addView(root, 0);
			return root;
		}

		public boolean isViewFromObject(View view, Object object) {
			return view == ((View) object);
		}

		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			indicator.setViewPager(viewPager);
		}

		public Parcelable saveState() {
			return null;
		}

		public void startUpdate(View arg0) {
		}

		public void finishUpdate(View arg0) {
		}

	}

	private class MenusAdapter extends BaseExpandableListAdapter {

		private final Canteen canteen;

		public MenusAdapter(Canteen c) {
			canteen = c;
		}

		public Object getChild(int groupPosition, int childPosition) {
			return canteen.getDish(groupPosition, childPosition);
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			if (canteen.isClosed(groupPosition))
				return 1;
			return canteen.getDishesCount(groupPosition);
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = mInflater.inflate(R.layout.list_item_menu_dish,
						null);
			Dish dish = (Dish) getChild(groupPosition, childPosition);
			TextView description = (TextView) convertView
					.findViewById(R.id.dish_description);
			TextView type = (TextView) convertView
					.findViewById(R.id.dish_description_type);
			if (canteen.isClosed(groupPosition)) {
				description.setText(R.string.lb_closed_canteen);
				type.setVisibility(View.GONE);
			} else {
				if (!TextUtils.isEmpty(dish.getDescription()))
					description.setText(dish.getDescription());
				else
					description.setText(dish.getState());
				type.setVisibility(View.VISIBLE);
				type.setText(dish.getDescriptionType());
			}
			return convertView;
		}

		public Object getGroup(int groupPosition) {
			return canteen.getDate(groupPosition);
		}

		public int getGroupCount() {
			return canteen.getMenuCount();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// Layout parameters for the ExpandableListView
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, 84);

			TextView textView = new TextView(getActivity());
			textView.setLayoutParams(lp);
			// Center the text vertically
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			textView.setPadding(84, 0, 0, 0);
			textView.setText(getGroup(groupPosition).toString());
			return textView;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}

		public boolean hasStableIds() {
			return true;
		}

	}

	@Override
	public Loader<Canteen[]> onCreateLoader(int loaderId, Bundle options) {
		return new CanteenLoader(getActivity(),
				SigarraContract.Canteens.CONTENT_URI, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Canteen[]> loader, Canteen[] canteens) {
		if (getActivity() == null || canteens == null)
			return;
		this.canteens = new ArrayList<Canteen>();
		for (Canteen canteen : canteens) {
			if (canteen.getMenus().length > 0)
				this.canteens.add(canteen);
		}
		if (this.canteens.isEmpty()) {
			showEmptyScreen(getString(R.string.lb_no_menu));
			return;
		}
		buildPages();
		setRefreshActionItemState(false);
		showMainScreen();
	}

	@Override
	public void onLoaderReset(Loader<Canteen[]> loader) {
	}

}
