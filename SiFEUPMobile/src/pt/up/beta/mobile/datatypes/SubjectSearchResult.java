package pt.up.beta.mobile.datatypes;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SubjectSearchResult implements Parcelable {
	@SerializedName("ocorr_id")
	private final String id;
	@SerializedName("codigo")
	private final String code;
	@SerializedName("nome")
	private final String name;
	@SerializedName("name")
	private final String nameEn;
	@SerializedName("periodo")
	private final String period;
	@SerializedName("data_inicio")
	private final String dateBegin;
	@SerializedName("data_fim")
	private final String dateEnd;
	@SerializedName("ano_lectivo")
	private final int year;
	
	private SubjectSearchResult(Parcel in){
		id = ParcelUtils.readString(in);
		code = ParcelUtils.readString(in);
		name = ParcelUtils.readString(in);
		nameEn = ParcelUtils.readString(in);
		period = ParcelUtils.readString(in);
		dateBegin = ParcelUtils.readString(in);
		dateEnd = ParcelUtils.readString(in);
		year = in.readInt();
	}

	public String getId() {
		return id;
	}
	
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getPeriod() {
		return period;
	}

	public String getDateBegin() {
		return dateBegin;
	}

	public String getDateEnd() {
		return dateEnd;
	}

	public int getYear() {
		return year;
	}

	public String getNameEn() {
		return nameEn;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, id);
		ParcelUtils.writeString(dest, code);
		ParcelUtils.writeString(dest, name);
		ParcelUtils.writeString(dest, nameEn);
		ParcelUtils.writeString(dest, period);
		ParcelUtils.writeString(dest, dateBegin);
		ParcelUtils.writeString(dest, dateEnd);
		dest.writeInt(year);
	}

	public static final Parcelable.Creator<SubjectSearchResult> CREATOR = new Parcelable.Creator<SubjectSearchResult>() {
		public SubjectSearchResult createFromParcel(Parcel in) {
			return new SubjectSearchResult(in);
		}
	
		public SubjectSearchResult[] newArray(int size) {
			return new SubjectSearchResult[size];
		}
	};
}
