package pt.up.fe.mobile.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;

public class FriendsActivity extends BaseSinglePaneActivity {
	
	@Override
    protected Fragment onCreatePane() {
		FriendsListFragment fri=new FriendsListFragment();
		//registerForContextMenu(fri.getListView());
        return fri;
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    } 
}

