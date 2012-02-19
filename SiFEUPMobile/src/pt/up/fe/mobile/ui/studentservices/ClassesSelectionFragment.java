package pt.up.fe.mobile.ui.studentservices;


import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.sifeup.SifeupAPI;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.LoginActivity;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * Classes Selection Fragment
 * 
 * @author Ângela Igreja
 *
 */
public class ClassesSelectionFragment extends BaseFragment implements OnClickListener
{
	private PagerAdapter mPagerAdapter;
    private ViewPager  mViewPager; 
    private TitlePageIndicator indicator;
    
    /** Number of options to choose the classes */
 	final static private int NUMBER_OPTIONS = 10;

	private String [] subjects;
	private String [] classes;

	private ClassesSelectionOption [] options = new ClassesSelectionOption[NUMBER_OPTIONS];

 	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
 		super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.classes_selection ,getParentContainer(), true);
        Button bt = (Button) root.findViewById(R.id.classes_selection_submit);
	    bt.setOnClickListener(this);

        mViewPager = (ViewPager)root.findViewById(R.id.pager);

		// Create our custom adapter to supply pages to the viewpager.
        mPagerAdapter = new PagerAdapter(getActivity().getSupportFragmentManager());
        
        // Find the indicator from the layout
        indicator = (TitlePageIndicator)root.findViewById(R.id.indicator);
        new ClassesSelectionTask().execute();
        
		return getParentContainer();
    }
 	
 	/**
 	 * Build Pages
 	 */
 	private void buildPages(){
        mViewPager.setAdapter(mPagerAdapter);

        indicator.setViewPager(mViewPager);
        // Start at a custom position
        indicator.setCurrentItem(0);
 	}
 	
 	@Override
	public void onClick(View v) {
		Toast.makeText(getActivity(),onSubmitClick() , Toast.LENGTH_LONG).show();
	}
	
 	/**
 	 * Action when Submit Button is clicked.
 	 * @return
 	 */
 	public String onSubmitClick() {
		StringBuilder bt = new StringBuilder();
		
		for ( ClassesSelectionOption f : options )
		{
			if ( f == null )
			{
				bt.append("Opção nao usada");
				bt.append("\n\n");
				continue;
			}
			bt.append(f.getChoiceStatus());
			bt.append("\n\n");
		}
		
		return bt.toString();
	}
 	
 	/**
 	 * Classes Selection Task
 	 * 
 	 */
 	 private class ClassesSelectionTask extends AsyncTask<Void, Void, String> {

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
					((BaseActivity)getActivity()).goLogin(LoginActivity.EXTRA_DIFFERENT_LOGIN_REVALIDATE);
					return;
				}
			}
			else{
				Log.e("Login","success");
				subjects = new String []{"EIND", "IELE", "SINF","OLA"};
			    classes = new String []{"Sem Turma" , "Turma 1", "Turma 2", "Turma 3","Turma 4"};
			    buildPages();
			    showMainScreen();    
			}
		}

 		@Override
 		protected String doInBackground(Void ... theVoid) {
 			try {
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
 	 * Pager Adapter
 	 * 
     * @author Ângela Igreja
 	 *
 	 */
    class PagerAdapter extends FragmentStatePagerAdapter implements TitleProvider
    {
    	
    	public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			if ( options[pos] == null )
				options[pos] = ClassesSelectionOption.getInstance(subjects, classes );
			return options[pos];
		}

		@Override
		public int getCount() {
			return NUMBER_OPTIONS;
		}
		
		@Override
		public String getTitle(int pos){
			return getString(R.string.classes_selection_option,pos+1);
		}	
    }
}