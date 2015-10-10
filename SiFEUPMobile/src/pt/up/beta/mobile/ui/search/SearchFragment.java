package pt.up.beta.mobile.ui.search;

import pt.up.mobile.R;
import pt.up.beta.mobile.ui.BaseFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author Angela Igreja
 * 
 */
public class SearchFragment extends BaseFragment {

	private ViewPager viewPager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View root = inflateMainScreen(R.layout.fragment_view_pager);
		viewPager = (ViewPager) root.findViewById(R.id.pager_menu);

		viewPager.setAdapter(new PagerMenuAdapter(getFragmentManager()));
		showMainScreen();
		return getParentContainer();// mandatory
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	}

	/**
	 * Pager Menu Adapter
	 * 
	 * @author Angela Igreja
	 * 
	 */
	class PagerMenuAdapter extends FragmentStatePagerAdapter {

		public PagerMenuAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_search_students);
			case 1:
				return getString(R.string.title_search_employees);
			case 2:
				return getString(R.string.title_search_rooms);
			case 3:
			default:
				return getString(R.string.title_search_subjects);
			}
		}

		public int getCount() {
			return 4;
		}

		@Override
		public Fragment getItem(int position) {
			final Fragment frag;
			switch (position) {
			case 0:
				frag = new StudentsSearchFragment();
				break;
			case 1:
				frag = new EmployeesSearchFragment();
				break;
			case 2:
				frag = new RoomsSearchFragment();
				break;
			case 3:
			default:
				frag = new SubjectsSearchFragment();
				break;
			}
			frag.setArguments(getArguments());
			return frag;
		}

	}

}
