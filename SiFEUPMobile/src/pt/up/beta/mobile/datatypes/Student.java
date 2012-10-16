package pt.up.beta.mobile.datatypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import android.content.res.Resources;

/**
 * Class that represents the Object Student
 * 
 * @author Ângela Igreja
 * 
 */
public class Student extends Profile implements Serializable {
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

	/**
	 * Parses a JSON String containing Employee info.
	 * 
	 * @param page
	 * @return boolean
	 * @throws JSONException
	 */
	public static Student parseJSON(String page) throws JSONException {
		final Student me = new Student();
		final JSONObject jObject = new JSONObject(page);
		me.setCode(jObject.getString("codigo"));
		me.setName(jObject.getString("nome"));
		me.setEmail(jObject.getString("email"));
		if (jObject.has("email_alternativo"))
			me.setEmailAlt(jObject.getString("email_alternativo"));
		if (jObject.has("telemovel"))
			me.setMobilePhone(jObject.getString("telemovel"));
		if (jObject.has("telefone"))
			me.setPhone(jObject.getString("telefone"));
		me.setCourses(StudentCourse.parseJSON(jObject.getJSONArray("cursos")));
		return me;
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