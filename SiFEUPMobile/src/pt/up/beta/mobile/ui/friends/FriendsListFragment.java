package pt.up.beta.mobile.ui.friends;


import pt.up.beta.mobile.friends.Friend;
import pt.up.beta.mobile.sifeup.SessionManager;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.ui.personalarea.ScheduleActivity;
import pt.up.beta.mobile.ui.personalarea.ScheduleFragment;
import pt.up.beta.mobile.ui.profile.ProfileActivity;
import pt.up.beta.mobile.R;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Fragment of the Friends Activity, communication with graphical interface.
 * Loading a list item, is initiated {@link ProfileActivity} activity.
 * 
 * @author Ângela Igreja
 * 
 */
public class FriendsListFragment extends BaseFragment implements
        OnItemClickListener {

    final String TAG = "FriendsListFragment";
    private ListView list;

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

    public void onResume (){
    	super.onResume();
        task = new FriendsTask().execute();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
        case R.id.menu_friends_delete:
            SessionManager.getInstance(getActivity()).removeFriend((int) info.id);
            new FriendsTask().execute();
            break;
        case R.id.menu_friends_timetable:
            String loginCode = SessionManager.getInstance(getActivity()).getFriendsList()
                    .get((int) info.id).getCode();
            if (getActivity() == null)
                return true;
            Intent i = new Intent(getActivity(), ScheduleActivity.class);
            i.putExtra(ScheduleFragment.SCHEDULE_CODE, loginCode);
            if (SessionManager.getInstance(getActivity()).getFriendsList().get((int) info.id).getCourse() == null)
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
        Intent i = new Intent(getActivity(), ProfileActivity.class);
        Friend f = SessionManager.getInstance(getActivity()).getFriend(position);
        if (f.getCourse() != null)
            i.putExtra(ProfileActivity.PROFILE_TYPE,
                    ProfileActivity.PROFILE_STUDENT);
        else
            i.putExtra(ProfileActivity.PROFILE_TYPE,
                    ProfileActivity.PROFILE_EMPLOYEE);
        i.putExtra(ProfileActivity.PROFILE_CODE, f.getCode());
        i.putExtra(Intent.EXTRA_TITLE, f.getName());
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    /** Classe privada para a busca de dados ao servidor */
    private class FriendsTask extends AsyncTask<Void, Void, Boolean> {

        protected void onPreExecute() {
            showLoadingScreen();
        }

        protected void onPostExecute(Boolean result) {
            if (getActivity() == null)
                return;
            if ( result ) {
                if (SessionManager.getInstance(getActivity()).getFriendsList().isEmpty()) {
                    showEmptyScreen(getString(R.string.label_no_friends));
                    return;
                }
                Log.i(TAG, "loading list...");

                // fill in the grid_item layout
                FriendAdapter adapter = new FriendAdapter(SessionManager.getInstance(getActivity()).getFriendsList(), getActivity().getLayoutInflater(), getImagedownloader());
                list.setAdapter(adapter);
                list.setOnItemClickListener(FriendsListFragment.this);
                showMainScreen();
                Log.i(TAG, "list loaded successfully");

            } else {
                Log.e("Login", "error");
    			showRepeatTaskScreen(getString(R.string.general_error));
    			return;
            }
        }

        @Override
        protected Boolean doInBackground(Void... theVoid) {
            return SessionManager.getInstance(getActivity()).loadFriends();
        }
    }

	protected void onRepeat() {
        task = new FriendsTask().execute();
	}

}
