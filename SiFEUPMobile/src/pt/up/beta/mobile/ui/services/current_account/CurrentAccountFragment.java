package pt.up.beta.mobile.ui.services.current_account;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.PaymentTypology;
import pt.up.beta.mobile.loaders.TuitionLoader;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.beta.mobile.syncadapter.SigarraSyncAdapterUtils;
import pt.up.beta.mobile.ui.BaseLoaderFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class CurrentAccountFragment extends BaseLoaderFragment implements
		LoaderCallbacks<PaymentTypology[]>, OnItemClickListener {

	private PaymentTypology[] typologies;

	private ViewPager viewPager;
	private LayoutInflater mInflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mInflater = inflater;
		View root = inflateMainScreen(R.layout.fragment_view_pager);
		viewPager = (ViewPager) root.findViewById(R.id.pager_menu);
		return getParentContainer();// mandatory
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().getSupportLoaderManager().initLoader(0, null, this);
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.refresh_menu_items, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh) {
			setRefreshActionItemState(true);
			SigarraSyncAdapterUtils.syncTuitions(AccountUtils
					.getActiveUserName(getActivity()));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onRepeat() {
		super.onRepeat();
		setRefreshActionItemState(true);
		SigarraSyncAdapterUtils.syncTuitions(AccountUtils
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

	@Override
	public Loader<PaymentTypology[]> onCreateLoader(int loaderId, Bundle options) {
		return new TuitionLoader(getActivity(),
				SigarraContract.Tuition.CONTENT_URI,
				SigarraContract.Tuition.COLUMNS,
				SigarraContract.Tuition.PROFILE,
				SigarraContract.Tuition.getTuitionSelectionArgs(AccountUtils
						.getActiveUserName(getActivity())), null);
	}

	@Override
	public void onLoadFinished(Loader<PaymentTypology[]> loader,
			PaymentTypology[] results) {
		if (getActivity() == null || results == null)
			return;
		typologies = results;
		viewPager.setAdapter(new PagerTypologiesAdapter());
		setRefreshActionItemState(false);
		showMainScreen();
	}

	@Override
	public void onLoaderReset(Loader<PaymentTypology[]> loader) {
	}

	class PagerTypologiesAdapter extends PagerAdapter {

		@Override
		public CharSequence getPageTitle(int position) {
			return typologies[position].getName();
		}

		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((View) view);

		}

		public int getCount() {
			return typologies.length;
		}

		public Object instantiateItem(View collection, int position) {
			ListView list = (ListView) mInflater.inflate(R.layout.generic_list,
					viewPager, false);
			list.setAdapter(new TypologyAdapter(getActivity(),
					typologies[position].getMovements()));
			list.setOnItemClickListener(CurrentAccountFragment.this);
			((ViewPager) collection).addView(list, 0);
			return list;
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

	@Override
	public void onItemClick(AdapterView<?> list, View view, int position,
			long id) {
		startActivity(new Intent(getActivity(), MovementDetailActivity.class)
				.putExtra(
						MovementDetailFragment.REFERENCE,
						typologies[viewPager.getCurrentItem()].getMovements()[position]));
	}

}
