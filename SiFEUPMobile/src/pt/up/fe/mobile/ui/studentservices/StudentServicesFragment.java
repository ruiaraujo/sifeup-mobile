package pt.up.fe.mobile.ui.studentservices;


import pt.up.fe.mobile.R;
import pt.up.fe.mobile.ui.tuition.TuitionHistoryActivity;
import pt.up.fe.mobile.ui.tuition.TuitionRefListActivity;
import pt.up.fe.mobile.ui.webclient.WebviewActivity;
import pt.up.fe.mobile.ui.webclient.WebviewFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

public class StudentServicesFragment extends Fragment
{
	private ExpandableListView menu;
	private LayoutInflater mInflater;
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
    }
	  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		mInflater = inflater;
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
		                 getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
			    		 break;
			    	case 1:
			    		// has children;
				   		 break;
			    	case 2:
				   		 startActivity(new Intent(getActivity(),ChangePasswordActivity.class));
		                 getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
				   		 break;
			    	case 3:
				   		 startActivity(new Intent(getActivity(),WebviewActivity.class)
				   		                     .putExtra(WebviewFragment.URL_INTENT, "https://www.fe.up.pt/si/mail_dinamico.ficheiros"));
		                 getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
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
		                 getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
				   		 break;
					case 1:
				   		 startActivity(new Intent(getActivity(), TuitionRefListActivity.class));
		                 getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
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
        		getString(R.string.btn_change_password),
        		getString(R.string.btn_dynamic_mail_files)};
        private String[][] children = {
                {  },
                { getString(R.string.btn_tuition_history)  , getString(R.string.btn_tuition_refs) },
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
        
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
        	ViewGroup root = (ViewGroup) mInflater.inflate(R.layout.list_item_menu, null) ;
            TextView textView= ((TextView)root.findViewById(R.id.list_menu_title));
          //Insert a extra padding to the children
            textView.setPadding( textView.getPaddingLeft() + 40 , 
			            		textView.getPaddingTop(),
			            		textView.getPaddingRight(),
			            		textView.getPaddingBottom());
            textView.setText(getChild(groupPosition, childPosition).toString());
            return root;            
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
        	ViewGroup root = (ViewGroup) mInflater.inflate(R.layout.list_item_menu, null) ;
            TextView textView= ((TextView)root.findViewById(R.id.list_menu_title));
            textView.setText(getGroup(groupPosition).toString());
            return root;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }

    }

}
