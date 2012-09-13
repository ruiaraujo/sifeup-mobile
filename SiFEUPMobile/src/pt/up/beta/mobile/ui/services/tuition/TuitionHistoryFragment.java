package pt.up.beta.mobile.ui.services.tuition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.beta.mobile.datatypes.YearsTuition;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.TuitionUtils;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TuitionHistoryFragment extends BaseFragment implements
		OnItemClickListener, ResponseCommand {

	private ListView list;
	private ArrayList<YearsTuition> history;

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
		task = TuitionUtils.getTuitionReply(
				AccountUtils.getActiveUserCode(getActivity()), this);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		startActivity(new Intent(getActivity(), TuitionActivity.class)
				.putExtra(TuitionFragment.CURRENT_YEAR, history.get(position)));
	}

	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error),
					Toast.LENGTH_LONG).show();
			goLogin();
			break;
		case NETWORK:
			showRepeatTaskScreen(getString(R.string.toast_server_error));
			break;
		default:
			showEmptyScreen(getString(R.string.general_error));
			break;
		}
	}

	@SuppressWarnings("unchecked")
	public void onResultReceived(Object... results) {
		if (getActivity() == null)
			return;
		if (results == null || results[0] == null)
			return;
		history = (ArrayList<YearsTuition>) results[0];
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
		showMainScreen();
	}

	protected void onRepeat() {
		showLoadingScreen();
		task = TuitionUtils.getTuitionReply(
				AccountUtils.getActiveUserCode(getActivity()), this);
	}
}
