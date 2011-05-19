package pt.up.fe.mobile.ui;


import com.google.android.apps.iosched.ui.BaseSinglePaneActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ExamsActivity extends BaseSinglePaneActivity {
	
    protected Fragment onCreatePane() {
    	
        return new ExamsFragment();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    }  
	
 /////////////////////////////////////////////////////////////////////////////////////////////////
    


}

