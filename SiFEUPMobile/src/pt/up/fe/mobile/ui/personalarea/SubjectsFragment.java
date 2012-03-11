package pt.up.fe.mobile.ui.personalarea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.datatypes.Subject;
import pt.up.fe.mobile.sifeup.ResponseCommand;
import pt.up.fe.mobile.sifeup.SessionManager;
import pt.up.fe.mobile.sifeup.SubjectUtils;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;

import external.com.google.android.apps.iosched.util.UIUtils;

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

public class SubjectsFragment extends BaseFragment implements
		OnItemClickListener, ResponseCommand {

	/** Contains all subscribed subjects */
	private ArrayList<Subject> subjects;
	private ListView list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AnalyticsUtils.getInstance(getActivity()).trackPageView("/Subjects");

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.generic_list,
				getParentContainer(), true);
		list = (ListView) root.findViewById(R.id.generic_list);
		return getParentContainer(); // this is mandatory.
	}
	

    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        task = SubjectUtils.getSubjectsReply(SessionManager.getInstance(getActivity())
                .getLoginCode(), Integer.toString(UIUtils
                .secondYearOfSchoolYear() - 1), this);
    }

	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error),
					Toast.LENGTH_LONG).show();
			((BaseActivity) getActivity()).goLogin();
			return;
		case NETWORK:
			Toast.makeText(getActivity(), getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
		default:// TODO: add general error message
			break;
		}
        getActivity().finish();
	}

	@SuppressWarnings("unchecked")
	public void onResultReceived(Object... results) {
		if (getActivity() == null)
			return;
		subjects = (ArrayList<Subject>) results[0];
		if (subjects.isEmpty()) {
			showEmptyScreen(getString(R.string.lb_no_subjects));
			return;
		}
		final String language = Locale.getDefault().getLanguage();
		final String[] from = new String[] { "name", "code", "time" };
		final int[] to = new int[] { R.id.exam_chair, R.id.exam_time,
				R.id.exam_room };
		// prepare the list of all records
		final List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for (Subject s : subjects) {
			HashMap<String, String> map = new HashMap<String, String>();
			if (language.startsWith("pt"))
				map.put(from[0], (s.getNamePt().trim().length() != 0) ? s
						.getNamePt() : s.getNameEn());
			else
				map.put(from[0], (s.getNameEn().trim().length() != 0) ? s
						.getNameEn() : s.getNamePt());
			map.put(from[1], s.getAcronym());
			map.put(from[2], getString(R.string.subjects_year, s.getYear(), s
					.getSemestre()));
			fillMaps.add(map);
		}
		// fill in the grid_item layout
		final SimpleAdapter adapter = new SimpleAdapter(getActivity(),
				fillMaps, R.layout.list_item_exam, from, to);
		list.setAdapter(adapter);
		list.setOnItemClickListener(SubjectsFragment.this);
		showMainScreen();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		if (getActivity() == null)
			return;
		Intent i = new Intent(getActivity(), SubjectDescriptionActivity.class);
		// assumed only one page of results
		int secondYear = UIUtils.secondYearOfSchoolYear();
		i.putExtra(SubjectDescriptionFragment.SUBJECT_CODE, subjects.get(
				position).getAcronym());
		i.putExtra(SubjectDescriptionFragment.SUBJECT_YEAR, Integer
				.toString(secondYear - 1)
				+ "/" + Integer.toString(secondYear));
		i.putExtra(SubjectDescriptionFragment.SUBJECT_PERIOD, subjects.get(
				position).getSemestre());

		final String language = Locale.getDefault().getLanguage();
		if (language.startsWith("pt"))
			i.putExtra(Intent.EXTRA_TITLE, subjects.get(position).getNamePt());
		else
			i.putExtra(Intent.EXTRA_TITLE, subjects.get(position).getNameEn());
		startActivity(i);

	}
}
