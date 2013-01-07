package pt.up.beta.mobile.ui.personalarea;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.datatypes.Exam;
import pt.up.beta.mobile.datatypes.Exam.Room;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.ui.profile.ProfileActivity;
import pt.up.beta.mobile.ui.subjects.SubjectDescriptionActivity;
import pt.up.beta.mobile.ui.subjects.SubjectDescriptionFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Class Description Fragment
 * 
 * @author Ângela Igreja
 * 
 */
public class ExamDescriptionFragment extends BaseFragment {
	/**
	 * The key for the student code in the intent.
	 */
	final public static String EXAM = "pt.up.fe.mobile.ui.studentarea.EXAM";
	private Exam exam;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		exam = (Exam) getArguments().getParcelable(EXAM);
		if (exam == null && savedInstanceState != null)
			exam = savedInstanceState.getParcelable(EXAM);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(EXAM, exam);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		final View root = inflateMainScreen(R.layout.class_description);

		// Subject
		final TextView subject = (TextView) root
				.findViewById(R.id.class_subject);
		subject.setText(Html.fromHtml(getString(R.string.class_subject,
				exam.getOcorrName())));
		subject.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (getActivity() == null)
					return;
				Intent i = new Intent(getActivity(),
						SubjectDescriptionActivity.class);
				// assumed only one page of results
				i.putExtra(SubjectDescriptionFragment.SUBJECT_CODE,
						exam.getOcorrId());
				i.putExtra(Intent.EXTRA_TITLE, exam.getOcorrName());
				startActivity(i);

			}
		});

		// Date
		final TextView date = (TextView) root.findViewById(R.id.exam_date);
		date.setText(Html.fromHtml(getString(R.string.date_exam, exam.getDate())));
		date.setVisibility(View.VISIBLE);

		if (exam.getRooms().length > 0) {
			// Rooms
			final LinearLayout roomsContainer = (LinearLayout) root
					.findViewById(R.id.list_rooms);
			final OnClickListener roomClick = new OnClickListener() {
				@Override
				public void onClick(View v) {
					final Room room = (Room) v.getTag();
					Intent i = new Intent(getActivity(), ProfileActivity.class);
					i.putExtra(ProfileActivity.PROFILE_TYPE,
							ProfileActivity.PROFILE_ROOM);
					i.putExtra(ProfileActivity.PROFILE_CODE, room.getId());
					i.putExtra(Intent.EXTRA_TITLE, room.toString());
					startActivity(i);
				}
			};
			for (Room room : exam.getRooms()) {
				TextView llItem = (TextView) inflater.inflate(
						R.layout.simple_list_item_clickable, null);
				llItem.setText(room.toString());
				// To know wich item has been clicked
				llItem.setTag(room);
				// In the onClickListener just get the id using getTag() on the
				// view
				llItem.setOnClickListener(roomClick);
				roomsContainer.addView(llItem);
			}
		} else {
			root.findViewById(R.id.list_rooms).setVisibility(View.GONE);
			root.findViewById(R.id.class_room).setVisibility(View.GONE);
		}

		// Start time
		final TextView startT = (TextView) root
				.findViewById(R.id.class_start_time);
		startT.setText(Html.fromHtml(getString(R.string.class_start_time,
				exam.getStartTime())));

		// End Time
		final TextView endT = (TextView) root.findViewById(R.id.class_end_time);
		endT.setText(Html.fromHtml(getString(R.string.class_end_time,
				exam.getEndTime())));

		showMainScreen();

		return getParentContainer();// mandatory
	}
}
