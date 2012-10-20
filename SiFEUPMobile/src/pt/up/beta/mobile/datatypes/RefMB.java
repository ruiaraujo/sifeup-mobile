package pt.up.beta.mobile.datatypes;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RefMB implements Parcelable {
	@SerializedName("designacao")
	private final String name;
	@SerializedName("entidade")
	private final String  entity;
	@SerializedName("referencia")
	private final String ref;
	@SerializedName("valor")
	private final double amount;
	@SerializedName("pagamento_ini")
	private final String startDate;
	@SerializedName("pagamento_fim")
	private final String endDate;

	public String getName() {
		return name;
	}

	public String getEntity() {
		return entity;
	}

	public String getRef() {
		return ref;
	}

	public double getAmount() {
		return amount;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<RefMB> CREATOR = new Parcelable.Creator<RefMB>() {
		public RefMB createFromParcel(Parcel in) {
			return new RefMB(in);
		}

		public RefMB[] newArray(int size) {
			return new RefMB[size];
		}
	};

	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, name);
		ParcelUtils.writeString(dest, entity);
		ParcelUtils.writeString(dest, ref);
		ParcelUtils.writeString(dest, startDate);
		ParcelUtils.writeString(dest, endDate);
		dest.writeDouble(amount);
	}

	private RefMB(Parcel in) {
		name = ParcelUtils.readString(in);
		entity = ParcelUtils.readString(in);
		ref = ParcelUtils.readString(in);
		startDate = ParcelUtils.readString(in);
		endDate = ParcelUtils.readString(in);
		amount = in.readDouble();
	}

}
