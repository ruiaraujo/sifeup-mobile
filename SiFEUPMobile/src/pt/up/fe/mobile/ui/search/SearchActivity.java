
package pt.up.fe.mobile.ui.search;


import pt.up.fe.mobile.R;
import pt.up.fe.mobile.ui.BaseMultiPaneActivity;
import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;


/**
 * An activity that shows session and sandbox search results. This activity can be either single
 * or multi-pane, depending on the device configuration. We want the multi-pane support that
 * {@link BaseMultiPaneActivity} offers, so we inherit from it instead of
 * {@link BaseSinglePaneActivity}.
 * 
 */
public class SearchActivity extends BaseSinglePaneActivity {

    private String mQuery;
    private StudentsSearchFragment studentsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();        
        mQuery = intent.getStringExtra(SearchManager.QUERY).trim();

        setContentView(R.layout.activity_search);
        // Record the query string in the recent queries suggestions provider.
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, 
                SearchSuggestionHistory.AUTHORITY, SearchSuggestionHistory.MODE);
        suggestions.saveRecentQuery(mQuery, null);
        //TODO: add a way to clean the previous searches
        final CharSequence title = getString(R.string.title_search_query, mQuery);
        actionbar.setTitle(title);

    }


	@Override
	protected Fragment onCreatePane() {
		return studentsFragment = new StudentsSearchFragment();
	}

	
	protected void onNewIntent( Intent query) {
		mQuery = query.getStringExtra(SearchManager.QUERY).trim();
		final CharSequence title = getString(R.string.title_search_query, mQuery);
		actionbar.setTitle(title);
        // Record the query string in the recent queries suggestions provider.
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, 
                SearchSuggestionHistory.AUTHORITY, SearchSuggestionHistory.MODE);
        suggestions.saveRecentQuery(mQuery, null);
	    studentsFragment = new StudentsSearchFragment();
	    studentsFragment.setArguments(intentToFragmentArguments(query));
	    getSupportFragmentManager().beginTransaction()
	        .replace(R.id.root_container, studentsFragment)
	        .commit();
	}
	
    
    /**
     * Any application that implements search suggestions based on previous actions (such as
     * recent queries, page/items viewed, etc.) should provide a way for the user to clear the
     * history.  This gives the user a measure of privacy, if they do not wish for their recent
     * searches to be replayed by other users of the device (via suggestions).
     * 
     * This example shows how to clear the search history for apps that use 
     * android.provider.SearchRecentSuggestions.  If you have developed a custom suggestions
     * provider, you'll need to provide a similar API for clearing history.
     * 
     * In this sample app we call this method from a "Clear History" menu item.  You could also 
     * implement the UI in your preferences, or any other logical place in your UI.
     * @param context 
     */
    public static void clearSearchHistory(Context context) {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(context, 
                SearchSuggestionHistory.AUTHORITY, SearchSuggestionHistory.MODE);
        suggestions.clearHistory();
    }

}
