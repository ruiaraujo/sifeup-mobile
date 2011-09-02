package pt.up.fe.mobile.ui.studentarea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.service.Subject;
import pt.up.fe.mobile.service.Subject.Book;
import pt.up.fe.mobile.service.Subject.EvaluationComponent;
import pt.up.fe.mobile.service.Subject.Software;
import pt.up.fe.mobile.service.Subject.Teacher;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.profile.ProfileActivity;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;
import external.com.zylinc.view.ViewPagerIndicator;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

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
        setHasOptionsMenu(true);
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
		
        new SubjectDescriptionTask().execute();
		
        return getParentContainer();
	}

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.subject_menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_subject_schedule) {
			Intent i = new Intent(getActivity() , ScheduleActivity.class);
			i.putExtra(ScheduleFragment.SCHEDULE_TYPE,ScheduleFragment.SCHEDULE_UC) ;
			i.putExtra(ScheduleFragment.SCHEDULE_CODE, subject.getCode()  );
    		i.putExtra(Intent.EXTRA_TITLE , getString(R.string.title_schedule_arg,subject.getAcronym() ));
			startActivity(i);
            return true;
        }
        if (item.getItemId() == R.id.menu_go_to_subject_sigarra) {
        	StringBuilder url = new StringBuilder("https://www.fe.up.pt/si/disciplinas_geral.formview?");
    		url.append("p_cad_codigo=");
    		url.append(code);
    		url.append("&p_ano_lectivo=");
    		url.append(year);
    		url.append("&p_periodo=" );
    		url.append(period);
    		Uri uri = Uri.parse( url.toString() );
    		startActivity( new Intent( Intent.ACTION_VIEW, uri ) );
            return true;
        }
        return super.onOptionsItemSelected(item);
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
		indicator.onlyCenterText(true);
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
					return getString(R.string.objectives);
				case 1 :
					return getString(R.string.content);
				case 2 :
					return getString(R.string.teachers);
				case 3 :
					return getString(R.string.bibliography);
				case 4 :
					return getString(R.string.software);
				case 5 :
					return getString(R.string.metodology);
				case 6 :
					return getString(R.string.evaluation);
				case 7 :
					return getString(R.string.admission_exams);
				case 8 :
					return getString(R.string.final_grade);	
				case 9 :
					return getString(R.string.special_evaluation);	
				case 10 :
					return getString(R.string.improvement_classification);	
				case 11 :
					return getString(R.string.comments);	
				case 12 :
					return getString(R.string.subject_content);	
			}
			
			return "";
		}
		
		@Override
		public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((View) view);
			
		}

		@Override
		public int getCount() {
			return 13;
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
				        list.setOnItemClickListener(new OnItemClickListener() {

							public void onItemClick(AdapterView<?> arg0, View arg1,
									int pos, long id) {
								Teacher b =subject.getTeachers().get(pos);
								Intent i = new Intent(getActivity() , ProfileActivity.class);
								i.putExtra(ProfileActivity.PROFILE_CODE, b.getCode());
								i.putExtra(ProfileActivity.PROFILE_TYPE, ProfileActivity.PROFILE_EMPLOYEE);
								i.putExtra(Intent.EXTRA_TITLE,b.getName());
								startActivity(i);
												
							}
						});
						return list;
				
				case 3:
					ListView listBooks = (ListView) layoutInflater.inflate(R.layout.generic_list, viewPager, false);
					((ViewPager) collection).addView(listBooks,0);	
					
					String[] fromBook = new String[] {"typeDescription", "authors", "title", "link", "isbn"};
						
			        int[] toBook = new int[] { R.id.typeDescription, R.id.authors, R.id.title, R.id.link, R.id.isbn};
				    // prepare the list of all records
			        List<HashMap<String, String>> fillMapsBooks = new ArrayList<HashMap<String, String>>();
			        
			        for(Book b : subject.getBibliography())
			        {
			             HashMap<String, String> map = new HashMap<String, String>();
			     
			             map.put("typeDescription", b.getTypeDescription());
			             map.put("authors", b.getAuthors());
			             map.put("title", b.getTitle());
			             map.put("link", b.getLink());
			             map.put("isbn", b.getIsbn());
			           
			             fillMapsBooks.add(map);  
			             
			         
			        }
			        
			     
			        SimpleAdapter adapterBooks = new SimpleAdapter(getActivity(), fillMapsBooks, R.layout.list_item_subject_book, fromBook, toBook);
			        listBooks.setAdapter(adapterBooks);
			        listBooks.setOnItemClickListener(new OnItemClickListener() {

						public void onItemClick(AdapterView<?> arg0, View arg1,
								int pos, long id) {
							Book b =subject.getBibliography().get(pos);
							if ( b.getLink() == null )
								return;
							Intent i = new Intent(Intent.ACTION_VIEW);
							i.setData(Uri.parse(b.getLink()));
							startActivity(i);
											
						}
					});
					return listBooks;
					
				case 4:
					ListView listSoftware = (ListView) layoutInflater.inflate(R.layout.generic_list, viewPager, false);
					((ViewPager) collection).addView(listSoftware,0);	
					
					String[] fromSoftware = new String[] {"name", "description"};
						
			        int[] toSoftware = new int[] { R.id.name, R.id.description};
			        
				    // prepare the list of all records
			        List<HashMap<String, String>> fillMapsSoftware = new ArrayList<HashMap<String, String>>();
			        
			        for(Software s : subject.getSoftware())
			        {
			             HashMap<String, String> map = new HashMap<String, String>();
			     
			             map.put(fromSoftware[0], s.getName());
			             map.put(fromSoftware[1], s.getDescription());
			             fillMapsSoftware.add(map);
			        }
		            SimpleAdapter adapterSoftware = new SimpleAdapter(getActivity(), fillMapsSoftware, R.layout.list_item_subject_software, fromSoftware, toSoftware);
				    listSoftware.setAdapter(adapterSoftware);
					return listSoftware;
				
				case 5:
					View metodology = layoutInflater.inflate(R.layout.subject_content, viewPager, false);
					TextView metodologyText = (TextView) metodology.findViewById(R.id.content);
					metodologyText.setText(subject.getMetodology());
					((ViewPager) collection).addView(metodology,0);
					return metodology;
					
				case 6:	
					ListView listEvaluation = (ListView) layoutInflater.inflate(R.layout.generic_list, viewPager, false);
					((ViewPager) collection).addView(listEvaluation,0);	
					
					String[] fromEvaluation = new String[] {"description", "type", "typeDesc", "length", "conclusionDate"};
						
			        int[] toEvaluation = new int[] { R.id.description,  R.id.type,  R.id.typeDesc, R.id.length, R.id.conclusionDate};
			        
				    // prepare the list of all records
			        List<HashMap<String, String>> fillMapsEvaluation = new ArrayList<HashMap<String, String>>();
			        
			        for(EvaluationComponent e : subject.getEvaluation())
			        {
			             HashMap<String, String> map = new HashMap<String, String>();
			     
			             map.put("description", e.getDescription());
			             map.put("type", e.getType());
			             map.put("typeDesc", e.getTypeDesc());
			             map.put("length", e.getLength());
			             map.put("conclusionDate", e.getConclusionDate());
			             fillMapsEvaluation.add(map);
			      
			        }
		            SimpleAdapter adapterEvaluation = new SimpleAdapter(getActivity(), fillMapsEvaluation, R.layout.list_item_subject_evaluation_component, fromEvaluation, toEvaluation);
				    listEvaluation.setAdapter(adapterEvaluation);
					return listEvaluation;
					
				case 7:
					View admissionExams = layoutInflater.inflate(R.layout.subject_content, viewPager, false);
					TextView admissionExamsText = (TextView) admissionExams.findViewById(R.id.content);
					admissionExamsText.setText(subject.getFrequenceCond());
					((ViewPager) collection).addView(admissionExams,0);
					return admissionExams;

				case 8:
					View finalGrade = layoutInflater.inflate(R.layout.subject_content, viewPager, false);
					TextView finalGradeText = (TextView) finalGrade.findViewById(R.id.content);
					finalGradeText.setText(subject.getEvaluationFormula());
					((ViewPager) collection).addView(finalGrade,0);
					return finalGrade;
					
				case 9:
					View specialEvaluation = layoutInflater.inflate(R.layout.subject_content, viewPager, false);
					TextView specialEvaluationText = (TextView) specialEvaluation.findViewById(R.id.content);
					specialEvaluationText.setText(subject.getEvaluationProc());
					((ViewPager) collection).addView(specialEvaluation,0);
					return specialEvaluation;
					
				case 10:
					View improvementClassification = layoutInflater.inflate(R.layout.subject_content, viewPager, false);
					TextView improvementClassificationText = (TextView) improvementClassification.findViewById(R.id.content);
					improvementClassificationText.setText(subject.getFrequenceCond());
					((ViewPager) collection).addView(improvementClassification,0);
					return improvementClassification;
					
				case 11:
					View comments = layoutInflater.inflate(R.layout.subject_content, viewPager, false);
					TextView commentsText = (TextView) comments.findViewById(R.id.content);
					commentsText.setText(subject.getObservations());
					((ViewPager) collection).addView(comments,0);
					return comments;
					
				case 12:	
					Intent i = new Intent(getActivity() , SubjectContentActivity.class);
					i.putExtra(SubjectContentActivity.SUBJECT_CODE, subject.getAcronym());
					i.putExtra(SubjectContentActivity.SUBJECT_YEAR, "2010/2011");
					i.putExtra(SubjectContentActivity.SUBJECT_PERIOD, subject.getSemestre());
					startActivity(i);		
			}
			
			return null;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
            return view==((View)object);
		}

		@Override
		public void finishUpdate(View arg0) {
			
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			
		}

    }
    

}
