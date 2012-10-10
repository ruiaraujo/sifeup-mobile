package pt.up.beta.mobile.ui;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.ui.notifications.NotificationsFragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

public class MenuFragment extends SherlockListFragment {

	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	private int mActivatedPosition = ListView.INVALID_POSITION;
	private final static FragmentOpener sDummy = new FragmentOpener() {
		@Override
		public void openFragment(
				@SuppressWarnings("rawtypes") Class fragmentClass,
				Bundle arguments, CharSequence title) {
		}
	};

	private FragmentOpener callback = sDummy;

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MenuAdapter adapter = new MenuAdapter(getActivity());
		adapter.add(new MenuEntry(R.string.btn_personal_area,
				R.drawable.home_btn_sessions));
		adapter.add(new MenuEntry(R.string.btn_student_services,
				R.drawable.home_btn_printer));
		adapter.add(new MenuEntry(R.string.btn_friends,
				R.drawable.home_btn_friends));
		adapter.add(new MenuEntry(R.string.btn_news, R.drawable.home_btn_news));
		adapter.add(new MenuEntry(R.string.btn_map, R.drawable.home_btn_map));
		adapter.add(new MenuEntry(R.string.btn_notifications,
				R.drawable.home_btn_announcements));
		setListAdapter(adapter);
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
				setActivatedPosition(savedInstanceState
						.getInt(STATE_ACTIVATED_POSITION));
			}
		}
	}

	private class MenuEntry {
		private final int tag;
		private final int iconRes;

		public MenuEntry(int tag, int iconRes) {
			this.tag = tag;
			this.iconRes = iconRes;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		callback = sDummy;
	}

	public void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	public class MenuAdapter extends ArrayAdapter<MenuEntry> {

		public MenuAdapter(Context context) {
			super(context, 0);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.main_menu_entry, null);
			}
			TextView title = (TextView) convertView
					.findViewById(android.R.id.text1);
			final Drawable icon = getResources().getDrawable(
					getItem(position).iconRes);
			title.setCompoundDrawablesWithIntrinsicBounds(icon, null, null,
					null);
			title.setText(getItem(position).tag);

			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		switch (position) {
		case 5:
			callback.openFragment(NotificationsFragment.class, null, null);
			break;

		default:
			break;
		}
	}

	public void setCallback(FragmentOpener callback) {
		this.callback = callback;
	}
}
