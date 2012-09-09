package pt.up.beta.mobile.ui.friends;

import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.Friend;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.ui.personalarea.ScheduleActivity;
import pt.up.beta.mobile.ui.personalarea.ScheduleFragment;
import pt.up.beta.mobile.ui.profile.ProfileActivity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * Fragment of the Friends Activity, communication with graphical interface.
 * Loading a list item, is initiated {@link ProfileActivity} activity.
 * 
 * @author Ângela Igreja
 * 
 */
public class FriendsListFragment extends BaseFragment implements
		OnItemClickListener, LoaderCallbacks<Cursor> {

	private static final String TAG = "FriendsListFragment";
	private List<Friend> friends;
	private ListView list;
	private FriendQueryHandler mQueryHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.generic_list,
				getParentContainer(), true);
		list = (ListView) root.findViewById(R.id.generic_list);
		return getParentContainer(); // this is mandatory.
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mQueryHandler = new FriendQueryHandler(getActivity()
				.getContentResolver());
	}

	@Override
	public void onStart() {
		super.onStart();
		registerForContextMenu(list);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.friends_menu_context, menu);
	}

	public void onResume() {
		super.onResume();
	    getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.menu_friends_delete:
			final Friend friend = friends.get(info.position);
			mQueryHandler.startDelete(0, null,
					SigarraContract.Friends.CONTENT_URI,
					SigarraContract.Friends.FRIEND_SELECTION,
					SigarraContract.Friends.getFriendSelectionArgs(
							AccountUtils.getActiveUserCode(getActivity()),
							friend.getCode()));

			break;
		case R.id.menu_friends_timetable:
			String loginCode = friends.get(info.position).getCode();
			if (getActivity() == null)
				return true;
			Intent i = new Intent(getActivity(), ScheduleActivity.class);
			i.putExtra(ScheduleFragment.SCHEDULE_CODE, loginCode);
			if (friends.get(info.position).getCourse() == null)
				i.putExtra(ScheduleFragment.SCHEDULE_TYPE,
						ScheduleFragment.SCHEDULE_EMPLOYEE);
			else
				i.putExtra(ScheduleFragment.SCHEDULE_TYPE,
						ScheduleFragment.SCHEDULE_STUDENT);
			startActivity(i);
		}
		return false;
	}

	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		final Intent i = new Intent(getActivity(), ProfileActivity.class);
		final Friend f = friends.get(position);
		if (f.getCourse() != null)
			i.putExtra(ProfileActivity.PROFILE_TYPE,
					ProfileActivity.PROFILE_STUDENT);
		else
			i.putExtra(ProfileActivity.PROFILE_TYPE,
					ProfileActivity.PROFILE_EMPLOYEE);
		i.putExtra(ProfileActivity.PROFILE_CODE, f.getCode());
		i.putExtra(Intent.EXTRA_TITLE, f.getName());
		startActivity(i);
		getActivity().overridePendingTransition(R.anim.slide_right_in,
				R.anim.slide_right_out);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		return new CursorLoader(getActivity(),
				SigarraContract.Friends.CONTENT_URI,
				SigarraContract.Friends.FRIENDS_COLUMNS,
				SigarraContract.Friends.USER_FRIENDS,
				SigarraContract.Friends
						.getUserFriendsSelectionArgs(AccountUtils
								.getActiveUserCode(getActivity())), null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor mCursor) {
		friends = Friend.parseCursor(mCursor);
		if (friends.isEmpty()) {
			showEmptyScreen(getString(R.string.label_no_friends));
			return;
		}
		Log.i(TAG, "loading list...");

		// fill in the grid_item layout
		list.setAdapter(new FriendAdapter(friends, getActivity()
				.getLayoutInflater(), getImagedownloader()));
		list.setOnItemClickListener(this);
		showMainScreen();
		Log.i(TAG, "list loaded successfully");
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	private class FriendQueryHandler extends AsyncQueryHandler {

		public FriendQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onDeleteComplete(int token, Object cookie, int result) {
			super.onDeleteComplete(token, cookie, result);
			getActivity().getSupportLoaderManager().restartLoader(0, null,
					FriendsListFragment.this);
		}

	}
}
