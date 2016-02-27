package pt.up.mobile.ui.profile;

import java.util.List;

import external.com.google.android.apps.iosched.util.UIUtils;
import pt.up.mobile.R;
import pt.up.mobile.datatypes.Student;
import pt.up.mobile.datatypes.StudentCourse;
import pt.up.mobile.datatypes.Profile.ProfileDetail;
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
		return details.size() + courses.length * 4; // 4 fields per course are
													// used
	}

	@Override
	public Object getItem(int position) {
		if (position < details.size()) {
			return details.get(position);
		}
		position -= details.size();
		switch (position % 4) {
		default:
		case 0:
			return HEADER;
		case 1:
			final String courseName = courses[position / 4].getCourseName();
			return courseName != null ? courseName : courses[position / 4]
					.getCourseTypeDesc();
		case 2:
			return courses[position / 4].getCurriculumYear() != null ? courses[position / 4]
					.getCurriculumYear() : context
					.getString(R.string.lb_unavailable);
		case 3:
			return courses[position / 4].getPlaceName();
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
		if (position % 4 == 0) {
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
			switch (position % 4) {
			case 1:
				title.setText(R.string.profile_title_programme);
				if (UIUtils.isLocalePortuguese()
						|| TextUtils.isEmpty(courses[position / 4]
								.getCourseNameEn()))
					content.setText(courses[position / 4].getCourseName());
				else
					content.setText(courses[position / 4].getCourseNameEn());
				break;
			case 2:
				title.setText(R.string.profile_title_year);
				content.setText(courses[position / 4].getCurriculumYear() != null ? courses[position / 4]
						.getCurriculumYear() : context
						.getString(R.string.lb_unavailable));
				break;
			case 3:
				title.setText(R.string.profile_title_faculty);
				content.setText(courses[position / 4].getPlaceName());
				break;
			}
		}

		return convertView;
	}
}
