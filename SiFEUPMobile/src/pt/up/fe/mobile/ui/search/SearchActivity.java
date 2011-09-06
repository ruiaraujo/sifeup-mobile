
package pt.up.fe.mobile.ui.search;


import pt.up.fe.mobile.R;
import pt.up.fe.mobile.ui.BaseMultiPaneActivity;
import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
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

        getActivityHelper().setupActionBar(getTitle(), 0);
        final CharSequence title = getString(R.string.title_search_query, mQuery);
        getActivityHelper().setActionBarTitle(title);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();

       /* ViewGroup detailContainer = (ViewGroup) findViewById(R.id.fragment_container_search_detail);
        if (detailContainer != null && detailContainer.getChildCount() > 1) {
            findViewById(android.R.id.empty).setVisibility(View.GONE);
        }*/
    }

	@Override
	protected Fragment onCreatePane() {
		return studentsFragment = new StudentsSearchFragment();
	}

	
	protected void onNewIntent( Intent query) {
		mQuery = query.getStringExtra(SearchManager.QUERY).trim();
		final CharSequence title = getString(R.string.title_search_query, mQuery);
	    getActivityHelper().setActionBarTitle(title);
	    studentsFragment = new StudentsSearchFragment();
	    studentsFragment.setArguments(intentToFragmentArguments(query));
	    getSupportFragmentManager().beginTransaction()
	        .replace(R.id.root_container, studentsFragment)
	        .commit();
	}

}
