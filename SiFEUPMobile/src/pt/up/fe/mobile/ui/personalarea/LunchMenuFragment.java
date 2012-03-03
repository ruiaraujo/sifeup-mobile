
package pt.up.fe.mobile.ui.personalarea;


import java.util.ArrayList;

import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.datatypes.Canteen;
import pt.up.fe.mobile.datatypes.Dish;
import pt.up.fe.mobile.sifeup.CanteenUtils;
import pt.up.fe.mobile.sifeup.ResponseCommand;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
public class LunchMenuFragment extends BaseFragment implements ResponseCommand
{
	private final static String CANTEEN_KEY = "pt.up.fe.mobile.ui.studentarea.CANTEENS";
	
	private PagerMenuAdapter pagerAdapter;
    private ViewPager  viewPager; 
    private TitlePageIndicator indicator;
	private ArrayList<Canteen> canteens;
    private LayoutInflater mInflater;
    
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	    AnalyticsUtils.getInstance(getActivity()).trackPageView("/Lunch Menu");
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
        indicator = (TitlePageIndicator)root.findViewById(R.id.indicator_menu);       
		return getParentContainer();//mandatory
	}
	
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if ( savedInstanceState != null )
        {
            canteens = savedInstanceState.getParcelableArrayList(CANTEEN_KEY);
            if ( canteens == null )
                task = CanteenUtils.getCanteensReply(this);
            else
            {
                buildPages();
                showFastMainScreen();
            }
        }
        else
        {
            task = CanteenUtils.getCanteensReply(this);
        }
    }
	
	/**
 	 * Build Pages
 	 */
 	private void buildPages(){
 		// Create our custom adapter to supply pages to the viewpager.
        pagerAdapter = new PagerMenuAdapter();

        viewPager.setAdapter(pagerAdapter);
        indicator.setViewPager(viewPager);
        
        // Start at a custom position
        indicator.setCurrentItem(0);
 	}
 	

 	@Override
 	public void onSaveInstanceState (Bundle outState){
 	    if ( canteens != null )
 	        outState.putParcelableArrayList(CANTEEN_KEY,canteens);
 	}

	
	/**
 	 * Pager Menu Adapter
 	 * 
 	 * @author Ângela Igreja
 	 *
 	 */
    class PagerMenuAdapter extends PagerAdapter implements TitleProvider
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
			list.expandGroup(0);
			((ViewPager) collection).addView(root,0);
            return root;
		}

		public boolean isViewFromObject(View view, Object object) {
            return view==((View)object);
		}

		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		    indicator.setViewPager(viewPager);
		}

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

	public void onError(ERROR_TYPE error) {
		if ( getActivity() == null )
	 		return;
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
			((BaseActivity)getActivity()).goLogin();
			break;
		case NETWORK:
			Toast.makeText(getActivity(), getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
		default:
			//TODO: general error
			break;
		}
        getActivity().finish();

	}

	@SuppressWarnings("unchecked")
	public void onResultReceived(Object... results) {
	    if ( getActivity() == null )
	        return;
		canteens = (ArrayList<Canteen>) results[0];
		if ( canteens.isEmpty() )
		{
		    showEmptyScreen(getString(R.string.lb_no_menu));
		    return;
		}
	    buildPages();
	    showMainScreen();
	}
}
