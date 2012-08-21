package pt.up.beta.mobile.ui;


import com.actionbarsherlock.app.SherlockFragment;

import pt.up.beta.mobile.sifeup.SessionManager;
import pt.up.beta.mobile.ui.utils.ImageDownloader;
import pt.up.beta.mobile.R;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * @author Rui Araújo
 *
 */
public class BaseFragment extends SherlockFragment {

    public final static String URL_INTENT = "pt.up.fe.mobile.ui.webclient.URL";
    protected final static String DIALOG = "dialog";
    private static ImageDownloader imageDownloader;
    
    private ViewSwitcher switcher;
    private View emptyScreen;
    private View placeHolder; // to be used when the child view is
    //replaced by "emptyScreen" view.
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
    
    @TargetApi(11)
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
    	if ( placeHolder != null )
    	{
            switcher.removeViewAt(1);
            switcher.addView(placeHolder, 1);
            placeHolder = null;
    	}
        if ( switcher.getCurrentView() != switcher.getChildAt(1) ) 
            switcher.showNext();
    }
    
    protected void showMainScreen(){
    	if ( placeHolder != null )
    	{
            switcher.removeViewAt(1);
            switcher.addView(placeHolder, 1);
            placeHolder = null;
    	}
    	if ( switcher.getCurrentView() != switcher.getChildAt(1) ) 
    	    flipIt();
    }
    
    protected void showEmptyScreen(final String message ){
        if ( switcher.getCurrentView() == switcher.getChildAt(1) ) 
            return;
        placeHolder = switcher.getChildAt(1);
        if ( placeHolder != null ) 
        {
            if ( switcher.getChildAt(1) != emptyScreen )
            {
    	        placeHolder = switcher.getChildAt(1);
    	        if ( placeHolder != null ) 
    	        {
    	            switcher.removeViewAt(1);
    	            switcher.addView(emptyScreen,1);

    	        }
            }
        }
        TextView text = (TextView) emptyScreen.findViewById(R.id.message);
        emptyScreen.findViewById(R.id.action).setVisibility(View.GONE);
        text.setText(message);
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
    @TargetApi(11)
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
        Intent i = new Intent(getActivity(), LauncherActivity.class);
      //  i.putExtra(LauncherActivity.EXTRA_DIFFERENT_LOGIN, LauncherActivity.EXTRA_DIFFERENT_LOGIN_LOGOUT);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
    }

	public static ImageDownloader getImagedownloader() {
		if ( imageDownloader == null )
			imageDownloader = new ImageDownloader();
		return imageDownloader;
	}
	
    protected void showRepeatTaskScreen(final String message ){
        if ( switcher.getCurrentView() == switcher.getChildAt(1) ) 
        {
            switcher.showNext();
        }
        if ( switcher.getChildAt(1) != emptyScreen )
        {
	        placeHolder = switcher.getChildAt(1);
	        if ( placeHolder != null ) 
	        {
	            switcher.removeViewAt(1);
	            switcher.addView(emptyScreen,1);
	        }
        }
        TextView text = (TextView) emptyScreen.findViewById(R.id.message);
        Button repeat = (Button) emptyScreen.findViewById(R.id.action);
        repeat.setVisibility(View.VISIBLE);
        repeat.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				BaseFragment.this.onRepeat();
			}
		});
        text.setText(message);
        switcher.showNext();
    }    
    
    protected void onRepeat(){
    	
    }
    
    protected void removeDialog(String dialog) {    // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(dialog);
        if (prev != null) {
            ft.remove(prev).commitAllowingStateLoss();
        }
    }

}
