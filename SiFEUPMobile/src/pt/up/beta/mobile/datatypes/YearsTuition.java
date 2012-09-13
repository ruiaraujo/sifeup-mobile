package pt.up.beta.mobile.datatypes;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

public class YearsTuition implements Parcelable {
	public static final int currentYear = 0;

	final private String year;
	final private String courseCode;
	final private String courseName;
	final private String state;
	final private String type;
	final private double totalPaid;
	final private double totalDebt;
	final private ArrayList<Payment> payments;
	final private ArrayList<RefMB> references;

	public YearsTuition(String year, String courseCode, String courseName,
			String state, String type, ArrayList<Payment> payments,
			double total_paid, double total_in_debt, ArrayList<RefMB> references) {
		this.year = year;
		this.courseCode = courseCode;
		this.courseName = courseName;
		this.state = state;
		this.type = type;
		this.payments = payments;
		this.totalPaid = total_paid;
		this.totalDebt = total_in_debt;
		this.references = references;
	}

	public String getYear() {
		return year;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public String getCourseName() {
		return courseName;
	}

	public String getState() {
		return state;
	}

	public String getType() {
		return type;
	}

	public ArrayList<Payment> getPayments() {
		return payments;
	}

	public double getTotal_paid() {
		return totalPaid;
	}

	public double getTotal_in_debt() {
		return totalDebt;
	}

	public ArrayList<RefMB> getReferences() {
		return references;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(totalDebt);
		dest.writeDouble(totalPaid);
		dest.writeInt(year != null ? 1 : 0);
		if (year != null)
			dest.writeString(year);
		dest.writeInt(courseCode != null ? 1 : 0);
		if (courseCode != null)
			dest.writeString(courseCode);
		dest.writeInt(courseName != null ? 1 : 0);
		if (courseName != null)
			dest.writeString(courseName);
		dest.writeInt(state != null ? 1 : 0);
		if (state != null)
			dest.writeString(state);
		dest.writeInt(type != null ? 1 : 0);
		if (type != null)
			dest.writeString(type);
		dest.writeTypedList(payments);
		dest.writeTypedList(references);
	}

	private YearsTuition(Parcel in) {
		totalDebt = in.readDouble();
		totalPaid = in.readDouble();
		if (in.readInt() == 1)
			year = in.readString();
		else
			year = null;
		if (in.readInt() == 1)
			courseCode = in.readString();
		else
			courseCode = null;
		if (in.readInt() == 1)
			courseName = in.readString();
		else
			courseName = null;
		if (in.readInt() == 1)
			state = in.readString();
		else
			state = null;
		if (in.readInt() == 1)
			type = in.readString();
		else
			type = null;
		payments = in.createTypedArrayList(Payment.CREATOR);
		references = in.createTypedArrayList(RefMB.CREATOR);

	}

	public static final Parcelable.Creator<YearsTuition> CREATOR = new Parcelable.Creator<YearsTuition>() {
		public YearsTuition createFromParcel(Parcel in) {
			return new YearsTuition(in);
		}

		public YearsTuition[] newArray(int size) {
			return new YearsTuition[size];
		}
	};

	public static YearsTuition parseJSON(JSONObject yearInfo) {
		try {
			final String year = yearInfo.getString("ano_lectivo");
			final String courseCode = yearInfo.getString("curso_sigla");
			final String courseName = yearInfo.getString("curso_nome");
			final String state = yearInfo.getString("estado");
			// may not be present if student has applied for a
			// scholarship
			final String type = yearInfo.optString("tipo");
			// If it is empty there is no more data to load
			if (TextUtils.isEmpty(type))
				return new YearsTuition(year, courseCode, courseName, state,
						type, new ArrayList<Payment>(), 0, 0, null);

			JSONArray jChild = yearInfo.getJSONArray("planos_pag");

			JSONObject jPayment = jChild.getJSONObject(0);
			JSONArray jPayments = jPayment.getJSONArray("prestacoes");

			ArrayList<Payment> payments = new ArrayList<Payment>();
			for (int i = 0; i < jPayments.length(); i++) {
				final Payment p = Payment.parseJSON(jPayments.getJSONObject(i));
				if (p != null)
					payments.add(p);
				else
					return null;
			}
			double total_paid = 0;
			double total_in_debt = 0;
			for (Payment p : payments) {
				total_paid += p.getAmountPaid();
				total_in_debt += p.getAmountDebt();
			}

			final ArrayList<RefMB> references = new ArrayList<RefMB>();
			JSONArray jRefs = yearInfo.optJSONArray("referencias");
			if (jRefs != null) {
				for (int i = 0; i < jRefs.length(); i++) {
					RefMB r = new RefMB();
					if (r.load(jRefs.getJSONObject(i)))
						references.add(r);
					else
						return null;
				}
			}
			return new YearsTuition(year, courseCode, courseName, state, type,
					payments, total_paid, total_in_debt, references);
		} catch (JSONException e) {
			Log.e("Propinas", "JSON error in year");
			e.printStackTrace();
			Log.e("Propina", e.getMessage());
			Log.e("Propina", yearInfo.toString());
			// TODO: report bug
			return null;

		}
	}

	public static List<YearsTuition> parseListJSON(JSONObject historyInfo) {
		List<YearsTuition> history = new ArrayList<YearsTuition>();
		try {
			final JSONArray jHistory = historyInfo.getJSONArray("pagamentos");
			for (int i = 0; i < jHistory.length(); i++) {
				try {
					YearsTuition yT = YearsTuition.parseJSON(jHistory
							.getJSONObject(i));
					if (yT != null)
						history.add(yT);
					else
						return null;
				} catch (JSONException e) {
					Log.e("Propinas",
							"Error getting the year from the JSON list in tuitions");
					e.printStackTrace();
					return null; // TODO: report this bug
				}
			}
		} catch (JSONException e1) {
			Log.e("Propinas", "error getting the array pagamentos");
			e1.printStackTrace();
			return null;
		}
		Log.i("Propinas", "Loaded history");
		return history;
	}

}
