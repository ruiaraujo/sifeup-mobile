package pt.up.beta.mobile.ui.services.tuition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.beta.mobile.datatypes.RefMB;
import pt.up.beta.mobile.datatypes.YearsTuition;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.TuitionUtils;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TuitionRefListFragment extends BaseFragment implements
		OnItemClickListener, ResponseCommand {

	private SimpleAdapter adapter;
	private YearsTuition currentYear;
	private ListView list;

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
        if (!AccountUtils.tuitionHistory.isLoaded())
            task = TuitionUtils.getTuitionReply(AccountUtils.getActiveUserCode(getActivity()), this);
        else {
            if ( loadList() )
                showFastMainScreen();
        }
    }

	private boolean loadList() {
		String[] from = new String[] { "name", "amount", "date" };
		int[] to = new int[] { R.id.tuition_ref_name, R.id.tuition_ref_amount,
				R.id.tuition_ref_date };
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		ArrayList<YearsTuition> history = AccountUtils.tuitionHistory
				.getHistory();
		currentYear = history.get(AccountUtils.tuitionHistory.currentYear);
		if ( currentYear == null )
		{
		    showEmptyScreen(getString(R.string.label_no_tuition_ref));
		    return false;
		}
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
		return true;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		currentYear.setSelectedReference(position);
		startActivity(new Intent(getActivity(), TuitionRefDetailActivity.class));
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

	public void onResultReceived(Object... results) {
        if (getActivity() == null)
            return;
		if (results == null || results[0] == null)
			return;
		if ( loadList() )
		    showMainScreen();
	}

	protected void onRepeat() {
		showLoadingScreen();
        task = TuitionUtils.getTuitionReply(AccountUtils.getActiveUserCode(getActivity()), this);
	}

}
