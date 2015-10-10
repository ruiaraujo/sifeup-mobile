package pt.up.beta.mobile.ui.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.mobile.R;
import pt.up.beta.mobile.datatypes.DynamicMailFile;
import pt.up.beta.mobile.downloader.DownloaderService;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.DynamicEmailUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.ui.BaseFragment;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class DynamicMailFilesFragment extends BaseFragment implements
		ResponseCommand<DynamicMailFile[]>, OnItemClickListener {

	private final static String FILES_KEY = "pt.up.fe.mobile.ui.studentarea.FILES";
	/** Stores all exams from Student */
	private DynamicMailFile[] files;
	final public static String PROFILE_CODE = "pt.up.fe.mobile.ui.studentarea.PROFILE";
	private ListView list;
	private String personCode;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflateMainScreen(R.layout.generic_list);
		list = (ListView) root.findViewById(R.id.generic_list);
		return getParentContainer(); // this is mandatory.
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		personCode = getArguments().getString(PROFILE_CODE);
		if (personCode == null)
			personCode = AccountUtils.getActiveUserCode(getActivity());
		if (savedInstanceState != null) {
			final Parcelable[] storedFiles = savedInstanceState
					.getParcelableArray(FILES_KEY);
			if (storedFiles == null) {
				task = DynamicEmailUtils.getDynamicEmailFiles(personCode, this,
						getActivity());
			} else {
				files = new DynamicMailFile[storedFiles.length];
				for (int i = 0; i < storedFiles.length; ++i)
					files[i] = (DynamicMailFile) storedFiles[i];
				if (populateList())
					showMainScreen();
			}
		} else
			task = DynamicEmailUtils.getDynamicEmailFiles(personCode, this,
					getActivity());

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (files != null)
			outState.putParcelableArray(FILES_KEY, files);
	}

	private boolean populateList() {
		if (getActivity() == null)
			return false;
		if (files.length == 0) {
			showEmptyScreen(getString(R.string.label_no_files));
			return false;
		}

		String[] from = new String[] { "chair", "time", "room" };
		int[] to = new int[] { R.id.exam_chair, R.id.exam_time, R.id.exam_room };
		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for (DynamicMailFile e : files) {
			HashMap<String, String> map = new HashMap<String, String>();

			map.put("chair", e.getName());
			map.put("time", e.getDate());
			map.put("room", e.getSubject());
			fillMaps.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps,
				R.layout.list_item_exam, from, to);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		return true;
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
	public void onResultReceived(DynamicMailFile[] results) {
		files = results;
		if (populateList())
			showMainScreen();
	}

	protected void onRepeat() {
		showLoadingScreen();
		task = DynamicEmailUtils.getDynamicEmailFiles(personCode, this,
				getActivity());
	}

	@Override
	public void onItemClick(AdapterView<?> list, View view, int position,
			long id) {
		final DynamicMailFile file = files[position];
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					getActivity()
							.startService(
									DownloaderService.newDownload(
											getActivity(),
											SifeupAPI.getDownloadMailFilesUrl(
													file.getCode(),
													AccountUtils
															.getActiveUserCode(getActivity())),
											file.getName(),
											null,
											0,
											AccountUtils.getAuthToken(
													getActivity(),
													AccountUtils
															.getActiveAccount(getActivity()))));
				} catch (OperationCanceledException e) {
					e.printStackTrace();
				} catch (AuthenticatorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
