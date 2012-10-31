package pt.up.beta.mobile.datatypes;

import com.google.gson.annotations.SerializedName;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

public class EmployeeMarkings implements Parcelable {

	@SerializedName("existe_erro")
	private final boolean error;
	@SerializedName("data")
	private final String date;
	@SerializedName("saldo")
	private final String balance;
	@SerializedName("saldo_acumulado")
	private final String accumulatedBalance;
	@SerializedName("injustificado")
	private final String injustifiedBalance;
	@SerializedName("injustificado_acumulado")
	private final String accumulatedInjustifiedBalance;
	@SerializedName("justificacoes")
	private final Justification[] justifications;
	@SerializedName("picagem_manha")
	private final PunchIn[] morning;
	@SerializedName("picagem_tarde")
	private final PunchIn[] afternoon;

	private EmployeeMarkings(Parcel in) {
		error = in.readInt() == 1;
		date = ParcelUtils.readString(in);
		balance = ParcelUtils.readString(in);
		accumulatedBalance = ParcelUtils.readString(in);
		injustifiedBalance = ParcelUtils.readString(in);
		accumulatedInjustifiedBalance = ParcelUtils.readString(in);
		justifications = new Justification[in.readInt()];
		in.readTypedArray(justifications, Justification.CREATOR);
		morning = new PunchIn[in.readInt()];
		in.readTypedArray(morning, PunchIn.CREATOR);
		afternoon = new PunchIn[in.readInt()];
		in.readTypedArray(afternoon, PunchIn.CREATOR);
	}

	public boolean isError() {
		return error;
	}

	public String getDate() {
		return date;
	}

	public String getBalance() {
		return balance;
	}

	public String getAccumulatedBalance() {
		return accumulatedBalance;
	}

	public String getInjustifiedBalance() {
		return injustifiedBalance;
	}

	public String getAccumulatedInjustifiedBalance() {
		return accumulatedInjustifiedBalance;
	}

	public Justification[] getJustifications() {
		return justifications;
	}

	public PunchIn[] getMorning() {
		return morning;
	}

	public PunchIn[] getAfternoon() {
		return afternoon;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (error)
			dest.writeInt(1);
		else
			dest.writeInt(0);
		ParcelUtils.writeString(dest, date);
		ParcelUtils.writeString(dest, balance);
		ParcelUtils.writeString(dest, accumulatedBalance);
		ParcelUtils.writeString(dest, injustifiedBalance);
		ParcelUtils.writeString(dest, accumulatedInjustifiedBalance);
		dest.writeInt(justifications.length);
		dest.writeTypedArray(justifications, flags);
		dest.writeInt(morning.length);
		dest.writeTypedArray(morning, flags);
		dest.writeInt(afternoon.length);
		dest.writeTypedArray(afternoon, flags);

	}

	public static final Parcelable.Creator<EmployeeMarkings> CREATOR = new Parcelable.Creator<EmployeeMarkings>() {
		public EmployeeMarkings createFromParcel(Parcel in) {
			return new EmployeeMarkings(in);
		}

		public EmployeeMarkings[] newArray(int size) {
			return new EmployeeMarkings[size];
		}
	};

	public static class PunchIn implements Parcelable {
		@SerializedName("hora")
		private final String time;
		@SerializedName("estado_just")
		private final String justificationState;

		private PunchIn(Parcel in) {
			time = ParcelUtils.readString(in);
			justificationState = ParcelUtils.readString(in);
		}

		public String getTime() {
			return time;
		}

		public String getJustificationState() {
			return justificationState;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			ParcelUtils.writeString(dest, time);
			ParcelUtils.writeString(dest, justificationState);
		}

		public static final Parcelable.Creator<PunchIn> CREATOR = new Parcelable.Creator<PunchIn>() {
			public PunchIn createFromParcel(Parcel in) {
				return new PunchIn(in);
			}

			public PunchIn[] newArray(int size) {
				return new PunchIn[size];
			}
		};
	}

	public static class Justification implements Parcelable {
		@SerializedName("id")
		private final String id;
		@SerializedName("estado")
		private final String state;
		@SerializedName("hora_inicio")
		private final String startTime;
		@SerializedName("hora_fim")
		private final String endTime;
		@SerializedName("observacoes")
		private final String obs;
		@SerializedName("tipo_justif")
		private final String justificationType;
		@SerializedName("descr_justif")
		private final String justificationDescr;
		@SerializedName("data_inicio")
		private final String startDate;
		@SerializedName("data_fim")
		private final String endDate;

		private Justification(Parcel in) {
			id = ParcelUtils.readString(in);
			state = ParcelUtils.readString(in);
			startTime = ParcelUtils.readString(in);
			endTime = ParcelUtils.readString(in);
			obs = ParcelUtils.readString(in);
			justificationType = ParcelUtils.readString(in);
			justificationDescr = ParcelUtils.readString(in);
			startDate = ParcelUtils.readString(in);
			endDate = ParcelUtils.readString(in);
		}

		public String getId() {
			return id;
		}

		public String getState() {
			return state;
		}

		public String getStartTime() {
			return startTime;
		}

		public String getEndTime() {
			return endTime;
		}

		public String getObs() {
			return obs;
		}

		public String getJustificationType() {
			return justificationType;
		}

		public String getJustificationDescr() {
			return justificationDescr;
		}

		public String getStartDate() {
			return startDate;
		}

		public String getEndDate() {
			return endDate;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			ParcelUtils.writeString(dest, id);
			ParcelUtils.writeString(dest, state);
			ParcelUtils.writeString(dest, startTime);
			ParcelUtils.writeString(dest, endTime);
			ParcelUtils.writeString(dest, obs);
			ParcelUtils.writeString(dest, justificationType);
			ParcelUtils.writeString(dest, justificationDescr);
			ParcelUtils.writeString(dest, startDate);
			ParcelUtils.writeString(dest, endDate);

		}

		public static final Parcelable.Creator<Justification> CREATOR = new Parcelable.Creator<Justification>() {
			public Justification createFromParcel(Parcel in) {
				return new Justification(in);
			}

			public Justification[] newArray(int size) {
				return new Justification[size];
			}
		};
	}

}
