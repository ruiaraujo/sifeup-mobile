package pt.up.beta.mobile.ui.personalarea;

import java.util.ArrayList;

import pt.up.mobile.R;
import pt.up.beta.mobile.datatypes.Park;
import pt.up.beta.mobile.sifeup.ParkUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.ui.BaseFragment;
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
		ResponseCommand<Park[]> {

	private final static String PARK_KEY = "pt.up.fe.mobile.ui.studentarea.PARKS";

	private ListView list;

	/** List of Parks 1, 3, 4 */
	private ArrayList<Park> parks;

	private LayoutInflater mInflater;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mInflater = inflater;

		View root = inflateMainScreen(R.layout.generic_list);
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
				task = ParkUtils.getParksReply(this, getActivity());
			} else {
				ParkAdapter adapter = new ParkAdapter(getActivity(),
						R.layout.list_item_park);
				list.setAdapter(adapter);
				showMainScreen();
			}
		} else {
			parks = new ArrayList<Park>();
			task = ParkUtils.getParksReply(this, getActivity());
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
			TextView places = (TextView) root
					.findViewById(R.id.park_occupation);

			final Park park = parks.get(position);

			tt.setText(park.getName());
			places.setText(getString(R.string.label_free_spots,
					Integer.toString(park.getPlacesNumber())));

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
			finish();
			break;
		case NETWORK:
			showRepeatTaskScreen(getString(R.string.toast_server_error));
			break;
		default:
			showEmptyScreen(getString(R.string.general_error));
			break;
		}
	}

	public void onResultReceived(Park[] result) {
		if (getActivity() == null)
			return;
		for (int i = 0; i < result.length; ++i) {
			parks.add(result[i]);
			switch (i) {
			case 0:
				parks.get(i).setName(
						getString(R.string.label_personnel_per_park));
				break;
			case 1:
				parks.get(i).setName(getString(R.string.label_student_park));
				break;
			case 2:
				parks.get(i).setName(
						getString(R.string.label_personnel_not_per_park));
				break;
			}
		}

		ParkAdapter adapter = new ParkAdapter(getActivity(),
				R.layout.list_item_park);
		list.setAdapter(adapter);
		showMainScreen();
	}

	protected void onRepeat() {
		showLoadingScreen();
		ParkUtils.getParksReply(this, getActivity());
	}

}
