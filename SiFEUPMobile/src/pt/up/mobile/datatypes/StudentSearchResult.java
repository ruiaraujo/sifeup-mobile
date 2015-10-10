package pt.up.mobile.datatypes;

import pt.up.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class StudentSearchResult implements Parcelable {

	@SerializedName("codigo")
	private final String code;
	@SerializedName("nome")
	private final String name;
	@SerializedName("curso")
	private final String course;
	@SerializedName("inst_nome")
	private final String facultyName;
	@SerializedName("inst_sigla")
	private final String facultyAcronym;
	@SerializedName("estado")
	private final String state;
	@SerializedName("a_lectivo")
	private final int currentYear;
	@SerializedName("a_lect_1_insc")
	private final int firstYear;
	
	private StudentSearchResult(Parcel in){
		code = ParcelUtils.readString(in);
		name = ParcelUtils.readString(in);
		course = ParcelUtils.readString(in);
		facultyName = ParcelUtils.readString(in);
		facultyAcronym = ParcelUtils.readString(in);
		state = ParcelUtils.readString(in);
		currentYear = in.readInt();
		firstYear = in.readInt();
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getFacultyName() {
		return facultyName;
	}

	public String getFacultyAcronym() {
		return facultyAcronym;
	}

	public String getState() {
		return state;
	}

	public int getCurrentYear() {
		return currentYear;
	}

	public int getFirstYear() {
		return firstYear;
	}

	public String getCourse() {
		return course;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, code);
		ParcelUtils.writeString(dest, name);
		ParcelUtils.writeString(dest, course);
		ParcelUtils.writeString(dest, facultyName);
		ParcelUtils.writeString(dest, facultyAcronym);
		ParcelUtils.writeString(dest, state);
		dest.writeInt(currentYear);
		dest.writeInt(firstYear);
	}

	public static final Parcelable.Creator<StudentSearchResult> CREATOR = new Parcelable.Creator<StudentSearchResult>() {
		public StudentSearchResult createFromParcel(Parcel in) {
			return new StudentSearchResult(in);
		}
	
		public StudentSearchResult[] newArray(int size) {
			return new StudentSearchResult[size];
		}
	};
}
