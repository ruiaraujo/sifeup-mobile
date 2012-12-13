package pt.up.beta.mobile.ui.subjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.TeachingService;
import pt.up.beta.mobile.datatypes.TeachingService.Subject;
import pt.up.beta.mobile.loaders.TeachingServiceLoader;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.beta.mobile.syncadapter.SigarraSyncAdapterUtils;
import pt.up.beta.mobile.ui.BaseLoaderFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class TeachingServiceFragment extends BaseLoaderFragment implements
		OnItemClickListener, LoaderCallbacks<TeachingService> {

	/** Contains all subscribed subjects */
	private TeachingService teachingService;
	private ListView list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflateMainScreen(R.layout.generic_list);
		list = (ListView) root.findViewById(R.id.generic_list);
		return getParentContainer(); // this is mandatory.
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().getSupportLoaderManager().initLoader(0, null, this);
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.refresh_menu_items, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh) {
			setRefreshActionItemState(true);
			SigarraSyncAdapterUtils.syncTeachingService(AccountUtils
					.getActiveUserName(getActivity()));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onRepeat() {
		super.onRepeat();
		setRefreshActionItemState(true);
		SigarraSyncAdapterUtils.syncTeachingService(AccountUtils
				.getActiveUserName(getActivity()));
	}

	@Override
	public void onItemClick(AdapterView<?> list, View view, int position,
			long id) {
		Intent i = new Intent(getActivity(), SubjectDescriptionActivity.class);
		final Subject subject = teachingService.getService()[position];
		i.putExtra(SubjectDescriptionFragment.SUBJECT_CODE,
				subject.getOcorrId());
		i.putExtra(Intent.EXTRA_TITLE, subject.getUcurrName());
		startActivity(i);

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
	public Loader<TeachingService> onCreateLoader(int loaderId, Bundle options) {
		return new TeachingServiceLoader(getActivity(),
				SigarraContract.TeachingService.CONTENT_URI,
				SigarraContract.TeachingService.COLUMNS,
				SigarraContract.TeachingService.PROFILE,
				SigarraContract.TeachingService
						.getTeachingServiceSelectionArgs(AccountUtils
								.getActiveUserName(getActivity())), null);
	}

	@Override
	public void onLoadFinished(Loader<TeachingService> loader,
			TeachingService results) {
		if (getActivity() == null || results == null)
			return;
		teachingService = results;
		if (teachingService.getService().length == 0) {
			showEmptyScreen(getString(R.string.lb_no_teaching_service));
			return;
		}
		final String[] from = new String[] { "name", "code", "time" };
		final int[] to = new int[] { R.id.exam_chair, R.id.exam_time,
				R.id.exam_room };
		// prepare the list of all records
		final List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for (Subject s : teachingService.getService()) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(from[0], s.getUcurrName());
			map.put(from[1], s.getCourse());
			map.put(from[2],
					getString(R.string.subjects_year, s.getCurriculumYear(),
							s.getPeriodCode()));
			fillMaps.add(map);
		}
		// fill in the grid_item layout
		final SimpleAdapter adapter = new SimpleAdapter(getActivity(),
				fillMaps, R.layout.list_item_exam, from, to);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		setRefreshActionItemState(false);
		showMainScreen();
	}

	@Override
	public void onLoaderReset(Loader<TeachingService> loader) {
	}

}
