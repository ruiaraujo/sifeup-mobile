package pt.up.beta.mobile.ui.subjects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.Subject;
import pt.up.beta.mobile.datatypes.Subject.Book;
import pt.up.beta.mobile.datatypes.Subject.EvaluationComponent;
import pt.up.beta.mobile.datatypes.Subject.Software;
import pt.up.beta.mobile.datatypes.Subject.Teacher;
import pt.up.beta.mobile.datatypes.SubjectFiles;
import pt.up.beta.mobile.datatypes.SubjectFiles.File;
import pt.up.beta.mobile.datatypes.SubjectFiles.Folder;
import pt.up.beta.mobile.downloader.DownloaderService;
import pt.up.beta.mobile.loaders.SubjectLoader;
import pt.up.beta.mobile.sifeup.AccountUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand.ERROR_TYPE;
import pt.up.beta.mobile.sifeup.SifeupAPI;
import pt.up.beta.mobile.syncadapter.SigarraSyncAdapterUtils;
import pt.up.beta.mobile.ui.BaseLoaderFragment;
import pt.up.beta.mobile.ui.personalarea.ScheduleActivity;
import pt.up.beta.mobile.ui.personalarea.ScheduleFragment;
import pt.up.beta.mobile.ui.profile.ProfileActivity;
import pt.up.beta.mobile.ui.webclient.WebviewActivity;
import pt.up.beta.mobile.ui.webclient.WebviewFragment;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TabPageIndicator;

import external.com.google.android.apps.iosched.util.UIUtils;

public class SubjectDescriptionFragment extends BaseLoaderFragment implements
		OnPageChangeListener, LoaderCallbacks<Subject> {

	public final static String SUBJECT_CODE = "pt.up.fe.mobile.ui.studentarea.SUBJECT_CODE";

	private String code;
	private Subject subject;
	private SubjectFiles subjectFiles;
	/** */
	private PagerSubjectAdapter pagerAdapter;

	/** */
	private LayoutInflater layoutInflater;

	/** */
	private ViewPager viewPager;

	/** */
	private TabPageIndicator indicator;

	private int currentPage = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		code = args.get(SUBJECT_CODE).toString();
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		layoutInflater = inflater;
		View root = inflater.inflate(R.layout.subject_description,
				getParentContainer(), true);
		viewPager = (ViewPager) root.findViewById(R.id.pager_subject);
		viewPager.setAdapter(new PagerSubjectAdapter());
		// Find the indicator from the layout
		indicator = (TabPageIndicator) root
				.findViewById(R.id.indicator_subject);
		// Create our custom adapter to supply pages to the viewpager.
		pagerAdapter = new PagerSubjectAdapter();
		viewPager.setAdapter(pagerAdapter);
		indicator.setViewPager(viewPager);
		// Set the indicator as the pageChangeListener
		indicator.setOnPageChangeListener(this);
		return getParentContainer();
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().getSupportLoaderManager().initLoader(0, null, this);
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.subject_menu_items, menu);
		inflater.inflate(R.menu.refresh_menu_items, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_subject_schedule) {
			Intent i = new Intent(getActivity(), ScheduleActivity.class);
			i.putExtra(ScheduleFragment.SCHEDULE_TYPE,
					ScheduleFragment.SCHEDULE_UC);
			i.putExtra(ScheduleFragment.SCHEDULE_CODE, code);
			i.putExtra(
					Intent.EXTRA_TITLE,
					getString(R.string.title_schedule_arg,
							subject != null ? subject.getNamePt() : code));
			startActivity(i);
			return true;
		}
		if (item.getItemId() == R.id.menu_other_occurrences) {
			startActivity(new Intent(getActivity(),
					OtherOccurrencesActivity.class).putExtra(
					OtherOccurrencesFragment.UCURR_CODE, subject.getUcurrId()));
			return true;
		}
		if (item.getItemId() == R.id.menu_enrolled_students) {
			startActivity(new Intent(getActivity(),
					EnrolledStudentsActivity.class).putExtra(
					EnrolledStudentsFragment.OCORR_CODE, code));
			return true;
		}
		if (item.getItemId() == R.id.menu_go_to_subject_sigarra) {
			Intent i = new Intent(getActivity(), WebviewActivity.class);
			i.putExtra(WebviewFragment.URL_INTENT,
					SifeupAPI.getSubjectSigarraUrl(code));
			startActivity(i);
			return true;
		}
		if (item.getItemId() == R.id.menu_refresh) {
			onRepeat();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onRepeat() {
		super.onRepeat();
		setRefreshActionItemState(true);
		SigarraSyncAdapterUtils.syncSubject(
				AccountUtils.getActiveUserName(getActivity()), code);

	}

	@Override
	public void onError(ERROR_TYPE error) {
		if (getActivity() == null)
			return;
		switch (error) {
		case AUTHENTICATION:
			Toast.makeText(getActivity(), getString(R.string.toast_auth_error),
					Toast.LENGTH_LONG).show();
			goLogin();
			break;
		case NETWORK:
			showRepeatTaskScreen(getString(R.string.toast_server_error));
			break;
		default:
			showEmptyScreen(getString(R.string.general_error));
			break;
		}
	}

	/**
	 * Pager Subject Adapter
	 * 
	 * @author Ângela Igreja
	 * 
	 */
	class PagerSubjectAdapter extends PagerAdapter {
		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.objectives);
			case 1:
				return getString(R.string.content);
			case 2:
				return getString(R.string.teachers);
			case 3:
				return getString(R.string.bibliography);
			case 4:
				return getString(R.string.software);
			case 5:
				return getString(R.string.metodology);
			case 6:
				return getString(R.string.evaluation);
			case 7:
				return getString(R.string.admission_exams);
			case 8:
				return getString(R.string.final_grade);
			case 9:
				return getString(R.string.special_evaluation);
			case 10:
				return getString(R.string.improvement_classification);
			case 11:
				return getString(R.string.comments);
			case 12:
				return getString(R.string.subject_content);
			}

			return "";
		}

		@Override
		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((View) view);

		}

		@Override
		public int getCount() {
			if (subject == null || subjectFiles == null)
				return 0;
			return 13;
		}

		@Override
		public Object instantiateItem(final View collection, int position) {
			View root = null;
			switch (position) {
			case 0: {
				if (!TextUtils.isEmpty(subject.getObjectives())) {
					root = layoutInflater.inflate(R.layout.subject_content,
							viewPager, false);
					final TextView text = (TextView) root
							.findViewById(R.id.content);
					text.setText(subject.getObjectives());
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}
			case 1: {
				if (!TextUtils.isEmpty(subject.getContent())) {
					root = layoutInflater.inflate(R.layout.subject_content,
							viewPager, false);
					final TextView text = (TextView) root
							.findViewById(R.id.content);
					text.setText(subject.getContent());
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}
			case 2: {
				if (subject.getTeachers() != null
						&& subject.getTeachers().length != 0) {
					root = layoutInflater.inflate(R.layout.generic_list,
							viewPager, false);
					ListView list = (ListView) root
							.findViewById(R.id.generic_list);
					String[] from = new String[] { "name" };
					int[] to = new int[] { R.id.teacher_name };
					// prepare the list of all records
					List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

					for (Teacher t : subject.getTeachers()) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(from[0], t.getName());
						fillMaps.add(map);
					}

					SimpleAdapter adapter = new SimpleAdapter(getActivity(),
							fillMaps, R.layout.list_item_subject_teacher, from,
							to);
					list.setAdapter(adapter);
					list.setOnItemClickListener(new OnItemClickListener() {

						public void onItemClick(AdapterView<?> arg0, View arg1,
								int pos, long id) {
							Teacher b = subject.getTeachers()[pos];
							Intent i = new Intent(getActivity(),
									ProfileActivity.class);
							i.putExtra(ProfileActivity.PROFILE_CODE,
									b.getCode());
							i.putExtra(ProfileActivity.PROFILE_TYPE,
									ProfileActivity.PROFILE_EMPLOYEE);
							i.putExtra(Intent.EXTRA_TITLE, b.getName());
							startActivity(i);

						}
					});
				} else
					root = getEmptyScreen(getString(R.string.no_data));

				break;
			}
			case 3: {
				if (subject.getBibliography() != null
						&& subject.getBibliography().length != 0) {
					root = layoutInflater.inflate(R.layout.generic_list,
							viewPager, false);
					ListView listBooks = (ListView) root
							.findViewById(R.id.generic_list);
					final String[] from = new String[] { "typeDescription",
							"authors", "title", "link", "isbn" };

					final int[] to = new int[] { R.id.typeDescription,
							R.id.authors, R.id.title, R.id.link, R.id.isbn };
					// prepare the list of all records
					final List<HashMap<String, String>> fillMapsBooks = new ArrayList<HashMap<String, String>>();

					for (Book b : subject.getBibliography()) {
						final HashMap<String, String> map = new HashMap<String, String>();
						map.put(from[0], b.getTypeDescription());
						map.put(from[1], b.getAuthors());
						map.put(from[2], b.getTitle());
						map.put(from[3], b.getLink());
						map.put(from[4], b.getIsbn());
						fillMapsBooks.add(map);
					}
					final SimpleAdapter adapterBooks = new SimpleAdapter(
							getActivity(), fillMapsBooks,
							R.layout.list_item_subject_book, from, to);
					listBooks.setAdapter(adapterBooks);
					listBooks.setOnItemClickListener(new OnItemClickListener() {

						public void onItemClick(AdapterView<?> arg0, View arg1,
								int pos, long id) {
							Book b = subject.getBibliography()[pos];
							if (b.getLink() == null)
								return;
							Intent i = new Intent(Intent.ACTION_VIEW);
							i.setData(Uri.parse(b.getLink()));
							startActivity(i);

						}
					});
				} else
					root = getEmptyScreen(getString(R.string.no_data));

				break;
			}
			case 4: {
				if (subject.getSoftware() != null
						&& subject.getSoftware().length != 0) {
					root = layoutInflater.inflate(R.layout.generic_list,
							viewPager, false);
					ListView listSoftware = (ListView) root
							.findViewById(R.id.generic_list);
					String[] fromSoftware = new String[] { "name",
							"description" };
					int[] toSoftware = new int[] { R.id.name, R.id.description };
					// prepare the list of all records
					List<HashMap<String, String>> fillMapsSoftware = new ArrayList<HashMap<String, String>>();

					for (Software s : subject.getSoftware()) {
						HashMap<String, String> map = new HashMap<String, String>();
						if (s.getName() != null) {
							map.put(fromSoftware[0], s.getName());
							map.put(fromSoftware[1], s.getDescription());
						} else
							map.put(fromSoftware[0], s.getDescription());
						fillMapsSoftware.add(map);
					}
					SimpleAdapter adapterSoftware = new SimpleAdapter(
							getActivity(), fillMapsSoftware,
							R.layout.list_item_subject_software, fromSoftware,
							toSoftware);
					listSoftware.setAdapter(adapterSoftware);
				} else
					root = getEmptyScreen(getString(R.string.no_data));

				break;
			}
			case 5: {
				if (!TextUtils.isEmpty(subject.getMetodology())) {
					root = layoutInflater.inflate(R.layout.subject_content,
							viewPager, false);
					TextView metodologyText = (TextView) root
							.findViewById(R.id.content);
					metodologyText.setText(subject.getMetodology());
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}
			case 6: {
				if (subject.getEvaluation() != null
						&& subject.getEvaluation().length != 0) {
					root = layoutInflater.inflate(R.layout.generic_list,
							viewPager, false);
					final ListView listEvaluation = (ListView) root
							.findViewById(R.id.generic_list);

					final String[] from = new String[] { "description",
							"typeDesc" };
					final int[] to = new int[] { R.id.description,
							R.id.typeDesc };
					// prepare the list of all records
					final List<HashMap<String, String>> fillMapsEvaluation = new ArrayList<HashMap<String, String>>();

					for (EvaluationComponent e : subject.getEvaluation()) {
						final HashMap<String, String> map = new HashMap<String, String>();
						if (e.getDescription() != null) {
							map.put(from[0], e.getDescription());
							map.put(from[1], e.getTypeDesc());
						} else
							map.put(from[0], e.getTypeDesc());
						fillMapsEvaluation.add(map);

					}
					final SimpleAdapter adapterEvaluation = new SimpleAdapter(
							getActivity(), fillMapsEvaluation,
							R.layout.list_item_subject_evaluation_component,
							from, to);
					listEvaluation.setAdapter(adapterEvaluation);
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}
			case 7: {
				if (!TextUtils.isEmpty(subject.getFrequenceCond())) {
					root = layoutInflater.inflate(R.layout.subject_content,
							viewPager, false);
					TextView admissionExamsText = (TextView) root
							.findViewById(R.id.content);
					admissionExamsText.setText(subject.getFrequenceCond());
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}
			case 8: {
				if (!TextUtils.isEmpty(subject.getEvaluationFormula())) {
					root = layoutInflater.inflate(R.layout.subject_content,
							viewPager, false);
					TextView finalGradeText = (TextView) root
							.findViewById(R.id.content);
					finalGradeText.setText(subject.getEvaluationFormula());
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}
			case 9: {
				if (!TextUtils.isEmpty(subject.getEvaluationProc())) {
					root = layoutInflater.inflate(R.layout.subject_content,
							viewPager, false);
					TextView specialEvaluationText = (TextView) root
							.findViewById(R.id.content);
					specialEvaluationText.setText(subject.getEvaluationProc());
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}
			case 10: {
				if (!TextUtils.isEmpty(subject.getImprovementProc())) {
					root = layoutInflater.inflate(R.layout.subject_content,
							viewPager, false);
					TextView improvementClassificationText = (TextView) root
							.findViewById(R.id.content);
					improvementClassificationText.setText(subject
							.getImprovementProc());
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}
			case 11: {
				if (!TextUtils.isEmpty(subject.getObservations())) {
					root = layoutInflater.inflate(R.layout.subject_content,
							viewPager, false);
					TextView commentsText = (TextView) root
							.findViewById(R.id.content);
					commentsText.setText(subject.getObservations());
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}
			case 12: {
				if (subjectFiles != null
						&& (subjectFiles.getCurrentFolder().getFolders().size() != 0 || subjectFiles
								.getCurrentFolder().getFiles().length != 0)) {
					root = layoutInflater.inflate(R.layout.generic_list,
							viewPager, false);
					root.setTag(getString(R.string.subject_content));
					final ListView list = (ListView) root
							.findViewById(R.id.generic_list);
					list.setAdapter(getSubjectContentAdapter());
					list.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> list, View item,
								int position, long id) {
							if (position >= subjectFiles.getCurrentFolder()
									.getFolders().size()) {
								// launch download;
								File toDownload = subjectFiles
										.getCurrentFolder().getFiles()[position
										- subjectFiles.getCurrentFolder()
												.getFolders().size()];
								if (toDownload.getUrl() == null
										|| toDownload.getUrl().trim().length() == 0) {
									try {
										getActivity()
												.startService(
														DownloaderService
																.newDownload(
																		getActivity(),
																		SifeupAPI
																				.getSubjectFileContents(Integer
																						.toString(toDownload
																								.getCode())),
																		toDownload
																				.getFilename(),
																		null,
																		toDownload
																				.getSize(),
																		AccountUtils
																				.getAuthToken(
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
								} else {
									Intent i = new Intent(Intent.ACTION_VIEW);
									i.setData(Uri.parse(toDownload.getUrl()));
									startActivity(i);
								}
								return;
							}
							subjectFiles.setCurrentFolder(subjectFiles
									.getCurrentFolder().getFolders()
									.get(position));
							View contents = viewPager
									.findViewWithTag(getString(R.string.subject_content));
							((ListView) contents
									.findViewById(R.id.generic_list))
									.setAdapter(getSubjectContentAdapter());

						}
					});
				} else
					root = getEmptyScreen(getString(R.string.no_data));
				break;
			}

			}
			if (root == null)
				root = getEmptyScreen(getString(R.string.no_data));
			((ViewPager) collection).addView(root, 0);
			return root;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((View) object);
		}

		@Override
		public void finishUpdate(View arg0) {

		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

	}

	private ListAdapter getSubjectContentAdapter() {
		String[] from = new String[] { "name" };
		int[] to = new int[] { R.id.folder_name };

		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

		for (Folder f : subjectFiles.getCurrentFolder().getFolders()) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(from[0], f.getName());
			fillMaps.add(map);
		}
		for (File f : subjectFiles.getCurrentFolder().getFiles()) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(from[0], f.getName());
			fillMaps.add(map);
		}
		return new SimpleAdapter(getActivity(), fillMaps,
				R.layout.list_item_folder, from, to);
	}

	public void onBackPressed() {
		if (currentPage == 12
				&& subjectFiles.getCurrentFolder().getParent() != null) {
			subjectFiles.setCurrentFolder(subjectFiles.getCurrentFolder()
					.getParent());
			View contents = viewPager
					.findViewWithTag(getString(R.string.subject_content));
			((ListView) contents.findViewById(R.id.generic_list))
					.setAdapter(getSubjectContentAdapter());
		} else {
			if (getActivity() != null)
				getActivity().finish();
		}
	}

	// Unused
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	// Unused
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int page) {
		currentPage = page;
	}

	@Override
	public Loader<Subject> onCreateLoader(int loaderId, Bundle args) {
		return new SubjectLoader(getActivity(),
				SigarraContract.Subjects.CONTENT_ITEM_URI,
				SigarraContract.Subjects.SUBJECT_COLUMNS,
				SigarraContract.Subjects.SUBJECT_SELECTION,
				SigarraContract.Subjects.getSubjectsSelectionArgs(code), null);
	}

	@Override
	public void onLoadFinished(Loader<Subject> loader, Subject cursor) {
		if (cursor != null) {
			subject = cursor;
			String title = subject.getNamePt();
			if (!UIUtils.isLocalePortuguese()
					&& !TextUtils.isEmpty(subject.getNameEn()))
				title = subject.getNameEn();
			getSherlockActivity().getSupportActionBar().setTitle(title);
			subjectFiles = subject.getFiles();
			// Start at a custom position
			indicator.setCurrentItem(0);
			indicator.notifyDataSetChanged();
			setRefreshActionItemState(false);
			showMainScreen();
		}
	}

	@Override
	public void onLoaderReset(Loader<Subject> loader) {
	}
}
