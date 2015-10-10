package pt.up.mobile.datatypes;

import pt.up.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class EmployeeSearchResult implements Parcelable {

	@SerializedName("codigo")
	private final String code;
	@SerializedName("nome")
	private final String name;
	@SerializedName("curso")
	private final String department;
	@SerializedName("inst_nome")
	private final String facultyName;
	@SerializedName("inst_sigla")
	private final String facultyAcronym;
	@SerializedName("sigla")
	private final String acronym;
	
	private EmployeeSearchResult(Parcel in){
		code = ParcelUtils.readString(in);
		name = ParcelUtils.readString(in);
		department = ParcelUtils.readString(in);
		facultyName = ParcelUtils.readString(in);
		facultyAcronym = ParcelUtils.readString(in);
		acronym = ParcelUtils.readString(in);
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

	public String getAcronym() {
		return acronym;
	}

	public String getDepartment() {
		return department;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, code);
		ParcelUtils.writeString(dest, name);
		ParcelUtils.writeString(dest, department);
		ParcelUtils.writeString(dest, facultyName);
		ParcelUtils.writeString(dest, facultyAcronym);
		ParcelUtils.writeString(dest, acronym);
	}

	
	public static final Parcelable.Creator<EmployeeSearchResult> CREATOR = new Parcelable.Creator<EmployeeSearchResult>() {
		public EmployeeSearchResult createFromParcel(Parcel in) {
			return new EmployeeSearchResult(in);
		}
	
		public EmployeeSearchResult[] newArray(int size) {
			return new EmployeeSearchResult[size];
		}
	};

}
