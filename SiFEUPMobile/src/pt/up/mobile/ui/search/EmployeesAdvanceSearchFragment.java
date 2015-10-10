package pt.up.mobile.ui.search;

import pt.up.mobile.R;
import pt.up.mobile.ui.BaseFragment;
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
import android.widget.TextView;

/**
 * 
 * @author Angela Igreja
 * 
 */

/*
 * EMPLOYEE_CODE EMPLOYEE_NAME EMPLOYEE_EMAIL EMPLOYEE_ACRONYM
 */
public class EmployeesAdvanceSearchFragment extends BaseFragment implements
		OnClickListener {

	private EditText name;
	private EditText code;
	private EditText email;
	private EditText acronym;
	private TextView acronymLabel;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflateMainScreen(R.layout.search_form);
		Button searchButton = (Button) root.findViewById(R.id.search);
		name = (EditText) root.findViewById(R.id.name);
		code = (EditText) root.findViewById(R.id.code);
		email = (EditText) root.findViewById(R.id.email);
		acronymLabel = (TextView) root.findViewById(R.id.first_year_label);
		acronymLabel.setText(R.string.lb_employee_acronym);
		acronym = (EditText) root.findViewById(R.id.first_year);
		acronym.setHint(R.string.hint_employee_acronym);

		searchButton.setOnClickListener(this);
		showMainScreen();
		return getParentContainer();
	}

	@Override
	public void onClick(View v) {

		final String nameStr = TextUtils.isEmpty(name.getText()) ? null : name
				.getText().toString();
		final String codeStr = TextUtils.isEmpty(code.getText()) ? null : code
				.getText().toString();
		final String emailStr = TextUtils.isEmpty(email.getText()) ? null
				: email.getText().toString();
		final String acronymStr = TextUtils.isEmpty(acronym.getText()) ? null
				: acronym.getText().toString();

		Fragment searchFrag = new EmployeesSearchFragment();
		final Bundle args = new Bundle();
		args.putStringArray(SearchManager.QUERY, new String[] { nameStr,
				codeStr, emailStr, acronymStr });

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
