package pt.up.mobile.datatypes;

import pt.up.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Class Notification
 * 
 * @author �ngela Igreja
 * 
 */
public class Notification implements Parcelable {

	/** Notification Code */
	@SerializedName("codigo")
	private final String code;

	/** Notification Link */
	@SerializedName("link")
	private final String link;

	/** Designation. Ex: Avisos */
	@SerializedName("designacao")
	private final String designation;

	/** Subject English name - */
	@SerializedName("descricao")
	private final String description;

	/** Subject acronym - */
	@SerializedName("beneficiario")
	private final String beneficiary;

	/** */
	@SerializedName("prioridade")
	private final int priority;

	/** */
	@SerializedName("data")
	private final String date;

	/** */
	@SerializedName("assunto")
	private final String subject;

	/** */
	@SerializedName("mensagem")
	private final String message;

	/** */
	@SerializedName("obs")
	private final String obs;

	@SerializedName("lida")
	private boolean read;

	private Notification(Parcel in) {
		code = ParcelUtils.readString(in);
		priority = in.readInt();
		link = ParcelUtils.readString(in);
		designation = ParcelUtils.readString(in);
		description = ParcelUtils.readString(in);
		beneficiary = ParcelUtils.readString(in);
		date = ParcelUtils.readString(in);
		subject = ParcelUtils.readString(in);
		message = ParcelUtils.readString(in);
		obs = ParcelUtils.readString(in);
		read = ParcelUtils.readBoolean(in);
	}

	public String getLink() {
		return link;
	}

	public String getDesignation() {
		return designation;
	}

	public String getDescription() {
		return description;
	}

	public String getBeneficiary() {
		return beneficiary;
	}

	public int getPriority() {
		return priority;
	}

	public String getCode() {
		return code;
	}

	public String getDate() {
		return date;
	}

	public String getSubject() {
		return subject;
	}

	public String getMessage() {
		return message;
	}

	public String getObs() {
		return obs;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, code);
		dest.writeInt(priority);
		ParcelUtils.writeString(dest, link);
		ParcelUtils.writeString(dest, designation);
		ParcelUtils.writeString(dest, description);
		ParcelUtils.writeString(dest, beneficiary);
		ParcelUtils.writeString(dest, date);
		ParcelUtils.writeString(dest, subject);
		ParcelUtils.writeString(dest, message);
		ParcelUtils.writeString(dest, obs);
		ParcelUtils.writeBoolean(dest, read);
	}
	public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() {
		public Notification createFromParcel(Parcel in) {
			return new Notification(in);
		}

		public Notification[] newArray(int size) {
			return new Notification[size];
		}
	};
}
