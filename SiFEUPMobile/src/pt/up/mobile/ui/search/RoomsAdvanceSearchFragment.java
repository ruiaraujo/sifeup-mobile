package pt.up.mobile.ui.search;

import pt.up.mobile.R;
import pt.up.mobile.ui.BaseFragment;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 
 * @author Angela Igreja
 * 
 */

/*
 * SUBJECT_CODE SUBJECT_NAME SUBJECT_ACRONYM SUBJECT_YEAR
 */
public class RoomsAdvanceSearchFragment extends BaseFragment implements
		OnClickListener {

	private EditText name;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflateMainScreen(R.layout.search_form);

		name = (EditText) root.findViewById(R.id.name);
		name.setHint(R.string.hint_room_name);
		final TextView nameLb = (TextView) root.findViewById(R.id.name_label);
		nameLb.setText(R.string.lb_room_name);
		root.findViewById(R.id.code_group).setVisibility(View.GONE);
		root.findViewById(R.id.year_group).setVisibility(View.GONE);
		root.findViewById(R.id.email_group).setVisibility(View.GONE);
		Button searchButton = (Button) root.findViewById(R.id.search);
		searchButton.setOnClickListener(this);
		showMainScreen();
		return getParentContainer();
	}

	@Override
	public void onClick(View v) {
		Fragment searchFrag = new RoomsSearchFragment();
		final Bundle args = new Bundle();
		args.putString(SearchManager.QUERY, name.getText().toString());
		searchFrag.setArguments(args);
		FragmentTransaction ft = getActivity().getSupportFragmentManager()
				.beginTransaction();
		ft.replace(R.id.root_container, searchFrag)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack(null).commit();
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
	}
}
