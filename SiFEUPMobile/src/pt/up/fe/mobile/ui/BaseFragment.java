package pt.up.fe.mobile.ui;

import pt.up.fe.mobile.R;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
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
    
    private Interpolator accelerator = new AccelerateInterpolator();
    private Interpolator decelerator = new DecelerateInterpolator();
    private void flipit() {
        final View visibleList;
        final View invisibleList;
        if (switcher.getCurrentView() == switcher.getChildAt(0)) {
            visibleList = switcher.getChildAt(0);
            invisibleList = switcher.getChildAt(1);
        } else {
            invisibleList = switcher.getChildAt(0);
            visibleList = switcher.getChildAt(1);
        }
        ObjectAnimator visToInvis = ObjectAnimator.ofFloat(visibleList, "rotationY", 0f, 90f);
        visToInvis.setDuration(500);
        visToInvis.setInterpolator(accelerator);
        final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(invisibleList, "rotationY",
                -90f, 0f);
        invisToVis.setDuration(500);
        invisToVis.setInterpolator(decelerator);
        visToInvis.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                invisToVis.start();
                switcher.showNext();
            }
        });
        visToInvis.start();
    }

    protected void showLoadingScreen(){
    	if ( switcher.getCurrentView() != switcher.getChildAt(0) ) 
    	    flipit();
    }
    
    protected void showMainScreen(){
    	if ( switcher.getCurrentView() != switcher.getChildAt(1) ) 
    	    flipit();
    }
    
    protected ViewGroup getParentContainer(){
    	if ( switcher == null )
    		throw new RuntimeException("onCreateView must be called before from super");
    	return switcher;
    }
}
