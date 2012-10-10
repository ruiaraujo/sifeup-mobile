package pt.up.beta.mobile.ui.services;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.loaders.LoadersConstants;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.syncadapter.SigarraSyncAdapterUtils;
import pt.up.beta.mobile.ui.BaseLoadingFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * Esta interface está responsável por ir buscar a informação do saldo de
 * impressão ao servidor e mostra-la. Existe um campo para inserção de um valor
 * e um botão que inicia a actividade PrintRefActivity.
 * 
 * This interface is responsible for fetching the information the balance to the
 * print server and shows it. There is a field to insert a value and a button
 * that starts PrintRefActivity activity.
 * 
 * @author Ângela Igreja
 * 
 */
public class PrintFragment extends BaseLoadingFragment implements
		LoaderCallbacks<Cursor> {

	private final static String PRINTERS_KEY = "pt.up.fe.mobile.ui.studentservices.PRINTING_QUOTA";

	private Double saldo;
	private TextView display;
	private TextView desc;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.print_balance,
				getParentContainer(), true);
		display = ((TextView) root.findViewById(R.id.print_balance));
		desc = ((TextView) root.findViewById(R.id.print_desc));
		final EditText value = (EditText) root.findViewById(R.id.print_value);
		root.findViewById(R.id.print_generate_reference).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						String newValue = value.getText().toString().trim();
						try {
							Double.valueOf(newValue);
						} catch (NumberFormatException e) {
							Toast.makeText(getActivity(),
									getString(R.string.toast_auth_error),
									Toast.LENGTH_LONG).show();
							value.requestFocus();
							return;
						}
						newValue = newValue.replace(".", ",");
						Intent i = new Intent(getActivity(),
								PrintRefActivity.class);
						i.putExtra("value", newValue);
						startActivity(i);
					}
				});
		return getParentContainer(); // mandatory

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			saldo = savedInstanceState.getDouble(PRINTERS_KEY);
			if (saldo == null)
				getActivity().getSupportLoaderManager().initLoader(
						LoadersConstants.PRINTING, null, this);
			else {
				displayData();
				showMainScreen();
			}
		} else {
			getActivity().getSupportLoaderManager().initLoader(
					LoadersConstants.PRINTING, null, this);
		}
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.refresh_menu_items, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh) {
			setRefreshActionItemState(true);
			SigarraSyncAdapterUtils.syncPrintingQuota(AccountUtils
					.getActiveUserName(getActivity()));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (saldo != null)
			outState.putDouble(PRINTERS_KEY, saldo);
	}

	private void displayData() {
		display.setText(getString(R.string.print_balance, saldo));
		long pagesA4Black = Math.round(saldo / 0.03f);
		if (pagesA4Black > 0)
			desc.setText(getString(R.string.print_can_print_a4_black,
					Long.toString(pagesA4Black)));
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle options) {
		return new CursorLoader(getActivity(),
				SigarraContract.PrintingQuota.CONTENT_URI,
				SigarraContract.PrintingQuota.COLUMNS,
				SigarraContract.PrintingQuota.PROFILE,
				SigarraContract.PrintingQuota
						.getPrintingQuotaSelectionArgs(AccountUtils
								.getActiveUserName(getActivity())), null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor.moveToFirst()) {
			saldo = cursor.getDouble(0);
			displayData();
			setRefreshActionItemState(false);
			showMainScreen();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

}