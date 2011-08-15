package pt.up.fe.mobile.ui.studentservices;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.ui.BaseFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;

/**
 * Change Password Fragment
 * 
 * @author Ângela Igreja
 *
 */
public class ChangePasswordFragment extends BaseFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
       super.onCreate(savedInstanceState);
       AnalyticsUtils.getInstance(getActivity()).trackPageView("/Change Password");
    }
	 
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) 
	{
    	//TODO: missing file
    	//ViewGroup root = (ViewGroup) inflater.inflate(R.layout.change_password, getParent(), true);
		
		super.onCreateView(inflater, container, savedInstanceState);
		
		return getParentContainer();
	} 
	    
	      
}

