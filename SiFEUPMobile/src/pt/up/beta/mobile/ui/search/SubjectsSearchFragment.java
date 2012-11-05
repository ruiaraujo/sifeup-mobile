package pt.up.beta.mobile.ui.search;

import java.util.ArrayList;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.datatypes.ResultsPage;
import pt.up.beta.mobile.datatypes.SubjectSearchResult;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.sifeup.SearchUtils;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.ui.subjects.SubjectDescriptionActivity;
import pt.up.beta.mobile.ui.subjects.SubjectDescriptionFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.endless.EndlessAdapter;

import external.com.google.android.apps.iosched.util.UIUtils;

/**
 * This interface is responsible for fetching the results of research to the
 * server and shows them a list. When loading a list item launches the activity
 * ProfileActivity.
 * 
 * @author Ângela Igreja
 * 
 */
public class SubjectsSearchFragment extends BaseFragment implements
		OnItemClickListener, ResponseCommand<ResultsPage<SubjectSearchResult>> {

	private final static String CURRENT_PAGE = "current_page";
	private final static String RESULTS = "results";
	private final static String RESULTS_PAGE = "result_page";

	// query is in SearchActivity, sent to here in the arguments
	private ArrayList<SubjectSearchResult> results = new ArrayList<SubjectSearchResult>();
	private ResultsPage<SubjectSearchResult> resultPage;
	private ListAdapter adapter;
	private String query;
	private ListView list;
	private int currentPage = 1;
	private String[] advancedSearchParameters;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.generic_list,
				getParentContainer(), true);
		list = (ListView) root.findViewById(R.id.generic_list);
		return getParentContainer();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (resultPage != null) {
			outState.putInt(CURRENT_PAGE, currentPage);
			outState.putParcelableArrayList(RESULTS, results);
			outState.putParcelable(RESULTS_PAGE, resultPage);
		}
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			resultPage = savedInstanceState.getParcelable(RESULTS_PAGE);
			if (resultPage != null) {
				results = savedInstanceState.getParcelableArrayList(RESULTS);
				currentPage = savedInstanceState.getInt(CURRENT_PAGE);
				if (hasMoreResults()) {
					adapter = new EndlessSearchAdapter(getActivity(),
							new SearchCustomAdapter(getActivity(),
									R.layout.list_item_search,
									new SubjectSearchResult[0]),
							R.layout.list_item_loading);
				} else {
					adapter = new SearchCustomAdapter(getActivity(),
							R.layout.list_item_friend,
							new SubjectSearchResult[0]);

				}
				list.setAdapter(adapter);
				list.setOnItemClickListener(this);
				list.setSelection(0);
				showMainScreen();
				return;
			}
		}

		advancedSearchParameters = getArguments().getStringArray(
				SearchManager.QUERY);
		if (advancedSearchParameters != null)
			task = SearchUtils.getSubjectsSearchReply(
					advancedSearchParameters[0], advancedSearchParameters[1],
					advancedSearchParameters[2], advancedSearchParameters[3],
					this, getActivity());
		else {
			query = getArguments().getString(SearchManager.QUERY);
			task = SearchUtils.getSubjectsSearchReply(null, query, null, null,
					this, getActivity());
		}
	}

	private boolean hasMoreResults() {
		if (resultPage == null)
			return true;
		if (results.size() >= resultPage.getSearchSize())
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

	public void onResultReceived(ResultsPage<SubjectSearchResult> results) {
		if (getActivity() == null)
			return;

		resultPage = results;
		if (resultPage.getResults().length == 0) {
			showEmptyScreen(getString(R.string.toast_search_error));
			return;
		}
		for (SubjectSearchResult s : resultPage.getResults())
			this.results.add(s);

		if (hasMoreResults()) {
			adapter = new EndlessSearchAdapter(getActivity(),
					new SearchCustomAdapter(getActivity(),
							R.layout.list_item_search,
							new SubjectSearchResult[0]),
					R.layout.list_item_loading);
		} else {
			adapter = new SearchCustomAdapter(getActivity(),
					R.layout.list_item_friend, new SubjectSearchResult[0]);

		}
		list.setAdapter(adapter);
		list.setOnItemClickListener(SubjectsSearchFragment.this);
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
		Intent i = new Intent(getActivity(), SubjectDescriptionActivity.class);
		// assumed only one page of results
		SubjectSearchResult subject = results.get(position);
		String title = subject.getName();
		if (!UIUtils.isLocalePortuguese()
				&& !TextUtils.isEmpty(subject.getNameEn()))
			title = subject.getNameEn();
		i.putExtra(Intent.EXTRA_TITLE, title);
		i.putExtra(SubjectDescriptionFragment.SUBJECT_CODE, subject.getId());
		startActivity(i);
	}

	public class EndlessSearchAdapter extends EndlessAdapter {

		public EndlessSearchAdapter(Context context, ListAdapter wrapped,
				int pendingResource) {
			super(context, wrapped, pendingResource);
		}

		@Override
		protected boolean cacheInBackground() throws Exception {
			final ResultsPage<SubjectSearchResult> page;

			if (advancedSearchParameters != null)
				page = SearchUtils.getSubjectsSearchReply(
						advancedSearchParameters[0],
						advancedSearchParameters[1],
						advancedSearchParameters[2],
						advancedSearchParameters[3], ++currentPage,
						getActivity());
			else {
				page = SearchUtils.getSubjectsSearchReply(null, query, null,
						null, ++currentPage, getActivity());
			}
			if (page == null)
				return false;
			for (SubjectSearchResult s : page.getResults())
				results.add(s);
			if (!hasMoreResults() || page.getResults().length == 0)
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

	public class SearchCustomAdapter extends ArrayAdapter<SubjectSearchResult> {

		public SearchCustomAdapter(Context context, int textViewResourceId,
				SubjectSearchResult[] objects) {
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
			final TextView name = (TextView) row.findViewById(R.id.friend_name);
			String title = results.get(position).getName();
			if (!UIUtils.isLocalePortuguese()
					&& !TextUtils.isEmpty(results.get(position).getNameEn()))
				title = results.get(position).getNameEn();
			name.setText(title);
			final TextView course = (TextView) row
					.findViewById(R.id.friend_course);
			course.setText(results.get(position).getCode()
					+ " "
					+ getString(R.string.subjects_year, results.get(position)
							.getYear(), results.get(position).getPeriod()));
			return row;
		}

		public int getCount() {
			return results.size();
		}
	}

	protected void onRepeat() {
		showLoadingScreen();
		if (advancedSearchParameters != null)
			task = SearchUtils.getSubjectsSearchReply(
					advancedSearchParameters[0], advancedSearchParameters[1],
					advancedSearchParameters[2], advancedSearchParameters[3],
					this, getActivity());
		else {
			task = SearchUtils.getSubjectsSearchReply(null, query, null, null,
					this, getActivity());
		}
	}

}
