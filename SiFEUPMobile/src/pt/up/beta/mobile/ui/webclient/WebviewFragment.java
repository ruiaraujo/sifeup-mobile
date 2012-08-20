package pt.up.beta.mobile.ui.webclient;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import pt.up.beta.mobile.R;
import pt.up.beta.mobile.sifeup.SessionManager;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.ui.dialogs.DownloaderFragment;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WebviewFragment extends BaseFragment {

    private WebView mWebView;
    private ProgressBar progressWebView;
    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if ( getArguments().get(URL_INTENT) != null )
            url = getArguments().get(URL_INTENT).toString();
        if (url == null && savedInstanceState != null )
            url = savedInstanceState.getString(URL_INTENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.webview, getParentContainer(),
                true);
        mWebView = (WebView) root.findViewById(R.id.webview);
        progressWebView = (ProgressBar) root
                .findViewById(R.id.webview_progress);
        return getParentContainer(); // mandatory
    }
    
    @TargetApi(11)
	public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        final WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.setWebViewClient(new FeupWebViewClient());
        mWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                    String contentDisposition, String mimetype,
                    long contentLength) {
                if ( getActivity() == null )
                	return;
                String filename = getFilename(contentDisposition);
                if (filename == null)
                {
                    filename = url.substring(url.lastIndexOf('/') + 1);
                }
                DownloaderFragment.newInstance("Downloader", url, filename, mimetype, contentLength)
                        .show(getFragmentManager(), "Downloader");

            }

        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if ( getActivity() == null )
                	return;
                //Normalize our progress along the progress bar's scale
               // progress = (Window.PROGRESS_END - Window.PROGRESS_START) / 100 * progress;
                //getSherlockActivity().setSupportProgress(progress);
                progressWebView.setProgress(progress);
            }
        });
        
        //Cleaning previous cookies
        CookieSyncManager.createInstance(getActivity().getApplicationContext());
        CookieManager cookies = CookieManager.getInstance();
        cookies.removeAllCookie();
        CookieSyncManager.getInstance().sync();
        mWebView.loadUrl(url);
        showFastMainScreen();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.webclient_menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_browser) {
            final Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(url));
            startActivity(browserIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getFilename(String contentDisposition) {
        final String pattern = "filename=\"";
        int filenamePos = contentDisposition.indexOf(pattern);
        if (filenamePos == -1)
            return null;
        int filenameFinal = contentDisposition.indexOf("\"", filenamePos
                + pattern.length());
        return contentDisposition.substring(filenamePos + pattern.length(),
                filenameFinal);
    }

    private class FeupWebViewClient extends WebViewClient {

        private boolean loggedIn = false;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if ( url.equals("https://sigarra.up.pt/feup/inqueritos_geral.inqueritos_list") )
            {//trying to stop stupid redirections
                view.loadUrl(WebviewFragment.this.url);
                return true;
            }
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                String description, String failingUrl) {
            if (getActivity() == null)
                return;
            Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        	progressWebView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if ( getActivity() == null )
            	return;
            if (!loggedIn) {
                // This is a ugly hack to login
                // This is needed because some devices are very slow
                // first we fill the form
                // then we reload the page
                // Logging in through javascript
                final SessionManager session = SessionManager.getInstance(getActivity());
                if ( session.loadSession() ) 
                {
                    if ( getActivity() == null )
                    	return;
                	Toast.makeText(getActivity(), R.string.msg_authenticating, Toast.LENGTH_SHORT).show();
	                final String user = session.getLoginCode();
	                final String pass = session.getLoginPassword();
	                if ( user.equals("") || pass.equals("") )
	                	goLogin();
	                mWebView.loadUrl("javascript: {"
	                        + "document.getElementById('user').value = '" + user
	                        + "';" + "var  pass = document.getElementById('pass');"
	                        + "pass.value = '" + pass + "';"
	                        + "var frms = pass.form; " + "frms.submit(); };");
	                loggedIn = true;
                }
            } else
                WebviewFragment.this.url = url;
            progressWebView.setVisibility(View.GONE);
            progressWebView.setProgress(0);
        }
    }

    public void onBackPressed() {
        if (getActivity() == null)
            return;
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else
        {
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
        }
    }

}
