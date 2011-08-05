package pt.up.fe.mobile.ui.studentservices;

import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class NewRequestActivity extends BaseSinglePaneActivity
{
	@Override
    protected Fragment onCreatePane() 
	{	
        return new CardRequestFragment();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) 
    {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();    
    }    
}
