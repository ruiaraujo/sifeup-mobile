package pt.up.mobile.datatypes;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class AcademicYear implements Comparable<AcademicYear>, Parcelable{

	private int year;
	private final List<SubjectEntry> firstSemester;
	private final List<SubjectEntry> secondSemester;
	public AcademicYear(){
		firstSemester = new ArrayList<SubjectEntry>();
		secondSemester = new ArrayList<SubjectEntry>();
	}
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public List<SubjectEntry> getFirstSemester() {
		return firstSemester;
	}
	public List<SubjectEntry> getSecondSemester() {
		return secondSemester;
	}
	
	@Override
	public int compareTo(AcademicYear another) {
		return year-another.year;
	}
	
	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(year);
		dest.writeTypedList(firstSemester);
		dest.writeTypedList(secondSemester);
	}
	
	private AcademicYear(Parcel in){
		year = in.readInt();
		firstSemester = new ArrayList<SubjectEntry>();
		secondSemester = new ArrayList<SubjectEntry>();
		in.readTypedList(firstSemester, SubjectEntry.CREATOR);
		in.readTypedList(secondSemester, SubjectEntry.CREATOR);
	}
	
	public static final Parcelable.Creator<AcademicYear> CREATOR = new Parcelable.Creator<AcademicYear>() {
		public AcademicYear createFromParcel(Parcel in) {
			return new AcademicYear(in);
		}

		public AcademicYear[] newArray(int size) {
			return new AcademicYear[size];
		}
	};
}
