package pt.up.mobile.ui.subjects;

import pt.up.mobile.R;
import pt.up.mobile.datatypes.Student;
import pt.up.mobile.sifeup.ResponseCommand;
import pt.up.mobile.sifeup.SubjectUtils;
import pt.up.mobile.ui.BaseFragment;
import pt.up.mobile.ui.profile.ProfileActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class EnrolledStudentsFragment extends BaseFragment implements
		OnItemClickListener, ResponseCommand<Student[]> {
	final public static String ENROLLED_KEY = "pt.up.fe.mobile.ui.studentarea.OCCURRENCES";
	/** Stores all exams from Student */
	private Student[] enrolledStudents;
	final public static String OCORR_CODE = "pt.up.fe.mobile.ui.studentarea.OCORR";
	private ListView list;
	private String ocorrId;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflateMainScreen(R.layout.generic_list);
		list = (ListView) root.findViewById(R.id.generic_list);
		return getParentContainer(); // this is mandatory.
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ocorrId = getArguments().getString(OCORR_CODE);
		if (savedInstanceState != null) {
			final Parcelable[] storedEnrolledStudents = savedInstanceState
					.getParcelableArray(ENROLLED_KEY);
			if (storedEnrolledStudents == null) {
				task = SubjectUtils.getSubjectEnrolledStudents(ocorrId, this,
						getActivity());
			} else {
				enrolledStudents = new Student[storedEnrolledStudents.length];
				for (int i = 0; i < storedEnrolledStudents.length; ++i)
					enrolledStudents[i] = (Student) storedEnrolledStudents[i];
				if (populateList())
					showMainScreen();
			}
		} else
			task = SubjectUtils.getSubjectEnrolledStudents(ocorrId, this,
					getActivity());

	}

	@Override
	public void onItemClick(AdapterView<?> list, View view, int position,
			long id) {
		Intent i = new Intent(getActivity(), ProfileActivity.class);
		final Student student = enrolledStudents[position];
		i.putExtra(ProfileActivity.PROFILE_TYPE,
				ProfileActivity.PROFILE_STUDENT);
		i.putExtra(ProfileActivity.PROFILE_CODE, student.getCode());
		i.putExtra(Intent.EXTRA_TITLE, student.getName());
		startActivity(i);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (enrolledStudents != null)
			outState.putParcelableArray(ENROLLED_KEY, enrolledStudents);
	}

	private boolean populateList() {
		if (getActivity() == null)
			return false;
		if (enrolledStudents.length == 0) {
			showEmptyScreen(getString(R.string.lb_no_enrolled_students));
			return false;
		}

		ArrayAdapter<Student> adapter = new ArrayAdapter<Student>(
				getActivity(), android.R.layout.simple_list_item_1,
				enrolledStudents);
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
	public void onResultReceived(Student[] results) {
		enrolledStudents = results;
		if (populateList())
			showMainScreen();
	}

	protected void onRepeat() {
		showLoadingScreen();
		task = SubjectUtils.getSubjectEnrolledStudents(ocorrId, this,
				getActivity());
	}

}
