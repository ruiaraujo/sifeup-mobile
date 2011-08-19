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
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
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
    private ViewPager  viewPager; 
    private ViewPagerIndicator indicator;
	private ArrayList<Canteen> canteens;
    private LayoutInflater mInflater;
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
		mInflater = inflater;
		View root = inflater.inflate(R.layout.menus_canteens, getParentContainer(), true);
        viewPager = (ViewPager)root.findViewById(R.id.pager_menu);
       
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
        pagerAdapter = new PagerMenuAdapter();

        viewPager.setAdapter(pagerAdapter);
        
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
        viewPager.setOnPageChangeListener(indicator);
        
        // Start at a custom position
        viewPager.setCurrentItem(0);
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
    class PagerMenuAdapter extends PagerAdapter implements ViewPagerIndicator.PageInfoProvider 
    {
    	
    	
		@Override
		public String getTitle(int pos){
			return canteens.get(pos).description;
		}
		
		public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((View) view);
			
		}

		public int getCount() {
			return canteens.size();
		}

		public Object instantiateItem(View collection, int position) {
			View root = mInflater.inflate(R.layout.menu, viewPager, false);
			ExpandableListView list = (ExpandableListView) root.findViewById(R.id.menu_list);
			list.setAdapter(new MenusAdapter(canteens.get(position)));
			((ViewPager) collection).addView(root,0);
			return root;
		}

		public boolean isViewFromObject(View view, Object object) {
            return view==((View)object);
		}

		public void restoreState(Parcelable arg0, ClassLoader arg1) {}

		public Parcelable saveState() {
			return null;
		}

		public void startUpdate(View arg0) {}

		public void finishUpdate(View arg0) {}

    }
    
    private class MenusAdapter extends BaseExpandableListAdapter {
        // Sample data set.  children[i] contains the children (String[]) for groups[i].
        Canteen canteen;
        public MenusAdapter(Canteen c){
        	canteen = c;
        }
         
        
        public Object getChild(int groupPosition, int childPosition) {
            return canteen.menus[groupPosition].dishes[childPosition];
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return canteen.menus[groupPosition].dishes.length;
        }

        
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
            return null;
        }

        public Object getGroup(int groupPosition) {
            return canteen.menus[groupPosition].date;
        }

        public int getGroupCount() {
            return canteen.menus.length;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
            // Layout parameters for the ExpandableListView
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                  ViewGroup.LayoutParams.MATCH_PARENT, 84);

            TextView textView = new TextView(getActivity());
            textView.setLayoutParams(lp);
            // Center the text vertically
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            textView.setPadding(84, 0, 0, 0);
            textView.setText(getGroup(groupPosition).toString());
            return textView;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }

    }
}
