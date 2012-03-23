package pt.up.beta.mobile.ui;


import pt.up.beta.mobile.sifeup.SessionManager;
import pt.up.beta.mobile.R;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * @author Rui Araújo
 *
 */
public class BaseFragment extends Fragment {

    public final static String URL_INTENT = "pt.up.fe.mobile.ui.webclient.URL";

    private ViewSwitcher switcher;
    private View emptyScreen;
    private LayoutInflater inflater;
    
    protected AsyncTask<?, ?, ?> task;
    
    private ObjectAnimator currentAnim;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		switcher = (ViewSwitcher) inflater.inflate(R.layout.loading_view, container, false);
		emptyScreen  =  inflater.inflate(R.layout.fragment_no_results, switcher, false);
		this.inflater = inflater;
		return switcher;
    }
    
   
    
    private Interpolator accelerator = new AccelerateInterpolator();
    private Interpolator decelerator = new DecelerateInterpolator();
    
    private void flipIt() {
        if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB )
        {
            switcher.showNext();
            return;
        }
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
                currentAnim = invisToVis;
                invisToVis.start();
                switcher.showNext();
            }
        });
        currentAnim = visToInvis;
        visToInvis.start();
    }

    protected void showLoadingScreen(){
    	if ( switcher.getCurrentView() != switcher.getChildAt(0) ) 
            switcher.showNext();
    }
    
    protected void showFastMainScreen(){
        if ( switcher.getCurrentView() != switcher.getChildAt(1) ) 
            switcher.showNext();
    }
    
    protected void showMainScreen(){
    	if ( switcher.getCurrentView() != switcher.getChildAt(1) ) 
    	    flipIt();
    }
    
    protected void showEmptyScreen(final String message ){
        if ( switcher.getCurrentView() == switcher.getChildAt(1) ) 
            return;
        if ( switcher.getChildAt(1) != null ) 
        {
            switcher.removeViewAt(1);
        }
        TextView text = (TextView) emptyScreen.findViewById(R.id.message);
        text.setText(message);
        switcher.addView(emptyScreen,1);
        flipIt();
    }    
    
    protected View getEmptyScreen(final String message ){
        View emptyScreen  =  inflater.inflate(R.layout.fragment_no_results, null);
        TextView text = (TextView) emptyScreen.findViewById(R.id.message);
        text.setText(message);
        return emptyScreen;
    }    
    
    protected ViewGroup getParentContainer(){
    	if ( switcher == null )
    		throw new RuntimeException("onCreateView must be called before from super");
    	return switcher;
    }
    
    @Override
    public void startActivity(Intent intent){
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }
    
    @Override
    public void onActivityCreated (Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);
        // Recovering the Cookie here
        // as every activity will descend from this one.
    	if ( !SessionManager.getInstance(getActivity()).isUserLoaded()  )
    		SessionManager.getInstance(getActivity()).loadSession();
    }

    @Override
    public void onDestroyView (){
        super.onDestroyView();
        if ( task != null )
        {
            task.cancel(true);
        }
    } 
    @Override
    public void onPause (){
        super.onPause();
        if ( currentAnim != null )
        {
            currentAnim.end();
        }
    }
    
    public void goLogin() {
    	if ( getActivity() == null )
    		return;
        Intent i = new Intent(getActivity(), LoginActivity.class);
        i.putExtra(LoginActivity.EXTRA_DIFFERENT_LOGIN, LoginActivity.EXTRA_DIFFERENT_LOGIN_LOGOUT);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
    }

}
