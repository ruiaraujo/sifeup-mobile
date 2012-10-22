package pt.up.beta.mobile.datatypes;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class StudentCourse implements Parcelable {
	@SerializedName("fest_id")
	private final String courseId;
	@SerializedName("fest_tipo")
	private final String courseType;
	@SerializedName("fest_tipo_descr")
	private final String courseTypeDesc;
	@SerializedName("cur_nome")
	private final String courseName;
	@SerializedName("fest_a_lect_1_insc")
	private final String firstYear;
	@SerializedName("fest_d_1_insc")
	private final String firstDate;
	@SerializedName("cur_id")
	private final String degreeId;
	@SerializedName("est_alectivo")
	private final String currentYear;
	@SerializedName("est_sig")
	private final String state;
	@SerializedName("est_nome")
	private final String stateName;
	@SerializedName("est_d_inicio")
	private final String stateBegin;
	@SerializedName("est_d_fim")
	private final String stateEnd;
	@SerializedName("inst_nome")
	private final String placeName;
	@SerializedName("inst_sigla")
	private final String placeAcronym;
	@SerializedName("ano_curricular")
	private final String curriculumYear;
	@SerializedName("media")
	private final String average;
	@SerializedName("inscricoes")
	private final SubjectEntry[] subjectEntries;

	public StudentCourse(String courseId, String courseType,
			String courseTypeDesc, String courseName, String firstYear,
			String firstDate, String degreeId, String currentYear,
			String state, String stateName, String stateBegin, String stateEnd,
			String placeName, String placeAcronym, String curriculumYear,
			String average, SubjectEntry[] subjectEntries) {
		this.courseId = courseId;
		this.courseType = courseType;
		this.courseTypeDesc = courseTypeDesc;
		this.courseName = courseName;
		this.firstYear = firstYear;
		this.firstDate = firstDate;
		this.degreeId = degreeId;
		this.currentYear = currentYear;
		this.state = state;
		this.stateName = stateName;
		this.stateBegin = stateBegin;
		this.stateEnd = stateEnd;
		this.placeName = placeName;
		this.placeAcronym = placeAcronym;
		this.curriculumYear = curriculumYear;
		this.average = average;
		this.subjectEntries = subjectEntries;
	}

	public String getCourseId() {
		return courseId;
	}

	public String getCourseType() {
		return courseType;
	}

	public String getCourseTypeDesc() {
		return courseTypeDesc;
	}

	public String getCourseName() {
		return courseName;
	}

	public String getFirstYear() {
		return firstYear;
	}

	public String getFirstDate() {
		return firstDate;
	}

	public String getDegreeId() {
		return degreeId;
	}

	public String getCurrentYear() {
		return currentYear;
	}

	public String getState() {
		return state;
	}

	public String getStateName() {
		return stateName;
	}

	public String getStateBegin() {
		return stateBegin;
	}

	public String getStateEnd() {
		return stateEnd;
	}

	public String getPlaceName() {
		return placeName;
	}

	public String getPlaceAcronym() {
		return placeAcronym;
	}

	public String getCurriculumYear() {
		return curriculumYear;
	}

	public String getAverage() {
		return average;
	}

	public SubjectEntry[] getSubjectEntries() {
		return subjectEntries;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((courseId == null) ? 0 : courseId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StudentCourse other = (StudentCourse) obj;
		if (courseId == null) {
			if (other.courseId != null)
				return false;
		} else if (!courseId.equals(other.courseId))
			return false;
		return true;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, courseId);
		ParcelUtils.writeString(dest, courseType);
		ParcelUtils.writeString(dest, courseTypeDesc);
		ParcelUtils.writeString(dest, courseName);
		ParcelUtils.writeString(dest, firstYear);
		ParcelUtils.writeString(dest, firstDate);
		ParcelUtils.writeString(dest, degreeId);
		ParcelUtils.writeString(dest, currentYear);
		ParcelUtils.writeString(dest, state);
		ParcelUtils.writeString(dest, stateName);
		ParcelUtils.writeString(dest, stateBegin);
		ParcelUtils.writeString(dest, stateEnd);
		ParcelUtils.writeString(dest, placeName);
		ParcelUtils.writeString(dest, placeAcronym);
		ParcelUtils.writeString(dest, curriculumYear);
		ParcelUtils.writeString(dest, average);
		dest.writeParcelableArray(subjectEntries, flags);
	}

	private StudentCourse(Parcel in) {
		courseId = ParcelUtils.readString(in);
		courseType = ParcelUtils.readString(in);
		courseTypeDesc = ParcelUtils.readString(in);
		courseName = ParcelUtils.readString(in);
		firstYear = ParcelUtils.readString(in);
		firstDate = ParcelUtils.readString(in);
		degreeId = ParcelUtils.readString(in);
		currentYear = ParcelUtils.readString(in);
		state = ParcelUtils.readString(in);
		stateName = ParcelUtils.readString(in);
		stateBegin = ParcelUtils.readString(in);
		stateEnd = ParcelUtils.readString(in);
		placeName = ParcelUtils.readString(in);
		placeAcronym = ParcelUtils.readString(in);
		curriculumYear = ParcelUtils.readString(in);
		average = ParcelUtils.readString(in);
		subjectEntries = (SubjectEntry[]) in
				.readParcelableArray(SubjectEntry.class.getClassLoader());
	}

	
	public static final Parcelable.Creator<StudentCourse> CREATOR = new Parcelable.Creator<StudentCourse>() {
		public StudentCourse createFromParcel(Parcel in) {
			return new StudentCourse(in);
		}

		public StudentCourse[] newArray(int size) {
			return new StudentCourse[size];
		}
	};

}
