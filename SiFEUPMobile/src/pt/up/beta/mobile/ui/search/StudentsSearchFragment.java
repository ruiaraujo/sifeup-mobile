package pt.up.beta.mobile.ui.search;

import java.util.ArrayList;

import com.commonsware.cwac.endless.EndlessAdapter;

import pt.up.beta.mobile.datatypes.Profile;
import pt.up.beta.mobile.datatypes.ResultsPage;
import pt.up.beta.mobile.datatypes.Student;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.sifeup.SearchUtils;
import pt.up.beta.mobile.tracker.AnalyticsUtils;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.ui.profile.ProfileActivity;
import pt.up.beta.mobile.R;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * This interface is responsible for fetching the results of research to the
 * server and shows them a list. When loading a list item launches the activity
 * ProfileActivity.
 * 
 * @author Ângela Igreja
 * 
 */
public class StudentsSearchFragment extends BaseFragment implements
		OnItemClickListener, ResponseCommand {

	// query is in SearchActivity, sent to here in the arguments
	private ArrayList<ResultsPage> results = new ArrayList<ResultsPage>();
	private ListAdapter adapter;
	private String query;
	private ListView list;

	private final static String REGEX_STUDENT_CODE = "^[0-9,;]{9}$";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AnalyticsUtils.getInstance(getActivity()).trackPageView("/Search");
		query = getArguments().getString(SearchManager.QUERY);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.generic_list,
				getParentContainer(), true);
		list = (ListView) root.findViewById(R.id.generic_list);
		return getParentContainer();
	}

    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
		if (query.matches(REGEX_STUDENT_CODE))
		    task = SearchUtils.getSingleStudentSearchReply(query, this);
		else
		    task = SearchUtils.getStudentsSearchReply(query, 1, this);
    }
    
	private boolean hasMoreResults() {
		if (results.isEmpty())
			return true;
		if (totalItemLoaded() >= results.get(0).getSearchSize())
			return false;
		return true;
	}

	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error),
					Toast.LENGTH_LONG).show();
			goLogin();
            getActivity().finish();
			break;
		case NETWORK:
            Toast.makeText(getActivity(), getString(R.string.toast_server_error),
                    Toast.LENGTH_LONG).show();
	        getActivity().finish();
	        break;
		default:
			showEmptyScreen(getString(R.string.toast_search_error));
			break;
		}
	}

	public void onResultReceived(Object... results) {
		if (getActivity() == null)
			return;
		if (query.matches(REGEX_STUDENT_CODE)) {
			if ( results == null )
			{
				showEmptyScreen(getString(R.string.toast_search_error));
				return;
			}
			ResultsPage resultsPage = new ResultsPage();
			resultsPage.setSearchSize(1);
			resultsPage.setPage(1);
			resultsPage.setPageResults(1);
			// add student to the page results
			resultsPage.getStudents().add((Student) results[0]);
			// add page to global results
			this.results.add(resultsPage);
		} else {
			final ResultsPage result = ( ResultsPage )results[0];
			if ( result.getStudents().size()  == 0)
			{
				showEmptyScreen(getString(R.string.toast_search_error));
				return;
			}
			else
				this.results.add( result );
		}
		if (hasMoreResults()) {
			adapter = new EndlessSearchAdapter(getActivity(),
					new SearchCustomAdapter(getActivity(),
							R.layout.list_item_search, new Student[0]),
					R.layout.list_item_loading);
		} else {
			adapter = new SearchCustomAdapter(getActivity(),
					R.layout.list_item_friend, new Student[0]);

		}
		list.setAdapter(adapter);
		list.setOnItemClickListener(StudentsSearchFragment.this);
		list.setSelection(0);
		showMainScreen();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		if (parent.getAdapter() instanceof EndlessSearchAdapter) {
			EndlessSearchAdapter a = (EndlessSearchAdapter) parent.getAdapter();
			// ignoring clicks on the pending view
			// see:
			// http://stackoverflow.com/questions/7938891/disable-click-for-pending-view-of-cwac-endless-adapter

			if (a.getItemViewType(position) == Adapter.IGNORE_ITEM_VIEW_TYPE)
				return;
		}
		Intent i = new Intent(getActivity(), ProfileActivity.class);
		// assumed only one page of results
		Profile profile = results.get(position / 15).getStudents().get(
				position % 15);
		i.putExtra(Intent.EXTRA_TITLE, profile.getName());
		i.putExtra(ProfileActivity.PROFILE_TYPE,
				ProfileActivity.PROFILE_STUDENT);
		i.putExtra(ProfileActivity.PROFILE_CODE, profile.getCode());
		startActivity(i);
	}

	public class EndlessSearchAdapter extends EndlessAdapter {

		public EndlessSearchAdapter(Context context, ListAdapter wrapped,
				int pendingResource) {
			super(context, wrapped, pendingResource);
		}

		@Override
		protected boolean cacheInBackground() throws Exception {
			ResultsPage page = SearchUtils.getStudentsSearchReply(query,
					results.size() * 15 + 1);
			if ( page == null )
				return false;
			results.add(page);
			if (results.isEmpty() || !hasMoreResults())
				return false;
			else
				return true;
		}

		@Override
		protected void appendCachedData() {
			SearchCustomAdapter adapter = (SearchCustomAdapter) getWrappedAdapter();
			adapter.notifyDataSetChanged();
		}
	}

	public class SearchCustomAdapter extends ArrayAdapter<Student> {

		public SearchCustomAdapter(Context context, int textViewResourceId,
				Student[] objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;

			if (row == null) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
				row = inflater
						.inflate(R.layout.list_item_search, parent, false);
			}
			TextView name = (TextView) row.findViewById(R.id.friend_name);
			name.setText(results.get(position / 15).getStudents().get(
					position % 15).getName());
			TextView course = (TextView) row.findViewById(R.id.friend_course);
			course.setText(results.get(position / 15).getStudents().get(
					position % 15).getProgrammeName());
			return row;
		}

		public int getCount() {
			return totalItemLoaded();
		}
	}

	private int totalItemLoaded() {
		int total = 0;
		for (ResultsPage result : results)
			total += result.getStudents().size();
		return total;
	}

}
