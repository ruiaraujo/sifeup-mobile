package pt.up.fe.mobile.ui.studentservices;

import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CardRequestFragment extends BaseFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
       super.onCreate(savedInstanceState);
       AnalyticsUtils.getInstance(getActivity()).trackPageView("/Card Request");
    }
	 
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) 
	{
		//ViewGroup root = (ViewGroup) inflater.inflate(R.layout.list_ucs_inscriptions, null);
		
		return  super.onCreateView(inflater, container, savedInstanceState);
	} 
	    
	      
}


