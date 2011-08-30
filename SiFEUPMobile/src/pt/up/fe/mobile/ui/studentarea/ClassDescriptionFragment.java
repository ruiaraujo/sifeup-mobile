package pt.up.fe.mobile.ui.studentarea;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.service.Block;

import pt.up.fe.mobile.ui.BaseFragment;
import external.com.google.android.apps.iosched.util.AnalyticsUtils;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

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
				Toast.makeText(getActivity(), "hello subject schedule", Toast.LENGTH_SHORT).show();
			}
		});
		
		// Teacher
		TextView teacher = (TextView) root.findViewById(R.id.class_teacher);
		
		teacher.setText(getString(R.string.class_teacher, block.getTeacherAcronym()));
		teacher.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "hello teacher profile", Toast.LENGTH_SHORT).show();
			}
		});
		
		// Room
		TextView room = (TextView) root.findViewById(R.id.class_room);
		room.setText(getString(R.string.class_room, block.getBuildingCode() + block.getRoomCode()));
		
		room.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "hello room schedule", Toast.LENGTH_SHORT).show();
			}
		});
		
		// Team
		TextView team = (TextView) root.findViewById(R.id.class_team);
		team.setText(getString(R.string.class_team, block.getClassAcronym()));
		
		// Start time
		//TODO: obter o start time correcto
		TextView startTime = (TextView) root.findViewById(R.id.class_start_time);
		startTime.setText(getString(R.string.class_start_time, block.getStartTime()));
		
		// Duration
		//TODO: colocar horas e minutos
		TextView duration = (TextView) root.findViewById(R.id.class_duration);
		duration.setText(getString(R.string.class_duration, block.getLectureDuration()));
		
		showMainScreen();
		
		return getParentContainer();//mandatory
	}
}
