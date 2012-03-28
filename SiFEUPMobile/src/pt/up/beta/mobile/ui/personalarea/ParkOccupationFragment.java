package pt.up.beta.mobile.ui.personalarea;

import java.util.ArrayList;

import pt.up.beta.mobile.datatypes.Park;
import pt.up.beta.mobile.sifeup.ParkUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.R;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

/**
 * Park Occupation Fragment
 * 
 * @author Ângela Igreja
 * 
 */
public class ParkOccupationFragment extends BaseFragment implements
		ResponseCommand {

	private final static String PARK_KEY = "pt.up.fe.mobile.ui.studentarea.PARKS";

	private ListView list;

	/** List of Parks 1, 3, 4 */
	private ArrayList<Park> parks;

	private LayoutInflater mInflater;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mInflater = inflater;

		View root = inflater.inflate(R.layout.generic_list,
				getParentContainer(), true);
		list = (ListView) root.findViewById(R.id.generic_list);
		list.setClickable(false);
		list.setFocusable(false);
		return getParentContainer();// this is mandatory
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			parks = savedInstanceState.getParcelableArrayList(PARK_KEY);
			if (parks == null) {
				parks = new ArrayList<Park>();
				task = ParkUtils.getParkReply("P1", this);
			} else {
				ParkAdapter adapter = new ParkAdapter(getActivity(),
						R.layout.list_item_park);
				list.setAdapter(adapter);
				showFastMainScreen();
			}
		} else {
			parks = new ArrayList<Park>();
			task = ParkUtils.getParkReply("P1", this);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (parks.size() != 0)
			outState.putParcelableArrayList(PARK_KEY, parks);
	}

	public class ParkAdapter extends ArrayAdapter<Park> {

		public ParkAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View root = mInflater.inflate(R.layout.list_item_park, list, false);
			TextView tt = (TextView) root.findViewById(R.id.park_name);
			ImageView light = (ImageView) root.findViewById(R.id.park_light);
			TextView places = (TextView) root.findViewById(R.id.park_occupation);

			final Park park = parks.get(position);

			tt.setText(park.getName());
			places.setText(getString(R.string.label_free_spots, Integer.toString(park.getPlacesNumber())));

			int placesNumber = park.getPlacesNumber();

			if (placesNumber == 0)
				light.setImageResource(R.drawable.red_light);
			else if (placesNumber < 10)
				light.setImageResource(R.drawable.yellow_light);
			else
				light.setImageResource(R.drawable.green_light);

			return root;
		}

		public int getCount() {
			return parks.size();
		}

		public boolean isEnabled(int position) {
			return false;
		}
	}

	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error),
					Toast.LENGTH_LONG).show();
			goLogin();
			break;
		case NETWORK:
			showRepeatTaskScreen(getString(R.string.toast_server_error));
			break;
		default:
			showEmptyScreen(getString(R.string.general_error));
			break;
		}
	}

	public void onResultReceived(Object... results) {
		if (getActivity() == null)
			return;
		parks.add((Park) results[0]);
		switch (parks.size()) {
		case 1:
			parks.get(0).setName(getString(R.string.label_personnel_per_park));
			ParkUtils.getParkReply("P3", this);
			break;
		case 2:
			parks.get(1).setName(getString(R.string.label_student_park));
			ParkUtils.getParkReply("P4", this);
			break;
		case 3:
			parks.get(2).setName(
					getString(R.string.label_personnel_not_per_park));
			ParkAdapter adapter = new ParkAdapter(getActivity(),
					R.layout.list_item_park);
			list.setAdapter(adapter);
			showMainScreen();
			break;
		}
	}

	protected void onRepeat() {
		showLoadingScreen();
		switch (parks.size()) {
		case 0:
			ParkUtils.getParkReply("P1", this);
			break;
		case 1:
			ParkUtils.getParkReply("P3", this);
			break;
		case 2:
			ParkUtils.getParkReply("P4", this);
			break;
		}
	}

}
