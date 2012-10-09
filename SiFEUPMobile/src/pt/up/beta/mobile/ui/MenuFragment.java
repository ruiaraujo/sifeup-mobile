package pt.up.beta.mobile.ui;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.ui.notifications.NotificationsActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MenuFragment extends ListFragment implements OnItemClickListener {

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MenuAdapter adapter = new MenuAdapter(getActivity());
		adapter.add(new MenuEntry(R.string.btn_student_area, R.drawable.home_btn_sessions));
		adapter.add(new MenuEntry(R.string.btn_student_services, R.drawable.home_btn_printer));
		adapter.add(new MenuEntry(R.string.btn_friends, R.drawable.home_btn_friends));
		adapter.add(new MenuEntry(R.string.btn_news, R.drawable.home_btn_news));
		adapter.add(new MenuEntry(R.string.btn_map, R.drawable.home_btn_map));
		adapter.add(new MenuEntry(R.string.btn_notifications, R.drawable.home_btn_announcements));
		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
	}

	private class MenuEntry {
		private final int tag;
		private final int iconRes;
		public MenuEntry(int tag, int iconRes) {
			this.tag = tag; 
			this.iconRes = iconRes;
		}
	}

	public class MenuAdapter extends ArrayAdapter<MenuEntry> {

		public MenuAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.main_menu_entry, null);
			}
			TextView title = (TextView) convertView.findViewById(android.R.id.text1);
			title.setCompoundDrawables(getResources().getDrawable(getItem(position).iconRes), null,null,null);
			title.setText(getItem(position).tag);

			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int positiong, long id) {
		switch (positiong) {
		case 5:
			startActivity(new Intent(getActivity(), NotificationsActivity.class));
			break;

		default:
			break;
		}
	}
}
