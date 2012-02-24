package pt.up.fe.mobile.ui.studentservices.tuition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.datatypes.YearsTuition;
import pt.up.fe.mobile.sifeup.ResponseCommand;
import pt.up.fe.mobile.sifeup.SessionManager;
import pt.up.fe.mobile.sifeup.TuitionUtils;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
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
	private SimpleAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AnalyticsUtils.getInstance(getActivity()).trackPageView(
				"/TuitionHistory");
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
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if (!SessionManager.tuitionHistory.isLoaded())
            TuitionUtils.getTuitionReply(SessionManager.getInstance()
                    .getLoginCode(), this);
        else {
            loadList();
            showFastMainScreen();
        }
    }

	private void loadList() {
		if (getActivity() == null)
			return;
		String[] from = new String[] { "year", "paid", "to_pay" };
		int[] to = new int[] { R.id.tuition_history_year,
				R.id.tuition_history_paid, R.id.tuition_history_to_pay };
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

		for (YearsTuition y : SessionManager.tuitionHistory.getHistory()) {
			HashMap<String, String> map = new HashMap<String, String>();

			map.put("year", getString(R.string.lbl_year) + " " + y.getYear());
			map.put("paid", getString(R.string.lbl_paid) + ": "
					+ y.getTotal_paid() + "€");
			if (y.getTotal_in_debt() > 0.0)
				map.put("to_pay", getString(R.string.lbl_still_to_pay) + ": "
						+ y.getTotal_in_debt() + "€");
			fillMaps.add(map);
		}

		// fill in the grid_item layout
		adapter = new SimpleAdapter(getActivity(), fillMaps,
				R.layout.list_item_tuition_history, from, to);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		Log.i("Propinas", "List view loaded successfully");
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		SessionManager.tuitionHistory.setSelected_year(position);
		startActivity(new Intent(getActivity(), TuitionActivity.class));
	}

	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error),
					Toast.LENGTH_LONG).show();
			((BaseActivity) getActivity()).goLogin();
			break;
		case NETWORK:
			Toast.makeText(getActivity(),
					getString(R.string.toast_server_error), Toast.LENGTH_LONG)
					.show();
		default:
			// TODO: general error
			break;
		}

	}

	public void onResultReceived(Object... results) {
		if (results == null || results[0] == null)
			return;
		loadList();
		showMainScreen();
	}
}
