package pt.up.beta.mobile.datatypes;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RoomSearchResult implements Parcelable {

	@SerializedName("id")
	private final String code;
	@SerializedName("sigla")
	private final String name;
	@SerializedName("desc")
	private final String desc;
	@SerializedName("edificio_nome")
	private final String buildingName;
	@SerializedName("edificio_sigla")
	private final String buildingAcronym;
	@SerializedName("edificio_id")
	private final String buildingId;
	@SerializedName("activo")
	private final String active;
	@SerializedName("piso")
	private final int floor;

	private RoomSearchResult(Parcel in) {
		code = ParcelUtils.readString(in);
		name = ParcelUtils.readString(in);
		desc = ParcelUtils.readString(in);
		buildingName = ParcelUtils.readString(in);
		buildingAcronym = ParcelUtils.readString(in);
		buildingId = ParcelUtils.readString(in);
		active = ParcelUtils.readString(in);
		floor = in.readInt();
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public String getBuildingAcronym() {
		return buildingAcronym;
	}

	public String getBuildingId() {
		return buildingId;
	}

	public int getFloor() {
		return floor;
	}

	public String getDescription() {
		return desc;
	}

	public boolean isActive() {
		if (active == null)
			return false;
		return active.equals("S");
	}
	
	public String getFullName() {
		final StringBuilder st = new StringBuilder();
		if ( buildingAcronym != null )
			st.append(buildingAcronym);
		st.append(name);
		return st.toString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, code);
		ParcelUtils.writeString(dest, name);
		ParcelUtils.writeString(dest, desc);
		ParcelUtils.writeString(dest, buildingName);
		ParcelUtils.writeString(dest, buildingAcronym);
		ParcelUtils.writeString(dest, buildingId);
		ParcelUtils.writeString(dest, active);
		dest.writeInt(floor);
	}

	public static final Parcelable.Creator<RoomSearchResult> CREATOR = new Parcelable.Creator<RoomSearchResult>() {
		public RoomSearchResult createFromParcel(Parcel in) {
			return new RoomSearchResult(in);
		}
	
		public RoomSearchResult[] newArray(int size) {
			return new RoomSearchResult[size];
		}
	};
}
