package pt.up.fe.mobile.ui.friends;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.datatypes.Friend;
import pt.up.fe.mobile.sifeup.SessionManager;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.profile.ProfileActivity;
import pt.up.fe.mobile.ui.studentarea.ScheduleActivity;
import pt.up.fe.mobile.ui.studentarea.ScheduleFragment;
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
import android.widget.SimpleAdapter;
import android.widget.Toast;
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
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Friends");
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

    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        new FriendsTask().execute();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
        case R.id.menu_friends_delete:
            SessionManager.friends.removeFriend((int) info.id);
            SessionManager.friends.saveToFile(getActivity()
                    .getApplicationContext());
            new FriendsTask().execute();
            break;
        case R.id.menu_friends_timetable:
            String loginCode = SessionManager.friends.getList()
                    .get((int) info.id).getCode();
            if (getActivity() == null)
                return true;
            Intent i = new Intent(getActivity(), ScheduleActivity.class);
            i.putExtra(ScheduleFragment.SCHEDULE_CODE, loginCode);
            if (SessionManager.friends.getList().get((int) info.id).getCourse() != null)
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
        Friend f = SessionManager.friends.getFriend(position);
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
    private class FriendsTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            showLoadingScreen();
        }

        protected void onPostExecute(String result) {
            if (getActivity() == null)
                return;
            if (!result.equals("")) {
                if (SessionManager.friends.getList().isEmpty()) {
                    showEmptyScreen(getString(R.string.label_no_friends));
                    return;
                }
                Log.i(TAG, "loading list...");

                String[] from = new String[] { "name", "course" };
                int[] to = new int[] { R.id.friend_name, R.id.friend_course };
                // prepare the list of all records
                List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
                for (Friend f : SessionManager.friends.getList()) {

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(from[0], f.getName());
                    if (f.getCourse() != null)
                        map.put(from[1], f.getCourse());
                    fillMaps.add(map);
                }

                // fill in the grid_item layout
                SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                        fillMaps, R.layout.list_item_friend, from, to);
                list.setAdapter(adapter);
                list.setOnItemClickListener(FriendsListFragment.this);
                showMainScreen();
                Log.i(TAG, "list loaded successfully");

            } else {
                Log.e("Login", "error");
                if (getActivity() != null) {
                    Toast.makeText(getActivity(),
                            getString(R.string.toast_auth_error),
                            Toast.LENGTH_LONG).show();
                    ((BaseActivity) getActivity())
                            .goLogin();
                    return;
                }
            }
        }

        @Override
        protected String doInBackground(Void... theVoid) {
            if (!SessionManager.friends.isLoaded()) {
                if (SessionManager.friends.loadFromFile(getActivity()))
                    return "Sucess";
            } else
                return "Sucess";
            return "";
        }
    }

}
