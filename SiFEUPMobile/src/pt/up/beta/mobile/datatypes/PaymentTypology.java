package pt.up.beta.mobile.datatypes;

import com.google.gson.annotations.SerializedName;

import pt.up.beta.mobile.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

public class PaymentTypology implements Parcelable {
	@SerializedName("nome")
	private final String name;
	@SerializedName("movimentos")
	private final Movement[] movements;

	private PaymentTypology(Parcel in) {
		name = ParcelUtils.readString(in);
		movements = new Movement[in.readInt()];
		in.readTypedArray(movements, Movement.CREATOR);
	}

	public String getName() {
		return name;
	}

	public Movement[] getMovements() {
		return movements;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ParcelUtils.writeString(dest, name);
		dest.writeInt(movements.length);
		dest.writeTypedArray(movements, flags);
	}

	public static class Movement implements Parcelable {

		@SerializedName("saldado")
		private final boolean paid;
		@SerializedName("curso_nome")
		private final String courseName;
		@SerializedName("curso_name")
		private final String courseNameEn;
		@SerializedName("curso_sigla")
		private final String courseAcronym;
		@SerializedName("descricao")
		private final String description;
		@SerializedName("d_valor")
		private final String startDate;
		@SerializedName("data_limite")
		private final String finalDate;
		@SerializedName("debito")
		private final String debt;
		@SerializedName("debito_falta")
		private final String debtMissing;
		@SerializedName("estado")
		private final String state;
		@SerializedName("referencia")
		private final String reference;
		@SerializedName("entidade")
		private final String entity;
		@SerializedName("tipo")
		private final String type;
		@SerializedName("motivo")
		private final String reason;
		@SerializedName("juros_mora")
		private final String defaultInterestes;

		private Movement(Parcel in) {
			paid = in.readByte() == 1;
			courseName = ParcelUtils.readString(in);
			courseNameEn = ParcelUtils.readString(in);
			courseAcronym = ParcelUtils.readString(in);
			description = ParcelUtils.readString(in);
			startDate = ParcelUtils.readString(in);
			finalDate = ParcelUtils.readString(in);
			debt = ParcelUtils.readString(in);
			debtMissing = ParcelUtils.readString(in);
			state = ParcelUtils.readString(in);
			reference = ParcelUtils.readString(in);
			entity = ParcelUtils.readString(in);
			type = ParcelUtils.readString(in);
			reason = ParcelUtils.readString(in);
			defaultInterestes = ParcelUtils.readString(in);
		}

		public boolean isPaid() {
			return paid;
		}

		public String getCourseName() {
			return courseName;
		}

		public String getCourseNameEn() {
			return courseNameEn;
		}

		public String getCourseAcronym() {
			return courseAcronym;
		}

		public String getDescription() {
			return description;
		}

		public String getStartDate() {
			return startDate;
		}

		public String getFinalDate() {
			return finalDate;
		}

		public String getDebt() {
			return debt;
		}

		public String getDebtMissing() {
			return debtMissing;
		}

		public String getState() {
			return state;
		}

		public String getReference() {
			return reference;
		}

		public String getEntity() {
			return entity;
		}

		public String getType() {
			return type;
		}

		public String getReason() {
			return reason;
		}

		public String getDefaultInterestes() {
			return defaultInterestes;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			if (paid)
				dest.writeInt(1);
			else
				dest.writeInt(0);
			ParcelUtils.writeString(dest, courseName);
			ParcelUtils.writeString(dest, courseNameEn);
			ParcelUtils.writeString(dest, courseAcronym);
			ParcelUtils.writeString(dest, description);
			ParcelUtils.writeString(dest, startDate);
			ParcelUtils.writeString(dest, finalDate);
			ParcelUtils.writeString(dest, debt);
			ParcelUtils.writeString(dest, debtMissing);
			ParcelUtils.writeString(dest, state);
			ParcelUtils.writeString(dest, reference);
			ParcelUtils.writeString(dest, entity);
			ParcelUtils.writeString(dest, type);
			ParcelUtils.writeString(dest, reason);
			ParcelUtils.writeString(dest, defaultInterestes);
		}

		public static final Parcelable.Creator<Movement> CREATOR = new Parcelable.Creator<Movement>() {
			public Movement createFromParcel(Parcel in) {
				return new Movement(in);
			}

			public Movement[] newArray(int size) {
				return new Movement[size];
			}
		};
	}

	public static final Parcelable.Creator<PaymentTypology> CREATOR = new Parcelable.Creator<PaymentTypology>() {
		public PaymentTypology createFromParcel(Parcel in) {
			return new PaymentTypology(in);
		}

		public PaymentTypology[] newArray(int size) {
			return new PaymentTypology[size];
		}
	};
}
