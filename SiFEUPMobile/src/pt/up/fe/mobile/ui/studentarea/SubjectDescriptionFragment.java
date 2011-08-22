package pt.up.fe.mobile.ui.studentarea;

import java.util.ArrayList;

import org.json.JSONException;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.service.Subject;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.profile.ProfileActivity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;



public class SubjectDescriptionFragment extends BaseFragment {
	
	private String code;
	private String year;
	private String period;
    Subject subject = new Subject();
    
    /** */
    private PagerSubjectAdapter pagerAdapter;
    
    /** */
    private LayoutInflater layoutInflater;
    
    /** */
    private ViewPager  viewPager; 
    private ViewPagerIndicator indicator;
    private ArrayList<String> subjectItems;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        code = args.get(SubjectDescriptionActivity.SUBJECT_CODE).toString();
		year = args.get(SubjectDescriptionActivity.SUBJECT_YEAR).toString();
		period = args.get(SubjectDescriptionActivity.SUBJECT_PERIOD).toString();
        subjectItems = new ArrayList<String>();
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/Subject Description");
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		layoutInflater = inflater;
		View root = inflater.inflate(R.layout.subject_description, getParentContainer(), true);
		viewPager = (ViewPager)root.findViewById(R.id.pager_subject);
		
        // Find the indicator from the layout
        indicator = (ViewPagerIndicator)root.findViewById(R.id.indicator_subject);
		//descriptionList = (ExpandableListView) root.findViewById(R.id.subject_description_list);
        
        new SubjectDescriptionTask().execute();
		return getParentContainer();
	}
       
	private void buildPages(){
 		// Create our custom adapter to supply pages to the viewpager.
        pagerAdapter = new PagerSubjectAdapter();

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
     * Private class to fetch data to server
     * 
     * @author Ângela Igreja
     * 
     */
    private class SubjectDescriptionTask extends AsyncTask<Void, Void, String> {

    	protected void onPreExecute (){
    		showLoadingScreen();
    	}

        protected void onPostExecute(String result) {
			if ( getActivity() == null )
				 return;
        	if ( result.equals("Success") )
        	{
				Log.e("Subjects","success");
				
				 try {
			         showMainScreen();
			         Log.e("JSON", "subjects visual list loaded");
				 }
				 catch (Exception ex){
					 ex.printStackTrace();
					 if ( getActivity() != null )
							Toast.makeText(getActivity(), "F*** Fragments", Toast.LENGTH_LONG).show();

				 }
    		}
			else if ( result.equals("Error") ){	
				Log.e("Login","error");
				if ( getActivity() != null ) 
				{
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(true);
					return;
				}
			}
			else if ( result.equals("") )
			{
				if ( getActivity() != null ) 	
				{
					Toast.makeText(getActivity(), getString(R.string.toast_server_error), Toast.LENGTH_LONG).show();
					getActivity().finish();
					return;
				}
			}
        }

		@Override
		protected String doInBackground(Void ... theVoid) 
		{
			String page = "";
		  	try {
	    			page = SifeupAPI.getSubjectDescReply(code,year,period);
	    			int error =	SifeupAPI.JSONError(page);
		    		
	    			switch (error)
		    		{
		    			case SifeupAPI.Errors.NO_AUTH:
		    				return "Error";
		    			case SifeupAPI.Errors.NO_ERROR:
		    				if (subject.JSONSubject(page) )
		    				{
		    					subjectItems.add(subject.getNameEn());
		    					subjectItems.add(subject.getNamePt());
		    					for(Object o : subjectItems)
		    					{
		    						System.out.println(o.toString());
		    					}
		    					return "Success";
		    				}
		    				else
		    					return "";
		    			case SifeupAPI.Errors.NULL_PAGE:
		    				return "";	
		    		}
			} catch (JSONException e) {
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

			return "";
		}
    }
    
    /**
 	 * Pager Subject Adapter
 	 * 
 	 * @author Ângela Igreja
 	 *
 	 */
    class PagerSubjectAdapter extends PagerAdapter implements ViewPagerIndicator.PageInfoProvider 
    {
		@Override
		public String getTitle(int pos){
			return subject.getNamePt();
		}
		
		@Override
		public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((View) view);
			
		}

		@Override
		public int getCount() {
			return subjectItems.size();
		}

		@Override
		public Object instantiateItem(View collection, int position) 
		{
			View root = layoutInflater.inflate(R.layout.subject_item, viewPager, false);
			
			ExpandableListView list = (ExpandableListView) root.findViewById(R.id.subject_list);
			
			list.setAdapter(new SubjectItemAdapter(subjectItems.get(position)));
			
			((ViewPager) collection).addView(root,0);
			return root;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
            return view==((View)object);
		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub
			
		}

    }
    
    private class SubjectItemAdapter extends BaseExpandableListAdapter {
        
    	Object item;
        
    	public SubjectItemAdapter(Object i){
        	item = i;
        }

    	//TODO: não sei que objecto é? Como sei qual é o layout? 
    	//como faco layout diferente para cada um?
		@Override
		public Object getChild(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}
         
    }
	
}
