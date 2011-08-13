package pt.up.fe.mobile.ui.studentservices;

import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Classes Selection Activity
 * @author Ângela Igreja
 *
 */
public class ClassesSelectionActivity extends BaseSinglePaneActivity
{
	@Override
    protected Fragment onCreatePane() 
	{	
		return new ClassesSelectionFragment();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) 
    {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();    
    }    
}

