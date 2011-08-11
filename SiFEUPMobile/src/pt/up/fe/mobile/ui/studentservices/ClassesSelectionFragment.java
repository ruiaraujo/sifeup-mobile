package pt.up.fe.mobile.ui.studentservices;

import external.com.zylinc.view.ViewPagerIndicator;

import pt.up.fe.mobile.R;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ClassesSelectionFragment extends Fragment
{
	PagerAdapter mPagerAdapter;
    ViewPager  mViewPager;
 	final static private int NUMBER_OPTIONS = 10;

	private String [] subjects;
	private String [] classes;
	private ClassesSelectionOption [] options = new ClassesSelectionOption[NUMBER_OPTIONS];

 	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.classes_selection ,null);
        
        //TODO:MOVE to taks
        subjects = new String []{"EIND", "IELE", "SINF","OLA"};
        classes = new String []{"Turma 1", "Turma 2", "Turma 3","Turma 4"};

        // Create our custom adapter to supply pages to the viewpager.
        mPagerAdapter = new PagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager = (ViewPager)root.findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
        
        // Start at a custom position
        mViewPager.setCurrentItem(0);
        
        // Find the indicator from the layout
        ViewPagerIndicator indicator = (ViewPagerIndicator)root.findViewById(R.id.indicator);
		
        // Set the indicator as the pageChangeListener
        mViewPager.setOnPageChangeListener(indicator);
        
        // Initialize the indicator. We need some information here:
        // * What page do we start on.
        // * How many pages are there in total
        // * A callback to get page titles
		indicator.init(0, mPagerAdapter.getCount(), mPagerAdapter);
		Resources res = getResources();
		Drawable prev = res.getDrawable(R.drawable.indicator_prev_arrow);
		Drawable next = res.getDrawable(R.drawable.indicator_next_arrow);
		
		// Set images for previous and next arrows.
		indicator.setArrows(prev, next);
		return root;
    }
    
    class PagerAdapter extends FragmentStatePagerAdapter implements ViewPagerIndicator.PageInfoProvider , 
    																	ClassesSelectionOption.onSubmitClickListener{
    	
    	public PagerAdapter(FragmentManager fm) {
			super(fm);
			
			for ( int i = 0 ; i < NUMBER_OPTIONS ; ++i)
			{
				options[i] = ClassesSelectionOption.getInstance(subjects, classes , this );
			}
		}

		@Override
		public Fragment getItem(int pos) {
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
		
		@Override
		public String onSubmitClick() {
			StringBuilder bt = new StringBuilder();
			for ( ClassesSelectionOption f : options )
			{
				bt.append(f.getChoiceStatus());
				bt.append("\n\n");
			}
			return bt.toString();
		}
    }


    

    
}