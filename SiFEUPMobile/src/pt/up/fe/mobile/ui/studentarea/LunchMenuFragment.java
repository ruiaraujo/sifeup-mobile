/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pt.up.fe.mobile.ui.studentarea;




import java.util.ArrayList;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;
import external.com.zylinc.view.ViewPagerIndicator;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * Lunch Menu Fragment
 * 
 * @author Ângela Igreja
 * 
 */
public class LunchMenuFragment extends BaseFragment 
{
	private PagerMenuAdapter pagerAdapter;
    private ViewPager  ViewPager; 
    private ViewPagerIndicator indicator;
   
 	/**
 	 * Cass Canteen. Save the name and menus of the canteen.
     *
 	 * @author Ângela Igreja
 	 *
 	 */
 	class Canteen
 	{
 		
 		public String name;
 		public String menu;
 		
 		Canteen(String n, String m)
 		{
 			this.name = n;
 			this.menu = m;
 		}
 	}
 	
	private ArrayList<Canteen> canteens;
    
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	    AnalyticsUtils.getInstance(getActivity()).trackPageView("/Lunch Menu");
	    canteens = new ArrayList<Canteen>();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) 
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflater.inflate(R.layout.menus_canteens, null);
		switcher.addView(root);
        ViewPager = (ViewPager)root.findViewById(R.id.pager_menu);

		        
        // Find the indicator from the layout
        indicator = (ViewPagerIndicator)root.findViewById(R.id.indicator_menu);
       
        new LunchMenusTask().execute();
        
		return switcher;//mandatory
	}
	
	/**
 	 * TODO: what is this?
 	 */
 	private void buildPages(){
 	// Create our custom adapter to supply pages to the viewpager.
        pagerAdapter = new PagerMenuAdapter(getActivity().getSupportFragmentManager());

        ViewPager.setAdapter(pagerAdapter);
        
        // Initialize the indicator. We need some information here:
        // * What page do we start on.
        // * How many pages are there in total
        // * A callback to get page titles
        //TODO
		indicator.init(0, pagerAdapter.getCount(), pagerAdapter);
		
		Resources res = getResources();
		Drawable prev = res.getDrawable(R.drawable.indicator_prev_arrow);
		Drawable next = res.getDrawable(R.drawable.indicator_next_arrow);
		
		// Set images for previous and next arrows.
		indicator.setArrows(prev, next);
		
        // Set the indicator as the pageChangeListener
        ViewPager.setOnPageChangeListener(indicator);
        
        // Start at a custom position
        ViewPager.setCurrentItem(0);
 	}
 	
	
	/**
 	 * Lunch Menus Task
 	 * 
 	 */
 	 private class LunchMenusTask extends AsyncTask<Void, Void, String> {

		protected void onPreExecute (){
			showLoadingScreen();
		}

		 protected void onPostExecute(String ret) {
		 	if ( getActivity() == null )
		 		return;
		 	if ( ret.equals("") )
			{
				if ( getActivity() != null ) 
				{
					Toast.makeText(getActivity(), getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
					getActivity().finish();
					return;
				}
			}
			else if ( ret.equals("Error") ){	
				if ( getActivity() != null ) 
				{
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(true);
					return;
				}
			}
			else{
				Log.e("Login","success");
				Canteen a = new Canteen("Grill", "null");
				canteens.add(a);
				Canteen b = new Canteen("Canteen", "null");
				canteens.add(b);
			    buildPages();
			    showMainScreen();
			}
		}

 		@Override
 		protected String doInBackground(Void ... theVoid) {
 			String page = "";
 			try {
 	    			page = SifeupAPI.getPrintingReply(
 								SessionManager.getInstance().getLoginCode());
 	    			
 	    			int error =	SifeupAPI.Errors.NO_ERROR;//SifeupAPI.JSONError(page);
 		    		switch (error)
 		    		{
 		    			case SifeupAPI.Errors.NO_AUTH:
 		    				return "Error";
 		    			case SifeupAPI.Errors.NO_ERROR:
 		    				return "Sucess";
 		    			case SifeupAPI.Errors.NULL_PAGE:
 		    				return "";
 		    		}

 	    		return "";
 				
 				
 			} catch (/*JSONException*/Exception e) {
 				if ( getActivity() != null ) 
 					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
 				e.printStackTrace();
 			}
 			return "";
 		}
     }

	
	
	/**
 	 * Pager Menu Adapter
 	 * 
 	 * @author angela
 	 *
 	 */
    class PagerMenuAdapter extends FragmentStatePagerAdapter implements ViewPagerIndicator.PageInfoProvider 
    {
    	
    	public PagerMenuAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			return new Fragment();
		}

		@Override
		public int getCount() {
			return canteens.size();
		}
		
		@Override
		public String getTitle(int pos){
			return canteens.get(pos).name;
		}	
    }
}
