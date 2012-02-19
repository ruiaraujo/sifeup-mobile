package pt.up.fe.mobile.ui.notifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.datatypes.Notification;
import pt.up.fe.mobile.sifeup.SifeupAPI;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.LoginActivity;

/**
 * Notifications Fragment
 * 
 * @author Ângela Igreja
 * 
 */
public class NotificationsFragment extends BaseFragment implements
        OnItemClickListener {

    private ListView list;

    private ArrayList<Notification> notifications;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView(
                "/Notifications");

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.generic_list,
                getParentContainer(), true);
        list = (ListView) root.findViewById(R.id.generic_list);
        notifications = new ArrayList<Notification>();
        new NotificationsTask().execute();
        return getParentContainer(); // this is mandatory.
    }

    /** Classe privada para a busca de dados ao servidor */
    private class NotificationsTask extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            showLoadingScreen();
        }

        protected void onPostExecute(String result) {
            if (getActivity() == null)
                return;

            if (result.equals("Success")) {
                Log.e("Notifications", "success");

                // fill in the grid_item layout
                if (notifications.isEmpty()) {
                    showEmptyScreen(getString(R.string.lb_no_notification));
                    return;
                }
                Log.e("JSON", "Notifications visual list loaded");

                String[] from = new String[] { "subject", "date",
                        "designation", "priority" };
                int[] to = new int[] { R.id.notification_subject,
                        R.id.notification_date, R.id.notification_designation,
                        R.id.notification_priority };

                // prepare the list of all records
                List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

                for (Notification n : notifications) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("subject", " " + n.getSubject());
                    map.put("date", " " + n.getDate());
                    map.put("designation", " " + n.getDesignation());
                    map.put("priority", " " + n.getPriorityString());
                    fillMaps.add(map);
                }

                // fill in the grid_item layout
                if (getActivity() == null)
                    return;

                SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                        fillMaps, R.layout.list_item_notification, from, to);
                list.setAdapter(adapter);
                list.setOnItemClickListener(NotificationsFragment.this);
                showMainScreen();
                Log.e("JSON", "Notifications visual list loaded");
                return;
            } else if (result.equals("Error")) {
                Log.e("Login", "error");
                if (getActivity() != null) {
                    Toast.makeText(getActivity(),
                            getString(R.string.toast_auth_error),
                            Toast.LENGTH_LONG).show();
                    ((BaseActivity) getActivity())
                            .goLogin(LoginActivity.EXTRA_DIFFERENT_LOGIN_REVALIDATE);
                    getActivity().finish();
                    return;
                }
            } else if (result.equals("")) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(),
                            getString(R.string.toast_server_error),
                            Toast.LENGTH_LONG).show();
                    getActivity().finish();
                    return;
                }
            }
        }

        @Override
        protected String doInBackground(String... code) {
            String page = "";
            try {
                page = SifeupAPI.getNotificationsReply();
                int error = SifeupAPI.JSONError(page);
                switch (error) {
                case SifeupAPI.Errors.NO_AUTH:
                    return "Error";
                case SifeupAPI.Errors.NO_ERROR:
                    JSONObject jObject = new JSONObject(page);

                    if (jObject.has("notificacoes")) {
                        Log.e("JSON", "founded notifications");

                        JSONArray jArray = jObject.getJSONArray("notificacoes");

                        if (jArray.length() == 0)
                            return "Success";

                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jNotification = jArray.getJSONObject(i);

                            Notification noti = new Notification();

                            noti.JSONNotification(jNotification);
                            notifications.add(noti);
                        }
                    }

                    return "Success";

                case SifeupAPI.Errors.NULL_PAGE:
                    return "Error";// When not authenticated, it returns a null
                                   // page.
                }
                return "Success";
            } catch (JSONException e) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), "F*** JSON",
                            Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            return "";
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long id) {

        if (getActivity() == null)
            return;

        Intent i = new Intent(getActivity(), NotificationsDescActivity.class);
        i.putExtra(NotificationsDescActivity.NOTIFICATION,
                notifications.get(position));

        // i.putExtra(NotificationsDescActivity.NOTIFICATION_SUBJECT,
        // notifications.get(position).getSubject());
        // i.putExtra(NotificationsDescActivity.NOTIFICATION_MESSAGE,notifications.get(position).getMessage());
        // i.putExtra(NotificationsDescActivity.NOTIFICATION_LINK,
        // notifications.get(position).getLink());
        // i.putExtra(Intent.EXTRA_TITLE,
        // notifications.get(position).getDesignation());

        startActivity(i);

    }
}
