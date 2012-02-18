package pt.up.fe.mobile.ui.studentarea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.LoginActivity;

import external.com.google.android.apps.iosched.util.UIUtils;

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

public class SubjectsFragment extends BaseFragment implements
        OnItemClickListener {

    /** Contains all subscribed subjects */
    private ArrayList<Subject> subjects = new ArrayList<Subject>();
    private ListView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Subjects");

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.generic_list,
                getParentContainer(), true);
        list = (ListView) root.findViewById(R.id.generic_list);
        new SubjectsTask().execute();
        return getParentContainer(); // this is mandatory.
    }

    /**
     * 
     * Represents a subject. Holds all data about it.
     * 
     */
    private class Subject {
        public String acronym; // "EICXXXX"
        public int year; // 3
        public String namePt; // Sistemas Distribuidos
        public String nameEn; // Distributed Systems
        public String semester; // 2S
    }

    /**
     * Subject Parser Stores Subjects in SubjectFragment.subjects Returns true
     * in case of correct parsing.
     * 
     * @param page
     * @return boolean
     * @throws JSONException
     */
    public boolean JSONSubjects(String page) throws JSONException {
        JSONObject jObject = new JSONObject(page);

        // clear old schedule
        this.subjects.clear();

        if (jObject.has("inscricoes")) {
            Log.e("JSON", "founded disciplines");
            JSONArray jArray = jObject.getJSONArray("inscricoes");

            // if year number is wrong, returns false
            if (jArray.length() == 0)
                return false;

            // iterate over jArray
            for (int i = 0; i < jArray.length(); i++) {
                // new JSONObject
                JSONObject jSubject = jArray.getJSONObject(i);
                // new Block
                Subject subject = new Subject();

                if (jSubject.has("dis_codigo"))
                    subject.acronym = jSubject.getString("dis_codigo"); // Monday
                                                                        // is
                                                                        // index
                                                                        // 0
                if (jSubject.has("ano_curricular"))
                    subject.year = jSubject.getInt("ano_curricular");
                if (jSubject.has("nome"))
                    subject.namePt = jSubject.getString("nome");
                if (jSubject.has("name"))
                    subject.nameEn = jSubject.getString("name");
                if (jSubject.has("periodo"))
                    subject.semester = jSubject.getString("periodo");

                // add block to schedule
                this.subjects.add(subject);
            }
            Log.e("JSON", "loaded disciplines");
            return true;
        }
        Log.e("JSON", "disciplines not found");
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
            long id) {
        if (getActivity() == null)
            return;
        Intent i = new Intent(getActivity(), SubjectDescriptionActivity.class);
        // assumed only one page of results
        int secondYear = UIUtils.secondYearOfSchoolYear();
        i.putExtra(SubjectDescriptionFragment.SUBJECT_CODE,
                subjects.get(position).acronym);
        i.putExtra(
                SubjectDescriptionFragment.SUBJECT_YEAR,
                Integer.toString(secondYear - 1) + "/"
                        + Integer.toString(secondYear));
        i.putExtra(SubjectDescriptionFragment.SUBJECT_PERIOD,
                subjects.get(position).semester);
        i.putExtra(Intent.EXTRA_TITLE, subjects.get(position).namePt);
        startActivity(i);

    }

    /** Classe privada para a busca de dados ao servidor */
    private class SubjectsTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            showLoadingScreen();
        }

        protected void onPostExecute(String result) {
            if (getActivity() == null)
                return;
            if (result.equals("Success")) {
                Log.d("Subjects", "success");
                if ( subjects.isEmpty() )
                {
                    showEmptyScreen(getString(R.string.lb_no_subjects));
                    return;
                }
                try {
                    final String language = Locale.getDefault().getLanguage();
                    final String[] from = new String[] { "name", "code", "time" };
                    final int[] to = new int[] { R.id.exam_chair,
                            R.id.exam_time, R.id.exam_room };
                    // prepare the list of all records
                    final List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
                    for (Subject s : subjects) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        if (language.startsWith("pt"))
                            map.put(from[0],
                                    (s.namePt.trim().length() != 0) ? s.namePt
                                            : s.nameEn);
                        else
                            map.put(from[0],
                                    (s.nameEn.trim().length() != 0) ? s.nameEn
                                            : s.namePt);
                        map.put(from[1], s.acronym);
                        map.put(from[2],
                                getString(R.string.subjects_year, s.year,
                                        s.semester));
                        fillMaps.add(map);
                    }
                    // fill in the grid_item layout
                    final SimpleAdapter adapter = new SimpleAdapter(
                            getActivity(), fillMaps, R.layout.list_item_exam,
                            from, to);
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(SubjectsFragment.this);
                    showMainScreen();
                    Log.d("JSON", "subjects visual list loaded");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), "F*** Fragments",
                                Toast.LENGTH_LONG).show();

                }
            } else if (result.equals("Error")) {
                Log.e("Login", "error");
                if (getActivity() != null) {
                    Toast.makeText(getActivity(),
                            getString(R.string.toast_auth_error),
                            Toast.LENGTH_LONG).show();
                    ((BaseActivity) getActivity())
                            .goLogin(LoginActivity.EXTRA_DIFFERENT_LOGIN_REVALIDATE);
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
        protected String doInBackground(Void... theVoid) {
            String page = "";
            try {
                page = SifeupAPI.getSubjectsReply(SessionManager.getInstance()
                        .getLoginCode(), Integer.toString(UIUtils
                        .secondYearOfSchoolYear() - 1));
                int error = SifeupAPI.JSONError(page);
                switch (error) {
                case SifeupAPI.Errors.NO_AUTH:
                    return "Error";
                case SifeupAPI.Errors.NO_ERROR:
                    JSONSubjects(page);
                    return "Success";
                case SifeupAPI.Errors.NULL_PAGE:
                    return "";
                }

                return "";
            } catch (JSONException e) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), "F*** JSON",
                            Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            return "";
        }
    }

}
