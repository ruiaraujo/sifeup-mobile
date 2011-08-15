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

    private ViewSwitcher switcher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		switcher = (ViewSwitcher) inflater.inflate(R.layout.loading_view, container, false);
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
    
    protected ViewGroup getParentContainer(){
    	if ( switcher == null )
    		throw new RuntimeException("onCreateView must be called before from super");
    	return switcher;
    }
}
