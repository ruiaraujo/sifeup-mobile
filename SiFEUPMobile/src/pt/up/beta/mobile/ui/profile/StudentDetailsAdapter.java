package pt.up.beta.mobile.ui.profile;

import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.datatypes.Profile.ProfileDetail;
import pt.up.beta.mobile.datatypes.Student;
import pt.up.beta.mobile.datatypes.StudentCourse;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StudentDetailsAdapter extends BaseAdapter {

	private final List<ProfileDetail> details;
	private final List<StudentCourse> courses;
	private final LayoutInflater mInflater;

	public StudentDetailsAdapter(Student student, Context mContext) {
		this.details = student.getProfileContents(mContext.getResources());
		this.courses = student.getCourses();
		this.mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return details.size() + courses.size() * 5; // 5 fields per course are
													// used
	}

	@Override
	public Object getItem(int position) {
		if (position < details.size()) {
			return details.get(position);
		}
		position -= details.size();
		if (position % 5 == 0) {
			return HEADER;
		}
		switch (position % 5) {
		case 1:
			return courses.get(position / 5).getCourseName();
		case 2:
			return courses.get(position / 5).getStateName();
		case 3:
			return courses.get(position / 5).getCurriculumYear();
		case 4:
			return courses.get(position / 5).getPlaceName();
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).hashCode();
	}

	private static final String HEADER = "header";
	private static final String NORMAL = "normal";

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position < details.size()) {
			if (convertView == null
					|| convertView.getTag().toString().equals(HEADER)) {
				convertView = mInflater.inflate(R.layout.list_item_profile,
						parent);
				convertView.setTag(NORMAL);
			}
			final TextView title = (TextView) convertView
					.findViewById(R.id.profile_item_title);
			final TextView content = (TextView) convertView
					.findViewById(R.id.profile_item_content);
			title.setText(details.get(position).title);
			content.setText(details.get(position).content);
		}
		position -= details.size();
		if (position % 5 == 0) {
			if (convertView == null
					|| convertView.getTag().toString().equals(NORMAL)) {
				convertView = mInflater.inflate(
						R.layout.list_item_grade_marker, parent);
				convertView.setTag(HEADER);
			}
			final TextView header = (TextView) convertView
					.findViewById(R.id.grade_marker);
			header.setText(R.string.profile_title_course);
		} else {

			if (convertView == null
					|| convertView.getTag().toString().equals(HEADER)) {
				convertView = mInflater.inflate(R.layout.list_item_profile,
						parent);
				convertView.setTag(NORMAL);
			}
			final TextView title = (TextView) convertView
					.findViewById(R.id.profile_item_title);
			final TextView content = (TextView) convertView
					.findViewById(R.id.profile_item_content);
			switch (position % 5) {
			case 1:
				title.setText(R.string.profile_title_programme);
				content.setText(courses.get(position / 5).getCourseName());
				break;
			case 2:
				title.setText(R.string.profile_title_status);
				content.setText(courses.get(position / 5).getStateName());
				break;
			case 3:
				title.setText(R.string.profile_title_year);
				content.setText(courses.get(position / 5).getCurriculumYear());
				break;
			case 4:
				title.setText(R.string.profile_title_faculty);
				content.setText(courses.get(position / 5).getPlaceName());
				break;
			}
		}

		return convertView;
	}
}
