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
import pt.up.fe.mobile.service.Canteen;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.service.Canteen.Dish;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;
import external.com.zylinc.view.ViewPagerIndicator;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
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
    
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	    AnalyticsUtils.getInstance(getActivity()).trackPageView("/Lunch Menu");
	    canteens = new ArrayList<Canteen>();
	}
	
	@Override
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
		indicator.onlyCenterText(true);
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
    			
    			int error =	SifeupAPI.JSONError(page);
	    		
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
 			} catch ( JSONException e) {
 				if ( getActivity() != null ) 
 				//	Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
 				e.printStackTrace();
 			}
 			return "";
 		}
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
     			
     			canteen.parseJson(jBlock);
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
			return canteens.get(pos).getDescription();
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
      
    	Canteen canteen;
        
    	public MenusAdapter(Canteen c){
        	canteen = c;
        }
         
        
        public Object getChild(int groupPosition, int childPosition) {
            return canteen.getDish(groupPosition , childPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return canteen.getDishesCount(groupPosition);
        }

        
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) 
        {
        	View root = mInflater.inflate(R.layout.list_item_menu_dish, null );
        	Dish dish = (Dish) getChild(groupPosition, childPosition);
        	TextView description = (TextView) root.findViewById(R.id.dish_description);
        	TextView type = (TextView) root.findViewById(R.id.dish_description_type);
            description.setText(dish.getDescription());
            type.setText(dish.getDescriptionType());
        	return root;
        }

        public Object getGroup(int groupPosition) {
            return canteen.getDate(groupPosition);
        }

        public int getGroupCount() {
            return canteen.getMenuCount();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) 
        {
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
            return false;
        }

        public boolean hasStableIds() {
            return true;
        }

    }
}
