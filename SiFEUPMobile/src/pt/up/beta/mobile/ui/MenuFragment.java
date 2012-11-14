package pt.up.beta.mobile.ui;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.ui.facilities.FeupFacilitiesActivity;
import pt.up.beta.mobile.ui.friends.FriendsActivity;
import pt.up.beta.mobile.ui.news.NewsActivity;
import pt.up.beta.mobile.ui.notifications.NotificationsActivity;
import pt.up.beta.mobile.ui.personalarea.PersonalAreaActivity;
import pt.up.beta.mobile.ui.services.ServicesActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class MenuFragment extends SherlockFragment implements
		OnItemClickListener {
	private ListView list;
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	private int mActivatedPosition = ListView.INVALID_POSITION;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		final View root = inflater.inflate(R.layout.fragment_sidemenu,
				container, false);
		list = (ListView) root.findViewById(R.id.list);
		return root;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MenuAdapter adapter = new MenuAdapter(getActivity());
		adapter.add(new MenuEntry(R.string.btn_student_area));
		adapter.add(new MenuEntry(R.string.btn_student_services));
		adapter.add(new MenuEntry(R.string.btn_friends));
		adapter.add(new MenuEntry(R.string.btn_news));
		adapter.add(new MenuEntry(R.string.btn_map));
		adapter.add(new MenuEntry(R.string.btn_notifications));
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
				setActivatedPosition(savedInstanceState
						.getInt(STATE_ACTIVATED_POSITION));
			}
		} else
			setActivatedPosition(getPositionforClass(getActivity().getClass()));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	public void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			list.setItemChecked(mActivatedPosition, false);
		} else {
			list.setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	private class MenuEntry {
		private final int tag;

		public MenuEntry(int tag) {
			this.tag = tag;
		}
	}

	public class MenuAdapter extends ArrayAdapter<MenuEntry> {

		public MenuAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.main_menu_entry, null);
			}
			TextView title = (TextView) convertView.findViewById(R.id.text);
			ImageView selected = (ImageView) convertView
					.findViewById(R.id.selected);
			if (position == mActivatedPosition) {
				selected.setVisibility(View.VISIBLE);
			} else
				selected.setVisibility(View.INVISIBLE);
			title.setText(getItem(position).tag);
			return convertView;
		}

	}

	private int getPositionforClass(Class<?> activity) {
		if (activity == PersonalAreaActivity.class)
			return 0;
		if (activity == ServicesActivity.class)
			return 1;
		if (activity == FriendsActivity.class)
			return 2;
		if (activity == NewsActivity.class)
			return 3;
		if (activity == FeupFacilitiesActivity.class)
			return 4;
		if (activity == NotificationsActivity.class)
			return 5;
		return ListView.INVALID_POSITION;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int positiong,
			long id) {
		BaseActivity activity = (BaseActivity) getActivity();
		final Class<?> activityToBeLaunched;
		switch (positiong) {
		case 0:
			activityToBeLaunched = PersonalAreaActivity.class;
			break;
		case 1:
			activityToBeLaunched = ServicesActivity.class;
			break;
		case 2:
			activityToBeLaunched = FriendsActivity.class;
			break;
		case 3:
			activityToBeLaunched = NewsActivity.class;
			break;
		case 4:
			activityToBeLaunched = FeupFacilitiesActivity.class;
			break;
		case 5:
			activityToBeLaunched = NotificationsActivity.class;
			break;
		default:
			return;
		}
		if (activity.getClass() == activityToBeLaunched) {
			activity.showAbove();
			return;
		}
		activity.openActivityOrFragment(new Intent(getActivity(),
				activityToBeLaunched));
	}
}
