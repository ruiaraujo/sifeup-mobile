package pt.up.beta.mobile.ui.personalarea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.ui.BaseListFragment;
import pt.up.beta.mobile.ui.profile.ProfileActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class PersonalAreaFragment extends BaseListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] from = new String[] { "title" };
		int[] to = new int[] { R.id.list_menu_title };

		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

		HashMap<String, String> profile = new HashMap<String, String>();
		profile.put(from[0], getString(R.string.btn_profile));
		fillMaps.add(profile);

		HashMap<String, String> schedule = new HashMap<String, String>();
		schedule.put(from[0], getString(R.string.btn_schedule));
		fillMaps.add(schedule);

		HashMap<String, String> exams = new HashMap<String, String>();
		exams.put(from[0], getString(R.string.btn_exams));
		fillMaps.add(exams);

		HashMap<String, String> subjects = new HashMap<String, String>();
		subjects.put(from[0], getString(R.string.btn_subjects));
		fillMaps.add(subjects);

		HashMap<String, String> food = new HashMap<String, String>();
		food.put(from[0], getString(R.string.btn_lunch_menu));
		fillMaps.add(food);

		HashMap<String, String> academic = new HashMap<String, String>();
		academic.put(from[0], getString(R.string.btn_academic_path));
		fillMaps.add(academic);

		HashMap<String, String> park = new HashMap<String, String>();
		park.put(from[0], getString(R.string.btn_park_occupation));
		fillMaps.add(park);

		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps,
				R.layout.list_item_menu, from, to);
		setListAdapter(adapter);

	}


	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().setTitle(R.string.title_personal_area);
	}
	/** {@inheritDoc} */
	public void onListItemClick(ListView l, View v, int position, long id) {

		switch (position) {
		case 0:
			startActivity(new Intent(getActivity(), ProfileActivity.class)
					.putExtra(ProfileActivity.PROFILE_TYPE,
							ProfileActivity.PROFILE_STUDENT));
			return;
		case 1:
			final Bundle extras = new Bundle();
			extras.putInt(ScheduleFragment.SCHEDULE_TYPE,
					ScheduleFragment.SCHEDULE_STUDENT);
			openFragment(ScheduleFragment.class, extras,
					getString(R.string.title_schedule));
			return;
		case 2:
			openFragment(ExamsFragment.class, null,null);
			return;
		case 3:
			openFragment(SubjectsFragment.class, null,null);
			return;
		case 4:
			openFragment(LunchMenuFragment.class, null,null);
			return;
		case 5:
			openFragment(AcademicPathFragment.class, null,null);
			return;
		case 6:
			openFragment(ParkOccupationFragment.class, null,null);
			return;
		}
	}

}
