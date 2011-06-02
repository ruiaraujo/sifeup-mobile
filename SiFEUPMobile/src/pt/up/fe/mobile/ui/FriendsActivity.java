package pt.up.fe.mobile.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;

public class FriendsActivity extends BaseSinglePaneActivity {
	
    protected Fragment onCreatePane() {
    	
        return new FriendsListFragment();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    }  

}

