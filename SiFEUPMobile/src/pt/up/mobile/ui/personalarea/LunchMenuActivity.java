package pt.up.mobile.ui.personalarea;


import pt.up.mobile.ui.BaseSinglePaneActivity;
import android.support.v4.app.Fragment;


/**
 * Lunch Menu Activity
 * 
 * @author Ângela Igreja
 */
public class LunchMenuActivity extends BaseSinglePaneActivity 
{
	
	@Override
    protected Fragment onCreatePane() 
	{
		LunchMenuFragment lunchMenu = new LunchMenuFragment();
	
        return lunchMenu;
    }
	
}

