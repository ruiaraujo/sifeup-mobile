package pt.up.beta.mobile.datatypes;

import java.util.List;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public abstract class Profile implements Parcelable {

	/** Employee code - "419454" */
	@SerializedName("codigo")
	private final String code;

	/** Employee name - "Gil António Oliveira da Silva" */
	@SerializedName("nome")
	private final String name;

	/** Employee email - "gils@fe.up.pt" */
	@SerializedName("email")
	private final String email;

	/** Employee alternative email. May be empty. */
	@SerializedName("email_alt")
	private final String emailAlt;

	/** Employee Phone - "22 557 4109" */
	@SerializedName("telefone")
	private final String phone;

	/** Employee Mobile Phone - 913970682 */
	@SerializedName("telemovel")
	private final String mobilePhone;

	/** Web page. May be empty. */
	@SerializedName("pagina_web")
	private final String webPage;

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getEmailAlt() {
		return emailAlt;
	}

	public String getPhone() {
		return phone;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public String getFirstName() {
		if (name == null)
			return null;
		final String[] names = name.split(" ");
		if (names.length > 0)
			return names[0];
		return null;
	}

	public String getLastName() {
		if (name == null)
			return null;
		final String[] names = name.split(" ");
		if (names.length > 0)
			return names[names.length - 1];
		return null;
	}

	public String getShortName() {
		final StringBuilder st = new StringBuilder();
		final String first = getFirstName();
		final String last = getLastName();
		if (!TextUtils.isEmpty(first)) {
			st.append(first);
			st.append(' ');
		}
		if (!TextUtils.isEmpty(last))
			st.append(last);
		return st.toString();
	}

	public String getWebPage() {
		return webPage;
	}

	public interface Type {
		String EMAIL = "email";
		String MOBILE = "mobile";
		String WEBPAGE = "webpage";
		String ROOM = "room";
	}

	/**
	 * 
	 * @author Rui Araújo
	 * 
	 */
	public class ProfileDetail {
		public String title;
		public String content;
		public String type;

		public ProfileDetail(String title, String content, String type) {
			this.title = title;
			this.content = content;
			this.type = type;
		}

	}

	abstract public List<ProfileDetail> getProfileContents(Resources res);

	abstract public String getType();

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, code);
		ParcelUtils.writeString(dest, name);
		ParcelUtils.writeString(dest, email);
		ParcelUtils.writeString(dest, emailAlt);
		ParcelUtils.writeString(dest, phone);
		ParcelUtils.writeString(dest, mobilePhone);
		ParcelUtils.writeString(dest, webPage);
	}

	protected Profile(Parcel in) {
		code = ParcelUtils.readString(in);
		name = ParcelUtils.readString(in);
		email = ParcelUtils.readString(in);
		emailAlt = ParcelUtils.readString(in);
		phone = ParcelUtils.readString(in);
		mobilePhone = ParcelUtils.readString(in);
		webPage = ParcelUtils.readString(in);
	}

	@Override
	public String toString() {
		return name;
	}

}
