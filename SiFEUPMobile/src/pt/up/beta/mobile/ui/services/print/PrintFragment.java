package pt.up.beta.mobile.ui.services.print;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.PrintingQuota;
import pt.up.beta.mobile.loaders.PrintingQuotaLoader;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.beta.mobile.syncadapter.SigarraSyncAdapterUtils;
import pt.up.beta.mobile.ui.BaseLoaderFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.viewpagerindicator.TitlePageIndicator;

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
public class PrintFragment extends BaseLoaderFragment implements
		LoaderCallbacks<PrintingQuota[]> {

	private final static String PRINTERS_KEY = "pt.up.fe.mobile.ui.studentservices.PRINTING_QUOTA";

	PrintingQuota[] quota;

	private ViewPager viewPager;
	private TitlePageIndicator indicator;
	private LayoutInflater mInflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mInflater = inflater;
		View root = inflater.inflate(R.layout.fragment_view_pager,
				getParentContainer(), true);
		viewPager = (ViewPager) root.findViewById(R.id.pager_menu);
		viewPager.setAdapter(new PagerCourseAdapter());
		// Find the indicator from the layout
		indicator = (TitlePageIndicator) root.findViewById(R.id.indicator_menu);
		return getParentContainer();// mandatory
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			final Parcelable[] storedQuota = savedInstanceState
					.getParcelableArray(PRINTERS_KEY);
			if (storedQuota == null)
				getActivity().getSupportLoaderManager().initLoader(0, null,
						this);
			else {
				quota = new PrintingQuota[storedQuota.length];
				for ( int i = 0; i < storedQuota.length; ++i )
					quota[i] = (PrintingQuota) storedQuota[i];
				displayData();
				showMainScreen();
			}
		} else {
			getActivity().getSupportLoaderManager().initLoader(0, null, this);
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
	protected void onRepeat() {
		super.onRepeat();
		setRefreshActionItemState(true);
		SigarraSyncAdapterUtils.syncPrintingQuota(AccountUtils
				.getActiveUserName(getActivity()));

	}

	@Override
	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error),
					Toast.LENGTH_LONG).show();
			finish();
			break;
		case NETWORK:
			showRepeatTaskScreen(getString(R.string.toast_server_error));
			break;
		default:
			showEmptyScreen(getString(R.string.general_error));
			break;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (quota != null)
			outState.putParcelableArray(PRINTERS_KEY, quota);
	}

	private void displayData() {
		viewPager.setAdapter(new PagerCourseAdapter());
		indicator.setViewPager(viewPager);
	}

	@Override
	public Loader<PrintingQuota[]> onCreateLoader(int loaderId, Bundle options) {
		return new PrintingQuotaLoader(getActivity(),
				SigarraContract.PrintingQuota.CONTENT_URI,
				SigarraContract.PrintingQuota.COLUMNS,
				SigarraContract.PrintingQuota.PROFILE,
				SigarraContract.PrintingQuota
						.getPrintingQuotaSelectionArgs(AccountUtils
								.getActiveUserName(getActivity())), null);
	}

	@Override
	public void onLoadFinished(Loader<PrintingQuota[]> loader,
			PrintingQuota[] cursor) {
		if (getActivity() == null || cursor == null)
			return;
		quota = cursor;
		displayData();
		setRefreshActionItemState(false);
		showMainScreen();

	}

	@Override
	public void onLoaderReset(Loader<PrintingQuota[]> loader) {
	}

	class PagerCourseAdapter extends PagerAdapter {

		@Override
		public CharSequence getPageTitle(int position) {
			return quota[position].getLogin();
		}

		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((View) view);

		}

		public int getCount() {
			if (quota == null)
				return 0;
			return quota.length;
		}

		public Object instantiateItem(View collection, int position) {
			ViewGroup root = (ViewGroup) mInflater.inflate(
					R.layout.print_balance, viewPager, false);
			final TextView display = ((TextView) root
					.findViewById(R.id.print_balance));
			final TextView desc = ((TextView) root
					.findViewById(R.id.print_desc));
			final EditText value = (EditText) root
					.findViewById(R.id.print_value);

			display.setText(getString(R.string.print_balance,
					quota[position].getQuotaAsString()));
			long pagesA4Black = Math.round(quota[position].getQuota() / 0.03f);
			if (pagesA4Black > 0)
				desc.setText(getString(R.string.print_can_print_a4_black,
						Long.toString(pagesA4Black)));
			root.findViewById(R.id.print_ref_group).setVisibility(View.GONE);
			root.findViewById(R.id.print_generate_reference)
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							String newValue = value.getText().toString().trim();
							try {
								if (Double.valueOf(newValue) < 1.0) {
									Toast.makeText(
											getActivity(),
											getString(R.string.toast_invalid_value),
											Toast.LENGTH_LONG).show();
									value.requestFocus();
									return;
								}
							} catch (NumberFormatException e) {
								Toast.makeText(
										getActivity(),
										getString(R.string.toast_invalid_value),
										Toast.LENGTH_LONG).show();
								value.requestFocus();
								return;
							}
							newValue = newValue.replace(".", ",");
							Intent i = new Intent(getActivity(),
									PrintRefActivity.class);
							i.putExtra(PrintRefFragment.PRINT_REF_KEY, newValue);
							i.putExtra(PrintRefFragment.CODE_KEY, AccountUtils
									.getActiveUserCode(getActivity()));
							startActivity(i);
						}
					});
			((ViewPager) collection).addView(root, 0);
			return root;
		}

		public boolean isViewFromObject(View view, Object object) {
			return view == ((View) object);
		}

		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			indicator.setViewPager(viewPager);
		}

		public Parcelable saveState() {
			return null;
		}

		public void startUpdate(View arg0) {
		}

		public void finishUpdate(View arg0) {
		}

	}

}