package pt.up.fe.mobile.ui.webclient;

import pt.up.fe.mobile.R;
import pt.up.fe.mobile.ui.BaseActivity;
import pt.up.fe.mobile.ui.BaseFragment;
import pt.up.fe.mobile.ui.DownloaderFragment;
import pt.up.fe.mobile.ui.LoginActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebviewFragment extends BaseFragment {

    public final static String URL_INTENT = "pt.up.fe.mobile.ui.webclient.URL";
    private WebView mWebView;
    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        url = getArguments().getString(URL_INTENT);
        if (url == null)
            url = savedInstanceState.getString(URL_INTENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.webview, getParentContainer(),
                true);
        mWebView = (WebView) root.findViewById(R.id.webview);
        

        final WebSettings webSettings = mWebView.getSettings();  
        webSettings.setJavaScriptEnabled(true);  
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);

        mWebView.setWebViewClient(new FeupWebViewClient());
        mWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                    String contentDisposition, String mimetype,
                    long contentLength) {
                String filename = getFilename(contentDisposition);
                if ( filename == null )
                    filename = url;
                DownloaderFragment.newInstance("Downloader",url ,filename )
                .show(getFragmentManager(), "Downloader");

            }

        });
        
        //Logging in through javascript
        final  SharedPreferences loginSettings = getActivity().getSharedPreferences(LoginActivity.class.getName(), Context.MODE_PRIVATE);  
        final String user = loginSettings.getString(LoginActivity.PREF_USERNAME, "");
        final String pass = loginSettings.getString(LoginActivity.PREF_PASSWORD, "") ;
        mWebView.loadUrl("javascript: {" +
                "document.getElementById('user').value = '"+user +"';" +
                "var  pass = document.getElementById('pass');" +
                "pass.value = '"+pass+"';" + 
                "var frms = pass.form; " +
                "frms.submit(); };");
        mWebView.loadUrl(url);  
        showFastMainScreen();
        return getParentContainer(); // mandatory
    }
    

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.webclient_menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_browser) {
            final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private String getFilename(String contentDisposition) {
        final String pattern = "filename=\"";
        int filenamePos = contentDisposition.indexOf(pattern);
        if ( filenamePos == -1 )
            return null;
        int filenameFinal = contentDisposition.indexOf("\"", filenamePos + pattern.length());
        return contentDisposition.substring(filenamePos + pattern.length(), filenameFinal);
    }
    
    private class FeupWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }  
        
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon){
            getActivity().showDialog(BaseActivity.DIALOG_FETCHING);
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
            WebviewFragment.this.url = url;
        }
    }

    public void onBackPressed() {
        if (mWebView != null &&  mWebView.canGoBack() )
        {
            mWebView.goBack();
        }
        else
            getActivity().finish();
    }
    

}
