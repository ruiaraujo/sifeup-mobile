package pt.up.beta.mobile.datatypes;

import java.util.ArrayList;
import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.utils.ParcelUtils;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Class Employee
 * 
 * @author Ângela Igreja
 * 
 */
public class Employee extends Profile implements Parcelable {

	/** Employee acronym - "GAOS" */
	@SerializedName("sigla")
	private final String acronym;

	/** Employee State - "A" */
	@SerializedName("estado")
	private final String state;

	/** Employee External Phone. May be empty. */
	@SerializedName("ext_tel")
	private final String extPhone;

	/** Employee Tele Alternative. May be empty. */
	@SerializedName("tele_alt")
	private final String teleAlt;

	/** Employee Voip Ext. - 3084 */
	@SerializedName("voip_ext")
	private final String voipExt;

	/** Employee Presentation. May be empty. */
	@SerializedName("apresentacao")
	private final String presentation;

	/** Employee Presentation. May be empty. */
	@SerializedName("apresentacao_uk")
	private final String presentationEn;

	/** Employee Rooms - D109 */
	@SerializedName("salas")
	private Room[] rooms;
	
	/** Employee Interests */
	@SerializedName("interesses")
	private Interest[] interests;

	public String getAcronym() {
		return acronym;
	}

	public String getState() {
		return state;
	}

	public List<ProfileDetail> getProfileContents(Resources res) {
		List<ProfileDetail> result = new ArrayList<ProfileDetail>();
		if (!TextUtils.isEmpty(getCode())) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_code), getCode(), null));
		}
		if (!TextUtils.isEmpty(getEmail())) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_email), getEmail(),
					Type.EMAIL));
		}
		if (!TextUtils.isEmpty(getEmailAlt())) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_email_alt),
					getEmailAlt(), Type.EMAIL));
		}
		if (!TextUtils.isEmpty(getMobilePhone())) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_mobile),
					getMobilePhone(), Type.MOBILE));
		}
		if (!TextUtils.isEmpty(getPhone())) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_telephone), getPhone(),
					Type.MOBILE));
		}
		if (!TextUtils.isEmpty(extPhone)) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_ext_telephone), extPhone,
					null));
		}
		if (!TextUtils.isEmpty(teleAlt)) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_ext_mobile), teleAlt,
					Type.MOBILE));
		}
		if (!TextUtils.isEmpty(voipExt)) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_ext_voip), voipExt
					.toString(), null));
		}
		if (!TextUtils.isEmpty(getWebPage())) {
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_website), getWebPage(),
					Type.WEBPAGE));
		}

		for (Room r : rooms) {
			// 3 is the regular ammount. This is loop only runs in the case
			// of positive number under 100
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_room), r.acronym,
					Type.ROOM));
		}
		
		final StringBuilder st = new StringBuilder();
		for ( Interest interest : interests ){
			if ( st.length() > 0 )
				st.append(", ");
			st.append(interest.name);
		}
		if ( st.length() > 0 )
			result.add(new ProfileDetail(res
					.getString(R.string.profile_title_interests), st.toString(),
					null));
		return result;
	}

	@Override
	public String getType() {
		return SifeupAPI.EMPLOYEE_TYPE;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		ParcelUtils.writeString(dest, acronym);
		ParcelUtils.writeString(dest, state);
		ParcelUtils.writeString(dest, extPhone);
		ParcelUtils.writeString(dest, teleAlt);
		ParcelUtils.writeString(dest, voipExt);
		ParcelUtils.writeString(dest, presentation);
		ParcelUtils.writeString(dest, presentationEn);
		dest.writeParcelableArray(rooms, flags);
		dest.writeParcelableArray(interests, flags);
	}

	private Employee(Parcel in) {
		super(in);
		acronym = ParcelUtils.readString(in);
		state = ParcelUtils.readString(in);
		extPhone = ParcelUtils.readString(in);
		teleAlt = ParcelUtils.readString(in);
		voipExt = ParcelUtils.readString(in);
		presentation = ParcelUtils.readString(in);
		presentationEn = ParcelUtils.readString(in);
		rooms = (Room[]) in.readParcelableArray(Room.class.getClassLoader());
		interests = (Interest[]) in.readParcelableArray(Interest.class.getClassLoader());
	}

	public static final Parcelable.Creator<Employee> CREATOR = new Parcelable.Creator<Employee>() {
		public Employee createFromParcel(Parcel in) {
			return new Employee(in);
		}

		public Employee[] newArray(int size) {
			return new Employee[size];
		}
	};

	/**
	 * Class Room
	 * 
	 * @author Ângela Igreja
	 * 
	 */
	public static class Room implements Parcelable {
		/** Edi Code - "D" */
		@SerializedName("id")
		private final String id;

		/** Room Code - 109 */
		@SerializedName("sigla")
		private final String acronym;

		private Room(Parcel in) {
			this.id = ParcelUtils.readString(in);
			this.acronym = ParcelUtils.readString(in);
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			ParcelUtils.writeString(dest, id);
			ParcelUtils.writeString(dest, acronym);
		}

		public static final Parcelable.Creator<Room> CREATOR = new Parcelable.Creator<Room>() {
			public Room createFromParcel(Parcel in) {
				return new Room(in);
			}

			public Room[] newArray(int size) {
				return new Room[size];
			}
		};
	}
	
	/**
	 * Class Room
	 * 
	 * @author Ângela Igreja
	 * 
	 */
	public static class Interest implements Parcelable {
		@SerializedName("nome")
		private final String name;

		private Interest(Parcel in) {
			this.name = ParcelUtils.readString(in);
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			ParcelUtils.writeString(dest, name);
		}

		public static final Parcelable.Creator<Interest> CREATOR = new Parcelable.Creator<Interest>() {
			public Interest createFromParcel(Parcel in) {
				return new Interest(in);
			}

			public Interest[] newArray(int size) {
				return new Interest[size];
			}
		};
	}
}
