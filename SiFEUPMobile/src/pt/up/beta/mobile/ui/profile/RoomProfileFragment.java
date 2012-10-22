package pt.up.beta.mobile.ui.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.datatypes.Employee;
import pt.up.beta.mobile.datatypes.Profile;
import pt.up.beta.mobile.datatypes.Profile.ProfileDetail;
import pt.up.beta.mobile.datatypes.RoomProfile;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.FacilitiesUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.syncadapter.SigarraSyncAdapterUtils;
import pt.up.beta.mobile.tracker.AnalyticsUtils;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.ui.personalarea.ScheduleActivity;
import pt.up.beta.mobile.ui.personalarea.ScheduleFragment;
import pt.up.beta.mobile.ui.utils.LoaderDrawable;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
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
		OnItemClickListener, ResponseCommand<RoomProfile> {
	private TextView name;
	private ImageView pic;
	private ListView details;
	private String code;

	/** User Info */
	private RoomProfile me;
	private List<ProfileDetail> contents;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		AnalyticsUtils.getInstance(getActivity()).trackPageView(
				"/Employee Profile");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.profile,
				getParentContainer(), true);
		name = ((TextView) root.findViewById(R.id.profile_name));
		pic = (ImageView) root.findViewById(R.id.profile_pic);
		details = ((ListView) root.findViewById(R.id.profile_details));
		
		((Button) root.findViewById(R.id.profile_link_schedule))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent i = new Intent(getActivity(),
								ScheduleActivity.class);
						i.putExtra(ScheduleFragment.SCHEDULE_TYPE,
								ScheduleFragment.SCHEDULE_ROOM);
						i.putExtra(ScheduleFragment.SCHEDULE_CODE, me.getCode());
						i.putExtra(
								Intent.EXTRA_TITLE,
								getString(R.string.title_schedule_arg,
										me.getFullName()));
						startActivity(i);
					}
				});
		return getParentContainer();
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		code = getArguments().getString(ProfileActivity.PROFILE_CODE);
		task = FacilitiesUtils.getRoomProfile(code, this, getActivity());

	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.refresh_menu_items, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh) {
			setRefreshActionItemState(true);
			SigarraSyncAdapterUtils.syncProfile(
					AccountUtils.getActiveUserName(getActivity()), code,
					SifeupAPI.EMPLOYEE_TYPE);

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View arg1, int position,
			long id) {
		if (contents.get(position).type == Profile.Type.WEBPAGE) {
			final Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(contents.get(position).content));
			startActivity(browserIntent);
		} else if (contents.get(position).type == Profile.Type.ROOM) {
			final Intent i = new Intent(getActivity(), ScheduleActivity.class);
			i.putExtra(ScheduleFragment.SCHEDULE_TYPE,
					ScheduleFragment.SCHEDULE_ROOM);
			i.putExtra(ScheduleFragment.SCHEDULE_CODE,
					contents.get(position).content);
			i.putExtra(
					Intent.EXTRA_TITLE,
					getString(R.string.title_schedule_arg,
							contents.get(position).content));
			startActivity(i);
		} else if (contents.get(position).type == Profile.Type.EMAIL) {
			final Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL,
					new String[] { contents.get(position).content });
			startActivity(Intent.createChooser(i,
					getString(R.string.profile_choose_email_app)));
		} else if (contents.get(position).type == Profile.Type.MOBILE) {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:"
					+ contents.get(position).content));
			startActivity(callIntent);
		}
	}

	public void onLoadFinished(Loader<Employee> loader, RoomProfile employee) {
		if (getActivity() == null || employee == null)
			return;
		me = employee;
		pic.setImageDrawable(new LoaderDrawable(getActivity()
				.getSupportLoaderManager(), pic, me.getCode(), getActivity(),
				((BitmapDrawable) getResources().getDrawable(
						R.drawable.speaker_image_empty)).getBitmap()));
		((SherlockFragmentActivity) getActivity()).getSupportActionBar()
				.setTitle(me.getName());
		name.setText(me.getName());
		String[] from = new String[] { "title", "content" };
		int[] to = new int[] { R.id.profile_item_title,
				R.id.profile_item_content };
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for (ProfileDetail s : contents) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(from[0], s.title);
			map.put(from[1], s.content);
			fillMaps.add(map);
		}

		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps,
				R.layout.list_item_profile, from, to);
		details.setAdapter(adapter);
		details.setOnItemClickListener(this);
		details.setSelection(0);
		setRefreshActionItemState(false);
		showMainScreen();

	}

	@Override
	public void onError(
			pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResultReceived(RoomProfile results) {
		// TODO Auto-generated method stub
		
	}

}
