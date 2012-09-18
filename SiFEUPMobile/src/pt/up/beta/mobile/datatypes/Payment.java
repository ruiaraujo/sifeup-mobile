package pt.up.beta.mobile.datatypes;

import org.acra.ACRA;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.beta.mobile.sifeup.AccountUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Time;
import android.util.Log;

public class Payment implements Parcelable {

	static enum State {
		PAID, LATE_ON_PAYMENT, TO_BE_PAID
	}

	final private State state;
	final private String name;
	final private Time dueDate;
	final private double amount;
	final private double amountPaid;
	final private double amountDebt;

	public Payment(State state, String name, Time dueDate, double amount,
			double amountPaid, double amountDebt) {
		this.state = state;
		this.name = name;
		this.dueDate = dueDate;
		this.amount = amount;
		this.amountPaid = amountPaid;
		this.amountDebt = amountDebt;
	}

	public State getState() {
		return state;
	}

	public String getName() {
		return name;
	}

	public Time getDueDate() {
		return dueDate;
	}

	public double getAmount() {
		return amount;
	}

	public double getAmountPaid() {
		return amountPaid;
	}

	public double getAmountDebt() {
		return amountDebt;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(amount);
		dest.writeDouble(amountDebt);
		dest.writeDouble(amountPaid);
		dest.writeInt(dueDate != null ? 1 : 0);
		if (dueDate != null)
			dest.writeString(dueDate.format3339(true));
		dest.writeInt(state != null ? 1 : 0);
		if (state != null) {
			if (state == State.PAID)
				dest.writeInt(0);
			else if (state == State.TO_BE_PAID)
				dest.writeInt(1);
			else
				/* if ( state == State.LATE_ON_PAYMENT ) */
				dest.writeInt(2);
		}
		dest.writeInt(name != null ? 1 : 0);
		if (name != null)
			dest.writeString(name);
	}

	private Payment(Parcel in) {
		amount = in.readDouble();
		amountDebt = in.readDouble();
		amountPaid = in.readDouble();
		if (in.readInt() == 1) {
			dueDate = new Time();
			dueDate.parse3339(in.readString());
		} else
			dueDate = null;
		if (in.readInt() == 1) {
			switch (in.readInt()) {
			case 0:
				state = State.PAID;
				break;
			case 1:
				state = State.TO_BE_PAID;
				break;
			case 2:
				state = State.LATE_ON_PAYMENT;
				break;
			default:
				state = null;
				break;
			}
		} else
			state = null;
		if (in.readInt() == 1)
			name = in.readString();
		else
			name = null;

	}

	public static final Parcelable.Creator<Payment> CREATOR = new Parcelable.Creator<Payment>() {
		public Payment createFromParcel(Parcel in) {
			return new Payment(in);
		}

		public Payment[] newArray(int size) {
			return new Payment[size];
		}
	};

	public static Payment parseJSON(JSONObject payment) {
		try {
			final String sit = payment.getString("situacao");
			final State state;
			if (sit.equals("Valor pago na totalidade"))
				state = State.PAID;
			else if (sit
					.equals("Valor não pago tendo sido já excedido o prazo"))
				state = State.LATE_ON_PAYMENT;
			else if (sit
					.equals("Valor não pago mas o prazo ainda não foi excedido"))
				state = State.TO_BE_PAID;
			else {
				Log.e("Propinas", "estado nao contemplado");
				state = null;
			}

			final String name = payment.getString("nome_prestacao");
			final String[] dueDateString = payment.getString("data_limite_pag")
					.split("-");
			final Time dueDate;
			if (dueDateString.length == 3) {
				dueDate = new Time(Time.TIMEZONE_UTC);
				dueDate.set(Integer.parseInt(dueDateString[2]),
						Integer.parseInt(dueDateString[1]) - 1,
						Integer.parseInt(dueDateString[0]));
			} else
				dueDate = null;
			final double amount = payment.getDouble("valor");
			final double amountPaid = payment.getDouble("valor_pago");
			final double amountDebt = payment.getDouble("valor_divida");
			return new Payment(state, name, dueDate, amount, amountPaid,
					amountDebt);
		} catch (JSONException e) {
			Log.e("Propinas", "JSON error in payment");
			e.printStackTrace();

			ACRA.getErrorReporter().handleSilentException(e);
			ACRA.getErrorReporter().handleSilentException(
					new RuntimeException("Id:"
							+ AccountUtils.getActiveUserCode(null) + "\n\n"
							+ payment.toString()));
		}
		return null;
	}

}
