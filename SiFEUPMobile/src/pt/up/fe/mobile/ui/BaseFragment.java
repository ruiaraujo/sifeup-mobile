package pt.up.fe.mobile.ui;

import pt.up.fe.mobile.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

/**
 * @author Rui Araújo
 *
 */
public class BaseFragment extends Fragment {

    protected ViewSwitcher switcher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	switcher = new ViewSwitcher(getActivity());
		switcher.addView(inflater.inflate(R.layout.loading_view, null));
		return switcher;
    }
    
    
    protected void showLoadingScreen(){
    	if ( switcher.getCurrentView() != switcher.getChildAt(0) ) 
			switcher.showNext();
    }
    
    protected void showMainScreen(){
    	if ( switcher.getCurrentView() != switcher.getChildAt(1) ) 
			switcher.showNext();
    }
}
