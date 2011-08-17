package pt.up.fe.mobile.ui.studentarea;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.ui.studentarea.LunchMenuFragment.Dish;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Menu Fragment
 * @author Ângela Igreja
 *
 */
public class MenuFragment extends Fragment 
{
    private LayoutInflater mInflater;
	private static Dish [] dishes;
	
	public static MenuFragment getInstance(Dish [] d)
	{
		dishes = d;
		MenuFragment f = new MenuFragment();
		return f;
	}
		
	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState)
	{
	    mInflater = inflater;
	 	View root = inflater.inflate(R.layout.menu ,null);
	    ListView list = (ListView) root.findViewById(R.id.menu_list);
	    list.setAdapter(new MenuAdapter());
	    return root;
	}

	//We save here the current selection state
	@Override
	public void onSaveInstanceState (Bundle outState){
		if ( outState == null )
			return;
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	}
        
    /**
     * This class create a Classes Option Adapter.
     * This adapter creates the views for the main list.
     *
     */
    private class MenuAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return dishes.length;
		}

		@Override
		public Object getItem(int position) {
			return dishes[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int positionList, View convertView, ViewGroup parent) 
		{
			LinearLayout root = (LinearLayout) mInflater.inflate(R.layout.list_item_menu_dish, null);
			((TextView)root.findViewById(R.id.dish_description)).setText((dishes[positionList]).description);
			((TextView)root.findViewById(R.id.dish_description_type)).setText((dishes[positionList]).descriptionType);
			return root;
		}

    }

}
