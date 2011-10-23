package pt.up.fe.mobile.ui.studentarea;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.Block;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.tracker.AnalyticsUtils;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;

import external.com.google.android.apps.iosched.ui.widget.BlockView;
import external.com.google.android.apps.iosched.ui.widget.BlocksLayout;
import external.com.google.android.apps.iosched.ui.widget.ObservableScrollView;
import external.com.google.android.apps.iosched.util.MotionEventUtils;
import external.com.google.android.apps.iosched.util.UIUtils;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
/**
 * This fragment is responsible of fetching a schedule of the student, 
 * which number is passed as argument. 
 *  Also it can export the schedule for Google Calendar.
 *
 * @author Ângela Igreja
 *
 */
public class ScheduleFragment extends BaseFragment implements
			ObservableScrollView.OnScrollListener, OnPageChangeListener, OnClickListener {
    private ViewPager mPager;
    private TextView mTitle;
    private int mTitleCurrentDayIndex = -1;
    private View mLeftIndicator;
    private View mRightIndicator;
    private ArrayList<Block> schedule = new ArrayList<Block>();
    private List<Day> mDays = new ArrayList<Day>();
    private long mondayMillis;
    private LayoutInflater mInflater;
    private boolean fetchingPreviousWeek = false;
    private boolean fetchingNextWeek = false;
    
    private String scheduleCode;
    private int scheduleType;
    /**
     * The key for the schedule code in the intent.
     */
    final public static String SCHEDULE_CODE  = "pt.up.fe.mobile.ui.studentarea.SCHEDULE_CODE";
    
    /**
     * The key for the schedule type in the intent.
     */
    final public static String SCHEDULE_TYPE  = "pt.up.fe.mobile.ui.studentarea.SCHEDULE_TYPE";

    final public static int SCHEDULE_STUDENT  = 0;
    final public static int SCHEDULE_EMPLOYEE  = 1;
    final public static int SCHEDULE_ROOM  = 2;
    final public static int SCHEDULE_UC  = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   Bundle args = getArguments();
        setHasOptionsMenu(true);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/exams");
  		scheduleCode = (String) getArguments().getString(SCHEDULE_CODE);
		if ( scheduleCode == null )
			scheduleCode = SessionManager.getInstance().getLoginCode();
		scheduleType = getArguments().getInt(SCHEDULE_TYPE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	mInflater  = inflater;
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_schedule,getParentContainer(),true);
		mPager = (ViewPager) root.findViewById(R.id.pager);
        mTitle = (TextView) root.findViewById(R.id.block_title);
        
        mPager.setOnPageChangeListener(this);
        
        mLeftIndicator = root.findViewById(R.id.indicator_left);
        mLeftIndicator.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if ((motionEvent.getAction() & MotionEventUtils.ACTION_MASK)
                        == MotionEvent.ACTION_DOWN) {
                	decreaseDay();
                    return true;
                }
                return false;
            }
        });
        mLeftIndicator.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	decreaseDay();
            }
        });

        mRightIndicator = root.findViewById(R.id.indicator_right);
        mRightIndicator.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if ((motionEvent.getAction() & MotionEventUtils.ACTION_MASK)
                        == MotionEvent.ACTION_DOWN) {
                	increaseDay();
                    return true;
                }
                return false;
            }
        });
        mRightIndicator.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	increaseDay();
            }
        });
        mondayMillis = firstDayofWeek(false);
        setupDay(mondayMillis - (3 * DateUtils.DAY_IN_MILLIS),0);
        setupDay(mondayMillis,1);
  		setupDay(mondayMillis + (1 * DateUtils.DAY_IN_MILLIS),2);
  		setupDay(mondayMillis + (2 * DateUtils.DAY_IN_MILLIS),3);
  		setupDay(mondayMillis + (3 * DateUtils.DAY_IN_MILLIS),4);
  		setupDay(mondayMillis + (4 * DateUtils.DAY_IN_MILLIS),5);
  		setupDay(mondayMillis + (7 * DateUtils.DAY_IN_MILLIS),6);
        onPageSelected(1);

		new ScheduleTask().execute();

        return getParentContainer();
    }
    
    private void increaseDay(){
    	mTitleCurrentDayIndex++;
    	mPager.setCurrentItem(mTitleCurrentDayIndex);
       	if ( mTitleCurrentDayIndex >= mDays.size() )
       		mTitleCurrentDayIndex = 1 ;
    }
    private void decreaseDay(){
    	mTitleCurrentDayIndex--;
    	mPager.setCurrentItem(mTitleCurrentDayIndex);
    	if ( mTitleCurrentDayIndex <= 0 )
    		mTitleCurrentDayIndex = mDays.size()-2 ;
    }
    

    public void onScrollChanged(ObservableScrollView view) {
        // Keep each day view at the same vertical scroll offset.
        final int scrollY = view.getScrollY();
        for (Day day : mDays) {
            if (day.scrollView != view) {
                day.scrollView.scrollTo(0, scrollY);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.schedule_menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_now) {
            if (!updateNowView(true)) {
            	AnalyticsUtils.getInstance(getActivity()).trackEvent(AnalyticsUtils.MENU_CAT, 
						"Click", "Go to Now", 1);
                Toast.makeText(getActivity(), R.string.toast_now_not_visible,
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (item.getItemId() == R.id.menu_export_calendar) {
        	// export to Calendar (create event)
        	AnalyticsUtils.getInstance(getActivity()).trackEvent(AnalyticsUtils.MENU_CAT, 
        									"Click", "Export Calendar", 1);
    		calendarExport();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
   
    private void movetoNextWeek(){
    	fetchingNextWeek = true;
    	mondayMillis = mDays.get(mDays.size()-1).timeStart;
    	for ( int i = 0 ; i < mDays.size() ; ++i )
    		updateDay(i, mondayMillis + i * DateUtils.DAY_IN_MILLIS );
    	new ScheduleTask().execute();
    }
    
    private void movetoPreviousWeek(){
    	fetchingPreviousWeek = true;
    	mondayMillis = mDays.get(0).timeStart;
    	mondayMillis -= 4 * DateUtils.DAY_IN_MILLIS;
    	for ( int i = 0 ; i < mDays.size() ; ++i )
    		updateDay(i, mondayMillis + i * DateUtils.DAY_IN_MILLIS );
    	new ScheduleTask().execute();
    }
    
    /**
     * Update position and visibility of "now" view.
     */
    private boolean updateNowView(boolean forceScroll) {
        final long now = UIUtils.getCurrentTime(false);

       Day nowDay = null; // effectively Day corresponding to today
        for (Day day : mDays) {
            if (now >= day.timeStart && now <= day.timeEnd) {
                nowDay = day;
                day.nowView.setVisibility(View.VISIBLE);
            } else {
                day.nowView.setVisibility(View.GONE);
            }
        }
        if (nowDay != null && forceScroll) {
        	long hours  = ( now - nowDay.timeStart) / 3600000;
        	double timeOffset =  (double)(hours -nowDay.blocksView.getTimeRulerStartHour() ) 
        					/ (double)nowDay.blocksView.getTimeRulerHours();
            // Scroll to show "now" in center
            mPager.setCurrentItem(nowDay.index);
            final int offset = (int) (nowDay.scrollView.getHeight()* timeOffset);
            nowDay.scrollView.scrollTo(0, offset);
            nowDay.blocksView.requestLayout();
            return true;
        }
        return false;
    }

    private void setupDay(long startMillis, int i) {
        Day day = new Day();

        // Setup data
        day.index = mDays.size();
        day.timeStart = startMillis;
        day.timeEnd = startMillis + DateUtils.DAY_IN_MILLIS;
        // Setup views
        day.rootView = (ViewGroup) mInflater.inflate(R.layout.blocks_content, null);

        day.scrollView = (ObservableScrollView) day.rootView.findViewById(R.id.blocks_scroll);
        day.scrollView.setOnScrollListener(this);

        day.blocksView = (BlocksLayout) day.rootView.findViewById(R.id.blocks);
        day.nowView = day.rootView.findViewById(R.id.blocks_now);

        day.blocksView.setDrawingCacheEnabled(true);
        day.blocksView.setAlwaysDrawnWithCacheEnabled(true);
        // Clear out any existing sessions before inserting again
        day.blocksView.removeAllBlocks();
        Time date = new Time(UIUtils.TIME_REFERENCE);
  		date.set(startMillis);
  		date.normalize(false);
        day.label =DateUtils.getDayOfWeekString(date.weekDay+1, DateUtils.LENGTH_LONG) +", " +
        			date.format("%d-%m");
        mDays.add(i, day);
    }
    
    private void updateDay( int index , long millis ){
    	
    	Time date = new Time(UIUtils.TIME_REFERENCE);
  		date.set(millis);
  		date.normalize(false);
        mDays.get(index).label =DateUtils.getDayOfWeekString(date.weekDay+1, DateUtils.LENGTH_LONG) +", " +
        			date.format("%d-%m");
        mDays.get(index).timeStart = millis;
        mDays.get(index).timeEnd = millis + DateUtils.DAY_IN_MILLIS;
    }
    
    private void cleanBlocks(){
    	for (int i = 0 ; i < mDays.size() ; ++i )
    		mDays.get(i).blocksView.removeAllBlocks();
    }

    
    /**
     * A helper class containing object references related to a particular day in the schedule.
     */
    @SuppressWarnings("serial")
	private class Day implements Serializable{
        private ViewGroup rootView;
        private ObservableScrollView scrollView;
        private View nowView;
        private BlocksLayout blocksView;

        private int index = -1;
        private String label = null;
        private long timeStart = -1;
        private long timeEnd = -1;
    }

    
    /** 
	 * Schedule Parser
	 * Stores Blocks in ScheduleFragment.schedule
	 * Returns true in case of correct parsing.
	 * 
	 * @param page
	 * @return boolean
	 * @throws JSONException
	 */
    public boolean JSONSchedule(String page) throws JSONException{
    	JSONObject jObject = new JSONObject(page);
    	
    	// clear old schedule
    	this.schedule.clear();
    	
    	if(jObject.has("horario")){
    		Log.e("JSON", "founded schedule");
    		JSONArray jArray = jObject.getJSONArray("horario");
    		
    		// iterate over jArray
    		for(int i = 0; i < jArray.length(); i++){
    			// new JSONObject
    			JSONObject jBlock = jArray.getJSONObject(i);
    			// new Block
    			Block block = new Block();
    			
    			if(jBlock.has("dia")) block.setWeekDay(jBlock.getInt("dia") - 2); // Monday is index 0
    			if(jBlock.has("hora_inicio")) block.setStartTime(jBlock.getInt("hora_inicio"));
    			if(jBlock.has("cad_codigo")) block.setLectureCode(jBlock.getString("cad_codigo"));
    			if(jBlock.has("cad_sigla")) block.setLectureAcronym(jBlock.getString("cad_sigla"));
    			if(jBlock.has("tipo")) block.setLectureType(jBlock.getString("tipo"));
    			if(jBlock.has("aula_duracao")) block.setLectureDuration(jBlock.getDouble("aula_duracao"));
    			if(jBlock.has("turma_sigla")) block.setClassAcronym(jBlock.getString("turma_sigla"));
    			if(jBlock.has("doc_sigla")) block.setTeacherAcronym(jBlock.getString("doc_sigla"));
    			if(jBlock.has("doc_codigo")) block.setTeacherCode(jBlock.getString("doc_codigo"));
    			if(jBlock.has("sala_cod")){
    				block.setRoomCode(jBlock.getString("sala_cod"));
    				while ( block.getRoomCode().length() < 3 )
    					block.setRoomCode("0" + block.getRoomCode());
    			}
    			
    			if(jBlock.has("edi_cod")) block.setBuildingCode(jBlock.getString("edi_cod"));
    			if(jBlock.has("periodo")) block.setSemester(jBlock.getString("periodo"));
    			int secondYear = UIUtils.secondYearOfSchoolYear(mondayMillis);
    			int firstYear = secondYear -1;
    			block.setYear(firstYear + "/" + secondYear);
    			// add block to schedule
    			this.schedule.add(block);
    		}
    		Log.e("JSON", "loaded schedule");
    		return true;
    	}
    	Log.e("JSON", "schedule not found");
    	return false;
    }


    private void addBlock(int dayIndex ,Block block ) {
        if (getActivity() == null) {
            return;
        }
        // Plus one because mDays.at(0) is a day of the previous week
        Day day = mDays.get(dayIndex+1);

        final String blockId = block.getLectureAcronym() + " (" + block.getLectureType() + ")" 
								+ "\n" +  block.getBuildingCode() + block.getRoomCode();
        final String title = blockId;
        final long start = block.getStartTime() * 1000 + day.timeStart;
        final long end = ( block.getStartTime() + (long)(block.getLectureDuration() * 3600) ) * 1000;
        final boolean containsStarred = false;

        int column = 0;
        if ( block.getLectureType().equals("T") )
        	column = 1;
		final BlockView blockView = new BlockView(getActivity(), blockId,title , start, end,
        		containsStarred , column );
		blockView.setOnClickListener(this);
        day.blocksView.addBlock(blockView);
            
              
       
    }
    
    /**
     * 
     * This function return the  milliseconds of the the first day of the week,
     * in our case Monday, though if it is past Friday, it will return
     * the next Monday.
     * 
     * @return mondayMillis milliseconds of the the first day of the week
     */
    
    private static long firstDayofWeek(boolean utc){
    	long mondayMillis = UIUtils.getCurrentTime(utc);
  		Time yourDate = new Time(UIUtils.TIME_REFERENCE);
  		yourDate.set(mondayMillis);
  		yourDate.normalize(false);
  		yourDate.minute=0;
  		yourDate.hour=0;
  		yourDate.second=1;
  		yourDate.normalize(false);
  		int weekDay = yourDate.weekDay -1;
  		//Our week starts at Monday
  		if ( weekDay < 0 )
  			weekDay = 6;
  		mondayMillis = yourDate.toMillis(true);
  		mondayMillis -= (weekDay * 24 * 60 * 60 * 1000);
  		if ( ( mondayMillis  + 5 * DateUtils.DAY_IN_MILLIS ) < UIUtils.getCurrentTime(false) )
  			mondayMillis += 7 * DateUtils.DAY_IN_MILLIS;
  		return mondayMillis;
    }
    
    
    
    
    
    
    /** Exports the schedule to Google Calendar
     * WARNING: This is done against Google recomendations.
     * TODO: Change to access the GData API directly.
     * 		 Produce an ICAL file which can be imported by most calendars.*/
    private boolean calendarExport(){
    	
    	Context ctx = this.getActivity();
    	final ContentResolver cr = ctx.getContentResolver();
    	Cursor cursor = null;
        //Creating Queries
        if ( Build.VERSION.SDK_INT >= 8)
            cursor = cr.query(
            		Uri.parse("content://com.android.calendar/calendars"), 
            		new String[]{ "_id", "displayName", "selected"  }, 
            		null, null, null);
        else
            cursor = cr.query(
            		Uri.parse("content://calendar/calendars"), 
            		new String[]{ "_id","displayName", "selected"  }, 
            		null, null, null);
        if ( cursor == null )
        {
        	if ( getActivity() != null ) 
        		Toast.makeText(getActivity(), R.string.toast_export_calendar_error, Toast.LENGTH_LONG).show();
        	return false;
        }
        // Iterate over calendars to store names and ids
        if ( cursor.moveToFirst() ) {
            final String[] calNames = new String[cursor.getCount()];
            final int[] calIds = new int[cursor.getCount()];
            for (int i = 0; i < calNames.length; i++) {
                calIds[i] = cursor.getInt(0);
                calNames[i] = cursor.getString(1);
                cursor.moveToNext();
            }
            
            // Throw a Calendar chooser menu
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setSingleChoiceItems(calNames, -1, new DialogInterface.OnClickListener() {
     
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	
                	// iterate over schedule and add them to schedule
                	for(Block b : schedule){
                		// new event
                		ContentValues event = new ContentValues();
                		event.put("calendar_id", calIds[which]);
                		event.put("title", b.getLectureAcronym() + " (" + b.getLectureType() + ")");
                		event.put("eventLocation", b.getBuildingCode()+b.getRoomCode());
                		event.put("description", "Professor: " + b.getTeacherAcronym());
                		long date =  UIUtils.convertToUtc(mondayMillis) + 
                					b.getWeekDay() * DateUtils.DAY_IN_MILLIS + 
                					b.getStartTime()*1000;
                		event.put("dtstart", date );
                		event.put("dtend", date + b.getLectureDuration()*3600000 );
                		
                		// TODO:  event recursive - this will not be done.
                		Uri newEvent = null;
                		// insert event
                		if (Integer.parseInt(Build.VERSION.SDK) >= 8 )
                			newEvent =cr.insert(Uri.parse("content://com.android.calendar/events"), event);
                		else
                			newEvent = cr.insert(Uri.parse("content://calendar/events"), event);
                		// check event error
                		if(newEvent == null) Log.e("ScheduleExport", "error on event");
                		
                	}
                	
                	
                    dialog.cancel();
                    if ( getActivity() != null ) 
                		Toast.makeText(getActivity(), R.string.toast_export_calendar_finished, Toast.LENGTH_LONG).show();

                }
     
            });
     
            builder.create().show();
        }
        cursor.close();
    	
    	return true;
    }
    
    class DayAdapter extends PagerAdapter
	{
		public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((View) view);
			
		}

		public void finishUpdate(View arg0) {}

		public int getCount() {
			return mDays.size();
		}

		public Object instantiateItem(View collection, int position) {
			((ViewPager) collection).addView(mDays.get(position).rootView,0);
			return mDays.get(position).rootView;
		}

		public boolean isViewFromObject(View view, Object object) {
            return view==((View)object);
		}

		public void restoreState(Parcelable arg0, ClassLoader arg1) {}

		public Parcelable saveState() {
			return null;
		}

		public void startUpdate(View arg0) {}

		


	}
	public void onPageScrollStateChanged(int state) {
		
	}

	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		
	}

	public void onPageSelected(int position) {
        if (position >= mDays.size())
        	return;
        mTitleCurrentDayIndex = position;
        Day day = mDays.get(position);
        mTitle.setText(day.label);
        if ( position == 0)
        	movetoPreviousWeek();
        else if (position == mDays.size()-1)
        	movetoNextWeek();
	}
	

	@Override
	public void onClick(View view ) {
		if (view instanceof BlockView) {
            Block block = findBlock(((BlockView)view).getBlockId());
            if ( block == null )
                Toast.makeText(getActivity(),"Something stupid happened", Toast.LENGTH_SHORT).show();
            if ( getActivity() == null )
            	return;
            Intent i = new Intent(getActivity() , ClassDescriptionActivity.class);
            i.putExtra(ClassDescriptionFragment.BLOCK, block);
            startActivity(i);
		}
	}

	private Block findBlock( String blockId) {
		for ( Block block : schedule )
		{
			String id =  block.getLectureAcronym() + " (" + block.getLectureType() + ")" 
							+ "\n" +  block.getBuildingCode() + block.getRoomCode();
			if (id.equals(blockId) )
				return block;
		}
		return null;
	}
    
    
    /** Classe privada para a busca de dados ao servidor */
    private class ScheduleTask extends AsyncTask<Void, Void, String> {

    	protected void onPreExecute (){
    		if ( fetchingNextWeek || fetchingPreviousWeek )
    		{
    			getActivity().showDialog(BaseActivity.DIALOG_FETCHING);
    			return;
    		}
    		showLoadingScreen();
    		
    	}

        protected void onPostExecute(String result) {
        	if ( getActivity() == null )
        		return;
        	if ( result.equals("Success") || result.equals("") )
        	{        		
        		if  ( fetchingNextWeek || fetchingPreviousWeek )
        		{
        			updateDay(0, mondayMillis - 3 * DateUtils.DAY_IN_MILLIS ); // previous friday
        			for ( int i = 1; i < mDays.size() -1 ; ++i )
            			updateDay(i, mondayMillis +  (i -1 ) * DateUtils.DAY_IN_MILLIS );
        			updateDay( mDays.size() -1, mondayMillis + 7 * DateUtils.DAY_IN_MILLIS ); //next monday
        			cleanBlocks();
        		}
        		
        				
				Log.e("Schedule","success");
				for ( Block block : schedule)
					addBlock(block.getWeekDay(), block);
				mPager.setAdapter(new DayAdapter());
				if ( fetchingPreviousWeek )
				{
					mPager.setCurrentItem(mDays.size()-2);
				}
				else
					mPager.setCurrentItem(1);
				if  ( fetchingNextWeek || fetchingPreviousWeek )
				{
					getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
					fetchingNextWeek = false;
					fetchingPreviousWeek = false;
				}
				showMainScreen();
			}
			else if ( result.equals("Error") ){
				Log.e("Schedule","fail");
				if ( getActivity() != null ) 
				{
					Toast.makeText(getActivity(), getString(R.string.toast_auth_error), Toast.LENGTH_LONG).show();
					((BaseActivity)getActivity()).goLogin(true);
					return;
				}
			}
        }

		@Override
		protected String doInBackground(Void ...  code) {
			String page = "";
		  	
			try {
		  		Time monday = new Time(UIUtils.TIME_REFERENCE);
		  		monday.set(mondayMillis);
		  		monday.normalize(false);
		  		String firstDay = monday.format("%Y%m%d");
		  		//Friday 
		  		monday.set(mondayMillis + (4 * DateUtils.DAY_IN_MILLIS));
		  		monday.normalize(false);
		  		String lastDay = monday.format("%Y%m%d");
		  		switch ( scheduleType )
		  		{
			  		case SCHEDULE_STUDENT : 
			    		page = SifeupAPI.getScheduleReply(scheduleCode, firstDay,	lastDay);
			    		break;
			  		case SCHEDULE_EMPLOYEE : 
			    		page = SifeupAPI.getEmployeeScheduleReply(scheduleCode, firstDay,	lastDay);
			    		break;
			  		case SCHEDULE_ROOM : 
			    		page = SifeupAPI.getRoomScheduleReply(scheduleCode.substring(0, 1),scheduleCode.substring(1), firstDay,	lastDay);
			    		break;
			  		case SCHEDULE_UC : 
			    		page = SifeupAPI.getUcScheduleReply(scheduleCode, firstDay,	lastDay);
			    		break;
			  		default : page = null;	
		  		}

	    		
	    		int error =	SifeupAPI.JSONError(page);
	    		switch (error)
	    		{
		    		case SifeupAPI.Errors.NO_AUTH:
		    			return "Error";
		    		case SifeupAPI.Errors.NO_ERROR:
		    			JSONSchedule(page);
		    			return "Success";
		    		case SifeupAPI.Errors.NULL_PAGE:
		    			return "";
	    		}
				
			} catch (JSONException e) {
				Looper.prepare();
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

			return "";
		}

    }


}
