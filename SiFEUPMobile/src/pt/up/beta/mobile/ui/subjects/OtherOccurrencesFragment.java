package pt.up.beta.mobile.ui.subjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.datatypes.OtherSubjectOccurrences;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.sifeup.SubjectUtils;
import pt.up.beta.mobile.ui.BaseFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import external.com.google.android.apps.iosched.util.UIUtils;

public class OtherOccurrencesFragment extends BaseFragment implements
		OnItemClickListener, ResponseCommand<OtherSubjectOccurrences[]> {
	final public static String OCCURRENCES_KEY = "pt.up.fe.mobile.ui.studentarea.OCCURRENCES";
	/** Stores all exams from Student */
	private OtherSubjectOccurrences[] occurrences;
	final public static String UCURR_CODE = "pt.up.fe.mobile.ui.studentarea.UCURR";
	private ListView list;
	private String uccurrId;

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
		uccurrId = getArguments().getString(UCURR_CODE);
		if (savedInstanceState != null) {
			occurrences = (OtherSubjectOccurrences[]) savedInstanceState
					.getParcelableArray(OCCURRENCES_KEY);
			if (occurrences == null) {
				task = SubjectUtils.getOtherSubjectOccurrences(uccurrId, this,
						getActivity());
			} else {
				if (populateList())
					showMainScreen();
			}
		} else
			task = SubjectUtils.getOtherSubjectOccurrences(uccurrId, this,
					getActivity());

	}

	@Override
	public void onItemClick(AdapterView<?> list, View view, int position,
			long id) {
		Intent i = new Intent(getActivity(), SubjectDescriptionActivity.class);
		final OtherSubjectOccurrences occurrence = occurrences[position];
		i.putExtra(SubjectDescriptionFragment.SUBJECT_CODE,
				occurrence.getOccurenceId());
		String title = occurrence.getName();
		if (!UIUtils.isLocalePortuguese()
				&& !TextUtils.isEmpty( occurrence.getNameEn()))
			title =  occurrence.getNameEn();
		i.putExtra(Intent.EXTRA_TITLE, title);
		startActivity(i);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (occurrences != null)
			outState.putParcelableArray(OCCURRENCES_KEY, occurrences);
	}

	private boolean populateList() {
		if (getActivity() == null)
			return false;
		if (occurrences.length == 0) {
			showEmptyScreen(getString(R.string.lb_no_other_occurrences));
			return false;
		}

		String[] from = new String[] { "chair", "time", };
		int[] to = new int[] { R.id.friend_name, R.id.friend_course };
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for (OtherSubjectOccurrences e : occurrences) {
			HashMap<String, String> map = new HashMap<String, String>();

			map.put(from[0], e.getName());
			map.put(from[1],
					getString(R.string.subjects_year, e.getYear(),
							e.getPeriodName()));
			fillMaps.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps,
				R.layout.list_item_search, from, to);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		return true;
	}

	@Override
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

	@Override
	public void onResultReceived(OtherSubjectOccurrences[] results) {
		occurrences = results;
		if (populateList())
			showMainScreen();
	}

	protected void onRepeat() {
		showLoadingScreen();
		task = SubjectUtils.getOtherSubjectOccurrences(uccurrId, this,
				getActivity());
	}

}
