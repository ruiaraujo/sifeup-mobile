package pt.up.beta.mobile.ui.services.tuition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.YearsTuition;
import pt.up.beta.mobile.loaders.TuitionLoader;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.syncadapter.SigarraSyncAdapterUtils;
import pt.up.beta.mobile.ui.BaseFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class TuitionHistoryFragment extends BaseFragment implements
		OnItemClickListener,
		LoaderCallbacks<List<YearsTuition>> {

	private ListView list;
	private List<YearsTuition> history;

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
			setRefreshActionItemState(true);
			SigarraSyncAdapterUtils.syncTuitions(AccountUtils.getActiveUserName(getActivity()));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		startActivity(new Intent(getActivity(), TuitionActivity.class)
				.putExtra(TuitionFragment.CURRENT_YEAR, history.get(position)));
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
		history = yearsTuition;
		String[] from = new String[] { "year", "paid", "to_pay" };
		int[] to = new int[] { R.id.tuition_history_year,
				R.id.tuition_history_paid, R.id.tuition_history_to_pay };
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for (YearsTuition y : history) {
			HashMap<String, String> map = new HashMap<String, String>();

			map.put(from[0], getString(R.string.lbl_year) + " " + y.getYear());
			map.put(from[1],
					getString(R.string.lbl_paid) + ": " + y.getTotal_paid()
							+ "€");
			if (y.getTotal_in_debt() > 0.0)
				map.put(from[2], getString(R.string.lbl_still_to_pay) + ": "
						+ y.getTotal_in_debt() + "€");
			fillMaps.add(map);
		}

		// fill in the grid_item layout
		list.setAdapter(new SimpleAdapter(getActivity(), fillMaps,
				R.layout.list_item_tuition_history, from, to));
		list.setOnItemClickListener(this);
		Log.i("Propinas", "List view loaded successfully");
		setRefreshActionItemState(false);
		showMainScreen();
	}

	@Override
	public void onLoaderReset(Loader<List<YearsTuition>> loader) {
	}
}
