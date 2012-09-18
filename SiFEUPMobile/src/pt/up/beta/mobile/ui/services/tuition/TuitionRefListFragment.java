package pt.up.beta.mobile.ui.services.tuition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import pt.up.beta.mobile.Constants;
import pt.up.beta.mobile.R;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.RefMB;
import pt.up.beta.mobile.datatypes.YearsTuition;
import pt.up.beta.mobile.loaders.TuitionLoader;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.syncadapter.SyncAdapter;
import pt.up.beta.mobile.ui.BaseFragment;
import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class TuitionRefListFragment extends BaseFragment implements
		OnItemClickListener, LoaderCallbacks<List<YearsTuition>> {

	private SimpleAdapter adapter;
	private YearsTuition currentYear;
	private ListView list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.generic_list,
				getParentContainer(), true);
		list = (ListView) root.findViewById(R.id.generic_list);
		return getParentContainer(); // this is mandatory.
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
			final Bundle extras = new Bundle();
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			extras.putBoolean(SyncAdapter.SINGLE_REQUEST, true);
			extras.putString(SyncAdapter.REQUEST_TYPE, SyncAdapter.TUITION);
			setRefreshActionItemState(true);
			ContentResolver.requestSync(
					new Account(AccountUtils.getActiveUserName(getActivity()),
							Constants.ACCOUNT_TYPE),
					SigarraContract.CONTENT_AUTHORITY, extras);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		startActivity(new Intent(getActivity(), TuitionRefDetailActivity.class)
				.putExtra(TuitionRefDetailFragment.REFERENCE, currentYear
						.getReferences().get(position)));
	}

	@Override
	public Loader<List<YearsTuition>> onCreateLoader(int loaderId,
			Bundle options) {
		return new TuitionLoader(getActivity(),
				SigarraContract.Tuition.CONTENT_URI,
				SigarraContract.Tuition.COLUMNS,
				SigarraContract.Tuition.PROFILE,
				SigarraContract.Tuition.getTuitionSelectionArgs(AccountUtils
						.getActiveUserName(getActivity())), null);
	}

	@Override
	public void onLoadFinished(Loader<List<YearsTuition>> loader,
			List<YearsTuition> yearsTuition) {
		if (getActivity() == null)
			return;
		if (yearsTuition == null)
			return;
		currentYear = yearsTuition.get(YearsTuition.currentYear);
		if (currentYear == null || currentYear.getReferences() == null) {
			showEmptyScreen(getString(R.string.label_no_tuition_ref));
			return;
		}
		String[] from = new String[] { "name", "amount", "date" };
		int[] to = new int[] { R.id.tuition_ref_name, R.id.tuition_ref_amount,
				R.id.tuition_ref_date };
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

		for (RefMB r : currentYear.getReferences()) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", r.getName());
			map.put("amount", Double.toString(r.getAmount()) + "€");
			map.put("date", r.getStartDate().format3339(true) + " "
					+ getString(R.string.interval_separator) + " "
					+ r.getEndDate().format3339(true));
			fillMaps.add(map);
		}

		// fill in the grid_item layout
		adapter = new SimpleAdapter(getActivity(), fillMaps,
				R.layout.list_item_tuition_ref, from, to);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		setRefreshActionItemState(false);
		showMainScreen();
	}

	@Override
	public void onLoaderReset(Loader<List<YearsTuition>> loader) {
	}

}
