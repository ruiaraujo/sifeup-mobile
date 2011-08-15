package pt.up.fe.mobile.ui.studentarea;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;



import external.com.google.android.apps.iosched.ui.widget.BlockView;
import external.com.google.android.apps.iosched.ui.widget.BlocksLayout;
import external.com.google.android.apps.iosched.ui.widget.ObservableScrollView;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;
import external.com.google.android.apps.iosched.util.MotionEventUtils;
import external.com.google.android.apps.iosched.util.UIUtils;
import external.com.zylinc.view.ViewPagerIndicator;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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
import android.view.ViewParent;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

/**
 * This fragment is responsible of fetching a schedule of the student, 
 * which number is passed as argument. 
 *  Also it can export the schedule for Google Calendar.
 *
 * @author Ângela Igreja
 *
 */
public class ScheduleFragment extends BaseFragment implements
			ObservableScrollView.OnScrollListener, OnPageChangeListener {
    private ViewPager mPager;
    private TextView mTitle;
    private int mTitleCurrentDayIndex = -1;
    private View mLeftIndicator;
    private View mRightIndicator;
    private ArrayList<Block> schedule = new ArrayList<Block>();
    private List<Day> mDays = new ArrayList<Day>();
    private long mondayMillis;
    private String personCode;
    
    /**
     * The key for the student code in the intent.
     */
    final public static String PROFILE_CODE  = "pt.up.fe.mobile.ui.studentarea.PROFILE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/exams");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
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
                	/*if ( mTitleCurrentDayIndex >= 0 )
                		mWorkspace.scrollLeft();
                	else
                		movetoPreviousWeek();*/
                    return true;
                }
                return false;
            }
        });
        mLeftIndicator.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	decreaseDay();
            	/*if ( mTitleCurrentDayIndex >= 0 )
            		mWorkspace.scrollLeft();
            	else
            		movetoPreviousWeek();*/
            }
        });

        mRightIndicator = root.findViewById(R.id.indicator_right);
        mRightIndicator.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if ((motionEvent.getAction() & MotionEventUtils.ACTION_MASK)
                        == MotionEvent.ACTION_DOWN) {
                	increaseDay();
                	/*if ( mTitleCurrentDayIndex < mDays.size() )
                		mWorkspace.scrollRight();
                	else
                		movetoNextWeek();*/
                    return true;
                }
                return false;
            }
        });
        mRightIndicator.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	increaseDay();
            	/*if ( mTitleCurrentDayIndex >= mDays.size() )
            		mWorkspace.scrollRight();
            	else
            		movetoNextWeek();*/
            }
        });
        mondayMillis = firstDayofWeek(false);
        setupDay(inflater , mondayMillis);
  		setupDay(inflater , mondayMillis + (1 * DateUtils.DAY_IN_MILLIS));
  		setupDay(inflater , mondayMillis + (2 * DateUtils.DAY_IN_MILLIS));
  		setupDay(inflater , mondayMillis + (3 * DateUtils.DAY_IN_MILLIS));
  		setupDay(inflater , mondayMillis + (4 * DateUtils.DAY_IN_MILLIS));
        onPageSelected(0);

  		personCode = (String) getArguments().get(PROFILE_CODE);
		if ( personCode == null )
			personCode = SessionManager.getInstance().getLoginCode();
		new ScheduleTask().execute();


       
/*        mWorkspace.setOnScrollListener(new Workspace.OnScrollListener() {
            public void onScroll(float screenFraction) {
                updateWorkspaceHeader(Math.round(screenFraction));
            }
        }, true);*/

        return getParentContainer();
    }
    
    private void increaseDay(){
    	if ( mTitleCurrentDayIndex + 1 >= mDays.size() )
    		return ;
    	mTitleCurrentDayIndex++;
    	mPager.setCurrentItem(mTitleCurrentDayIndex);
    }
    private void decreaseDay(){
    	if ( mTitleCurrentDayIndex - 1 <= 0 )
    		return ;
    	mTitleCurrentDayIndex--;
    	mPager.setCurrentItem(mTitleCurrentDayIndex);
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
                Toast.makeText(getActivity(), R.string.toast_now_not_visible,
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (item.getItemId() == R.id.menu_export_calendar) {
        	// export to Calendar (create event)
    		calendarExport();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
   /* 
    private void movetoNextWeek(){
    	goingLeft = false;
    	mondayMillis += 7 * DateUtils.DAY_IN_MILLIS;
    	for ( int i = 0 ; i < mDays.size() ; ++i )
    		updateDay(i, mondayMillis + i * DateUtils.DAY_IN_MILLIS );
    	new ScheduleTask().execute();
    }
    
    private void movetoPreviousWeek(){
    	goingLeft = true;
    	mondayMillis -= 7 * DateUtils.DAY_IN_MILLIS;
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

    /** Classe privada para a busca de dados ao servidor */
    private class ScheduleTask extends AsyncTask<Void, Void, String> {

    	protected void onPreExecute (){
    		showLoadingScreen();
    	}

        protected void onPostExecute(String result) {
        	if ( result.equals("Success") || result.equals("") )
        	{
        		/*if ( goingLeft ){
					updateWorkspaceHeader(mDays.size()-1);
					for ( int i = 0 ; i < mDays.size() ; ++i )
						mWorkspace.scrollRight();
				}
				else
				{
					updateWorkspaceHeader(0);
					for ( int i = 0 ; i < mDays.size() ; ++i )
						mWorkspace.scrollLeft();
				}*/
        		cleanBlocks();
				Log.e("Schedule","success");
				for ( Block block : schedule)
					addBlock(block.weekDay, block);
				mPager.setAdapter(new DayAdapter());
				mPager.setCurrentItem(0);
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
		  		
	    		page = SifeupAPI.getScheduleReply(personCode, 
								firstDay, 
								lastDay);
	    		
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
    private void setupDay(LayoutInflater inflater, long startMillis) {
        Day day = new Day();

        // Setup data
        day.index = mDays.size();
        day.timeStart = startMillis;
        day.timeEnd = startMillis + DateUtils.DAY_IN_MILLIS;
        // Setup views
        day.rootView = (ViewGroup) inflater.inflate(R.layout.blocks_content, null);

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
        mDays.add(day);
    }
    
    private void updateDay( int index , long millis ){
    	
    	Time date = new Time(UIUtils.TIME_REFERENCE);
  		date.set(millis);
  		date.normalize(false);
        mDays.get(index).label =DateUtils.getDayOfWeekString(date.weekDay+1, DateUtils.LENGTH_LONG) +", " +
        			date.format("%d-%m");
    }
    
    private void cleanBlocks(){
    	for (int i = 0 ; i < mDays.size() ; ++i )
    		mDays.get(i).blocksView.removeAllBlocks();
    }

    /**
     * 
     * Represents a lecture.
     * Holds all data about it.
     * (time, place, teacher)
     *
     */
    private class Block implements Serializable{
    	private int weekDay; // [1 ... 6]
    	private int startTime; // seconds from midnight
    	
    	private String lectureCode; // EIC0036
    	private String lectureAcronym; // ex: SDIS
    	private String lectureType; // T|TP|P
    	private double lectureDuration; // 2; 1,5 (in hours)
    	private String classAcronym; // 3MIEIC1
    	
    	private String teacherAcronym; // RMA
    	private String teacherCode; // 466651
    	
    	private String roomCode; // 002
    	private String buildingCode; // B
    	private String semester; // 2S
    }
    
    /**
     * A helper class containing object references related to a particular day in the schedule.
     */
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
    			
    			if(jBlock.has("dia")) block.weekDay = jBlock.getInt("dia") - 2; // Monday is index 0
    			if(jBlock.has("hora_inicio")) block.startTime = jBlock.getInt("hora_inicio");
    			if(jBlock.has("cad_codigo")) block.lectureCode = jBlock.getString("cad_codigo");
    			if(jBlock.has("cad_sigla")) block.lectureAcronym = jBlock.getString("cad_sigla");
    			if(jBlock.has("tipo")) block.lectureType = jBlock.getString("tipo");
    			if(jBlock.has("aula_duracao")) block.lectureDuration = jBlock.getDouble("aula_duracao");
    			if(jBlock.has("turma_sigla")) block.classAcronym = jBlock.getString("turma_sigla");
    			if(jBlock.has("doc_sigla")) block.teacherAcronym = jBlock.getString("doc_sigla");
    			if(jBlock.has("doc_codigo")) block.teacherCode = jBlock.getString("doc_codigo");
    			if(jBlock.has("sala_cod")){
    				block.roomCode = jBlock.getString("sala_cod");
    				while ( block.roomCode.length() < 3 )
    					block.roomCode = "0" + block.roomCode;
    			}
    			
    			if(jBlock.has("edi_cod")) block.buildingCode = jBlock.getString("edi_cod");
    			if(jBlock.has("periodo")) block.semester = jBlock.getString("periodo");
    			
    			// add block to schedule
    			this.schedule.add(block);
    		}
    		Log.e("JSON", "loaded schedule");
    		return true;
    	}
    	Log.e("JSON", "schedule not found");
    	return false;
    }


    public void addBlock(int dayIndex ,Block block ) {
        if (getActivity() == null) {
            return;
        }

        Day day = mDays.get(dayIndex);

        final String blockId = block.lectureAcronym;
        final String title = block.lectureAcronym + " (" + block.lectureType + ")" 
        						+ "\n" +  block.buildingCode + block.roomCode;
        final long start = block.startTime * 1000 + day.timeStart;
        final long end = ( block.startTime + (long)(block.lectureDuration * 3600) ) * 1000;
        final boolean containsStarred = false;

        int column = 0;
        if ( block.lectureType.equals("T") )
        	column = 1;
		final BlockView blockView = new BlockView(getActivity(), blockId,title , start, end,
        		containsStarred , column );

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
    public boolean calendarExport(){
    	
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
                		event.put("title", b.lectureAcronym + " (" + b.lectureType + ")");
                		event.put("eventLocation", b.buildingCode+b.roomCode);
                		event.put("description", "Professor: " + b.teacherAcronym);
                		long date =  UIUtils.convertToUtc(mondayMillis) + 
                					b.weekDay * DateUtils.DAY_IN_MILLIS + 
                					b.startTime*1000;
                		event.put("dtstart", date );
                		event.put("dtend", date + b.lectureDuration*3600000 );
                		
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
		// TODO Auto-generated method stub
		
	}

	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		// TODO Auto-generated method stub
		
	}

	public void onPageSelected(int position) {
        if (position >= mDays.size())
        	return;
        mTitleCurrentDayIndex = position;
        Day day = mDays.get(position);
        mTitle.setText(day.label);
        		
	}
    
    
}
