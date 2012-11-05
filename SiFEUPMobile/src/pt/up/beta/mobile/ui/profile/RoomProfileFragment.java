package pt.up.beta.mobile.ui.profile;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.datatypes.RoomProfile;
import pt.up.beta.mobile.datatypes.RoomProfile.Attributes;
import pt.up.beta.mobile.datatypes.RoomProfile.People;
import pt.up.beta.mobile.sifeup.FacilitiesUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.tracker.AnalyticsUtils;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.ui.facilities.FeupFacilitiesDetailsActivity;
import pt.up.beta.mobile.ui.facilities.FeupFacilitiesDetailsFragment;
import pt.up.beta.mobile.ui.personalarea.ScheduleActivity;
import pt.up.beta.mobile.ui.personalarea.ScheduleFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * Employee Profile Fragment This interface is responsible for fetching the
 * employee's profile information to the server and shows it. Have one argument
 * that is the number of employee.
 * 
 * @author Ângela Igreja
 */
public class RoomProfileFragment extends BaseFragment implements
		ResponseCommand<RoomProfile> {
	private ViewGroup root;
	private LayoutInflater mInflater;
	private String code;

	private RoomProfile room;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		AnalyticsUtils.getInstance(getActivity())
				.trackPageView("/Room Profile");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mInflater = inflater;
		root = (ViewGroup) inflater.inflate(R.layout.room_profile,
				getParentContainer(), true);
		return getParentContainer();
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		code = getArguments().getString(ProfileActivity.PROFILE_CODE);
		task = FacilitiesUtils.getRoomProfile(code, this, getActivity());

	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.schedule_menu_items, menu);
		inflater.inflate(R.menu.map_menu_items, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_schedule) {
			final Intent intent = new Intent(getActivity(),
					ScheduleActivity.class);
			intent.putExtra(ScheduleFragment.SCHEDULE_TYPE,
					ScheduleFragment.SCHEDULE_ROOM);
			intent.putExtra(ScheduleFragment.SCHEDULE_CODE, room.getCode());
			intent.putExtra(Intent.EXTRA_TITLE,
					getString(R.string.title_schedule_arg, room.getFullName()));
			startActivity(intent);
			return true;
		}
		if (item.getItemId() == R.id.menu_map) {
			final Intent intent = new Intent(getActivity(),
					FeupFacilitiesDetailsActivity.class);
			intent.putExtra(FeupFacilitiesDetailsFragment.ROOM_EXTRA, room);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
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

	@Override
	public void onResultReceived(RoomProfile results) {
		if (getActivity() == null)
			return;
		room = results;
		getSherlockActivity().getSupportActionBar()
				.setTitle(room.getFullName());
		TextView roomName = (TextView) root.findViewById(R.id.room_name);
		roomName.setText(Html.fromHtml(getString(R.string.room_name,
				room.getFullName())));
		TextView buildingName = (TextView) root
				.findViewById(R.id.building_name);
		buildingName.setText(Html.fromHtml(getString(R.string.room_building,
				room.getBuildingName())));
		TextView area = (TextView) root.findViewById(R.id.room_area);
		area.setText(Html.fromHtml(getString(R.string.room_area, room.getArea())));
		TextView usage = (TextView) root.findViewById(R.id.room_usage);
		usage.setText(Html.fromHtml(getString(R.string.room_usage,
				room.getUsage())));

		if (room.getAtributes().length > 0) {
			LinearLayout attributesContainer = (LinearLayout) root
					.findViewById(R.id.list_attributes);
			for (Attributes attr : room.getAtributes()) {
				TextView llItem = (TextView) mInflater.inflate(
						R.layout.simple_list_item1, null);
				llItem.setText(Html.fromHtml("<b>"+attr.getName() + ":</b> " + attr.getContent()));
				llItem.setClickable(false);
				llItem.setFocusable(false);
				llItem.setFocusableInTouchMode(false);
				attributesContainer.addView(llItem);
			}
		} else {
			root.findViewById(R.id.list_attributes).setVisibility(View.GONE);
		}

		final OnClickListener personClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				final People person = (People) v.getTag();
				final Intent i = new Intent(getActivity(),
						ProfileActivity.class);
				i.putExtra(ProfileActivity.PROFILE_CODE, person.getCode());
				i.putExtra(ProfileActivity.PROFILE_TYPE,
						ProfileActivity.PROFILE_EMPLOYEE);
				i.putExtra(Intent.EXTRA_TITLE, person.getName());
				startActivity(i);
			}
		};
		if (room.getResponsible().length > 0) {
			LinearLayout responsibleContainer = (LinearLayout) root
					.findViewById(R.id.list_responsible);
			for (People person : room.getResponsible()) {
				TextView llItem = (TextView) mInflater.inflate(
						R.layout.simple_list_item1, null);
				llItem.setText(person.getName());
				// To know wich item has been clicked
				if (person.isPerson()) {
					llItem.setTag(person);
					// In the onClickListener just get the id using getTag() on
					// the view
					llItem.setOnClickListener(personClick);
				}
				responsibleContainer.addView(llItem);
			}
		} else {
			root.findViewById(R.id.list_responsible).setVisibility(View.GONE);
			root.findViewById(R.id.room_responsible).setVisibility(View.GONE);
		}

		if (room.getOccupiers().length > 0) {
			LinearLayout occupiersContainer = (LinearLayout) root
					.findViewById(R.id.list_occupiers);
			for (People person : room.getOccupiers()) {
				TextView llItem = (TextView) mInflater.inflate(
						R.layout.simple_list_item1, null);
				llItem.setText(person.getName());
				// To know wich item has been clicked
				if (person.isPerson()) {
					llItem.setTag(person);
					// In the onClickListener just get the id using getTag() on
					// the view
					llItem.setOnClickListener(personClick);
				}
				occupiersContainer.addView(llItem);
			}
		} else {
			root.findViewById(R.id.list_occupiers).setVisibility(View.GONE);
			root.findViewById(R.id.room_occupiers).setVisibility(View.GONE);
		}
		showMainScreen();

	}
}
