package pt.up.beta.mobile.datatypes;

import java.util.ArrayList;
import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Class that represents the Object Student
 * 
 * @author Ângela Igreja
 * 
 */
public class Student extends Profile implements Parcelable {

	@SerializedName("cursos")
	private final StudentCourse[] courses;

	public interface Type {
		String EMAIL = "email";
		String MOBILE = "mobile";
	}

	public List<ProfileDetail> getProfileContents(Resources res) {
		List<ProfileDetail> result = new ArrayList<ProfileDetail>();
		if (!TextUtils.isEmpty(getCode())) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_code), getCode(), null));
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

	public StudentCourse[] getCourses() {
		return courses;
	}

	public String getProgrammeNames() {
		if (courses.length == 0)
			return "";
		if (courses.length == 1)
			return courses[0].getCourseName();
		final StringBuilder st = new StringBuilder();
		for (int i = 0; i < courses.length; ++i) {
			if (i != 0)
				st.append(" ,");
			st.append(courses[i].getCourseName());
		}
		return st.toString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(courses.length);
		dest.writeParcelableArray(courses, flags);
	}

	private Student(Parcel in) {
		super(in);
		courses = new StudentCourse[in.readInt()];
		in.readTypedArray(courses, StudentCourse.CREATOR);
	}

	public static final Parcelable.Creator<Student> CREATOR = new Parcelable.Creator<Student>() {
		public Student createFromParcel(Parcel in) {
			return new Student(in);
		}

		public Student[] newArray(int size) {
			return new Student[size];
		}
	};
}