package de.kauz.starcitizen.informer.activities;

import de.kauz.starcitizen.informer.R;
import de.kauz.starcitizen.informer.utils.InformerConstants;
import de.kauz.starcitizen.informer.utils.MyApp;
import de.kauz.starcitizen.informer.utils.ViewHelper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * Browser container for embedded websites.
 * 
 * @author MadKauz
 * 
 */
public class BrowserContainer extends ActionBarActivity {

	private WebView webView;
	private ProgressBar progressBar;
	private RelativeLayout scrollHelper;
	private String url = "";

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);

		this.webView = (WebView) findViewById(R.id.browserWebView);
		this.scrollHelper = (RelativeLayout) findViewById(R.id.scrollHelper);
		this.progressBar = (ProgressBar) findViewById(R.id.browserLoadingProgress);
		this.progressBar.setMax(100);

		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);

		webView.getSettings().setJavaScriptEnabled(true);

		final Activity activity = this;
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				progressBar.setProgress(progress);
			}

		});
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				MyApp.getInstance().showError(activity,
						"Sorry, I received " + description);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				ViewHelper.fadeIn(scrollHelper);
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				ViewHelper.fadeOut(scrollHelper);
				super.onPageFinished(view, url);
			}
		});

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			String title = extras
					.getString(InformerConstants.EXTRAS_ACTIVITY_BROWSER_NAME);
			this.url = extras
					.getString(InformerConstants.EXTRAS_ACTIVITY_BROWSER_URL);
			setTitle(title);
			getSupportActionBar().setIcon(InformerConstants.MENU_ICONS[5]);

			if (this.url != null) {
				// handle url malformed errors
				if (!this.url.startsWith("http://")
						&& !this.url.startsWith("https://")) {
					this.url = "http://" + this.url;
				}
				if (MyApp.getInstance().isOnline(this)) {
					this.webView.loadUrl(url);
				} else {
					MyApp.getInstance().showError(
							getResources()
									.getString(R.string.errorNoConnection));
				}
			} else {
				MyApp.getInstance().showError(
						getResources().getString(R.string.errorBadLink));
			}
		} else {
			MyApp.getInstance().showError(
					getResources().getString(R.string.errorParseFault));
		}

	}

	@Override
	public void onBackPressed() {
		if (this.webView.copyBackForwardList().getCurrentIndex() > 0) {
			this.webView.goBack();
		} else {
			super.onBackPressed();
		}
	}

}