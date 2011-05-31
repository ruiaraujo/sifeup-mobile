
package pt.up.fe.mobile.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;

public class PrintActivity extends BaseSinglePaneActivity {
	@Override
    protected Fragment onCreatePane() {
    	
        return new PrintFragment();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
        
    }    

	

}
