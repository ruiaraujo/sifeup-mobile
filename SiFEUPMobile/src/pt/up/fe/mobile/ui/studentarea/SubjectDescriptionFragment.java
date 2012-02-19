package pt.up.fe.mobile.ui.studentarea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitleProvider;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.datatypes.Student;
import pt.up.fe.mobile.datatypes.Subject;
import pt.up.fe.mobile.datatypes.SubjectContent;
import pt.up.fe.mobile.datatypes.Subject.Book;
import pt.up.fe.mobile.datatypes.Subject.EvaluationComponent;
import pt.up.fe.mobile.datatypes.Subject.Software;
import pt.up.fe.mobile.datatypes.Subject.Teacher;
import pt.up.fe.mobile.datatypes.SubjectContent.File;
import pt.up.fe.mobile.datatypes.SubjectContent.Folder;
import pt.up.fe.mobile.sifeup.ResponseCommand;
import pt.up.fe.mobile.sifeup.SubjectUtils;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.DownloaderFragment;
import pt.up.fe.mobile.ui.LoginActivity;
import pt.up.fe.mobile.ui.profile.ProfileActivity;
import pt.up.fe.mobile.ui.webclient.WebviewActivity;
import pt.up.fe.mobile.ui.webclient.WebviewFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SubjectDescriptionFragment extends BaseFragment implements OnPageChangeListener, ResponseCommand {
	

	public final static String SUBJECT_CODE = "pt.up.fe.mobile.ui.studentarea.SUBJECT_CODE"; 
	public final static String SUBJECT_YEAR = "pt.up.fe.mobile.ui.studentarea.SUBJECT_YEAR"; 
	public final static String SUBJECT_PERIOD = "pt.up.fe.mobile.ui.studentarea.SUBJECT_PERIOD"; 
	
	private String code;
	private String year;
	private String period;
    Subject subject;
    SubjectContent subjectContent;
    /** */
    private PagerSubjectAdapter pagerAdapter;
    
    /** */
    private LayoutInflater layoutInflater;
    
    /** */
    private ViewPager  viewPager; 
    
    /** */
    private TabPageIndicator indicator;
    
    private int currentPage = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        code = args.get(SUBJECT_CODE).toString();
		year = args.get(SUBJECT_YEAR).toString();
		period = args.get(SUBJECT_PERIOD).toString();
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
		viewPager.setAdapter(new PagerSubjectAdapter());
        // Find the indicator from the layout
        indicator = (TabPageIndicator)root.findViewById(R.id.indicator_subject);
 		// Create our custom adapter to supply pages to the viewpager.
        pagerAdapter = new PagerSubjectAdapter();
        viewPager.setAdapter(pagerAdapter);
        indicator.setViewPager(viewPager);
        // Set the indicator as the pageChangeListener
        indicator.setOnPageChangeListener(this);
        SubjectUtils.getSubjectReply(code, year, period, this);		
        return getParentContainer();
	}

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.subject_menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_subject_schedule) {
        	if ( subject == null || subject.getCode() == null )
        		return true;
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
    		Intent i = new Intent(getActivity(), WebviewActivity.class);
            i.putExtra(WebviewFragment.URL_INTENT, url.toString());
    		startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    

	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error),
					Toast.LENGTH_LONG).show();
			((BaseActivity) getActivity())
					.goLogin(LoginActivity.EXTRA_DIFFERENT_LOGIN_REVALIDATE);
			return;
		default:// TODO: add general error message
			break;

		}
	}

	public void onResultReceived(Object... results) {
		if (getActivity() == null)
			return;
		if (subject == null )
		{
			subject = (Subject) results[0];
			SubjectUtils.getSubjectContentReply(code, year, period, this);
			return;
		}
		if ( subjectContent == null )
		{
			subjectContent = (SubjectContent) results[0];
			pagerAdapter.notifyDataSetChanged();
	        // Start at a custom position
	        indicator.setCurrentItem(0);
	        indicator.notifyDataSetChanged();
	        showMainScreen();	
		}
	}

    
    /**
 	 * Pager Subject Adapter
 	 * 
 	 * @author Ângela Igreja
 	 *
 	 */
    class PagerSubjectAdapter extends PagerAdapter implements TitleProvider
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
			if ( subject == null  || subjectContent == null)
				return 0;
			return 13;
		}

		@Override
		public Object instantiateItem(final View collection, int position) 
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
				{
						ListView list = (ListView) layoutInflater.inflate(R.layout.generic_list, viewPager, false);
						((ViewPager) collection).addView(list,0);	
						
						//String[] from = new String[] {"code", "name", "time"};
						String[] from = new String[] { "name"};
				        //int[] to = new int[] { R.id.teacher_code, R.id.teacher_name ,R.id.teacher_time };
						int[] to = new int[] { R.id.teacher_name};
					         // prepare the list of all records
				        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
				        
				        for(Teacher t : subject.getTeachers())
				        {
				             HashMap<String, String> map = new HashMap<String, String>();
				     
				          //   map.put("code", t.getCode());
				             map.put("name", t.getName());
				          //   map.put("time", t.getTime());
				           
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
				}
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
				{	ListView listEvaluation = (ListView) layoutInflater.inflate(R.layout.generic_list, viewPager, false);
					((ViewPager) collection).addView(listEvaluation,0);	
					
					String[] fromEvaluation = new String[] {"description", /*"type",*/ "typeDesc",/* "length", "conclusionDate"*/};
						
			        int[] toEvaluation = new int[] { R.id.description,  /*R.id.type,*/  R.id.typeDesc,/* R.id.length, R.id.conclusionDate*/};
			        
				    // prepare the list of all records
			        List<HashMap<String, String>> fillMapsEvaluation = new ArrayList<HashMap<String, String>>();
			        
			        for(EvaluationComponent e : subject.getEvaluation())
			        {
			             HashMap<String, String> map = new HashMap<String, String>();
			     
			             map.put("description", e.getDescription());
			            // map.put("type", e.getType());
			             map.put("typeDesc", e.getTypeDesc());
			             //map.put("length", e.getLength());
			            // map.put("conclusionDate", e.getConclusionDate());
			             fillMapsEvaluation.add(map);
			      
			        }
		            SimpleAdapter adapterEvaluation = new SimpleAdapter(getActivity(), fillMapsEvaluation, R.layout.list_item_subject_evaluation_component, fromEvaluation, toEvaluation);
				    listEvaluation.setAdapter(adapterEvaluation);
					return listEvaluation;
				}	
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
				{
					final ListView list = (ListView) layoutInflater.inflate(R.layout.generic_list, viewPager, false);
					((ViewPager) collection).addView(list,0);	
					String[] from = new String[] {"name"};
					int[] to = new int[] {R.id.folder_name};
					
					// prepare the list of all records
					List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
					 
					for(Folder f : subjectContent.getCurrentFolder().getFolders())
					{
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("name", f.getName());
						fillMaps.add(map);
					}
					for(File f : subjectContent.getCurrentFolder().getFiles())
					{
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("name", f.getName());
						fillMaps.add(map);
					}
					
					SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.list_item_folder, from, to);
					list.setAdapter(adapter);
					list.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> list, View item,
								int position, long id) {
							if ( position >= subjectContent.getCurrentFolder().getFolders().size()  )
							{
								//launch download;
								File toDownload = subjectContent.getCurrentFolder().getFiles().get(position-subjectContent.getCurrentFolder().getFolders().size());
								if ( toDownload.getUrl() == null || toDownload.getUrl().trim().length() == 0)
								{
									DownloaderFragment.newInstance(toDownload.getName(),"https://www.fe.up.pt/si/conteudos_service.conteudos_cont?pct_id="+toDownload.getCode() , toDownload.getFilename())
														.show(getFragmentManager(), "Downloader");
								}
								else
								{
									Intent i = new Intent(Intent.ACTION_VIEW);
									i.setData(Uri.parse(toDownload.getUrl()));
									startActivity(i);
								}
								return;
							}
							subjectContent.setCurrentFolder(subjectContent.getCurrentFolder().getFolders().get(position));
							pagerAdapter.notifyDataSetChanged();
							
					        
						}
					});
					return list; 
				}
						
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
		
		//This is just implemented like this so 
		// that the view pager will update itself when notifyDataSetChanged is called.
		public int getItemPosition(Object object) {
	        return POSITION_NONE;
	    }

    }
    
	public void onBackPressed() {
		if ( currentPage == 12 && subjectContent.getCurrentFolder().getParent() != null )
		{
			subjectContent.setCurrentFolder(subjectContent.getCurrentFolder().getParent());
			pagerAdapter.notifyDataSetChanged();
		}
		else
		{
			if ( getActivity() != null )
				getActivity().finish();
		}
	}

	//Unused
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    //Unused
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int page) {
        currentPage = page;
    }



}
