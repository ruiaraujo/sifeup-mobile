package pt.up.beta.mobile.ui.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.beta.mobile.Constants;
import pt.up.beta.mobile.R;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.Employee;
import pt.up.beta.mobile.datatypes.Profile;
import pt.up.beta.mobile.datatypes.Profile.ProfileDetail;
import pt.up.beta.mobile.loaders.EmployeeLoader;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.syncadapter.SyncAdapter;
import pt.up.beta.mobile.tracker.AnalyticsUtils;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.ui.personalarea.ScheduleActivity;
import pt.up.beta.mobile.ui.personalarea.ScheduleFragment;
import android.accounts.Account;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
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
public class EmployeeProfileFragment extends BaseFragment implements
		OnItemClickListener, LoaderCallbacks<Employee> {
	private TextView name;
	private ImageView pic;
	private ListView details;
	private CheckBox friend;
	private String code;

	private AsyncQueryHandler mQueryHandler;
	/** User Info */
	private Employee me;
	private List<ProfileDetail> contents;

	private final static int PROFILE_LOADER = 0;
	private final static int FRIEND_LOADER = 1;

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
		friend = ((CheckBox) root.findViewById(R.id.profile_star_friend));
		friend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (friend.isChecked()) {
					final ContentValues values = new ContentValues();
					values.put(SigarraContract.FriendsColumns.CODE_FRIEND,
							me.getCode());
					values.put(SigarraContract.FriendsColumns.NAME_FRIEND,
							me.getName());
					values.put(SigarraContract.FriendsColumns.USER_CODE,
							AccountUtils.getActiveUserCode(getActivity()));
					mQueryHandler.startInsert(0, null,
							SigarraContract.Friends.CONTENT_URI, values);
				} else
					mQueryHandler.startDelete(0, null,
							SigarraContract.Friends.CONTENT_URI,
							SigarraContract.Friends.FRIEND_SELECTION,
							SigarraContract.Friends.getFriendSelectionArgs(
									AccountUtils
											.getActiveUserCode(getActivity()),
									me.getCode()));
			}
		});
		((Button) root.findViewById(R.id.profile_link_schedule))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent i = new Intent(getActivity(),
								ScheduleActivity.class);
						i.putExtra(ScheduleFragment.SCHEDULE_TYPE,
								ScheduleFragment.SCHEDULE_EMPLOYEE);
						i.putExtra(ScheduleFragment.SCHEDULE_CODE, me.getCode());
						i.putExtra(
								Intent.EXTRA_TITLE,
								getString(R.string.title_schedule_arg,
										me.getName()));
						startActivity(i);
					}
				});
		return getParentContainer();
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mQueryHandler = new AsyncQueryHandler(getActivity()
				.getContentResolver()) {
		};
		code = getArguments().getString(ProfileActivity.PROFILE_CODE);
		if (code == null)
			code = AccountUtils.getActiveUserCode(getActivity());

		// You can't friend yourself
		if (code.equals(AccountUtils.getActiveUserCode(getActivity())))
			friend.setVisibility(View.GONE);
		else
			getActivity().getSupportLoaderManager().initLoader(FRIEND_LOADER, null,
					new FriendChecker());
		getActivity().getSupportLoaderManager().initLoader(PROFILE_LOADER, null, this);
	}
	


	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.refresh_menu_items, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh) {
			final Bundle extras = new Bundle();
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
			extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			extras.putBoolean(SyncAdapter.SINGLE_REQUEST, true);
			extras.putString(SyncAdapter.REQUEST_TYPE, SyncAdapter.PROFILE);
			extras.putString(SyncAdapter.PROFILE_CODE, code);
			extras.putString(SyncAdapter.PROFILE_TYPE, SifeupAPI.EMPLOYEE_TYPE);
			setRefreshActionItemState(true);
			ContentResolver.requestSync(
					new Account(AccountUtils.getActiveUserName(getActivity()),
							Constants.ACCOUNT_TYPE),
					SigarraContract.CONTENT_AUTHORITY, extras);
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

	@Override
	public Loader<Employee> onCreateLoader(int loaderId, Bundle options) {
		return new EmployeeLoader(getActivity(),
				SigarraContract.Profiles.CONTENT_URI,
				SigarraContract.Profiles.PROFILE_COLUMNS,
				SigarraContract.Profiles.PROFILE,
				SigarraContract.Profiles.getProfileSelectionArgs(code,
						SifeupAPI.EMPLOYEE_TYPE), null);
	}

	@Override
	public void onLoadFinished(Loader<Employee> loader, Employee cursor) {
		if (cursor != null) {
			if (getActivity() == null)
				return;
			me = cursor;
			pic.setImageDrawable(getResources().getDrawable(
					R.drawable.speaker_image_empty));
			getImagedownloader().download(
					SifeupAPI.getPersonPicUrl(me.getCode()), pic,
					((BitmapDrawable) pic.getDrawable()).getBitmap());
			contents = me.getProfileContents(getResources());
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
	}

	@Override
	public void onLoaderReset(Loader<Employee> loader) {
	}

	private class FriendChecker implements LoaderCallbacks<Cursor> {

		@Override
		public Loader<Cursor> onCreateLoader(int loaderId, Bundle options) {
			return new CursorLoader(
					getActivity(),
					SigarraContract.Friends.CONTENT_URI,
					SigarraContract.Friends.FRIENDS_COLUMNS,
					SigarraContract.Friends.FRIEND_SELECTION,
					SigarraContract.Friends.getFriendSelectionArgs(
							AccountUtils.getActiveUserCode(getActivity()), code),
					null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			friend.setChecked(cursor.getCount() != 0);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}

	}

}
