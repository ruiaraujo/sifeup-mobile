package pt.up.beta.mobile.datatypes;

import java.util.ArrayList;
import java.util.List;

import pt.up.mobile.R;
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
		if (!TextUtils.isEmpty(getEmail())) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_email), getEmail(),
					Type.EMAIL));
		}
		if (!TextUtils.isEmpty(getEmailAlt())) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_email_alt),
					getEmailAlt(), Type.EMAIL));
		}
		if (!TextUtils.isEmpty(getMobilePhone())) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_mobile),
					getMobilePhone(), Type.MOBILE));
		}
		if (!TextUtils.isEmpty(getPhone())) {
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
		if (courses == null)
			return "";
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
		if (courses != null) {
			dest.writeInt(1);
			dest.writeInt(courses.length);
			dest.writeTypedArray(courses, flags);
		} else
			dest.writeInt(0);
	}

	private Student(Parcel in) {
		super(in);
		if (in == null || in.readInt() == 0) {
			courses = null;
			return;
		}
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