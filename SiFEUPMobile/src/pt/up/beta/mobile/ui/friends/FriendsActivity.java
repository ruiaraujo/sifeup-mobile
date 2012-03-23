package pt.up.beta.mobile.ui.friends;


import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;

public class FriendsActivity extends BaseSinglePaneActivity {
	
	@Override
    protected Fragment onCreatePane() {
        return new FriendsListFragment();
    }
}

