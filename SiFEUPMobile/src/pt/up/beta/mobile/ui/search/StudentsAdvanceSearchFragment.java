package pt.up.beta.mobile.ui.search;

import pt.up.mobile.R;
import pt.up.beta.mobile.ui.BaseFragment;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
 * @author Angela Igreja
 * 
 */
/*
 * CODE 
 * NAME
 * EMAIL 
 * FIRST YEAR
 */
public class StudentsAdvanceSearchFragment extends BaseFragment implements
		OnClickListener {

	private EditText name;
	private EditText code;
	private EditText email;
	private EditText firstYear;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflateMainScreen(R.layout.search_form);
		Button searchButton = (Button) root.findViewById(R.id.search);
		name = (EditText) root.findViewById(R.id.name);
		code = (EditText) root.findViewById(R.id.code);
		email = (EditText) root.findViewById(R.id.email);
		firstYear = (EditText) root.findViewById(R.id.first_year);
		searchButton.setOnClickListener(this);
		showMainScreen();
		return getParentContainer();
	}

	@Override
	public void onClick(View v) {

		final String nameStr = TextUtils.isEmpty(name.getText())?null:name.getText().toString();
		final String codeStr = TextUtils.isEmpty(code.getText())?null:code.getText().toString();
		final String emailStr = TextUtils.isEmpty(email.getText())?null:email.getText().toString();
		final String firstYearStr = TextUtils.isEmpty(firstYear.getText())?null:firstYear.getText().toString();
			
		Fragment searchFrag = new StudentsSearchFragment();
		final Bundle args = new Bundle();
		args.putStringArray(SearchManager.QUERY, new String[]{nameStr, codeStr, emailStr, firstYearStr});

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
