package pt.up.beta.mobile.datatypes;

import com.google.gson.annotations.SerializedName;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

public class DynamicMailFile implements Parcelable {
	@SerializedName("codigo")
	private final String code;
	
	@SerializedName("nome")
	private final String name;

	@SerializedName("tamanho")
	private final long size;
	
	@SerializedName("assunto")
	private final String subject;
	
	@SerializedName("data")
	private final String date;

	private DynamicMailFile(Parcel in) {
		code = ParcelUtils.readString(in);
		name = ParcelUtils.readString(in);
		subject = ParcelUtils.readString(in);
		date = ParcelUtils.readString(in);
		size = in.readLong();
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public long getSize() {
		return size;
	}

	public String getSubject() {
		return subject;
	}

	public String getDate() {
		return date;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, code);
		ParcelUtils.writeString(dest, name);
		ParcelUtils.writeString(dest, subject);
		ParcelUtils.writeString(dest, date);
		dest.writeLong(size);
	}

	public static final Parcelable.Creator<DynamicMailFile> CREATOR = new Parcelable.Creator<DynamicMailFile>() {
		public DynamicMailFile createFromParcel(Parcel in) {
			return new DynamicMailFile(in);
		}

		public DynamicMailFile[] newArray(int size) {
			return new DynamicMailFile[size];
		}
	};
}
