package pt.up.fe.mobile.ui.studentservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import pt.up.fe.mobile.R;
import pt.up.fe.mobile.ui.studentarea.AcademicPathActivity;
import pt.up.fe.mobile.ui.studentarea.ExamsActivity;
import pt.up.fe.mobile.ui.studentarea.ScheduleActivity;
import pt.up.fe.mobile.ui.studentarea.SubjectsActivity;
import pt.up.fe.mobile.ui.tuition.TuitionActivity;
import pt.up.fe.mobile.ui.tuition.TuitionMenuActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class StudentServicesFragment extends Fragment
{
	private ExpandableListView menu;
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
    }
	  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	ViewGroup root = (ViewGroup) inflater.inflate(R.layout.studentservices, null);
    	menu = (ExpandableListView) root.findViewById(R.id.studentservices_menu);
    	menu.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				switch( groupPosition )
		    	{
			    	case 0:
			    		 startActivity(new Intent(getActivity(),PrintActivity.class));
			    		 break;
			    	case 1:
				   		 startActivity(new Intent(getActivity(),TuitionMenuActivity.class));
				   		 break;
			    	case 2:
				   		 startActivity(new Intent(getActivity(),UCsInscriptionsActivity.class));
				   		 break;
			    	case 3:
				   		 startActivity(new Intent(getActivity(),ChangePasswordActivity.class));
				   		 break;
			    	case 4:
				   		 startActivity(new Intent(getActivity(),CardRequestActivity.class));
				   		 break;
			    	case 5:
			    		// has children;
				   		 break;
			    	case 6:
				   		 startActivity(new Intent(getActivity(), ScholarProfitActivity.class));
				   		 break;
		    	}
				return false;
			}
		});
    	menu.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				switch( groupPosition )
		    	{
					case 5:
						switch( childPosition )
						{
							case 0:
						   		 startActivity(new Intent(getActivity(), NewRequestActivity.class));
						   		 break;
							case 1:
						   		 startActivity(new Intent(getActivity(), AllRequestsActivity.class));
						   		 break;	
						}
						break;
		    	}
				
				return true;
			}
		});
    	menu.setAdapter(new StudentServicesAdapter());
    	return root;

    }
    
 
    private class StudentServicesAdapter extends BaseExpandableListAdapter {
        // Sample data set.  children[i] contains the children (String[]) for groups[i].
        private String[] groups = { 
        		getString(R.string.btn_printing), 
        		getString(R.string.btn_tuition),
        		getString(R.string.btn_uc_inscriptions),
        		getString(R.string.btn_change_password),
        		getString(R.string.btn_card_request),
        		getString(R.string.btn_requests),
        		getString(R.string.btn_scholar_profit)};
        private String[][] children = {
                {  },
                {  },
                {  },
                {  },
                {  },
                {getString(R.string.btn_new_request), getString(R.string.btn_all_request) },
                {  },
        };
        
         
        
        public Object getChild(int groupPosition, int childPosition) {
            return children[groupPosition][childPosition];
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return children[groupPosition].length;
        }
        //TODO: move to xml
        public TextView getGenericView() {
            // Layout parameters for the ExpandableListView
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 64);

            TextView textView = new TextView(getActivity());
            textView.setLayoutParams(lp);
            // Center the text vertically
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            textView.setPadding(64, 0, 0, 0);
            
            return textView;
        }
        
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
            TextView textView = getGenericView();
            textView.setText(getChild(groupPosition, childPosition).toString());
            return textView;
        }

        public Object getGroup(int groupPosition) {
            return groups[groupPosition];
        }

        public int getGroupCount() {
            return groups.length;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
            TextView textView = getGenericView();
            textView.setText(getGroup(groupPosition).toString());
            return textView;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }

    }

}
