package pt.up.beta.mobile.ui.profile;

import java.util.List;

import external.com.google.android.apps.iosched.util.UIUtils;

import pt.up.mobile.R;
import pt.up.beta.mobile.datatypes.Profile.ProfileDetail;
import pt.up.beta.mobile.datatypes.Student;
import pt.up.beta.mobile.datatypes.StudentCourse;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StudentDetailsAdapter extends BaseAdapter {

	private final List<ProfileDetail> details;
	private final StudentCourse[] courses;
	private final LayoutInflater mInflater;
	private final Context context;

	public StudentDetailsAdapter(Student student, Context mContext) {
		this.details = student.getProfileContents(mContext.getResources());
		this.courses = student.getCourses();
		this.context = mContext;
		this.mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return details.size() + courses.length * 5; // 5 fields per course are
													// used
	}

	@Override
	public Object getItem(int position) {
		if (position < details.size()) {
			return details.get(position);
		}
		position -= details.size();
		switch (position % 5) {
		default:
		case 0:
			return HEADER;
		case 1:
			final String courseName = courses[position / 5].getCourseName();
			return courseName != null ? courseName : courses[position / 5]
					.getCourseTypeDesc();
		case 2:
			return courses[position / 5].getStateName();
		case 3:
			return courses[position / 5].getCurriculumYear() != null ? courses[position / 5]
					.getCurriculumYear() : context
					.getString(R.string.lb_unavailable);
		case 4:
			return courses[position / 5].getPlaceName();
		}
	}

	@Override
	public long getItemId(int position) {
		final Object item = getItem(position);
		if (item != null)
			return item.hashCode();
		return 0;
	}

	private static final String HEADER = "header";
	private static final String NORMAL = "normal";

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position < details.size()) {
			if (convertView == null
					|| convertView.getTag().toString().equals(HEADER)) {
				convertView = mInflater.inflate(R.layout.list_item_profile,
						parent, false);
				convertView.setTag(NORMAL);
			}
			final TextView title = (TextView) convertView
					.findViewById(R.id.profile_item_title);
			final TextView content = (TextView) convertView
					.findViewById(R.id.profile_item_content);
			title.setText(details.get(position).title);
			content.setText(details.get(position).content);
			return convertView;
		}
		position -= details.size();
		if (position % 5 == 0) {
			if (convertView == null
					|| convertView.getTag().toString().equals(NORMAL)) {
				convertView = mInflater.inflate(
						R.layout.list_item_programme_marker, parent, false);
				convertView.setTag(HEADER);
			}
			final TextView header = (TextView) convertView
					.findViewById(R.id.marker);
			header.setText(R.string.profile_title_course);
		} else {

			if (convertView == null
					|| convertView.getTag().toString().equals(HEADER)) {
				convertView = mInflater.inflate(R.layout.list_item_profile,
						parent, false);
				convertView.setTag(NORMAL);
			}
			final TextView title = (TextView) convertView
					.findViewById(R.id.profile_item_title);
			final TextView content = (TextView) convertView
					.findViewById(R.id.profile_item_content);
			switch (position % 5) {
			case 1:
				title.setText(R.string.profile_title_programme);
				if (UIUtils.isLocalePortuguese()
						|| TextUtils.isEmpty(courses[position / 5]
								.getCourseNameEn()))
					content.setText(courses[position / 5].getCourseName());
				else
					content.setText(courses[position / 5].getCourseNameEn());
				break;
			case 2:
				title.setText(R.string.profile_title_status);
				content.setText(courses[position / 5].getStateName());
				break;
			case 3:
				title.setText(R.string.profile_title_year);
				content.setText(courses[position / 5].getCurriculumYear() != null ? courses[position / 5]
						.getCurriculumYear() : context
						.getString(R.string.lb_unavailable));
				break;
			case 4:
				title.setText(R.string.profile_title_faculty);
				content.setText(courses[position / 5].getPlaceName());
				break;
			}
		}

		return convertView;
	}
}
