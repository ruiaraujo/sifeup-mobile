package pt.up.beta.mobile.ui.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import pt.up.beta.mobile.datatypes.Profile;
import pt.up.beta.mobile.datatypes.Student;
import pt.up.beta.mobile.datatypes.Profile.ProfileDetail;
import pt.up.beta.mobile.friends.Friend;
import pt.up.beta.mobile.sifeup.ProfileUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.sifeup.SessionManager;
import pt.up.beta.mobile.tracker.AnalyticsUtils;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.ui.personalarea.ScheduleActivity;
import pt.up.beta.mobile.ui.personalarea.ScheduleFragment;
import pt.up.beta.mobile.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Student Profile Fragment This interface is responsible for fetching the
 * student's profile information to the server and shows it. Have one argument
 * that is the number of student.
 * 
 * @author Ângela Igreja
 */
public class StudentProfileFragment extends BaseFragment implements
		OnItemClickListener, ResponseCommand {
	private TextView name;
	private ImageView pic;
	private ListView details;
	private CheckBox friend;

	/** User Info */
	private Student me;
	private List<ProfileDetail> contents;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		AnalyticsUtils.getInstance(getActivity()).trackPageView(
				"/StudentProfile");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.profile,
				getParentContainer(), true);
		name = (TextView) root.findViewById(R.id.profile_name);
		pic = (ImageView) root.findViewById(R.id.profile_pic);
		details = (ListView) root.findViewById(R.id.profile_details);
		friend = (CheckBox) root.findViewById(R.id.profile_star_friend);
		friend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Friend fr = new Friend(me.getCode(), me.getName(), me
						.getProgrammeAcronym());
				if (friend.isChecked())
					SessionManager.getInstance(getActivity()).addFriend(fr);
				else
					SessionManager.getInstance(getActivity()).removeFriend(fr);
			}
		});
		((Button) root.findViewById(R.id.profile_link_schedule))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent i = new Intent(getActivity(),
								ScheduleActivity.class);
						i
								.putExtra(ScheduleFragment.SCHEDULE_CODE, me
										.getCode());
						i.putExtra(ScheduleFragment.SCHEDULE_TYPE,
								ScheduleFragment.SCHEDULE_STUDENT);
						startActivity(i);
					}
				});

		return getParentContainer();
	}
	
	public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        String code = getArguments().getString(ProfileActivity.PROFILE_CODE);
        if ( code == null )
            code = SessionManager.getInstance(getActivity()).getLoginCode();
        // You can't friend yourself
        if (code.equals(SessionManager.getInstance(getActivity()).getLoginCode()))
            friend.setVisibility(View.INVISIBLE);
        task = ProfileUtils.getStudentReply(code, this);
    }

	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error),
					Toast.LENGTH_LONG).show();
			goLogin();
			break;
		case NETWORK:
			Toast.makeText(getActivity(),
					getString(R.string.toast_server_error), Toast.LENGTH_LONG)
					.show();
		default:
			// TODO: general error
			break;
		}
        getActivity().finish();
	}

	public void onResultReceived(Object... results) {
		if (getActivity() == null)
			return;
		if ( me != null ){
		    pic.setImageBitmap((Bitmap) results[0]);
		    return;
		}
		me = (Student) results[0];
        task = ProfileUtils.getPersonPic(me.getCode(), this);
		contents = me.getProfileContents(getResources());
		getActivity().getSupportActionBar().setTitle(me.getName());
		name.setText(me.getName());
		if (SessionManager.getInstance(getActivity()).isFriend(me.getCode()))
			friend.setChecked(true);
		else
			friend.setChecked(false);
		String[] from = new String[] { "title", "content" };
		int[] to = new int[] { R.id.profile_item_title,
				R.id.profile_item_content };
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for (ProfileDetail s : contents) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(from[0], s.title);
			map.put(from[1], s.content);
			fillMaps.add(map);
		}

		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps,
				R.layout.list_item_profile, from, to);
		details.setAdapter(adapter);
		details.setOnItemClickListener(StudentProfileFragment.this);
		details.setSelection(0);
		showMainScreen();
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View arg1, int position,
			long id) {
		if (contents.get(position).type == Profile.Type.WEBPAGE) {
			final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
					.parse(contents.get(position).content));
			startActivity(browserIntent);
		} else if (contents.get(position).type == Profile.Type.ROOM) {
			final Intent i = new Intent(getActivity(), ScheduleActivity.class);
			i.putExtra(ScheduleFragment.SCHEDULE_TYPE,
					ScheduleFragment.SCHEDULE_ROOM);
			i.putExtra(ScheduleFragment.SCHEDULE_CODE,
					contents.get(position).content);
			i.putExtra(Intent.EXTRA_TITLE,
					getString(R.string.title_schedule_arg, contents
							.get(position).content));
			startActivity(i);
		} else if (contents.get(position).type == Profile.Type.EMAIL) {
			final Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL, new String[] { contents
					.get(position).content });
			startActivity(Intent.createChooser(i,
					getString(R.string.profile_choose_email_app)));
		} else if (contents.get(position).type == Profile.Type.MOBILE) {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:"
					+ contents.get(position).content));
			startActivity(callIntent);
		}
	}
}
