package pt.up.mobile.datatypes;

import com.google.gson.annotations.SerializedName;

import pt.up.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

public class RoomProfile implements Parcelable {
	@SerializedName("id")
	private final String code;
	@SerializedName("sigla")
	private final String name;
	@SerializedName("edificio_nome")
	private final String buildingName;
	@SerializedName("edificio_sigla")
	private final String buildingAcronym;
	@SerializedName("edificio_id")
	private final String buildingId;
	@SerializedName("utilizacao")
	private final String usage;
	@SerializedName("area")
	private final String area;
	@SerializedName("piso")
	private final int floor;
	@SerializedName("atributos")
	private final Attributes[] atributes;
	@SerializedName("responsaveis")
	private final People[] responsible;
	@SerializedName("ocupantes")
	private final People[] occupiers;

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
	
	public String getFullName() {
		final StringBuilder st = new StringBuilder();
		if ( buildingAcronym != null )
			st.append(buildingAcronym);
		st.append(name);
		return st.toString();
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

	public String getUsage() {
		return usage;
	}

	public String getArea() {
		return area;
	}

	public int getFloor() {
		return floor;
	}

	public Attributes[] getAtributes() {
		return atributes;
	}

	public People[] getResponsible() {
		return responsible;
	}

	public People[] getOccupiers() {
		return occupiers;
	}

	private RoomProfile(Parcel in) {
		code = ParcelUtils.readString(in);
		name = ParcelUtils.readString(in);
		usage = ParcelUtils.readString(in);
		area = ParcelUtils.readString(in);
		buildingName = ParcelUtils.readString(in);
		buildingAcronym = ParcelUtils.readString(in);
		buildingId = ParcelUtils.readString(in);
		floor = in.readInt();
		atributes = new Attributes[in.readInt()];
		in.readTypedArray(atributes, Attributes.CREATOR);
		responsible = new People[in.readInt()];
		in.readTypedArray(responsible, People.CREATOR);
		occupiers = new People[in.readInt()];
		in.readTypedArray(occupiers, People.CREATOR);

	}

	public static class Attributes implements Parcelable {
		@SerializedName("nome")
		private final String name;
		public String getName() {
			return name;
		}

		public String getContent() {
			return content;
		}

		@SerializedName("conteudo")
		private final String content;

		private Attributes(Parcel in) {
			name = ParcelUtils.readString(in);
			content = ParcelUtils.readString(in);
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			ParcelUtils.writeString(dest, name);
			ParcelUtils.writeString(dest, content);
		}

		public static final Parcelable.Creator<Attributes> CREATOR = new Parcelable.Creator<Attributes>() {
			public Attributes createFromParcel(Parcel in) {
				return new Attributes(in);
			}

			public Attributes[] newArray(int size) {
				return new Attributes[size];
			}
		};
	}

	public static class People implements Parcelable {
		@SerializedName("tipo")
		private final String type;
		@SerializedName("codigo")
		private final String code;
		@SerializedName("nome")
		private final String name;

		private People(Parcel in) {
			type = ParcelUtils.readString(in);
			code = ParcelUtils.readString(in);
			name = ParcelUtils.readString(in);
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			ParcelUtils.writeString(dest, type);
			ParcelUtils.writeString(dest, code);
			ParcelUtils.writeString(dest, name);
		}
		public static final Parcelable.Creator<People> CREATOR = new Parcelable.Creator<People>() {
			public People createFromParcel(Parcel in) {
				return new People(in);
			}

			public People[] newArray(int size) {
				return new People[size];
			}
		};

		public String getType() {
			return type;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public boolean isPerson() {
			return type.equals(EMPLOYEE_TYPE);
		}
		
		private final static String EMPLOYEE_TYPE = "funcionario";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, code);
		ParcelUtils.writeString(dest, name);
		ParcelUtils.writeString(dest, usage);
		ParcelUtils.writeString(dest, area);
		ParcelUtils.writeString(dest, buildingName);
		ParcelUtils.writeString(dest, buildingAcronym);
		ParcelUtils.writeString(dest, buildingId);
		dest.writeInt(floor);
		dest.writeInt(atributes.length);
		dest.writeTypedArray(atributes, flags);
		dest.writeInt(responsible.length);
		dest.writeTypedArray(responsible, flags);
		dest.writeInt(occupiers.length);
		dest.writeTypedArray(occupiers, flags);
	}

	public static final Parcelable.Creator<RoomProfile> CREATOR = new Parcelable.Creator<RoomProfile>() {
		public RoomProfile createFromParcel(Parcel in) {
			return new RoomProfile(in);
		}

		public RoomProfile[] newArray(int size) {
			return new RoomProfile[size];
		}
	};
}
