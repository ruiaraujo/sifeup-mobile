package pt.up.fe.mobile.ui;

import java.util.ArrayList;
import java.util.jar.JarFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.SessionManager;
import pt.up.fe.mobile.service.SifeupAPI;


import com.google.android.apps.iosched.provider.ScheduleContract;
import com.google.android.apps.iosched.ui.widget.BlockView;
import com.google.android.apps.iosched.ui.widget.ObservableScrollView;
import com.google.android.apps.iosched.ui.widget.Workspace;
import com.google.android.apps.iosched.util.AnalyticsUtils;
import com.google.android.apps.iosched.util.MotionEventUtils;
import com.google.android.apps.iosched.util.UIUtils;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleFragment extends Fragment implements
			ObservableScrollView.OnScrollListener,
			View.OnClickListener{
	
    private Workspace mWorkspace;
    private TextView mTitle;
    private int mTitleCurrentDayIndex = -1;
    private View mLeftIndicator;
    private View mRightIndicator;
    private ArrayList<Block> schedule = new ArrayList<Block>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsUtils.getInstance(getActivity()).trackPageView("/exams");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_schedule, null);

		  mWorkspace = (Workspace) root.findViewById(R.id.workspace);

	        mTitle = (TextView) root.findViewById(R.id.block_title);

	        mLeftIndicator = root.findViewById(R.id.indicator_left);
	        mLeftIndicator.setOnTouchListener(new View.OnTouchListener() {
	            public boolean onTouch(View view, MotionEvent motionEvent) {
	                if ((motionEvent.getAction() & MotionEventUtils.ACTION_MASK)
	                        == MotionEvent.ACTION_DOWN) {
	                    mWorkspace.scrollLeft();
	                    return true;
	                }
	                return false;
	            }
	        });
	        mLeftIndicator.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	                mWorkspace.scrollLeft();
	            }
	        });

	        mRightIndicator = root.findViewById(R.id.indicator_right);
	        mRightIndicator.setOnTouchListener(new View.OnTouchListener() {
	            public boolean onTouch(View view, MotionEvent motionEvent) {
	                if ((motionEvent.getAction() & MotionEventUtils.ACTION_MASK)
	                        == MotionEvent.ACTION_DOWN) {
	                    mWorkspace.scrollRight();
	                    return true;
	                }
	                return false;
	            }
	        });
	        mRightIndicator.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	                mWorkspace.scrollRight();
	            }
	        });
		new ScheduleTask().execute();

        updateWorkspaceHeader(0);
        mWorkspace.setOnScrollListener(new Workspace.OnScrollListener() {
            public void onScroll(float screenFraction) {
                updateWorkspaceHeader(Math.round(screenFraction));
            }
        }, true);

        return root;
    }
    
    public void updateWorkspaceHeader(int dayIndex) {
        if (mTitleCurrentDayIndex == dayIndex) {
            return;
        }

        mTitleCurrentDayIndex = dayIndex;
        //Day day = mDays.get(dayIndex);
        //mTitle.setText(day.label);

        /*mLeftIndicator
                .setVisibility((dayIndex != 0) ? View.VISIBLE : View.INVISIBLE);
        mRightIndicator
                .setVisibility((dayIndex < mDays.size() - 1) ? View.VISIBLE : View.INVISIBLE);*/
    }

    

    public void onScrollChanged(ObservableScrollView view) {
        // Keep each day view at the same vertical scroll offset.
        final int scrollY = view.getScrollY();
      /*  for (Day day : mDays) {
            if (day.scrollView != view) {
                day.scrollView.scrollTo(0, scrollY);
            }
        }*/
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.schedule_menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    

    /** {@inheritDoc} */
    public void onClick(View view) {
        if (view instanceof BlockView) {
            String title = ((BlockView)view).getText().toString();
            AnalyticsUtils.getInstance(getActivity()).trackEvent(
                    "Schedule", "Session Click", title, 0);
            final String blockId = ((BlockView) view).getBlockId();
            final Uri sessionsUri = ScheduleContract.Blocks.buildSessionsUri(blockId);

            final Intent intent = new Intent(Intent.ACTION_VIEW, sessionsUri);
            intent.putExtra(SessionsFragment.EXTRA_SCHEDULE_TIME_STRING,
                    ((BlockView) view).getBlockTimeString());
            ((BaseActivity) getActivity()).openActivityOrFragment(intent);
        }
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
        return super.onOptionsItemSelected(item);
    }
    

    /**
     * Update position and visibility of "now" view.
     */
    private boolean updateNowView(boolean forceScroll) {
        final long now = UIUtils.getCurrentTime(getActivity());

        /*Day nowDay = null; // effectively Day corresponding to today
        for (Day day : mDays) {
            if (now >= day.timeStart && now <= day.timeEnd) {
                nowDay = day;
                day.nowView.setVisibility(View.VISIBLE);
            } else {
                day.nowView.setVisibility(View.GONE);
            }
        }

        if (nowDay != null && forceScroll) {
            // Scroll to show "now" in center
            mWorkspace.setCurrentScreen(nowDay.index);
            final int offset = nowDay.scrollView.getHeight() / 2;
            nowDay.nowView.requestRectangleOnScreen(new Rect(0, offset, 0, offset), true);
            nowDay.blocksView.requestLayout();
            return true;
        }
*/
        return false;
    }

    /** Classe privada para a busca de dados ao servidor */
    private class ScheduleTask extends AsyncTask<Void, Void, String> {

    	protected void onPreExecute (){
    		if ( getActivity() != null ) 
    			getActivity().showDialog(ScheduleActivity.DIALOG_FETCHING);  
    	}

        protected void onPostExecute(String result) {
        	if ( !result.equals("") )
        	{
				Log.e("Schedule","success");
			}
			else{	
				if ( getActivity() != null ) 
				{
					getActivity().removeDialog(ScheduleActivity.DIALOG_FETCHING);
					startActivity(new Intent(getActivity(), LoginActivity.class));
					getActivity().finish();
					return;
				}
			}
        	if ( getActivity() != null ) 
        		getActivity().removeDialog(ScheduleActivity.DIALOG_FETCHING);
        }

		@Override
		protected String doInBackground(Void ... theVoid) {
			String page = "";
		  	try {
		  		long yourDateMillis = System.currentTimeMillis();
		  		Time yourDate = new Time();
		  		yourDate.set(yourDateMillis);
		  		yourDate.normalize(false);
		  		int weekDay = yourDate.weekDay -1;
		  		//Our week starts at Monday
		  		if ( weekDay < 0 )
		  			weekDay = 6;
		  		yourDateMillis -= (weekDay * 24 * 60 * 60 * 1000);
		  		yourDate.set(yourDateMillis);
		  		yourDate.normalize(false);
		  		String firstDay = yourDate.format("%Y%m%d");
		  		//Friday
		  		yourDate.set(yourDateMillis + (4 * 24 * 60 * 60 * 1000));
		  		yourDate.normalize(false);
		  		String lastDay = yourDate.format("%Y%m%d");
	    		page = SifeupAPI.getScheduleReply(
								SessionManager.getInstance().getLoginCode(), 
								firstDay, 
								lastDay);
		  		JSONSchedule(page);
	    		if(SifeupAPI.JSONError(page))
	    			return "F***";
				else
					return page;
				
			} catch (JSONException e) {
				if ( getActivity() != null ) 
					Toast.makeText(getActivity(), "F*** JSON", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

			return "";
		}
    }
    
    /**
     * 
     * Represents a lecture.
     * Holds all data about it.
     * (time, place, teacher)
     *
     */
    private class Block{
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
    			
    			if(jBlock.has("dia")) block.weekDay = jBlock.getInt("dia");
    			if(jBlock.has("hora_inicio")) block.startTime = jBlock.getInt("hora_inicio");
    			if(jBlock.has("cad_codigo")) block.lectureCode = jBlock.getString("cad_codigo");
    			if(jBlock.has("cad_sigla")) block.lectureAcronym = jBlock.getString("cad_sigla");
    			if(jBlock.has("tipo")) block.lectureType = jBlock.getString("tipo");
    			if(jBlock.has("aula_duracao")) block.lectureDuration = jBlock.getDouble("aula_duracao");
    			if(jBlock.has("turma_sigla")) block.classAcronym = jBlock.getString("turma_sigla");
    			if(jBlock.has("doc_sigla")) block.teacherAcronym = jBlock.getString("doc_sigla");
    			if(jBlock.has("doc_codigo")) block.teacherCode = jBlock.getString("doc_codigo");
    			if(jBlock.has("sala_cod")) block.roomCode = jBlock.getString("sala_cod");
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
    
}
