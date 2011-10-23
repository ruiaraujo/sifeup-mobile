package pt.up.fe.mobile.ui.studentarea;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.Block;
import pt.up.fe.mobile.tracker.AnalyticsUtils;

import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.profile.ProfileActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * Class Description Fragment
 * 
 * @author Ângela Igreja
 * 
 */
public class ClassDescriptionFragment extends BaseFragment 
{  
	/**
     * The key for the student code in the intent.
     */
    final public static String BLOCK = "pt.up.fe.mobile.ui.studentarea.BLOCK";
    private Block block;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	    AnalyticsUtils.getInstance(getActivity()).trackPageView("/Class Description");
        Bundle args = getArguments();
        block = (Block) args.get(BLOCK);
	}

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) 
	{
		super.onCreateView(inflater, container, savedInstanceState);
		
		View root = inflater.inflate(R.layout.class_description, getParentContainer() , true);
		
		// Subject
		TextView subject = (TextView) root.findViewById(R.id.class_subject);
		
		subject.setText(getString(R.string.class_subject, block.getLectureAcronym()));
		subject.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ( getActivity() == null )
					return;
				Intent i = new Intent(getActivity() , SubjectDescriptionActivity.class);
				// assumed only one page of results
				i.putExtra(SubjectDescriptionFragment.SUBJECT_CODE, block.getLectureCode());
				i.putExtra(SubjectDescriptionFragment.SUBJECT_YEAR, block.getYear());
				i.putExtra(SubjectDescriptionFragment.SUBJECT_PERIOD, block.getSemester());
				i.putExtra(Intent.EXTRA_TITLE, block.getLectureAcronym());
				startActivity(i);

			}
		});
		
		// Teacher
		TextView teacher = (TextView) root.findViewById(R.id.class_teacher);
		
		teacher.setText(getString(R.string.class_teacher, block.getTeacherAcronym()));
		teacher.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity() , ProfileActivity.class);
				i.putExtra(ProfileActivity.PROFILE_CODE, block.getTeacherCode());
				i.putExtra(ProfileActivity.PROFILE_TYPE, ProfileActivity.PROFILE_EMPLOYEE);
				i.putExtra(Intent.EXTRA_TITLE, block.getTeacherAcronym());
				startActivity(i);
			}
		});
		
		// Room
		TextView room = (TextView) root.findViewById(R.id.class_room);
		room.setText(getString(R.string.class_room, block.getBuildingCode() + block.getRoomCode()));
		
		room.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity() , ScheduleActivity.class);
				i.putExtra(ScheduleFragment.SCHEDULE_TYPE,ScheduleFragment.SCHEDULE_ROOM) ;
				i.putExtra(ScheduleFragment.SCHEDULE_CODE, block.getBuildingCode() + block.getRoomCode()  );
	    		i.putExtra(Intent.EXTRA_TITLE , getString(R.string.title_schedule_arg,
	    				 		block.getBuildingCode() + block.getRoomCode()));

				startActivity(i);
			}
		});
		
		// Team
		TextView team = (TextView) root.findViewById(R.id.class_team);
		team.setText(getString(R.string.class_team, block.getClassAcronym()));
		
		// Start time
		int startTime = block.getStartTime();
		String start = Integer.toString(startTime/3600) + ":" + Integer.toString(startTime%3600);
		TextView startT = (TextView) root.findViewById(R.id.class_start_time);
		startT.setText(getString(R.string.class_start_time,start ));
		
		// End Time
		int endTime = (int) (block.getStartTime() + block.getLectureDuration()*3600);
		String end = Integer.toString(endTime/3600) + ":" + Integer.toString(endTime%3600);
		TextView endT = (TextView) root.findViewById(R.id.class_end_time);
		endT.setText(getString(R.string.class_end_time, end));
		
		showMainScreen();
		
		return getParentContainer();//mandatory
	}
}
