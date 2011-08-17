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




import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
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
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private Dish[] dishes;	
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
		View root = inflater.inflate(R.layout.menus_canteens, getParentContainer(), true);
        ViewPager = (ViewPager)root.findViewById(R.id.pager_menu);
       
        // Find the indicator from the layout
        indicator = (ViewPagerIndicator)root.findViewById(R.id.indicator_menu);
       
        new LunchMenusTask().execute();
        
		return getParentContainer();//mandatory
	}
	
	/**
 	 * Build Pages
 	 */
 	private void buildPages(){
 	// Create our custom adapter to supply pages to the viewpager.
        pagerAdapter = new PagerMenuAdapter(getActivity().getSupportFragmentManager());

        ViewPager.setAdapter(pagerAdapter);
        
        // Initialize the indicator. We need some information here:
        // * What page do we start on.
        // * How many pages are there in total
        // * A callback to get page titles
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
        dishes = pagerAdapter.getCanteen(0).menus[0].dishes;
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
			    buildPages();
			    showMainScreen();
			}
		}

 		@Override
 		protected String doInBackground(Void ... theVoid) {
 			String page = "";
 			
 			try 
 			{	
    			page = SifeupAPI.getCanteensReply();
    			
    			int error =	SifeupAPI.Errors.NO_ERROR;//SifeupAPI.JSONError(page);
	    		
    			switch (error)
	    		{
	    			case SifeupAPI.Errors.NO_AUTH:
	    				return "Error";
	    			case SifeupAPI.Errors.NO_ERROR:
	    				JSONLunchMenu(page);
	    				return "Sucess";
	    			case SifeupAPI.Errors.NULL_PAGE:
	    				return "";
	    		}

 	    		return "";
 			} catch (/*JSONException*/Exception e) {
 				if ( getActivity() != null ) 
 				//	Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
 				e.printStackTrace();
 			}
 			return "";
 		}
     }

  	/**
  	 * Class Canteen. Save the name and menus of the canteen.
     *
  	 * @author Ângela Igreja
  	 *
  	 */
  	private class Canteen implements Serializable
  	{
  		private int code;
  		private String description;
  		private String timetable;
  		private Menu[] menus;
  	}
  	
	/**
  	 * Class Menu. Save the information of menu.
     *
  	 * @author Ângela Igreja
  	 *
  	 */
  	private class Menu implements Serializable
  	{
  		private String state;
  		private String date;
  		private Dish[] dishes;  		
  	}
  	
  	
  	/**
  	 * Class Dish. Save the information of dish.
     *
  	 * @author Ângela Igreja
  	 *
  	 */
  	public class Dish implements Serializable
  	{
  		private String state;
  		public String description;
  		private int type;
  		public String descriptionType;
  		
  		
  	}
     
     /** 
 	 * Canteens Parser.
 	 * Returns true in case of correct parsing.
 	 * 
 	 * @param page
 	 * @return boolean
 	 * @throws JSONException
 	 */
     public boolean JSONLunchMenu(String page) throws JSONException
     {
     	JSONObject jObject = new JSONObject(page);
     	     	
     	if(jObject.has("cantinas"))
     	{
     		Log.e("JSON", "founded cantinas");
     		JSONArray jArray = jObject.getJSONArray("cantinas");

     		for(int i = 0; i < jArray.length(); i++)
     		{
    
     			JSONObject jBlock = jArray.getJSONObject(i);
     
     			Canteen canteen = new Canteen();
     			
     			if(jBlock.has("codigo")) canteen.code = jBlock.getInt("codico"); 
     			
     			if(jBlock.has("descricao")) canteen.description = jBlock.getString("descricao");
     			
     			if(jBlock.has("horario")) canteen.timetable = jBlock.getString("horario");
     			
     			if(jBlock.has("ementas"))
     			{
     				JSONArray jArrayMenus = jBlock.getJSONArray("ementas");
     				canteen.menus = new Menu[jArrayMenus.length()];
     				for(int j = 0; j < jArrayMenus.length(); j++)
     	     		{
     					JSONObject jMenu = jArrayMenus.getJSONObject(j);
     					
     					Menu menu = new Menu();
     					
     					if(jMenu.has("estado")) menu.state = jMenu.getString("estado"); 
     					
     					if(jMenu.has("data")) menu.date = jMenu.getString("data"); 
     					
     					if(jMenu.has("pratos"))
     					{
     						JSONArray jArrayDishs = jMenu.getJSONArray("pratos");
     						menu.dishes = new Dish[jArrayDishs.length()];
     						for(int k = 0; k < jArrayDishs.length(); k++)
     	     	     		{
     	     					JSONObject jDish = jArrayDishs.getJSONObject(k);
     	     					
     	     					Dish dish = new Dish();
     	     					
     	     					if(jDish.has("estado")) dish.state = jDish.getString("estado"); 
     	     					
     	     					if(jDish.has("descricao")) dish.description = jDish.getString("descricao");
     	     					
     	     					if(jDish.has("tipo")) dish.type = jDish.getInt("tipo"); 
     	     					
     	     					if(jDish.has("tipo_descr")) dish.descriptionType = jDish.getString("tipo_descr"); 
     	     					menu.dishes[k] = dish;
     	     	     		}
     						
     					}
     					canteen.menus[j] = menu;
     	     		}
     				
     			}
     			// add canteen to canteens
     			this.canteens.add(canteen);
     		}
     		Log.e("JSON", "loaded canteens");
     		return true;
     	}
     	Log.e("JSON", "canteens not found");
     	return false;
    }
	
	/**
 	 * Pager Menu Adapter
 	 * 
 	 * @author Ângela Igreja
 	 *
 	 */
    class PagerMenuAdapter extends FragmentStatePagerAdapter implements ViewPagerIndicator.PageInfoProvider 
    {
    	
    	public PagerMenuAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) 
		{
			return MenuFragment.getInstance(dishes);
		}

		@Override
		public int getCount() {
			return canteens.size();
		}
		
		@Override
		public String getTitle(int pos){
			return canteens.get(pos).description;
		}
		
		public Canteen getCanteen(int pos){
			return canteens.get(pos);
		}
    }
}
