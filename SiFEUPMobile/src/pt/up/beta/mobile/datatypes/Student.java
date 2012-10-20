package pt.up.beta.mobile.datatypes;

import java.util.ArrayList;
import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import android.content.res.Resources;

import com.google.gson.annotations.SerializedName;

/**
 * Class that represents the Object Student
 * 
 * @author Ângela Igreja
 * 
 */
public class Student extends Profile {

	@SerializedName("cursos")
	private List<StudentCourse> courses;

	public interface Type {
		String EMAIL = "email";
		String MOBILE = "mobile";
	}

	public List<ProfileDetail> getProfileContents(Resources res) {
		List<ProfileDetail> result = new ArrayList<ProfileDetail>();
		if (code != null && !code.equals("")) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_code), code, null));
		}
		if (getEmail() != null) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_email), getEmail(),
					Type.EMAIL));
		}
		if (getEmailAlt() != null) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_email_alt),
					getEmailAlt(), Type.EMAIL));
		}
		if (getMobilePhone() != null) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_mobile),
					getMobilePhone(), Type.MOBILE));
		}
		if (getPhone() != null) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_telephone), getPhone(),
					Type.MOBILE));
		}

		return result;

	}

	@Override
	public String getType() {
		return SifeupAPI.STUDENT_TYPE;
	}

	public List<StudentCourse> getCourses() {
		return courses;
	}

	public void setCourses(List<StudentCourse> courses) {
		this.courses = courses;
	}

	public CharSequence getProgrammeNames() {
		if (courses.size() == 0)
			return "";
		if (courses.size() == 1)
			return courses.get(0).getCourseName();
		final StringBuilder st = new StringBuilder();
		for (int i = 0; i < courses.size(); ++i) {
			if (i != 0)
				st.append(" ,");
			st.append(courses.get(i).getCourseName());
		}
		return st.toString();
	}
}