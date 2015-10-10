package pt.up.mobile.ui.search;

import android.content.SearchRecentSuggestionsProvider;

/**
 * To create a search suggestions provider using the built-in recent queries mode, 
 * simply extend SearchRecentSuggestionsProvider as shown here, and configure with
 * a unique authority and the mode you with to use.  For more information, see
 * {@link android.content.SearchRecentSuggestionsProvider}.
 */
public class SearchSuggestionHistory extends SearchRecentSuggestionsProvider {
    
    /**
     * This is the provider authority identifier.  The same string must appear in your
     * Manifest file, and any time you instantiate a 
     * {@link android.provider.SearchRecentSuggestions} helper class. 
     */
	public final static String AUTHORITY = "pt.up.fe.mobile.SearchSuggestionProvider";
    /**
     * These flags determine the operating mode of the suggestions provider.  This value should 
     * not change from run to run, because when it does change, your suggestions database may 
     * be wiped.
     */
    public final static int MODE = DATABASE_MODE_QUERIES;
    
    /**
     * The main job of the constructor is to call setupSuggestions(String, int) with the
     * appropriate configuration values.
     */
    public SearchSuggestionHistory() {
        super();
        setupSuggestions(AUTHORITY, MODE);
    }
}
