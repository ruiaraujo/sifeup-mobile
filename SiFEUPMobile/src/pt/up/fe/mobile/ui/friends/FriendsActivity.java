package pt.up.fe.mobile.ui.friends;


import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class FriendsActivity extends BaseSinglePaneActivity {
	
	@Override
    protected Fragment onCreatePane() {
		FriendsListFragment fri=new FriendsListFragment();
		//registerForContextMenu(findViewById(android.R.id.list));
        return fri;
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //registerForContextMenu(findViewById(android.R.id.list));
        getActivityHelper().setupSubActivity();
    } 
}

