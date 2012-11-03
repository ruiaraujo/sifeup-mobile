package pt.up.beta.mobile.ui.services.current_account;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.datatypes.PaymentTypology.Movement;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TypologyAdapter extends BaseAdapter {

	final LayoutInflater mInflater;
	final Context context;
	final Movement[] movements;

	public TypologyAdapter(Context context, Movement[] objects) {
		this.movements = objects;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return movements.length;
	}

	@Override
	public Object getItem(int position) {
		return movements[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_current_account,
					parent, false);
		}
		final Movement mov = movements[position];
		final TextView desc = (TextView) convertView
				.findViewById(R.id.description);
		desc.setText(mov.getCourseAcronym() == null ? mov.getDescription()
				: mov.getCourseAcronym() + " - " + mov.getDescription());
		final TextView dates = (TextView) convertView
				.findViewById(R.id.dates);
		dates.setText(context.getString(R.string.lbl_date, mov.getStartDate(),
				mov.getFinalDate() == null ? "..." : mov.getFinalDate()));
		if ( mov.getState().equals("Anulado") ){
			dates.setTextColor(context.getResources().getColor(R.color.light_gray));
			desc.setTextColor(context.getResources().getColor(R.color.light_gray));
		}
		else
		{
			dates.setTextColor(context.getResources().getColor(R.color.body_text_2));
			desc.setTextColor(context.getResources().getColor(R.color.body_text_1));
		}
		return convertView;
	}

}
