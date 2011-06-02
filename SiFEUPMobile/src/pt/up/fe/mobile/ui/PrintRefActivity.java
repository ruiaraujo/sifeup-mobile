
package pt.up.fe.mobile.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;

public class PrintRefActivity extends BaseSinglePaneActivity {
	@Override
    protected Fragment onCreatePane() {
    	
        return new PrintRefFragment();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
        
    }    

	

}
