package pt.up.fe.mobile.ui.studentarea;


import pt.up.fe.mobile.ui.BaseSinglePaneActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;


/**
 * Class Description Activity
 * 
 * @author Ângela Igreja
 */
public class ClassDescriptionActivity extends BaseSinglePaneActivity 
{
	
	@Override
    protected Fragment onCreatePane() 
	{
        return new ClassDescriptionFragment();
    }
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) 
    {
        super.onPostCreate(savedInstanceState);
        getActivityHelper().setupSubActivity();
    } 
}

