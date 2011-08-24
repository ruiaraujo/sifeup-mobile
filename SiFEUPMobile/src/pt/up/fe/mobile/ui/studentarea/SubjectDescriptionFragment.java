package pt.up.fe.mobile.ui.studentarea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.service.Subject;
import pt.up.fe.mobile.service.Subject.Teacher;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
    
    /** */
    private ViewPagerIndicator indicator;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        code = args.get(SubjectDescriptionActivity.SUBJECT_CODE).toString();
		year = args.get(SubjectDescriptionActivity.SUBJECT_YEAR).toString();
		period = args.get(SubjectDescriptionActivity.SUBJECT_PERIOD).toString();
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
					 buildPages();
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
		public String getTitle(int position)
		{
			switch ( position )
			{
				case 0 :
					return "Objectives";
					//getString(R.string.objectives);
				case 1 :
					return  "Content";
					//getString(R.string.content);
				case 2 :
					return "Teachers";
					//getString(R.string.teachers);
			}
			
			return "";
		}
		
		@Override
		public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((View) view);
			
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Object instantiateItem(View collection, int position) 
		{
			switch ( position )
			{
				case 0 :	
						View root = layoutInflater.inflate(R.layout.subject_content, viewPager, false);
						TextView text = (TextView) root.findViewById(R.id.content);
						text.setText(subject.getContent());
						((ViewPager) collection).addView(root,0);
						return root;
						
				case 1 :	
						View root2 = layoutInflater.inflate(R.layout.subject_content, viewPager, false);
						TextView text2 = (TextView) root2.findViewById(R.id.content);
						text2.setText(subject.getObjectives());
						((ViewPager) collection).addView(root2,0);
						return root2;
						
				case 2 :
						ListView list = (ListView) layoutInflater.inflate(R.layout.generic_list, viewPager, false);
						((ViewPager) collection).addView(list,0);	
						
						String[] from = new String[] {"code", "name", "time"};
						
				        int[] to = new int[] { R.id.teacher_code, R.id.teacher_name ,R.id.teacher_time };
					         // prepare the list of all records
				        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
				        
				        for(Teacher t : subject.getTeachers())
				        {
				             HashMap<String, String> map = new HashMap<String, String>();
				     
				             map.put("code", t.getCode());
				             map.put("name", t.getName());
				             map.put("time", t.getTime());
				           
				             fillMaps.add(map);   
				        }
				        
				        SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_subject_teacher, from, to);
				        list.setAdapter(adapter);
						return list;
						
						
			}
			
			return null;
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
    

}
