package pt.up.beta.mobile.ui.services;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.ui.BaseActivity;
import pt.up.beta.mobile.ui.search.AdvanceSearchActivity;
import pt.up.beta.mobile.ui.services.current_account.CurrentAccountActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

public class ServicesFragment extends Fragment {
	private ExpandableListView menu;
	private LayoutInflater mInflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		ViewGroup root = (ViewGroup) inflater.inflate(
				R.layout.fragment_services, null);
		menu = (ExpandableListView) root
				.findViewById(R.id.studentservices_menu);
		menu.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				final BaseActivity activity = (BaseActivity) getActivity();
				switch (groupPosition) {
				case 0:
					activity.openActivityOrFragment(new Intent(getActivity(),
							PrintActivity.class));
					break;
				case 1:
					activity.openActivityOrFragment(new Intent(getActivity(),
							CurrentAccountActivity.class));
					break;
				case 2:
					activity.openActivityOrFragment(new Intent(getActivity(),
							ChangePasswordActivity.class));
					break;
				case 3:
					activity.openActivityOrFragment(new Intent(getActivity(),
							DynamicMailFilesActivity.class));
					break;
				}
				return false;
			}
		});
		menu.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				final BaseActivity activity = (BaseActivity) getActivity();
				switch (groupPosition) {
				case 4:
					switch (childPosition) {
					case 0:
						activity.openActivityOrFragment(new Intent(
								getActivity(), AdvanceSearchActivity.class)
								.putExtra(AdvanceSearchActivity.SEARCH_TYPE,
										AdvanceSearchActivity.STUDENT)
								.putExtra(
										Intent.EXTRA_TITLE,
										getString(R.string.title_advanced_search_students)));
						break;
					case 1:
						activity.openActivityOrFragment(new Intent(
								getActivity(), AdvanceSearchActivity.class)
								.putExtra(AdvanceSearchActivity.SEARCH_TYPE,
										AdvanceSearchActivity.EMPLOYEE)
								.putExtra(
										Intent.EXTRA_TITLE,
										getString(R.string.title_advanced_search_employees)));
						break;
					case 2:
						activity.openActivityOrFragment(new Intent(
								getActivity(), AdvanceSearchActivity.class)
								.putExtra(AdvanceSearchActivity.SEARCH_TYPE,
										AdvanceSearchActivity.ROOM)
								.putExtra(
										Intent.EXTRA_TITLE,
										getString(R.string.title_advanced_search_rooms)));
						break;
					case 3:
						activity.openActivityOrFragment(new Intent(
								getActivity(), AdvanceSearchActivity.class)
								.putExtra(AdvanceSearchActivity.SEARCH_TYPE,
										AdvanceSearchActivity.SUBJECTS)
								.putExtra(
										Intent.EXTRA_TITLE,
										getString(R.string.title_advanced_search_subjects)));
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
		// Sample data set. children[i] contains the children (String[]) for
		// groups[i].
		private String[] groups = { getString(R.string.btn_printing),
				getString(R.string.btn_current_account),
				getString(R.string.btn_change_password),
				getString(R.string.btn_dynamic_mail_files),
				getString(R.string.title_advanced_search) };
		private String[][] children = {
				{},
				{},
				{},
				{},
				{ getString(R.string.title_advanced_search_students),
						getString(R.string.title_advanced_search_employees),
						getString(R.string.title_advanced_search_rooms),
						getString(R.string.title_advanced_search_subjects) } };

		public Object getChild(int groupPosition, int childPosition) {
			return children[groupPosition][childPosition];
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			return children[groupPosition].length;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ViewGroup root = (ViewGroup) mInflater.inflate(
					R.layout.list_item_menu, null);
			TextView textView = ((TextView) root
					.findViewById(R.id.list_menu_title));
			// Insert a extra padding to the children
			textView.setPadding(textView.getPaddingLeft() + 40,
					textView.getPaddingTop(), textView.getPaddingRight(),
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

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			ViewGroup root = (ViewGroup) mInflater.inflate(
					R.layout.list_item_menu, null);
			TextView textView = ((TextView) root
					.findViewById(R.id.list_menu_title));
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
