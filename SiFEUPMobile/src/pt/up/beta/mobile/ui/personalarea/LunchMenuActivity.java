package pt.up.beta.mobile.ui.personalarea;


import pt.up.beta.mobile.ui.BaseSinglePaneActivity;
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

