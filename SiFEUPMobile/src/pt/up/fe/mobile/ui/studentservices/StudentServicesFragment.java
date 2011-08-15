package pt.up.fe.mobile.ui.studentservices;


import pt.up.fe.mobile.R;
import pt.up.fe.mobile.ui.tuition.TuitionHistoryActivity;
import pt.up.fe.mobile.ui.tuition.TuitionRefListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
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
			    		// has children;
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
			    	case 7:
			    		 startActivity(new Intent(getActivity(),ClassesSelectionActivity.class));
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
				case 1:
					switch( childPosition )
					{
					case 0:
				   		 startActivity(new Intent(getActivity(), TuitionHistoryActivity.class));
				   		 break;
					case 1:
				   		 startActivity(new Intent(getActivity(), TuitionRefListActivity.class));
				   		 break;	
					}
					break;
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
        		getString(R.string.btn_scholar_profit),
        		getString(R.string.btn_classes_selection)};
        private String[][] children = {
                {  },
                { getString(R.string.btn_tuition_history)  , getString(R.string.btn_tuition_refs) },
                {  },
                {  },
                {  },
                {getString(R.string.btn_new_request), getString(R.string.btn_all_request) },
                {  },
                {  }
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
                  ViewGroup.LayoutParams.MATCH_PARENT, 84);

            TextView textView = new TextView(getActivity());
            textView.setLayoutParams(lp);
            // Center the text vertically
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            textView.setPadding(84, 0, 0, 0);
            
            return textView;
        }
        
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
            TextView textView = getGenericView();
            //Insert a extra padding to the children
            textView.setPadding( textView.getPaddingLeft() + 32 , 
			            		textView.getPaddingTop(),
			            		textView.getPaddingRight(),
			            		textView.getPaddingBottom());
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
