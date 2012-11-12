package pt.up.beta.mobile.datatypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;

import pt.up.beta.mobile.utils.StringUtils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * Holds All Info about Academic Path
 * 
 */
public class AcademicPath implements Parcelable {
	private final StudentCourse course;
	private final List<AcademicYear> ucs;

	private AcademicPath(StudentCourse course) {
		ucs = new ArrayList<AcademicYear>();
		this.course = course;
	}

	/**
	 * Parses a JSON String containing Academic Path info, Stores that info at
	 * Object
	 * 
	 * @param page
	 * @return boolean
	 * @throws JSONException
	 */
	public static AcademicPath instance(StudentCourse course){
		final AcademicPath academicPath = new AcademicPath(course);

		for (SubjectEntry subject : course.getSubjectEntries()) {

			AcademicYear year = null;
			for (int j = 0; j < academicPath.ucs.size(); ++j) {
				if (academicPath.ucs.get(j).getYear() == subject.getAno()) {
					year = academicPath.ucs.get(j);
					break;
				}
			}
			// add uc to academic path
			if (year == null) {
				year = new AcademicYear();
				year.setYear(subject.getAno());
				academicPath.ucs.add(year);
			}
			if (subject.getPercodigo().equals("1S")) {
				year.getFirstSemester().add(subject);
			} else // TODO How to deal with anual stuff
			{
				year.getSecondSemester().add(subject);
			}
		}
		Collections.sort(academicPath.ucs);
		return academicPath;
	}

	public String getCourseAcronym() {
		return course.getCourseAcronym() == null ? StringUtils
				.getAcronym(course.getCourseName()) : course.getCourseAcronym();
	}

	public String getAverage() {
		return course.getAverage();
	}

	public List<AcademicYear> getUcs() {
		return ucs;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(course, flags);
		dest.writeTypedList(ucs);
	}

	private AcademicPath(Parcel in) {
		course = in.readParcelable(StudentCourse.class.getClassLoader());
		ucs = new ArrayList<AcademicYear>();
		in.readTypedList(ucs, AcademicYear.CREATOR);

	}

	public static final Parcelable.Creator<AcademicPath> CREATOR = new Parcelable.Creator<AcademicPath>() {
		public AcademicPath createFromParcel(Parcel in) {
			return new AcademicPath(in);
		}

		public AcademicPath[] newArray(int size) {
			return new AcademicPath[size];
		}
	};

	public String getCourseYears() {
		return course.getCurriculumYear();
	}

}
